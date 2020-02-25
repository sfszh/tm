package co.ruizhang.trademe.viewmodels

sealed class NavigateEvent {
    class VisitSearchList(categoryId : String) : NavigateEvent()
}