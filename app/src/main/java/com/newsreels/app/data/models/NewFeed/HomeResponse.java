package com.newsreels.app.data.models.NewFeed;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.BaseModel;

public class HomeResponse extends BaseModel {

	@SerializedName("sections")
	private ArrayList<SectionsItem> sections;

	public ArrayList<SectionsItem> getSections(){
		return sections;
	}
}