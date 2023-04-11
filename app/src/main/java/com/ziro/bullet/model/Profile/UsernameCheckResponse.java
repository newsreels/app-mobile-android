
package com.ziro.bullet.model.Profile;

import com.google.gson.annotations.SerializedName;

public class UsernameCheckResponse {

    @SerializedName("valid")
    private boolean valid;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
