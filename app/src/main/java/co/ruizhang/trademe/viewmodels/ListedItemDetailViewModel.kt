package co.ruizhang.trademe.viewmodels

import androidx.lifecycle.ViewModel
import co.ruizhang.trademe.data.ListedItemDetail
import co.ruizhang.trademe.data.ListedItemDetailRepository
import io.reactivex.Observable

class ListedItemDetailViewModel (private val repo: ListedItemDetailRepository) :
    ViewModel() {
    val detail: Observable<ListedItemDetail> = repo.listedItemDetail

    fun start(id: Long) {
        repo.search(id)
    }
}