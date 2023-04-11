package com.ziro.bullet.data.models.postarticle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentTags {

    @SerializedName("tags")
    private List<TagItem> topics = null;

    public List<TagItem> getTopics() {
        return topics;
    }

    public void setTopics(List<TagItem> topics) {
        this.topics = topics;
    }

}
