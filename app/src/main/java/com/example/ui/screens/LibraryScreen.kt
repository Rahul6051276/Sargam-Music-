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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.OfflinePin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PlaylistSummary
import com.example.ui.components.ArtworkImage
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.ErrorCoral
import com.example.ui.theme.MidnightBackground
import com.example.ui.theme.MidnightOutline
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.OnEmeraldTeal
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LibraryScreen(
    playlists: List<PlaylistSummary>,
    onCreatePlaylistClick: () -> Unit,
    onOpenPlaylist: (Long) -> Unit,
    onDeletePlaylist: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("library_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.library_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(R.string.library_empty_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onCreatePlaylistClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldTeal,
                    contentColor = OnEmeraldTeal
                ),
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .height(42.dp)
                    .testTag("library_new_playlist_button")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.library_new_playlist),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 148.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(playlists, key = { it.playlistId }) { playlist ->
                Surface(
                    onClick = { onOpenPlaylist(playlist.playlistId) },
                    color = MidnightSurface,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        1.dp,
                        if (playlist.isFavorites) EmeraldTeal.copy(alpha = 0.45f)
                        else MidnightOutline.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playlist_card_${playlist.playlistId}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            if (playlist.isFavorites) EmeraldTeal.copy(alpha = 0.35f)
                                            else ElectricCyan.copy(alpha = 0.3f),
                                            MidnightBackground
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
                                    cornerRadius = 14.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (playlist.isFavorites) Icons.Rounded.Favorite else Icons.Rounded.LibraryMusic,
                                    contentDescription = null,
                                    tint = if (playlist.isFavorites) EmeraldTeal else ElectricCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = playlist.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (playlist.description.isNotBlank()) {
                                Text(
                                    text = playlist.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${playlist.trackCount} songs",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ElectricCyan
                                )
                                if (playlist.downloadedCount > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.OfflinePin,
                                            contentDescription = null,
                                            tint = EmeraldTeal,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "${playlist.downloadedCount} offline",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = EmeraldTeal
                                        )
                                    }
                                }
                            }
                        }

                        if (!playlist.isFavorites) {
                            IconButton(
                                onClick = { onDeletePlaylist(playlist.playlistId, playlist.name) },
                                modifier = Modifier.testTag("delete_playlist_${playlist.playlistId}")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = stringResource(R.string.action_delete),
                                    tint = ErrorCoral,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = EmeraldTeal
                            )
                        }
                    }
                }
            }
        }
    }
}
