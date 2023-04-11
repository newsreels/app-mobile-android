package com.ziro.bullet.data.models.postarticle;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.location.Location;

import java.util.List;

public class CurrentLocations {

    @SerializedName("tags")
    private List<Location> locations = null;

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
