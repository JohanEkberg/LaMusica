package se.westpay.lamusica.ui.startup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.ui.category.CategoryFragment
import se.westpay.lamusica.ui.settings.SettingsFragment


/**
 * A simple [Fragment] subclass.
 * Use the [StartupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartupFragment : Fragment() {

    private lateinit var _startupViewModel: StartupViewModel

    companion object {
        val CLASS_NAME = "${StartupFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = StartupFragment::class.simpleName ?: ""

        fun newInstance(): StartupFragment = StartupFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _startupViewModel = StartupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //_startupViewModel.startApplication(requireContext() as MainActivity, shouldClearAllTables = true,forceScan = true)
        // Inflate the layout for this fragment

        _startupViewModel.requireScan.observe(viewLifecycleOwner, Observer { requireScan ->
            if (requireScan) {
                (requireActivity() as MainActivity).loadFragment(SettingsFragment.FRAGMENT_NAME)
            } else {
                (requireActivity() as MainActivity).loadFragment(CategoryFragment.FRAGMENT_NAME)
            }

        })
        return inflater.inflate(R.layout.fragment_startup, container, false)
    }

    fun startApplication() {
        _startupViewModel.startApplication(requireContext() as MainActivity)
    }
}