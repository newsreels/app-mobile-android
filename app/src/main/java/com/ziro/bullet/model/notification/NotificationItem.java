package com.ziro.bullet.model.notification;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationItem {
    private String header;

    @SerializedName("id")
    private String id;

    @SerializedName("detail_id")
    private String detail_id;

    @SerializedName("detail_image")
    private String detail_image;

    @SerializedName("details")
    private String details;

    @SerializedName("image")
    private ArrayList<String> images;

    @SerializedName("type")
    private String type;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("source")
    private String source;

    @SerializedName("headline")
    private String headline;

    @SerializedName("context")
    private String context;

    @SerializedName("read")
    private boolean read;

    public NotificationItem(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public String getId() {
        return id;
    }

    public String getDetailId() {
        return detail_id;
    }

    public String getDetailImage() {
        return detail_image;
    }

    public String getDetails() {
        return details;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getType() {
        return type;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getSource() {
        return source;
    }

    public String getHeadline() {
        return headline;
    }

    public boolean isRead() {
        return read;
    }

    public String getContext() {
        return context;
    }
}
