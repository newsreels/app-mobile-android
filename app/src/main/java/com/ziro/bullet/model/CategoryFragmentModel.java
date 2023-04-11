package com.ziro.bullet.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ziro.bullet.model.Tabs.SubItem;

import java.util.List;

public class CategoryFragmentModel implements Parcelable {

    private String articleId, topicId, sourceId, headlineId, locationId, name, contextId;
    private List<SubItem> subItems;
    private String greeting;
    private boolean isFontSizeUpdated;

    public CategoryFragmentModel() {
    }

    public CategoryFragmentModel(String name) {
        this.name = name;
    }


    protected CategoryFragmentModel(Parcel in) {
        articleId = in.readString();
        topicId = in.readString();
        sourceId = in.readString();
        headlineId = in.readString();
        locationId = in.readString();
        name = in.readString();
        contextId = in.readString();
        subItems = in.createTypedArrayList(SubItem.CREATOR);
        greeting = in.readString();
        isFontSizeUpdated = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(articleId);
        dest.writeString(topicId);
        dest.writeString(sourceId);
        dest.writeString(headlineId);
        dest.writeString(locationId);
        dest.writeString(name);
        dest.writeString(contextId);
        dest.writeTypedList(subItems);
        dest.writeString(greeting);
        dest.writeByte((byte) (isFontSizeUpdated ? 1 : 0));
    }

    public static final Creator<CategoryFragmentModel> CREATOR = new Creator<CategoryFragmentModel>() {
        @Override
        public CategoryFragmentModel createFromParcel(Parcel in) {
            return new CategoryFragmentModel(in);
        }

        @Override
        public CategoryFragmentModel[] newArray(int size) {
            return new CategoryFragmentModel[size];
        }
    };

    public boolean isFontSizeUpdated() {
        return isFontSizeUpdated;
    }

    public void setFontSizeUpdated(boolean fontSizeUpdated) {
        isFontSizeUpdated = fontSizeUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getHeadlineId() {
        return headlineId;
    }

    public void setHeadlineId(String headlineId) {
        this.headlineId = headlineId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public List<SubItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<SubItem> subItems) {
        this.subItems = subItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
