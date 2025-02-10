package se.westpay.lamusica.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.TAG
import se.westpay.lamusica.audio.MusicPlayer
import se.westpay.lamusica.databinding.FragmentSearchBinding
import se.westpay.lamusica.datalayer.SongEntity

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var _searchViewModel: SearchViewModel
    var returnToFragment: String = ""

    companion object {
        val CLASS_NAME = "${SearchFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = SearchFragment::class.simpleName ?: ""
        //const val ARG_PARAM_RETURN_FRAGMENT = "param_search"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView $CLASS_NAME")
        _searchViewModel = SearchViewModel()
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        //returnToFragment = arguments?.getString(ARG_PARAM_RETURN_FRAGMENT) ?: ""

        val recyclerView = binding.searchRecyclerView
        recyclerView.adapter = SearchAdapter(
            TableData(emptyList()),
            this::setSongWithArtistAndAlbum
        )
        recyclerView.layoutManager = LinearLayoutManager(context)

        _searchViewModel.searchResult.observe(viewLifecycleOwner, Observer { result ->
            Log.i(TAG, "searchResult observer")
            (recyclerView.adapter as SearchAdapter).updateAdapterData(result)
        })

        _searchViewModel.audioFileMetaData.observe(viewLifecycleOwner, Observer { audioFileMetaData ->
            Log.i(TAG, "audioFileMetaData observer")
            if (audioFileMetaData != null) {
                MusicPlayer.addToPlayerQueue(audioFileMetaData)
                (requireActivity() as MainActivity).showPlayerBottomSheet()
                //Toast.makeText((requireActivity() as MainActivity), "Song ${audioFileMetaData.title} added to queue!", Toast.LENGTH_LONG).show()
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated $CLASS_NAME")
    }

    fun setSearchString(value: String) {
        Log.d(TAG, "Search string: ${value}")
        _searchViewModel.doSearch(
            context = requireActivity() as MainActivity,
            searchString = value
        )
    }

    private fun setSongWithArtistAndAlbum(songEntity: SongEntity) {
        _searchViewModel.getArtistAndAlbumForSong(requireActivity() as MainActivity, songEntity)
    }
}