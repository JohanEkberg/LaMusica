package se.westpay.lamusica

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    @Test
    fun getAlbumSongs() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val items = AudioDataDatabaseAccess.getAlbumSongsData(context, 1659)
            Log.d("DatabaseTest", items.toString())
            assertTrue(items != null)
        }
    }

    @Test
    fun getSearchArtists() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val items = AudioDataDatabaseAccess.getArtistByName(context, "Ali%")
                items?.forEach { item ->
                    Log.i("DatabaseTest", "${item.artistName}")
                }
                delay(10000)
                Log.i("DatabaseTest", "Done!")
                assertTrue(items != null)
            } catch(e: Exception) {
                Log.e("DatabaseTest", "Exception: ${e.message}")
            }
        }
    }

    @Test
    fun getArtists() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val items = AudioDataDatabaseAccess.getArtistsData(context)
                items.forEach { item ->
                    Log.i("DatabaseTest", "Id = ${item.artistId} Name = ${item.artistName}")
                }
                delay(10000)
                Log.i("DatabaseTest", "Done!")
                assertTrue(items.isNotEmpty())
            } catch(e: Exception) {
                Log.e("DatabaseTest", "Exception: ${e.message}")
            }
        }
    }

    @Test
    fun getArtist() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val item = AudioDataDatabaseAccess.getArtistData(context, 606)
                Log.i("DatabaseTest", "Id = ${item.artistId} Name = ${item.artistName}")
                delay(10000)
                Log.i("DatabaseTest", "Done!")
                assertTrue(item.artistName?.isNotEmpty() == true)
            } catch(e: Exception) {
                Log.e("DatabaseTest", "Exception: ${e.message}")
            }
        }
    }

    @Test
    fun getArtistSongs() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val item = AudioDataDatabaseAccess.getArtistWithSongsData(context, 606)
                Log.i("DatabaseTest", "Artist: Id = ${item?.artistEntity?.artistId} Name = ${item?.artistEntity?.artistName}")
                item?.songList?.forEach { itemNotNull ->
                    Log.i("DatabaseTest", "Song = ${itemNotNull.songName}")
                }
                delay(10000)
                Log.i("DatabaseTest", "Done!")
                assertTrue(item?.artistEntity?.artistName?.isNotEmpty() == true)
            } catch(e: Exception) {
                Log.e("DatabaseTest", "Exception: ${e.message}")
            }
        }
    }

    @Test
    fun getArtistAlbums() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val item = AudioDataDatabaseAccess.getArtistWithAlbumsData(context, 606)
                Log.i("DatabaseTest", "Artist: Id = ${item?.artistEntity?.artistId} Name = ${item?.artistEntity?.artistName}")
                item?.songList?.forEach { itemNotNull ->
                    Log.i("DatabaseTest", "Album id = ${itemNotNull.album.albumId} ${itemNotNull.album.albumName} - ${itemNotNull.songEntity.songName}")
                }
                delay(10000)
                Log.i("DatabaseTest", "Done!")
                assertTrue(item?.songList?.isNotEmpty() == true)
            } catch(e: Exception) {
                Log.e("DatabaseTest", "Exception: ${e.message}")
            }
        }
    }

    @Test
    fun getAllSongs() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val items = AudioDataDatabaseAccess.getAllSongs(context)
                items?.forEach { itemNotNull ->
                    Log.i("DatabaseTest", "Name = ${itemNotNull.songName} Artist id =  ${itemNotNull.songArtistId}")
                }
                delay(10000)
                Log.i("DatabaseTest", "Done!")
                assertTrue(items?.isNotEmpty() == true)
            } catch(e: Exception) {
                Log.e("DatabaseTest", "Exception: ${e.message}")
            }
        }
    }

    @Test
    fun getSongs() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val items = AudioDataDatabaseAccess.getSongByName(context, "a%")
                items?.forEach { itemNotNull ->
                    val artist = AudioDataDatabaseAccess.getArtistData(context, itemNotNull.songArtistId?.toInt() ?: 0)
                    val album = AudioDataDatabaseAccess.getAlbumData(context, itemNotNull.songAlbumId?.toInt() ?: 0)
                    Log.i("DatabaseTest", "Name = ${itemNotNull.songName} Artist = ${artist.artistName} Id = ${artist.artistId} Album = ${album.albumName} Id = ${album.albumId}")
                }

                delay(10000)
                Log.i("DatabaseTest", "Done!")
                assertTrue(items?.isNotEmpty() == true)
            } catch(e: Exception) {
                Log.e("DatabaseTest", "Exception: ${e.message}")
            }
        }
    }
}