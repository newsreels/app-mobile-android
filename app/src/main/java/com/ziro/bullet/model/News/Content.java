
package com.ziro.bullet.model.News;

import java.util.List;
import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Content {

    @SerializedName("bullets")
    private List<Bullet> mBullets;
    @SerializedName("image")
    private String mImage;
    @SerializedName("source_link")
    private String mSourceLink;
    @SerializedName("source_name")
    private String mSourceName;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("tint")
    private String tint;
    @SerializedName("publish_time")
    private String publish_time;
    @SerializedName("color")
    private String color;

    public String getTint() {
        return tint;
    }

    public void setTint(String tint) {
        this.tint = tint;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Bullet> getBullets() {
        return mBullets;
    }

    public void setBullets(List<Bullet> bullets) {
        mBullets = bullets;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getSourceLink() {
        return mSourceLink;
    }

    public void setSourceLink(String sourceLink) {
        mSourceLink = sourceLink;
    }

    public String getSourceName() {
        return mSourceName;
    }

    public void setSourceName(String sourceName) {
        mSourceName = sourceName;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

}
