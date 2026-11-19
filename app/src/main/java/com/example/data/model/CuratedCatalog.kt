package com.example.data.model

object CuratedCatalog {

    // 1. Golden Era & Old Classics Hub (Strict Original-Version Filter enabled; zero gold/yellow hex colors)
    val goldenRetroCollections: List<MoodCollection> = listOf(
        MoodCollection(
            id = "retro_70s_80s_melodies",
            title = "70s & 80s Golden Melodies",
            subtitle = "Kishore Kumar, Mohd. Rafi, Lata Mangeshkar, Asha Bhosle, R.D. Burman",
            badge = "70s · 80s",
            searchQuery = "Kishore Kumar Mohd Rafi Lata Mangeshkar original Hindi songs",
            gradientStartHex = 0xFF0F4C5C,
            gradientEndHex = 0xFF131B2E,
            category = CollectionCategory.GOLDEN_RETRO,
            isOldClassic = true,
            seedQueries = listOf(
                "Kishore Kumar RD Burman original Hindi classic songs Saregama",
                "Mohammed Rafi Lata Mangeshkar 70s 80s evergreen Hindi songs",
                "Asha Bhosle Kishore Kumar original Bollywood retro hits",
                "70s 80s evergreen Bollywood classic melodies original audio"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.OLD_CLASSICS)
        ),
        MoodCollection(
            id = "retro_90s_romantic_sad",
            title = "90s Romantic & Sad Classics",
            subtitle = "Kumar Sanu, Udit Narayan, Alka Yagnik, Nadeem-Shravan",
            badge = "90s ERA",
            searchQuery = "Kumar Sanu Udit Narayan Alka Yagnik 90s Bollywood songs",
            gradientStartHex = 0xFF6B21A8,
            gradientEndHex = 0xFF131B2E,
            category = CollectionCategory.GOLDEN_RETRO,
            isOldClassic = true,
            seedQueries = listOf(
                "Kumar Sanu Alka Yagnik 90s romantic original songs Tips Venus",
                "Udit Narayan Nadeem Shravan 90s evergreen Hindi hits",
                "90s Bollywood romantic and sad classic songs original audio",
                "Best of 90s Hindi love songs Kumar Sanu Udit Narayan"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.OLD_CLASSICS,
                HomeFilterChip.NINETIES_HITS
            )
        ),
        MoodCollection(
            id = "retro_ghazals_sufi",
            title = "Soulful Ghazals & Sufi Nights",
            subtitle = "Jagjit Singh, Pankaj Udhas, Nusrat & Rahat Fateh Ali Khan",
            badge = "GHAZAL · SUFI",
            searchQuery = "Jagjit Singh Pankaj Udhas Nusrat Fateh Ali Khan ghazals sufi",
            gradientStartHex = 0xFF065F46,
            gradientEndHex = 0xFF0F172A,
            category = CollectionCategory.GOLDEN_RETRO,
            isOldClassic = true,
            seedQueries = listOf(
                "Jagjit Singh best evergreen ghazals original audio",
                "Nusrat Fateh Ali Khan Rahat Fateh Ali Khan soulful sufi qawwali",
                "Pankaj Udhas Talat Aziz classic Hindi ghazals original",
                "Soulful late night Hindi Urdu ghazals and sufi songs"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.OLD_CLASSICS,
                HomeFilterChip.GHAZAL_SUFI
            )
        ),
        MoodCollection(
            id = "retro_old_sad_gems",
            title = "Old Sad & Melancholy Gems",
            subtitle = "Mukesh, Kishore Kumar, Mohd. Rafi emotional classics",
            badge = "MELANCHOLY",
            searchQuery = "Mukesh Kishore Kumar Mohd Rafi old Hindi sad songs original",
            gradientStartHex = 0xFF312E81,
            gradientEndHex = 0xFF0E172A,
            category = CollectionCategory.GOLDEN_RETRO,
            isOldClassic = true,
            seedQueries = listOf(
                "Mukesh evergreen sad Hindi songs original audio Saregama",
                "Kishore Kumar Mohammed Rafi emotional sad classic Bollywood songs",
                "Lata Mangeshkar old sad Hindi melodies original",
                "Classic 60s 70s Hindi sad songs original soundtrack"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.OLD_CLASSICS)
        ),
        MoodCollection(
            id = "retro_roadtrip_duets",
            title = "Retro Road Trip & Duets",
            subtitle = "Classic Bollywood evergreen duets & feel-good oldies",
            badge = "DUETS",
            searchQuery = "Classic Bollywood evergreen duets Kishore Lata Rafi Asha",
            gradientStartHex = 0xFF0E7490,
            gradientEndHex = 0xFF131B2E,
            category = CollectionCategory.GOLDEN_RETRO,
            isOldClassic = true,
            seedQueries = listOf(
                "Kishore Kumar Lata Mangeshkar evergreen romantic duets original",
                "Mohammed Rafi Asha Bhosle feel good Bollywood old duets",
                "Retro Bollywood road trip happy classic Hindi songs",
                "RD Burman classic upbeat Hindi duets original audio"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.OLD_CLASSICS,
                HomeFilterChip.NINETIES_HITS
            )
        )
    )

