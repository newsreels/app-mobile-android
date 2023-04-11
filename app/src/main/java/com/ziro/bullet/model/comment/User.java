package com.ziro.bullet.model.comment;

import com.google.gson.annotations.SerializedName;

public class User{

    public User(String image, String name) {
        this.image = image;
        this.name = name;
    }

    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return image;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}