package com.ziro.bullet.model.Tabs;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NewsItem {

	@SerializedName("data")
	private ArrayList<DataItem> data;

	@SerializedName("header_text")
	private String headerText;
	@SerializedName("image")
	private String image;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setData(ArrayList<DataItem> data){
		this.data = data;
	}

	public ArrayList<DataItem> getData(){
		return data;
	}

	public void setHeaderText(String headerText){
		this.headerText = headerText;
	}

	public String getHeaderText(){
		return headerText;
	}
}