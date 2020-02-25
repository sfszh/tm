package co.ruizhang.trademe.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.ruizhang.trademe.databinding.FragmentMainBinding
import co.ruizhang.trademe.viewmodels.CategoryViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainFragment : Fragment() {
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
            val action =
                MainFragmentDirections.actionMainFragmentToSingleListingFragment("categoryid-placeholder")
            findNavController().navigate(action)
        }

        viewModel.viewData
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { viewData ->
                    Timber.d("viewData receive")
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
}