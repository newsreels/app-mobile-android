package com.ziro.bullet.interfaces;

import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.model.articles.Article;

public interface OnGotoChannelListener {
        void onItemClicked(TYPE type, String id, String name, boolean favorite);
        void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position);
        void onArticleSelected(Article article);
    }