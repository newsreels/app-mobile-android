package com.ziro.bullet.APIResources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point {
    @Expose
    @SerializedName("longitude")
    private int mLongitude;
    @Expose
    @SerializedName("latitude")
    private int mLatitude;

    public int getLongitude() {
        return mLongitude;
    }

    public void setLongitude(int mLongitude) {
        this.mLongitude = mLongitude;
    }

    public int getLatitude() {
        return mLatitude;
    }

    public void setLatitude(int mLatitude) {
        this.mLatitude = mLatitude;
    }
}
