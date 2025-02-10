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
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.datalayer.ArtistWithSongs

class ArtistsAdapter : PagingDataAdapter<ArtistWithSongs, ArtistsAdapter.ArtistViewHolder>(AUDIO_DATA_COMPARATOR) {
    private val artistIdMap: MutableMap<Int, Long> = mutableMapOf()

    inner class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.artistImage)
        var artistTitleTextView: TextView = view.findViewById(R.id.artistTitle)
        var numberOfAlbumTextView: TextView = view.findViewById(R.id.numberOfAlbums)

        fun bindTo(audioData: ArtistWithSongs) {
//            val artWork = audioData.albumList[0].filePath?.let { path ->
//                FileOperations.getAudioFileMetaData(path)
//            }
//            if (artWork == null) {
//                imageView.setImageResource(R.drawable.default_music2)
//            } else {
//                if (artWork.bitmap != null) {
//                    imageView.setImageBitmap(artWork.bitmap)
//                } else {
//                    imageView.setImageResource(R.drawable.default_music2)
//                }
//            }

            // Set artwork
            if (audioData.songList[0].albumUri?.isNotEmpty() == true) {
                val imageUri = Uri.parse(audioData.songList[0].albumUri)
                val artWork = getAlbumArtwork(imageView.context.contentResolver, imageUri)
                imageView.setImageBitmap(artWork)
            } else {
                imageView.setImageResource(R.drawable.default_music2)
            }

            artistTitleTextView.text = audioData.artistEntity.artistName

            /**
             * Get number of albums.
             */
            val albums = mutableListOf<Long>()
            audioData.songList.forEach { item ->
                if (!albums.contains(item.songAlbumId) && item.songAlbumId != null) {
                    albums.add(item.songAlbumId)
                }
            }
            val nbrOfAlbums: Int =  albums.size
            numberOfAlbumTextView.text = numberOfAlbumTextView.context.resources.getString(R.string.artist_albums, nbrOfAlbums.toString())

            artistIdMap[layoutPosition] = audioData.artistEntity.artistId

            imageView.setOnClickListener { view ->
                val context = view.context as MainActivity
                val artistId = artistIdMap[layoutPosition]
                val bundle = bundleOf(
                    ArtistFragment.ARG_PARAM_ARTIST to artistId
                )
                context.loadFragment(ArtistFragment.FRAGMENT_NAME, bundle)
            }
        }
    }

    companion object {
        val AUDIO_DATA_COMPARATOR = object : DiffUtil.ItemCallback<ArtistWithSongs>() {
            override fun areItemsTheSame(oldItem: ArtistWithSongs, newItem: ArtistWithSongs): Boolean =
                // User ID serves as unique ID
                oldItem.artistEntity.artistId == newItem.artistEntity.artistId

            override fun areContentsTheSame(oldItem: ArtistWithSongs, newItem: ArtistWithSongs): Boolean =
                // Compare full contents (note: Java users should call .equals())
                oldItem == newItem
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.artist_item, parent, false)
        return ArtistViewHolder(itemView)
    }

    @NonNull
    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        Log.d(TAG, "ArtistsAdapter: onBindViewHolder position = ${position}")
        val item = getItem(position)
        item?.let { itemNotNull->
            holder.bindTo(itemNotNull)
        }
    }

    override fun getItemCount(): Int {
        val count = super.getItemCount()
        Log.d(TAG, "ArtistsAdapter: getItemCount count = ${count}")
        return count
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