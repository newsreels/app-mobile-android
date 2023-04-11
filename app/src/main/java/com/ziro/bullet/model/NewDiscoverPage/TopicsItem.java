package com.ziro.bullet.model.NewDiscoverPage;

import com.google.gson.annotations.SerializedName;

public class TopicsItem{

	@SerializedName("image")
	private String image;

	@SerializedName("color")
	private String color;

	@SerializedName("context")
	private String context;

	@SerializedName("name")
	private String name;

	@SerializedName("icon")
	private String icon;

	@SerializedName("id")
	private String id;

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

	public void setContext(String context){
		this.context = context;
	}

	public String getContext(){
		return context;
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

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}
}