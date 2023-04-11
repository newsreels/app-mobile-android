package com.ziro.bullet.data.models.sources;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Info{

	@SerializedName("info")
	private Info info;

	@SerializedName("category_title")
	private String categoryTitle;

	@SerializedName("name")
	private String name;

	@SerializedName("language")
	private String language;

	@SerializedName("categories")
	private ArrayList<CategoriesItem> categories;

	@SerializedName("category")
	private String category;

	@SerializedName("favorite")
	private boolean favorite;

	public Info getInfo(){
		return info;
	}

	public String getCategoryTitle(){
		return categoryTitle;
	}

	public String getName(){
		return name;
	}

	public String getLanguage(){
		return language;
	}

	public ArrayList<CategoriesItem> getCategories(){
		return categories;
	}

	public String getCategory(){
		return category;
	}

	public boolean isFavorite(){
		return favorite;
	}
}