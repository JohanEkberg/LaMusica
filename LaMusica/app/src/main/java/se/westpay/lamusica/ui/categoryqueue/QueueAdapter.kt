package se.westpay.lamusica.ui.categoryqueue

import android.util.Log
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.repositories.AudioFileMetaData
import se.westpay.lamusica.utilities.DateTime


class QueueAdapter(songs: List<AudioFileMetaData>, removeSongCallback: (AudioFileMetaData) -> Unit)
    :  RecyclerView.Adapter<QueueAdapter.SongQueueViewHolder>() {

    private var _songs = songs.toMutableList()
    private val _removeSongCallback = removeSongCallback

    fun updateAdapterItems(songs: List<AudioFileMetaData>) {
        // Remove the first item,
        // so that the view holder is up to date with the new size
        if (_songs.size > songs.size) {
            notifyItemRemoved(0)
        }

        // Add the new list
        _songs.clear()
        _songs = songs.toMutableList()
        Log.d(TAG, "QueueAdapter: addData songs list size = ${_songs.size}")
        notifyItemRangeChanged(0, _songs.size)
    }

    private fun removeAdapterItem(position: Int) {
        val audioFile = _songs[position]
        _songs.removeAt(position)
        _removeSongCallback(audioFile)
        Log.d(TAG, "QueueAdapter: removeAdapterItem song = ${audioFile.title}")
        notifyItemRemoved(position)
    }

    inner class SongQueueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var songTitleTextView: TextView = view.findViewById(R.id.songTitle)
        var artistTextView: TextView = view.findViewById(R.id.artist)
        var albumTextView: TextView = view.findViewById(R.id.album)
        var durationTextView: TextView = view.findViewById(R.id.duration)
        var removeFromQueue: ImageView = view.findViewById(R.id.removeFromQueueIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongQueueViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.queue_item,
            parent,
            false
        )
        return SongQueueViewHolder(itemView)
    }

    @NonNull
    override fun onBindViewHolder(holder: SongQueueViewHolder, position: Int) {
        val objectId = Integer.toHexString(System.identityHashCode(holder))
        Log.d(TAG, "QueueAdapter: onBindViewHolder - position = ${position} object id = ${objectId}")
        val item = _songs[position]
        holder.songTitleTextView.text = item.title
        holder.artistTextView.text = item.artist
        holder.albumTextView.text = item.album
        holder.durationTextView.text = holder.durationTextView.context.resources.getString(
            R.string.song_duration,
            DateTime.getFormattedDurationTime(item.duration)
        )

        // Remove song click listener
        holder.removeFromQueue.setOnClickListener { _ ->
            val layerPosition = holder.layoutPosition
            Log.d(TAG, "QueueAdapter: Remove click listener layer - position = ${layerPosition} list size = ${_songs.size}")
            val range = Range(0, _songs.size)
            if (range.contains(layerPosition)) {
                removeAdapterItem(layerPosition)
            } else {
                Log.e(TAG, "QueueAdapter: Remove click listener - position ${layerPosition} is not in range")
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "QueueAdapter: getItemCount - item count = ${_songs.size}")
        return _songs.size
    }
}