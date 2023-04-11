package com.ziro.bullet.model.Discover;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;

import java.io.Serializable;


public class DiscoverItem implements Serializable {

	@SerializedName("title")
	private String title;

	@SerializedName("type")
	private String type;

	@SerializedName("view")
	private String view;

	@SerializedName("data")
	private ArrayList<Source> data;

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setView(String view){
		this.view = view;
	}

	public String getView(){
		return view;
	}

	public void setData(ArrayList<Source> data){
		this.data = data;
	}

	public ArrayList<Source> getData(){
		return data;
	}
}