package se.westpay.lamusica.ui.categoryalbum

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

class AlbumsFragment : Fragment() {
    private val _classTag = javaClass.simpleName
    private val NUMBER_OF_COLUMNS = 3

    private lateinit var _albumsViewModel: AlbumsViewModel

    companion object {
        val CLASS_NAME = "${AlbumsFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = AlbumsFragment::class.simpleName ?: ""

        fun newInstance(): AlbumsFragment = AlbumsFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(_classTag, "onAttach ${this.toString()}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(_classTag, "onCreate ${this.toString()}")
        _albumsViewModel = AlbumsViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(_classTag, "onCreateView ${this.toString()}")
        val root = inflater.inflate(R.layout.fragment_albums, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.albumsRecyclerView)
        val context = requireActivity() as MainActivity
        val layoutManager = GridLayoutManager(context, NUMBER_OF_COLUMNS)
        recyclerView.layoutManager = layoutManager
        val albumAdapter = AlbumsAdapter()
        recyclerView.adapter = albumAdapter
        _albumsViewModel.populateAlbums(
            requireContext() as MainActivity,
            recyclerView.adapter as AlbumsAdapter
        )
        return root
    }
}