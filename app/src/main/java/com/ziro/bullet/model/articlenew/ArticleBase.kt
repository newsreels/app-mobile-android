
package com.ziro.bullet.model.articlenew
import com.google.gson.annotations.SerializedName
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.searchResult.Articles

data class ArticleBase (

	@SerializedName("articles") val articles : List<Article>,
	@SerializedName("meta") val meta : Meta
)