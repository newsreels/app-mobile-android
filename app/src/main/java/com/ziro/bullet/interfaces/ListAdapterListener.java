package com.ziro.bullet.interfaces;

public interface ListAdapterListener {
    void verticalScrollList(boolean isEnable);

    void nextArticle(int position);

    void prevArticle(int position);

    void clickArticle(int position);
}