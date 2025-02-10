package se.westpay.lamusica.ui.lyrics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.westpay.lamusica.TAG
import se.westpay.lamusica.databinding.FragmentLyricsDialogBinding

class LyricsFragment : Fragment() {

    private var _binding: FragmentLyricsDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        val CLASS_NAME = "${LyricsFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = LyricsFragment::class.simpleName ?: ""
        const val ARG_PARAM_LYRICS = "param_lyrics"
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
        val lyrics = arguments?.getString(ARG_PARAM_LYRICS) ?: ""

        _binding =  FragmentLyricsDialogBinding.inflate(inflater, container, false)
        binding.lyricsTextView.text = lyrics
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume $CLASS_NAME")

    }
}