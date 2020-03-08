package co.ruizhang.trademe.data

import co.ruizhang.trademe.BuildConfig
import io.reactivex.Single
import retrofit2.http.*

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

    @GET("/v1/Listings/{listing_id}.json")
    @Headers("Content-type: application/json")
    fun detail(
        @Header("Authorization") authorization: String,
        @Path(value = "listing_id", encoded = true) listingId: Long
    ): Single<ListedItemDetailReponse>
}

fun getAuthorization() : String {
   return "OAuth oauth_consumer_key=\"${BuildConfig.CONSUMER_KEY}\",oauth_signature_method=\"PLAINTEXT\",oauth_signature=\"${
    BuildConfig.CONSUMER_SECRET
    }%26\""
}