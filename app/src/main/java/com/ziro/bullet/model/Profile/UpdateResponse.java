
package com.ziro.bullet.model.Profile;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.userInfo.User;

public class UpdateResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
