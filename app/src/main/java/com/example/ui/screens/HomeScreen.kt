package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ArtistRadio
import com.example.data.model.HomeFilterChip
import com.example.data.model.MoodCollection
import com.example.data.model.MovieAlbum
import com.example.data.model.Track
import com.example.data.model.TrackDownloadProgress
import com.example.ui.components.ArtworkImage
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightOutline
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.OnEmeraldTeal
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    homeState: HomeUiState,
    currentPlayingVideoId: String?,
    downloadedVideoIds: Set<String>,
    favoriteVideoIds: Set<String>,
    downloadProgressMap: Map<String, TrackDownloadProgress>,
    onFilterSelected: (HomeFilterChip) -> Unit,
    onSearchShortcutClick: () -> Unit,
    onPlayTrendingClick: () -> Unit,
    onMoodClick: (MoodCollection) -> Unit,
    onArtistRadioClick: (ArtistRadio) -> Unit,
    onMovieAlbumClick: (MovieAlbum) -> Unit,
    onTrackClick: (Track, List<Track>) -> Unit,
    onPlayAllFresh: (List<Track>) -> Unit,
    onDownloadAllFresh: (String, List<Track>) -> Unit,
    onDownloadTrack: (Track) -> Unit,
    onAddToPlaylist: (Track) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onRefreshHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleGoldenRetro = homeState.visibleGoldenRetro
    val visibleTrending = homeState.visibleTrending
    val visibleMoods = homeState.visibleMoods
    val visibleArtists = homeState.visibleArtistRadios
    val visibleAlbums = homeState.visibleMovieAlbums

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 148.dp)
    ) {
        // Brand Header
        item(key = "home_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary
                        )
                        Surface(
                            color = EmeraldTeal.copy(alpha = 0.14f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = null,
                                    tint = EmeraldTeal,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "AD-FREE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldTeal
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(R.string.tagline_ad_free),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Surface(
                    onClick = onSearchShortcutClick,
                    color = MidnightSurface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, MidnightOutline),
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("home_search_shortcut")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = stringResource(R.string.nav_search),
                            tint = ElectricCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Interactive Language & Era Filter Chips
        item(key = "home_filter_chips") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .testTag("home_filter_chips_row")
            ) {
                items(HomeFilterChip.entries, key = { it.name }) { chip ->
                    val selected = homeState.selectedFilter == chip
                    FilterChip(
                        selected = selected,
                        onClick = { onFilterSelected(chip) },
                        label = {
                            Text(
                                text = chip.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MidnightSurface,
                            labelColor = TextSecondary,
                            selectedContainerColor = EmeraldTeal,
                            selectedLabelColor = OnEmeraldTeal
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = MidnightOutline,
                            selectedBorderColor = EmeraldTeal
                        ),
                        shape = CircleShape,
                        modifier = Modifier.testTag("home_filter_${chip.name.lowercase()}")
                    )
                }
            }
        }

        // Hero Banner Card (shown on All filter for clean editorial focus)
        if (homeState.selectedFilter == HomeFilterChip.ALL) {
            item(key = "home_hero_banner") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .height(188.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(onClick = onPlayTrendingClick)
                        .testTag("home_hero_banner")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MidnightBackground.copy(alpha = 0.92f),
                                        MidnightSurface.copy(alpha = 0.70f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = ElectricCyan.copy(alpha = 0.18f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = stringResource(R.string.hero_badge),
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = stringResource(R.string.hero_title),
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                                maxLines = 2
                            )
                            Text(
                                text = stringResource(R.string.hero_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }

                        Button(
                            onClick = onPlayTrendingClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldTeal,
                                contentColor = OnEmeraldTeal
                            ),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("hero_play_trending_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.hero_cta),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 1. Dedicated Golden Era & Old Classics Hub
        if (visibleGoldenRetro.isNotEmpty()) {
            item(key = "section_golden_retro_header") {
                SectionHeader(
                    title = "Golden Era & Old Classics",
                    subtitle = "Original vintage melodies · Zero remixes or jukeboxes",
                    badgeText = "EVERGREEN",
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                )
            }

            item(key = "golden_retro_carousel") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.testTag("golden_retro_carousel")
                ) {
                    items(visibleGoldenRetro, key = { it.id }) { collection ->
                        EditorialCollectionCard(
                            collection = collection,
                            onClick = { onMoodClick(collection) }
                        )
                    }
                }
            }
        }

        // 2. Legendary & Modern Artist Radio Stations (Circular Artist Avatars)
        if (visibleArtists.isNotEmpty()) {
            item(key = "section_artist_radios_header") {
                SectionHeader(
                    title = "Artist Radio Stations",
                    subtitle = "Tap any legend or star for a fresh 40-track radio mix",
                    badgeText = "40-TRACK MIX",
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                )
            }

            item(key = "artist_radios_carousel") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.testTag("artist_radios_carousel")
                ) {
                    items(visibleArtists, key = { it.id }) { artist ->
                        ArtistRadioCircleCard(
                            artist = artist,
                            onClick = { onArtistRadioClick(artist) }
                        )
                    }
                }
            }
        }

        // 3. Editor's Picks: Trending & Fresh Shelf
        if (visibleTrending.isNotEmpty()) {
            item(key = "section_trending_header") {
                SectionHeader(
                    title = "Editor's Picks · Trending & Fresh",
                    subtitle = "Daily rotated YouTube Music playlists & chartbusters",
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
                )
            }

            item(key = "trending_carousel") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.testTag("trending_fresh_carousel")
                ) {
                    items(visibleTrending, key = { it.id }) { collection ->
                        EditorialCollectionCard(
                            collection = collection,
                            onClick = { onMoodClick(collection) }
                        )
                    }
                }
            }
        }

        // 4. Featured Movie Albums & Soundtracks Carousel
        if (visibleAlbums.isNotEmpty()) {
            item(key = "section_movie_albums_title") {
                SectionHeader(
                    title = stringResource(R.string.section_movie_albums),
                    subtitle = "Full Bollywood soundtracks · Play & download offline",
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
                )
            }

            item(key = "movie_albums_carousel") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.testTag("featured_albums_carousel")
                ) {
                    items(visibleAlbums, key = { it.id }) { album ->
                        MovieAlbumSquareCard(
                            album = album,
                            onClick = { onMovieAlbumClick(album) }
                        )
                    }
                }
            }
        }

        // 5. Mood & Activity Shelf
        if (visibleMoods.isNotEmpty()) {
            item(key = "section_moods_title") {
                SectionHeader(
                    title = "Mood & Activity",
                    subtitle = "Curated soundscapes for late nights, drives & workouts",
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
                )
            }

            item(key = "moods_carousel") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.testTag("moods_carousel")
                ) {
                    items(visibleMoods, key = { it.id }) { mood ->
                        MoodVibeCard(
                            mood = mood,
                            onClick = { onMoodClick(mood) }
                        )
                    }
                }
            }
        }

        // 6. Dynamic Fresh Picks Section (Synced with active filter chip + Rotate/Refresh)
        item(key = "section_fresh_picks_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = homeState.freshPicksTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tap refresh to rotate fresh tracks & playlists",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = onRefreshHome,
                    modifier = Modifier.testTag("home_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.action_refresh),
                        tint = ElectricCyan
                    )
                }
            }
        }

        if (homeState.freshPicks.isNotEmpty()) {
            item(key = "fresh_picks_actions") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onPlayAllFresh(homeState.freshPicks) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldTeal,
                            contentColor = OnEmeraldTeal
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("fresh_play_all_button")
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.action_play_all), fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onDownloadAllFresh(homeState.freshPicksTitle, homeState.freshPicks) },
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.65f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("fresh_download_all_button")
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.action_download_all), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        when {
            homeState.isLoadingFreshPicks -> {
                item(key = "fresh_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(color = EmeraldTeal)
                            Text(
                                text = "Curating ${homeState.freshPicksTitle}…",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
            homeState.freshPicks.isEmpty() -> {
                item(key = "fresh_empty") {
                    Surface(
                        color = MidnightSurface,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MidnightOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = homeState.errorMessage ?: "Unable to fetch online feed.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            OutlinedButton(
                                onClick = onRefreshHome,
                                border = BorderStroke(1.dp, EmeraldTeal),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldTeal)
                            ) {
                                Text(stringResource(R.string.action_retry))
                            }
                        }
                    }
                }
            }
            else -> {
                items(homeState.freshPicks.take(15), key = { it.videoId }) { track ->
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                        TrackRowItem(
                            track = track,
                            isCurrentTrack = track.videoId == currentPlayingVideoId,
                            isDownloaded = downloadedVideoIds.contains(track.videoId),
                            isFavorite = favoriteVideoIds.contains(track.videoId),
                            downloadProgress = downloadProgressMap[track.videoId],
                            onPlayClick = { onTrackClick(track, homeState.freshPicks) },
                            onDownloadClick = { onDownloadTrack(track) },
                            onAddToPlaylistClick = { onAddToPlaylist(track) },
                            onToggleFavoriteClick = { onToggleFavorite(track) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            if (!badgeText.isNullOrBlank()) {
                Surface(
                    color = ElectricCyan.copy(alpha = 0.14f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun EditorialCollectionCard(
    collection: MoodCollection,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(172.dp)
            .testTag("collection_card_${collection.id}")
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(142.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(collection.gradientStartHex),
                            Color(collection.gradientEndHex)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            if (!collection.artworkUrl.isNullOrBlank()) {
                ArtworkImage(
                    url = collection.artworkUrl,
                    contentDescription = collection.title,
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 20.dp
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(collection.gradientStartHex).copy(alpha = 0.35f),
                                    MidnightBackground.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
            } else {
                Icon(
                    imageVector = moodIconFor(collection.id),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.22f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )
            }

            Surface(
                color = MidnightBackground.copy(alpha = 0.72f),
                shape = CircleShape,
                border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.45f)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = collection.badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldTeal,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(EmeraldTeal),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = OnEmeraldTeal,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = collection.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = collection.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ArtistRadioCircleCard(
    artist: ArtistRadio,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(102.dp)
            .testTag("artist_radio_${artist.id}")
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(artist.accentStartHex),
                            Color(artist.accentEndHex)
                        )
                    )
                )
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MidnightSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                if (!artist.avatarUrl.isNullOrBlank()) {
                    ArtworkImage(
                        url = artist.avatarUrl,
                        contentDescription = artist.name,
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 46.dp
                    )
                } else {
                    Text(
                        text = artist.name.split(" ")
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            .joinToString(""),
                        style = MaterialTheme.typography.headlineSmall,
                        color = EmeraldTeal
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(MidnightBackground.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Radio,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = artist.name,
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = artist.eraLabel,
            style = MaterialTheme.typography.labelSmall,
            color = if (artist.isClassicLegend) ElectricCyan else EmeraldTeal,
            maxLines = 1
        )
    }
}

@Composable
fun MovieAlbumSquareCard(
    album: MovieAlbum,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(152.dp)
            .testTag("movie_album_card_${album.id}")
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(album.gradientStartHex),
                            Color(album.gradientEndHex)
                        )
                    )
                )
        ) {
            if (!album.artworkUrl.isNullOrBlank()) {
                ArtworkImage(
                    url = album.artworkUrl,
                    contentDescription = album.title,
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 18.dp
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    MidnightBackground.copy(alpha = 0.72f)
                                )
                            )
                        )
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Album,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.24f),
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.Center)
                )
            }

            if (album.year.isNotBlank()) {
                Surface(
                    color = MidnightBackground.copy(alpha = 0.78f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = album.year,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(EmeraldTeal),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = OnEmeraldTeal,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = album.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = album.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MoodVibeCard(
    mood: MoodCollection,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        modifier = Modifier
            .width(176.dp)
            .height(126.dp)
            .testTag("mood_card_${mood.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(mood.gradientStartHex),
                            Color(mood.gradientEndHex)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Icon(
                imageVector = moodIconFor(mood.id),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.16f),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.BottomEnd)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = MidnightBackground.copy(alpha = 0.45f),
                    shape = CircleShape
                ) {
                    Text(
                        text = mood.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = mood.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mood.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary.copy(alpha = 0.78f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

fun moodIconFor(moodId: String): ImageVector = when {
    moodId.contains("viral") || moodId.contains("new") -> Icons.Rounded.AutoAwesome
    moodId.contains("top_50") || moodId.contains("punjabi") -> Icons.Rounded.LocalFireDepartment
    moodId.contains("english") -> Icons.Rounded.Public
    moodId.contains("romantic") || moodId.contains("love") -> Icons.Rounded.Favorite
    moodId.contains("lofi") || moodId.contains("sad") || moodId.contains("heartbreak") || moodId.contains("melancholy") ->
        Icons.Rounded.Nightlight
    moodId.contains("drive") || moodId.contains("roadtrip") -> Icons.Rounded.DirectionsCar
    moodId.contains("workout") || moodId.contains("gym") -> Icons.Rounded.FitnessCenter
    moodId.contains("party") -> Icons.Rounded.Celebration
    moodId.contains("ghazal") || moodId.contains("sufi") -> Icons.Rounded.LibraryMusic
    moodId.contains("retro") || moodId.contains("70s") || moodId.contains("90s") -> Icons.Rounded.Album
    else -> Icons.Rounded.MusicNote
}
