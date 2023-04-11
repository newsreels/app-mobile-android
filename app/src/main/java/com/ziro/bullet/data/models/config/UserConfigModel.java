package com.ziro.bullet.data.models.config;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.dataclass.Region;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.model.Edition.EditionsItem;
import com.ziro.bullet.data.models.userInfo.User;

import java.util.ArrayList;

public class UserConfigModel {
    @SerializedName("editions")
    private ArrayList<EditionsItem> editions;

    @SerializedName("static")
    private Static mStatic;

    @SerializedName("home_preference")
    private HomePreference home_preference;


    @SerializedName("user")
    private User user;

    @SerializedName("terms")
    private Terms terms;

    @SerializedName("alert")
    private Alert alert;

    @SerializedName("ads")
    private Ads ads;

    @SerializedName("rating")
    private Rating rating;

    @SerializedName("channels")
    private ArrayList<Source> channels;

    @SerializedName("wallet")
    private String wallet;

    @SerializedName("locations")
    private ArrayList<Location> regionsList;

    @SerializedName("onboarded")
    private boolean onboarded;

    public ArrayList<Source> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Source> channels) {
        this.channels = channels;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }

    public Static getmStatic() {
        return mStatic;
    }

    public void setStatic(Static mStatic) {
        this.mStatic = mStatic;
    }

    public HomePreference getHomePreference() {
        return home_preference;
    }

    public void setHomePreference(HomePreference home_preference) {
        this.home_preference = home_preference;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<EditionsItem> getEditions() {
        return editions;
    }

    public void setEditions(ArrayList<EditionsItem> editions) {
        this.editions = editions;
    }

    public HomePreference getHome_preference() {
        return home_preference;
    }

    public void setHome_preference(HomePreference home_preference) {
        this.home_preference = home_preference;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Ads getAds() {
        return ads;
    }

    public void setAds(Ads ads) {
        this.ads = ads;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public boolean isOnboarded() {
        return onboarded;
    }

    public ArrayList<Location> getRegionsList() {
        return regionsList;
    }
}
