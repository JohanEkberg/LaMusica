package se.westpay.lamusica.datalayer

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.westpay.lamusica.TAG
import se.westpay.lamusica.repositories.AudioFileMetaData
import java.util.*

object AudioDataDatabaseAccess {

    fun existDataBase(context: Context) : Boolean = AppDatabase.isValidFile(context)

    suspend fun storeArtistData(context: Context, audioData: AudioFileMetaData) : Long {
        return withContext(Dispatchers.IO) {
            var id = -1L
            try {
                val db = AppDatabase.getInstance(context)
                val artistEntity = ArtistEntity(
                    artistName = audioData.artist
                )
                id = db.audioDataDao().insert(artistEntity)
                Log.i(TAG, "Artist id ${id} stored in database")
            } catch(e: Exception) {
                Log.e(TAG, "Exception when storing data to the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            id
        }
    }

    suspend fun storeAlbumData(context: Context, audioData: AudioFileMetaData) : Long {
        return withContext(Dispatchers.IO) {
            var id = -1L
            try {
                val db = AppDatabase.getInstance(context)
                val albumEntity = AlbumEntity(
                    albumName = audioData.album.replace("\\s+".toRegex(), " "),
                    albumUri = audioData.albumUri.toString(),
                    genre = audioData.genre,
                    year = audioData.year
                )
                id = db.audioDataDao().insert(albumEntity)
                Log.i(TAG, "Album id ${id} stored in database")
            } catch(e: Exception) {
                Log.e(TAG, "Exception when storing data to the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            id
        }
    }

    fun storeSongData(context: Context, audioData: AudioFileMetaData, artistId: Long, albumId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val audioDataEntity = SongEntity(
                    timestampDate = Date(),
                    songArtistId = artistId,
                    songAlbumId = albumId,
                    songName = audioData.title,
                    songUri = audioData.songUri.toString(),
                    albumUri = audioData.albumUri.toString(),
                    format = audioData.format,
                    duration = audioData.duration,
                    resolution = audioData.resolution,
                    size = audioData.size
                )
                db.audioDataDao().insert(audioDataEntity)
                //Log.i(TAG, "Song id ${audioDataEntity.songId} stored in database")
            } catch(e: Exception) {
                Log.e(TAG, "Exception when storing data to the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
        }
    }


//    fun storeAudioData(context: Context, audioData: AudioFileMetaData) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val db = AppDatabase.getInstance(context)
//
//                val audioDataEntity = AudioDataEntity(
//                    timestampDate = Date(),
//                    artistName = audioData.artist,
//                    albumName = audioData.album,
//                    songName = audioData.title,
//                    filePath = audioData.path,
//                    genre = audioData.genre,
//                    year = audioData.year,
//                    format = audioData.format,
//                    duration = audioData.duration,
//                    resolution = audioData.resolution,
//                    size = audioData.size
//                )
//                db.audioDataDao().insert(audioDataEntity)
//                Log.i(TAG, "Audio data ${audioDataEntity.id} stored in database")
//            } catch(e: Exception) {
//                Log.e(TAG, "Exception when storing data to the database: ${e.message}")
//                AppDatabase.destroyInstance()
//            }
//        }
//    }

    /**
     * TODO: Add paging library 3 to handle large data in recycleview.
     */
    suspend fun getPagedAlbumData(context: Context,) : Flow<PagingData<AlbumEntity>>? {
        return withContext(Dispatchers.IO) {
            var items: Flow<PagingData<AlbumEntity>>? = null
            try {
                val db = AppDatabase.getInstance(context)
                items = Pager(
                    PagingConfig(
                        pageSize = 10,
                        enablePlaceholders = true,
                        maxSize = 200)
                ) {
                    db.audioDataDao().getPagedAlbums()
                }.flow
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            items
        }
    }

    suspend fun getPagedArtistWithSongsData(context: Context) : Flow<PagingData<ArtistWithSongs>>? {
        return withContext(Dispatchers.IO) {
            var items: Flow<PagingData<ArtistWithSongs>>? = null
            try {
                val db = AppDatabase.getInstance(context)
                items = Pager(
                        PagingConfig(
                                pageSize = 10,
                                enablePlaceholders = true,
                                maxSize = 200)
                ) {
                    db.audioDataDao().getPagedArtistsWithSongs()
                }.flow
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            items
        }
    }

//    suspend fun getPagedAlbumWithSongsData(context: Context) : Flow<PagingData<AlbumWithSongs>>? {
//        return withContext(Dispatchers.IO) {
//            var items: Flow<PagingData<AlbumWithSongs>>? = null
//            try {
//                val db = AppDatabase.getInstance(context)
//                items = Pager(
//                    PagingConfig(
//                        pageSize = 10,
//                        enablePlaceholders = true,
//                        maxSize = 200)
//                ) {
//                    db.audioDataDao().getPagedAlbumWithSongs()
//                }.flow
//            } catch(e: Exception) {
//                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
//                AppDatabase.destroyInstance()
//            }
//            items
//        }
//    }

    suspend fun getPagedAlbumWithSongsData(context: Context) : Flow<PagingData<AlbumWithSongs>>? {
        return withContext(Dispatchers.IO) {
            var items: Flow<PagingData<AlbumWithSongs>>? = null
            try {
                val db = AppDatabase.getInstance(context)
                items = Pager(
                        PagingConfig(
                                pageSize = 10,
                                enablePlaceholders = true,
                                maxSize = 200)
                ) {
                    db.audioDataDao().getPagedAlbumWithSongs()
                }.flow
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            items
        }
    }

    suspend fun getArtistsData(context: Context) : List<ArtistEntity> {
        return withContext(Dispatchers.IO) {
            var item: List<ArtistEntity>? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getArtists()
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item ?: listOf(ArtistEntity(0, ""))
        }
    }

    suspend fun getArtistData(context: Context, artistId: Int) : ArtistEntity {
        return withContext(Dispatchers.IO) {
            var item: ArtistEntity? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getArtistById(artistId)
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item ?: ArtistEntity(0, "")
        }
    }

    suspend fun getArtistByName(context: Context, artistName: String) : List<ArtistEntity>? {
        return withContext(Dispatchers.IO) {
            var item: List<ArtistEntity>? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getArtistByName(artistName)
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item
        }
    }

    suspend fun getArtistWithSongsData(context: Context, artistId: Long) : ArtistWithSongs? {
        return withContext(Dispatchers.IO) {
            var items: ArtistWithSongs? = null
            try {
                val db = AppDatabase.getInstance(context)
                items = db.audioDataDao().getArtistWithSongs(artistId)
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            items
        }
    }

    suspend fun getArtistWithAlbumsData(context: Context, artistId: Long) : ArtistWithAlbums? {
        return withContext(Dispatchers.IO) {
            var item: ArtistWithAlbums? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getArtistWithAlbums(artistId)
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item
        }
    }

    suspend fun getAlbumSongsData(context: Context, albumId: Long) : AlbumWithSongs? {
        return withContext(Dispatchers.IO) {
            var item: AlbumWithSongs? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getAlbumWithSongs(albumId)
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item
        }
    }

    suspend fun getAlbumData(context: Context, albumId: Int) : AlbumEntity {
        return withContext(Dispatchers.IO) {
            var item: AlbumEntity? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getAlbumById(albumId)
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item ?: AlbumEntity(0, "", "", "", "")
        }
    }

    suspend fun getAlbumByName(context: Context, albumName: String) : List<AlbumEntity>? {
        return withContext(Dispatchers.IO) {
            var item: List<AlbumEntity>? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getAlbumByName(albumName)
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item
        }
    }

    suspend fun getSongByName(context: Context, songName: String) : List<SongEntity>? {
        return withContext(Dispatchers.IO) {
            var item: List<SongEntity>? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getSongByName(songName)
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item
        }
    }

    suspend fun getAllSongs(context: Context) : List<SongEntity>? {
        return withContext(Dispatchers.IO) {
            var item: List<SongEntity>? = null
            try {
                val db = AppDatabase.getInstance(context)
                item = db.audioDataDao().getAllSongs()
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
            }
            item
        }
    }


    suspend fun deleteAllDataInDatabase(context: Context) : Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val db = AppDatabase.getInstance(context)
                db.audioDataDao().deleteAllData()
                true
            } catch(e: Exception) {
                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
                AppDatabase.destroyInstance()
                false
            }
        }
    }

    /**
     * Clear all tables will not reset the primary keys.
     */
    suspend fun clearAllTables(context: Context) {
        return withContext(Dispatchers.IO) {
            try {
                val db = AppDatabase.getInstance(context)
                db.clearAllTables()
            } catch(e: Exception) {
                Log.e(TAG, "Exception when clear all tables: ${e.message}")
                AppDatabase.destroyInstance()
            }
        }
    }

//    suspend fun getAllAudioData(context: Context) : MutableList<AudioFileMetaData> {
//        return withContext(Dispatchers.IO) {
//            val audioFileMetaDataList = mutableListOf<AudioFileMetaData>()
//            try {
//                val db = AppDatabase.getInstance(context)
//                var artistEntityList = db.audioDataDao().getAll()
//                for (value in artistEntityList) {
//                    audioFileMetaDataList.add(
//                            AudioFileMetaData(
//                                    path = value.filePath ?: "",
//                                    title = value.songName ?: "",
//                                    album = value.albumName ?: "",
//                                    artist = value.artistName ?: "",
//                                    genre = value.genre ?: "",
//                                    year = value.year ?: "",
//                                    format = value.format ?: "",
//                                    duration = value.duration ?: "",
//                                    resolution = value.resolution ?: "",
//                                    size = value.size ?: 0
//                            )
//                    )
//                }
//            } catch(e: Exception) {
//                Log.e(TAG, "Exception when retrieving data from the database: ${e.message}")
//                AppDatabase.destroyInstance()
//            }
//            audioFileMetaDataList
//        }
//    }
}