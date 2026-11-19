package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.model.CuratedCatalog
import com.example.data.model.Track
import com.example.ui.components.AddToPlaylistBottomSheet
import com.example.ui.components.BatchDownloadBanner
import com.example.ui.components.CreatePlaylistDialog
import com.example.ui.components.FullScreenNowPlayingDialog
import com.example.ui.components.StickyMiniPlayer
import com.example.ui.navigation.AlbumDetailRoute
import com.example.ui.navigation.DownloadsRoute
import com.example.ui.navigation.HomeRoute
import com.example.ui.navigation.LibraryRoute
import com.example.ui.navigation.MoodDetailRoute
import com.example.ui.navigation.PlaylistDetailRoute
import com.example.ui.navigation.SearchRoute
import com.example.ui.screens.AlbumDetailScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MoodDetailScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.EmeraldTealContainer
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.SargamTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.SargamViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SargamViewModel by viewModels {
        SargamViewModel.provideFactory(application as SargamApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            SargamTheme {
                SargamAppRoot(viewModel = viewModel)
            }
        }
    }
}

private data class TopLevelTab(
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Any,
    val testTag: String
)

@Composable
private fun SargamAppRoot(viewModel: SargamViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    // Request POST_NOTIFICATIONS cleanly on Android 13+ for lock-screen media controls visibility
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val homeState by viewModel.homeState.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val albumDetailState by viewModel.albumDetailState.collectAsStateWithLifecycle()
    val moodDetailState by viewModel.moodDetailState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val downloadedTracks by viewModel.downloadedTracks.collectAsStateWithLifecycle()
    val downloadedVideoIds by viewModel.downloadedVideoIds.collectAsStateWithLifecycle()
    val downloadedAlbums by viewModel.downloadedAlbums.collectAsStateWithLifecycle()
    val totalDownloadBytes by viewModel.totalDownloadSizeBytes.collectAsStateWithLifecycle()
    val playlistSummaries by viewModel.playlistSummaries.collectAsStateWithLifecycle()
    val favoriteVideoIds by viewModel.favoriteVideoIds.collectAsStateWithLifecycle()
    val downloadProgressMap by viewModel.trackDownloadProgress.collectAsStateWithLifecycle()
    val batchDownloadState by viewModel.batchDownloadState.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var showNowPlaying by remember { mutableStateOf(false) }
    var trackForPlaylistSheet by remember { mutableStateOf<Track?>(null) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(playerState.errorMessage) {
        playerState.errorMessage?.let { err ->
            snackbarHostState.showSnackbar(err)
        }
    }

    val tabs = remember {
        listOf(
            TopLevelTab(
                labelRes = R.string.nav_home,
                selectedIcon = Icons.Rounded.Home,
                unselectedIcon = Icons.Outlined.Home,
                route = HomeRoute,
                testTag = "nav_tab_home"
            ),
            TopLevelTab(
                labelRes = R.string.nav_search,
                selectedIcon = Icons.Rounded.Search,
                unselectedIcon = Icons.Outlined.Search,
                route = SearchRoute,
                testTag = "nav_tab_search"
            ),
            TopLevelTab(
                labelRes = R.string.nav_downloads,
                selectedIcon = Icons.Rounded.DownloadForOffline,
                unselectedIcon = Icons.Outlined.DownloadForOffline,
                route = DownloadsRoute,
                testTag = "nav_tab_downloads"
            ),
            TopLevelTab(
                labelRes = R.string.nav_library,
                selectedIcon = Icons.Rounded.LibraryMusic,
                unselectedIcon = Icons.Outlined.LibraryMusic,
                route = LibraryRoute,
                testTag = "nav_tab_library"
            )
        )
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigateToTab: (Any) -> Unit = { destination ->
        navController.navigate(destination) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBackground)
    ) {
        val useNavigationRail = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail) {
                NavigationRail(
                    containerColor = MidnightSurface,
                    contentColor = TextPrimary,
                    modifier = Modifier
                        .fillMaxHeight()
                        .statusBarsPadding()
                ) {
                    tabs.forEach { tab ->
                        val selected = currentDestination?.hasRoute(tab.route::class) == true
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateToTab(tab.route) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = stringResource(tab.labelRes)
                                )
                            },
                            label = { Text(stringResource(tab.labelRes)) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = EmeraldTeal,
                                selectedTextColor = EmeraldTeal,
                                indicatorColor = EmeraldTealContainer,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = MidnightBackground,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = MidnightSurfaceElevated,
                            contentColor = TextPrimary,
                            actionColor = EmeraldTeal
                        )
                    }
                },
                bottomBar = {
                    if (!useNavigationRail) {
                        NavigationBar(
                            containerColor = MidnightSurface,
                            tonalElevation = 0.dp
                        ) {
                            tabs.forEach { tab ->
                                val selected = currentDestination?.hasRoute(tab.route::class) == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = stringResource(tab.labelRes)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(tab.labelRes),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EmeraldTeal,
                                        selectedTextColor = EmeraldTeal,
                                        indicatorColor = EmeraldTealContainer,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted
                                    ),
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable<HomeRoute> {
                            HomeScreen(
                                homeState = homeState,
                                currentPlayingVideoId = playerState.currentTrack?.videoId,
                                downloadedVideoIds = downloadedVideoIds,
                                favoriteVideoIds = favoriteVideoIds,
                                downloadProgressMap = downloadProgressMap,
                                onSearchShortcutClick = { navigateToTab(SearchRoute) },
                                onPlayTrendingClick = { viewModel.playTrendingHero() },
                                onMoodClick = { mood ->
                                    viewModel.openMoodCollection(mood)
                                    navController.navigate(MoodDetailRoute(mood.id))
                                },
                                onMovieAlbumClick = { album ->
                                    viewModel.openMovieAlbum(album)
                                    navController.navigate(AlbumDetailRoute(album.id))
                                },
                                onTrackClick = { track, queue -> viewModel.playTrack(track, queue) },
                                onPlayAllFresh = { tracks -> viewModel.playAll(tracks) },
                                onDownloadAllFresh = { tracks ->
                                    viewModel.downloadCollection("New Releases", tracks)
                                },
                                onDownloadTrack = { viewModel.downloadTrack(it) },
                                onAddToPlaylist = { trackForPlaylistSheet = it },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onRefreshHome = { viewModel.loadHomeContent(forceRefresh = true) }
                            )
                        }

                        composable<SearchRoute> {
                            SearchScreen(
                                searchState = searchState,
                                currentPlayingVideoId = playerState.currentTrack?.videoId,
                                downloadedVideoIds = downloadedVideoIds,
                                favoriteVideoIds = favoriteVideoIds,
                                downloadProgressMap = downloadProgressMap,
                                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onSubmitSearch = { viewModel.submitSearchNow(it) },
                                onFilterSelect = { viewModel.onSearchFilterSelected(it) },
                                onOpenMovieAlbum = { album ->
                                    viewModel.openMovieAlbum(album)
                                    navController.navigate(AlbumDetailRoute(album.id))
                                },
                                onTrackClick = { track, queue -> viewModel.playTrack(track, queue) },
                                onDownloadTrack = { viewModel.downloadTrack(it) },
                                onAddToPlaylist = { trackForPlaylistSheet = it },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                        }

                        composable<DownloadsRoute> {
                            DownloadsScreen(
                                downloadedTracks = downloadedTracks,
                                downloadedAlbums = downloadedAlbums,
                                playlistSummaries = playlistSummaries,
                                totalSizeBytes = totalDownloadBytes,
                                currentPlayingVideoId = playerState.currentTrack?.videoId,
                                favoriteVideoIds = favoriteVideoIds,
                                downloadProgressMap = downloadProgressMap,
                                onPlayAllOffline = { tracks -> viewModel.playAll(tracks) },
                                onPlayTrack = { track, queue -> viewModel.playTrack(track, queue) },
                                onDeleteTrack = { viewModel.deleteDownloadedTrack(it) },
                                onDeleteAlbum = { viewModel.deleteDownloadedAlbum(it) },
                                onPlayOfflinePlaylist = { id, name ->
                                    viewModel.playOfflinePlaylist(id, name)
                                },
                                onOpenPlaylist = { id ->
                                    navController.navigate(PlaylistDetailRoute(id))
                                },
                                onAddToPlaylist = { trackForPlaylistSheet = it },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                        }

                        composable<LibraryRoute> {
                            LibraryScreen(
                                playlists = playlistSummaries,
                                onCreatePlaylistClick = { showCreatePlaylistDialog = true },
                                onOpenPlaylist = { id ->
                                    navController.navigate(PlaylistDetailRoute(id))
                                },
                                onDeletePlaylist = { id, name ->
                                    viewModel.deletePlaylist(id, name)
                                }
                            )
                        }

                        composable<AlbumDetailRoute> { entry ->
                            val route = entry.toRoute<AlbumDetailRoute>()
                            LaunchedEffect(route.albumId) {
                                if (albumDetailState.album?.id != route.albumId) {
                                    CuratedCatalog.findFeaturedAlbumById(route.albumId)?.let {
                                        viewModel.openMovieAlbum(it)
                                    }
                                }
                            }
                            AlbumDetailScreen(
                                state = albumDetailState,
                                currentPlayingVideoId = playerState.currentTrack?.videoId,
                                downloadedVideoIds = downloadedVideoIds,
                                favoriteVideoIds = favoriteVideoIds,
                                downloadProgressMap = downloadProgressMap,
                                onBackClick = { navController.popBackStack() },
                                onRefreshClick = {
                                    albumDetailState.album?.let { viewModel.openMovieAlbum(it, forceRefresh = true) }
                                },
                                onPlayFullAlbum = { tracks -> viewModel.playAll(tracks) },
                                onDownloadFullAlbum = { title, tracks ->
                                    viewModel.downloadCollection(title, tracks)
                                },
                                onTrackClick = { track, queue -> viewModel.playTrack(track, queue) },
                                onDownloadTrack = { viewModel.downloadTrack(it) },
                                onAddToPlaylist = { trackForPlaylistSheet = it },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                        }

                        composable<MoodDetailRoute> { entry ->
                            val route = entry.toRoute<MoodDetailRoute>()
                            LaunchedEffect(route.moodId) {
                                if (moodDetailState.mood?.id != route.moodId) {
                                    CuratedCatalog.findMoodById(route.moodId)?.let {
                                        viewModel.openMoodCollection(it)
                                    }
                                }
                            }
                            MoodDetailScreen(
                                state = moodDetailState,
                                currentPlayingVideoId = playerState.currentTrack?.videoId,
                                downloadedVideoIds = downloadedVideoIds,
                                favoriteVideoIds = favoriteVideoIds,
                                downloadProgressMap = downloadProgressMap,
                                onBackClick = { navController.popBackStack() },
                                onRefreshClick = {
                                    moodDetailState.mood?.let { viewModel.openMoodCollection(it, forceRefresh = true) }
                                },
                                onPlayAll = { tracks -> viewModel.playAll(tracks) },
                                onDownloadAll = { title, tracks ->
                                    viewModel.downloadCollection(title, tracks)
                                },
                                onTrackClick = { track, queue -> viewModel.playTrack(track, queue) },
                                onDownloadTrack = { viewModel.downloadTrack(it) },
                                onAddToPlaylist = { trackForPlaylistSheet = it },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                        }

                        composable<PlaylistDetailRoute> { entry ->
                            val route = entry.toRoute<PlaylistDetailRoute>()
                            val playlist by remember(route.playlistId) {
                                viewModel.observePlaylist(route.playlistId)
                            }.collectAsStateWithLifecycle(initialValue = null)
                            val playlistTracks by remember(route.playlistId) {
                                viewModel.observePlaylistTracks(route.playlistId)
                            }.collectAsStateWithLifecycle(initialValue = emptyList())

                            PlaylistDetailScreen(
                                playlist = playlist,
                                tracks = playlistTracks,
                                currentPlayingVideoId = playerState.currentTrack?.videoId,
                                downloadedVideoIds = downloadedVideoIds,
                                favoriteVideoIds = favoriteVideoIds,
                                downloadProgressMap = downloadProgressMap,
                                onBackClick = { navController.popBackStack() },
                                onPlayAll = { tracks -> viewModel.playAll(tracks) },
                                onDownloadAll = { title, tracks ->
                                    viewModel.downloadCollection(title, tracks)
                                },
                                onTrackClick = { track, queue -> viewModel.playTrack(track, queue) },
                                onDownloadTrack = { viewModel.downloadTrack(it) },
                                onAddToPlaylist = { trackForPlaylistSheet = it },
                                onToggleFavorite = { viewModel.toggleFavorite(it) },
                                onRemoveFromPlaylist = { track ->
                                    viewModel.removeTrackFromPlaylist(route.playlistId, track)
                                }
                            )
                        }
                    }

                    // Bottom Overlay: Live Batch Download Banner + Sticky Mini-Player
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        BatchDownloadBanner(
                            state = batchDownloadState,
                            onCancel = { viewModel.cancelBatchDownload() },
                            onDismiss = { viewModel.dismissBatchBanner() }
                        )
                        StickyMiniPlayer(
                            playerState = playerState,
                            onExpandClick = { showNowPlaying = true },
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSkipNext = { viewModel.skipNext() }
                        )
                    }
                }
            }
        }
    }

    // Full-Screen Now Playing Sheet
    val activeTrack = playerState.currentTrack
    if (showNowPlaying && activeTrack != null) {
        FullScreenNowPlayingDialog(
            playerState = playerState,
            isDownloaded = downloadedVideoIds.contains(activeTrack.videoId),
            isFavorite = favoriteVideoIds.contains(activeTrack.videoId),
            downloadProgress = downloadProgressMap[activeTrack.videoId],
            onDismiss = { showNowPlaying = false },
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onSkipNext = { viewModel.skipNext() },
            onSkipPrevious = { viewModel.skipPrevious() },
            onSeekTo = { viewModel.seekTo(it) },
            onToggleShuffle = { viewModel.toggleShuffle() },
            onCycleRepeat = { viewModel.cycleRepeatMode() },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onDownloadTrack = { viewModel.downloadTrack(it) },
            onAddToPlaylist = { trackForPlaylistSheet = it },
            onPlayQueueIndex = { viewModel.playQueueIndex(it) }
        )
    }

    // Add-to-Playlist Sheet
    trackForPlaylistSheet?.let { selectedTrack ->
        AddToPlaylistBottomSheet(
            track = selectedTrack,
            playlists = playlistSummaries,
            onDismiss = { trackForPlaylistSheet = null },
            onSelectPlaylist = { playlist ->
                viewModel.addTrackToPlaylist(playlist.playlistId, playlist.name, selectedTrack)
                trackForPlaylistSheet = null
            },
            onCreateNewPlaylistClick = {
                trackForPlaylistSheet = null
                showCreatePlaylistDialog = true
            }
        )
    }

    // Create Custom Playlist Dialog
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onCreate = { name, desc -> viewModel.createPlaylist(name, desc) }
        )
    }
}
