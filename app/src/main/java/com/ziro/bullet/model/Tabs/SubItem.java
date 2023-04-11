package com.ziro.bullet.model.Tabs;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SubItem implements Parcelable {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    protected SubItem(Parcel in) {
        id = in.readString();
        title = in.readString();
    }

    public static final Creator<SubItem> CREATOR = new Creator<SubItem>() {
        @Override
        public SubItem createFromParcel(Parcel in) {
            return new SubItem(in);
        }

        @Override
        public SubItem[] newArray(int size) {
            return new SubItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
    }
}
