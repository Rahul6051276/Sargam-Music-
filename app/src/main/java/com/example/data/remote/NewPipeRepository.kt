package com.example.data.remote

import android.net.Uri
import android.util.Log
import com.example.data.model.ArtistRadio
import com.example.data.model.MoodCollection
import com.example.data.model.MovieAlbum
import com.example.data.model.ResolvedAudioStream
import com.example.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.Image
import org.schabi.newpipe.extractor.MediaFormat
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.playlist.PlaylistInfo
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory
import org.schabi.newpipe.extractor.stream.AudioStream
import org.schabi.newpipe.extractor.stream.DeliveryMethod
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class NewPipeRepository {

    companion object {
        private const val TAG = "NewPipeRepository"

        // Standard track duration bounds
        private const val MIN_SONG_SECONDS = 75L
        private const val MAX_INDIVIDUAL_SONG_SECONDS = 600L

        // Strict Original-Version Oldies duration bounds: 2.5 to 8 minutes
        private const val MIN_CLASSIC_ORIGINAL_SECONDS = 150L
        private const val MAX_CLASSIC_ORIGINAL_SECONDS = 480L

        private val BASE_EXCLUDED_KEYWORDS = listOf(
            "jukebox",
            "full album",
            "full movie",
            "audio jukebox",
            "video jukebox",
            "non stop",
            "nonstop",
            "1 hour",
            "2 hour",
            "3 hour",
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
            "ringtone",
            "#shorts",
            "shorts",
            "podcast",
            "episode",
            "comedy scene",
            "movie scene",
            "dialogue"
        )

        // Extra exclusions strictly applied to Old Classics & Classic Artist Radios
        private val CLASSIC_REMIX_COVER_EXCLUDED = listOf(
            "dj ",
            " dj",
            "remix",
            "bass boosted",
            "bassboosted",
            "8d audio",
            "8d ",
            "slowed",
            "reverb",
            "cover",
            "karaoke",
            "instrumental",
            "unplugged cover",
            "lofi flip",
            "lo-fi remix",
            "trap mix",
            "club mix",
            "tiktok",
            "reels",
            "tribute",
            "orchestra",
            "piano version",
            "guitar cover",
            "flute version",
            "female version",
            "male version",
            "ai cover",
            "scene"
        )

        // Trusted official vintage / studio music labels boosted during weighted shuffle
        private val OFFICIAL_LABEL_HINTS = listOf(
            "saregama",
            "t-series",
            "tips",
            "venus",
            "shemaroo",
            "sony music",
            "zee music",
            "yrf",
            "universal music",
            "gaane sune ansune",
            "ishtar music",
            "ultra bollywood",
            "- topic"
        )
    }

    private val collectionCache = ConcurrentHashMap<String, List<Track>>()
    private val albumCache = ConcurrentHashMap<String, List<Track>>()
    private val artistRadioCache = ConcurrentHashMap<String, List<Track>>()
    private val rotationCounter = AtomicInteger(0)

    private fun dailySeedIndex(seedCount: Int, extraOffset: Int = 0): Int {
        if (seedCount <= 1) return 0
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return ((dayOfYear + extraOffset) % seedCount + seedCount) % seedCount
    }

    suspend fun searchSongs(
        query: String,
        albumTag: String = "",
        limit: Int = 25,
        strictOriginalOldies: Boolean = false
    ): List<Track> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return@withContext emptyList()

        val collected = mutableListOf<Track>()

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
                .filter { isCleanMusicTrack(it, strictOriginalOldies) }
                .mapNotNull { it.toTrack(albumTag) }
        )

        if (collected.size < limit) {
            val videoItems = runCatching {
                fetchSearchStreamItems(
                    query = if (strictOriginalOldies) "$cleanQuery original audio song" else "$cleanQuery song audio",
                    contentFilters = listOf(YoutubeSearchQueryHandlerFactory.VIDEOS)
                )
            }.onFailure { err ->
                Log.w(TAG, "VIDEOS search failed for '$cleanQuery': ${err.message}")
            }.getOrDefault(emptyList())

            collected.addAll(
                videoItems
                    .filter { isCleanMusicTrack(it, strictOriginalOldies) }
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

        // 1. Real YouTube playlist extraction via PlaylistInfo.getInfo
        if (!album.playlistUrl.isNullOrBlank()) {
            tracks.addAll(
                extractTracksFromPlaylistUrl(
                    playlistUrl = album.playlistUrl,
                    albumTag = album.title,
                    strictOriginalOldies = false,
                    maxTracks = 30
                )
            )
        }

        // 2. Discover an official YouTube Music Album / Playlist for this movie
        if (tracks.size < 6) {
            val playlistUrls = findMatchingPlaylistUrls(
                query = "${album.title} original motion picture soundtrack",
                preferMusicAlbums = true,
                maxPlaylists = 2
            )
            for (url in playlistUrls) {
                if (tracks.size >= 18) break
                tracks.addAll(
                    extractTracksFromPlaylistUrl(
                        playlistUrl = url,
                        albumTag = album.title,
                        strictOriginalOldies = false,
                        maxTracks = 24
                    )
                )
            }
        }

        // 3. Supplement with individual song search excluding jukeboxes/trailers
        if (tracks.size < 8) {
            tracks.addAll(
                searchSongs(
                    query = album.searchQuery,
                    albumTag = album.title,
                    limit = 20,
                    strictOriginalOldies = false
                )
            )
        }

        val finalTracks = deduplicateTracks(tracks).take(25)
        if (finalTracks.isNotEmpty()) {
            albumCache[album.id] = finalTracks
        }
        finalTracks
    }

    /**
     * Fetches 30–50 real songs for any curated collection (Golden Retro, Trending, or Mood)
     * by combining real YouTube playlists (PlaylistInfo.getInfo) with rotated seed searches
     * and applying a smart weighted shuffle so fresh tracks appear at the top.
     */
    suspend fun fetchMoodTracks(
        mood: MoodCollection,
        forceRefresh: Boolean = false,
        targetCount: Int = 40
    ): List<Track> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            collectionCache[mood.id]?.let { cached ->
                if (cached.size >= 15) {
                    return@withContext smartWeightedShuffle(cached, mood.isOldClassic)
                }
            }
        }

        val rotation = if (forceRefresh) rotationCounter.incrementAndGet() else rotationCounter.get()
        val seeds = mood.seedQueries.ifEmpty { listOf(mood.searchQuery) }
        val primarySeedIdx = dailySeedIndex(seeds.size, rotation)
        val primarySeed = seeds[primarySeedIdx]
        val secondarySeed = seeds[(primarySeedIdx + 1) % seeds.size]

        val rawTracks = mutableListOf<Track>()

        // Step 1: Extract real curated YouTube playlists via PlaylistInfo.getInfo
        val playlistUrls = findMatchingPlaylistUrls(
            query = "$primarySeed playlist",
            preferMusicAlbums = false,
            maxPlaylists = 3
        )
        val rotatedPlaylistUrls = if (playlistUrls.size > 1) {
            val shift = rotation % playlistUrls.size
            playlistUrls.drop(shift) + playlistUrls.take(shift)
        } else {
            playlistUrls
        }

        for (plUrl in rotatedPlaylistUrls.take(2)) {
            if (rawTracks.size >= targetCount + 10) break
            rawTracks.addAll(
                extractTracksFromPlaylistUrl(
                    playlistUrl = plUrl,
                    albumTag = mood.title,
                    strictOriginalOldies = mood.isOldClassic,
                    maxTracks = 32
                )
            )
        }

        // Step 2: Combine with primary & secondary rotated searches for rich variety (30–50 songs)
        if (rawTracks.size < targetCount) {
            rawTracks.addAll(
                searchSongs(
                    query = primarySeed,
                    albumTag = mood.title,
                    limit = 25,
                    strictOriginalOldies = mood.isOldClassic
                )
            )
        }

        if (rawTracks.size < targetCount && secondarySeed != primarySeed) {
            rawTracks.addAll(
                searchSongs(
                    query = secondarySeed,
                    albumTag = mood.title,
                    limit = 25,
                    strictOriginalOldies = mood.isOldClassic
                )
            )
        }

        val unique = deduplicateTracks(rawTracks).take(50)
        val rotated = smartWeightedShuffle(unique, mood.isOldClassic)
        if (rotated.isNotEmpty()) {
            collectionCache[mood.id] = rotated
        }
        rotated
    }

    /**
     * Generates a 40-track Artist Radio mix for both Classic Legends (strict original filter)
     * and Modern Stars using real YouTube playlists + rotated artist seeds.
     */
    suspend fun fetchArtistRadioTracks(
        artist: ArtistRadio,
        forceRefresh: Boolean = false
    ): List<Track> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            artistRadioCache[artist.id]?.let { cached ->
                if (cached.size >= 20) {
                    return@withContext smartWeightedShuffle(cached, artist.isClassicLegend)
                }
            }
        }

        val rotation = if (forceRefresh) rotationCounter.incrementAndGet() else rotationCounter.get()
        val seeds = artist.seedQueries
        val primaryIdx = dailySeedIndex(seeds.size, rotation)
        val primarySeed = seeds[primaryIdx]
        val secondarySeed = seeds[(primaryIdx + 1) % seeds.size]

        val rawTracks = mutableListOf<Track>()

        // 1. Fetch tracks from top matching YouTube artist playlists via PlaylistInfo.getInfo
        val playlistUrls = findMatchingPlaylistUrls(
            query = "${artist.name} best hit songs playlist",
            preferMusicAlbums = false,
            maxPlaylists = 3
        )
        for (plUrl in playlistUrls.take(2)) {
            if (rawTracks.size >= 45) break
            rawTracks.addAll(
                extractTracksFromPlaylistUrl(
                    playlistUrl = plUrl,
                    albumTag = "${artist.name} Radio",
                    strictOriginalOldies = artist.isClassicLegend,
                    maxTracks = 30
                )
            )
        }

        // 2. Supplement with rotated artist searches to reach 40 handpicked songs
        if (rawTracks.size < 40) {
            rawTracks.addAll(
                searchSongs(
                    query = primarySeed,
                    albumTag = "${artist.name} Radio",
                    limit = 25,
                    strictOriginalOldies = artist.isClassicLegend
                )
            )
        }
        if (rawTracks.size < 40 && secondarySeed != primarySeed) {
            rawTracks.addAll(
                searchSongs(
                    query = secondarySeed,
                    albumTag = "${artist.name} Radio",
                    limit = 25,
                    strictOriginalOldies = artist.isClassicLegend
                )
            )
        }

        val unique = deduplicateTracks(rawTracks).take(40)
        val rotated = smartWeightedShuffle(unique, artist.isClassicLegend)
        if (rotated.isNotEmpty()) {
            artistRadioCache[artist.id] = rotated
        }
        rotated
    }

    /**
     * Fetches related music tracks using streamInfo.relatedItems for Infinite Auto-Play Radio,
     * preserving the current track's era/mood (e.g., old songs queue more classic old songs).
     */
    suspend fun fetchRelatedRadioTracks(
        videoId: String,
        excludeVideoIds: Set<String>,
        preferClassicEra: Boolean,
        fallbackAlbumTag: String = "Auto-Play Radio"
    ): List<Track> = withContext(Dispatchers.IO) {
        val watchUrl = "https://www.youtube.com/watch?v=$videoId"
        Log.d(TAG, "Fetching related Auto-Play radio tracks for $videoId (classicEra=$preferClassicEra)")

        val streamInfo = runCatching {
            StreamInfo.getInfo(ServiceList.YouTube, watchUrl)
        }.onFailure { err ->
            Log.w(TAG, "Related items fetch failed for $videoId: ${err.message}")
        }.getOrNull() ?: return@withContext emptyList()

        val relatedStreams = streamInfo.relatedItems
            .orEmpty()
            .filterIsInstance<StreamInfoItem>()

        val filtered = relatedStreams
            .filter { isCleanMusicTrack(it, strictOriginalOldies = preferClassicEra) }
            .mapNotNull { it.toTrack(fallbackAlbumTag) }
            .filter { it.videoId !in excludeVideoIds }

        val unique = deduplicateTracks(filtered)
        // If strict classic filter was too tight on related items, relax slightly while still blocking jukeboxes/shorts
        val finalPool = if (unique.size < 3 && preferClassicEra) {
            val relaxed = relatedStreams
                .filter { isCleanMusicTrack(it, strictOriginalOldies = false) }
                .mapNotNull { it.toTrack(fallbackAlbumTag) }
                .filter { it.videoId !in excludeVideoIds }
            deduplicateTracks(unique + relaxed)
        } else {
            unique
        }

        smartWeightedShuffle(finalPool, preferClassicEra).take(10)
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

    suspend fun fetchThumbnailPreviewForQuery(query: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            val items = fetchSearchStreamItems(
                query = query,
                contentFilters = listOf(YoutubeSearchQueryHandlerFactory.MUSIC_SONGS)
            )
            items.firstOrNull()?.let { pickBestThumbnail(it.thumbnails) }
        }.getOrNull()
    }

    /**
     * Resolves the best progressive M4A audio-only stream from NewPipeExtractor's StreamInfo.
     * Preserved intact for reliable ExoPlayer streaming and offline downloads.
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

        val validUrlAudioStreams = allAudioStreams.filter { stream ->
            stream.isUrl &&
                !stream.content.isNullOrBlank() &&
                stream.content.startsWith("http", ignoreCase = true)
        }

        val progressiveAudioStreams = validUrlAudioStreams.filter { stream ->
            stream.deliveryMethod == DeliveryMethod.PROGRESSIVE_HTTP
        }

        val bestProgressiveM4a = progressiveAudioStreams
            .filter { it.format == MediaFormat.M4A }
            .maxByOrNull { it.averageBitrate }

        val bestProgressiveWebm = progressiveAudioStreams
            .filter { it.format == MediaFormat.WEBMA_OPUS || it.format == MediaFormat.WEBMA }
            .maxByOrNull { it.averageBitrate }

        val bestAnyProgressive = progressiveAudioStreams
            .maxByOrNull { it.averageBitrate }

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

        val muxedFallback = streamInfo.videoStreams.orEmpty()
            .filter { vs ->
                vs.isUrl &&
                    !vs.content.isNullOrBlank() &&
                    vs.content.startsWith("http", ignoreCase = true) &&
                    vs.deliveryMethod == DeliveryMethod.PROGRESSIVE_HTTP
            }
            .minByOrNull { it.height }

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

    private fun findMatchingPlaylistUrls(
        query: String,
        preferMusicAlbums: Boolean,
        maxPlaylists: Int
    ): List<String> {
        val urls = mutableListOf<String>()
        val filterOrder = if (preferMusicAlbums) {
            listOf(YoutubeSearchQueryHandlerFactory.MUSIC_ALBUMS, YoutubeSearchQueryHandlerFactory.PLAYLISTS)
        } else {
            listOf(YoutubeSearchQueryHandlerFactory.PLAYLISTS, YoutubeSearchQueryHandlerFactory.MUSIC_ALBUMS)
        }

        for (filter in filterOrder) {
            if (urls.size >= maxPlaylists) break
            runCatching {
                val extractor = ServiceList.YouTube.getSearchExtractor(query, listOf(filter), "")
                extractor.fetchPage()
                val items = extractor.initialPage.items.filterIsInstance<PlaylistInfoItem>()
                items.forEach { item ->
                    val plUrl = item.url
                    if (!plUrl.isNullOrBlank() && plUrl !in urls && (item.streamCount <= 0 || item.streamCount >= 6)) {
                        urls.add(plUrl)
                    }
                }
            }.onFailure { err ->
                Log.w(TAG, "Playlist search ($filter) failed for '$query': ${err.message}")
            }
        }
        return urls.take(maxPlaylists)
    }

    private fun extractTracksFromPlaylistUrl(
        playlistUrl: String,
        albumTag: String,
        strictOriginalOldies: Boolean,
        maxTracks: Int
    ): List<Track> {
        val collected = mutableListOf<Track>()
        runCatching {
            val playlistInfo = PlaylistInfo.getInfo(ServiceList.YouTube, playlistUrl)
            val firstPageItems = playlistInfo.relatedItems.orEmpty()
            collected.addAll(
                firstPageItems
                    .filter { isCleanMusicTrack(it, strictOriginalOldies) }
                    .mapNotNull { it.toTrack(albumTag) }
            )

            // Fetch a second page if the playlist is larger and we still want more songs
            if (collected.size < maxTracks && playlistInfo.hasNextPage() && playlistInfo.nextPage != null) {
                runCatching {
                    val more = PlaylistInfo.getMoreItems(ServiceList.YouTube, playlistUrl, playlistInfo.nextPage)
                    collected.addAll(
                        more.items.orEmpty()
                            .filter { isCleanMusicTrack(it, strictOriginalOldies) }
                            .mapNotNull { it.toTrack(albumTag) }
                    )
                }
            }
        }.onFailure { err ->
            Log.w(TAG, "PlaylistInfo.getInfo failed for $playlistUrl: ${err.message}")
        }
        return collected.take(maxTracks)
    }

    private fun fetchSearchStreamItems(
        query: String,
        contentFilters: List<String>
    ): List<StreamInfoItem> {
        val extractor = ServiceList.YouTube.getSearchExtractor(query, contentFilters, "")
        extractor.fetchPage()
        return extractor.initialPage.items.filterIsInstance<StreamInfoItem>()
    }

    private fun isCleanMusicTrack(item: StreamInfoItem, strictOriginalOldies: Boolean): Boolean {
        val titleLower = item.name.orEmpty().lowercase()
        val uploaderLower = item.uploaderName.orEmpty().lowercase()
        if (titleLower.isBlank()) return false

        // Always block shorts, trailers, jukeboxes, interviews, reactions, full movies
        if (BASE_EXCLUDED_KEYWORDS.any { keyword -> titleLower.contains(keyword) }) return false
        if (item.url.orEmpty().contains("/shorts/", ignoreCase = true)) return false

        val duration = item.duration
        if (duration <= 0L) return false

        if (strictOriginalOldies) {
            // Keep strictly 2.5 to 8 minute clean original audio tracks
            if (duration < MIN_CLASSIC_ORIGINAL_SECONDS || duration > MAX_CLASSIC_ORIGINAL_SECONDS) {
                return false
            }
            if (CLASSIC_REMIX_COVER_EXCLUDED.any { bad ->
                    titleLower.contains(bad) || uploaderLower.contains("dj ") || uploaderLower.contains("remix")
                }
            ) {
                return false
            }
        } else {
            if (duration < MIN_SONG_SECONDS || duration > MAX_INDIVIDUAL_SONG_SECONDS) {
                return false
            }
        }
        return true
    }

    /**
     * Applies a smart weighted shuffle: official-label / high-quality studio tracks are placed
     * in the top tier and shuffled within tiers so users always hear fresh songs first without
     * sacrificing audio/originality quality.
     */
    private fun smartWeightedShuffle(tracks: List<Track>, prioritizeOfficialLabels: Boolean): List<Track> {
        if (tracks.size <= 3) return tracks
        val random = Random(System.currentTimeMillis() xor tracks.size.toLong())
        if (!prioritizeOfficialLabels) {
            val topBucket = tracks.take(14).shuffled(random)
            val restBucket = tracks.drop(14).shuffled(random)
            return topBucket + restBucket
        }

        val (officialTier, standardTier) = tracks.partition { track ->
            val artistLower = track.artist.lowercase()
            OFFICIAL_LABEL_HINTS.any { hint -> artistLower.contains(hint) }
        }
        val shuffledOfficial = officialTier.shuffled(random)
        val shuffledStandard = standardTier.shuffled(random)

        // Interleave top official gems with fresh discoveries so the top feels both authentic and varied
        val result = mutableListOf<Track>()
        var oIdx = 0
        var sIdx = 0
        while (oIdx < shuffledOfficial.size || sIdx < shuffledStandard.size) {
            repeat(2) {
                if (oIdx < shuffledOfficial.size) result.add(shuffledOfficial[oIdx++])
            }
            if (sIdx < shuffledStandard.size) result.add(shuffledStandard[sIdx++])
        }
        return result
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
            .replace(Regex("\\((?i:official\\s*(music\\s*)?(video|audio|lyric(al)?\\s*video)|full\\s*(video|audio|song)|4k|hd|lyrical|remastered)\\)"), "")
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
