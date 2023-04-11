
package com.ziro.bullet.model.Menu;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("id")
    private String id;
    @SerializedName("favorite")
    private boolean favorite;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("image")
    private String image;
    @SerializedName("name")
    private String mName;
    @SerializedName("follower")
    private String follower;
    @SerializedName("color")
    private String color;
    @SerializedName("context")
    private String context;

    public Category() {
    }

    public Category(String mName) {
        this.mName = mName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getmIcon() {
        return mIcon;
    }

    public void setmIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
