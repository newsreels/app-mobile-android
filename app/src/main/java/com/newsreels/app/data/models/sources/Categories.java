package com.newsreels.app.data.models.sources;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.BaseModel;

import java.util.ArrayList;

public class Categories extends BaseModel {

    @SerializedName("categories")
    private ArrayList<CategoryModel> CategoryModel;

    public ArrayList<com.newsreels.app.data.models.sources.CategoryModel> getCategoryModel() {
        return CategoryModel;
    }

    public void setCategoryModel(ArrayList<com.newsreels.app.data.models.sources.CategoryModel> categoryModel) {
        CategoryModel = categoryModel;
    }
}
