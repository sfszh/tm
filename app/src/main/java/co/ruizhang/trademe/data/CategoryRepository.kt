package co.ruizhang.trademe.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

data class Category(
    val id: String,
    val name: String,
    val path: String,
    val isLeaf: Boolean,
    var subCategories: List<Category> = emptyList()
) {
    fun find(id: String): Category? {
        val nodesToVisit = mutableListOf(this)
        while (nodesToVisit.isNotEmpty()) {
            val currentNode = nodesToVisit.first()
            nodesToVisit.removeAt(0)
            currentNode.subCategories.let { nodesToVisit.addAll(it) }
            if (currentNode.id == id) {
                return currentNode
            }
        }
        return null
    }


    fun findByPath(path : String) :Category ? {
        val nodesToVisit = mutableListOf(this)
        while (nodesToVisit.isNotEmpty()) {
            val currentNode = nodesToVisit.first()
            nodesToVisit.removeAt(0)
            currentNode.subCategories.let { nodesToVisit.addAll(it) }
            if (currentNode.path == path) {
                return currentNode
            }
        }
        return null
    }
}

interface CategoryRepository {
    val categories: Observable<ResultData<Category>>
    fun load(useCache: Boolean)
}


class CategoryRepositoryImpl constructor(
    private val api: TrademeApi
) : CategoryRepository {
    private val loadEvent: PublishSubject<Boolean> = PublishSubject.create()

    override val categories: Observable<ResultData<Category>> = loadEvent
        .flatMapSingle {
            api.getCategory()
                .subscribeOn(Schedulers.io())
                .map { root ->
                    root.toDomain()
                }
                .map { ResultData.Success(it) }
        }

    override fun load(useCache: Boolean) {
        loadEvent.onNext(useCache)
    }

    private fun CategoryApiModel.toDomain(): Category {
        if (this.isLeaf) {
            return Category(
                this.id,
                this.name,
                this.path ?: "",
                this.isLeaf,
                emptyList()
            )
        } else {
            return Category(
                this.id,
                this.name,
                this.path ?: "",
                this.isLeaf,
                this.subcategories?.map { it.toDomain() } ?: emptyList()
            )
        }


    }
}
