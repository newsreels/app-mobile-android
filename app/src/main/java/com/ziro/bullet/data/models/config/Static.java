package com.ziro.bullet.data.models.config;

import com.google.gson.annotations.SerializedName;

public class Static{

	@SerializedName("home_image")
	private HomeImage homeImage;

	public void setHomeImage(HomeImage homeImage) {
		this.homeImage = homeImage;
	}

	public HomeImage getHomeImage(){
		return homeImage;
	}
}