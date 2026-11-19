package com.example.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.data.model.PlayerUiState
import com.example.data.model.RepeatModeState
import com.example.data.model.Track
import com.example.data.remote.NewPipeRepository
import com.example.data.repository.MusicLibraryRepository
import com.example.service.SargamPlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.random.Random

class PlaybackController(
    context: Context,
    private val newPipeRepository: NewPipeRepository,
    private val libraryRepository: MusicLibraryRepository
) {
    companion object {
        private const val TAG = "PlaybackController"
    }

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var resolveJob: Job? = null
    private var positionTickerJob: Job? = null

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startPositionTicker()
            } else {
                positionTickerJob?.cancel()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val buffering = playbackState == Player.STATE_BUFFERING
            _uiState.update { state ->
                val controllerDuration = mediaController?.duration?.takeIf { it > 0L }
                state.copy(
                    isBuffering = buffering,
                    durationMs = controllerDuration ?: state.durationMs
                )
            }
            if (playbackState == Player.STATE_ENDED) {
                handleTrackCompleted()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e(TAG, "ExoPlayer error: ${error.errorCodeName}", error)
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    isBuffering = false,
                    isResolvingStream = false,
                    errorMessage = "Playback failed (${error.errorCodeName}). Tap next or retry."
                )
            }
        }
    }

    fun connect() {
        if (controllerFuture != null) return
        val sessionToken = SessionToken(
            appContext,
            ComponentName(appContext, SargamPlaybackService::class.java)
        )
        val future = MediaController.Builder(appContext, sessionToken).buildAsync()
        controllerFuture = future
        future.addListener(
            {
                runCatching { future.get() }
                    .onSuccess { controller ->
                        mediaController = controller
                        controller.addListener(playerListener)
                        syncControllerState()
                    }
                    .onFailure { err ->
                        Log.e(TAG, "Failed connecting MediaController", err)
                    }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun playTrack(track: Track, queue: List<Track> = listOf(track)) {
        val effectiveQueue = if (queue.any { it.videoId == track.videoId }) {
            queue
        } else {
            listOf(track) + queue
        }
        val startIndex = effectiveQueue.indexOfFirst { it.videoId == track.videoId }.coerceAtLeast(0)
        _uiState.update {
            it.copy(
                queue = effectiveQueue,
                currentIndex = startIndex,
                currentTrack = effectiveQueue[startIndex],
                errorMessage = null
            )
        }
        prepareAndPlayIndex(startIndex)
    }

    fun playQueue(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        val safeIndex = startIndex.coerceIn(0, tracks.lastIndex)
        _uiState.update {
            it.copy(
                queue = tracks,
                currentIndex = safeIndex,
                currentTrack = tracks[safeIndex],
                errorMessage = null
            )
        }
        prepareAndPlayIndex(safeIndex)
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            if (controller.mediaItemCount == 0 && _uiState.value.currentTrack != null) {
                prepareAndPlayIndex(_uiState.value.currentIndex.coerceAtLeast(0))
            } else {
                controller.play()
            }
        }
    }

    fun skipToNext() {
        val state = _uiState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        val nextIndex = if (state.shuffleEnabled && queue.size > 1) {
            var candidate = Random.nextInt(queue.size)
            while (candidate == state.currentIndex) {
                candidate = Random.nextInt(queue.size)
            }
            candidate
        } else if (state.currentIndex + 1 < queue.size) {
            state.currentIndex + 1
        } else {
            0
        }
        prepareAndPlayIndex(nextIndex)
    }

    fun skipToPrevious() {
        val controller = mediaController
        if (controller != null && controller.currentPosition > 3500L) {
            controller.seekTo(0L)
            _uiState.update { it.copy(positionMs = 0L) }
            return
        }
        val state = _uiState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        val prevIndex = if (state.currentIndex - 1 >= 0) {
            state.currentIndex - 1
        } else {
            queue.lastIndex
        }
        prepareAndPlayIndex(prevIndex)
    }

    fun playQueueIndex(index: Int) {
        val queue = _uiState.value.queue
        if (index in queue.indices) {
            prepareAndPlayIndex(index)
        }
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceAtLeast(0L)
        mediaController?.seekTo(clamped)
        _uiState.update { it.copy(positionMs = clamped) }
    }

    fun toggleShuffle() {
        _uiState.update { it.copy(shuffleEnabled = !it.shuffleEnabled) }
    }

    fun cycleRepeatMode() {
        _uiState.update { state ->
            val nextMode = when (state.repeatMode) {
                RepeatModeState.OFF -> RepeatModeState.ALL
                RepeatModeState.ALL -> RepeatModeState.ONE
                RepeatModeState.ONE -> RepeatModeState.OFF
            }
            state.copy(repeatMode = nextMode)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun handleTrackCompleted() {
        val state = _uiState.value
        val queue = state.queue
        if (queue.isEmpty()) return

        when (state.repeatMode) {
            RepeatModeState.ONE -> {
                mediaController?.seekTo(0L)
                mediaController?.play()
            }
            RepeatModeState.ALL -> {
                skipToNext()
            }
            RepeatModeState.OFF -> {
                if (state.shuffleEnabled) {
                    skipToNext()
                } else if (state.currentIndex + 1 < queue.size) {
                    prepareAndPlayIndex(state.currentIndex + 1)
                } else {
                    _uiState.update { it.copy(isPlaying = false, positionMs = it.durationMs) }
                }
            }
        }
    }

    private fun prepareAndPlayIndex(index: Int) {
        val queue = _uiState.value.queue
        if (index !in queue.indices) return
        val track = queue[index]

        resolveJob?.cancel()
        resolveJob = scope.launch {
            _uiState.update {
                it.copy(
                    currentIndex = index,
                    currentTrack = track,
                    isResolvingStream = true,
                    isBuffering = true,
                    positionMs = 0L,
                    durationMs = track.durationSeconds * 1000L,
                    errorMessage = null
                )
            }

            try {
                // Step 1: Always check Room Database & internal storage first for instant offline playback
                val localFile = libraryRepository.getLocalFileIfDownloaded(track.videoId)
                val (mediaUri, isOffline) = if (localFile != null) {
                    Uri.fromFile(localFile) to true
                } else {
                    // Step 2: Resolve fresh audio-only YouTube stream URL via NewPipeExtractor
                    val resolved = withContext(Dispatchers.IO) {
                        newPipeRepository.resolveAudioStream(track.videoId)
                    }
                    Uri.parse(resolved.url) to false
                }

                val controller = awaitMediaController()

                val metadataBuilder = MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .setAlbumTitle(track.albumName.ifBlank { "Sargam Hi-Fi" })

                if (track.thumbnailUrl.isNotBlank()) {
                    metadataBuilder.setArtworkUri(Uri.parse(track.thumbnailUrl))
                }

                val mediaItem = MediaItem.Builder()
                    .setMediaId(track.videoId)
                    .setUri(mediaUri)
                    .setMediaMetadata(metadataBuilder.build())
                    .build()

                controller.setMediaItem(mediaItem)
                controller.prepare()
                controller.play()

                _uiState.update {
                    it.copy(
                        isResolvingStream = false,
                        isPlayingOfflineFile = isOffline,
                        errorMessage = null
                    )
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.e(TAG, "Error preparing track ${track.title}", e)
                _uiState.update {
                    it.copy(
                        isResolvingStream = false,
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "Unable to stream track. Check connection or play offline downloads."
                    )
                }
            }
        }
    }

    private suspend fun awaitMediaController(): MediaController {
        mediaController?.let { return it }
        if (controllerFuture == null) connect()
        val future = controllerFuture ?: throw IllegalStateException("MediaController unavailable")
        return suspendCancellableCoroutine { cont ->
            future.addListener(
                {
                    if (cont.isActive) {
                        runCatching { future.get() }
                            .onSuccess { controller ->
                                mediaController = controller
                                cont.resume(controller)
                            }
                            .onFailure { err ->
                                cont.cancel(err)
                            }
                    }
                },
                MoreExecutors.directExecutor()
            )
        }
    }

    private fun syncControllerState() {
        val controller = mediaController ?: return
        _uiState.update {
            it.copy(
                isPlaying = controller.isPlaying,
                positionMs = controller.currentPosition.coerceAtLeast(0L),
                durationMs = controller.duration.takeIf { d -> d > 0L } ?: it.durationMs
            )
        }
        if (controller.isPlaying) {
            startPositionTicker()
        }
    }

    private fun startPositionTicker() {
        positionTickerJob?.cancel()
        positionTickerJob = scope.launch {
            while (isActive) {
                val controller = mediaController
                if (controller != null) {
                    val pos = controller.currentPosition.coerceAtLeast(0L)
                    val dur = controller.duration.takeIf { it > 0L }
                    _uiState.update { state ->
                        state.copy(
                            positionMs = pos,
                            durationMs = dur ?: state.durationMs
                        )
                    }
                }
                delay(500L)
            }
        }
    }
}
