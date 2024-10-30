package com.example.example

import com.google.gson.annotations.SerializedName


data class Ovr (

  @SerializedName("OvN" ) var OvN : Int?              = null,
  @SerializedName("Onm" ) var Onm : String?           = null,
  @SerializedName("R"   ) var R   : Int?              = null,
  @SerializedName("Wk"  ) var Wk  : Int?              = null,
  @SerializedName("OvS" ) var OvS : String?           = null,
  @SerializedName("OvT" ) var OvT : ArrayList<String> = arrayListOf()

)