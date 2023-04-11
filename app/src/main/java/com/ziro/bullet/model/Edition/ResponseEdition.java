package com.ziro.bullet.model.Edition;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseEdition {

    @SerializedName("editions")
    private ArrayList<EditionsItem> editions;

    @SerializedName("meta")
    private Meta meta;

    public void setEditions(ArrayList<EditionsItem> editions){
        this.editions = editions;
    }

    public ArrayList<EditionsItem> getEditions(){
        return editions;
    }

    public void setMeta(Meta meta){
        this.meta = meta;
    }

    public Meta getMeta(){
        return meta;
    }
}