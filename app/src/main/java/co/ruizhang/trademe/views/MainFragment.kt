package co.ruizhang.trademe.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.ruizhang.trademe.databinding.FragmentMainBinding
import co.ruizhang.trademe.databinding.PathNodeBinding
import co.ruizhang.trademe.viewmodels.CategoryViewData
import co.ruizhang.trademe.viewmodels.CategoryViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainFragment : Fragment(), CategoryListClickListener {
    private val viewModel: CategoryViewModel by viewModel()

    private lateinit var binding: FragmentMainBinding
    private var disposable: CompositeDisposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        binding.chooseButton.setOnClickListener {
            viewModel.save()

        }
        binding.categoryList.adapter = CategoryListAdapter(this)
        binding.categoryList.layoutManager = LinearLayoutManager(context)

        viewModel.viewData
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { viewData ->
                    Timber.d("viewData receive")
                    viewData.data?.let { pageViewData ->
                        (binding.categoryList.adapter as CategoryListAdapter).submitList(
                            pageViewData.categories
                        )
                        binding.nodesContainer?.removeAllViews()
                        pageViewData.path
                            .map { viewData ->
                                val nodeView = PathNodeBinding.inflate(layoutInflater)
                                nodeView.node.text = viewData.name
                                nodeView.node.setOnClickListener {
                                    viewModel.selectPathNode(viewData.path)
                                }
                                nodeView
                            }
                            .forEach { nodeView ->
                                binding.nodesContainer?.addView(nodeView.node)
                            }
                    }
                },

                onError = {
                    Timber.e(it)
                })
            .addTo(disposable)

        viewModel.navigate
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
        viewModel.start()
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    override fun onPathNodeClicked(path: String) {
        viewModel.selectPathNode(path)
    }

    override fun onCategoryClicked(category: CategoryViewData) {
        viewModel.selectCategory(category)
    }
}