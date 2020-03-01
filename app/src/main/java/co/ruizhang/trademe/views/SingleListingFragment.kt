package co.ruizhang.trademe.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import co.ruizhang.trademe.R
import co.ruizhang.trademe.databinding.FragmentSingleListingBinding
import timber.log.Timber

class SingleListingFragment : Fragment() {
    private lateinit var binding: FragmentSingleListingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =  FragmentSingleListingBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        val search = menu.findItem(R.id.search)
        val searchView: SearchView = MenuItemCompat.getActionView(search) as SearchView
        initSearch(searchView)
    }

    private fun initSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { Timber.d(it) }
                return true
            }

        })

    }
}