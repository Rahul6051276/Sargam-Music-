package com.example.data.download

import android.content.Context
import android.util.Log
import com.example.data.local.DownloadedTrackEntity
import com.example.data.model.BatchDownloadState
import com.example.data.model.DownloadStatus
import com.example.data.model.Track
import com.example.data.model.TrackDownloadProgress
import com.example.data.remote.NewPipeRepository
import com.example.data.remote.OkHttpDownloader
import com.example.data.repository.MusicLibraryRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class BatchDownloadManager(
    context: Context,
    private val okHttpClient: OkHttpClient,
    private val newPipeRepository: NewPipeRepository,
    private val libraryRepository: MusicLibraryRepository
) {
    companion object {
        private const val TAG = "BatchDownloadManager"
        private const val MUSIC_DIR_NAME = "music"
        private const val BUFFER_SIZE = 8 * 1024
    }

    private val appContext = context.applicationContext
    private val musicDir: File = File(appContext.filesDir, MUSIC_DIR_NAME).apply {
        if (!exists()) mkdirs()
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _trackProgress = MutableStateFlow<Map<String, TrackDownloadProgress>>(emptyMap())
    val trackProgress: StateFlow<Map<String, TrackDownloadProgress>> = _trackProgress.asStateFlow()

    private val _batchState = MutableStateFlow<BatchDownloadState?>(null)
    val batchState: StateFlow<BatchDownloadState?> = _batchState.asStateFlow()

    private var batchJob: Job? = null

    fun downloadSingleTrack(track: Track) {
        val currentStatus = _trackProgress.value[track.videoId]?.status
        if (currentStatus == DownloadStatus.DOWNLOADING || currentStatus == DownloadStatus.QUEUED) {
            return
        }
        scope.launch {
            updateTrackProgress(
                track.videoId,
                TrackDownloadProgress(
                    videoId = track.videoId,
                    title = track.title,
                    progress = 0f,
                    status = DownloadStatus.QUEUED
                )
            )
            downloadTrackInternal(track) { _, _ -> }
        }
    }

    /**
     * Sequentially downloads every track in a Movie Album, Mood Collection, or Playlist
     * into internal app storage (context.filesDir/music/) with live progress reporting.
     */
    fun downloadCollection(collectionTitle: String, tracks: List<Track>) {
        if (tracks.isEmpty()) return

        batchJob?.cancel()
        batchJob = scope.launch {
            val total = tracks.size
            var completed = 0
            var failed = 0

            tracks.forEach { track ->
                updateTrackProgress(
                    track.videoId,
                    TrackDownloadProgress(
                        videoId = track.videoId,
                        title = track.title,
                        progress = 0f,
                        status = DownloadStatus.QUEUED
                    )
                )
            }

            _batchState.value = BatchDownloadState(
                collectionTitle = collectionTitle,
                totalCount = total,
                completedCount = 0,
                failedCount = 0,
                currentTrackTitle = tracks.first().title,
                currentTrackProgress = 0f,
                isActive = true
            )

            for (track in tracks) {
                if (!isActive) break

                _batchState.update { state ->
                    state?.copy(
                        currentTrackTitle = track.title,
                        currentTrackProgress = 0f
                    )
                }

                val enrichedTrack = if (track.albumName.isBlank()) {
                    track.copy(albumName = collectionTitle)
                } else {
                    track
                }

                val success = downloadTrackInternal(enrichedTrack) { progress, _ ->
                    _batchState.update { state ->
                        state?.copy(currentTrackProgress = progress)
                    }
                }

                if (success) {
                    completed++
                } else {
                    failed++
                }

                _batchState.update { state ->
                    state?.copy(
                        completedCount = completed,
                        failedCount = failed,
                        currentTrackProgress = 0f
                    )
                }
            }

            _batchState.update { state ->
                state?.copy(isActive = false, currentTrackProgress = 1f)
            }
        }
    }

    fun cancelBatchDownload() {
        batchJob?.cancel()
        batchJob = null
        _batchState.value = null
        _trackProgress.update { map ->
            map.filterValues { it.status == DownloadStatus.COMPLETED }
        }
    }

    fun dismissBatchBanner() {
        if (_batchState.value?.isActive == false) {
            _batchState.value = null
        }
    }

    private suspend fun downloadTrackInternal(
        track: Track,
        onProgress: (Float, Long) -> Unit
    ): Boolean {
        return try {
            // Skip network if already downloaded cleanly in internal storage
            val existingFile = libraryRepository.getLocalFileIfDownloaded(track.videoId)
            if (existingFile != null) {
                updateTrackProgress(
                    track.videoId,
                    TrackDownloadProgress(
                        videoId = track.videoId,
                        title = track.title,
                        progress = 1f,
                        status = DownloadStatus.COMPLETED
                    )
                )
                onProgress(1f, existingFile.length())
                return true
            }

            updateTrackProgress(
                track.videoId,
                TrackDownloadProgress(
                    videoId = track.videoId,
                    title = track.title,
                    progress = 0.04f,
                    status = DownloadStatus.DOWNLOADING
                )
            )

            val resolved = newPipeRepository.resolveAudioStream(track.videoId)
            if (!musicDir.exists()) musicDir.mkdirs()

            val targetFile = File(musicDir, "${track.videoId}.${resolved.extension}")
            val tempFile = File(musicDir, "${track.videoId}.${resolved.extension}.tmp")

            val request = Request.Builder()
                .url(resolved.url)
                .header("User-Agent", OkHttpDownloader.USER_AGENT)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("HTTP ${response.code} while downloading audio")
                }
                val body = response.body ?: throw IllegalStateException("Empty audio stream body")
                val totalBytes = body.contentLength()

                body.byteStream().use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var downloadedBytes = 0L
                        var lastUiUpdateMs = 0L
                        var bytesRead = input.read(buffer)

                        while (bytesRead != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead

                            val now = System.currentTimeMillis()
                            if (now - lastUiUpdateMs >= 150L) {
                                lastUiUpdateMs = now
                                val fraction = if (totalBytes > 0L) {
                                    (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0.05f, 0.99f)
                                } else {
                                    0.5f
                                }
                                updateTrackProgress(
                                    track.videoId,
                                    TrackDownloadProgress(
                                        videoId = track.videoId,
                                        title = track.title,
                                        progress = fraction,
                                        status = DownloadStatus.DOWNLOADING
                                    )
                                )
                                onProgress(fraction, downloadedBytes)
                            }
                            bytesRead = input.read(buffer)
                        }
                        output.flush()
                    }
                }
            }

            if (targetFile.exists()) targetFile.delete()
            if (!tempFile.renameTo(targetFile)) {
                tempFile.copyTo(targetFile, overwrite = true)
                tempFile.delete()
            }

            libraryRepository.saveDownloadedTrack(
                DownloadedTrackEntity(
                    videoId = track.videoId,
                    title = track.title,
                    artist = track.artist,
                    albumName = track.albumName,
                    durationSeconds = track.durationSeconds,
                    thumbnailUrl = track.thumbnailUrl,
                    localFilePath = targetFile.absolutePath,
                    fileSizeBytes = targetFile.length()
                )
            )

            updateTrackProgress(
                track.videoId,
                TrackDownloadProgress(
                    videoId = track.videoId,
                    title = track.title,
                    progress = 1f,
                    status = DownloadStatus.COMPLETED
                )
            )
            onProgress(1f, targetFile.length())
            true
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(TAG, "Failed downloading ${track.title}: ${e.message}", e)
            updateTrackProgress(
                track.videoId,
                TrackDownloadProgress(
                    videoId = track.videoId,
                    title = track.title,
                    progress = 0f,
                    status = DownloadStatus.FAILED,
                    errorMessage = e.message ?: "Download failed"
                )
            )
            false
        }
    }

    private fun updateTrackProgress(videoId: String, progress: TrackDownloadProgress) {
        _trackProgress.update { current ->
            current + (videoId to progress)
        }
    }
}
