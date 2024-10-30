package com.example.example

import com.google.gson.annotations.SerializedName


data class Bow (

  @SerializedName("Pid" ) var Pid : Int?    = null,
  @SerializedName("Ov"  ) var Ov  : Int?    = null,
  @SerializedName("Md"  ) var Md  : Int?    = null,
  @SerializedName("R"   ) var R   : Int?    = null,
  @SerializedName("Wk"  ) var Wk  : Int?    = null,
  @SerializedName("NB"  ) var NB  : Int?    = null,
  @SerializedName("WB"  ) var WB  : Int?    = null,
  @SerializedName("Er"  ) var Er  : Double? = null,
  @SerializedName("A"   ) var A   : Int?    = null

)