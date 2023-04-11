package com.ziro.bullet.model.language;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LanguagesItem implements Serializable {

    @SerializedName("image")
    private String image;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("sample")
    private String label;

    @SerializedName("id")
    private String id;

    @SerializedName("selected")
    private boolean selected;

    @SerializedName("tag")
    private String tag;

    public LanguagesItem(String image, String name, String label, String id, boolean selected, String tag) {
        this.image = image;
        this.name = name;
        this.label = label;
        this.id = id;
        this.selected = selected;
        this.tag = tag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}