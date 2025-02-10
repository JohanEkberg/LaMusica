package se.westpay.lamusica.ui.lyrics

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import se.westpay.lamusica.TAG
import se.westpay.lamusica.databinding.FragmentLyricsDialogBinding

class LyricsDialog(private val lyric: String) : DialogFragment() {

    private var _binding: FragmentLyricsDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView, id = ${hashCode()}")
        _binding = FragmentLyricsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.lyricsTextView.text = lyric
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set lyric, exception: ${e.message}")
        }
    }
}