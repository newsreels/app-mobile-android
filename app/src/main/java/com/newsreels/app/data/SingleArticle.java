package com.newsreels.app.data;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.model.articles.Article;
import com.newsreels.app.model.Reel.ReelsItem;

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
