package com.ziro.bullet.model.discoverNew.trading

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ticker(
    val day: Day?,
    val lastTrade: LastTrade?,
    val lastQuote: LastQuote?,
    val min: Min?,
    val prevDay: PrevDay?,
    var ticker: String?,
    val todaysChange: Double,
    val todaysChangePerc: Double,
    val updated: Long,
    var icon: String,
    var tickerName: String,
    var iconFrom: String,
    var iconTo: String,
    var fromName: String,
    var toName: String
) : Parcelable