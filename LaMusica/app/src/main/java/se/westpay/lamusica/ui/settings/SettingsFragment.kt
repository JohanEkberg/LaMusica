package se.westpay.lamusica.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.TAG
import se.westpay.lamusica.databinding.FragmentSettingsBinding
import se.westpay.lamusica.ui.category.CategoryFragment
import se.westpay.lamusica.ui.categoryartist.ArtistAdapter
import se.westpay.lamusica.ui.search.SearchFragment

class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var _settingsViewModel: SettingsViewModel
    private lateinit var _logScanAdapter: LogScanAdapter

    companion object {
        val CLASS_NAME = "${SettingsFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = SettingsFragment::class.simpleName ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView $CLASS_NAME")
        _settingsViewModel = SettingsViewModel()

        _settingsViewModel.scanDone.observe(viewLifecycleOwner, Observer { success ->
            manageProgressBar(false)
            if (success) {
                (requireActivity() as MainActivity).loadFragment(CategoryFragment.FRAGMENT_NAME)
            } else {
                Toast.makeText(context, "Scan failed!", Toast.LENGTH_SHORT).show()
            }
        })

        _settingsViewModel.artistAdded.observe(viewLifecycleOwner, Observer { artist ->
            if (artist.isNotEmpty()) {
                _logScanAdapter.addLogEntry(artist)

                // Auto-scroll to the latest item
                binding.logEntriesRecycleView.scrollToPosition(_logScanAdapter.itemCount - 1)
            }
        })

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated $CLASS_NAME")
        _logScanAdapter = LogScanAdapter()
        binding.logEntriesRecycleView.adapter = _logScanAdapter
        binding.logEntriesRecycleView.layoutManager = LinearLayoutManager(requireActivity() as MainActivity)
        setupClickListener()
        manageProgressBar(false)
    }

    private fun setupClickListener() {
        binding.scanButton.setOnClickListener { _ ->
            manageProgressBar(true)
            _settingsViewModel.doScan(requireActivity() as MainActivity)
        }
    }

    private fun manageProgressBar(enable: Boolean) {
        if (enable) {
            binding.busyProgressBar.visibility = View.VISIBLE
        } else {
            binding.busyProgressBar.visibility = View.INVISIBLE
        }

    }
}