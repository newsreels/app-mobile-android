package com.ziro.bullet.model.Discover;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;


public class DataItem implements Serializable {

	@SerializedName("id")
	private String id;

	@SerializedName("name")
	private String name;

	@SerializedName("icon")
	private String icon;

	@SerializedName("image")
	private String image;

	@SerializedName("color")
	private String color;

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setIcon(String icon){
		this.icon = icon;
	}

	public String getIcon(){
		return icon;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setColor(String color){
		this.color = color;
	}

	public String getColor(){
		return color;
	}
}