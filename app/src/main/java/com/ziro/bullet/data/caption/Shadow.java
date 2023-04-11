package com.ziro.bullet.data.caption;

import com.google.gson.annotations.SerializedName;

public class Shadow {

    @SerializedName("color")
    private String shadowColor;
    @SerializedName("radius")
    private float radius;
    @SerializedName("x")
    private float x;
    @SerializedName("y")
    private float y;

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
    }

    public float getShadow_radius() {
        return radius;
    }

    public void setShadow_radius(float radius) {
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
