package com.ziro.bullet.model.Reel;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ReelResponse {

    @SerializedName("meta")
    private Meta meta;

    @SerializedName("reels")
    private List<ReelsItem> reels;

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setReels(List<ReelsItem> reels) {
        this.reels = reels;
    }

    public List<ReelsItem> getReels() {
        return reels;
    }
}