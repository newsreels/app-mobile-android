package com.ziro.bullet.data.dataclass

import com.google.gson.annotations.SerializedName
import com.ziro.bullet.data.models.topics.Topics

data class OnBoardingModel(
    @SerializedName("languages")
    val languageList: ArrayList<ContentLanguage>,

    @SerializedName("location")
    val regions: ArrayList<Region>,

    @SerializedName("topics")
    val topics: ArrayList<Topics>
)

data class SaveOnBoardingModel(
    @SerializedName("languages")
    val languageList: List<String>,

    @SerializedName("locations")
    val regions: List<String>,

    @SerializedName("topics")
    val topics: List<String>
)


