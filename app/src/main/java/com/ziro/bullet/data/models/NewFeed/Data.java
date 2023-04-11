package com.ziro.bullet.data.models.NewFeed;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.SingleTopic;
import com.ziro.bullet.model.Reel.ReelsItem;

public class Data {

    @SerializedName("channels")
    private ArrayList<Source> channels;

    @SerializedName("header")
    private String header;

    @SerializedName("topics")
    private ArrayList<Topics> topics;

    @SerializedName("reels")
    private ArrayList<ReelsItem> reels;

    @SerializedName("footer")
    private Footer footer;

    @SerializedName("image")
    private String image;

    @SerializedName("followed")
    private boolean followed;

    @SerializedName("articles")
    private ArrayList<Article> articles;

    @SerializedName("article")
    private Article article;

    @SerializedName("topic")
    private SingleTopic topic;

    @SerializedName("context")
    private String context;

    @SerializedName("subheader")
    private String subheader;

    public SingleTopic getTopic() {
        return topic;
    }

    public void setTopic(SingleTopic topic) {
        this.topic = topic;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public ArrayList<Source> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Source> channels) {
        this.channels = channels;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ArrayList<Topics> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Topics> topics) {
        this.topics = topics;
    }

    public ArrayList<ReelsItem> getReels() {
        return reels;
    }

    public void setReels(ArrayList<ReelsItem> reels) {
        this.reels = reels;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Footer getFooter() {
        return footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }
}