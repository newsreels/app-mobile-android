package com.ziro.bullet.model.discoverNew.weather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class WeatherForecastResponse(
    val current: @RawValue Current,
    val forecast: @RawValue Forecast,
    val location: @RawValue Location,
    var isfernite: Boolean = false
) : Parcelable