    // 2. Editor's Picks: Trending & Fresh Shelf
    val trendingFreshCollections: List<MoodCollection> = listOf(
        MoodCollection(
            id = "viral_hits_2026",
            title = "Viral Hits 2026",
            subtitle = "Chart-topping Hindi, Indie & Bollywood viral tracks",
            badge = "VIRAL 2026",
            searchQuery = "Viral Hindi Bollywood songs 2026 official audio",
            gradientStartHex = 0xFF007A5E,
            gradientEndHex = 0xFF0B273A,
            category = CollectionCategory.TRENDING_FRESH,
            seedQueries = listOf(
                "Latest Viral Hindi Bollywood songs 2026 official audio",
                "Trending Indian Indie Hindi pop hits 2026 official",
                "New Bollywood chartbusters 2025 2026 official song"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI)
        ),
        MoodCollection(
            id = "top_50_india",
            title = "Top 50 India",
            subtitle = "Most streamed Hindi & Punjabi hits across India",
            badge = "TOP 50",
            searchQuery = "Top 50 India trending Hindi Punjabi songs official",
            gradientStartHex = 0xFF0369A1,
            gradientEndHex = 0xFF131B3A,
            category = CollectionCategory.TRENDING_FRESH,
            seedQueries = listOf(
                "Top 50 India trending Hindi Punjabi songs official audio",
                "Most played Bollywood Punjabi songs 2026 official",
                "India Superhits Hindi Punjabi chart playlist"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.PUNJABI)
        ),
        MoodCollection(
            id = "latest_bollywood_albums",
            title = "Latest Bollywood Albums",
            subtitle = "Fresh cinema soundtracks & new releases",
            badge = "NEW RELEASES",
            searchQuery = "Latest Bollywood movie album songs 2026 official",
            gradientStartHex = 0xFF1D4ED8,
            gradientEndHex = 0xFF0F172A,
            category = CollectionCategory.TRENDING_FRESH,
            seedQueries = listOf(
                "Latest Hindi Bollywood movie album songs 2026 official",
                "New Bollywood soundtrack releases T-Series Zee Music Sony Music",
                "Fresh Hindi movie songs 2025 2026 official audio"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI)
        ),
        MoodCollection(
            id = "punjabi_chartbusters",
            title = "Punjabi Chartbusters",
            subtitle = "Diljit Dosanjh, Karan Aujla, AP Dhillon, Shubh",
            badge = "PUNJABI",
            searchQuery = "Trending Punjabi hits Diljit Dosanjh Karan Aujla AP Dhillon",
            gradientStartHex = 0xFF0F766E,
            gradientEndHex = 0xFF131B2E,
            category = CollectionCategory.TRENDING_FRESH,
            seedQueries = listOf(
                "Diljit Dosanjh Karan Aujla trending Punjabi songs official",
                "AP Dhillon Shubh latest Punjabi hits official audio",
                "Top Punjabi chartbusters 2026 official song"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.PUNJABI)
        ),
        MoodCollection(
            id = "english_global_hits",
            title = "Global English Pop",
            subtitle = "The Weeknd, Dua Lipa, Coldplay, Ed Sheeran & Trending",
            badge = "ENGLISH",
            searchQuery = "Top trending English pop hits official audio 2026",
            gradientStartHex = 0xFF4338CA,
            gradientEndHex = 0xFF0F172A,
            category = CollectionCategory.TRENDING_FRESH,
            seedQueries = listOf(
                "Top Global English pop songs 2026 official audio",
                "The Weeknd Coldplay Dua Lipa hit English songs",
                "Billboard Hot English hits official music audio"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.ENGLISH)
        )
    )

