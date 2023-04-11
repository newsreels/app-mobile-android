package com.ziro.bullet.data.models.search;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.topics.Topics;

import java.util.ArrayList;

public class SearchSource {
    @SerializedName("followed")
    private ArrayList<Source> followedSources;

    @SerializedName("new")
    private ArrayList<Source> newSources;

    @SerializedName("limit")
    private int limit;

    public ArrayList<Source> getFollowedSources() {
        return followedSources;
    }

    public void setFollowedSources(ArrayList<Source> followedSources) {
        this.followedSources = followedSources;
    }

    public ArrayList<Source> getNewSources() {
        return newSources;
    }

    public void setNewSources(ArrayList<Source> newSources) {
        this.newSources = newSources;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
