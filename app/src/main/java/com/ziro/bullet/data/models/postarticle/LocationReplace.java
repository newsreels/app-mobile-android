package com.ziro.bullet.data.models.postarticle;

import com.ziro.bullet.data.models.location.Location;

public class LocationReplace {

    private String tempId;
    private Location location;

    public LocationReplace(String tempId, Location location) {
        this.tempId = tempId;
        this.location = location;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
