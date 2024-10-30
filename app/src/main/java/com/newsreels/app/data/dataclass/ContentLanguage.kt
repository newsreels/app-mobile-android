package com.newsreels.app.data.dataclass

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.newsreels.app.data.models.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentLanguage (
    var id: String,
    var name: String,
    var sample: String,
    var image: String,
    var favorite: Boolean
) :Parcelable {
    fun deepCopy() : ContentLanguage {
        return Gson().fromJson(Gson().toJson(this), this.javaClass)
    }
}

