package com.ziro.bullet.data.models.author;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.data.models.BaseModel;
import com.ziro.bullet.model.articles.Author;

import java.util.ArrayList;

public class AuthorSearchResponse extends BaseModel {

    @SerializedName("authors")
    private ArrayList<Author> authors;

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }
}
