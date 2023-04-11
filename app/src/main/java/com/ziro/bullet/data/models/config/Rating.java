package com.ziro.bullet.data.models.config;

import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("interval")
    private int interval;

    @SerializedName("next_interval")
    private int nextInterval;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getNextInterval() {
        return nextInterval;
    }

    public void setNextInterval(int nextInterval) {
        this.nextInterval = nextInterval;
    }
}
