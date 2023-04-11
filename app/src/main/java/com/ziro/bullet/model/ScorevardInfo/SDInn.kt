package com.ziro.bullet.model.ScorevardInfo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SDInn (

  @SerializedName("Pt"  ) var Pt  : Int?           = null,
  @SerializedName("Wk"  ) var Wk  : Int?           = null,
  @SerializedName("Ov"  ) var Ov  : Double?           = null,
  @SerializedName("Ti"  ) var Ti  : String?        = null,
  @SerializedName("Tn"  ) var Tn  : Int?           = null,
  @SerializedName("Inn" ) var Inn : Int?           = null,
  @SerializedName("Rr"  ) var Rr  : Double?        = null,
  @SerializedName("Ex"  ) var Ex  : Int?           = null,
  @SerializedName("B"   ) var B   : Int?           = null,
  @SerializedName("LB"  ) var LB  : Int?           = null,
  @SerializedName("NB"  ) var NB  : Int?           = null,
  @SerializedName("WB"  ) var WB  : Int?           = null,
  @SerializedName("Pen" ) var Pen : Int?           = null,
  @SerializedName("Bat" ) var Bat : ArrayList<Bat> = arrayListOf(),
//  @SerializedName("Bow" ) var Bow : ArrayList<Bow> = arrayListOf(),
//  @SerializedName("FoW" ) var FoW : ArrayList<FoW> = arrayListOf(),
//  @SerializedName("Com" ) var Com : ArrayList<Com> = arrayListOf(),
//  @SerializedName("Ovr" ) var Ovr : ArrayList<Ovr> = arrayListOf()

) : Parcelable