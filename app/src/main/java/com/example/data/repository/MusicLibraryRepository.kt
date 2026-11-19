package com.example.data.repository

import com.example.data.local.DownloadedTrackEntity
import com.example.data.local.MusicDao
import com.example.data.local.PlaylistEntity
import com.example.data.local.PlaylistSummary
import com.example.data.local.PlaylistTrackEntity
import com.example.data.model.DownloadedAlbumGroup
import com.example.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

class MusicLibraryRepository(
    private val dao: MusicDao
) {
    val downloadedTracksFlow: Flow<List<Track>> = dao.observeDownloadedTracks()
        .map { entities ->
            entities.mapNotNull { entity ->
                if (File(entity.localFilePath).exists()) entity.toDomain() else null
            }
        }

    val downloadedVideoIdsFlow: Flow<Set<String>> = downloadedTracksFlow
        .map { list -> list.map { it.videoId }.toSet() }

    val downloadedAlbumsFlow: Flow<List<DownloadedAlbumGroup>> = downloadedTracksFlow
        .map { tracks ->
            tracks
                .filter { it.albumName.isNotBlank() }
                .groupBy { it.albumName }
                .map { (albumName, albumTracks) ->
                    DownloadedAlbumGroup(
                        albumName = albumName,
                        artworkUrl = albumTracks.firstOrNull { it.thumbnailUrl.isNotBlank() }?.thumbnailUrl.orEmpty(),
                        tracks = albumTracks
                    )
                }
                .sortedByDescending { it.tracks.size }
        }

    val totalDownloadSizeBytesFlow: Flow<Long> = dao.observeTotalDownloadSizeBytes()

    val playlistSummariesFlow: Flow<List<PlaylistSummary>> = dao.observePlaylistSummaries()

    val favoriteVideoIdsFlow: Flow<Set<String>> = dao.observeFavoriteVideoIds()
        .map { it.toSet() }

    suspend fun ensureDefaultPlaylistsExist() = withContext(Dispatchers.IO) {
        if (dao.getFavoritesPlaylist() == null) {
            dao.insertPlaylist(
                PlaylistEntity(
                    name = "Favorites",
                    description = "Songs you loved across Sargam",
                    isFavorites = true
                )
            )
        }
    }

    /**
     * Checks local Room DB + internal storage before any online stream resolution.
     */
    suspend fun getLocalFileIfDownloaded(videoId: String): File? = withContext(Dispatchers.IO) {
        val entity = dao.getDownloadedTrackById(videoId) ?: return@withContext null
        val file = File(entity.localFilePath)
        if (file.exists() && file.length() > 0L) {
            file
        } else {
            dao.deleteDownloadedTrack(videoId)
            null
        }
    }

    suspend fun saveDownloadedTrack(entity: DownloadedTrackEntity) = withContext(Dispatchers.IO) {
        dao.upsertDownloadedTrack(entity)
    }

    suspend fun deleteDownloadedTrack(videoId: String) = withContext(Dispatchers.IO) {
        val existing = dao.getDownloadedTrackById(videoId)
        if (existing != null) {
            runCatching { File(existing.localFilePath).delete() }
            dao.deleteDownloadedTrack(videoId)
        }
    }

    suspend fun deleteDownloadedAlbum(albumName: String) = withContext(Dispatchers.IO) {
        val all = dao.getAllDownloadedTracksOnce()
        all.filter { it.albumName == albumName }.forEach { entity ->
            runCatching { File(entity.localFilePath).delete() }
        }
        dao.deleteDownloadedAlbum(albumName)
    }

    suspend fun createPlaylist(name: String, description: String): Long = withContext(Dispatchers.IO) {
        dao.insertPlaylist(
            PlaylistEntity(
                name = name.trim(),
                description = description.trim(),
                isFavorites = false
            )
        )
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        dao.deletePlaylist(playlistId)
    }

    suspend fun addTrackToPlaylist(playlistId: Long, track: Track) = withContext(Dispatchers.IO) {
        dao.upsertPlaylistTrack(
            PlaylistTrackEntity(
                playlistId = playlistId,
                videoId = track.videoId,
                title = track.title,
                artist = track.artist,
                albumName = track.albumName,
                durationSeconds = track.durationSeconds,
                thumbnailUrl = track.thumbnailUrl
            )
        )
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, videoId: String) = withContext(Dispatchers.IO) {
        dao.removeTrackFromPlaylist(playlistId, videoId)
    }

    suspend fun toggleFavorite(track: Track): Boolean = withContext(Dispatchers.IO) {
        val fav = dao.getFavoritesPlaylist() ?: run {
            val id = dao.insertPlaylist(
                PlaylistEntity(
                    name = "Favorites",
                    description = "Songs you loved across Sargam",
                    isFavorites = true
                )
            )
            PlaylistEntity(playlistId = id, name = "Favorites", isFavorites = true)
        }
        val existingTracks = dao.getTracksForPlaylistOnce(fav.playlistId)
        val alreadyFav = existingTracks.any { it.videoId == track.videoId }
        if (alreadyFav) {
            dao.removeTrackFromPlaylist(fav.playlistId, track.videoId)
            false
        } else {
            addTrackToPlaylist(fav.playlistId, track)
            true
        }
    }

    fun observePlaylist(playlistId: Long): Flow<PlaylistEntity?> = dao.observePlaylistById(playlistId)

    fun observePlaylistTracks(playlistId: Long): Flow<List<Track>> {
        return combine(
            dao.observeTracksForPlaylist(playlistId),
            dao.observeDownloadedTracks()
        ) { playlistTracks, downloaded ->
            val downloadedMap = downloaded
                .filter { File(it.localFilePath).exists() }
                .associateBy { it.videoId }
            playlistTracks.map { it.toDomain(downloadedMap) }
        }
    }

    suspend fun getOfflineTracksForPlaylist(playlistId: Long): List<Track> = withContext(Dispatchers.IO) {
        val tracks = dao.getTracksForPlaylistOnce(playlistId)
        val downloadedMap = dao.getAllDownloadedTracksOnce()
            .filter { File(it.localFilePath).exists() }
            .associateBy { it.videoId }
        tracks.mapNotNull { pt ->
            downloadedMap[pt.videoId]?.let { pt.toDomain(downloadedMap) }
        }
    }
}
