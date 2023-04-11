package com.ziro.bullet.data.models.relevant;

import com.google.gson.annotations.SerializedName;

import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.Reel.ReelsItem;

import java.util.List;

public class RelevantResponse {

    @SerializedName("article_context")
    private String articleContext;

    @SerializedName("article_page")
    private ArticlePage articlePage;

    @SerializedName("exact_match")
    private String exactMatch;

    @SerializedName("articles")
    private List<Article> articles = null;

    @SerializedName("topics")
    private List<Topics> topics = null;

    @SerializedName("sources")
    private List<Source> sources = null;

    @SerializedName("locations")
    private List<Location> locations = null;

    @SerializedName("authors")
    private List<Author> authors = null;

    @SerializedName("reels")
    private List<ReelsItem> reels = null;

    public String getExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(String exactMatch) {
        this.exactMatch = exactMatch;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public List<Topics> getTopics() {
        return topics;
    }

    public void setTopics(List<Topics> topics) {
        this.topics = topics;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getArticleContext() {
        return articleContext;
    }

    public void setArticleContext(String articleContext) {
        this.articleContext = articleContext;
    }

    public ArticlePage getArticlePage() {
        return articlePage;
    }

    public void setArticlePage(ArticlePage articlePage) {
        this.articlePage = articlePage;
    }

    public List<ReelsItem> getReels() {
        return reels;
    }

    public void setReels(List<ReelsItem> reels) {
        this.reels = reels;
    }

    public class ArticlePage {
        @SerializedName("next")
        private String next;

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }
    }
}
