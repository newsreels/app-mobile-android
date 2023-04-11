package com.ziro.bullet.model.News;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class NewsResponse {

    @SerializedName("category")
    private Category category;

    @SerializedName("next_page")
    private String nextPage;

    @SerializedName("prev_category")
    private int prevCategory;

    @SerializedName("next_category")
    private int nextCategory;

    @SerializedName("total_categories")
    private int total_categories;

    public int getTotal_categories() {
        return total_categories;
    }

    public void setTotal_categories(int total_categories) {
        this.total_categories = total_categories;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public int getPrevCategory() {
        return prevCategory;
    }

    public void setPrevCategory(int prevCategory) {
        this.prevCategory = prevCategory;
    }

    public int getNextCategory() {
        return nextCategory;
    }

    public void setNextCategory(int nextCategory) {
        this.nextCategory = nextCategory;
    }
}
