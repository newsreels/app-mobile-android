package com.ziro.bullet.model.articles;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Author implements Parcelable {

    @SerializedName("id")
    private String id;
    //FOR ARTICLE OBJECT
    @SerializedName("name")
    private String name;
    @SerializedName("username")
    private String username;
    @SerializedName("image")
    private String image;
    //END ARTICLE OBJECT
    @SerializedName("context")
    private String context;
    //FOR GET API AUTHOR SPECIFIC
    @SerializedName("first_name")
    private String first_name;
    @SerializedName("last_name")
    private String last_name;
    @SerializedName("profile_image")
    private String profile_image;
    @SerializedName("cover_image")
    private String cover_image;
    @SerializedName("language")
    private String language;
    @SerializedName("follower_count")
    private int follower_count;
    @SerializedName("post_count")
    private int post_count;
    @SerializedName("favorite")
    private boolean follow;
    //END AUTHOR SPECIFIC
    @SerializedName("verified")
    private boolean verified;

    public Author() {
    }



    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getFollower_count() {
        return follower_count;
    }

    public void setFollower_count(int follower_count) {
        this.follower_count = follower_count;
    }

    public int getPost_count() {
        return post_count;
    }

    public void setPost_count(int post_count) {
        this.post_count = post_count;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNameToDisplay() {
        if (TextUtils.isEmpty(name)) {
            return first_name + " " + last_name;
        }
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.image);
        dest.writeString(this.context);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.profile_image);
        dest.writeString(this.cover_image);
        dest.writeString(this.language);
        dest.writeInt(this.follower_count);
        dest.writeInt(this.post_count);
        dest.writeByte(this.follow ? (byte) 1 : (byte) 0);
        dest.writeByte(this.verified ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.name = source.readString();
        this.username = source.readString();
        this.image = source.readString();
        this.context = source.readString();
        this.first_name = source.readString();
        this.last_name = source.readString();
        this.profile_image = source.readString();
        this.cover_image = source.readString();
        this.language = source.readString();
        this.follower_count = source.readInt();
        this.post_count = source.readInt();
        this.follow = source.readByte() != 0;
        this.verified = source.readByte() != 0;
    }

    protected Author(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.username = in.readString();
        this.image = in.readString();
        this.context = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.profile_image = in.readString();
        this.cover_image = in.readString();
        this.language = in.readString();
        this.follower_count = in.readInt();
        this.post_count = in.readInt();
        this.follow = in.readByte() != 0;
        this.verified = in.readByte() != 0;
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel source) {
            return new Author(source);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}
