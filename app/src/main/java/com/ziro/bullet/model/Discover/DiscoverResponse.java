package com.ziro.bullet.model.Discover;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;


public class DiscoverResponse implements Serializable {

	@SerializedName("image")
	private String image;
	@SerializedName("sub_heading")
	private String sub_heading;

	@SerializedName("discover")
	private ArrayList<DiscoverItem> discover;

	public void setDiscover(ArrayList<DiscoverItem> discover){
		this.discover = discover;
	}

	public ArrayList<DiscoverItem> getDiscover(){
		return discover;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getSub_heading() {
		return sub_heading;
	}

	public void setSub_heading(String sub_heading) {
		this.sub_heading = sub_heading;
	}
}