package com.ziro.bullet.data.models.NewFeed;

import com.google.gson.annotations.SerializedName;

public class SectionsItem{

	public SectionsItem(){}

	public SectionsItem(String type) {
		this.type = type;
	}

	@SerializedName("data")
	private Data data;

	@SerializedName("type")
	private String type;

	@SerializedName("id")
	private String id;

	public Data getData(){
		return data;
	}

	public String getType(){
		return type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}