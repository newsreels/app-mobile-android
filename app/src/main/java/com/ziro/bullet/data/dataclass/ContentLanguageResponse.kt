package com.ziro.bullet.data.dataclass

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentLanguageResponse(
    @SerializedName("languages")
    var languageList: ArrayList<ContentLanguage>,
    var meta: Meta
) : Parcelable