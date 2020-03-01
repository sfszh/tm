package co.ruizhang.trademe.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface TrademeApi {
    @GET("/v1/Categories.json")
    @Headers("Content-type: application/json")
    fun getCategory(): Single<CategoryApiModel>

    @GET("/v1/Search/General.json")
    @Headers("Content-type: application/json")
    fun search(
        @Header("Authorization") authorization: String,
        @Query("rows") rows: Int,
        @Query("search_string") searchString: String,
        @Query("category") category: String,
        @Query("page") page: Int
    ): Single<SearchResponse>
}