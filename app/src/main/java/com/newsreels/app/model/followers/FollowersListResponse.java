package com.newsreels.app.model.followers;

import com.google.gson.annotations.SerializedName;
import com.newsreels.app.data.models.BaseModel;
import com.newsreels.app.model.articles.Author;

import java.util.List;

public class FollowersListResponse extends BaseModel {

    @SerializedName("users")
    private List<Author> users = null;

    public List<Author> getUsers() {
        return users;
    }

    public void setUsers(List<Author> users) {
        this.users = users;
    }

}
