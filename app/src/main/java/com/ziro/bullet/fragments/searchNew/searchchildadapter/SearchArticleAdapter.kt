package com.ziro.bullet.fragments.searchNew.searchchildadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.model.articles.Article

class SearchArticleAdapter :
    RecyclerView.Adapter<SearchArticleAdapter.ArticleViewHolder>() {

    private var articleTopicList = listOf<Article>()
    private lateinit var searchFirstChildInterface: SearchFirstChildInterface

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(articleTopic: Article) {

            Glide.with(itemView.context)
                .load(articleTopic.image)
                .placeholder(R.drawable.img_place_holder)
                .into(itemView.findViewById(R.id.iv_article_image))
            itemView.findViewById<TextView>(R.id.tv_article_headline).text = articleTopic.title
            itemView.findViewById<TextView>(R.id.tv_time).text = "28 min"
            itemView.findViewById<TextView>(R.id.tv_authorname).text = "Paul Steinhauser"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.article_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.onBind(articleTopicList[position])
        holder.itemView.setOnClickListener {
            if (searchFirstChildInterface != null) {
                searchFirstChildInterface.searchChildArticleSecondOnClick(articleTopicList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return articleTopicList.size
    }

    fun addChildListener(searchFirstChildInterface: SearchFirstChildInterface) {
        this.searchFirstChildInterface = searchFirstChildInterface
    }

    fun updateTopics(trendingTopicList: List<Article>) {
        this.articleTopicList = trendingTopicList
        notifyDataSetChanged()
    }
}