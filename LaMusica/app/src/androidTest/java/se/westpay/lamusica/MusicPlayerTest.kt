package se.westpay.lamusica

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import se.westpay.lamusica.audio.MusicPlayer
import se.westpay.lamusica.repositories.AudioFileMetaData

@RunWith(AndroidJUnit4::class)
class MusicPlayerTest {

    private fun getRawResourceUri(context: Context, resId: Int): Uri {
        return Uri.parse("android.resource://${context.packageName}/$resId")
    }

    private fun createQueueItem(index: Int, uri: Uri = Uri.parse("content://media/external/audio/media/1000034682")) : AudioFileMetaData {
        return AudioFileMetaData(
            songUri = uri,
            title = "Title-${index}",
            album = "Album-${index}",
            artist = "artist-${index}",
            genre = "genre-${index}",
            year = "year-${index}",
            format = "format-${index}",
            duration = "duration-${index}",
            resolution = "resolution-${index}",
            size =  index.toLong(),
            bitmap = null,
            albumUri = Uri.parse("content://media/external/audio/albums/2115860290831367398")
        )
    }

    @Test
    fun addToPlayerQueueTest() {
        runBlocking {
            var success = false
            MusicPlayer.addSongChangeCallback { song ->
                Log.d("addToPlayerQueueTest", "Song changed callback: ${song?.title} ${song?.artist}")
                success = true
            }
            MusicPlayer.addToPlayerQueue(createQueueItem(0))
            delay(2000)
            if (success) {
                success = MusicPlayer.getPlayerQueue().size == 1
            }
            assertTrue(success)
        }
    }

    @Test
    fun playSongFromQueueTest() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                var success = false
                MusicPlayer.addSongChangeCallback { song ->
                    Log.d("playSongFromQueueTest", "Song changed callback: ${song?.title} ${song?.artist}")
                    success = true
                }
                val uri: Uri = getRawResourceUri(context, R.raw.test_song)
                MusicPlayer.addToPlayerQueue(createQueueItem(0, uri))
                MusicPlayer.addProgressCallback { progress ->
                    Log.d("playSongFromQueueTest", "Song progress callback: ${progress}")
                    success = true
                }
                delay(2000)
                MusicPlayer.play(context)
                delay(5000)
                MusicPlayer.pause()
                MusicPlayer.stop()
                assertTrue(success)
            } catch (e: Exception) {
                Log.e("playSongFromQueueTest", "Exception: ${e.message}")
                assertTrue(false)
            }



        }
    }
}