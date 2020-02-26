package co.ruizhang.trademe.viewmodels

import androidx.lifecycle.ViewModel
import co.ruizhang.trademe.data.CategoryRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class CategoryViewModel constructor(
    private val repository: CategoryRepository
) : ViewModel() {
    private val currentSelection: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    val viewData: Observable<ViewResult<CategoryPageViewData>> =
        Observables.combineLatest(currentSelection, repository.categories)
            .map{ (selectedId, categroyResult) ->
                val category =
                    categroyResult.data ?: return@map ViewResult.Error<CategoryPageViewData>(
                        null,
                        IllegalStateException()
                    )
                val currentCat =
                    category.find(selectedId) ?: return@map ViewResult.Error<CategoryPageViewData>(
                        null,
                        IllegalStateException()
                    )
                val subCats = currentCat.subCategories.map {
                    CategoryViewData(it.id, it.name, it.path.toPathNodes(), it.isLeaf)
                }

                val currentPathNodes = currentCat.path.toPathNodes()
                val pageViewData = CategoryPageViewData(currentPathNodes, subCats)
                return@map ViewResult.Success(pageViewData)
            }


    val _navigate: PublishSubject<NavigateEvent.VisitSearchList> = PublishSubject.create()
    val navigate: Observable<NavigateEvent.VisitSearchList> = _navigate

    private val _message: PublishSubject<String> = PublishSubject.create()
    val message: Observable<String> = _message

    fun start() {
        repository.load(true)
    }

    fun selectCategory(category: CategoryViewData) {
        if (category.isLeaf) {
            Timber.d("is Leaf")
            _message.onNext("is Leaf")
        } else {
            currentSelection.onNext(category.id)
        }

    }

    fun selectPathNode(indexNumber: Int) {
        //TODO add path node selection
    }


    fun save() {
        val selectedId = currentSelection.value ?: ""
        _navigate.onNext(NavigateEvent.VisitSearchList(selectedId))
    }

    companion object {
        val ROOT_NODE = CategoryViewData("", "Root", emptyList(), false)
    }

    private fun String.toPathNodes(): List<PathNodeViewData> {
        return this.split("/").mapIndexed { index, s ->
            PathNodeViewData(index, s)
        }
    }
}

data class CategoryPageViewData(
    val path: List<PathNodeViewData>,
    val categories: List<CategoryViewData>
)

data class PathNodeViewData(
    val id: Int,
    val name: String
)

data class CategoryViewData(
    val id: String,
    val name: String,
    val path: List<PathNodeViewData>,
    val isLeaf: Boolean
)

data class Selection(
    val categoryChain: List<String> = emptyList()
)


