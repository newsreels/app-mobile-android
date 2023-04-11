package com.ziro.bullet.model.discoverNew.scorecard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoW (

	@SerializedName("Pid") val pid : Int,
	@SerializedName("Bid") val bid : Int,
	@SerializedName("R") val r : Int,
	@SerializedName("B") val b : Double,
	@SerializedName("Wk") val wk : Int,
	@SerializedName("WkN") val wkN : Int,
	@SerializedName("Co") val co : String
) : Parcelable