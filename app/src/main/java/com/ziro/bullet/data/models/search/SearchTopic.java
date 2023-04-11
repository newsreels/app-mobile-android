package com.ziro.bullet.data.models.search;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.topics.Topics;

import java.util.ArrayList;

public class SearchTopic {
    @SerializedName("followed")
    private ArrayList<Topics> followedTopics;

    @SerializedName("new")
    private ArrayList<Topics> newTopics;

    @SerializedName("limit")
    private int limit;

    public ArrayList<Topics> getFollowedTopics() {
        return followedTopics;
    }

    public void setFollowedTopics(ArrayList<Topics> followedTopics) {
        this.followedTopics = followedTopics;
    }

    public ArrayList<Topics> getNewTopics() {
        return newTopics;
    }

    public void setNewTopics(ArrayList<Topics> newTopics) {
        this.newTopics = newTopics;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
