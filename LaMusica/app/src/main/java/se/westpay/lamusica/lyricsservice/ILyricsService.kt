package se.westpay.lamusica.lyricsservice

interface ILyricsService {
    suspend fun getLyrics(artist: String, title: String) : String
    suspend fun getLyricsWithTimestamp(artist: String, title: String) : LyricsWithTimestamp?
}

data class LyricsWithTimestamp(var plainLyrics: String, var syncedLyrics: String, var duration: Int)