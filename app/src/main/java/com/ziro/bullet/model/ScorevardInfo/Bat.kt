package com.ziro.bullet.model.ScorevardInfo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
@Parcelize
data class Bat (

  @SerializedName("Pid"  ) var Pid  : Int?    = null,
  @SerializedName("Lp"   ) var Lp   : Int?    = null,
  @SerializedName("R"    ) var R    : Int?    = null,
//  @SerializedName("$4"   ) var $4   : Int?    = null,
//  @SerializedName("$6"   ) var $6   : Int?    = null,
  @SerializedName("B"    ) var B    : Int?    = null,
  @SerializedName("Bid"  ) var Bid  : Int?    = null,
  @SerializedName("Fid"  ) var Fid  : Int?    = null,
  @SerializedName("Sr"   ) var Sr   : Double? = null,
  @SerializedName("LpTx" ) var LpTx : String? = null,
  @SerializedName("A"    ) var A    : Int?    = null,
  @SerializedName("Pl"   ) var Pl   : Int?    = null

) : Parcelable