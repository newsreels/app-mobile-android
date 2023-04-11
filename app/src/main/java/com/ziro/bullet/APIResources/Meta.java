package com.ziro.bullet.APIResources;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Meta {
    @Expose
    @SerializedName("next")
    private String mNext;

    public String getNext() {
        return mNext;
    }

    public void setNext(String mNext) {
        this.mNext = mNext;
    }
}
