package se.westpay.lamusica.ui.categoryalbum

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import se.westpay.lamusica.datalayer.AlbumWithSongs
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess

class AlbumViewModel : ViewModel() {

    private val _albumWithSongs = MutableLiveData<AlbumWithSongs>()
    val albumWithSongs: LiveData<AlbumWithSongs> = _albumWithSongs

    fun getAlbumSongsById(context: Context, albumId: Int) {
        viewModelScope.launch {
            _albumWithSongs.value = AudioDataDatabaseAccess.getAlbumSongsData(context, albumId.toLong())
        }
    }
}