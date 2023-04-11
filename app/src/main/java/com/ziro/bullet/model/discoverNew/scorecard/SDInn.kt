package com.ziro.bullet.model.discoverNew.scorecard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SDInn (

	@SerializedName("Pt") val pt : Int,
	@SerializedName("Wk") val wk : Int,
	@SerializedName("Ov") val ov : Double,
	@SerializedName("Ti") val ti : String,
	@SerializedName("Tn") val tn : Int,
	@SerializedName("Inn") val inn : Int,
	@SerializedName("Rr") val rr : Double,
	@SerializedName("Ex") val ex : Int,
	@SerializedName("B") val b : Int,
	@SerializedName("LB") val lB : Int,
	@SerializedName("NB") val nB : Int,
	@SerializedName("WB") val wB : Int,
	@SerializedName("Pen") val pen : Int,
	@SerializedName("Bat") var bat : List<Bat>,
	@SerializedName("Bow") val bow : List<Bow>,
	@SerializedName("FoW") val foW : List<FoW>,
	@SerializedName("Com") val com : List<Com>,
	@SerializedName("Ovr") val ovr : List<Ovr>
) : Parcelable
