package se.westpay.lamusica.ui.categoryartist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R

class ArtistsFragment : Fragment() {
    private val _classTag = javaClass.simpleName

    private lateinit var _artistsViewModel: ArtistsViewModel
    private lateinit var _recyclerView: RecyclerView

    companion object {
        val CLASS_NAME = "${ArtistsFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = ArtistsFragment::class.simpleName ?: ""
        fun newInstance(): ArtistsFragment = ArtistsFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(_classTag, "onAttach ${this.toString()}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(_classTag, "onCreate ${this.toString()}")
        _artistsViewModel = ArtistsViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(_classTag, "onCreateView ${this.toString()}")
        val root = inflater.inflate(R.layout.fragment_artists, container, false)
        _recyclerView = root.findViewById(R.id.artistsRecyclerView)
        val context = requireActivity() as MainActivity
        val layoutManager = GridLayoutManager(context, 1)
        _recyclerView.layoutManager = layoutManager
        val artistsAdapter = ArtistsAdapter()
        _recyclerView.adapter = artistsAdapter
        _artistsViewModel.populateArtists(
            requireContext() as MainActivity,
            _recyclerView.adapter as ArtistsAdapter)
        return root
    }
}