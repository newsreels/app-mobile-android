package com.newsreels.app.interfaces;

import com.newsreels.app.data.TYPE;
import com.newsreels.app.model.articles.Article;

public interface OnGotoChannelListener {
        void onItemClicked(TYPE type, String id, String name, boolean favorite);
        void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position);
        void onArticleSelected(Article article);
    }