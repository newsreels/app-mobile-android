package com.ziro.bullet.model.discoverNew.tabletest

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class L(

    @SerializedName("Tables") var Tables: ArrayList<Tables> = arrayListOf()

) : Parcelable