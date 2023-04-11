package com.ziro.bullet.model.discoverNew.scorecard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bat (

	@SerializedName("Pid") val pid : Int,
	@SerializedName("Lp") val lp : Int,
	@SerializedName("R") val r : Int,
	@SerializedName("$4") var score4 : Int,
	@SerializedName("$6") var score6 : Int,
	@SerializedName("B") val b : Int,
	@SerializedName("Bid") val bid : Int,
	@SerializedName("Fid") val fid : Int,
	@SerializedName("Sr") val sr : Double,
	@SerializedName("LpTx") val lpTx : String,
	@SerializedName("A") val a : Int,
	@SerializedName("Pl") val pl : Int
) : Parcelable