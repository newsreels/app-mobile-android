package com.ziro.bullet.model.ScorevardInfo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScorecardBase (

  @SerializedName("Eid"   ) var Eid   : String?          = null,
  @SerializedName("SDInn" ) var SDInn : ArrayList<SDInn> = arrayListOf(),
//  @SerializedName("Prns"  ) var Prns  : ArrayList<Prns>  = arrayListOf()

) : Parcelable