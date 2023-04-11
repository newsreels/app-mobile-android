package com.ziro.bullet.model.TCP;

import com.google.gson.annotations.SerializedName;

public class TCP {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("follower")
    private String follower;

    @SerializedName("icon")
    private String icon;

    @SerializedName("image")
    private String image;

    @SerializedName("color")
    private String color;

    @SerializedName("favorite")
    private boolean favorite;

    @SerializedName("context")
    private String context;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TCP(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
