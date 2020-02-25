package co.ruizhang.trademe.viewmodels

import androidx.lifecycle.ViewModel
import co.ruizhang.trademe.data.CategoryRepository
import co.ruizhang.trademe.data.ResultData
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class CategoryViewModel constructor(
    private val repository: CategoryRepository
) : ViewModel() {
    private val currentSelection: BehaviorSubject<Selection> =
        BehaviorSubject.createDefault(Selection(listOf(ROOT_NODE)))

    val viewData: Observable<ViewResult<CategoryPageViewData>> =
        Observables.combineLatest(currentSelection, repository.categories) { selection, result ->
            val latestSelection = selection.categoryChain.last()
            val pathString = latestSelection.path.joinToString("/")
            val categoryViewDataList = result.data?.filter { it.path == pathString }?.map {
                val pathNodes =
                    it.path.split("/").mapIndexed { index, s -> PathNodeViewData(index, s) }
                CategoryViewData(it.id, it.name, pathNodes, it.isLeaf)
            } ?: emptyList()
            val nodeViewData = latestSelection.path
            val viewData = CategoryPageViewData(nodeViewData, categoryViewDataList)

            when (result) {
                is ResultData.Success -> ViewResult.Success(viewData)
                is ResultData.Error -> ViewResult.Error(viewData, result.throwable)
            }
        }


    private val _navigate: PublishSubject<NavigateEvent.VisitSearchList> = PublishSubject.create()
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
            val categoryChain = currentSelection.value?.categoryChain ?: listOf(ROOT_NODE)
            val newChain = Selection(categoryChain + category)
            currentSelection.onNext(newChain)
        }

    }

    fun selectPathNode(indexNumber: Int) {
        val newSelection = Selection(
            currentSelection.value?.categoryChain?.subList(0, indexNumber) ?: listOf(
                ROOT_NODE
            )
        )

        currentSelection.onNext(newSelection)
    }


    fun save() {
        val selectedCategory = currentSelection.value?.categoryChain?.last() ?: ROOT_NODE
        _navigate.onNext(NavigateEvent.VisitSearchList(selectedCategory.id))
    }

    companion object {
        val ROOT_NODE = CategoryViewData("", "Root", emptyList(), false)
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
    val categoryChain: List<CategoryViewData>
)


