package com.ziro.bullet.data.models.config;

import com.google.gson.annotations.SerializedName;

public class Terms {
    @SerializedName("community")
    private boolean community;

    public boolean getCommunity() {
        return community;
    }

    public void setCommunity(boolean community) {
        this.community = community;
    }
}
