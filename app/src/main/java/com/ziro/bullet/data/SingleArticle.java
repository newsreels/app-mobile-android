package com.ziro.bullet.data;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.Reel.ReelsItem;

public class SingleArticle {
    @SerializedName("article")
    private Article article;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    @SerializedName("reel")
    private ReelsItem reels;

    public ReelsItem getReels() {
        return reels;
    }

    public void setReels(ReelsItem reels) {
        this.reels = reels;
    }
}
