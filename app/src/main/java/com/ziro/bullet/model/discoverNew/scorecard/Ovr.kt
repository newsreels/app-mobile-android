package com.ziro.bullet.model.discoverNew.scorecard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ovr (

	@SerializedName("OvN") val ovN : Int,
	@SerializedName("Onm") val onm : String,
	@SerializedName("R") val r : Int,
	@SerializedName("Wk") val wk : Int,
	@SerializedName("OvS") val ovS : String,
	@SerializedName("OvT") val ovT : List<String>
) : Parcelable