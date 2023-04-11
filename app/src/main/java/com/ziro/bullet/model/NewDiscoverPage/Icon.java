package com.ziro.bullet.model.NewDiscoverPage;

import com.google.gson.annotations.SerializedName;

public class Icon {

    @SerializedName("icon")
    private String icon;

    @SerializedName("name")
    private String name;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
