package co.ruizhang.trademe.data

import co.ruizhang.trademe.BuildConfig
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

interface SearchRepository {
    val listings: Observable<ResultData<List<ListingItem>>>
    fun search(text: String, categoryId: String)
}

data class ListingItem(
    val id: Long,
    val title: String,
    val price: Double,
    val imageUrl : String?
)


class SearchRepositoryImpl(
    private val api: TrademeApi
) : SearchRepository {
    private val loadEvent: PublishSubject<Pair<String, String>> = PublishSubject.create()


    override val listings: Observable<ResultData<List<ListingItem>>> = loadEvent
        .flatMapSingle { event ->
            val searchText = event.first
            val categoryId = event.second

            val authorization =
                "OAuth oauth_consumer_key=\"${BuildConfig.CONSUMER_KEY}\",oauth_signature_method=\"PLAINTEXT\",oauth_signature=\"${
                BuildConfig.CONSUMER_SECRET
                }%26\""
            api.search(
                authorization = authorization,
                rows = 20,
                searchString = searchText,
                category = categoryId,
                page = 1
            )
                .subscribeOn(Schedulers.io())
        }
        .map { resp: SearchResponse ->
            val list = resp.list.map { ListingItem(it.listingId, it.title, it.price, it.imageUrl) }
            ResultData.Success(list) as ResultData<List<ListingItem>>
        }
        .onErrorReturn {
            ResultData.Error(null, it)
        }


    override fun search(text: String, categoryId: String) {
        loadEvent.onNext(Pair(text, categoryId))
    }
}
