package com.ziro.bullet.model.comment;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.model.articles.Info;

public class CommentCounterResponse {

    @SerializedName("info")
    private Info info;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}
