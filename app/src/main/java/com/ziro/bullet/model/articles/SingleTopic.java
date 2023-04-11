package com.ziro.bullet.model.articles;

import com.google.gson.annotations.SerializedName;

public class SingleTopic {

    @SerializedName("id")
    private String id;

    @SerializedName("followed")
    private boolean followed;

    @SerializedName("unfollowed_text")
    private String unfollowed_text;

    @SerializedName("followed_text")
    private String followed_text;

    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getUnfollowed_text() {
        return unfollowed_text;
    }

    public void setUnfollowed_text(String unfollowed_text) {
        this.unfollowed_text = unfollowed_text;
    }

    public String getFollowed_text() {
        return followed_text;
    }

    public void setFollowed_text(String followed_text) {
        this.followed_text = followed_text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
