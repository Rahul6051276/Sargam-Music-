package com.example.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import java.io.IOException

class OkHttpDownloader(
    private val client: OkHttpClient
) : Downloader() {

    companion object {
        private const val TAG = "OkHttpDownloader"

        // Shared User-Agent used identically by both NewPipeExtractor and ExoPlayer DefaultHttpDataSource
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0"
        private const val YOUTUBE_CONSENT_COOKIE = "SOCS=CAI"
    }

    @Throws(IOException::class, ReCaptchaException::class)
    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        val requestBody = when {
            dataToSend != null -> dataToSend.toRequestBody(null)
            httpMethod.equals("POST", ignoreCase = true) ||
                httpMethod.equals("PUT", ignoreCase = true) -> ByteArray(0).toRequestBody(null)
            else -> null
        }

        val requestBuilder = okhttp3.Request.Builder()
            .method(httpMethod, requestBody)
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", "en-US,en;q=0.9")

        var hasCookieHeader = false
        for ((headerName, headerValues) in headers) {
            if (headerName.equals("Cookie", ignoreCase = true)) {
                hasCookieHeader = true
            }
            if (headerValues.size > 1) {
                requestBuilder.removeHeader(headerName)
                for (headerValue in headerValues) {
                    requestBuilder.addHeader(headerName, headerValue)
                }
            } else if (headerValues.size == 1) {
                requestBuilder.header(headerName, headerValues[0])
            }
        }

        if (!hasCookieHeader && url.contains("youtube.com", ignoreCase = true)) {
            requestBuilder.header("Cookie", YOUTUBE_CONSENT_COOKIE)
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            if (response.code == 429) {
                Log.w(TAG, "HTTP 429 ReCaptcha challenge for $url")
                throw ReCaptchaException("reCaptcha Challenge requested", url)
            }

            val responseBodyString = response.body?.string().orEmpty()
            val latestUrl = response.request.url.toString()

            if (!response.isSuccessful) {
                Log.w(TAG, "HTTP ${response.code} ${response.message} for $httpMethod $url")
            }

            return Response(
                response.code,
                response.message,
                response.headers.toMultimap(),
                responseBodyString,
                latestUrl
            )
        }
    }
}
