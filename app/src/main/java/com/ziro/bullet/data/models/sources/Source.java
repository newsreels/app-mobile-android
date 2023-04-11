package com.ziro.bullet.data.models.sources;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Source implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("follower")
    private String follower;
    @SerializedName("name")
    private String name;
    @SerializedName("link")
    private String link;
    @SerializedName("color")
    private String color;
    @SerializedName("text_color")
    private String text_color;
    @SerializedName("image")
    private String image;
    @SerializedName("portrait_image")
    private String portrait_image;
    @SerializedName("icon")
    private String icon;
    @SerializedName("language")
    private String language;
    @SerializedName("category")
    private String category;
    @SerializedName("location")
    private Location location;
    @SerializedName("favorite")
    private boolean favorite;
    @SerializedName("verified")
    private boolean verified;
    @SerializedName("context")
    private String context;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("description")
    private String description;
    @SerializedName("update_count")
    private int update_count;
    @SerializedName("follower_count")
    private int follower_count;
    @SerializedName("post_count")
    private int post_count;
    @SerializedName("own")
    private boolean own;
    @SerializedName("has_reel")
    private boolean has_reel;
    @SerializedName("has_article")
    private boolean has_article;
    @SerializedName("name_image")
    private String name_image;
    @SerializedName("background_image")
    private String background_image;
    @SerializedName("username")
    private String username;

    public Source() {
    }

    public Source(String id) {
        this.id = id;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getText_color() {
        return text_color;
    }

    public void setText_color(String text_color) {
        this.text_color = text_color;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUpdate_count() {
        return update_count;
    }

    public void setUpdate_count(int update_count) {
        this.update_count = update_count;
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

    public boolean isOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    public boolean isHas_reel() {
        return has_reel;
    }

    public void setHas_reel(boolean has_reel) {
        this.has_reel = has_reel;
    }

    public boolean isHas_article() {
        return has_article;
    }

    public void setHas_article(boolean has_article) {
        this.has_article = has_article;
    }

    public String getName_image() {
        return name_image;
    }

    public void setName_image(String name_image) {
        this.name_image = name_image;
    }

    public String getBackground_image() {
        return background_image;
    }

    public void setBackground_image(String background_image) {
        this.background_image = background_image;
    }

    public String getPortrait_image() {
        return portrait_image;
    }

    public void setPortrait_image(String portrait_image) {
        this.portrait_image = portrait_image;
    }

    public String getImagePortraitOrNormal() {
        if (TextUtils.isEmpty(getPortrait_image())) {
            return getImage();
        }
        return getPortrait_image();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.follower);
        dest.writeString(this.name);
        dest.writeString(this.link);
        dest.writeString(this.color);
        dest.writeString(this.text_color);
        dest.writeString(this.image);
        dest.writeString(this.portrait_image);
        dest.writeString(this.icon);
        dest.writeString(this.language);
        dest.writeString(this.category);
        dest.writeSerializable(this.location);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.verified ? (byte) 1 : (byte) 0);
        dest.writeString(this.context);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeString(this.description);
        dest.writeInt(this.update_count);
        dest.writeInt(this.follower_count);
        dest.writeInt(this.post_count);
        dest.writeByte(this.own ? (byte) 1 : (byte) 0);
        dest.writeByte(this.has_reel ? (byte) 1 : (byte) 0);
        dest.writeByte(this.has_article ? (byte) 1 : (byte) 0);
        dest.writeString(this.name_image);
        dest.writeString(this.background_image);
        dest.writeString(this.username);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.follower = source.readString();
        this.name = source.readString();
        this.link = source.readString();
        this.color = source.readString();
        this.text_color = source.readString();
        this.image = source.readString();
        this.portrait_image = source.readString();
        this.icon = source.readString();
        this.language = source.readString();
        this.category = source.readString();
        this.location = (Location) source.readSerializable();
        this.favorite = source.readByte() != 0;
        this.verified = source.readByte() != 0;
        this.context = source.readString();
        this.created_at = source.readString();
        this.updated_at = source.readString();
        this.description = source.readString();
        this.update_count = source.readInt();
        this.follower_count = source.readInt();
        this.post_count = source.readInt();
        this.own = source.readByte() != 0;
        this.has_reel = source.readByte() != 0;
        this.has_article = source.readByte() != 0;
        this.name_image = source.readString();
        this.background_image = source.readString();
        this.username = source.readString();
    }

    protected Source(Parcel in) {
        this.id = in.readString();
        this.follower = in.readString();
        this.name = in.readString();
        this.link = in.readString();
        this.color = in.readString();
        this.text_color = in.readString();
        this.image = in.readString();
        this.portrait_image = in.readString();
        this.icon = in.readString();
        this.language = in.readString();
        this.category = in.readString();
        this.location = (Location) in.readSerializable();
        this.favorite = in.readByte() != 0;
        this.verified = in.readByte() != 0;
        this.context = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.description = in.readString();
        this.update_count = in.readInt();
        this.follower_count = in.readInt();
        this.post_count = in.readInt();
        this.own = in.readByte() != 0;
        this.has_reel = in.readByte() != 0;
        this.has_article = in.readByte() != 0;
        this.name_image = in.readString();
        this.background_image = in.readString();
        this.username = in.readString();
    }

    public static final Creator<Source> CREATOR = new Creator<Source>() {
        @Override
        public Source createFromParcel(Parcel source) {
            return new Source(source);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };
}
