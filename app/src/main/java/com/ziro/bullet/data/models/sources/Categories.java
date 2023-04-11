package com.ziro.bullet.data.models.sources;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.ArrayList;

public class Categories extends BaseModel {

    @SerializedName("categories")
    private ArrayList<CategoryModel> CategoryModel;

    public ArrayList<com.ziro.bullet.data.models.sources.CategoryModel> getCategoryModel() {
        return CategoryModel;
    }

    public void setCategoryModel(ArrayList<com.ziro.bullet.data.models.sources.CategoryModel> categoryModel) {
        CategoryModel = categoryModel;
    }
}
