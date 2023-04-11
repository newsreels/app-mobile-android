package com.ziro.bullet.model;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;

import java.util.ArrayList;

public class CategorizedChannelsData {

    @SerializedName("channels")
    private ArrayList<Source> channels;
    @SerializedName("title")
    private String title;

    public ArrayList<Source> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Source> channels) {
        this.channels = channels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
