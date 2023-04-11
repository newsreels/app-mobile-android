package com.ziro.bullet.model.NewDiscoverPage;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

public class NewDiscoverResponse extends BaseModel {

	@SerializedName("discover")
	private ArrayList<DiscoverItem> discover;

	public void setDiscover(ArrayList<DiscoverItem> discover){
		this.discover = discover;
	}

	public ArrayList<DiscoverItem> getDiscover(){
		return discover;
	}
}