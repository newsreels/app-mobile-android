package com.ziro.bullet.data.models.sources;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.ArrayList;

public class SourceModel extends BaseModel {

    @SerializedName("sources")
    private ArrayList<Source> sources;

    public ArrayList<Source> getSources() {
        return sources;
    }

    public void setSources(ArrayList<Source> sources) {
        this.sources = sources;
    }
}
