package se.westpay.lamusica.lyricsservice

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import se.westpay.lamusica.lyricsservice.LyricsApiQueryParameters.ARTIST_KEY
import se.westpay.lamusica.lyricsservice.LyricsApiQueryParameters.ARTIST_NAME_KEY
import se.westpay.lamusica.lyricsservice.LyricsApiQueryParameters.TITLE_KEY
import se.westpay.lamusica.lyricsservice.LyricsApiQueryParameters.TRACK_NAME_KEY

interface ILyricsApi {
    @Headers("Accept: plain/text")
    @GET("v1/{$ARTIST_KEY}/{$TITLE_KEY}")
    fun requestSongLyric(@Path(ARTIST_KEY) artist: String, @Path(TITLE_KEY) title: String): Call<LyricsResponse>

    @Headers("Accept: plain/text")
    @GET("api/get")
    fun requestSongLyricWithTimestamp(
        @Header("User-Agent") userAgent: String,
        @Query(ARTIST_NAME_KEY) artist: String, @Query(TRACK_NAME_KEY) title: String
    ): Call<LyricsWithTimestampResponse>
}

data class LyricsResponse(
    @SerializedName("lyrics") val lyrics: String
)

data class LyricsWithTimestampResponse(
    @SerializedName("id") val id: String,
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("albumName") val albumName: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("instrumental") val instrumental: Boolean,
    @SerializedName("plainLyrics") val plainLyrics: String,
    @SerializedName("syncedLyrics") val syncedLyrics: String,
)