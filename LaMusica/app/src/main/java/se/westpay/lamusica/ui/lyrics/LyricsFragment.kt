package se.westpay.lamusica.ui.lyrics

import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.westpay.lamusica.TAG
import se.westpay.lamusica.databinding.FragmentLyricsBinding

class LyricsFragment : Fragment() {

    private var _binding: FragmentLyricsBinding? = null
    private val binding get() = _binding!!
    private lateinit var _lyricsViewModel: LyricsViewModel

    companion object {
        val CLASS_NAME = "${LyricsFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = LyricsFragment::class.simpleName ?: ""
        const val ARG_PARAM_PLAIN_LYRICS = "param_plain_lyrics"
        const val ARG_PARAM_SYNCED_LYRICS = "param_synced_lyrics"
        const val ARG_PARAM_DURATION = "param_duration"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate $CLASS_NAME")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView $CLASS_NAME")
        val plainLyrics = arguments?.getString(ARG_PARAM_PLAIN_LYRICS) ?: ""
        val syncedLyrics = arguments?.getString(ARG_PARAM_SYNCED_LYRICS) ?: ""
        val duration = arguments?.getInt(ARG_PARAM_DURATION) ?: 0

        Log.i(TAG, "Argument: ${plainLyrics} ${syncedLyrics} ${duration}")

        _binding =  FragmentLyricsBinding.inflate(inflater, container, false)
        _lyricsViewModel = LyricsViewModel(
            plainLyrics = plainLyrics, syncedLyrics = syncedLyrics, duration = duration
        )

        binding.lyricsTextView.text = syncedLyrics
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume $CLASS_NAME")
    }

    fun setLyricTimeSpan(progressValue: Int) {
        val text = _lyricsViewModel.getSpannableText(progressValue, ForegroundColorSpan(Color.BLUE))
        binding.lyricsTextView.text = text
    }
}