    // 3. Mood & Activity Shelf
    val moodActivityCollections: List<MoodCollection> = listOf(
        MoodCollection(
            id = "mood_late_night_lofi",
            title = "Late Night Lo-Fi",
            subtitle = "Dreamy midnight acoustic, chilled Hindi & English vibes",
            badge = "MIDNIGHT",
            searchQuery = "Hindi Bollywood acoustic chill late night songs",
            gradientStartHex = 0xFF3730A3,
            gradientEndHex = 0xFF0E172A,
            category = CollectionCategory.MOOD_ACTIVITY,
            seedQueries = listOf(
                "Hindi acoustic midnight chill Bollywood love songs",
                "Late night calm soulful Hindi indie songs",
                "Chill English midnight synth pop acoustic songs"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.ENGLISH)
        ),
        MoodCollection(
            id = "mood_long_drive",
            title = "Long Drive Classics",
            subtitle = "Highway Bollywood road trip & breezy travel anthems",
            badge = "ROAD TRIP",
            searchQuery = "Bollywood road trip long drive Hindi travel songs",
            gradientStartHex = 0xFF0F766E,
            gradientEndHex = 0xFF102A43,
            category = CollectionCategory.MOOD_ACTIVITY,
            seedQueries = listOf(
                "Bollywood long drive road trip Hindi songs Pritam Mohit Chauhan",
                "Best Hindi travel highway chill songs official audio",
                "Punjabi Hindi car drive vibes songs"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.PUNJABI,
                HomeFilterChip.NINETIES_HITS
            )
        ),
        MoodCollection(
            id = "mood_workout",
            title = "High-Energy Workout",
            subtitle = "Adrenaline Punjabi beats, Bollywood hype & English pump",
            badge = "WORKOUT",
            searchQuery = "High energy Punjabi Hindi gym workout songs beats",
            gradientStartHex = 0xFF1D4ED8,
            gradientEndHex = 0xFF0B1F3A,
            category = CollectionCategory.MOOD_ACTIVITY,
            seedQueries = listOf(
                "High energy Punjabi gym workout songs Sidhu Moose Wala Karan Aujla",
                "Bollywood motivational gym workout songs Bhaag Milkha Sultan",
                "High energy English workout hip hop pop anthems"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.PUNJABI,
                HomeFilterChip.ENGLISH
            )
        ),
        MoodCollection(
            id = "mood_heartbreak_sad",
            title = "Heartbreak & Sad",
            subtitle = "Emotional Arijit Singh, KK, Vishal Mishra & soulful ballads",
            badge = "SOULFUL",
            searchQuery = "Hindi sad heartbreak Bollywood emotional songs Arijit Singh",
            gradientStartHex = 0xFF881337,
            gradientEndHex = 0xFF131B2E,
            category = CollectionCategory.MOOD_ACTIVITY,
            seedQueries = listOf(
                "Arijit Singh KK emotional heartbreak Hindi songs official",
                "Best Bollywood sad breakup soulful songs official audio",
                "Emotional Punjabi sad songs B Praak Amrinder Gill"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.PUNJABI,
                HomeFilterChip.GHAZAL_SUFI
            )
        ),
        MoodCollection(
            id = "mood_party_dance",
            title = "Party & Dance",
            subtitle = "Non-stop Bollywood club bangers & Punjabi dancefloor hits",
            badge = "PARTY",
            searchQuery = "Bollywood party dance songs Badshah Diljit Honey Singh",
            gradientStartHex = 0xFF0284C7,
            gradientEndHex = 0xFF1E1B4B,
            category = CollectionCategory.MOOD_ACTIVITY,
            seedQueries = listOf(
                "Bollywood party dance club hit songs official audio",
                "Punjabi wedding party dance bangers Diljit Badshah",
                "Upbeat English dance club pop hits official"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.PUNJABI,
                HomeFilterChip.ENGLISH
            )
        ),
        MoodCollection(
            id = "mood_romantic_love",
            title = "Romantic Love",
            subtitle = "Timeless Bollywood romance, Arijit, Shreya & Atif",
            badge = "ROMANCE",
            searchQuery = "Best Bollywood romantic love Hindi songs Arijit Shreya",
            gradientStartHex = 0xFF9D174D,
            gradientEndHex = 0xFF1D1838,
            category = CollectionCategory.MOOD_ACTIVITY,
            seedQueries = listOf(
                "Best Bollywood romantic love Hindi songs Arijit Singh Shreya Ghoshal",
                "Atif Aslam Pritam soulful Hindi love songs official",
                "Romantic Sufi Bollywood songs Rahat Fateh Ali Khan"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.GHAZAL_SUFI
            )
        )
    )

