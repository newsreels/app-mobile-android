package com.ziro.bullet.model.articles;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

import java.util.ArrayList;

public class ForYou extends BaseModel {


    @SerializedName("data")
    private ArrayList<ArticleResponse> mArticleResponses;

    public ArrayList<ArticleResponse> getArticleResponse() {
        return mArticleResponses;
    }

    public void setmArticleResponses(ArrayList<ArticleResponse> mArticleResponses) {
        this.mArticleResponses = mArticleResponses;
    }
}
