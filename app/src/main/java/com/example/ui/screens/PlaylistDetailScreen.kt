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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.local.PlaylistEntity
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

@Composable
fun PlaylistDetailScreen(
    playlist: PlaylistEntity?,
    tracks: List<Track>,
    currentPlayingVideoId: String?,
    downloadedVideoIds: Set<String>,
    favoriteVideoIds: Set<String>,
    downloadProgressMap: Map<String, TrackDownloadProgress>,
    onBackClick: () -> Unit,
    onPlayAll: (List<Track>) -> Unit,
    onDownloadAll: (String, List<Track>) -> Unit,
    onTrackClick: (Track, List<Track>) -> Unit,
    onDownloadTrack: (Track) -> Unit,
    onAddToPlaylist: (Track) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    onRemoveFromPlaylist: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val playlistName = playlist?.name ?: "Playlist"
    val offlineCount = tracks.count { downloadedVideoIds.contains(it.videoId) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("playlist_detail_screen"),
        contentPadding = PaddingValues(bottom = 148.dp)
    ) {
        item(key = "playlist_detail_header") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0B3B49),
                                MidnightSurface,
                                MidnightBackground
                            )
                        )
                    )
                    .padding(bottom = 18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .testTag("playlist_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = TextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            color = MidnightBackground.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.45f)),
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (playlist?.isFavorites == true) Icons.Rounded.Favorite else Icons.Rounded.LibraryMusic,
                                    contentDescription = null,
                                    tint = EmeraldTeal,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playlistName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary
                            )
                            if (!playlist?.description.isNullOrBlank()) {
                                Text(
                                    text = playlist!!.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "${tracks.size} songs · $offlineCount offline",
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricCyan,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onPlayAll(tracks) },
                            enabled = tracks.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldTeal,
                                contentColor = OnEmeraldTeal
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("playlist_play_all_button")
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_play_all), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onDownloadAll(playlistName, tracks) },
                            enabled = tracks.isNotEmpty(),
                            border = BorderStroke(1.2.dp, ElectricCyan),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("playlist_download_all_button")
                        ) {
                            Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_download_all), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        if (tracks.isEmpty()) {
            item(key = "playlist_empty") {
                Surface(
                    color = MidnightSurface,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MidnightOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "No songs in \"$playlistName\" yet. Tap the menu icon on any song across Home, Search, or Movie Albums to add it here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        } else {
            itemsIndexed(tracks, key = { idx, t -> "${t.videoId}_$idx" }) { index, track ->
                Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                    TrackRowItem(
                        track = track,
                        trackNumber = index + 1,
                        isCurrentTrack = track.videoId == currentPlayingVideoId,
                        isDownloaded = downloadedVideoIds.contains(track.videoId),
                        isFavorite = favoriteVideoIds.contains(track.videoId),
                        downloadProgress = downloadProgressMap[track.videoId],
                        onPlayClick = { onTrackClick(track, tracks) },
                        onDownloadClick = { onDownloadTrack(track) },
                        onAddToPlaylistClick = { onAddToPlaylist(track) },
                        onToggleFavoriteClick = { onToggleFavorite(track) },
                        onRemoveClick = { onRemoveFromPlaylist(track) },
                        removeLabel = "Remove from Playlist"
                    )
                }
            }
        }
    }
}
