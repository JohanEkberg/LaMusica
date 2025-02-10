package se.westpay.lamusica.lyricsservice

interface ILyricsService {
    suspend fun getLyrics(artist: String, title: String) : String
}