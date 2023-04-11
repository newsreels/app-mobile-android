package com.ziro.bullet.model.discoverNew.scorecard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bow (

	@SerializedName("Pid") val pid : Int,
	@SerializedName("Ov") val ov : Int,
	@SerializedName("Md") val md : Int,
	@SerializedName("R") val r : Int,
	@SerializedName("Wk") val wk : Int,
	@SerializedName("NB") val nB : Int,
	@SerializedName("WB") val wB : Int,
	@SerializedName("Er") val er : Double,
	@SerializedName("A") val a : Int
) : Parcelable