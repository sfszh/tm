package co.ruizhang.trademe.views

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.ruizhang.trademe.R
import co.ruizhang.trademe.databinding.FragmentMainBinding
import co.ruizhang.trademe.databinding.PathNodeBinding
import co.ruizhang.trademe.viewmodels.CategoryViewData
import co.ruizhang.trademe.viewmodels.CategoryViewModel
import co.ruizhang.trademe.viewmodels.SearchListViewModel
import co.ruizhang.trademe.viewmodels.ViewResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainFragment : Fragment(), CategoryListClickListener, ListingListClickListener {
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val searchViewModel: SearchListViewModel by viewModel()


    private lateinit var binding: FragmentMainBinding
    private var disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Do some stuff

            inflater.inflate(R.menu.menu_main, menu)
            val search = menu.findItem(R.id.search)
            val searchView: SearchView = MenuItemCompat.getActionView(search) as SearchView
            initSearch(searchView)
        }
    }


    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        binding.chooseButton.setOnClickListener {
            categoryViewModel.save()

        }
        binding.categoryList.adapter = CategoryListAdapter(this)
        binding.categoryList.layoutManager = LinearLayoutManager(context)

        categoryViewModel.viewData
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { viewData ->
                    Timber.d("viewData receive")
                    when (viewData) {
                        is ViewResult.Loading -> {
                            if (viewData.data == null) {
                                binding.emptyStateProgress!!.visibility = View.VISIBLE
                            }
                            binding.emptyStateText!!.visibility = View.INVISIBLE

                        }
                        is ViewResult.Success -> viewData.data?.let { pageViewData ->
                            binding.emptyStateProgress!!.visibility = View.INVISIBLE
                            binding.emptyStateProgress!!.visibility = View.INVISIBLE
                            (binding.categoryList.adapter as CategoryListAdapter).submitList(
                                pageViewData.categories
                            )
                            binding.nodesContainer?.removeAllViews()
                            pageViewData.path
                                .map { viewData ->
                                    val nodeView = PathNodeBinding.inflate(layoutInflater)
                                    nodeView.node.text =
                                        if (viewData.name.isEmpty()) "root" else viewData.name
                                    nodeView.node.setOnClickListener {
                                        categoryViewModel.selectPathNode(viewData.path)
                                    }
                                    nodeView
                                }
                                .forEach { nodeView ->
                                    binding.nodesContainer?.addView(nodeView.node)
                                }
                        }
                        is ViewResult.Error -> {
                            binding.emptyStateText!!.visibility = View.VISIBLE
                            binding.emptyStateProgress!!.visibility = View.INVISIBLE

                        }
                    }
                },

                onError = {
                    Timber.e(it)
                })
            .addTo(disposable)
        categoryViewModel.selectedCategory
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { categoryId ->
                    searchViewModel.setSearchCategory(categoryId)
                },
                onError = {
                    Timber.e(it)
                }
            )

        binding.listingList.adapter = ListingListAdapter(this)
        binding.listingList.layoutManager = LinearLayoutManager(context)
        searchViewModel.viewData
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



        categoryViewModel.navigate
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { event ->
                    val action =
                        MainFragmentDirections.actionMainFragmentToSingleListingFragment(event.categoryId)
                    findNavController().navigate(action)
                },
                onError = {
                    Timber.e(it)
                }

            )

    }

    override fun onResume() {
        super.onResume()
        categoryViewModel.start()
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }


    private fun initSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchViewModel.setSearchText(it)
                }
                return true
            }

        })
    }

    override fun onCategoryClicked(category: CategoryViewData) {
        categoryViewModel.selectCategory(category)
    }

    override fun onListingItemClicked(id: Long) {
        val action = MainFragmentDirections.actionMainFragmentToListingDetailFragment(id.toString())
        findNavController().navigate(action)
    }
}