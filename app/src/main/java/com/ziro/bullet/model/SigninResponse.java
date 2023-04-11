
package com.ziro.bullet.model;

import com.google.gson.annotations.SerializedName;

public class SigninResponse {

    @SerializedName("exist")
    private Boolean mExist;

    public Boolean getExist() {
        return mExist;
    }

    public void setExist(Boolean exist) {
        mExist = exist;
    }

}
