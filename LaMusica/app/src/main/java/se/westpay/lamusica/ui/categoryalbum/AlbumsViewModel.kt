package se.westpay.lamusica.ui.categoryalbum

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess

class AlbumsViewModel : ViewModel() {

    fun populateAlbums(context: Context, albumsAdapter: AlbumsAdapter) {
        CoroutineScope(Dispatchers.IO).launch {
            val items = AudioDataDatabaseAccess.getPagedAlbumWithSongsData(context)
            items?.let { itemsNotNull ->
                itemsNotNull.collectLatest { pagingData ->
                    albumsAdapter.submitData(pagingData)
                }
            }
        }
    }
}