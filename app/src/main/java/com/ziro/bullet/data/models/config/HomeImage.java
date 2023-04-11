package com.ziro.bullet.data.models.config;

import com.google.gson.annotations.SerializedName;

public class HomeImage{

	@SerializedName("left")
	private String left;

	@SerializedName("right")
	private String right;

	@SerializedName("opacity")
	private float opacity;

	public void setLeft(String left) {
		this.left = left;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public String getLeft(){
		return left;
	}

	public String getRight(){
		return right;
	}
}