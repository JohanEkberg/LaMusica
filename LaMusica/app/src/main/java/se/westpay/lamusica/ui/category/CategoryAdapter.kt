package se.westpay.lamusica.ui.category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import se.westpay.lamusica.ui.categoryalbum.AlbumsFragment
import se.westpay.lamusica.ui.categoryartist.ArtistsFragment
import se.westpay.lamusica.ui.categoryqueue.QueueFragment

class CategoryAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val CARD_ITEM_SIZE = 3
    private var _pageMap = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int {
        return CARD_ITEM_SIZE
    }

    /**
     * Create the fragments in the viewpager.
     */
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                val fragment = AlbumsFragment.newInstance()
                _pageMap[0] = fragment
                fragment
            } 1 -> {
                val fragment = ArtistsFragment.newInstance()
                _pageMap[1] = fragment
                fragment
            } 2 -> {
                val fragment = QueueFragment.newInstance()
                _pageMap[2] = fragment
                fragment
            } else -> {
                AlbumsFragment.newInstance()
            }
        }
    }

    fun refreshFragment(position: Int, fragment: Fragment) {
        notifyItemChanged(position)
    }
}