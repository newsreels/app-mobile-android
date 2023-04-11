package com.ziro.bullet.model.NewDiscoverPage;

import com.google.gson.annotations.SerializedName;

public class DiscoverItem {

    @SerializedName("data")
    private Data data;

    @SerializedName("subtitle")
    private String subtitle;

    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private String type;

    @SerializedName("context")
    private String context;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

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
}