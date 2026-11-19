package com.example.data.remote

import android.net.Uri
import android.util.Log
import com.example.data.model.MoodCollection
import com.example.data.model.MovieAlbum
import com.example.data.model.ResolvedAudioStream
import com.example.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.MediaFormat
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.DeliveryMethod
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.util.concurrent.ConcurrentHashMap

class NewPipeRepository {

    companion object {
        private const val TAG = "NewPipeRepository"
        private const val MIN_SONG_SECONDS = 65L
        private const val MAX_INDIVIDUAL_SONG_SECONDS = 660L // Exclude >11 min jukeboxes

        private val EXCLUDED_KEYWORDS = listOf(
            "jukebox",
            "full album",
            "full movie",
            "audio jukebox",
            "video jukebox",
            "non stop",
            "nonstop",
            "1 hour",
            "2 hour",
            "mashup",
            "trailer",
            "teaser",
            "making of",
            "behind the scenes",
            "interview",
            "reaction",
            "review",
            "explained",
            "status video",
            "whatsapp status",
            "ringtone"
        )
    }

    private val moodCache = ConcurrentHashMap<String, List<Track>>()
    private val albumCache = ConcurrentHashMap<String, List<Track>>()

    suspend fun searchSongs(
        query: String,
        albumTag: String = "",
        limit: Int = 25
    ): List<Track> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return@withContext emptyList()

        val collected = mutableListOf<Track>()

        // Try YouTube Music Songs filter first for cleaner studio audio tracks
        val musicItems = runCatching {
            fetchSearchStreamItems(
                query = cleanQuery,
                contentFilters = listOf(YoutubeSearchQueryHandlerFactory.MUSIC_SONGS)
            )
        }.onFailure { err ->
            Log.w(TAG, "MUSIC_SONGS search failed for '$cleanQuery': ${err.message}")
        }.getOrDefault(emptyList())

        collected.addAll(
            musicItems
                .filter { isLikelyIndividualSong(it) }
                .mapNotNull { it.toTrack(albumTag) }
        )

        // Supplement with standard YouTube video search if music filter yields fewer results
        if (collected.size < 10) {
            val videoItems = runCatching {
                fetchSearchStreamItems(
                    query = "$cleanQuery song audio",
                    contentFilters = listOf(YoutubeSearchQueryHandlerFactory.VIDEOS)
                )
            }.onFailure { err ->
                Log.w(TAG, "VIDEOS search failed for '$cleanQuery': ${err.message}")
            }.getOrDefault(emptyList())

            collected.addAll(
                videoItems
                    .filter { isLikelyIndividualSong(it) }
                    .mapNotNull { it.toTrack(albumTag) }
            )
        }

