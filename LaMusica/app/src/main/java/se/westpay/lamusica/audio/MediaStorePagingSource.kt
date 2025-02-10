//package se.westpay.lamusica.audio
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import se.westpay.lamusica.models.AlbumSongs
//
//class MediaStorePagingSource(
//    private val _audioMediaStore: AudioMediaStore
//    ) : PagingSource<Int, AlbumSongs>() {
//    private val FIRST_PAGE = 1
//
//    override fun getRefreshKey(state: PagingState<Int, AlbumSongs>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AlbumSongs> {
//        return try {
//            val page = params.key ?: FIRST_PAGE
//            val response = _audioMediaStore.getPagedAlbumSongs(page, params.loadSize)
//            LoadResult.Page(
//                data = response,
//                prevKey = if (page == FIRST_PAGE) null else page - 1,
//                nextKey = if (response.isEmpty()) null else page + 1
//            )
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//}