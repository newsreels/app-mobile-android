
package com.ziro.bullet.model.News;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Bullet {

    @SerializedName("data")
    private String mData;

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

}
