package com.ziro.bullet.activities.articledetail

import com.ziro.bullet.data.models.sources.Source
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articles.Article

interface ArticleFragInterface {

    fun share(article:Article)
    fun viewFullArticle(article:Article)
    fun commentsPage(article:Article)


//    fun onItemClick(position: Int, item: Source?)
}