package se.westpay.lamusica.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.datalayer.AlbumEntity
import se.westpay.lamusica.datalayer.ArtistEntity
import se.westpay.lamusica.datalayer.SongEntity
import se.westpay.lamusica.ui.categoryalbum.AlbumFragment
import se.westpay.lamusica.ui.categoryartist.ArtistFragment

class SearchAdapter(
    private var _tableData: TableData,
    val clickListener: (SongEntity) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewTypes {
        ARTIST,
        ALBUM,
        SONG,
        HEADER
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var searchItemTextView: TextView = view.findViewById(R.id.searchItem)

        fun bindTo(headerData: HeaderData) {
            searchItemTextView.text = "${headerData.title}:"
        }
    }

    inner class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var searchItemTextView: TextView = view.findViewById(R.id.searchItem)

        fun bindTo(artistEntity: ArtistEntity) {
            searchItemTextView.text = artistEntity.artistName

            searchItemTextView.setOnClickListener { view ->
                val context = view.context as MainActivity
                val artistId = artistEntity.artistId
                val bundle = bundleOf(
                    ArtistFragment.ARG_PARAM_ARTIST to artistId
                )
                context.loadFragment(ArtistFragment.FRAGMENT_NAME, bundle)
            }
        }
    }

    inner class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var searchItemTextView: TextView = view.findViewById(R.id.searchItem)

        fun bindTo(albumEntity: AlbumEntity) {
            searchItemTextView.text = albumEntity.albumName

            searchItemTextView.setOnClickListener { view ->
                val context = view.context as MainActivity
                val albumId = albumEntity.albumId.toInt()
                val bundle = bundleOf(
                    AlbumFragment.ARG_PARAM_ALBUM to albumId
                )
                context.loadFragment(AlbumFragment.FRAGMENT_NAME, bundle)
            }
        }
    }

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var searchItemTextView: TextView = view.findViewById(R.id.searchItem)

        fun bindTo(songEntity: SongEntity) {
            searchItemTextView.text = songEntity.songName

            searchItemTextView.setOnClickListener { _ ->
                this@SearchAdapter.clickListener(songEntity)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            ViewTypes.ARTIST.ordinal -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
                return ArtistViewHolder(itemView)
            }
            ViewTypes.ALBUM.ordinal -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
                return AlbumViewHolder(itemView)
            }
            ViewTypes.SONG.ordinal -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
                return SongViewHolder(itemView)
            }
            ViewTypes.HEADER.ordinal -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_header_item, parent, false)
                return HeaderViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
                return SongViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < _tableData.itemList.size) {
            when(_tableData.itemList[position]) {
                is ArtistEntity -> {
                    ViewTypes.ARTIST.ordinal
                }
                is AlbumEntity -> {
                    ViewTypes.ALBUM.ordinal
                }
                is SongEntity -> {
                    ViewTypes.SONG.ordinal
                }
                is HeaderData -> {
                    ViewTypes.HEADER.ordinal
                }
                else -> {
                    ViewTypes.HEADER.ordinal
                }
            }
        } else {
            ViewTypes.HEADER.ordinal
        }
    }

    override fun getItemCount(): Int {
        return _tableData.itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d(TAG, "SearchAdapter: onBindViewHolder position = ${position}")

        if (position < _tableData.itemList.size) {
            when(_tableData.itemList[position]) {
                is ArtistEntity -> {
                    (holder as ArtistViewHolder).bindTo(_tableData.itemList[position] as ArtistEntity)
                }
                is AlbumEntity -> {
                    (holder as AlbumViewHolder).bindTo(_tableData.itemList[position] as AlbumEntity)
                }
                is SongEntity -> {
                    (holder as SongViewHolder).bindTo(_tableData.itemList[position] as SongEntity)
                }
                is HeaderData -> {
                    (holder as HeaderViewHolder).bindTo(_tableData.itemList[position] as HeaderData)
                }
                else -> {
                    ViewTypes.HEADER.ordinal
                }
            }
        }
    }

    fun updateAdapterData(searchData: TableData) {
        _tableData = searchData
        notifyDataSetChanged()
    }
}