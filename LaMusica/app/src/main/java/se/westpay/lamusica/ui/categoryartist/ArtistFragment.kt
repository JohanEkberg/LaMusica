package se.westpay.lamusica.ui.categoryartist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.datalayer.ArtistWithAlbums
import se.westpay.lamusica.ui.lyrics.LyricsFragment

class ArtistFragment : Fragment() {
    private val _classTag = javaClass.simpleName
    private lateinit var _recyclerView: RecyclerView

    private lateinit var _artistViewModel: ArtistViewModel

    companion object {
        val CLASS_NAME = "${ArtistFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = ArtistFragment::class.simpleName ?: ""
        const val ARG_PARAM_ARTIST = "param_artist"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(_classTag, "onAttach $CLASS_NAME")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(_classTag, "onCreate $CLASS_NAME")
        _artistViewModel = ArtistViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(_classTag, "onCreateView $CLASS_NAME")
        val root = inflater.inflate(R.layout.fragment_artist, container, false)
        val context = requireActivity() as MainActivity
        _recyclerView = root.findViewById(R.id.artistRecyclerView)

        // Get albums for artist
        val artistId = arguments?.getLong(ArtistFragment.ARG_PARAM_ARTIST) ?: 0
        _artistViewModel.getAlbumByArtistId(context, artistId)

        // Observer for when the album songs are available
        _artistViewModel.artistWithAlbums.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                val artistWithAlbums: ArtistWithAlbums = it
                val artistAdapter = ArtistAdapter(artistWithAlbums, context::showPlayerBottomSheet)
                _recyclerView.adapter = artistAdapter

                // This is needed so that the RecyclerView gets visible, unclear why!
                _recyclerView.layoutManager = LinearLayoutManager(context)

                // Adjust the recycler view to the bottom sheet
                //_recyclerView.setPadding(0,0,0, context.getPlayerBottomSheetBottomMargin())
            }
        })

        return root
    }

    fun getRecycleView() : RecyclerView {
        return _recyclerView
    }
}