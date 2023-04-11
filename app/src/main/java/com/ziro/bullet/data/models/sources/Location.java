package com.ziro.bullet.data.models.sources;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {

	@SerializedName("country")
	private String country;

	@SerializedName("city")
	private String city;

	@SerializedName("county")
	private String county;

	@SerializedName("global")
	private Boolean global;

	@SerializedName("state")
	private String state;

	@SerializedName("point")
	private Point point;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public Boolean getGlobal() {
		return global;
	}

	public void setGlobal(Boolean global) {
		this.global = global;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
}