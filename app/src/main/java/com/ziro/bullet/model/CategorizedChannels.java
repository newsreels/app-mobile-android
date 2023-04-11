 package com.ziro.bullet.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CategorizedChannels {

    @SerializedName("data")
    private ArrayList<CategorizedChannelsData> channelsData;

    public ArrayList<CategorizedChannelsData> getChannelsData() {
        return channelsData;
    }

    public void setChannels(ArrayList<CategorizedChannelsData> channelsData) {
        this.channelsData = channelsData;
    }
}
