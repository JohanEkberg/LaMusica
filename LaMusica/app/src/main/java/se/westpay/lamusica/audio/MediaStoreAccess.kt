//package se.westpay.lamusica.audio
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.ImageDecoder
//import android.net.Uri
//import android.os.Build
//import android.provider.MediaStore
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.withContext
//import se.westpay.lamusica.TAG
//import se.westpay.lamusica.datalayer.AlbumWithSongs
//import se.westpay.lamusica.models.AlbumSongs
//import se.westpay.lamusica.models.ArtistModel
//
//object MediaStoreAccess {
//    private val _currentPage = MutableStateFlow("0")
//    val currentPage = _currentPage.asStateFlow()
//
//    //@RequiresApi(Build.VERSION_CODES.O)
////    suspend fun getPagedAlbumSongsData(context: Context) : Flow<PagingData<AlbumSongs>>? {
////        return withContext(Dispatchers.IO) {
////            var items: Flow<PagingData<AlbumSongs>>? = null
////            try {
////                val audioMediaStore = AudioMediaStore(context)
////
////                items = Pager(
////                    config = PagingConfig(
////                        pageSize = 10,
////                        enablePlaceholders = true,
////                        maxSize = 200),
////                    pagingSourceFactory = { MediaStorePagingSource(audioMediaStore) }
////                ).flow
////            } catch(e: Exception) {
////                Log.e(TAG, "Exception when get album songs paged data! ${e.message}")
////            }
////            items
////        }
////    }
//
//    fun getPagedAlbumSongsData(context: Context) : Flow<PagingData<AlbumSongs>> {
//        val audioMediaStore = AudioMediaStore(context)
//        return Pager(
//            config = PagingConfig(
//                pageSize = 10,
//                enablePlaceholders = true,
//                maxSize = 200),
//            pagingSourceFactory = { MediaStorePagingSource(audioMediaStore) }
//        ).flow
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    fun getAlbumArtWork(context: Context, uri: Uri) : Bitmap? {
//        var bitmap: Bitmap? = null
//        try {
//            val audioMediaStore = AudioMediaStore(context)
//            bitmap = audioMediaStore.getAlbumArtWork(uri)
//        } catch(e: Exception) {
//            Log.e(TAG, "Exception when get album art work! ${e.message}")
//        }
//        return bitmap
//    }
//
//    fun getArtist(context: Context, artistId: Long) : ArtistModel {
//        var artist = ArtistModel()
//        try {
//            val audioMediaStore = AudioMediaStore(context)
//            artist = audioMediaStore.getArtist(artistId)
//        } catch(e: Exception) {
//            Log.e(TAG, "Exception when get artist! ${e.message}")
//        }
//        return artist
//    }
//}