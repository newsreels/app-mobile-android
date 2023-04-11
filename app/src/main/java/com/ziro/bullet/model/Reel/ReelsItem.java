package com.ziro.bullet.model.Reel;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.caption.CaptionDetails;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.articles.MediaMeta;

import java.util.ArrayList;

public class ReelsItem implements Parcelable {

    //if u add any new keys to this model, don't forget to add in override methods too.

    @SerializedName("captions")
    private ArrayList<CaptionDetails> captions;

    @SerializedName("publish_time")
    private String publishTime;

    @SerializedName("info")
    private Info info;

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private String id;

    @SerializedName("media")
    private String media;

    @SerializedName("media_landscape")
    private String mediaLandscape;

    @SerializedName("context")
    private String context;

    @SerializedName("image")
    private String image;

    @SerializedName("source")
    private Source source;

    @SerializedName("status")
    private String status;

    @SerializedName("link")
    private String link;

    @SerializedName("media_meta")
    private MediaMeta mediaMeta;

    @SerializedName("authors")
    private ArrayList<Author> author;

    @SerializedName("verified")
    private boolean verified;

    @SerializedName("native_title")
    private boolean native_title;

    @SerializedName("debug")
    private String debug;

    private boolean selected;

    private String type;

    private boolean isCached;

    private boolean isSeen;


    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean isNative_title() {
        return native_title;
    }

    public void setNative_title(boolean native_title) {
        this.native_title = native_title;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }

    public ArrayList<CaptionDetails> getCaptions() {
        return captions;
    }

    public void setCaptions(ArrayList<CaptionDetails> captions) {
        this.captions = captions;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String type) {
        this.debug = debug;
    }

    public boolean isNativetitle() {
        return native_title;
    }

    public void setNativetitle(boolean native_title) {
        this.native_title = native_title;
    }

    public ArrayList<Author> getAuthor() {
        return author;
    }

    public void setAuthor(ArrayList<Author> author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public MediaMeta getMediaMeta() {
        return mediaMeta;
    }

    public void setMediaMeta(MediaMeta mediaMeta) {
        this.mediaMeta = mediaMeta;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMedia() {
        return media;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMediaLandscape() {
        return mediaLandscape;
    }

    public void setMediaLandscape(String mediaLandscape) {
        this.mediaLandscape = mediaLandscape;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ReelsItem() {
    }

    public String getSourceImageToDisplay() {
        if (getSource() != null) {
            return getSource().getIcon();
        } else {
            if (author != null && author.size() > 0) {
                Author authorObj = author.get(0);
                if (authorObj != null) {
                    return authorObj.getImage();
                }
            }
        }
        return "";
    }

    public String getSourceNameToDisplay() {
        if (getSource() != null) {
            return getSource().getName();
        } else {
            if (author != null && author.size() > 0) {
                Author authorObj = author.get(0);
                if (authorObj != null) {
                    return TextUtils.isEmpty(authorObj.getUsername()) ? authorObj.getName() : authorObj.getUsername();
                }
            }
        }
        return "";
    }

    public String getAuthorNameToDisplay() {
        if (author != null && author.size() > 0) {
            Author authorObj = author.get(0);
            if (authorObj != null) {
                if (getSource() != null) {
                    return TextUtils.isEmpty(authorObj.getUsername()) ? authorObj.getName() : authorObj.getUsername();
                }
            }
        }
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.publishTime);
        dest.writeParcelable(this.info, flags);
        dest.writeString(this.description);
        dest.writeString(this.id);
        dest.writeString(this.media);
        dest.writeString(this.mediaLandscape);
        dest.writeString(this.context);
        dest.writeString(this.image);
        dest.writeParcelable(this.source, flags);
        dest.writeString(this.status);
        dest.writeString(this.link);
        dest.writeParcelable(this.mediaMeta, flags);
        dest.writeTypedList(this.author);
        dest.writeByte(this.verified ? (byte) 1 : (byte) 0);
        dest.writeByte(this.native_title ? (byte) 1 : (byte) 0);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeString(this.type);
    }

    public void readFromParcel(Parcel source) {
        this.publishTime = source.readString();
        this.info = source.readParcelable(Info.class.getClassLoader());
        this.description = source.readString();
        this.id = source.readString();
        this.media = source.readString();
        this.mediaLandscape = source.readString();
        this.context = source.readString();
        this.image = source.readString();
        this.source = source.readParcelable(Source.class.getClassLoader());
        this.status = source.readString();
        this.link = source.readString();
        this.mediaMeta = source.readParcelable(MediaMeta.class.getClassLoader());
        this.author = source.createTypedArrayList(Author.CREATOR);
        this.verified = source.readByte() != 0;
        this.native_title = source.readByte() != 0;
        this.selected = source.readByte() != 0;
        this.type = source.readString();
    }

    protected ReelsItem(Parcel in) {
        this.publishTime = in.readString();
        this.info = in.readParcelable(Info.class.getClassLoader());
        this.description = in.readString();
        this.id = in.readString();
        this.media = in.readString();
        this.mediaLandscape = in.readString();
        this.context = in.readString();
        this.image = in.readString();
        this.source = in.readParcelable(Source.class.getClassLoader());
        this.status = in.readString();
        this.link = in.readString();
        this.mediaMeta = in.readParcelable(MediaMeta.class.getClassLoader());
        this.author = in.createTypedArrayList(Author.CREATOR);
        this.verified = in.readByte() != 0;
        this.native_title = in.readByte() != 0;
        this.selected = in.readByte() != 0;
        this.type = in.readString();
    }

    public static final Creator<ReelsItem> CREATOR = new Creator<ReelsItem>() {
        @Override
        public ReelsItem createFromParcel(Parcel source) {
            return new ReelsItem(source);
        }

        @Override
        public ReelsItem[] newArray(int size) {
            return new ReelsItem[size];
        }
    };
}