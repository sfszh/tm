package co.ruizhang.trademe.viewmodels

sealed class ViewResult<T> (val data: T?) {
    class Success<T>(t: T) : ViewResult<T>(t)
    class Loading<T>(t: T?) : ViewResult<T>(t)
    class Error<T>(t: T?, val throwable: Throwable): ViewResult<T>(t)
}