package se.westpay.lamusica

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import se.westpay.lamusica.lyricsservice.LyricsService

@RunWith(AndroidJUnit4::class)
class LyricsApiTest {

    @Test
    fun getLyricsTest() {
        runBlocking {
            val lyrics = LyricsService.getLyrics("abba", "dancing queen")
            Log.d("getLyricsTest", lyrics)
            assertTrue(lyrics.isNotEmpty())
        }
    }

    @Test
    fun getLyricsWithTimestampTest() {
        runBlocking {
            val lyrics = LyricsService.getLyricsWithTimestamp("abba", "dancing queen")
            Log.d("getLyricsWithTimestampTest", "${lyrics?.syncedLyrics} ${lyrics?.plainLyrics}  ${lyrics?.duration}")
            assertTrue(lyrics?.syncedLyrics?.isNotEmpty() == true && lyrics.duration != 0)
        }
    }
}