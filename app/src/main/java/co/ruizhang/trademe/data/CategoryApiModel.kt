package co.ruizhang.trademe.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class CategoryApiModel (
    @Expose
    @SerializedName("Number")
    val id : String,
    @Expose
    @SerializedName("Name")
    val name : String,
    @Expose
    @SerializedName("Path")
    val path : String?,
    @Expose
    @SerializedName("Subcategories")
    val subcategories : List<CategoryApiModel>?,
    @Expose
    @SerializedName("IsLeaf")
    val isLeaf : Boolean
)