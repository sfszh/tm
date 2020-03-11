package co.ruizhang.trademe.viewmodels

import androidx.lifecycle.ViewModel
import co.ruizhang.trademe.data.CategoryRepository
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class CategoryViewModel constructor(
    private val repository: CategoryRepository
) : ViewModel() {
    private val currentSelection: BehaviorSubject<Selection> = BehaviorSubject.createDefault(
        Selection.CategorySelection("")
    )
    val viewData: Observable<ViewResult<CategoryPageViewData>> =
        Observables.combineLatest(currentSelection, repository.categories)
            .map { (selection, categroyResult) ->
                val category =
                    categroyResult.data ?: return@map ViewResult.Error<CategoryPageViewData>(
                        null,
                        IllegalStateException()
                    )
                val currentCat = when (selection) {
                    is Selection.PathSelection -> category.findByPath(selection.path)
                    is Selection.CategorySelection -> {
                        _selectedCategory.onNext(selection.id)
                        category.find(selection.id)
                    }
                } ?: return@map ViewResult.Error<CategoryPageViewData>(
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
            .startWith(ViewResult.Loading<CategoryPageViewData>(null))


    private val _navigate: PublishSubject<NavigateEvent.VisitSearchList> = PublishSubject.create()
    val navigate: Observable<NavigateEvent.VisitSearchList> = _navigate

    private val _message: PublishSubject<String> = PublishSubject.create()
    val message: Observable<String> = _message

    private val _selectedCategory: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    val selectedCategory: Observable<String> = _selectedCategory

    fun start() {
        repository.load(true)
    }

    fun selectCategory(category: CategoryViewData) {
        currentSelection.onNext(Selection.CategorySelection(category.id))
    }

    fun selectPathNode(path: String) {
        currentSelection.onNext(Selection.PathSelection(path))
    }


    fun save() {
        _navigate.onNext(NavigateEvent.VisitSearchList(_selectedCategory.value ?: ""))
    }

    private fun String.toPathNodes(): List<PathNodeViewData> {
        val nodeList = this.split("/")
        return nodeList.mapIndexed { index, s ->
            val path = nodeList.subList(0, index + 1).joinToString("/")
            PathNodeViewData(path, s)
        }
    }
}

data class CategoryPageViewData(
    val path: List<PathNodeViewData>,
    val categories: List<CategoryViewData>
)

data class PathNodeViewData(
    val path: String,
    val name: String
)

data class CategoryViewData(
    val id: String,
    val name: String,
    val path: List<PathNodeViewData>,
    val isLeaf: Boolean
)

sealed class Selection {
    data class PathSelection(val path: String) : Selection()
    data class CategorySelection(val id: String) : Selection()
}



