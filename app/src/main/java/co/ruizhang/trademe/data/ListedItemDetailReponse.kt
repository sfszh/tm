package co.ruizhang.trademe.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ListedItemDetailReponse(
    @Expose
    @SerializedName("ListingId")
    val listingId: Long,
    @Expose
    @SerializedName("Title")
    val title: String,
    @Expose
    @SerializedName("StartPrice")
    val startPrice: String,
    @Expose
    @SerializedName("PictureHref")
    val pictureHref: String?
)