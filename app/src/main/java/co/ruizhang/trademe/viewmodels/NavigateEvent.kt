package co.ruizhang.trademe.viewmodels

sealed class NavigateEvent {
    class VisitSearchList(val categoryId : String) : NavigateEvent()
}