package com.newsreels.app.data.dataclass

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.newsreels.app.data.models.location.Location
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Region(
    var id: String = "",
    var name: String = "",
    var city: String = "",
    var image: String = "",
    var flag: String= "",
    var country: String = "",
    var favorite: Boolean = false
) : Parcelable

@Parcelize
data class RegionResponse(
    @SerializedName("locations")
    var regionList: ArrayList<Region>,
    var meta: Meta
) :Parcelable