package se.westpay.lamusica

import android.net.Uri
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import se.westpay.lamusica.audio.SongQueue
import se.westpay.lamusica.repositories.AudioFileMetaData

@RunWith(AndroidJUnit4::class)
class SongQueueTest {

    private fun createQueueItem(index: Int) : AudioFileMetaData {
        return AudioFileMetaData(
            songUri = Uri.parse("content://media/external/audio/media/1000034682"),
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

    private fun createQueueItems() : SongQueue {
        val queue = SongQueue()
        for (i in 0..5) {
            queue.addQueueItem(createQueueItem(i))
        }
        return queue
    }

    @Test
    fun getQueueItemTest() {
        runBlocking {
            val item = createQueueItems().getQueueItem()
            val result = item?.title.equals("Title-0")
            Log.d("getQueueItemTest", "Result = ${result}")
            assertTrue(result)
        }
    }

    @Test
    fun getQueueItemsTest() {
        runBlocking {
            val queue = createQueueItems()
            val copyOfQueue = queue.getQueueItems()
            val result = queue.isEqual(copyOfQueue)
            Log.d("getQueueItemsTest", "Result = ${result}")
            assertTrue(result)
        }
    }

    @Test
    fun removeQueueItemTest() {
        runBlocking {
            val queue = createQueueItems()
            queue.removeQueueItem(createQueueItem(2))
            val removedItem = createQueueItem(2)
            val result = removedItem in queue.getQueueItems()
            Log.d("removeQueueItemTest", "Result = ${result}")
            assertTrue(!result)
        }
    }

    @Test
    fun clearQueueTest() {
        runBlocking {
            val queue = createQueueItems()
            if (!queue.clearQueue()) {
                assertTrue(false)
            }
            Log.d("clearQueueTest", "Result = ${queue.isEmpty()}")
            assertTrue(queue.isEmpty())
        }
    }

    @Test
    fun queueSizeTest() {
        runBlocking {
            assertTrue(createQueueItems().queueSize() == 6)
        }
    }
}