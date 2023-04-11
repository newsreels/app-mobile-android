package com.ziro.bullet.data.models.sources;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.ArrayList;

public class CategoryModel extends BaseModel {

    @SerializedName("name")
    private String name;

    @SerializedName("sources")
    private ArrayList<Source> sources;

    public ArrayList<Source> getSources() {
        return sources;
    }

    public void setSources(ArrayList<Source> sources) {
        this.sources = sources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
