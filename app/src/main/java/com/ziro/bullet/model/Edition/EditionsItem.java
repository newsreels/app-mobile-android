package com.ziro.bullet.model.Edition;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EditionsItem {

    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    @SerializedName("language")
    private String language;

    @SerializedName("id")
    private String id;

    @SerializedName("selected")
    private Boolean selected;

    @SerializedName("has_child")
    private Boolean has_child;
    private boolean isExpanded = false;

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public Boolean getHas_child() {
        return has_child;
    }

    public void setHas_child(Boolean has_child) {
        this.has_child = has_child;
    }

    private ArrayList<EditionsItem> subEditionList;

    public ArrayList<EditionsItem> getSubEditionList() {
        return subEditionList;
    }

    public void setSubEditionList(ArrayList<EditionsItem> subEditionList) {
        this.subEditionList = subEditionList;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return "EditionsItem{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", id='" + id + '\'' +
                ", selected=" + selected +
                ", has_child=" + has_child +
                ", isExpanded=" + isExpanded +
                ", subEditionList=" + subEditionList +
                '}';
    }
}