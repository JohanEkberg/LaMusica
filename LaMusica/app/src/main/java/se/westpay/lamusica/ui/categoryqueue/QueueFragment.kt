package se.westpay.lamusica.ui.categoryqueue

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.TAG
import se.westpay.lamusica.audio.MusicPlayer

class QueueFragment : Fragment() {
    private val _classTag = javaClass.simpleName
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _queueViewModel: QueueViewModel

    companion object {
        val CLASS_NAME = "${QueueFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = QueueFragment::class.simpleName ?: ""
        fun newInstance(): QueueFragment = QueueFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(_classTag, "onAttach ${this.toString()}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(_classTag, "onCreate ${this.toString()}")
        //_queueViewModel = QueueViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(_classTag, "onCreateView ${this.toString()}")
        val root =  inflater.inflate(R.layout.fragment_queue, container, false)
        _queueViewModel = ViewModelProvider(this)[QueueViewModel::class.java]
        _recyclerView = root.findViewById(R.id.queueRecyclerView)

        val queueAdapter = QueueAdapter(
            MusicPlayer.getPlayerQueue().ifEmpty { listOf() },
            _queueViewModel::removeSong
        )
        val context = requireActivity() as MainActivity
        val layoutManager = GridLayoutManager(context, 1)
        _recyclerView.layoutManager = layoutManager
        _recyclerView.adapter = queueAdapter
        _queueViewModel.startCheckForQueueChange()

        _queueViewModel.audioFileMetaData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Log.d(TAG, "Observer: audio list size ${it.size}")
                (_recyclerView.adapter as QueueAdapter).updateAdapterItems(it)
            }
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        //_queueViewModel.updateSongs()
    }
}