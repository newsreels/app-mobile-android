package com.newsreels.app.interfaces;


import com.newsreels.app.model.articles.Author;

public interface AuthorApiCallback {
    void loaderShow(boolean flag);

    void error(String error);

    void success(Author author);
}
