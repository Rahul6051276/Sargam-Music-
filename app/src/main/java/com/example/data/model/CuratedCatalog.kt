package com.example.data.model

object CuratedCatalog {

    // 7 Auto-Curated Mood & Vibe Collections (Strictly zero gold or yellow gradients)
    val moodCollections: List<MoodCollection> = listOf(
        MoodCollection(
            id = "new_releases",
            title = "New Releases",
            subtitle = "Latest Hindi & Bollywood Songs 2026",
            badge = "FRESH",
            searchQuery = "Latest Hindi Bollywood Songs 2026 official audio",
            gradientStartHex = 0xFF007A5E,
            gradientEndHex = 0xFF0B273A
        ),
        MoodCollection(
            id = "trending_top_50",
            title = "Trending Top 50",
            subtitle = "Trending Hindi & Punjabi Hits",
            badge = "CHARTS",
            searchQuery = "Trending Hindi Punjabi Hit Songs 2026 official",
            gradientStartHex = 0xFF0369A1,
            gradientEndHex = 0xFF131B3A
        ),
        MoodCollection(
            id = "romantic_love",
            title = "Romantic Love",
            subtitle = "Best Bollywood Love & Soulful Songs",
            badge = "SOULFUL",
            searchQuery = "Best Bollywood Romantic Love Soulful Hindi Songs Arijit Singh",
            gradientStartHex = 0xFF9D174D,
            gradientEndHex = 0xFF1D1838
        ),
        MoodCollection(
            id = "sad_heartbreak",
            title = "Sad & Heartbreak",
            subtitle = "Hindi Sad & Lo-Fi Songs",
            badge = "LATE NIGHT",
            searchQuery = "Hindi Sad Heartbreak Soulful Lo-Fi Bollywood Songs",
            gradientStartHex = 0xFF3730A3,
            gradientEndHex = 0xFF0E172A
        ),
        MoodCollection(
            id = "travel_long_drive",
            title = "Travel & Long Drive",
            subtitle = "Bollywood Road Trip & Chill Vibes",
            badge = "ROAD TRIP",
            searchQuery = "Bollywood Road Trip Long Drive Chill Hindi Songs",
            gradientStartHex = 0xFF0F766E,
            gradientEndHex = 0xFF102A43
        ),
        MoodCollection(
            id = "gym_workout",
            title = "Gym & Workout",
            subtitle = "High-Energy Workout & Punjabi Beats",
            badge = "BEAST MODE",
            searchQuery = "High Energy Punjabi Hindi Gym Workout Rap Beats Songs",
            gradientStartHex = 0xFF1D4ED8,
            gradientEndHex = 0xFF0B1F3A
        ),
        MoodCollection(
            id = "evergreen_90s",
            title = "90s Evergreen Hits",
            subtitle = "Classic Bollywood Retro Collection",
            badge = "CLASSIC",
            searchQuery = "90s Evergreen Classic Bollywood Hindi Kumar Sanu Udit Narayan Songs",
            gradientStartHex = 0xFF5B21B6,
            gradientEndHex = 0xFF131B2E
        )
    )

    // Featured Movie Albums & Soundtracks Carousel (Strictly zero gold or yellow)
    val featuredMovieAlbums: List<MovieAlbum> = listOf(
        MovieAlbum(
            id = "album_kabir_singh",
            title = "Kabir Singh",
            subtitle = "Sachet-Parampara, Mithoon, Vishal Mishra",
            searchQuery = "Kabir Singh movie songs official",
            year = "2019",
            gradientStartHex = 0xFF881337,
            gradientEndHex = 0xFF131B2E
        ),
        MovieAlbum(
            id = "album_dhurandhar",
            title = "Dhurandhar",
            subtitle = "Shashwat Sachdev · Original Soundtrack",
            searchQuery = "Dhurandhar movie songs official",
            year = "2025",
            gradientStartHex = 0xFF065F46,
            gradientEndHex = 0xFF0F172A
        ),
        MovieAlbum(
            id = "album_animal",
            title = "Animal",
            subtitle = "Pritam, Vishal Mishra, Manan Bhardwaj",
            searchQuery = "Animal Hindi movie songs official",
            year = "2023",
            gradientStartHex = 0xFF1E3A8A,
            gradientEndHex = 0xFF0F172A
        ),
        MovieAlbum(
            id = "album_aashiqui_2",
            title = "Aashiqui 2",
            subtitle = "Mithoon, Jeet Gannguli, Ankit Tiwari",
            searchQuery = "Aashiqui 2 movie songs official",
            year = "2013",
            gradientStartHex = 0xFF0E7490,
            gradientEndHex = 0xFF111827
        ),
        MovieAlbum(
            id = "album_yjhd",
            title = "Yeh Jawaani Hai Deewani",
            subtitle = "Pritam · Classic Youth Soundtrack",
            searchQuery = "Yeh Jawaani Hai Deewani movie songs official",
            year = "2013",
            gradientStartHex = 0xFF4338CA,
            gradientEndHex = 0xFF0F172A
        ),
        MovieAlbum(
            id = "album_rockstar",
            title = "Rockstar",
            subtitle = "A. R. Rahman, Mohit Chauhan",
            searchQuery = "Rockstar A R Rahman movie songs official",
            year = "2011",
            gradientStartHex = 0xFF6D28D9,
            gradientEndHex = 0xFF0F172A
        ),
        MovieAlbum(
            id = "album_shershaah",
            title = "Shershaah",
            subtitle = "Tanishk Bagchi, B Praak, Jasleen Royal",
            searchQuery = "Shershaah movie songs official",
            year = "2021",
            gradientStartHex = 0xFF047857,
            gradientEndHex = 0xFF111827
        ),
        MovieAlbum(
            id = "album_brahmastra",
            title = "Brahmāstra",
            subtitle = "Pritam, Arijit Singh, Amitabh Bhattacharya",
            searchQuery = "Brahmastra movie songs Hindi official",
            year = "2022",
            gradientStartHex = 0xFF0284C7,
            gradientEndHex = 0xFF131B2E
        )
    )

    val quickSearchChips: List<String> = listOf(
        "Arijit Singh",
        "Kabir Singh",
        "Dhurandhar",
        "Animal",
        "Pritam",
        "Shreya Ghoshal",
        "Diljit Dosanjh",
        "A. R. Rahman"
    )

    fun findMoodById(id: String): MoodCollection? = moodCollections.firstOrNull { it.id == id }

    fun findFeaturedAlbumById(id: String): MovieAlbum? = featuredMovieAlbums.firstOrNull { it.id == id }
}
