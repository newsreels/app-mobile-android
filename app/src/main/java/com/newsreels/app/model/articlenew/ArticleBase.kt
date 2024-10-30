
package com.newsreels.app.model.articlenew
import com.google.gson.annotations.SerializedName
import com.newsreels.app.model.articles.Article
import com.newsreels.app.model.searchResult.Articles

data class ArticleBase (

	@SerializedName("articles") val articles : List<Article>,
	@SerializedName("meta") val meta : Meta
)