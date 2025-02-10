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
import androidx.core.os.bundleOf
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.datalayer.AlbumWithSongs
import java.io.FileNotFoundException

class AlbumsAdapter : PagingDataAdapter<AlbumWithSongs, AlbumsAdapter.AlbumViewHolder>(AUDIO_DATA_COMPARATOR) {
    private val albumIdMap: MutableMap<Int, Int> = mutableMapOf()

    inner class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.albumImage)
        private var albumTitleTextView: TextView = view.findViewById(R.id.albumTitle)
        private var albumGroupTextView: TextView = view.findViewById(R.id.albumGroup)
        private var albumSongsTextView: TextView = view.findViewById(R.id.albumSongs)

        fun bindTo(audioData: AlbumWithSongs) {
            try {
                // Set artwork
                if (audioData.albumEntity.albumUri?.isNotEmpty() == true) {
                    val imageUri = Uri.parse(audioData.albumEntity.albumUri)
                    val artWork = getAlbumArtwork(imageView.context.contentResolver, imageUri)
                    imageView.setImageBitmap(artWork)
                } else {
                    imageView.setImageResource(R.drawable.default_music2)
                }

                // Set album information
                albumTitleTextView.text = audioData.albumEntity.albumName
                albumGroupTextView.text = audioData.songList[0].artist.artistName
                val nbrOfSongs = audioData.songList.size
                albumSongsTextView.text =
                    albumSongsTextView.context.resources.getString(R.string.album_songs, nbrOfSongs)

                // Add the album id (primary key for the database table albums)
                albumIdMap[layoutPosition] = audioData.albumEntity.albumId.toInt()

                // Add click listener for the [AlbumFragment]
                imageView.setOnClickListener { view ->
                    val context = view.context as MainActivity
                    val albumId = albumIdMap[layoutPosition]
                    val bundle = bundleOf(
                        AlbumFragment.ARG_PARAM_ALBUM to albumId
                    )
                    context.loadFragment(AlbumFragment.FRAGMENT_NAME, bundle)
                }
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "Missing artwork")
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
            }
        }
    }

    companion object {
        val AUDIO_DATA_COMPARATOR = object : DiffUtil.ItemCallback<AlbumWithSongs>() {
            override fun areItemsTheSame(oldItem: AlbumWithSongs, newItem: AlbumWithSongs): Boolean =
                // User ID serves as unique ID
                oldItem.albumEntity.albumId == newItem.albumEntity.albumId

            override fun areContentsTheSame(oldItem: AlbumWithSongs, newItem: AlbumWithSongs): Boolean =
                // Compare full contents (note: Java users should call .equals())
                oldItem == newItem
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return AlbumViewHolder(itemView)
    }

    @NonNull
    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { itemNotNull->
            holder.bindTo(itemNotNull)
        }
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