    val allCollections: List<MoodCollection> =
        goldenRetroCollections + trendingFreshCollections + moodActivityCollections

    // Backward-compatible alias
    val moodCollections: List<MoodCollection> = allCollections

    // 4. Legendary & Modern Artist Radio Stations (12 stations -> 40-track auto-generated radio mixes)
    val artistRadios: List<ArtistRadio> = listOf(
        // Classic Legends
        ArtistRadio(
            id = "artist_kishore_kumar",
            name = "Kishore Kumar",
            eraLabel = "LEGEND",
            subtitle = "40-Track Evergreen Radio · Original Classics",
            isClassicLegend = true,
            seedQueries = listOf(
                "Kishore Kumar best evergreen original Hindi songs Saregama",
                "Kishore Kumar RD Burman classic hit songs original audio",
                "Kishore Kumar romantic and sad classic Bollywood songs"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.OLD_CLASSICS),
            accentStartHex = 0xFF00E5A0,
            accentEndHex = 0xFF0E7490
        ),
        ArtistRadio(
            id = "artist_mohd_rafi",
            name = "Mohd. Rafi",
            eraLabel = "LEGEND",
            subtitle = "40-Track Golden Voice Radio · Original Classics",
            isClassicLegend = true,
            seedQueries = listOf(
                "Mohammed Rafi evergreen original Hindi songs Saregama",
                "Mohd Rafi classic romantic and soulful Bollywood hits",
                "Mohammed Rafi Lata Mangeshkar classic original duets"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.OLD_CLASSICS),
            accentStartHex = 0xFF38BDF8,
            accentEndHex = 0xFF1D4ED8
        ),
        ArtistRadio(
            id = "artist_lata_mangeshkar",
            name = "Lata Mangeshkar",
            eraLabel = "LEGEND",
            subtitle = "40-Track Nightingale Radio · Original Classics",
            isClassicLegend = true,
            seedQueries = listOf(
                "Lata Mangeshkar evergreen original Hindi songs Saregama",
                "Lata Mangeshkar 70s 80s 90s classic Bollywood hits",
                "Lata Mangeshkar Kishore Kumar original romantic songs"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.OLD_CLASSICS,
                HomeFilterChip.NINETIES_HITS
            ),
            accentStartHex = 0xFF818CF8,
            accentEndHex = 0xFF5B21B6
        ),
        ArtistRadio(
            id = "artist_kumar_sanu",
            name = "Kumar Sanu",
            eraLabel = "90s KING",
            subtitle = "40-Track 90s Melody Radio · Original Classics",
            isClassicLegend = true,
            seedQueries = listOf(
                "Kumar Sanu 90s evergreen original Bollywood songs",
                "Kumar Sanu Alka Yagnik romantic Hindi hits official",
                "Kumar Sanu Nadeem Shravan classic 90s melodies"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.OLD_CLASSICS,
                HomeFilterChip.NINETIES_HITS
            ),
            accentStartHex = 0xFF2DD4BF,
            accentEndHex = 0xFF0F766E
        ),
        ArtistRadio(
            id = "artist_udit_narayan",
            name = "Udit Narayan",
            eraLabel = "90s ICON",
            subtitle = "40-Track Evergreen 90s Radio · Original Classics",
            isClassicLegend = true,
            seedQueries = listOf(
                "Udit Narayan best 90s evergreen Bollywood songs original",
                "Udit Narayan Alka Yagnik classic Hindi romantic hits",
                "Udit Narayan Shah Rukh Khan Aamir Khan 90s hits"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.OLD_CLASSICS,
                HomeFilterChip.NINETIES_HITS
            ),
            accentStartHex = 0xFF38BDF8,
            accentEndHex = 0xFF4338CA
        ),
        ArtistRadio(
            id = "artist_jagjit_singh",
            name = "Jagjit Singh",
            eraLabel = "GHAZAL",
            subtitle = "40-Track Soulful Ghazal Radio · Original Classics",
            isClassicLegend = true,
            seedQueries = listOf(
                "Jagjit Singh evergreen classic ghazals original audio",
                "Jagjit Singh Chitra Singh best soulful ghazals",
                "Jagjit Singh Bollywood movie ghazals Arth Sarfarosh"
            ),
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.OLD_CLASSICS,
                HomeFilterChip.GHAZAL_SUFI
            ),
            accentStartHex = 0xFF10B981,
            accentEndHex = 0xFF065F46
        ),
        // Modern Stars
        ArtistRadio(
            id = "artist_arijit_singh",
            name = "Arijit Singh",
            eraLabel = "MODERN",
            subtitle = "40-Track Soulful Hits Radio",
            isClassicLegend = false,
            seedQueries = listOf(
                "Arijit Singh best Bollywood romantic soulful songs official",
                "Arijit Singh latest Hindi hit songs 2025 2026 official",
                "Arijit Singh sad and heartbreak melodies official audio"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.GHAZAL_SUFI),
            accentStartHex = 0xFF00E5A0,
            accentEndHex = 0xFF0369A1
        ),
        ArtistRadio(
            id = "artist_kk",
            name = "KK",
            eraLabel = "TIMELESS",
            subtitle = "40-Track Nostalgic Anthems Radio",
            isClassicLegend = false,
            seedQueries = listOf(
                "KK Krishnakumar Kunnath best Bollywood songs official",
                "KK Pritam Emraan Hashmi evergreen Hindi songs",
                "KK romantic rock soulful Hindi hits official audio"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.NINETIES_HITS),
            accentStartHex = 0xFF6366F1,
            accentEndHex = 0xFF1E3A8A
        ),
        ArtistRadio(
            id = "artist_pritam",
            name = "Pritam",
            eraLabel = "MAESTRO",
            subtitle = "40-Track Blockbuster Soundtracks Radio",
            isClassicLegend = false,
            seedQueries = listOf(
                "Pritam best Bollywood hit songs official audio",
                "Pritam Arijit Singh KK Mohit Chauhan blockbuster songs",
                "Pritam Yeh Jawaani Hai Deewani Ae Dil Hai Mushkil songs"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI),
            accentStartHex = 0xFF0EA5E9,
            accentEndHex = 0xFF0F4C5C
        ),
        ArtistRadio(
            id = "artist_shreya_ghoshal",
            name = "Shreya Ghoshal",
            eraLabel = "MELODY",
            subtitle = "40-Track Classical & Romantic Radio",
            isClassicLegend = false,
            seedQueries = listOf(
                "Shreya Ghoshal best Bollywood romantic soulful songs official",
                "Shreya Ghoshal Arijit Singh Sonu Nigam Hindi hits",
                "Shreya Ghoshal classical semi classical Bollywood songs"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.GHAZAL_SUFI),
            accentStartHex = 0xFFEC4899,
            accentEndHex = 0xFF5B21B6
        ),
        ArtistRadio(
            id = "artist_diljit_dosanjh",
            name = "Diljit Dosanjh",
            eraLabel = "PUNJABI",
            subtitle = "40-Track Global Punjabi Radio",
            isClassicLegend = false,
            seedQueries = listOf(
                "Diljit Dosanjh best Punjabi hit songs official audio",
                "Diljit Dosanjh Lover Born To Shine Kinni Kinni official",
                "Diljit Dosanjh Bollywood and Punjabi party hits"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.PUNJABI, HomeFilterChip.HINDI),
            accentStartHex = 0xFF00E5A0,
            accentEndHex = 0xFF047857
        ),
        ArtistRadio(
            id = "artist_atif_aslam",
            name = "Atif Aslam",
            eraLabel = "SUFI POP",
            subtitle = "40-Track Romantic & Sufi Radio",
            isClassicLegend = false,
            seedQueries = listOf(
                "Atif Aslam best Bollywood romantic Hindi songs official",
                "Atif Aslam Tera Hone Laga Hoon Jeene Laga Hoon official",
                "Atif Aslam soulful sufi and acoustic hits"
            ),
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.GHAZAL_SUFI),
            accentStartHex = 0xFF38BDF8,
            accentEndHex = 0xFF0E7490
        )
    )

    // 5. Featured Movie Albums & Soundtracks Carousel (Strictly zero gold or yellow)
    val featuredMovieAlbums: List<MovieAlbum> = listOf(
        MovieAlbum(
            id = "album_kabir_singh",
            title = "Kabir Singh",
            subtitle = "Sachet-Parampara, Mithoon, Vishal Mishra",
            searchQuery = "Kabir Singh movie songs official",
            year = "2019",
            gradientStartHex = 0xFF881337,
            gradientEndHex = 0xFF131B2E,
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI)
        ),
        MovieAlbum(
            id = "album_dhurandhar",
            title = "Dhurandhar",
            subtitle = "Shashwat Sachdev · Original Soundtrack",
            searchQuery = "Dhurandhar movie songs official",
            year = "2025",
            gradientStartHex = 0xFF065F46,
            gradientEndHex = 0xFF0F172A,
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI)
        ),
        MovieAlbum(
            id = "album_animal",
            title = "Animal",
            subtitle = "Pritam, Vishal Mishra, Manan Bhardwaj",
            searchQuery = "Animal Hindi movie songs official",
            year = "2023",
            gradientStartHex = 0xFF1E3A8A,
            gradientEndHex = 0xFF0F172A,
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.PUNJABI)
        ),
        MovieAlbum(
            id = "album_aashiqui_2",
            title = "Aashiqui 2",
            subtitle = "Mithoon, Jeet Gannguli, Ankit Tiwari",
            searchQuery = "Aashiqui 2 movie songs official",
            year = "2013",
            gradientStartHex = 0xFF0E7490,
            gradientEndHex = 0xFF111827,
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI)
        ),
        MovieAlbum(
            id = "album_ddlj",
            title = "Dilwale Dulhania Le Jayenge",
            subtitle = "Jatin-Lalit, Lata Mangeshkar, Udit Narayan",
            searchQuery = "DDLJ Dilwale Dulhania Le Jayenge movie songs original",
            year = "1995",
            gradientStartHex = 0xFF5B21B6,
            gradientEndHex = 0xFF131B2E,
            filters = setOf(
                HomeFilterChip.ALL,
                HomeFilterChip.HINDI,
                HomeFilterChip.OLD_CLASSICS,
                HomeFilterChip.NINETIES_HITS
            )
        ),
        MovieAlbum(
            id = "album_yjhd",
            title = "Yeh Jawaani Hai Deewani",
            subtitle = "Pritam · Classic Youth Soundtrack",
            searchQuery = "Yeh Jawaani Hai Deewani movie songs official",
            year = "2013",
            gradientStartHex = 0xFF4338CA,
            gradientEndHex = 0xFF0F172A,
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI)
        ),
        MovieAlbum(
            id = "album_rockstar",
            title = "Rockstar",
            subtitle = "A. R. Rahman, Mohit Chauhan",
            searchQuery = "Rockstar A R Rahman movie songs official",
            year = "2011",
            gradientStartHex = 0xFF6D28D9,
            gradientEndHex = 0xFF0F172A,
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.GHAZAL_SUFI)
        ),
        MovieAlbum(
            id = "album_shershaah",
            title = "Shershaah",
            subtitle = "Tanishk Bagchi, B Praak, Jasleen Royal",
            searchQuery = "Shershaah movie songs official",
            year = "2021",
            gradientStartHex = 0xFF047857,
            gradientEndHex = 0xFF111827,
            filters = setOf(HomeFilterChip.ALL, HomeFilterChip.HINDI, HomeFilterChip.PUNJABI)
        )
    )

    val quickSearchChips: List<String> = listOf(
        "Kishore Kumar",
        "Arijit Singh",
        "Kabir Singh",
        "90s Kumar Sanu",
        "Jagjit Singh Ghazals",
        "Dhurandhar",
        "Diljit Dosanjh",
        "A. R. Rahman"
    )

    fun freshPickQueryForFilter(filter: HomeFilterChip): MoodCollection = when (filter) {
        HomeFilterChip.ALL -> trendingFreshCollections.first()
        HomeFilterChip.HINDI -> trendingFreshCollections[2]
        HomeFilterChip.OLD_CLASSICS -> goldenRetroCollections.first()
        HomeFilterChip.NINETIES_HITS -> goldenRetroCollections[1]
        HomeFilterChip.PUNJABI -> trendingFreshCollections[3]
        HomeFilterChip.ENGLISH -> trendingFreshCollections[4]
        HomeFilterChip.GHAZAL_SUFI -> goldenRetroCollections[2]
    }

    fun isClassicEraTrack(track: Track): Boolean {
        val combined = "${track.title} ${track.artist} ${track.albumName}".lowercase()
        val classicMarkers = listOf(
            "kishore", "rafi", "lata", "asha bhosle", "mukesh", "r.d. burman", "rd burman",
            "kumar sanu", "udit narayan", "alka yagnik", "jagjit singh", "pankaj udhas",
            "nusrat", "ghazal", "70s", "80s", "90s", "retro", "classic", "old ", "evergreen",
            "melancholy", "saregama", "ddlj", "nadeem"
        )
        return classicMarkers.any { combined.contains(it) }
    }

    fun findMoodById(id: String): MoodCollection? = allCollections.firstOrNull { it.id == id }

    fun findArtistById(id: String): ArtistRadio? = artistRadios.firstOrNull { it.id == id }

    fun findFeaturedAlbumById(id: String): MovieAlbum? = featuredMovieAlbums.firstOrNull { it.id == id }
}
