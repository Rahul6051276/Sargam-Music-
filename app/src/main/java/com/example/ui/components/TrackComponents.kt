package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.OfflinePin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.data.local.PlaylistSummary
import com.example.data.model.BatchDownloadState
import com.example.data.model.DownloadStatus
import com.example.data.model.Track
import com.example.data.model.TrackDownloadProgress
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightOutline
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MidnightSurfaceElevated
import com.example.ui.theme.MidnightSurfaceHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ArtworkImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    val shape = RoundedCornerShape(cornerRadius)
    if (url.isNullOrBlank()) {
        Image(
            painter = painterResource(id = R.drawable.img_album_placeholder),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(shape)
        )
    } else {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(shape),
            loading = {
                Image(
                    painter = painterResource(id = R.drawable.img_album_placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            },
            error = {
                Image(
                    painter = painterResource(id = R.drawable.img_album_placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
    }
}

@Composable
fun TrackRowItem(
    track: Track,
    isCurrentTrack: Boolean,
    isDownloaded: Boolean,
    isFavorite: Boolean,
    downloadProgress: TrackDownloadProgress?,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
    onToggleFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    trackNumber: Int? = null,
    onRemoveClick: (() -> Unit)? = null,
    removeLabel: String = "Remove"
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val rowBg = if (isCurrentTrack) {
        MidnightSurfaceElevated
    } else {
        Color.Transparent
    }

    Surface(
        color = rowBg,
        shape = RoundedCornerShape(14.dp),
        border = if (isCurrentTrack) BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.45f)) else null,
        modifier = modifier
            .fillMaxWidth()
            .testTag("track_row_${track.videoId}")
            .clickable(onClick = onPlayClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (trackNumber != null) {
                Box(
                    modifier = Modifier.width(26.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isCurrentTrack) {
                        Icon(
                            imageVector = Icons.Rounded.GraphicEq,
                            contentDescription = null,
                            tint = EmeraldTeal,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "%02d".format(trackNumber),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted
                        )
                    }
                }
            }

            Box(modifier = Modifier.size(54.dp)) {
                ArtworkImage(
                    url = track.thumbnailUrl,
                    contentDescription = stringResource(R.string.cd_album_art),
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 11.dp
                )
                if (isCurrentTrack && trackNumber == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(11.dp))
                            .background(MidnightBackground.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.GraphicEq,
                            contentDescription = null,
                            tint = EmeraldTeal,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCurrentTrack) EmeraldTeal else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isDownloaded) {
                        Icon(
                            imageVector = Icons.Rounded.OfflinePin,
                            contentDescription = stringResource(R.string.cd_downloaded),
                            tint = EmeraldTeal,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = buildString {
                            append(track.artist)
                            if (track.durationSeconds > 0L) {
                                append(" · ")
                                append(track.formattedDuration)
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // One-tap Download / Live Progress Indicator
            Box(
                modifier = Modifier.minimumInteractiveComponentSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isDownloaded -> {
                        Icon(
                            imageVector = Icons.Rounded.DownloadDone,
                            contentDescription = stringResource(R.string.cd_downloaded),
                            tint = EmeraldTeal,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    downloadProgress?.status == DownloadStatus.DOWNLOADING -> {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { downloadProgress.progress.coerceIn(0.05f, 1f) },
                                modifier = Modifier.size(24.dp),
                                color = ElectricCyan,
                                trackColor = MidnightSurfaceHigh,
                                strokeWidth = 2.6.dp,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                    downloadProgress?.status == DownloadStatus.QUEUED -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TextSecondary,
                            strokeWidth = 2.dp
                        )
                    }
                    else -> {
                        IconButton(
                            onClick = onDownloadClick,
                            modifier = Modifier.testTag("download_track_${track.videoId}")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = stringResource(R.string.cd_download),
                                tint = ElectricCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("track_more_${track.videoId}")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Track options",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = MidnightSurfaceElevated
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                                color = TextPrimary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) EmeraldTeal else TextSecondary
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleFavoriteClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Playlist", color = TextPrimary) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onAddToPlaylistClick()
                        }
                    )
                    if (!isDownloaded) {
                        DropdownMenuItem(
                            text = { Text("Download Offline", color = TextPrimary) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Download,
                                    contentDescription = null,
                                    tint = EmeraldTeal
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDownloadClick()
                            }
                        )
                    }
                    if (onRemoveClick != null) {
                        DropdownMenuItem(
                            text = { Text(removeLabel, color = ErrorCoral) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = null,
                                    tint = ErrorCoral
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onRemoveClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BatchDownloadBanner(
    state: BatchDownloadState?,
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = state != null, modifier = modifier) {
        state?.let { batch ->
            Surface(
                color = MidnightSurfaceElevated,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("batch_download_banner")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (batch.isActive) Icons.Rounded.Download else Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = if (batch.isActive) ElectricCyan else EmeraldTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (batch.isActive) {
                                    "Downloading ${batch.collectionTitle} (${batch.completedCount}/${batch.totalCount})"
                                } else {
                                    "Downloaded ${batch.completedCount}/${batch.totalCount} · ${batch.collectionTitle}"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = if (batch.isActive) onCancel else onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.action_cancel),
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (batch.isActive) {
                        Text(
                            text = batch.currentTrackTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    LinearProgressIndicator(
                        progress = { batch.overallProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = if (batch.isActive) ElectricCyan else EmeraldTeal,
                        trackColor = MidnightSurfaceHigh,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
    track: Track,
    playlists: List<PlaylistSummary>,
    onDismiss: () -> Unit,
    onSelectPlaylist: (PlaylistSummary) -> Unit,
    onCreateNewPlaylistClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MidnightSurface,
        contentColor = TextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = stringResource(R.string.add_to_playlist_title),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            Surface(
                onClick = onCreateNewPlaylistClick,
                color = MidnightSurfaceElevated,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, EmeraldTeal.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sheet_create_playlist_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmeraldTeal.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = EmeraldTeal
                        )
                    }
                    Text(
                        text = "Create New Playlist",
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(playlists, key = { it.playlistId }) { playlist ->
                    Surface(
                        onClick = { onSelectPlaylist(playlist) },
                        color = MidnightSurfaceElevated,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("select_playlist_${playlist.playlistId}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                EmeraldTeal.copy(alpha = 0.35f),
                                                ElectricCyan.copy(alpha = 0.35f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (playlist.isFavorites) Icons.Rounded.Favorite else Icons.Rounded.LibraryMusic,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playlist.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${playlist.trackCount} songs · ${playlist.downloadedCount} offline",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightSurface,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.playlist_dialog_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.playlist_name_hint), color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldTeal,
                        unfocusedBorderColor = MidnightOutline,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = EmeraldTeal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playlist_name_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.playlist_desc_hint), color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = MidnightOutline,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ElectricCyan
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playlist_desc_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name.trim(), description.trim())
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldTeal,
                    contentColor = MidnightBackground
                ),
                modifier = Modifier.testTag("confirm_create_playlist_button")
            ) {
                Text(
                    text = stringResource(R.string.action_create),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel), color = TextSecondary)
            }
        }
    )
}

fun formatMillis(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSeconds = ms / 1000L
    val mins = totalSeconds / 60L
    val secs = totalSeconds % 60L
    return "%d:%02d".format(mins, secs)
}

fun formatMegabytes(bytes: Long): String {
    if (bytes <= 0L) return "0 MB"
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb < 1.0) "%.1f MB".format(mb) else "%.1f MB".format(mb)
}
