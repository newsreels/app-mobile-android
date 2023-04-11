package com.ziro.bullet.model.discoverNew.scorecard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Com (

	@SerializedName("Ov") val ov : Double,
	@SerializedName("Aid") val aid : Int,
	@SerializedName("Oid") val oid : Int,
	@SerializedName("T") val t : String,
//	@SerializedName("S") val s : String,
//	@SerializedName("Sv") val sv : String
) : Parcelable