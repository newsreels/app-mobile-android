package com.ziro.bullet.model.Reel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Point implements Parcelable {

	@SerializedName("latitude")
	private int latitude;

	@SerializedName("longitude")
	private int longitude;

	public void setLatitude(int latitude){
		this.latitude = latitude;
	}

	public int getLatitude(){
		return latitude;
	}

	public void setLongitude(int longitude){
		this.longitude = longitude;
	}

	public int getLongitude(){
		return longitude;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.latitude);
		dest.writeInt(this.longitude);
	}

	public void readFromParcel(Parcel source) {
		this.latitude = source.readInt();
		this.longitude = source.readInt();
	}

	public Point() {
	}

	protected Point(Parcel in) {
		this.latitude = in.readInt();
		this.longitude = in.readInt();
	}

	public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {
		@Override
		public Point createFromParcel(Parcel source) {
			return new Point(source);
		}

		@Override
		public Point[] newArray(int size) {
			return new Point[size];
		}
	};
}