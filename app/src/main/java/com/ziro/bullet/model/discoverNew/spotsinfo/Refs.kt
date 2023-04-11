package com.ziro.bullet.model.discoverNew.spotsinfo
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Refs (

	@SerializedName("Nm") val nm : String,
	@SerializedName("Cid") val cid : String,
	@SerializedName("Cn") val cn : String,
	@SerializedName("Kn") val kn : Int
) : Parcelable