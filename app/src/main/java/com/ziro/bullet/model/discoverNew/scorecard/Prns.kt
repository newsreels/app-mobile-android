package com.ziro.bullet.model.discoverNew.scorecard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Prns (

	@SerializedName("Pid") val pid : Int,
	@SerializedName("Fn") val fn : String,
	@SerializedName("Ln") val ln : String,
	@SerializedName("Snm") val snm : String
) : Parcelable