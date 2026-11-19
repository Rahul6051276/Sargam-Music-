package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    // Downloaded Tracks
    @Query("SELECT * FROM downloaded_tracks ORDER BY downloadedAt DESC")
    fun observeDownloadedTracks(): Flow<List<DownloadedTrackEntity>>

    @Query("SELECT * FROM downloaded_tracks WHERE videoId = :videoId LIMIT 1")
    suspend fun getDownloadedTrackById(videoId: String): DownloadedTrackEntity?

    @Query("SELECT * FROM downloaded_tracks")
    suspend fun getAllDownloadedTracksOnce(): List<DownloadedTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDownloadedTrack(track: DownloadedTrackEntity)

    @Query("DELETE FROM downloaded_tracks WHERE videoId = :videoId")
    suspend fun deleteDownloadedTrack(videoId: String)

    @Query("DELETE FROM downloaded_tracks WHERE albumName = :albumName")
    suspend fun deleteDownloadedAlbum(albumName: String)

    @Query("SELECT COALESCE(SUM(fileSizeBytes), 0) FROM downloaded_tracks")
    fun observeTotalDownloadSizeBytes(): Flow<Long>

    // Playlists
    @Query(
        """
        SELECT 
            p.playlistId,
            p.name,
            p.description,
            p.coverUrl,
            p.isFavorites,
            COUNT(pt.videoId) AS trackCount,
            COUNT(dt.videoId) AS downloadedCount,
            (SELECT pt2.thumbnailUrl FROM playlist_tracks pt2 
             WHERE pt2.playlistId = p.playlistId 
             ORDER BY pt2.addedAt DESC LIMIT 1) AS firstThumbnailUrl
        FROM playlists p
        LEFT JOIN playlist_tracks pt ON p.playlistId = pt.playlistId
        LEFT JOIN downloaded_tracks dt ON pt.videoId = dt.videoId
        GROUP BY p.playlistId
        ORDER BY p.isFavorites DESC, p.createdAt DESC
        """
    )
    fun observePlaylistSummaries(): Flow<List<PlaylistSummary>>

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId LIMIT 1")
    fun observePlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    @Query("SELECT * FROM playlists WHERE isFavorites = 1 LIMIT 1")
    suspend fun getFavoritesPlaylist(): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId AND isFavorites = 0")
    suspend fun deletePlaylist(playlistId: Long)

    // Playlist Tracks
    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY addedAt DESC")
    fun observeTracksForPlaylist(playlistId: Long): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY addedAt DESC")
    suspend fun getTracksForPlaylistOnce(playlistId: Long): List<PlaylistTrackEntity>

    @Query(
        """
        SELECT pt.videoId FROM playlist_tracks pt
        INNER JOIN playlists p ON p.playlistId = pt.playlistId
        WHERE p.isFavorites = 1
        """
    )
    fun observeFavoriteVideoIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlaylistTrack(track: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun removeTrackFromPlaylist(playlistId: Long, videoId: String)
}
