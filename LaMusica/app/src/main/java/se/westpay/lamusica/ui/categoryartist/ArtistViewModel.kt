package se.westpay.lamusica.ui.categoryartist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import se.westpay.lamusica.datalayer.ArtistWithAlbums
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess

class ArtistViewModel : ViewModel() {

    private val _artistWithAlbums = MutableLiveData<ArtistWithAlbums>()
    val artistWithAlbums: LiveData<ArtistWithAlbums> = _artistWithAlbums

    fun getAlbumByArtistId(context: Context, artistId: Long) {
        viewModelScope.launch {
            _artistWithAlbums.value = AudioDataDatabaseAccess.getArtistWithAlbumsData(context, artistId)
        }
    }
}