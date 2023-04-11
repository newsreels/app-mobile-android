package com.ziro.bullet.model.discoverNew.trading

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LastTrade(
    @SerializedName("c") val tradeCondition: List<Int>?,
    @SerializedName("i") val id: String,
    @SerializedName("p") val tradePrice: Double,
    @SerializedName("s") val tradeSize: Double,
    @SerializedName("t") val time: Long,
    @SerializedName("x") val exchange: Double
): Parcelable