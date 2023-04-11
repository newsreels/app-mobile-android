package com.ziro.bullet.data.models;

import java.util.List;

public class CategorySequenceModel {

    private String type;
    private List<String> ids;

    public CategorySequenceModel(String type, List<String> ids) {
        this.type = type;
        this.ids = ids;
    }

    public String getType() {
        return type;
    }

    public List<String> getIds() {
        return ids;
    }
}
