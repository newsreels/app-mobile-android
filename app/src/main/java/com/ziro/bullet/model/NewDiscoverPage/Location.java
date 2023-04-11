package com.ziro.bullet.model.NewDiscoverPage;

import com.google.gson.annotations.SerializedName;

public class Location{

	@SerializedName("country")
	private String country;

	@SerializedName("city")
	private String city;

	@SerializedName("county")
	private String county;

	@SerializedName("global")
	private boolean global;

	@SerializedName("state")
	private String state;

	@SerializedName("point")
	private Point point;

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setCounty(String county){
		this.county = county;
	}

	public String getCounty(){
		return county;
	}

	public void setGlobal(boolean global){
		this.global = global;
	}

	public boolean isGlobal(){
		return global;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setPoint(Point point){
		this.point = point;
	}

	public Point getPoint(){
		return point;
	}
}