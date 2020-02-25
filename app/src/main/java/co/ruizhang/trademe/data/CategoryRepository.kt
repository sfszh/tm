package co.ruizhang.trademe.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

data class Category(
    val id: String,
    val name: String,
    val path: String,
    val isLeaf: Boolean
)

interface CategoryRepository {
    val categories: Observable<ResultData<List<Category>>>
    fun load(useCache: Boolean)
}


class CategoryRepositoryImpl constructor(
    private val api: TrademeApi
) : CategoryRepository {
    private val loadEvent: PublishSubject<Boolean> = PublishSubject.create()

    override val categories: Observable<ResultData<List<Category>>> = loadEvent
        .flatMapSingle {
            api.getCategory()
                .subscribeOn(Schedulers.io())
                .map { root ->
                    root.subcategories
                        .map {
                            Category(it.id, it.name, it.path, it.isLeaf)
                        }
                }
                .map { ResultData.Success(it) }
        }

    override fun load(useCache: Boolean) {
        loadEvent.onNext(useCache)
    }
}
