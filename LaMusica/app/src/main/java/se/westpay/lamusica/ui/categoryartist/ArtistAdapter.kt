package se.westpay.lamusica.ui.categoryartist

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.audio.MusicPlayer
import se.westpay.lamusica.datalayer.*
import se.westpay.lamusica.repositories.AudioFileMetaData
import se.westpay.lamusica.utilities.DateTime


class ArtistAdapter(private val artistWithAlbums: ArtistWithAlbums,
                    val showPlayerBottomSheetCallback: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //private var _parentViewMap = mutableMapOf<Long?, ParentView>()
    private var _viewRepresentationList = mutableListOf<ParentView>()
    private val _songPathMap: MutableMap<Int, AudioFileMetaData> = mutableMapOf()
    private val _sortedSongList: List<AlbumWithSongs2>

    init {
        // Initialise the parent views
        val currentAlbumIdList = mutableListOf<Long>()

        // Setup parent view map and the child list for each parent view
        var parentPosition: Int = 0
        _sortedSongList = artistWithAlbums.songList.sortedBy { item-> item.songEntity.songAlbumId }
        _sortedSongList.forEachIndexed { index, songItem ->
            var parentId =
                currentAlbumIdList.find { albumId -> albumId == songItem.album.albumId } ?: -1
            if (parentId == -1L) {
                parentId = songItem.album.albumId
                currentAlbumIdList.add(parentId)
                _viewRepresentationList.add(
                    ParentView(
                        viewPosition = parentPosition,
                        firstSongOnAlbumIndex = index,
                        albumId = songItem.album.albumId,
                        isExpanded = false,
                        childList = null
                    )
                )
                parentPosition += 1
            }
        }
    }

    private fun getChildViews(currentPosition: Int, albumId: Long) : List<ChildView> {
        val childViews = mutableListOf<ChildView>()
        var newPosition = currentPosition
        _sortedSongList.forEachIndexed  { index, item ->
            if (albumId == item.songEntity.songAlbumId) {
                newPosition += 1
                childViews.add(ChildView(songIndex = index, viewPosition = newPosition))
                Log.i(TAG, "Added child: View position = ${newPosition} song index = ${index} album id = ${albumId}")
            }
        }
        return childViews
    }

    private fun expandViews(currentPosition: Int, currentViewRepresentation: List<ParentView>) : MutableList<ParentView> {
        val newList = mutableListOf<ParentView>()
        var newPosition: Int = currentPosition
        currentViewRepresentation.forEach { item ->
            if (currentPosition == item.viewPosition) {
                Log.i(TAG, "Expand parent view: View position = ${newPosition} album id = ${item.albumId}")
                val parentView = ParentView(
                    viewPosition = currentPosition,
                    firstSongOnAlbumIndex = item.firstSongOnAlbumIndex,
                    albumId = item.albumId,
                    isExpanded = true,
                    childList = getChildViews(currentPosition = currentPosition, albumId = item.albumId)
                )
                newList.add(parentView)
                newPosition += 1 // Add parent view
                newPosition += parentView.childList?.size ?: 1
            } else if (currentPosition > item.viewPosition) {
                newList.add(item)
            } else {
                Log.i(TAG, "Adjust parent view: View position = ${newPosition} album id = ${item.albumId}")
                val parentView = ParentView(
                    viewPosition = newPosition,
                    firstSongOnAlbumIndex = item.firstSongOnAlbumIndex,
                    albumId = item.albumId,
                    isExpanded = item.isExpanded,
                    childList = if (item.isExpanded) {
                        getChildViews(currentPosition = newPosition, albumId = item.albumId)
                    } else {
                        null
                    }
                )
                newList.add(parentView)
                newPosition += 1 // Add parent view
                newPosition += parentView.childList?.size ?: 0
            }
        }
        return newList
    }

    private fun collapseViews(currentPosition: Int, currentViewRepresentation: List<ParentView>) : MutableList<ParentView> {
        val newList = mutableListOf<ParentView>()
        var newPosition: Int = currentPosition
        currentViewRepresentation.forEach { item ->
            if (currentPosition == item.viewPosition) {
                Log.i(TAG, "Collapse parent view: View position = ${newPosition} album id = ${item.albumId}")
                val parentView = ParentView(
                    viewPosition = currentPosition,
                    firstSongOnAlbumIndex = item.firstSongOnAlbumIndex,
                    albumId = item.albumId,
                    isExpanded = false,
                    childList = null
                )
                newList.add(parentView)
                newPosition += 1 // Add parent view
            } else if (currentPosition > item.viewPosition) {
                newList.add(item)
            } else {
                Log.i(TAG, "Adjust parent view: View position = ${newPosition} album id = ${item.albumId}")
                val parentView = ParentView(
                    viewPosition = newPosition,
                    firstSongOnAlbumIndex = item.firstSongOnAlbumIndex,
                    albumId = item.albumId,
                    isExpanded = item.isExpanded,
                    childList = if (item.isExpanded) {
                        getChildViews(currentPosition = newPosition, albumId = item.albumId)
                    } else {
                        null
                    }
                )
                newList.add(parentView)
                newPosition += 1 // Add parent view
                newPosition += parentView.childList?.size ?: 0
            }
        }
        return newList
    }

    inner class AlbumsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var imageView: ImageView = view.findViewById(R.id.albumImage)
        private var _albumTitleTextView: TextView = view.findViewById(R.id.albumTitle)
        private var _albumYearTextView: TextView = view.findViewById(R.id.albumYear)
        private var _albumSongsTextView: TextView = view.findViewById(R.id.albumSongs)
        private var _expandImageView: ImageView = view.findViewById(R.id.expandImage)

        // Track views
        private var trackItemLayout: LinearLayout = view.findViewById(R.id.trackItem)
        private var songTitleTextView: TextView = view.findViewById(R.id.songTitle)
        private var albumGroupTextView: TextView = view.findViewById(R.id.albumGroup)
        private var songPlayTimeTextView: TextView = view.findViewById(R.id.songPlayTime)
        private var addToQueueImageView: ImageView = view.findViewById(R.id.addToQueueIconImage)

        /**
         * Get number of song for an album.
         */
        private fun getNbrOfAlbumSongs(albumId: Long) : Int {
            var nbrOfSongs: Int = 0

            artistWithAlbums.songList.forEach { item ->
                if(item.album.albumId == albumId) {
                    nbrOfSongs++
                }
            }
            return nbrOfSongs
        }

        private fun bindToTrack(audioData: AlbumWithSongs2) {
            songTitleTextView.text = audioData.songEntity.songName
            val duration = DateTime.getFormattedDurationTime(audioData.songEntity.duration ?: "")
            songPlayTimeTextView.text = duration
            albumGroupTextView.text = audioData.album.albumName

            // Add the song meta data
            val audioFileMetaData = AudioFileMetaData(
                songUri = Uri.parse(audioData.songEntity.songUri),
                title = audioData.songEntity.songName ?: "",
                album = audioData.album.albumName ?: "",
                artist = artistWithAlbums.artistEntity.artistName ?: "",
                genre = "",
                year = audioData.songEntity.timestampDate.toString() ?: "",
                format = "",
                duration = audioData.songEntity.duration ?: "",
                resolution = "",
                size = audioData.songEntity.size ?: 0,
                bitmap = null,
                albumUri = Uri.parse(audioData.songEntity.albumUri)
            )
            _songPathMap[0] = audioFileMetaData

            // Click listener to add a song to song queue
            addToQueueImageView.setOnClickListener { view ->
                _songPathMap[layoutPosition]?.let { audioFile ->
                    MusicPlayer.addToPlayerQueue(audioFile)
                    showPlayerBottomSheetCallback()
                    //Toast.makeText(view.context, "Song ${audioFile.title} added to queue!", Toast.LENGTH_LONG).show()
                }
            }
        }

        fun bindTo(audioData: AlbumWithSongs2) {
            try {
                // Set artwork
                if (audioData.songEntity.albumUri?.isNotEmpty() == true) {
                    val imageUri = Uri.parse(audioData.songEntity.albumUri)
                    val artWork = getAlbumArtwork(imageView.context.contentResolver, imageUri)
                    imageView.setImageBitmap(artWork)
                } else {
                    imageView.setImageResource(R.drawable.default_music2)
                }

                _albumTitleTextView.text = audioData.album.albumName
                val year = audioData.album.year
                _albumYearTextView.text = _albumYearTextView.context.resources.getString(R.string.album_year, year ?: 0)
                val nbrOfSongs = getNbrOfAlbumSongs(audioData.album.albumId)
                _albumSongsTextView.text =
                    _albumSongsTextView.context.resources.getString(R.string.album_songs, nbrOfSongs)

                // Show correct image depending on parent view is expanded or not
                if (isExpanded(layoutPosition)) {
                    _expandImageView.setImageResource(R.drawable.ic_expand_less)

                    // Handle the scenario when only one song in an album
//                    if (_parentViewMap.size == 1 && _parentViewMap[0]?.childList?.isEmpty() == null) {
                        //trackItemLayout.visibility = View.VISIBLE
                        //bindToTrack(audioData)
                    //}
                } else {
                    // Handle the scenario when only one song in an album
//                    if (_parentViewMap.size == 1 &&
//                        _parentViewMap[getParentKeyByPosition(layoutPosition)]?.childList?.isEmpty() == null) {
//                        trackItemLayout.visibility = View.INVISIBLE
//                    }
                    //trackItemLayout.visibility = View.GONE
                    _expandImageView.setImageResource(R.drawable.ic_expand_more)
                }

                // Click listener for the expand icon
                _expandImageView.setOnClickListener { _ ->
                    manageExpandAndCollapse(layoutPosition)
                    notifyDataSetChanged()
                }
            } catch(e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
            }
        }
    }

    private fun isExpanded(currentPosition: Int) : Boolean {
        var isExpanded = false
        run loop@ {
            _viewRepresentationList.forEach { item ->
                if (currentPosition == item.viewPosition) {
                    isExpanded = item.isExpanded
                    return@loop
                }
            }
        }
        return isExpanded
    }

    private fun manageExpandAndCollapse(currentPosition: Int) {
        try {
            var isExpanded = false
            run loop@ {
                _viewRepresentationList.forEach { item ->
                    if (currentPosition == item.viewPosition) {
                        isExpanded = !item.isExpanded
                        return@loop
                    }
                }
            }

            _viewRepresentationList = if (isExpanded) {
                Log.i(TAG, "Expand parent view: ${currentPosition}")
                expandViews(currentPosition, _viewRepresentationList)
            } else {
                Log.i(TAG, "Collapse parent view: ${currentPosition}")
                collapseViews(currentPosition, _viewRepresentationList)
            }
        } catch(e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
        }
    }

    inner class TracksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var songTitleTextView: TextView = view.findViewById(R.id.songTitle)
        var albumGroupTextView: TextView = view.findViewById(R.id.albumGroup)
        var songPlayTimeTextView: TextView = view.findViewById(R.id.songPlayTime)
        var addToQueueImageView: ImageView = view.findViewById(R.id.addToQueueIconImage)

        fun bindTo(albumWithSongs2: AlbumWithSongs2) {
            songTitleTextView.text = albumWithSongs2.songEntity.songName
            val duration = DateTime.getFormattedDurationTime(albumWithSongs2.songEntity.duration ?: "")
            songPlayTimeTextView.text = duration
            albumGroupTextView.text = albumWithSongs2.album.albumName

            // Add the song meta data
            val audioFileMetaData = AudioFileMetaData(
                songUri = Uri.parse(albumWithSongs2.songEntity.songUri),
                title = albumWithSongs2.songEntity.songName ?: "",
                album = albumWithSongs2.album.albumName ?: "",
                artist = artistWithAlbums.artistEntity.artistName ?: "",
                genre = "",
                year = albumWithSongs2.songEntity.timestampDate.toString() ?: "",
                format = "",
                duration = albumWithSongs2.songEntity.duration ?: "",
                resolution = "",
                size = albumWithSongs2.songEntity.size ?: 0,
                bitmap = null,
                albumUri = Uri.parse(albumWithSongs2.songEntity.albumUri)
            )
            _songPathMap[layoutPosition] = audioFileMetaData

            // Click listener to add a song to song queue
            addToQueueImageView.setOnClickListener { view ->
                _songPathMap[layoutPosition]?.let { audioFile ->
                    MusicPlayer.addToPlayerQueue(audioFile)
                    showPlayerBottomSheetCallback()
                    //Toast.makeText(view.context, "Song ${audioFile.title} added to queue!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (ExpandableView.ViewTypes.PARENT_VIEWTYPE.value == viewType) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.artist_album_item, parent, false)
            AlbumsViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tracks_item, parent, false)
            TracksViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(TAG, "ArtistAdapter: onBindViewHolder position = ${position} ${holder}")

        when(holder) {
            is AlbumsViewHolder -> {
                val parentView = getViewByPosition(position) as ParentView
                holder.bindTo(_sortedSongList[parentView.firstSongOnAlbumIndex])

            } is TracksViewHolder -> {
                val childView = getViewByPosition(position) as ChildView
                holder.bindTo(_sortedSongList[childView.songIndex])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getViewByPosition(position)) {
            is ParentView -> {
                ExpandableView.ViewTypes.PARENT_VIEWTYPE.value
            }
            is ChildView -> {
                ExpandableView.ViewTypes.CHILD_VIEWTYPE.value
            }
            else -> {
                Log.e(TAG, "Unknown view type")
                ExpandableView.ViewTypes.PARENT_VIEWTYPE.value
            }
        }
    }

    override fun getItemCount() : Int {
        val count =  getNumberOfVisibleItems()
        Log.d(TAG, "ArtistAdapter: getItemCount count = ${count}")
        return count
    }

    /**
     * Get number of visible view items.
     */
    private fun getNumberOfVisibleItems() : Int {
        var nbrOfChildItems: Int = 0
        _viewRepresentationList.forEach { item ->
            if (item.isExpanded) {
                nbrOfChildItems += item.childList?.size ?: 0
            }
        }
        return nbrOfChildItems + _viewRepresentationList.size
    }

    /**
     * Get view (parent or child) by the position.
     */
    private fun getViewByPosition(position: Int) : ExpandableView {
        var view: ExpandableView? = null
        try {
            run loop@ {
                _viewRepresentationList.forEach { item ->
                    Log.d(TAG, "Pos: ${position} ParentViewPos: ${item.viewPosition}")
                    if (position == item.viewPosition) {
                        view = item
                        return@loop
                    } else if (item.isExpanded) {
                        run innerLoop@ {
                            item.childList?.forEach { childItem ->
                                Log.d(TAG, "Pos: ${position} ChildViewPos: ${childItem.viewPosition}")
                                if (position == childItem.viewPosition) {
                                    view = childItem
                                    return@innerLoop
                                }
                            }
                        }
                        if (view != null) {
                            return@loop
                        }
                    }
                }
            }

            if (view == null) {
                throw Exception("No view found!")
            }
        } catch(e: Exception) {
            Log.e(TAG, "Exception ${e.message}")
        }

        return view ?: _viewRepresentationList.first()
    }

    /**
     * Get album cover artwork.
     *
     * @param resolver
     * @param contentUri
     * @return Bitmap?
     */
    private fun getAlbumArtwork(resolver: ContentResolver, contentUri: Uri) : Bitmap? {
        return try {
            resolver.loadThumbnail(contentUri, Size(640, 480), null)
        } catch(e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            null
        }
    }
}

interface ExpandableView {
    enum class ViewTypes(val value: Int) {
        PARENT_VIEWTYPE (1111),
        CHILD_VIEWTYPE(2222)
    }
}

data class ParentView(
    val viewPosition: Int,
    val firstSongOnAlbumIndex: Int,
    val albumId: Long,
    var isExpanded: Boolean,
    var childList: List<ChildView>? = null
) : ExpandableView

data class ChildView(
    val viewPosition: Int,
    val songIndex: Int
) : ExpandableView