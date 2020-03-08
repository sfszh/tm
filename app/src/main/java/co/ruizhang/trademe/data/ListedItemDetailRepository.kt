package co.ruizhang.trademe.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

data class ListedItemDetail(
    val id: Long,
    val title: String,
    val price: String,
    val pictureHref: String?
)

interface ListedItemDetailRepository {
    val listedItemDetail: Observable<ListedItemDetail>
    fun search(id: Long)
}

class ListedItemDetailRepositoryImpl constructor(private val api: TrademeApi) :
    ListedItemDetailRepository {

    private val getDetailEvent: PublishSubject<Long> = PublishSubject.create()

    override val listedItemDetail: Observable<ListedItemDetail> = getDetailEvent
        .flatMapSingle { id ->
            api.detail(getAuthorization(), id)
                .subscribeOn(Schedulers.io())
                .map { apiModel ->
                    ListedItemDetail(
                        apiModel.listingId,
                        apiModel.title,
                        apiModel.startPrice,
                        apiModel.pictureHref
                    )
                }
        }

    override fun search(id: Long) {
        getDetailEvent.onNext(id)
    }
}