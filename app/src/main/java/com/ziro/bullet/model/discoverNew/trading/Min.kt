package com.ziro.bullet.model.discoverNew.trading

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Min(
    @SerializedName("c") val closingPrice: Double,
    @SerializedName("h") val highestPrice: Double,
    @SerializedName("l") val lowestPrice: Double,
    @SerializedName("o") val openingPrice: Double,
    @SerializedName("v") val volume: Double,
    @SerializedName("vw") val volumeWeight: Double
): Parcelable