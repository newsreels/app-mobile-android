package com.newsreels.app.model.discoverNew.trading

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LastQuote(
    @SerializedName("a") val askPrice: Double,
    @SerializedName("b") val bidPrice: Double,
    val t: Long,
    val x: Int
): Parcelable