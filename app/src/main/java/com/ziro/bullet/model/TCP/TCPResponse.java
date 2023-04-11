package com.ziro.bullet.model.TCP;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.ArrayList;

public class TCPResponse extends BaseModel {

    @SerializedName("topics")
    private ArrayList<TCP> topics;

    @SerializedName("sources")
    private ArrayList<TCP> sources;

    @SerializedName("locations")
    private ArrayList<TCP> locations;

    public ArrayList<TCP> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<TCP> topics) {
        this.topics = topics;
    }

    public ArrayList<TCP> getSources() {
        return sources;
    }

    public void setSources(ArrayList<TCP> topics) {
        this.sources = sources;
    }

    public ArrayList<TCP> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<TCP> locations) {
        this.locations = locations;
    }
}
