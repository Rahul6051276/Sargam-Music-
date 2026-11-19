package com.example

import android.app.Application
import com.example.data.download.BatchDownloadManager
import com.example.data.local.SargamDatabase
import com.example.data.remote.NewPipeRepository
import com.example.data.remote.OkHttpDownloader
import com.example.data.repository.MusicLibraryRepository
import com.example.player.PlaybackController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import java.util.concurrent.TimeUnit

class SargamApplication : Application() {

    lateinit var okHttpClient: OkHttpClient
        private set
    lateinit var database: SargamDatabase
        private set
    lateinit var newPipeRepository: NewPipeRepository
        private set
    lateinit var libraryRepository: MusicLibraryRepository
        private set
    lateinit var batchDownloadManager: BatchDownloadManager
        private set
    lateinit var playbackController: PlaybackController
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        // Initialize NewPipeExtractor with Indian content localization for rich Bollywood/Hindi catalog
        NewPipe.init(
            OkHttpDownloader(okHttpClient),
            Localization("en", "IN"),
            ContentCountry("IN")
        )

        database = SargamDatabase.getInstance(this)
        newPipeRepository = NewPipeRepository()
        libraryRepository = MusicLibraryRepository(database.musicDao())
        batchDownloadManager = BatchDownloadManager(
            context = this,
            okHttpClient = okHttpClient,
            newPipeRepository = newPipeRepository,
            libraryRepository = libraryRepository
        )
        playbackController = PlaybackController(
            context = this,
            newPipeRepository = newPipeRepository,
            libraryRepository = libraryRepository
        )
        playbackController.connect()

        appScope.launch {
            libraryRepository.ensureDefaultPlaylistsExist()
        }
    }
}
