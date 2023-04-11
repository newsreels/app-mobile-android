package com.ziro.bullet.model.Tabs;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataItem implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("raw_id")
    private String rawId;

    @SerializedName("title")
    private String title;

    @SerializedName("locked")
    private boolean isLocked;

    @SerializedName("followed")
    private boolean isFollowed;

    @SerializedName("type")
    private String type;

    @SerializedName("image")
    private String image;

    @SerializedName("greeting")
    private String greeting;

    @SerializedName("sub")
    private List<SubItem> subItems;

    protected DataItem(Parcel in) {
        id = in.readString();
        rawId = in.readString();
        title = in.readString();
        isLocked = in.readByte() != 0;
        isFollowed = in.readByte() != 0;
        type = in.readString();
        image = in.readString();
        greeting = in.readString();
        subItems = in.createTypedArrayList(SubItem.CREATOR);
    }

    public static final Creator<DataItem> CREATOR = new Creator<DataItem>() {
        @Override
        public DataItem createFromParcel(Parcel in) {
            return new DataItem(in);
        }

        @Override
        public DataItem[] newArray(int size) {
            return new DataItem[size];
        }
    };

    public String getRawId() {
        return rawId;
    }

    public void setRawId(String rawId) {
        this.rawId = rawId;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public List<SubItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<SubItem> subItems) {
        this.subItems = subItems;
    }

    public DataItem() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(rawId);
        parcel.writeString(title);
        parcel.writeByte((byte) (isLocked ? 1 : 0));
        parcel.writeByte((byte) (isFollowed ? 1 : 0));
        parcel.writeString(type);
        parcel.writeString(image);
        parcel.writeString(greeting);
        parcel.writeTypedList(subItems);
    }
}