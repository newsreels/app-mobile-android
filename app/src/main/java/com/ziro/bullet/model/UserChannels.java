package com.ziro.bullet.model;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;

import java.util.ArrayList;

public class UserChannels {

    @SerializedName("channels")
    private ArrayList<Source> channels;

    public ArrayList<Source> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Source> channels) {
        this.channels = channels;
    }
}
