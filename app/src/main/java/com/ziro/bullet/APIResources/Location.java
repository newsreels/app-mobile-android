package com.ziro.bullet.APIResources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {
    @Expose
    @SerializedName("point")
    private Point mPoint;
    @Expose
    @SerializedName("global")
    private boolean mGlobal;
    @Expose
    @SerializedName("country")
    private String mCountry;
    @Expose
    @SerializedName("state")
    private String mState;
    @Expose
    @SerializedName("county")
    private String mCounty;
    @Expose
    @SerializedName("city")
    private String mCity;

    public Point getPoint() {
        return mPoint;
    }

    public void setPoint(Point mPoint) {
        this.mPoint = mPoint;
    }

    public boolean getGlobal() {
        return mGlobal;
    }

    public void setGlobal(boolean mGlobal) {
        this.mGlobal = mGlobal;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getState() {
        return mState;
    }

    public void setState(String mState) {
        this.mState = mState;
    }

    public String getCounty() {
        return mCounty;
    }

    public void setCounty(String mCounty) {
        this.mCounty = mCounty;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }
}
