package com.example.data.remote

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

        val requestBody = if (dataToSend != null) {
            dataToSend.toRequestBody(null)
        } else if (httpMethod.equals("POST", ignoreCase = true) ||
            httpMethod.equals("PUT", ignoreCase = true)
        ) {
            ByteArray(0).toRequestBody(null)
        } else {
            null
        }

        val requestBuilder = okhttp3.Request.Builder()
            .method(httpMethod, requestBody)
            .url(url)
            .addHeader("User-Agent", USER_AGENT)

        if (url.contains("youtube.com")) {
            requestBuilder.addHeader("Cookie", YOUTUBE_CONSENT_COOKIE)
        }

        for ((headerName, headerValues) in headers) {
            if (headerValues.size > 1) {
                requestBuilder.removeHeader(headerName)
                for (headerValue in headerValues) {
                    requestBuilder.addHeader(headerName, headerValue)
                }
            } else if (headerValues.size == 1) {
                requestBuilder.header(headerName, headerValues[0])
            }
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            if (response.code == 429) {
                throw ReCaptchaException("reCaptcha Challenge requested", url)
            }

            val responseBodyString = response.body?.string().orEmpty()
            val latestUrl = response.request.url.toString()

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
