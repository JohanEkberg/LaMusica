package se.westpay.lamusica.ui.categoryartist

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import se.westpay.lamusica.datalayer.AudioDataDatabaseAccess

class ArtistsViewModel : ViewModel() {
    fun populateArtists(context: Context, artistsAdapter: ArtistsAdapter) {
        CoroutineScope(Dispatchers.IO).launch {
//        viewModelScope.launch {
            val items = AudioDataDatabaseAccess.getPagedArtistWithSongsData(context)
            items?.let { itemsNotNull ->
                itemsNotNull.collectLatest { pagingData ->
                    artistsAdapter.submitData(pagingData)
                }
            }
        }
    }
}