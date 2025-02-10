package se.westpay.lamusica.ui.search

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.westpay.lamusica.TAG
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess
import se.westpay.lamusica.datalayer.DataBaseEntity
import se.westpay.lamusica.datalayer.SongEntity
import se.westpay.lamusica.repositories.AudioFileMetaData

class SearchViewModel: ViewModel() {
    private val _searchResult = MutableLiveData<TableData>()
    val searchResult: LiveData<TableData> = _searchResult
    private val _audioFileMetaData = MutableLiveData<AudioFileMetaData>()
    val audioFileMetaData: LiveData<AudioFileMetaData> = _audioFileMetaData

    fun doSearch(context: Context, searchString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val artistHeader = listOf<HeaderData>(HeaderData(SearchAdapter.ViewTypes.ARTIST.name))
            val artistItems = AudioDataDatabaseAccess.getArtistByName(context, "$searchString%")
            val albumHeader = listOf<HeaderData>(HeaderData(SearchAdapter.ViewTypes.ALBUM.name))
            val albumItems = AudioDataDatabaseAccess.getAlbumByName(context, "$searchString%")
            val songHeader = listOf<HeaderData>(HeaderData(SearchAdapter.ViewTypes.SONG.name))
            val songItems = AudioDataDatabaseAccess.getSongByName(context, "$searchString%")

            val itemList = concatenate(
                artistHeader,
                artistItems ?: emptyList(),
                albumHeader,
                albumItems ?: emptyList(),
                songHeader,
                songItems ?: emptyList()
            )
            _searchResult.postValue(TableData(itemList))
        }
    }

    fun getArtistAndAlbumForSong(context: Context, song: SongEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val artistName: String = song.songArtistId?.toInt()?.let { artistIdNotNull ->
               val artist = AudioDataDatabaseAccess.getArtistData(context, artistIdNotNull)
                artist.artistName
            } ?: run {
                Log.e(TAG, "Failed to get artist!")
                ""
            }

            val albumName = song.songAlbumId?.toInt()?.let { albumIdNotNull ->
                val album = AudioDataDatabaseAccess.getAlbumData(context, albumIdNotNull)
                album.albumName
            } ?: run {
                Log.e(TAG, "Failed to get album!")
                ""
            }

            val audioFileMetaData = AudioFileMetaData(
                songUri = Uri.parse(song.songUri),
                title = song.songName ?: "",
                album = albumName,
                artist = artistName,
                genre = "",
                year = song.timestampDate.toString() ?: "",
                format = "",
                duration = song.duration ?: "",
                resolution = "",
                size = song.size ?: 0,
                bitmap = null,
                albumUri = Uri.parse(song.albumUri)
            )
            _audioFileMetaData.postValue(audioFileMetaData)
        }
    }
}

fun <DataBaseEntity> concatenate(vararg lists: List<DataBaseEntity>): List<DataBaseEntity> {
    return listOf(*lists).flatten()
}

data class TableData(
    val itemList: List<DataBaseEntity>
)

data class HeaderData(val title: String) : DataBaseEntity