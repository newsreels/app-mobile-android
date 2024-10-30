package com.newsreels.app.model.discoverNew.trading

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.newsreels.app.data.models.BaseModel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class CryptoForexApiResponse(
    @SerializedName("meta")
    var meta: @RawValue BaseModel.Meta? = null,
    val status: String?,
    var tickers: List<Ticker>?
) : Parcelable