package com.ziro.bullet.model.Tabs;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("for_you")
    private List<NewsItem> forYou;

    @SerializedName("top_news")
    private List<NewsItem> topNews;

    public void setForYou(List<NewsItem> forYou) {
        this.forYou = forYou;
    }

    public List<NewsItem> getForYou() {
        return forYou;
    }

    public void setTopNews(List<NewsItem> topNews) {
        this.topNews = topNews;
    }

    public List<NewsItem> getTopNews() {
        return topNews;
    }
}