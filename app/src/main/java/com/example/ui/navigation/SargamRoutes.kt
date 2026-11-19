package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object SearchRoute

@Serializable
data object DownloadsRoute

@Serializable
data object LibraryRoute

@Serializable
data class AlbumDetailRoute(val albumId: String)

@Serializable
data class MoodDetailRoute(val moodId: String)

@Serializable
data class PlaylistDetailRoute(val playlistId: Long)
