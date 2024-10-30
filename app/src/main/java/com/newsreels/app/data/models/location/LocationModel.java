package com.newsreels.app.data.models.location;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.BaseModel;

import java.util.ArrayList;

public class LocationModel extends BaseModel {


    @SerializedName("locations")
    private ArrayList<Location> locations;

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }
}
