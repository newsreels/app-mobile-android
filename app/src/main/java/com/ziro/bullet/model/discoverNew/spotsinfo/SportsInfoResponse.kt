package com.ziro.bullet.model.discoverNew.spotsinfo
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SportsInfoResponse (

	@SerializedName("Eid") val eid : Int,
	@SerializedName("Esd") val esd : Long,
	@SerializedName("Vnm") val vnm : String,
	@SerializedName("VCnm") val vCnm : String,
	@SerializedName("Vneut") val vneut : Int,
	@SerializedName("Vcy") val vcy : String,
	@SerializedName("Vcid") val vcid : Int,
	@SerializedName("Refs") val refs : List<Refs>
) : Parcelable