package com.ziro.bullet.data.models.topics;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class Topics implements Parcelable {

    public static final Creator<Topics> CREATOR = new Creator<Topics>() {
        @Override
        public Topics createFromParcel(Parcel source) {
            return new Topics(source);
        }

        @Override
        public Topics[] newArray(int size) {
            return new Topics[size];
        }
    };
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("follower")
    private String follower;
    private String type;
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

    public Topics() {
        this.name = name;
    }

    public Topics(String name) {
        this.name = name;
    }

    public Topics(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public Topics(String id, String name, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    protected Topics(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.follower = in.readString();
        this.type = in.readString();
        this.icon = in.readString();
        this.image = in.readString();
        this.color = in.readString();
        this.favorite = in.readByte() != 0;
        this.context = in.readString();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.follower);
        dest.writeString(this.type);
        dest.writeString(this.icon);
        dest.writeString(this.image);
        dest.writeString(this.color);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
        dest.writeString(this.context);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.follower = source.readString();
        this.type = source.readString();
        this.icon = source.readString();
        this.image = source.readString();
        this.color = source.readString();
        this.favorite = source.readByte() != 0;
        this.context = source.readString();
    }
}
