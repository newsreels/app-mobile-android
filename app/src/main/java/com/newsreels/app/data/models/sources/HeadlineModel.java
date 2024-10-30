package com.newsreels.app.data.models.sources;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.BaseModel;

import java.util.ArrayList;

public class HeadlineModel extends BaseModel {

    @SerializedName("headlines")
    private ArrayList<Source> headlines;

    public ArrayList<Source> getSources() {
        return headlines;
    }

    public void setSources(ArrayList<Source> headlines) {
        this.headlines = headlines;
    }
}
