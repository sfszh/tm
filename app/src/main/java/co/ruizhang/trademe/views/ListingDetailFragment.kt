package co.ruizhang.trademe.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import co.ruizhang.trademe.databinding.FragmentListedItemBinding
import co.ruizhang.trademe.viewmodels.ListedItemDetailViewModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.Exception

class ListingDetailFragment : Fragment() {

    private val viewModel: ListedItemDetailViewModel by viewModel()
    private lateinit var binding: FragmentListedItemBinding
    private val disposable: CompositeDisposable = CompositeDisposable()

    private val args: ListingDetailFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("zz Start")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListedItemBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        val picasso = Picasso.get()
        viewModel.detail
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    picasso.load(it.pictureHref)
                        .into(binding.image, object : Callback {
                            override fun onSuccess() {
                                Timber.d("picasso success")
                            }

                            override fun onError(e: Exception?) {
                                Timber.e(e)
                            }
                        })
                    binding.price.text = it.price
                    binding.title.text = it.title
                },
                onError = {
                    Timber.e(it)
                }
            )
            .addTo(disposable)
        viewModel.start(args.listingId.toLong())

    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

}