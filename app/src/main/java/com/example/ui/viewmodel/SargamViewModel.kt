package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SargamApplication
import com.example.data.download.BatchDownloadManager
import com.example.data.local.PlaylistEntity
import com.example.data.local.PlaylistSummary
import com.example.data.model.ArtistRadio
import com.example.data.model.BatchDownloadState
import com.example.data.model.CuratedCatalog
import com.example.data.model.DownloadedAlbumGroup
import com.example.data.model.HomeFilterChip
import com.example.data.model.MoodCollection
import com.example.data.model.MovieAlbum
import com.example.data.model.PlayerUiState
import com.example.data.model.SearchFilter
import com.example.data.model.Track
import com.example.data.model.TrackDownloadProgress
import com.example.data.remote.NewPipeRepository
import com.example.data.repository.MusicLibraryRepository
import com.example.player.PlaybackController
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedFilter: HomeFilterChip = HomeFilterChip.ALL,
    val goldenRetroCollections: List<MoodCollection> = CuratedCatalog.goldenRetroCollections,
    val trendingCollections: List<MoodCollection> = CuratedCatalog.trendingFreshCollections,
    val moodCollections: List<MoodCollection> = CuratedCatalog.moodActivityCollections,
    val artistRadios: List<ArtistRadio> = CuratedCatalog.artistRadios,
    val featuredAlbums: List<MovieAlbum> = CuratedCatalog.featuredMovieAlbums,
    val freshPicks: List<Track> = emptyList(),
    val freshPicksTitle: String = "Fresh Picks For You",
    val isLoadingFreshPicks: Boolean = true,
    val errorMessage: String? = null
) {
    val visibleGoldenRetro: List<MoodCollection>
        get() = if (selectedFilter == HomeFilterChip.ALL) {
            goldenRetroCollections
        } else {
            goldenRetroCollections.filter { selectedFilter in it.filters }
        }

    val visibleTrending: List<MoodCollection>
        get() = if (selectedFilter == HomeFilterChip.ALL) {
            trendingCollections
        } else {
            trendingCollections.filter { selectedFilter in it.filters }
        }

    val visibleMoods: List<MoodCollection>
        get() = if (selectedFilter == HomeFilterChip.ALL) {
            moodCollections
        } else {
            moodCollections.filter { selectedFilter in it.filters }
        }

    val visibleArtistRadios: List<ArtistRadio>
        get() = if (selectedFilter == HomeFilterChip.ALL) {
            artistRadios
        } else {
            artistRadios.filter { selectedFilter in it.filters }
                .ifEmpty { artistRadios }
        }

    val visibleMovieAlbums: List<MovieAlbum>
        get() = if (selectedFilter == HomeFilterChip.ALL) {
            featuredAlbums
        } else {
            featuredAlbums.filter { selectedFilter in it.filters }
        }
}

data class SearchUiState(
    val query: String = "",
    val filter: SearchFilter = SearchFilter.ALL,
    val songResults: List<Track> = emptyList(),
    val albumResults: List<MovieAlbum> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false,
    val errorMessage: String? = null
)

