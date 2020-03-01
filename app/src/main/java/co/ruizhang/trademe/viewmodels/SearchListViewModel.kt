package co.ruizhang.trademe.viewmodels

import androidx.lifecycle.ViewModel
import co.ruizhang.trademe.data.ResultData
import co.ruizhang.trademe.data.SearchRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject

class SearchListViewModel(private val searchRepository: SearchRepository) : ViewModel() {
    private val categoryEvent: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    private val searchTextEvent: BehaviorSubject<String> = BehaviorSubject.create()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    val viewData: Observable<ViewResult<List<ListingItemViewData>>> = searchRepository.listings
        .map { result ->
            val list = result.data ?: return@map ViewResult.Error<List<ListingItemViewData>>(
                null,
                IllegalStateException()
            )

            return@map list.map {
                ListingItemViewData(
                    it.id,
                    it.title,
                    String.format("%.2f", it.price)
                )
            }.let { ViewResult.Success(it) }
        }


    init {
        searchTextEvent
            .withLatestFrom(categoryEvent)
            .map { (searchText, category) ->
                searchRepository.search(searchText, category)
            }
            .subscribeBy(
                {}, {}
            )
            .addTo(compositeDisposable)

    }


    fun setSearchText(searchText: String) {
        searchTextEvent.onNext(searchText)
    }


    fun setSearchCategory(searchCategory: String) {
        categoryEvent.onNext(searchCategory)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

data class ListingItemViewData(
    val id: Long,
    val name: String,
    val price: String
)


