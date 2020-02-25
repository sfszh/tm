package co.ruizhang.trademe.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

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
                    root.flat()
                }
                .map { ResultData.Success(it) }
        }

    override fun load(useCache: Boolean) {
        loadEvent.onNext(useCache)
    }

    private fun CategoryApiModel.flat(): List<Category> {
        val nodesToVisit = mutableListOf(this)
        val domainModelList = mutableListOf<Category>()
        while (nodesToVisit.isNotEmpty()) {
            val currentNode = nodesToVisit.first()
            nodesToVisit.removeAt(0)
            currentNode.subcategories?.let { nodesToVisit.addAll(it) }
            if(currentNode.path != null) {
                domainModelList.add(
                    Category(
                        currentNode.id,
                        currentNode.name,
                        currentNode.path,
                        currentNode.isLeaf
                    )
                )
            } else {
                Timber.d("path is null?")
            }
        }
        return domainModelList

    }

}
