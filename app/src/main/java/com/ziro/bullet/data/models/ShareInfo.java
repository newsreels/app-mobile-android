package com.ziro.bullet.data.models;

import com.google.gson.annotations.SerializedName;

public class ShareInfo {

    @SerializedName("share_message")
    private String share_message;

    @SerializedName("source_blocked")
    private boolean source_blocked;

    @SerializedName("author_blocked")
    private boolean author_blocked;

    @SerializedName("source_followed")
    private boolean source_followed;

    @SerializedName("article_archived")
    private boolean article_archived;

    @SerializedName("media")
    private String media;

    @SerializedName("link")
    private String link;

    @SerializedName("download_link")
    private String download_link;

    public String getShare_message() {
        return share_message;
    }

    public boolean isSource_blocked() {
        return source_blocked;
    }

    public void setSource_blocked(boolean source_blocked) {
        this.source_blocked = source_blocked;
    }

    public boolean isSource_followed() {
        return source_followed;
    }

    public boolean isArticle_archived() {
        return article_archived;
    }

    public boolean isAuthor_blocked() {
        return author_blocked;
    }


    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getLink() {
        return link;
    }

    public String getDownloadLink() {
        return download_link;
    }
}
