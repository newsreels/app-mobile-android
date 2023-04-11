package com.ziro.bullet.interfaces;


import com.ziro.bullet.model.articles.Author;

public interface AuthorApiCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(Author author);
}
