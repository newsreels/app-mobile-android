package com.ziro.bullet.data.models.NewFeed;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;

public class HomeResponse extends BaseModel {

	@SerializedName("sections")
	private ArrayList<SectionsItem> sections;

	public ArrayList<SectionsItem> getSections(){
		return sections;
	}
}