package com.ziro.bullet.data.caption;

import com.google.gson.annotations.SerializedName;

public class Font{

	@SerializedName("size")
	private int size;

	@SerializedName("color")
	private String color;

	@SerializedName("style")
	private String style;

	@SerializedName("family")
	private String family;

	public void setSize(int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}

	public void setColor(String color){
		this.color = color;
	}

	public String getColor(){
		return color;
	}

	public void setStyle(String style){
		this.style = style;
	}

	public String getStyle(){
		return style;
	}

	public void setFamily(String family){
		this.family = family;
	}

	public String getFamily(){
		return family;
	}
}