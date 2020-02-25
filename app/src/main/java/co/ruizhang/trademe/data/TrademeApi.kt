package co.ruizhang.trademe.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers

interface TrademeApi {
    @GET("/v1/Categories.json")
    @Headers("Content-type: application/json")
    fun getCategory() : Single<CategoryApiModel>
}