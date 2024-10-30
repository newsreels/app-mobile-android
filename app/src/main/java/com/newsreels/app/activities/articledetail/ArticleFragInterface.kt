package com.newsreels.app.activities.articledetail

import com.newsreels.app.data.models.sources.Source
import com.newsreels.app.model.Reel.ReelsItem
import com.newsreels.app.model.articles.Article

interface ArticleFragInterface {

    fun share(article:Article)
    fun viewFullArticle(article:Article)
    fun commentsPage(article:Article)


//    fun onItemClick(position: Int, item: Source?)
}