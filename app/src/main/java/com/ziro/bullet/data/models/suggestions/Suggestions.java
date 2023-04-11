package com.ziro.bullet.data.models.suggestions;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.search.Search;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.data.models.topics.TopicsModel;

import java.util.ArrayList;

public class Suggestions {
    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private String type;

    @SerializedName("data")
    private ArrayList<Search> topics;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Search> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Search> topics) {
        this.topics = topics;
    }
}
