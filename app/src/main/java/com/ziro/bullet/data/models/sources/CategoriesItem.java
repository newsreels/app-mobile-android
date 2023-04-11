package com.ziro.bullet.data.models.sources;

import com.google.gson.annotations.SerializedName;

public class CategoriesItem{

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private String id;

	@SerializedName("context")
	private String context;

	public String getName(){
		return name;
	}

	public String getId(){
		return id;
	}

	public String getContext() {
		return context;
	}
}