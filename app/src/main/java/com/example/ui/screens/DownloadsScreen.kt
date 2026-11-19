package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.OfflinePin
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PlaylistSummary
import com.example.data.model.DownloadedAlbumGroup
import com.example.data.model.Track
import com.example.data.model.TrackDownloadProgress
import com.example.ui.components.ArtworkImage
import com.example.ui.components.TrackRowItem
import com.example.ui.components.formatMegabytes
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightOutline
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.OnEmeraldTeal
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DownloadsScreen(
    downloadedTracks: List<Track>,
    downloadedAlbums: List<DownloadedAlbumGroup>,
    playlistSummaries: List<PlaylistSummary>,
    totalSizeBytes: Long,
    currentPlayingVideoId: String?,
    favoriteVideoIds: Set<String>,
    downloadProgressMap: Map<String, TrackDownloadProgress>,
    onPlayAllOffline: (List<Track>) -> Unit,
    onPlayTrack: (Track, List<Track>) -> Unit,
    onDeleteTrack: (Track) -> Unit,
    onDeleteAlbum: (String) -> Unit,
    onPlayOfflinePlaylist: (Long, String) -> Unit,
    onOpenPlaylist: (Long) -> Unit,
    onAddToPlaylist: (Track) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.downloads_tab_songs),
        stringResource(R.string.downloads_tab_albums),
        stringResource(R.string.downloads_tab_playlists)
    )
    val offlinePlaylists = playlistSummaries.filter { it.downloadedCount > 0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("downloads_screen")
    ) {
        // Offline Vault Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.downloads_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.OfflinePin,
                            contentDescription = null,
                            tint = EmeraldTeal,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "${downloadedTracks.size} offline songs",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldTeal
                        )
                        Icon(
                            imageVector = Icons.Rounded.Storage,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = formatMegabytes(totalSizeBytes),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                if (downloadedTracks.isNotEmpty()) {
                    Button(
                        onClick = { onPlayAllOffline(downloadedTracks) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldTeal,
                            contentColor = OnEmeraldTeal
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("downloads_play_all_button")
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Play Offline",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Sub-tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { idx, label ->
                    val selected = selectedTab == idx
                    FilterChip(
                        selected = selected,
                        onClick = { selectedTab = idx },
                        label = {
                            Text(
                                text = label,
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
                        modifier = Modifier.testTag("downloads_tab_$idx")
                    )
                }
            }
        }

        if (downloadedTracks.isEmpty()) {
            OfflineEmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 148.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedTab) {
                    0 -> {
                        items(downloadedTracks, key = { it.videoId }) { track ->
                            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                                TrackRowItem(
                                    track = track,
                                    isCurrentTrack = track.videoId == currentPlayingVideoId,
                                    isDownloaded = true,
                                    isFavorite = favoriteVideoIds.contains(track.videoId),
                                    downloadProgress = downloadProgressMap[track.videoId],
                                    onPlayClick = { onPlayTrack(track, downloadedTracks) },
                                    onDownloadClick = {},
                                    onAddToPlaylistClick = { onAddToPlaylist(track) },
                                    onToggleFavoriteClick = { onToggleFavorite(track) },
                                    onRemoveClick = { onDeleteTrack(track) },
                                    removeLabel = "Delete Download"
                                )
                            }
                        }
                    }

                    1 -> {
                        if (downloadedAlbums.isEmpty()) {
                            item(key = "no_albums_offline") {
                                Text(
                                    text = "Download songs from any Movie Album or Mood Collection to see grouped offline albums here.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp)
                                )
                            }
                        } else {
                            items(downloadedAlbums, key = { it.albumName }) { group ->
                                Surface(
                                    onClick = { onPlayAllOffline(group.tracks) },
                                    color = MidnightSurface,
                                    shape = RoundedCornerShape(18.dp),
                                    border = BorderStroke(1.dp, MidnightOutline.copy(alpha = 0.7f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 4.dp)
                                        .testTag("offline_album_${group.albumName}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ArtworkImage(
                                            url = group.artworkUrl,
                                            contentDescription = group.albumName,
                                            modifier = Modifier.size(64.dp),
                                            cornerRadius = 12.dp
                                        )
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Album,
                                                    contentDescription = null,
                                                    tint = ElectricCyan,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = group.albumName,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = TextPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Text(
                                                text = "${group.tracks.size} offline songs ready",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = EmeraldTeal,
                                                modifier = Modifier.padding(top = 3.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { onPlayAllOffline(group.tracks) },
                                            modifier = Modifier.testTag("play_offline_album_${group.albumName}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.PlayArrow,
                                                contentDescription = stringResource(R.string.cd_play),
                                                tint = EmeraldTeal
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteAlbum(group.albumName) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.DeleteOutline,
                                                contentDescription = stringResource(R.string.action_delete),
                                                tint = ErrorCoral
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        if (offlinePlaylists.isEmpty()) {
                            item(key = "no_playlists_offline") {
                                Text(
                                    text = "Add downloaded songs to your playlists or tap Download All inside any playlist to play playlists offline.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp)
                                )
                            }
                        } else {
                            items(offlinePlaylists, key = { it.playlistId }) { playlist ->
                                Surface(
                                    onClick = { onOpenPlaylist(playlist.playlistId) },
                                    color = MidnightSurface,
                                    shape = RoundedCornerShape(18.dp),
                                    border = BorderStroke(1.dp, MidnightOutline.copy(alpha = 0.7f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 4.dp)
                                        .testTag("offline_playlist_${playlist.playlistId}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(58.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(
                                                            EmeraldTeal.copy(alpha = 0.3f),
                                                            ElectricCyan.copy(alpha = 0.3f)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (!playlist.firstThumbnailUrl.isNullOrBlank()) {
                                                ArtworkImage(
                                                    url = playlist.firstThumbnailUrl,
                                                    contentDescription = playlist.name,
                                                    modifier = Modifier.fillMaxSize(),
                                                    cornerRadius = 12.dp
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = if (playlist.isFavorites) Icons.Rounded.Favorite else Icons.Rounded.LibraryMusic,
                                                    contentDescription = null,
                                                    tint = EmeraldTeal,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = playlist.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${playlist.downloadedCount} of ${playlist.trackCount} songs offline",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = EmeraldTeal,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { onPlayOfflinePlaylist(playlist.playlistId, playlist.name) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.PlayArrow,
                                                contentDescription = stringResource(R.string.cd_play),
                                                tint = EmeraldTeal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OfflineEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MidnightSurfaceElevated,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(190.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_offline_vault),
                contentDescription = stringResource(R.string.downloads_empty_title),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = stringResource(R.string.downloads_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.downloads_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}
