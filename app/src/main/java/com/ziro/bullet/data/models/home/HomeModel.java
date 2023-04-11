package com.ziro.bullet.data.models.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.Tabs.DataItem;

import java.util.ArrayList;

public class HomeModel implements Parcelable {
    @SerializedName("data")
    private ArrayList<DataItem> data;


    @SerializedName("topics")
    private ArrayList<Topics> topicsList;

    @SerializedName("sources")
    private ArrayList<Source> sourceList;

    @SerializedName("authors")
    private ArrayList<Author> authorList;

    public void setData(ArrayList<DataItem> data){
        this.data = data;
    }

    public ArrayList<DataItem> getData(){
        return data;
    }


    public ArrayList<Topics> getTopicsList() {
        return topicsList;
    }

    public ArrayList<Source> getSourceList() {
        return sourceList;
    }

    public ArrayList<Author> getAuthorList() {
        return authorList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.data);
        dest.writeTypedList(this.topicsList);
        dest.writeList(this.sourceList);
        dest.writeTypedList(this.authorList);
    }

    public void readFromParcel(Parcel source) {
        this.data = new ArrayList<DataItem>();
        source.readList(this.data, DataItem.class.getClassLoader());
        this.topicsList = source.createTypedArrayList(Topics.CREATOR);
        this.sourceList = new ArrayList<Source>();
        source.readList(this.sourceList, Source.class.getClassLoader());
        this.authorList = source.createTypedArrayList(Author.CREATOR);
    }

    public HomeModel() {
    }

    protected HomeModel(Parcel in) {
        this.data = new ArrayList<DataItem>();
        in.readList(this.data, DataItem.class.getClassLoader());
        this.topicsList = in.createTypedArrayList(Topics.CREATOR);
        this.sourceList = new ArrayList<Source>();
        in.readList(this.sourceList, Source.class.getClassLoader());
        this.authorList = in.createTypedArrayList(Author.CREATOR);
    }

    public static final Creator<HomeModel> CREATOR = new Creator<HomeModel>() {
        @Override
        public HomeModel createFromParcel(Parcel source) {
            return new HomeModel(source);
        }

        @Override
        public HomeModel[] newArray(int size) {
            return new HomeModel[size];
        }
    };
}
