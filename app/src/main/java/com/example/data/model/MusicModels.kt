package com.example.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class Track(
    val videoId: String,
    val title: String,
    val artist: String,
    val albumName: String = "",
    val durationSeconds: Long = 0L,
    val thumbnailUrl: String = "",
    val streamUrl: String? = null,
    val localFilePath: String? = null,
    val isDownloaded: Boolean = false
) {
    val formattedDuration: String
        get() {
            if (durationSeconds <= 0L) return "--:--"
            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            return "%d:%02d".format(mins, secs)
        }
}

@Immutable
data class MovieAlbum(
    val id: String,
    val title: String,
    val subtitle: String,
    val searchQuery: String,
    val year: String = "",
    val playlistUrl: String? = null,
    val artworkUrl: String? = null,
    val gradientStartHex: Long = 0xFF0F3D4C,
    val gradientEndHex: Long = 0xFF131B2E
)

@Immutable
data class MoodCollection(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val searchQuery: String,
    val gradientStartHex: Long,
    val gradientEndHex: Long
)

enum class SearchFilter(val label: String) {
    ALL("All"),
    SONGS("Songs"),
    MOVIE_ALBUMS("Movie Albums")
}

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    COMPLETED,
    FAILED
}

@Immutable
data class TrackDownloadProgress(
    val videoId: String,
    val title: String,
    val progress: Float = 0f,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val errorMessage: String? = null
)

@Immutable
data class BatchDownloadState(
    val collectionTitle: String,
    val totalCount: Int,
    val completedCount: Int,
    val failedCount: Int,
    val currentTrackTitle: String,
    val currentTrackProgress: Float,
    val isActive: Boolean
) {
    val overallProgress: Float
        get() {
            if (totalCount <= 0) return 0f
            return ((completedCount + currentTrackProgress) / totalCount.toFloat()).coerceIn(0f, 1f)
        }
}

@Immutable
data class DownloadedAlbumGroup(
    val albumName: String,
    val artworkUrl: String,
    val tracks: List<Track>
)

@Immutable
data class ResolvedAudioStream(
    val url: String,
    val extension: String,
    val bitrateKbps: Int,
    val mimeType: String
)

enum class RepeatModeState {
    OFF,
    ALL,
    ONE
}

@Immutable
data class PlayerUiState(
    val currentTrack: Track? = null,
    val queue: List<Track> = emptyList(),
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isResolvingStream: Boolean = false,
    val isPlayingOfflineFile: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatModeState = RepeatModeState.OFF,
    val errorMessage: String? = null
) {
    val progressFraction: Float
        get() = if (durationMs > 0L) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
