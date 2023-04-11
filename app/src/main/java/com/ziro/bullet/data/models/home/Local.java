package com.ziro.bullet.data.models.home;

import com.google.gson.annotations.SerializedName;

public class Local {

    public Local(String id) {
        this.id = id;
    }

    @SerializedName("id")
    private String id;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
