package com.ziro.bullet.model.articles;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.data.models.NewFeed.Footer;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.model.Reel.ReelsItem;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Article implements Parcelable {

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    private String header, tabTitle, tabId, tabIcon,context;
    private Footer footer;
    @SerializedName("bullets")
    private List<Bullet> mBullets;
    @SerializedName("created_at")
    private String mCreatedAt;
    @SerializedName("id")
    private String mId;
    @SerializedName("image")
    private String mImage;
    @SerializedName("link")
    private String mLink;
    @SerializedName("publish_time")
    private String mPublishTime;
    @SerializedName("source")
    private Source mSource;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("updated_at")
    private String mUpdatedAt;
    @SerializedName("color")
    private String color;
    @SerializedName("type")
    private String type;
    @SerializedName("original_link")
    private String originalLink;
    @SerializedName("mute")
    private boolean mute;
    @SerializedName("topics")
    private ArrayList<Topics> topicsList;
    @SerializedName("info")
    private Info info;
    @SerializedName("media_meta")
    private MediaMeta mediaMeta;
    @SerializedName("language")
    private String languageCode;
    @SerializedName("articles")
    private ArrayList<Article> articles;
    private String fragTag;
    private int lastPosition = 0;
    private boolean selected;
    private boolean youtubePlaying;
    private boolean isForYouLastItem;
    private boolean followed;
    private String forYouTitle;
    private VideoInfo videoInfo;
    @SerializedName("authors")
    private ArrayList<Author> author;
    @SerializedName("reels")
    private ArrayList<ReelsItem> reels;
    @SerializedName("channels")
    private ArrayList<Source> channels;
    private String followed_text;
    private String unfollowed_text;
    private boolean topicAdded;
    private int posState;
    private float offsetState;
    private String footerType;
    @SerializedName("subheader")
    private String subHeader;

    public Article() {
    }

    public Article(String mTitle) {
        this.mTitle = mTitle;
    }

    public Article(String id, String type) {
        this.type = type;
        selected = false;
    }

    protected Article(Parcel in) {
        header = in.readString();
        context = in.readString();
        tabTitle = in.readString();
        tabId = in.readString();
        tabIcon = in.readString();
        footer = in.readParcelable(Footer.class.getClassLoader());
        mBullets = in.createTypedArrayList(Bullet.CREATOR);
        mCreatedAt = in.readString();
        mId = in.readString();
        mImage = in.readString();
        mLink = in.readString();
        mPublishTime = in.readString();
        mSource = in.readParcelable(Source.class.getClassLoader());
        mStatus = in.readString();
        mTitle = in.readString();
        mUpdatedAt = in.readString();
        color = in.readString();
        type = in.readString();
        originalLink = in.readString();
        mute = in.readByte() != 0;
        topicsList = in.createTypedArrayList(Topics.CREATOR);
        info = in.readParcelable(Info.class.getClassLoader());
        mediaMeta = in.readParcelable(MediaMeta.class.getClassLoader());
        languageCode = in.readString();
        articles = in.createTypedArrayList(Article.CREATOR);
        fragTag = in.readString();
        lastPosition = in.readInt();
        selected = in.readByte() != 0;
        youtubePlaying = in.readByte() != 0;
        isForYouLastItem = in.readByte() != 0;
        followed = in.readByte() != 0;
        forYouTitle = in.readString();
        videoInfo = in.readParcelable(VideoInfo.class.getClassLoader());
        author = in.createTypedArrayList(Author.CREATOR);
        reels = in.createTypedArrayList(ReelsItem.CREATOR);
        channels = in.createTypedArrayList(Source.CREATOR);
        followed_text = in.readString();
        unfollowed_text = in.readString();
        topicAdded = in.readByte() != 0;
        posState = in.readInt();
        offsetState = in.readFloat();
        footerType = in.readString();
        subHeader = in.readString();
    }

    public boolean isTopicAdded() {
        return topicAdded;
    }

    public void setTopicAdded(boolean topicAdded) {
        this.topicAdded = topicAdded;
    }

    public String getFollowed_text() {
        return followed_text;
    }

    public void setFollowed_text(String followed_text) {
        this.followed_text = followed_text;
    }

    public String getUnfollowed_text() {
        return unfollowed_text;
    }

    public void setUnfollowed_text(String unfollowed_text) {
        this.unfollowed_text = unfollowed_text;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    public ArrayList<Source> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Source> channels) {
        this.channels = channels;
    }

    public ArrayList<ReelsItem> getReels() {
        return reels;
    }

    public void setReels(ArrayList<ReelsItem> reels) {
        this.reels = reels;
    }

    public ArrayList<Author> getAuthor() {
        return author;
    }

    public void setAuthor(ArrayList<Author> author) {
        this.author = author;
    }

    public MediaMeta getMediaMeta() {
        return mediaMeta;
    }

    public void setMediaMeta(MediaMeta mediaMeta) {
        this.mediaMeta = mediaMeta;
    }

    public boolean isYoutubePlaying() {
        return youtubePlaying;
    }

    public void setYoutubePlaying(boolean youtubePlaying) {
        this.youtubePlaying = youtubePlaying;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public String getFragTag() {
        return fragTag;
    }

    public void setFragTag(String fragTag) {
        this.fragTag = fragTag;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ArrayList<com.ziro.bullet.data.models.topics.Topics> getTopics() {
        return topicsList;
    }

    public void setTopicsList(ArrayList<Topics> topicsList) {
        this.topicsList = topicsList;
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

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getPublishTime() {
        return mPublishTime;
    }

    public void setPublishTime(String publishTime) {
        mPublishTime = publishTime;
    }

    public Source getSource() {
        return mSource;
    }

    public void setSource(Source source) {
        mSource = source;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }


    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getTabIcon() {
        return tabIcon;
    }

    public void setTabIcon(String tabIcon) {
        this.tabIcon = tabIcon;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public boolean isForYouLastItem() {
        return isForYouLastItem;
    }

    public void setForYouLastItem(boolean forYouLastItem) {
        isForYouLastItem = forYouLastItem;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public String getForYouTitle() {
        return forYouTitle;
    }

    public void setForYouTitle(String forYouTitle) {
        this.forYouTitle = forYouTitle;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    public int getPosState() {
        return posState;
    }

    public void setPosState(int posState) {
        this.posState = posState;
    }

    public float getOffsetState() {
        return offsetState;
    }

    public void setOffsetState(float offsetState) {
        this.offsetState = offsetState;
    }

    public String getFooterType() {
        return footerType;
    }

    public void setFooterType(String footerType) {
        this.footerType = footerType;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    @Override
    public String toString() {
        return "Article{" +
                "mBullets=" + mBullets +
                ", mCreatedAt='" + mCreatedAt + '\'' +
                ", mId='" + mId + '\'' +
                ", mImage='" + mImage + '\'' +
                ", mLink='" + mLink + '\'' +
                ", mPublishTime='" + mPublishTime + '\'' +
                ", mSource=" + mSource +
                ", mStatus='" + mStatus + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mUpdatedAt='" + mUpdatedAt + '\'' +
                ", color='" + color + '\'' +
                ", type='" + type + '\'' +
                ", mute=" + mute +
                ", topicsList=" + topicsList +
                ", info=" + info +
                ", fragTag='" + fragTag + '\'' +
                ", lastPosition=" + lastPosition +
                ", selected=" + selected +
                ", youtubePlaying=" + youtubePlaying +
                ", original link=" + originalLink +
                ", videoInfo=" + videoInfo +
                '}';
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

    public String getOriginalLink() {
        return originalLink;
    }

    public void setOriginalLink(String originalLink) {
        this.originalLink = originalLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header);
        dest.writeString(context);
        dest.writeString(tabTitle);
        dest.writeString(tabId);
        dest.writeString(tabIcon);
        dest.writeParcelable(footer, flags);
        dest.writeTypedList(mBullets);
        dest.writeString(mCreatedAt);
        dest.writeString(mId);
        dest.writeString(mImage);
        dest.writeString(mLink);
        dest.writeString(mPublishTime);
        dest.writeParcelable(mSource, flags);
        dest.writeString(mStatus);
        dest.writeString(mTitle);
        dest.writeString(mUpdatedAt);
        dest.writeString(color);
        dest.writeString(type);
        dest.writeString(originalLink);
        dest.writeByte((byte) (mute ? 1 : 0));
        dest.writeTypedList(topicsList);
        dest.writeParcelable(info, flags);
        dest.writeParcelable(mediaMeta, flags);
        dest.writeString(languageCode);
        dest.writeTypedList(articles);
        dest.writeString(fragTag);
        dest.writeInt(lastPosition);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (youtubePlaying ? 1 : 0));
        dest.writeByte((byte) (isForYouLastItem ? 1 : 0));
        dest.writeByte((byte) (followed ? 1 : 0));
        dest.writeString(forYouTitle);
        dest.writeParcelable(videoInfo, flags);
        dest.writeTypedList(author);
        dest.writeTypedList(reels);
        dest.writeTypedList(channels);
        dest.writeString(followed_text);
        dest.writeString(unfollowed_text);
        dest.writeByte((byte) (topicAdded ? 1 : 0));
        dest.writeInt(posState);
        dest.writeFloat(offsetState);
        dest.writeString(footerType);
        dest.writeString(subHeader);
    }
}
