package com.ziro.bullet.model.language;

import com.google.gson.annotations.SerializedName;

public class Meta{

    @SerializedName("next")
    private String next;

    public void setNext(String next){
        this.next = next;
    }

    public String getNext(){
        return next;
    }
}