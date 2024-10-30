package com.example.example

import com.google.gson.annotations.SerializedName


data class FoW (

  @SerializedName("Pid" ) var Pid : Int?    = null,
  @SerializedName("Bid" ) var Bid : Int?    = null,
  @SerializedName("R"   ) var R   : Int?    = null,
  @SerializedName("B"   ) var B   : Double? = null,
  @SerializedName("Wk"  ) var Wk  : Int?    = null,
  @SerializedName("WkN" ) var WkN : Int?    = null,
  @SerializedName("Co"  ) var Co  : String? = null

)