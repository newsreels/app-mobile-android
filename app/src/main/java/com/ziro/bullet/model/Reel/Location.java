package com.ziro.bullet.model.Reel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Location implements Parcelable {

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.country);
		dest.writeString(this.city);
		dest.writeString(this.county);
		dest.writeByte(this.global ? (byte) 1 : (byte) 0);
		dest.writeString(this.state);
		dest.writeParcelable(this.point, flags);
	}

	public void readFromParcel(Parcel source) {
		this.country = source.readString();
		this.city = source.readString();
		this.county = source.readString();
		this.global = source.readByte() != 0;
		this.state = source.readString();
		this.point = source.readParcelable(Point.class.getClassLoader());
	}

	public Location() {
	}

	protected Location(Parcel in) {
		this.country = in.readString();
		this.city = in.readString();
		this.county = in.readString();
		this.global = in.readByte() != 0;
		this.state = in.readString();
		this.point = in.readParcelable(Point.class.getClassLoader());
	}

	public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
		@Override
		public Location createFromParcel(Parcel source) {
			return new Location(source);
		}

		@Override
		public Location[] newArray(int size) {
			return new Location[size];
		}
	};
}