package com.example.example

import com.google.gson.annotations.SerializedName


data class Com (

  @SerializedName("Ov"  ) var Ov  : Int?    = null,
  @SerializedName("Aid" ) var Aid : Int?    = null,
  @SerializedName("Oid" ) var Oid : Int?    = null,
  @SerializedName("T"   ) var T   : String? = null,
  @SerializedName("S"   ) var S   : String? = null,
  @SerializedName("Sv"  ) var Sv  : String? = null

)