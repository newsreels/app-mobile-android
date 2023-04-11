package com.ziro.bullet.model.articles;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.model.Reel.ReelsItem;

import java.util.ArrayList;
import java.util.List;


public class ArticleResponse extends BaseModel {

    @SerializedName("articles")
    private ArrayList<Article> mArticles;

    @SerializedName("group_articles")
    private ArrayList<Article> mGroupArticles;

    @SerializedName("title")
    private String categoryName;

    @SerializedName("id")
    private String id;

    @SerializedName("header")
    private String header;

    @SerializedName("footer")
    private String footer;

    @SerializedName("icon")
    private String icon;

    @SerializedName("authors")
    private ArrayList<Author> authors;

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    @SerializedName("reels")
    private List<ReelsItem> reels;

    public void setmArticles(ArrayList<Article> mArticles) {
        this.mArticles = mArticles;
    }

    public void setReels(List<ReelsItem> reels){
        this.reels = reels;
    }

    public List<ReelsItem> getReels(){
        return reels;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ArrayList<Article> getArticles() {
        return mArticles;
    }

    public String getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ArrayList<Article> getmGroupArticles() {
        return mGroupArticles;
    }

    public void setmGroupArticles(ArrayList<Article> mGroupArticles) {
        this.mGroupArticles = mGroupArticles;
    }
}
