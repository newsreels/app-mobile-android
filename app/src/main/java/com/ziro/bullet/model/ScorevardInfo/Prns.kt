package com.example.example

import com.google.gson.annotations.SerializedName


data class Prns (

  @SerializedName("Pid" ) var Pid : String? = null,
  @SerializedName("Fn"  ) var Fn  : String? = null,
  @SerializedName("Ln"  ) var Ln  : String? = null,
  @SerializedName("Snm" ) var Snm : String? = null

)