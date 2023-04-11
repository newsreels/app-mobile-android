package com.ziro.bullet.model.articles;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Info implements Parcelable {

    @SerializedName("view_count")
    private String viewCount;
    @SerializedName("comment_count")
    private int comment_count;
    @SerializedName("like_count")
    private int like_count;
    @SerializedName("is_liked")
    private boolean isLiked;
    @SerializedName("source_followed")
    private boolean source_followed;

    public boolean isSource_followed() {
        return source_followed;
    }

    public void setSource_followed(boolean source_followed) {
        this.source_followed = source_followed;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public String getViewCount() {
        if(viewCount.equals("")){
            return String.valueOf(0);
        }
        return viewCount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.viewCount);
        dest.writeInt(this.comment_count);
        dest.writeInt(this.like_count);
        dest.writeByte(this.isLiked ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.viewCount = source.readString();
        this.comment_count = source.readInt();
        this.like_count = source.readInt();
        this.isLiked = source.readByte() != 0;
    }

    public Info() {
    }

    protected Info(Parcel in) {
        this.viewCount = in.readString();
        this.comment_count = in.readInt();
        this.like_count = in.readInt();
        this.isLiked = in.readByte() != 0;
    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @Override
        public Info createFromParcel(Parcel source) {
            return new Info(source);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };
}
