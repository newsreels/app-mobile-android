package com.ziro.bullet.data.models.sources;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

public class CategoryPaginationModel extends BaseModel {

    @SerializedName("category")
    private CategoryModel categoryModel;

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }
}
