package co.ruizhang.trademe.viewmodels

import androidx.lifecycle.ViewModel

class SearchListViewModel : ViewModel() {

}

data class ListingItemViewData (
    val id : String,
    val name : String,
    val price : String
)


