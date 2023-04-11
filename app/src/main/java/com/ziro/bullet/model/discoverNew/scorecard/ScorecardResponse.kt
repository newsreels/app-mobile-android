package com.ziro.bullet.model.discoverNew.scorecard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScorecardResponse (

	@SerializedName("Eid") val eid : Int,
	@SerializedName("SDInn") val sDInn : List<SDInn>,
	@SerializedName("Prns") val prns : List<Prns>
) : Parcelable