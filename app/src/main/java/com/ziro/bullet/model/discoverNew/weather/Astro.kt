package com.ziro.bullet.model.discoverNew.weather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Astro(
    val moon_illumination: String,
    val moon_phase: String,
    val moonrise: String,
    val moonset: String,
    val sunrise: String,
    val sunset: String
):Parcelable