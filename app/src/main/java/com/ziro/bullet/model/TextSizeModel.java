package com.ziro.bullet.model;

public class TextSizeModel {
    private float headline_size;
    private float bullet_size;
    private int id;

    public TextSizeModel(float headline_size, float bullet_size, int id) {
        this.headline_size = headline_size;
        this.bullet_size = bullet_size;
        this.id = id;
    }

    public void setHeadline_size(float headline_size) {
        this.headline_size = headline_size;
    }

    public void setBullet_size(float bullet_size) {
        this.bullet_size = bullet_size;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getHeadline_size() {
        return headline_size;
    }

    public float getBullet_size() {
        return bullet_size;
    }

    public int getId() {
        return id;
    }
}
