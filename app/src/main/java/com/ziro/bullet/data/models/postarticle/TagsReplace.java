package com.ziro.bullet.data.models.postarticle;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TagsReplace {

    private String tempId;
    private TagItem tagItem;

    public TagsReplace(String tempId, TagItem tagItem) {
        this.tempId = tempId;
        this.tagItem = tagItem;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public TagItem getTagItem() {
        return tagItem;
    }

    public void setTagItem(TagItem tagItem) {
        this.tagItem = tagItem;
    }
}