data class AlbumDetailUiState(
    val album: MovieAlbum? = null,
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class MoodDetailUiState(
    val mood: MoodCollection? = null,
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ArtistRadioDetailUiState(
    val artist: ArtistRadio? = null,
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SargamViewModel(
    private val newPipeRepository: NewPipeRepository,
    private val libraryRepository: MusicLibraryRepository,
    private val batchDownloadManager: BatchDownloadManager,
    private val playbackController: PlaybackController
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val _albumDetailState = MutableStateFlow(AlbumDetailUiState())
    val albumDetailState: StateFlow<AlbumDetailUiState> = _albumDetailState.asStateFlow()

    private val _moodDetailState = MutableStateFlow(MoodDetailUiState())
    val moodDetailState: StateFlow<MoodDetailUiState> = _moodDetailState.asStateFlow()

    private val _artistRadioDetailState = MutableStateFlow(ArtistRadioDetailUiState())
    val artistRadioDetailState: StateFlow<ArtistRadioDetailUiState> = _artistRadioDetailState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val playerState: StateFlow<PlayerUiState> = playbackController.uiState

    val trackDownloadProgress: StateFlow<Map<String, TrackDownloadProgress>> =
        batchDownloadManager.trackProgress

    val batchDownloadState: StateFlow<BatchDownloadState?> =
        batchDownloadManager.batchState

    val downloadedTracks: StateFlow<List<Track>> = libraryRepository.downloadedTracksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedVideoIds: StateFlow<Set<String>> = libraryRepository.downloadedVideoIdsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val downloadedAlbums: StateFlow<List<DownloadedAlbumGroup>> = libraryRepository.downloadedAlbumsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDownloadSizeBytes: StateFlow<Long> = libraryRepository.totalDownloadSizeBytesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val playlistSummaries: StateFlow<List<PlaylistSummary>> = libraryRepository.playlistSummariesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteVideoIds: StateFlow<Set<String>> = libraryRepository.favoriteVideoIdsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private var homeLoadJob: Job? = null
    private var searchJob: Job? = null
    private var albumLoadJob: Job? = null
    private var moodLoadJob: Job? = null
    private var artistLoadJob: Job? = null

    init {
        loadHomeContent(forceRefresh = false)
        prefetchHomeArtwork()
    }

    fun selectHomeFilter(filter: HomeFilterChip) {
        if (_homeState.value.selectedFilter == filter) return
        _homeState.update { it.copy(selectedFilter = filter) }
        loadHomeContent(forceRefresh = false)
    }

    fun loadHomeContent(forceRefresh: Boolean = false) {
        homeLoadJob?.cancel()
        homeLoadJob = viewModelScope.launch {
            val activeFilter = _homeState.value.selectedFilter
            val targetCollection = CuratedCatalog.freshPickQueryForFilter(activeFilter)
            val sectionTitle = when (activeFilter) {
                HomeFilterChip.ALL -> "Fresh Picks For You"
                else -> "${activeFilter.label} Picks"
            }

            _homeState.update {
                it.copy(
                    freshPicksTitle = sectionTitle,
                    isLoadingFreshPicks = true,
                    errorMessage = null
                )
            }
            try {
                val tracks = newPipeRepository.fetchMoodTracks(
                    mood = targetCollection,
                    forceRefresh = forceRefresh,
                    targetCount = 24
                )
                _homeState.update {
                    it.copy(
                        freshPicks = tracks,
                        isLoadingFreshPicks = false,
                        errorMessage = if (tracks.isEmpty()) "No online tracks found right now." else null
                    )
                }
            } catch (e: Exception) {
                _homeState.update {
                    it.copy(
                        isLoadingFreshPicks = false,
                        errorMessage = "You appear to be offline. Switch to Downloads for saved tracks."
                    )
                }
            }
        }
    }

    private fun prefetchHomeArtwork() {
        // Prefetch featured movie album covers
        viewModelScope.launch {
            val albums = _homeState.value.featuredAlbums
            val updated = albums.map { album ->
                async {
                    val cover = newPipeRepository.fetchAlbumCoverPreview(album)
                    if (!cover.isNullOrBlank()) album.copy(artworkUrl = cover) else album
                }
            }.awaitAll()
            _homeState.update { it.copy(featuredAlbums = updated) }
        }

        // Prefetch circular artist radio avatars
        viewModelScope.launch {
            val artists = _homeState.value.artistRadios
            val updatedArtists = artists.map { artist ->
                async {
                    val thumb = newPipeRepository.fetchThumbnailPreviewForQuery(
                        "${artist.name} official audio song"
                    )
                    if (!thumb.isNullOrBlank()) artist.copy(avatarUrl = thumb) else artist
                }
            }.awaitAll()
            _homeState.update { it.copy(artistRadios = updatedArtists) }
        }

        // Prefetch shelf cover art for Golden Retro & Trending shelves
        viewModelScope.launch {
            val retroUpdated = _homeState.value.goldenRetroCollections.map { col ->
                async {
                    val thumb = newPipeRepository.fetchThumbnailPreviewForQuery(col.seedQueries.first())
                    if (!thumb.isNullOrBlank()) col.copy(artworkUrl = thumb) else col
                }
            }.awaitAll()
            _homeState.update { it.copy(goldenRetroCollections = retroUpdated) }

            val trendingUpdated = _homeState.value.trendingCollections.map { col ->
                async {
                    val thumb = newPipeRepository.fetchThumbnailPreviewForQuery(col.seedQueries.first())
                    if (!thumb.isNullOrBlank()) col.copy(artworkUrl = thumb) else col
                }
            }.awaitAll()
            _homeState.update { it.copy(trendingCollections = trendingUpdated) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchState.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchState.update {
                it.copy(
                    songResults = emptyList(),
                    albumResults = emptyList(),
                    isSearching = false,
                    hasSearched = false,
                    errorMessage = null
                )
            }
            return
        }
        searchJob = viewModelScope.launch {
            delay(450L)
            executeSearch(query)
        }
    }

    fun submitSearchNow(query: String = _searchState.value.query) {
        _searchState.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) return
        searchJob = viewModelScope.launch {
            executeSearch(query)
        }
    }

    fun onSearchFilterSelected(filter: SearchFilter) {
        _searchState.update { it.copy(filter = filter) }
    }

    private suspend fun executeSearch(query: String) {
        _searchState.update { it.copy(isSearching = true, errorMessage = null) }
        try {
            val songsDeferred = viewModelScope.async { newPipeRepository.searchSongs(query, limit = 25) }
            val albumsDeferred = viewModelScope.async { newPipeRepository.searchMovieAlbums(query) }
            val songs = songsDeferred.await()
            val albums = albumsDeferred.await()

            val topSongThumb = songs.firstOrNull()?.thumbnailUrl
            val enrichedAlbums = albums.mapIndexed { idx, album ->
                if (idx == 0 && album.artworkUrl.isNullOrBlank() && !topSongThumb.isNullOrBlank()) {
                    album.copy(artworkUrl = topSongThumb)
                } else {
                    album
                }
            }

            _searchState.update {
                it.copy(
                    songResults = songs,
                    albumResults = enrichedAlbums,
                    isSearching = false,
                    hasSearched = true,
                    errorMessage = if (songs.isEmpty() && enrichedAlbums.isEmpty()) {
                        "No tracks or albums matched \"$query\"."
                    } else null
                )
            }
        } catch (e: Exception) {
            _searchState.update {
                it.copy(
                    isSearching = false,
                    hasSearched = true,
                    errorMessage = "Search failed. Check your internet connection."
                )
            }
        }
    }

    fun openMovieAlbum(album: MovieAlbum, forceRefresh: Boolean = false) {
        albumLoadJob?.cancel()
        _albumDetailState.value = AlbumDetailUiState(
            album = album,
            tracks = emptyList(),
            isLoading = true,
            errorMessage = null
        )
        albumLoadJob = viewModelScope.launch {
            try {
                val fetchedTracks = newPipeRepository.fetchMovieAlbumTracks(album, forceRefresh)
                val coverArt = album.artworkUrl?.takeIf { it.isNotBlank() }
                    ?: fetchedTracks.firstOrNull { it.thumbnailUrl.isNotBlank() }?.thumbnailUrl
                _albumDetailState.update {
                    it.copy(
                        album = album.copy(artworkUrl = coverArt),
                        tracks = fetchedTracks,
                        isLoading = false,
                        errorMessage = if (fetchedTracks.isEmpty()) {
                            "Could not load songs for ${album.title}. Try refreshing."
                        } else null
                    )
                }
            } catch (e: Exception) {
                val offlineMatches = downloadedTracks.value.filter {
                    it.albumName.equals(album.title, ignoreCase = true)
                }
                _albumDetailState.update {
                    it.copy(
                        tracks = offlineMatches,
                        isLoading = false,
                        errorMessage = if (offlineMatches.isEmpty()) {
                            "Offline: no downloaded tracks found for ${album.title}."
                        } else null
                    )
                }
            }
        }
    }

    fun openMoodCollection(mood: MoodCollection, forceRefresh: Boolean = false) {
        moodLoadJob?.cancel()
        _moodDetailState.value = MoodDetailUiState(
            mood = mood,
            tracks = emptyList(),
            isLoading = true,
            errorMessage = null
        )
        moodLoadJob = viewModelScope.launch {
            try {
                val tracks = newPipeRepository.fetchMoodTracks(
                    mood = mood,
                    forceRefresh = forceRefresh,
                    targetCount = 40
                )
                _moodDetailState.update {
                    it.copy(
                        tracks = tracks,
                        isLoading = false,
                        errorMessage = if (tracks.isEmpty()) {
                            "No tracks returned for ${mood.title}. Tap Refresh."
                        } else null
                    )
                }
            } catch (e: Exception) {
                val offlineMatches = downloadedTracks.value.filter {
                    it.albumName.equals(mood.title, ignoreCase = true)
                }
                _moodDetailState.update {
                    it.copy(
                        tracks = offlineMatches,
                        isLoading = false,
                        errorMessage = if (offlineMatches.isEmpty()) {
                            "Offline: download ${mood.title} while connected to play anytime."
                        } else null
                    )
                }
            }
        }
    }

    fun openArtistRadio(artist: ArtistRadio, forceRefresh: Boolean = false) {
        artistLoadJob?.cancel()
        val enrichedArtist = _homeState.value.artistRadios.firstOrNull { it.id == artist.id } ?: artist
        _artistRadioDetailState.value = ArtistRadioDetailUiState(
            artist = enrichedArtist,
            tracks = emptyList(),
            isLoading = true,
            errorMessage = null
        )
        artistLoadJob = viewModelScope.launch {
            try {
                val tracks = newPipeRepository.fetchArtistRadioTracks(
                    artist = enrichedArtist,
                    forceRefresh = forceRefresh
                )
                val avatar = enrichedArtist.avatarUrl?.takeIf { it.isNotBlank() }
                    ?: tracks.firstOrNull { it.thumbnailUrl.isNotBlank() }?.thumbnailUrl
                _artistRadioDetailState.update {
                    it.copy(
                        artist = enrichedArtist.copy(avatarUrl = avatar),
                        tracks = tracks,
                        isLoading = false,
                        errorMessage = if (tracks.isEmpty()) {
                            "Unable to load radio mix for ${enrichedArtist.name}. Tap Refresh."
                        } else null
                    )
                }
            } catch (e: Exception) {
                val offlineMatches = downloadedTracks.value.filter {
                    it.artist.contains(enrichedArtist.name, ignoreCase = true) ||
                        it.albumName.contains(enrichedArtist.name, ignoreCase = true)
                }
                _artistRadioDetailState.update {
                    it.copy(
                        tracks = offlineMatches,
                        isLoading = false,
                        errorMessage = if (offlineMatches.isEmpty()) {
                            "Offline: download ${enrichedArtist.name} Radio while connected."
                        } else null
                    )
                }
            }
        }
    }

    // Playback actions
    fun playTrack(track: Track, queue: List<Track> = listOf(track)) {
        playbackController.playTrack(track, queue)
    }

    fun playAll(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) {
            showSnackbar("No tracks available to play.")
            return
        }
        playbackController.playQueue(tracks, startIndex)
    }

    fun playTrendingHero() {
        val currentFresh = _homeState.value.freshPicks
        if (currentFresh.isNotEmpty()) {
            playAll(currentFresh)
        } else if (downloadedTracks.value.isNotEmpty()) {
            playAll(downloadedTracks.value)
        } else {
            viewModelScope.launch {
                val trending = CuratedCatalog.trendingFreshCollections.first()
                val tracks = runCatching { newPipeRepository.fetchMoodTracks(trending) }.getOrDefault(emptyList())
                if (tracks.isNotEmpty()) {
                    playAll(tracks)
                } else {
                    showSnackbar("Connect to internet to stream Trending hits.")
                }
            }
        }
    }

    fun togglePlayPause() = playbackController.togglePlayPause()
    fun skipNext() = playbackController.skipToNext()
    fun skipPrevious() = playbackController.skipToPrevious()
    fun seekTo(positionMs: Long) = playbackController.seekTo(positionMs)
    fun toggleShuffle() = playbackController.toggleShuffle()
    fun toggleAutoPlay() {
        playbackController.toggleAutoPlay()
        val enabled = playbackController.uiState.value.autoPlayEnabled
        showSnackbar(if (enabled) "Infinite Auto-Play Radio enabled" else "Auto-Play Radio paused")
    }
    fun cycleRepeatMode() = playbackController.cycleRepeatMode()
    fun playQueueIndex(index: Int) = playbackController.playQueueIndex(index)
    fun clearPlaybackError() = playbackController.clearError()

    // Download actions
    fun downloadTrack(track: Track) {
        if (downloadedVideoIds.value.contains(track.videoId)) {
            showSnackbar("\"${track.title}\" is already saved offline.")
            return
        }
        batchDownloadManager.downloadSingleTrack(track)
        showSnackbar("Downloading \"${track.title}\" for offline playback…")
    }

    fun downloadCollection(collectionTitle: String, tracks: List<Track>) {
        if (tracks.isEmpty()) {
            showSnackbar("No tracks to download.")
            return
        }
        batchDownloadManager.downloadCollection(collectionTitle, tracks)
        showSnackbar("Queued ${tracks.size} songs from $collectionTitle for offline download.")
    }

    fun cancelBatchDownload() = batchDownloadManager.cancelBatchDownload()
    fun dismissBatchBanner() = batchDownloadManager.dismissBatchBanner()

    fun deleteDownloadedTrack(track: Track) {
        viewModelScope.launch {
            libraryRepository.deleteDownloadedTrack(track.videoId)
            showSnackbar("Removed \"${track.title}\" from offline storage.")
        }
    }

    fun deleteDownloadedAlbum(albumName: String) {
        viewModelScope.launch {
            libraryRepository.deleteDownloadedAlbum(albumName)
            showSnackbar("Removed offline album \"$albumName\".")
        }
    }

    // Playlist & Favorite actions
    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            val isNowFav = libraryRepository.toggleFavorite(track)
            showSnackbar(
                if (isNowFav) "Added \"${track.title}\" to Favorites"
                else "Removed \"${track.title}\" from Favorites"
            )
        }
    }

    fun createPlaylist(name: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            libraryRepository.createPlaylist(name, description)
            showSnackbar("Created playlist \"$name\"")
        }
    }

    fun deletePlaylist(playlistId: Long, name: String) {
        viewModelScope.launch {
            libraryRepository.deletePlaylist(playlistId)
            showSnackbar("Deleted playlist \"$name\"")
        }
    }

    fun addTrackToPlaylist(playlistId: Long, playlistName: String, track: Track) {
        viewModelScope.launch {
            libraryRepository.addTrackToPlaylist(playlistId, track)
            showSnackbar("Added \"${track.title}\" to $playlistName")
        }
    }

    fun removeTrackFromPlaylist(playlistId: Long, track: Track) {
        viewModelScope.launch {
            libraryRepository.removeTrackFromPlaylist(playlistId, track.videoId)
            showSnackbar("Removed \"${track.title}\" from playlist")
        }
    }

    fun playOfflinePlaylist(playlistId: Long, playlistName: String) {
        viewModelScope.launch {
            val offlineTracks = libraryRepository.getOfflineTracksForPlaylist(playlistId)
            if (offlineTracks.isEmpty()) {
                showSnackbar("No downloaded songs in \"$playlistName\" yet.")
            } else {
                playAll(offlineTracks)
            }
        }
    }

    fun observePlaylist(playlistId: Long): Flow<PlaylistEntity?> =
        libraryRepository.observePlaylist(playlistId)

    fun observePlaylistTracks(playlistId: Long): Flow<List<Track>> =
        libraryRepository.observePlaylistTracks(playlistId)

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    companion object {
        fun provideFactory(app: SargamApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SargamViewModel(
                        newPipeRepository = app.newPipeRepository,
                        libraryRepository = app.libraryRepository,
                        batchDownloadManager = app.batchDownloadManager,
                        playbackController = app.playbackController
                    ) as T
                }
            }
    }
}
