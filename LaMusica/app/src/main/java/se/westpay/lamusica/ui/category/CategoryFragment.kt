package se.westpay.lamusica.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import se.westpay.lamusica.MainActivity
import se.westpay.lamusica.R
import se.westpay.lamusica.ui.lyrics.LyricsFragment

class CategoryFragment : Fragment() {
    private val _classTag = javaClass.simpleName
    private lateinit var _categoryTabLayout: TabLayout
    private lateinit var _categoryViewPager: ViewPager2
    private lateinit var _categoryViewModel: CategoryViewModel

    companion object {
        val CLASS_NAME = "${CategoryFragment::class.simpleName} ${Integer.toHexString(hashCode())}"
        val FRAGMENT_NAME = CategoryFragment::class.simpleName ?: ""
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _categoryViewModel =
            ViewModelProvider(this)[CategoryViewModel::class.java]

        val root = inflater.inflate(R.layout.fragment_category, container, false)
        val context = requireActivity() as MainActivity

        // Setup viewpager2
        _categoryViewPager = root.findViewById(R.id.categoryViewPager)
        _categoryTabLayout = root.findViewById(R.id.categoryTabs)
        _categoryViewPager.adapter = CategoryAdapter(context)

        // Setup tabs.
       TabLayoutMediator(_categoryTabLayout, _categoryViewPager) { tab, position ->
           tab.text = when (position) {
                    0 -> resources.getString(R.string.tab_album)
                    1 -> resources.getString(R.string.tab_artist)
                    2 -> resources.getString(R.string.tab_playqueue)
                    else -> resources.getString(R.string.tab_album)
           }
        }.attach()

        // To get swipe event of viewpager2
        _categoryViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position) {
                    0 -> {
                        //(_categoryViewPager.adapter as CategoryAdapter).notifyItemChanged(0)
                    }
                    1 -> {
                        //(_categoryViewPager.adapter as CategoryAdapter).notifyItemChanged(1)
                    }
                    2 -> {
                        //(_categoryViewPager.adapter as CategoryAdapter).notifyItemChanged(2)
                    }
                    else -> {}
                }

            }
        })

        //_categoryViewPager.unregisterOnPageChangeCallback() TODO: Pass the registered callback as parameter

        return root
    }
}