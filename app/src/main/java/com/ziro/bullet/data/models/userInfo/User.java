package com.ziro.bullet.data.models.userInfo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.search.Registration;

public class User {

    @SerializedName("id")
    private String id;

    @SerializedName("first_name")
    private String first_name;
    @SerializedName("name")
    private String name;

    @SerializedName("last_name")
    private String last_name;

    @SerializedName("profile_image")
    private String profile_image;

    @SerializedName("cover_image")
    private String cover_image;

    @SerializedName("setup")
    private boolean setup;

    @SerializedName("verified")
    private boolean verified;

    @SerializedName("email")
    private String email;

    @SerializedName("hasPassword")
    private boolean hasPassword;

    @SerializedName("guestValid")
    private boolean guestValid;

    @SerializedName("username")
    private String username;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public boolean getHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGuestValid() {
        return guestValid;
    }

    public void setGuestValid(boolean guestValid) {
        this.guestValid = guestValid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameToDisplay() {
        if (TextUtils.isEmpty(name)) {
            return first_name + " " + last_name;
        }
        return name;
    }
}
