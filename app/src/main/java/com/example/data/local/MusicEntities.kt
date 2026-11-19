package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.Track

@Entity(tableName = "downloaded_tracks")
data class DownloadedTrackEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val artist: String,
    val albumName: String,
    val durationSeconds: Long,
    val thumbnailUrl: String,
    val localFilePath: String,
    val fileSizeBytes: Long,
    val downloadedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Track = Track(
        videoId = videoId,
        title = title,
        artist = artist,
        albumName = albumName,
        durationSeconds = durationSeconds,
        thumbnailUrl = thumbnailUrl,
        localFilePath = localFilePath,
        isDownloaded = true
    )
}

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Long = 0L,
    val name: String,
    val description: String = "",
    val coverUrl: String = "",
    val isFavorites: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "videoId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playlistId"]), Index(value = ["videoId"])]
)
data class PlaylistTrackEntity(
    val playlistId: Long,
    val videoId: String,
    val title: String,
    val artist: String,
    val albumName: String,
    val durationSeconds: Long,
    val thumbnailUrl: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(downloadedMap: Map<String, DownloadedTrackEntity>): Track {
        val dl = downloadedMap[videoId]
        return Track(
            videoId = videoId,
            title = title,
            artist = artist,
            albumName = albumName,
            durationSeconds = durationSeconds,
            thumbnailUrl = thumbnailUrl,
            localFilePath = dl?.localFilePath,
            isDownloaded = dl != null
        )
    }
}

data class PlaylistSummary(
    val playlistId: Long,
    val name: String,
    val description: String,
    val coverUrl: String,
    val isFavorites: Boolean,
    val trackCount: Int,
    val downloadedCount: Int,
    val firstThumbnailUrl: String?
)
