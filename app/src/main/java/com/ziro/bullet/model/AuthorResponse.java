package com.ziro.bullet.model;

import com.google.gson.annotations.SerializedName;
import com.ziro.bullet.model.articles.Author;

public class AuthorResponse {

    @SerializedName("author")
    private Author author;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
