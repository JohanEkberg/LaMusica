package se.westpay.lamusica.lyricsservice

import android.util.Log
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import se.westpay.lamusica.BuildConfig
import se.westpay.lamusica.TAG
import se.westpay.lamusica.lyricsservice.LyricsApiEndPoints.BASE_URL
import se.westpay.lamusica.lyricsservice.LyricsApiEndPoints.BASE_URL_LRC_LIB_API

object LyricsService : ILyricsService {
    private const val VERSION_STRING = "LaMusica/${BuildConfig.VERSION_NAME}"

    private val instance: ILyricsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_LRC_LIB_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ILyricsApi::class.java)
    }

    override suspend fun getLyrics(artist: String, title: String): String {
        return try {
            val api = instance.requestSongLyric(artist, title).awaitResponse()
            if (api.isSuccessful) {
                api.body()?.lyrics ?: run {
                    Log.e(TAG, "Failed to get lyrics, empty body")
                    ""
                }
            } else {
                Log.e(TAG, "Failed to get lyrics, API failure")
                ""
            }
        } catch(e: Exception) {
            Log.e(TAG, "Failed to get lyrics, exception: ${e.message}")
            ""
        }
    }

    override suspend fun getLyricsWithTimestamp(artist: String, title: String): LyricsWithTimestamp? {
        return try {
            val api = instance.requestSongLyricWithTimestamp(userAgent = VERSION_STRING, artist = artist, title = title).awaitResponse()
            if (api.isSuccessful) {
                api.body()?.let {
                    LyricsWithTimestamp(it.plainLyrics, it.syncedLyrics, it.duration)
                }
            } else {
                null
            }
        } catch(e: Exception) {
            Log.e(TAG, "Failed to get lyrics with timestamp, exception: ${e.message}")
            null
        }
    }
}


