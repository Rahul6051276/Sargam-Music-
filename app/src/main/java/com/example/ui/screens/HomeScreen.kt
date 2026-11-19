package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
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
    onSearchShortcutClick: () -> Unit,
    onPlayTrendingClick: () -> Unit,
    onMoodClick: (MoodCollection) -> Unit,
    onMovieAlbumClick: (MovieAlbum) -> Unit,
    onTrackClick: (Track, List<Track>) -> Unit,
    onPlayAllFresh: (List<Track>) -> Unit,
    onDownloadAllFresh: (List<Track>) -> Unit,
    onDownloadTrack: (Track) -> Unit,
    onAddToPlaylist: (Track) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onRefreshHome: () -> Unit,
    modifier: Modifier = Modifier
) {
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

        // Hero Banner Card
        item(key = "home_hero_banner") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .height(192.dp)
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

        // Featured Movie Albums & Soundtracks Carousel
        item(key = "section_movie_albums_title") {
            SectionHeader(
                title = stringResource(R.string.section_movie_albums),
                subtitle = "Full Bollywood soundtracks · Play & download offline",
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )
        }

        item(key = "movie_albums_carousel") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.testTag("featured_albums_carousel")
            ) {
                items(homeState.featuredAlbums, key = { it.id }) { album ->
                    MovieAlbumSquareCard(
                        album = album,
                        onClick = { onMovieAlbumClick(album) }
                    )
                }
            }
        }

        // Auto-Curated Moods & Vibes
        item(key = "section_moods_title") {
            SectionHeader(
                title = stringResource(R.string.section_moods),
                subtitle = "Auto-curated fresh Hindi, Bollywood & Punjabi vibes",
                modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
            )
        }

        item(key = "moods_carousel") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.testTag("moods_carousel")
            ) {
                items(homeState.moods, key = { it.id }) { mood ->
                    MoodVibeCard(
                        mood = mood,
                        onClick = { onMoodClick(mood) }
                    )
                }
            }
        }

        // Fresh Picks Section
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
                        text = stringResource(R.string.section_fresh_picks),
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "Latest Hindi & Bollywood audio releases",
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
                        onClick = { onDownloadAllFresh(homeState.freshPicks) },
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
                                text = "Fetching fresh Bollywood tracks…",
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
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

fun moodIconFor(moodId: String): ImageVector = when (moodId) {
    "new_releases" -> Icons.Rounded.AutoAwesome
    "trending_top_50" -> Icons.Rounded.LocalFireDepartment
    "romantic_love" -> Icons.Rounded.Favorite
    "sad_heartbreak" -> Icons.Rounded.Nightlight
    "travel_long_drive" -> Icons.Rounded.DirectionsCar
    "gym_workout" -> Icons.Rounded.FitnessCenter
    "evergreen_90s" -> Icons.Rounded.Album
    else -> Icons.Rounded.MusicNote
}
