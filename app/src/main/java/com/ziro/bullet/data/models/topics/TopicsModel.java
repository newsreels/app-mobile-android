package com.ziro.bullet.data.models.topics;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.ArrayList;

public class TopicsModel extends BaseModel {
    @SerializedName("topics")
    private ArrayList<Topics> topics;

    public ArrayList<Topics> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Topics> topics) {
        this.topics = topics;
    }
}
