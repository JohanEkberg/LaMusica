package se.westpay.lamusica.lyricsservice

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import se.westpay.lamusica.lyricsservice.LyricsApiQueryParameters.ARTIST_KEY
import se.westpay.lamusica.lyricsservice.LyricsApiQueryParameters.TITLE_KEY

interface ILyricsApi {
    @Headers("Accept: plain/text")
    @GET("v1/{$ARTIST_KEY}/{$TITLE_KEY}")
    fun requestSongLyric(@Path(ARTIST_KEY) artist: String, @Path(TITLE_KEY) title: String): Call<LyricsResponse>

    // TODO: Add API to get lyrics with timestamp
    @Headers("Accept: plain/text")
    @GET("v1/{$ARTIST_KEY}/{$TITLE_KEY}")
    fun requestSongLyricWithTimestamp(@Path(ARTIST_KEY) artist: String, @Path(TITLE_KEY) title: String): Call<LyricsResponse>
}

data class LyricsResponse(
    @SerializedName("lyrics") val lyrics: String
)