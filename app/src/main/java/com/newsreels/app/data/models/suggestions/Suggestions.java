package com.newsreels.app.data.models.suggestions;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.search.Search;
import com.newsreels.app.data.models.topics.Topics;
import com.newsreels.app.data.models.topics.TopicsModel;

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
