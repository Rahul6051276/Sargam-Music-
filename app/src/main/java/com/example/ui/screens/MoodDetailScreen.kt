package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.Track
import com.example.data.model.TrackDownloadProgress
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightOutline
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.OnEmeraldTeal
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MoodDetailUiState

@Composable
fun MoodDetailScreen(
    state: MoodDetailUiState,
    currentPlayingVideoId: String?,
    downloadedVideoIds: Set<String>,
    favoriteVideoIds: Set<String>,
    downloadProgressMap: Map<String, TrackDownloadProgress>,
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onPlayAll: (List<Track>) -> Unit,
    onDownloadAll: (String, List<Track>) -> Unit,
    onTrackClick: (Track, List<Track>) -> Unit,
    onDownloadTrack: (Track) -> Unit,
    onAddToPlaylist: (Track) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val mood = state.mood
    val gradientStart = Color(mood?.gradientStartHex ?: 0xFF007A5E)
    val gradientEnd = Color(mood?.gradientEndHex ?: 0xFF0B273A)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("mood_detail_screen"),
        contentPadding = PaddingValues(bottom = 148.dp)
    ) {
        item(key = "mood_header") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(gradientStart, gradientEnd, MidnightBackground)
                        )
                    )
                    .padding(bottom = 18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("mood_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                                tint = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = onRefreshClick,
                            modifier = Modifier.testTag("mood_refresh_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = stringResource(R.string.action_refresh),
                                tint = TextPrimary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Surface(
                            color = MidnightBackground.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.45f)),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = moodIconFor(mood?.id.orEmpty()),
                                    contentDescription = null,
                                    tint = EmeraldTeal,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MidnightBackground.copy(alpha = 0.5f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = mood?.badge ?: "CURATED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = mood?.title ?: "Mood Mix",
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = mood?.subtitle.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onPlayAll(state.tracks) },
                            enabled = state.tracks.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldTeal,
                                contentColor = OnEmeraldTeal
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("mood_play_all_button")
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_play_all), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                onDownloadAll(mood?.title ?: "Mood Mix", state.tracks)
                            },
                            enabled = state.tracks.isNotEmpty(),
                            border = BorderStroke(1.2.dp, ElectricCyan),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("mood_download_all_button")
                        ) {
                            Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_download_all), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        when {
            state.isLoading -> {
                item(key = "mood_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = EmeraldTeal)
                            Text(
                                text = "Curating fresh tracks for ${mood?.title ?: "this vibe"}…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
            state.tracks.isEmpty() -> {
                item(key = "mood_empty") {
                    Surface(
                        color = MidnightSurface,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MidnightOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = state.errorMessage ?: "No tracks loaded.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                            OutlinedButton(
                                onClick = onRefreshClick,
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
                itemsIndexed(state.tracks, key = { idx, t -> "${t.videoId}_$idx" }) { index, track ->
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                        TrackRowItem(
                            track = track,
                            trackNumber = index + 1,
                            isCurrentTrack = track.videoId == currentPlayingVideoId,
                            isDownloaded = downloadedVideoIds.contains(track.videoId),
                            isFavorite = favoriteVideoIds.contains(track.videoId),
                            downloadProgress = downloadProgressMap[track.videoId],
                            onPlayClick = { onTrackClick(track, state.tracks) },
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
