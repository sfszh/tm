package co.ruizhang.trademe.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class SearchResponse (
    @Expose
    @SerializedName("TotalCount")
    val totalCount : Int,
    @Expose
    @SerializedName("List")
    val list : List<ListingApiModel>
)

open class ListingApiModel(
    @Expose
    @SerializedName("ListingId")
    val listingId: Long,
    @Expose
    @SerializedName("Title")
    val title : String,
    @Expose
    @SerializedName("Number")
    val price : Double,
    @SerializedName("PictureHref")
    val imageUrl : String?
)
