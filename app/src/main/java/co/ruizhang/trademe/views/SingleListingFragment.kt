package co.ruizhang.trademe.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import co.ruizhang.trademe.R
import co.ruizhang.trademe.databinding.FragmentSingleListingBinding
import co.ruizhang.trademe.viewmodels.CategoryViewModel
import co.ruizhang.trademe.viewmodels.SearchListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SingleListingFragment : Fragment(), ListingListClickListener {
    private val viewModel: SearchListViewModel by viewModel()
    private lateinit var binding: FragmentSingleListingBinding
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    val args: SingleListingFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleListingBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        return binding.root
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
                newText?.let {
                    viewModel.setSearchText(it)
                }
                return true
            }

        })
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        viewModel.setSearchCategory(args.categoryId)
        binding.listingList.adapter = ListingListAdapter(this)
        binding.listingList.layoutManager = LinearLayoutManager(context)
        viewModel.viewData
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { viewResult ->
                    Timber.d("viewResult")
                    viewResult.data?.let {
                        (binding.listingList.adapter as ListingListAdapter).submitList(it)
                    }
                },
                onError = {
                    Timber.e(it)

                }
            )
            .addTo(disposable)
    }


    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    override fun onListingItemClicked(id: Long) {

    }
}