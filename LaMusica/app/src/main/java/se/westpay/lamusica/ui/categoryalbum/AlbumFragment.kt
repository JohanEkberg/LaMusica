package se.westpay.lamusica.ui.categoryalbum

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.datalayer.AlbumWithSongs
import se.westpay.lamusica.ui.lyrics.LyricsFragment

class AlbumFragment : Fragment() {
    private val _classTag = javaClass.simpleName
    private lateinit var _recyclerView: RecyclerView

    lateinit var _albumViewModel: AlbumViewModel

    companion object {
        val CLASS_NAME = "${AlbumFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = AlbumFragment::class.simpleName ?: ""
        const val ARG_PARAM_ALBUM = "param_album"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(_classTag, "onAttach $CLASS_NAME")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(_classTag, "onCreate $CLASS_NAME")
        _albumViewModel = AlbumViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(_classTag, "onCreateView $CLASS_NAME")
        val root = inflater.inflate(R.layout.fragment_album_songs, container, false)
        val context = requireActivity() as MainActivity
        _recyclerView = root.findViewById(R.id.albumSongsRecyclerView)

        // Get album songs
        val albumId = arguments?.getInt(ARG_PARAM_ALBUM) ?: 0
        _albumViewModel.getAlbumSongsById(context, albumId)

        // Observer for when the album songs are available
        _albumViewModel.albumWithSongs.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val albumWithSongs: AlbumWithSongs = it
                val albumSongsAdapter = AlbumSongsAdapter(albumWithSongs,
                    context::showPlayerBottomSheet)
                _recyclerView.adapter = albumSongsAdapter

                // This is needed so that the RecyclerView gets visible, unclear why!
                _recyclerView.layoutManager = LinearLayoutManager(context)

                // Adjust the recycler view to the bottom sheet
                //Log.d(TAG, "BottomSheet margin: ${context.getPlayerBottomSheetBottomMargin()}")
                //_recyclerView.setPadding(0,0,0, context.getPlayerBottomSheetBottomMargin())
            }
        })

        return root
    }

    fun getRecycleView() : RecyclerView {
        return _recyclerView
    }
}