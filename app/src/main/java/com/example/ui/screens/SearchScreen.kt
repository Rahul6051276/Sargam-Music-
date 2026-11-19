package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CuratedCatalog
import com.example.data.model.MovieAlbum
import com.example.data.model.SearchFilter
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SearchUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    searchState: SearchUiState,
    currentPlayingVideoId: String?,
    downloadedVideoIds: Set<String>,
    favoriteVideoIds: Set<String>,
    downloadProgressMap: Map<String, TrackDownloadProgress>,
    onQueryChange: (String) -> Unit,
    onSubmitSearch: (String) -> Unit,
    onFilterSelect: (SearchFilter) -> Unit,
    onOpenMovieAlbum: (MovieAlbum) -> Unit,
    onTrackClick: (Track, List<Track>) -> Unit,
    onDownloadTrack: (Track) -> Unit,
    onAddToPlaylist: (Track) -> Unit,
    onToggleFavorite: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBackground)
            .testTag("search_screen")
    ) {
        // Header + Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Instant Music Search",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )

            OutlinedTextField(
                value = searchState.query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = EmeraldTeal
                    )
                },
                trailingIcon = {
                    if (searchState.query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.cd_clear_search),
                                tint = TextSecondary
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        onSubmitSearch(searchState.query)
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MidnightSurface,
                    unfocusedContainerColor = MidnightSurface,
                    focusedBorderColor = EmeraldTeal,
                    unfocusedBorderColor = MidnightOutline,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = EmeraldTeal
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field")
            )

            // Filter Tabs: All | Songs | Movie Albums
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SearchFilter.entries.forEach { filter ->
                    val selected = searchState.filter == filter
                    FilterChip(
                        selected = selected,
                        onClick = { onFilterSelect(filter) },
                        label = {
                            Text(
                                text = filter.label,
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
                        modifier = Modifier.testTag("search_filter_${filter.name.lowercase()}")
                    )
                }
            }
        }

        // Body Content
        when {
            searchState.isSearching -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = EmeraldTeal)
                        Text(
                            text = "Searching songs & full movie albums…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            !searchState.hasSearched && searchState.query.isBlank() -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 148.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item(key = "quick_chips") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.TrendingUp,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Popular Searches & Movies",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CuratedCatalog.quickSearchChips.forEach { term ->
                                    Surface(
                                        onClick = { onSubmitSearch(term) },
                                        color = MidnightSurface,
                                        shape = CircleShape,
                                        border = BorderStroke(1.dp, MidnightOutline),
                                        modifier = Modifier.testTag("quick_chip_${term.lowercase().replace(" ", "_")}")
                                    ) {
                                        Text(
                                            text = term,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = TextPrimary,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item(key = "browse_movie_albums_header") {
                        Text(
                            text = "Explore Movie Albums",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }

                    items(CuratedCatalog.featuredMovieAlbums, key = { it.id }) { album ->
                        SearchMovieAlbumRow(
                            album = album,
                            onClick = { onOpenMovieAlbum(album) }
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 148.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (!searchState.errorMessage.isNullOrBlank()) {
                        item(key = "search_error") {
                            Surface(
                                color = MidnightSurface,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = searchState.errorMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(18.dp)
                                )
                            }
                        }
                    }

                    val showAlbums = searchState.filter == SearchFilter.ALL ||
                        searchState.filter == SearchFilter.MOVIE_ALBUMS
                    val showSongs = searchState.filter == SearchFilter.ALL ||
                        searchState.filter == SearchFilter.SONGS

                    if (showAlbums && searchState.albumResults.isNotEmpty()) {
                        item(key = "albums_result_header") {
                            Text(
                                text = "Movie Albums & Soundtracks",
                                style = MaterialTheme.typography.titleMedium,
                                color = ElectricCyan,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                            )
                        }

                        if (searchState.filter == SearchFilter.ALL) {
                            item(key = "albums_result_carousel") {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(searchState.albumResults, key = { it.id }) { album ->
                                        MovieAlbumSquareCard(
                                            album = album,
                                            onClick = { onOpenMovieAlbum(album) }
                                        )
                                    }
                                }
                            }
                        } else {
                            items(searchState.albumResults, key = { it.id }) { album ->
                                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 3.dp)) {
                                    SearchMovieAlbumRow(
                                        album = album,
                                        onClick = { onOpenMovieAlbum(album) }
                                    )
                                }
                            }
                        }
                    }

                    if (showSongs && searchState.songResults.isNotEmpty()) {
                        item(key = "songs_result_header") {
                            Text(
                                text = "Songs (${searchState.songResults.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = EmeraldTeal,
                                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 4.dp)
                            )
                        }

                        items(searchState.songResults, key = { it.videoId }) { track ->
                            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                                TrackRowItem(
                                    track = track,
                                    isCurrentTrack = track.videoId == currentPlayingVideoId,
                                    isDownloaded = downloadedVideoIds.contains(track.videoId),
                                    isFavorite = favoriteVideoIds.contains(track.videoId),
                                    downloadProgress = downloadProgressMap[track.videoId],
                                    onPlayClick = { onTrackClick(track, searchState.songResults) },
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
    }
}

@Composable
private fun SearchMovieAlbumRow(
    album: MovieAlbum,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MidnightSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MidnightOutline.copy(alpha = 0.65f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("search_album_row_${album.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(album.gradientStartHex), Color(album.gradientEndHex))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!album.artworkUrl.isNullOrBlank()) {
                    ArtworkImage(
                        url = album.artworkUrl,
                        contentDescription = album.title,
                        modifier = Modifier.fillMaxSize(),
                        cornerRadius = 12.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Album,
                        contentDescription = null,
                        tint = ElectricCyan,
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

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = EmeraldTeal
            )
        }
    }
}
