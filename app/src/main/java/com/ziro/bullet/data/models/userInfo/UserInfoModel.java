package com.ziro.bullet.data.models.userInfo;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.search.Registration;

public class UserInfoModel {
    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
