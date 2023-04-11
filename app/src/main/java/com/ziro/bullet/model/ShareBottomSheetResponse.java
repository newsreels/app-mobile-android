
package com.ziro.bullet.model;

import com.google.gson.annotations.SerializedName;

public class ShareBottomSheetResponse {

    public ShareBottomSheetResponse() {
    }

    @SerializedName("message")
    private String message;

    @SerializedName("errors")
    private Errors errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public class Errors {
        @SerializedName("message")
        private String message;
    }
}