        deduplicateTracks(collected).take(limit)
    }

    suspend fun searchMovieAlbums(query: String): List<MovieAlbum> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return@withContext emptyList()

        val albums = mutableListOf<MovieAlbum>()

        val formattedTitle = cleanQuery.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { ch -> ch.uppercase() }
            }

        albums.add(
            MovieAlbum(
                id = "search_movie_${cleanQuery.lowercase().replace(Regex("[^a-z0-9]"), "_")}",
                title = formattedTitle,
                subtitle = "Full Movie Album · Individual Songs",
                searchQuery = "$cleanQuery movie songs official",
                year = "Soundtrack",
                gradientStartHex = 0xFF0A4A5A,
                gradientEndHex = 0xFF131B2E
            )
        )

        val playlistItems = mutableListOf<PlaylistInfoItem>()
        runCatching {
            val extractor = ServiceList.YouTube.getSearchExtractor(
                "$cleanQuery album songs",
                listOf(YoutubeSearchQueryHandlerFactory.MUSIC_ALBUMS),
                ""
            )
            extractor.fetchPage()
            playlistItems.addAll(extractor.initialPage.items.filterIsInstance<PlaylistInfoItem>())
        }

        if (playlistItems.size < 5) {
            runCatching {
                val extractor = ServiceList.YouTube.getSearchExtractor(
                    "$cleanQuery full movie songs playlist",
                    listOf(YoutubeSearchQueryHandlerFactory.PLAYLISTS),
                    ""
                )
                extractor.fetchPage()
                playlistItems.addAll(extractor.initialPage.items.filterIsInstance<PlaylistInfoItem>())
            }
        }

        val seenUrls = mutableSetOf<String>()
        playlistItems.forEachIndexed { idx, item ->
            val url = item.url ?: return@forEachIndexed
            if (!seenUrls.add(url)) return@forEachIndexed
            val thumb = pickBestThumbnail(item.thumbnails)
            val streamCountText = if (item.streamCount > 0) "${item.streamCount} tracks" else "Album Playlist"
            albums.add(
                MovieAlbum(
                    id = "yt_album_${url.hashCode()}_$idx",
                    title = cleanDisplayTitle(item.name.orEmpty().ifBlank { formattedTitle }),
                    subtitle = item.uploaderName?.takeIf { it.isNotBlank() }
                        ?.let { "$it · $streamCountText" } ?: streamCountText,
                    searchQuery = "${item.name.orEmpty()} $cleanQuery songs",
                    year = streamCountText,
                    playlistUrl = url,
                    artworkUrl = thumb,
                    gradientStartHex = if (idx % 2 == 0) 0xFF0F4C5C else 0xFF1E3A5F,
                    gradientEndHex = 0xFF131B2E
                )
            )
        }

        albums.take(12)
    }

    suspend fun fetchMovieAlbumTracks(
        album: MovieAlbum,
        forceRefresh: Boolean = false
    ): List<Track> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            albumCache[album.id]?.let { cached ->
                if (cached.isNotEmpty()) return@withContext cached
            }
        }

        val tracks = mutableListOf<Track>()

        if (!album.playlistUrl.isNullOrBlank()) {
            runCatching {
                val playlistExtractor = ServiceList.YouTube.getPlaylistExtractor(album.playlistUrl)
                playlistExtractor.fetchPage()
                val items = playlistExtractor.initialPage.items.filterIsInstance<StreamInfoItem>()
                tracks.addAll(
                    items
                        .filter { isLikelyIndividualSong(it) }
                        .mapNotNull { it.toTrack(album.title) }
                )
            }.onFailure { err ->
                Log.w(TAG, "Playlist extraction fallback for ${album.title}: ${err.message}")
            }
        }

        if (tracks.size < 4) {
            runCatching {
                val searchExtractor = ServiceList.YouTube.getSearchExtractor(
                    "${album.title} original motion picture soundtrack",
                    listOf(YoutubeSearchQueryHandlerFactory.MUSIC_ALBUMS),
                    ""
                )
                searchExtractor.fetchPage()
                val bestPlaylist = searchExtractor.initialPage.items
                    .filterIsInstance<PlaylistInfoItem>()
                    .firstOrNull { pl ->
                        pl.name?.contains(album.title, ignoreCase = true) == true
                    }

                if (bestPlaylist?.url != null) {
                    val plExtractor = ServiceList.YouTube.getPlaylistExtractor(bestPlaylist.url)
                    plExtractor.fetchPage()
                    val plItems = plExtractor.initialPage.items.filterIsInstance<StreamInfoItem>()
                    tracks.addAll(
                        plItems
                            .filter { isLikelyIndividualSong(it) }
                            .mapNotNull { it.toTrack(album.title) }
                    )
                }
            }.onFailure { err ->
                Log.w(TAG, "Album playlist lookup fallback for ${album.title}: ${err.message}")
            }
        }

        if (tracks.size < 6) {
            val searched = searchSongs(
                query = album.searchQuery,
                albumTag = album.title,
                limit = 18
            )
            tracks.addAll(searched)
        }

        val finalTracks = deduplicateTracks(tracks).take(20)
        if (finalTracks.isNotEmpty()) {
            albumCache[album.id] = finalTracks
        }
        finalTracks
    }

    suspend fun fetchMoodTracks(
        mood: MoodCollection,
        forceRefresh: Boolean = false
    ): List<Track> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            moodCache[mood.id]?.let { cached ->
                if (cached.isNotEmpty()) return@withContext cached
            }
        }

        val tracks = searchSongs(
            query = mood.searchQuery,
            albumTag = mood.title,
            limit = 25
        )
        if (tracks.isNotEmpty()) {
            moodCache[mood.id] = tracks
        }
        tracks
    }

    suspend fun fetchAlbumCoverPreview(album: MovieAlbum): String? = withContext(Dispatchers.IO) {
        if (!album.artworkUrl.isNullOrBlank()) return@withContext album.artworkUrl
        runCatching {
            val items = fetchSearchStreamItems(
                query = "${album.title} movie song official",
                contentFilters = listOf(YoutubeSearchQueryHandlerFactory.MUSIC_SONGS)
            )
            items.firstOrNull()?.let { pickBestThumbnail(it.thumbnails) }
        }.getOrNull()
    }

    /**
     * Resolves the best progressive M4A audio-only stream from NewPipeExtractor's StreamInfo.
     * Filters out null/empty URLs, non-URL manifests, video-only streams, and DASH-only segments
     * whenever a progressive HTTP M4A (or WebM/Opus) audio stream is available.
     */
    suspend fun resolveAudioStream(videoId: String): ResolvedAudioStream = withContext(Dispatchers.IO) {
        val watchUrl = "https://www.youtube.com/watch?v=$videoId"
        Log.d(TAG, "Resolving audio stream for $watchUrl")

        val streamInfo = try {
            StreamInfo.getInfo(ServiceList.YouTube, watchUrl)
        } catch (e: Exception) {
            Log.e(TAG, "StreamInfo.getInfo failed for $videoId: ${e.javaClass.simpleName}: ${e.message}", e)
            throw IllegalStateException("Extraction failed: ${e.message ?: e.javaClass.simpleName}", e)
        }

        val allAudioStreams: List<AudioStream> = streamInfo.audioStreams.orEmpty()
        Log.d(
            TAG,
            "Found ${allAudioStreams.size} raw audioStreams for $videoId: " +
                allAudioStreams.joinToString { s ->
                    "[fmt=${s.format?.name}, delivery=${s.deliveryMethod}, bitrate=${s.averageBitrate}, isUrl=${s.isUrl}, hasContent=${!s.content.isNullOrBlank()}]"
                }
        )

        // Filter to valid playable HTTP URLs (excluding null/empty strings and non-URL manifest blobs)
        val validUrlAudioStreams = allAudioStreams.filter { stream ->
            stream.isUrl &&
                !stream.content.isNullOrBlank() &&
                stream.content.startsWith("http", ignoreCase = true)
        }

        // Strongly prefer progressive HTTP streams over DASH/HLS segment manifests
        val progressiveAudioStreams = validUrlAudioStreams.filter { stream ->
            stream.deliveryMethod == DeliveryMethod.PROGRESSIVE_HTTP
        }

        // Priority 1: Best progressive HTTP M4A (AAC) stream
        val bestProgressiveM4a = progressiveAudioStreams
            .filter { it.format == MediaFormat.M4A }
            .maxByOrNull { it.averageBitrate }

        // Priority 2: Best progressive HTTP WebM / Opus audio stream
        val bestProgressiveWebm = progressiveAudioStreams
            .filter { it.format == MediaFormat.WEBMA_OPUS || it.format == MediaFormat.WEBMA }
            .maxByOrNull { it.averageBitrate }

        // Priority 3: Any other progressive HTTP audio stream
        val bestAnyProgressive = progressiveAudioStreams
            .maxByOrNull { it.averageBitrate }

        // Priority 4: Any valid HTTP URL audio stream (preferring M4A, highest bitrate)
        val bestFallbackAudio = validUrlAudioStreams
            .sortedWith(
                compareByDescending<AudioStream> { if (it.format == MediaFormat.M4A) 2 else 1 }
                    .thenByDescending { it.averageBitrate }
            )
            .firstOrNull()

        val selectedAudio = bestProgressiveM4a
            ?: bestProgressiveWebm
            ?: bestAnyProgressive
            ?: bestFallbackAudio

        if (selectedAudio != null) {
            val format = selectedAudio.format
            val extension = when (format) {
                MediaFormat.M4A -> "m4a"
                MediaFormat.WEBMA, MediaFormat.WEBMA_OPUS -> "webm"
                else -> format?.suffix?.ifBlank { "m4a" } ?: "m4a"
            }
            val mimeType = format?.mimeType ?: if (extension == "m4a") "audio/mp4" else "audio/webm"
            Log.d(
                TAG,
                "Selected audio stream for $videoId: format=${format?.name}, delivery=${selectedAudio.deliveryMethod}, bitrate=${selectedAudio.averageBitrate}kbps"
            )
            return@withContext ResolvedAudioStream(
                url = selectedAudio.content,
                extension = extension,
                bitrateKbps = selectedAudio.averageBitrate.coerceAtLeast(128),
                mimeType = mimeType
            )
        }

        // Priority 5: Last-resort fallback to progressive muxed MP4 stream (contains audio track) if audioStreams was empty
        val muxedFallback = streamInfo.videoStreams.orEmpty()
            .filter { vs ->
                vs.isUrl &&
                    !vs.content.isNullOrBlank() &&
                    vs.content.startsWith("http", ignoreCase = true) &&
                    vs.deliveryMethod == DeliveryMethod.PROGRESSIVE_HTTP
            }
            .minByOrNull { it.height } // Lowest video resolution to minimize bandwidth when playing audio

        if (muxedFallback != null) {
            Log.w(TAG, "No audio-only stream found for $videoId; falling back to muxed stream (${muxedFallback.resolution})")
            return@withContext ResolvedAudioStream(
                url = muxedFallback.content,
                extension = muxedFallback.format?.suffix?.ifBlank { "mp4" } ?: "mp4",
                bitrateKbps = 128,
                mimeType = muxedFallback.format?.mimeType ?: "video/mp4"
            )
        }

        val errMsg = "No playable audio stream URL returned by YouTube for videoId=$videoId " +
            "(audioStreams=${allAudioStreams.size}, errors=${streamInfo.errors.size})"
        Log.e(TAG, errMsg)
        throw IllegalStateException(errMsg)
    }

    private fun fetchSearchStreamItems(
        query: String,
        contentFilters: List<String>
    ): List<StreamInfoItem> {
        val extractor = ServiceList.YouTube.getSearchExtractor(query, contentFilters, "")
        extractor.fetchPage()
        return extractor.initialPage.items.filterIsInstance<StreamInfoItem>()
    }

    private fun isLikelyIndividualSong(item: StreamInfoItem): Boolean {
        val titleLower = item.name.orEmpty().lowercase()
        if (titleLower.isBlank()) return false
        if (EXCLUDED_KEYWORDS.any { keyword -> titleLower.contains(keyword) }) return false

        val duration = item.duration
        if (duration <= 0L) return false
        if (duration < MIN_SONG_SECONDS || duration > MAX_INDIVIDUAL_SONG_SECONDS) return false
        return true
    }

    private fun StreamInfoItem.toTrack(albumTag: String): Track? {
        val videoId = extractVideoId(url.orEmpty()) ?: return null
        val cleanedTitle = cleanDisplayTitle(name.orEmpty())
        val cleanedArtist = cleanArtistName(uploaderName.orEmpty())
        val thumbnail = pickBestThumbnail(thumbnails).ifBlank {
            "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
        }
        return Track(
            videoId = videoId,
            title = cleanedTitle,
            artist = cleanedArtist,
            albumName = albumTag,
            durationSeconds = duration.coerceAtLeast(0L),
            thumbnailUrl = thumbnail
        )
    }

    private fun extractVideoId(url: String): String? {
        if (url.isBlank()) return null
        val parsed = runCatching { Uri.parse(url) }.getOrNull()
        val vParam = parsed?.getQueryParameter("v")
        if (!vParam.isNullOrBlank()) return vParam

        val pathSegments = parsed?.pathSegments.orEmpty()
        if (url.contains("youtu.be/") && pathSegments.isNotEmpty()) {
            return pathSegments.last()
        }
        val shortsIdx = pathSegments.indexOf("shorts")
        if (shortsIdx >= 0 && shortsIdx + 1 < pathSegments.size) {
            return pathSegments[shortsIdx + 1]
        }
        return null
    }

    private fun pickBestThumbnail(thumbnails: List<Image>?): String {
        if (thumbnails.isNullOrEmpty()) return ""
        return thumbnails
            .filter { !it.url.isNullOrBlank() }
            .maxByOrNull { it.width }
            ?.url
            ?: thumbnails.lastOrNull()?.url.orEmpty()
    }

    private fun cleanDisplayTitle(raw: String): String {
        return raw
            .replace(Regex("\\((?i:official\\s*(music\\s*)?(video|audio|lyric(al)?\\s*video)|full\\s*(video|audio|song)|4k|hd|lyrical)\\)"), "")
            .replace(Regex("\\[(?i:official\\s*(music\\s*)?(video|audio)|full\\s*(video|audio|song)|4k|hd)\\]"), "")
            .replace(Regex("\\s{2,}"), " ")
            .trim()
            .ifBlank { raw.trim() }
    }

    private fun cleanArtistName(raw: String): String {
        return raw
            .replace(" - Topic", "", ignoreCase = true)
            .replace("VEVO", "", ignoreCase = true)
            .trim()
            .ifBlank { "Various Artists" }
    }

    private fun deduplicateTracks(tracks: List<Track>): List<Track> {
        val seenIds = mutableSetOf<String>()
        val seenNormalizedTitles = mutableSetOf<String>()
        val result = mutableListOf<Track>()

        for (track in tracks) {
            val normalizedTitle = track.title
                .lowercase()
                .replace(Regex("[^a-z0-9]"), "")
                .take(28)
            if (seenIds.add(track.videoId) && (normalizedTitle.isBlank() || seenNormalizedTitles.add(normalizedTitle))) {
                result.add(track)
            }
        }
        return result
    }
}
