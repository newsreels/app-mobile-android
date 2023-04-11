package com.ziro.bullet.model;

import com.google.gson.annotations.SerializedName;

public class CategoriesItem{

	@SerializedName("name")
	private String name;

	@SerializedName("icon")
	private String icon;

	@SerializedName("value")
	private String value;
	private boolean selected;

	public CategoriesItem(String name, String icon, String value,boolean selected) {
		this.name = name;
		this.icon = icon;
		this.value = value;
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

}