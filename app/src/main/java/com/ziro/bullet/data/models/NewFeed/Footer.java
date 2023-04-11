package com.ziro.bullet.data.models.NewFeed;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Footer implements Parcelable {

    @SerializedName("prefix")
    private String prefix;

    @SerializedName("title")
    private String title;


    public Footer(String title) {
        this.title = title;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
        dest.writeString(this.prefix);
        dest.writeString(this.title);
    }

    public void readFromParcel(Parcel source) {
        this.prefix = source.readString();
        this.title = source.readString();
    }

    protected Footer(Parcel in) {
        this.prefix = in.readString();
        this.title = in.readString();
    }

    public static final Creator<Footer> CREATOR = new Creator<Footer>() {
        @Override
        public Footer createFromParcel(Parcel source) {
            return new Footer(source);
        }

        @Override
        public Footer[] newArray(int size) {
            return new Footer[size];
        }
    };
}
