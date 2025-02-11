package se.westpay.lamusica.audio

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import se.westpay.lamusica.TAG
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess
import se.westpay.lamusica.repositories.AudioFileMetaData


class AudioScanner {
    //private val extension = arrayOf("mp3", "flac")

    private fun checkURIResource(resolver: ContentResolver, contentUri: Uri?): Boolean {
        val cursor: Cursor? = resolver.query(contentUri!!, null, null, null, null)
        val doesExist = cursor != null && cursor.moveToFirst()
        cursor?.close()
        return doesExist
    }

    suspend fun scanAudioFiles(context: Context) : Boolean {
        var success = true
        try {
            val collection =
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val query =  context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                null
            )

            query?.use { cursor ->
                if (!iterateMediaStore(
                        context = context,
                        cursor = cursor,
                        columnIndices = getCacheColumnIndices(cursor)
                )) {
                    Log.e(TAG, "Scan media store failed")
                }
            }
        } catch(e: Exception) {
            Log.e(TAG, "Scan media files failed, exception: ${e.message}, stacktrace: ${Log.getStackTraceString(e)}")
            success = false
        }
        return success
    }

    private fun getCacheColumnIndices(cursor: Cursor) : CacheColumnIndices? {
        return try {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val yearColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            CacheColumnIndices(
                idColumn = idColumn,
                artistColumn = artistColumn,
                albumColumn = albumColumn,
                titleColumn = titleColumn,
                yearColumn =  yearColumn,
                durationColumn = durationColumn,
                sizeColumn = sizeColumn,
                albumIdColumn = albumIdColumn
            )
        } catch(e: Exception) {
            Log.e(TAG, "Failed to get cache column indices, exception: ${e.message}")
            null
        }
    }

    private suspend fun iterateMediaStore(context: Context, cursor: Cursor, columnIndices: CacheColumnIndices?) : Boolean {
        return try {
            if (columnIndices == null) {
                Log.e(TAG, "Failed to iterate media store, no column indices provided")
                return false
            }

            var latestAlbumId: Long = -1
            val albumList = mutableListOf<AlbumItem>()
            var latestArtistId: Long = -1
            val artistList = mutableListOf<ArtistItem>()

            loop@while (cursor.moveToNext()) {
                // Get values of columns for a given audio file.
                val songId = cursor.getLong(columnIndices.idColumn)
                val artist = cursor.getString(columnIndices.artistColumn)
                val album = cursor.getString(columnIndices.albumColumn)
                val title = cursor.getString(columnIndices.titleColumn)
                val year = cursor.getString(columnIndices.yearColumn) ?: ""
                val duration = cursor.getString(columnIndices.durationColumn) ?: ""
                val size = cursor.getInt(columnIndices.sizeColumn)
                val albumId = cursor.getLong(columnIndices.albumIdColumn)

                val albumUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    albumId
                )
                val artworkExist = checkURIResource(context.contentResolver, albumUri)

                val songUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    songId
                )

                Log.d(TAG,
                    "Artist=${artist} " +
                            "Album=${album} " +
                            "Title=${title} " +
                            "Year=${year} " +
                            "Duration=${duration} " +
                            "Size=${size} " +
                            "AlbumUri=${albumUri} " +
                            "SongUri=${songUri} " +
                            "Artwork exist=${artworkExist}")

                if (artist.isNullOrEmpty() ||
                    artist.equals("<unknown>") ||
                    album.isNullOrEmpty() ||
                    title.isNullOrEmpty() ||
                    size <= 0) {
                    Log.i(
                        TAG,
                        "Skip item: " +
                                "Artist=${artist} " +
                                "Album=${album} " +
                                "Title=${title} " +
                                "Size=${size}"
                    )
                    continue@loop
                }

                /**
                 * Store data in the album database table if it is a new album.
                 */
                val audioFileMetaData = AudioFileMetaData(
                    songUri = songUri,
                    title = title,
                    album = album,
                    artist = artist,
                    genre = "",
                    year = year,
                    format = "",
                    duration = duration,
                    resolution = "",
                    size = size.toLong(),
                    bitmap = null,
                    albumUri = if (artworkExist) { albumUri } else { Uri.parse("") }
                )

                val currentAlbum = albumList.find { item -> album.equals(item.value, ignoreCase = true) }
                if (currentAlbum == null) {
                    latestAlbumId = AudioDataDatabaseAccess.storeAlbumData(context, audioFileMetaData)
                    albumList.add(AlbumItem(value = album, id = latestAlbumId))
                } else {
                    latestAlbumId = currentAlbum.id
                }

                val currentArtist = artistList.find { item -> artist.equals(item.value, ignoreCase = true) }
                if (currentArtist == null) {
                    latestArtistId = AudioDataDatabaseAccess.storeArtistData(context, audioFileMetaData)
                    artistList.add(ArtistItem(value = artist, id = latestArtistId))
                } else {
                    latestArtistId = currentArtist.id
                }

                //AudioDataDatabaseAccess.storeSongData(context, audioFileMetaData, latestAddedArtist.id, latestAddedAlbum.id)
                AudioDataDatabaseAccess.storeSongData(
                    context,
                    audioFileMetaData,
                    artistId = latestArtistId,
                    albumId = latestAlbumId
                )
            }
            Log.d(TAG, "Total processed songs: ${albumList.size}")
            cursor.close()
            true
        } catch(e: Exception) {
            Log.e(TAG, "Failed to iterate media store, exception: ${e.message}")
            false
        }
    }
}

data class AlbumItem(var value: String = "", var id: Long = -1)
data class ArtistItem(var value: String = "", var id: Long = -1)
data class CacheColumnIndices(
    val idColumn: Int,
    val artistColumn: Int,
    val albumColumn: Int,
    val titleColumn: Int,
    val yearColumn: Int,
    val durationColumn: Int,
    val sizeColumn: Int,
    val albumIdColumn: Int
)
