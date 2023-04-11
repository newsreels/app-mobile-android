
package com.ziro.bullet.model.News;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.model.articles.Article;

public class Category {

    @SerializedName("contents")
    private ArrayList<Article> mContents;
    @SerializedName("name")
    private String mName;

    public ArrayList<Article> getContents() {
        return mContents;
    }

    public void setContents(ArrayList<Article> contents) {
        mContents = contents;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @SerializedName("next_page")
    private String next_page;
    @SerializedName("current_page")
    private int current_page;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public String getNext_page() {
        return next_page;
    }

    public void setNext_page(String next_page) {
        this.next_page = next_page;
    }
}
