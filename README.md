# Sargam — Ad-Free Music Streaming & Offline Player

**Sargam** is a 100% ad-free native Android music streaming and offline audio player built in **Kotlin** and **Jetpack Compose (Material 3)**, inspired by the modern listening experience of JioSaavn and Gaana.

Designed with a sleek **Midnight Dark** aesthetic (`#090D16` background, `#131B2E` surfaces, `#00E5A0` Emerald Teal primary accent, and `#38BDF8` Electric Cyan secondary accent), Sargam streams high-quality audio-only tracks directly from YouTube without requiring API keys or manual MP3 hosting, and saves individual songs or entire movie albums locally for seamless airplane-mode playback.

---

## Features

### 1. Automatic YouTube Audio Engine (Zero API Keys)
- Powered by **NewPipeExtractor (`v0.26.3`)** with a custom **OkHttp** downloader (`OkHttpDownloader`).
- Automatically searches YouTube Music tracks, artists, playlists, and movie soundtracks.
- Resolves progressive audio-only streams (`.m4a` / `.webm`) without loading video.
- Filters out long single-video jukeboxes, trailers, and non-song uploads in favor of clean individual tracks.

### 2. Home, Curated Moods & Featured Movie Albums
- **7 Auto-Curated Mood & Vibe Collections:**
  - *New Releases* (Latest Hindi & Bollywood Songs 2026)
  - *Trending Top 50* (Trending Hindi & Punjabi Hits)
  - *Romantic Love* (Best Bollywood Love & Soulful Songs)
  - *Sad & Heartbreak* (Hindi Sad & Lo-Fi Songs)
  - *Travel & Long Drive* (Bollywood Road Trip & Chill Vibes)
  - *Gym & Workout* (High-Energy Workout & Punjabi Beats)
  - *90s Evergreen Hits* (Classic Bollywood Retro Collection)
- **Featured Movie Albums Carousel:** Iconic Bollywood soundtracks (*Kabir Singh*, *Dhurandhar*, *Animal*, *Aashiqui 2*, *Yeh Jawaani Hai Deewani*, *Rockstar*, *Shershaah*, *Brahmāstra*) with dedicated full-bleed `AlbumDetailScreen` views.
- **Instant Search:** Real-time debounced search with `All`, `Songs`, and `Movie Albums` filters—search any movie name to open its full album tracklist.

### 3. One-Click Batch Downloads & Custom Playlists
- Every Movie Album, Mood Collection, and Custom Playlist includes **Play All** / **Play Full Album** and **Download All** / **Download Full Album** actions.
- **Sequential Batch Download Manager:** Downloads every song in an album or playlist one-by-one into internal app storage (`context.filesDir/music/`) with live per-track progress indicators and an interactive collection progress banner.
- **Custom Playlists:** Create and manage personal playlists (e.g., *Favorites*, *Gym*, *Travel*) stored locally in Room.

### 4. Seamless Offline Mode (Offline Vault)
- Before streaming any song, Sargam checks the local **Room Database** and internal storage—if the track is saved locally, it plays directly from `Uri.fromFile(...)` even with no internet connection or airplane mode enabled.
- Dedicated **Downloads** tab organized into **All Songs**, **Movie Albums**, and **Offline Playlists** with live storage telemetry.

### 5. Background & Screen-Off Playback (Media3)
- Built with **AndroidX Media3** (`media3-exoplayer`, `media3-session`, `media3-ui`) using a foreground `MediaSessionService` (`SargamPlaybackService`).
- Configured with `DefaultHttpDataSource.Factory` matching the NewPipe `User-Agent` and `setAllowCrossProtocolRedirects(true)` for reliable streaming, plus `DefaultDataSource.Factory` for transparent local file playback.
- Continues playing smoothly while minimized or with the screen locked, complete with lock-screen and notification controls (Play, Pause, Next, Previous, Seek).
- Includes a sticky **Mini-Player** and a **Full-Screen Now Playing** sheet with seekbar, shuffle, repeat modes, favorite toggle, one-tap download, add-to-playlist, and an **Up Next Queue** sheet.

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 (Edge-to-Edge, Adaptive `NavigationBar` / `NavigationRail`) |
| **Typography** | Bundled Google Fonts (`Syne` + `Plus Jakarta Sans`) |
| **Audio Extraction** | `NewPipeExtractor v0.26.3` (JitPack) + `OkHttp` |
| **Media Playback** | AndroidX Media3 (`ExoPlayer`, `MediaSessionService`, `MediaController`) |
| **Local Persistence** | Room Database with KSP (`DownloadedTrackEntity`, `PlaylistEntity`, `PlaylistTrackEntity`) |
| **Navigation** | Navigation Compose with `@Serializable` type-safe routes |
| **Image Loading** | Coil Compose |
| **Concurrency** | Kotlin Coroutines & `StateFlow` / `Flow` |
| **CI/CD** | GitHub Actions (`gradle/actions/setup-gradle@v4`) |

