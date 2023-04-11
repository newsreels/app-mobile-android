
package com.ziro.bullet.data.models.push;


import com.google.gson.annotations.SerializedName;

public class PushResponse {

    @SerializedName("push")
    private Push mPush;

    public Push getPush() {
        return mPush;
    }

    public void setPush(Push push) {
        mPush = push;
    }

}
