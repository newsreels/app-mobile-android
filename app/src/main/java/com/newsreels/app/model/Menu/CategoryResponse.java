
package com.newsreels.app.model.Menu;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.BaseModel;

public class CategoryResponse extends BaseModel{

    @SerializedName(value = "topics", alternate = "category")
    private ArrayList<Category> mCategory;

    public ArrayList<Category> getCategory() {
        return mCategory;
    }

    public void setCategory(ArrayList<Category> category) {
        mCategory = category;
    }

}
