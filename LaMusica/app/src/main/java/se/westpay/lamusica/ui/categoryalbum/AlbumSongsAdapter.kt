package se.westpay.lamusica.ui.categoryalbum

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.audio.MusicPlayer
import se.westpay.lamusica.datalayer.AlbumWithSongs
import se.westpay.lamusica.datalayer.SongWithArtist
import se.westpay.lamusica.repositories.AudioFileMetaData
import se.westpay.lamusica.utilities.DateTime


class AlbumSongsAdapter(private val albumWithSongs: AlbumWithSongs,
                        val showPlayerBottomSheetCallback: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val _songPathMap: MutableMap<Int, AudioFileMetaData> = mutableMapOf()
    //private val _songPathMap: MutableMap<Int, String> = mutableMapOf()
//    private var _playButtonIconState = false
//    private var _currentSelectedItem = CurrentSelectedItem()

    private fun getTotalPlayTime(songList: List<SongWithArtist>) : String {
        val totalDuration = songList.sumOf {
            if (it.songEntity.duration.isNullOrEmpty()) {
                0
            } else {
                it.songEntity.duration.toInt()
            }
        }
        return DateTime.getFormattedDurationTime(totalDuration.toString())
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Header
        var imageView: ImageView = view.findViewById(R.id.albumImage)
        var albumTitleTextView: TextView = view.findViewById(R.id.albumTitle)
        var albumGroupHeaderTextView: TextView = view.findViewById(R.id.albumGroupHeader)
        var albumSongsTextView: TextView = view.findViewById(R.id.albumSongs)
        var totalPlayTimeTextView: TextView = view.findViewById(R.id.totalPlayTime)

        // Tracks list
        var songTitleTextView: TextView = view.findViewById(R.id.songTitle)
        var albumGroupTextView: TextView = view.findViewById(R.id.albumGroup)
        var songPlayTimeTextView: TextView = view.findViewById(R.id.songPlayTime)
        var addToQueueImageView: ImageView = view.findViewById(R.id.addToQueueIconImage)

        fun bindTo(audioData: AlbumWithSongs) {
            // Set artwork
            if (audioData.albumEntity.albumUri?.isNotEmpty() == true) {
                val imageUri = Uri.parse(audioData.albumEntity.albumUri)
                val artWork = getAlbumArtwork(imageView.context.contentResolver, imageUri)
                imageView.setImageBitmap(artWork)
            } else {
                imageView.setImageResource(R.drawable.default_music2)
            }

            albumTitleTextView.text = audioData.albumEntity.albumName
            albumGroupHeaderTextView.text = audioData.songList[0].artist.artistName
            val nbrOfSongs = audioData.songList.size
            albumSongsTextView.text = albumSongsTextView.context.resources.getString(R.string.album_songs, nbrOfSongs)
            totalPlayTimeTextView.text = getTotalPlayTime(audioData.songList)

            // Setup first track
            songTitleTextView.text = audioData.songList[0].songEntity.songName
            albumGroupTextView.text = audioData.songList[0].artist.artistName
            val duration = DateTime.getFormattedDurationTime(audioData.songList[0].songEntity.duration ?: "")
            songPlayTimeTextView.text = duration

            // Add the album id (primary key for the database table albums)
//            _songPathMap[layoutPosition] = audioData.songList[0].songEntity.contentUri.toString()

            val audioFileMetaData = AudioFileMetaData(
                songUri = Uri.parse(audioData.songList[0].songEntity.songUri),
                title = audioData.songList[0].songEntity.songName ?: "",
                album = audioData.albumEntity.albumName ?: "",
                artist = audioData.songList[0].artist.artistName ?: "",
                genre = "",
                year = audioData.songList[0].songEntity.timestampDate.toString() ?: "",
                format = "",
                duration = audioData.songList[0].songEntity.duration ?: "",
                resolution = "",
                size = audioData.songList[0].songEntity.size ?: 0,
                bitmap = null,
                albumUri = Uri.parse(audioData.songList[0].songEntity.albumUri)
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

    inner class TracksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var songTitleTextView: TextView = view.findViewById(R.id.songTitle)
        var albumGroupTextView: TextView = view.findViewById(R.id.albumGroup)
        var songPlayTimeTextView: TextView = view.findViewById(R.id.songPlayTime)
        var addToQueueImageView: ImageView = view.findViewById(R.id.addToQueueIconImage)

        fun bindTo(songWithArtist: SongWithArtist) {
            songTitleTextView.text = songWithArtist.songEntity.songName
            albumGroupTextView.text = songWithArtist.artist.artistName
            val duration = DateTime.getFormattedDurationTime(songWithArtist.songEntity.duration ?: "")
            songPlayTimeTextView.text = duration

            val audioFileMetaData = AudioFileMetaData(
                songUri = Uri.parse(songWithArtist.songEntity.songUri),
                title = songWithArtist.songEntity.songName ?: "",
                album = "", // TODO: Get album name
                artist = songWithArtist.artist.artistName ?: "",
                genre = "",
                year = songWithArtist.songEntity.timestampDate.toString() ?: "",
                format = "",
                duration = songWithArtist.songEntity.duration ?: "",
                resolution = "",
                size = songWithArtist.songEntity.size ?: 0,
                bitmap = null,
                albumUri = Uri.parse(songWithArtist.songEntity.albumUri)
            )

            // Add the album id (primary key for the database table albums)
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

    companion object {
        val TYPE_HEADER = 0
        val TYPE_ITEM = 1
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tracks_item, parent, false)
                TracksViewHolder(itemView)
            } TYPE_HEADER -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.album_songs_header, parent, false)
                HeaderViewHolder(itemView)
            } else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tracks_item, parent, false)
                TracksViewHolder(itemView)
            }
        }
    }

    @NonNull
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.bindTo(albumWithSongs)
            } is TracksViewHolder -> {
                holder.bindTo(albumWithSongs.songList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return if (position == 0) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return albumWithSongs.songList.size
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

data class CurrentSelectedItem(var imageView: ImageView? = null, var position: Int = 0)