---

## Project Structure

```text
.
├── .github/workflows/
│   └── build_apk.yml                 # Automated Debug APK build & artifact upload
├── app/
│   ├── build.gradle.kts              # App dependencies, Compose, KSP, Media3, NewPipeExtractor
│   ├── proguard-rules.pro            # Rhino JS & NewPipeExtractor keep rules
│   └── src/main/
│       ├── AndroidManifest.xml       # Permissions & SargamPlaybackService declaration
│       ├── java/com/example/
│       │   ├── SargamApplication.kt  # Initializes NewPipe, OkHttp, Room, Downloader, Player
│       │   ├── MainActivity.kt       # Adaptive Scaffold, NavHost, Mini-Player & Now Playing
│       │   ├── data/
│       │   │   ├── download/         # BatchDownloadManager (internal storage queue)
│       │   │   ├── local/            # SargamDatabase, MusicDao, Room Entities
│       │   │   ├── model/            # Track, MovieAlbum, MoodCollection, CuratedCatalog
│       │   │   ├── remote/           # OkHttpDownloader, NewPipeRepository
│       │   │   └── repository/       # MusicLibraryRepository
│       │   ├── player/               # PlaybackController (offline-first MediaController)
│       │   ├── service/              # SargamPlaybackService (Media3 foreground service)
│       │   └── ui/
│       │       ├── components/       # TrackRowItem, MiniPlayer, FullScreenNowPlaying, Sheets
│       │       ├── navigation/       # Type-safe @Serializable routes
│       │       ├── screens/          # Home, Search, AlbumDetail, MoodDetail, Downloads, Library, PlaylistDetail
│       │       ├── theme/            # Midnight Dark ColorScheme, Typography, Shapes
│       │       └── viewmodel/        # SargamViewModel
│       └── res/                      # Custom adaptive launcher icons, fonts, banners & strings
├── gradle/
│   ├── libs.versions.toml            # Centralized version catalog
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts
└── settings.gradle.kts               # Includes Google, Maven Central, and JitPack
```

---

## Building the APK

### Option 1: GitHub Actions (Automatic CI/CD)
Push this repository to the `main` branch (or trigger the workflow manually from the **Actions** tab):
1. GitHub Actions sets up **JDK 17 (Temurin)** and **Gradle 9.3.1** via `gradle/actions/setup-gradle@v4` (no binary `gradle-wrapper.jar` required).
2. Generates a debug keystore if one isn't present and runs `gradle assembleDebug`.
3. Uploads the compiled APK as the **`sargam-debug-apk`** workflow artifact.

### Option 2: Build Locally
Requires **JDK 17**, **Android SDK**, and **Gradle 9.3.1**:

```bash
gradle assembleDebug
```

The output APK will be located at:
```text
app/build/outputs/apk/debug/app-debug.apk
```

---

## Permissions

- `INTERNET` & `ACCESS_NETWORK_STATE` — Search tracks, load artwork, and stream/download audio.
- `WAKE_LOCK` — Keep audio streaming reliably when the screen turns off.
- `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_MEDIA_PLAYBACK` — Maintain uninterrupted background playback via Media3 `MediaSessionService`.
- `POST_NOTIFICATIONS` — Display lock-screen and notification shade playback controls on Android 13+.

---

## Disclaimer

Sargam is built for personal and educational use. All music metadata and audio streams are retrieved directly from publicly accessible YouTube sources using `NewPipeExtractor`, and offline files are stored strictly inside private internal app storage.
