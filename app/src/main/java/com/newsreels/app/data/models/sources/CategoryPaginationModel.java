package com.newsreels.app.data.models.sources;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.BaseModel;

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
