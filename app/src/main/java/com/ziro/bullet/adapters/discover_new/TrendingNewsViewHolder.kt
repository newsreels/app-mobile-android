package com.ziro.bullet.adapters.discover_new

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.articles.Article

class TrendingNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var isPostArticle = false
    private var adFailedListener: AdFailedListener? = null
    private var context: AppCompatActivity? = null
    private var shareToMainInterface: ShareToMainInterface? = null
    private var swipeListener: TempCategorySwipeListener? = null
    private var gotoChannelListener: OnGotoChannelListener? = null
    private var detailsActivityInterface: DetailsActivityInterface? = null
    private var goHomeMainActivity: GoHome? = null
    private var showOptionsLoaderCallback: ShowOptionsLoaderCallback? = null
    private var mPrefConfig: PrefConfig? = null
    private var isGotoFollowShow = false
    private var type: String? = null
    private var commentClick: CommentClick? = null
    private var lifecycle: Lifecycle? = null
    private var discoverChildInterface: DiscoverChildInterface? = null

    fun setParameters(
        mCommentClick: CommentClick,
        isPostArticle: Boolean,
        context: AppCompatActivity?,
        type: String?,
        isGotoFollowShow: Boolean,
        detailsActivityInterface: DetailsActivityInterface?,
        goHomeMainActivity: GoHome?,
        shareToMainInterface: ShareToMainInterface?,
        swipeListener: TempCategorySwipeListener?,
        gotoChannelListener: OnGotoChannelListener?,
        showOptionsLoaderCallback: ShowOptionsLoaderCallback?,
        adFailedListener: AdFailedListener?,
        lifecycle: Lifecycle?,
        discoverChildInterface: DiscoverChildInterface
    ) {
        this.context = context
        this.swipeListener = swipeListener
        this.goHomeMainActivity = goHomeMainActivity
        this.shareToMainInterface = shareToMainInterface
        this.gotoChannelListener = gotoChannelListener
        this.detailsActivityInterface = detailsActivityInterface
        this.isGotoFollowShow = isGotoFollowShow
        this.type = type
        this.commentClick = mCommentClick
        this.isPostArticle = isPostArticle
        this.showOptionsLoaderCallback = showOptionsLoaderCallback
        this.adFailedListener = adFailedListener
        this.lifecycle = lifecycle
        this.discoverChildInterface = discoverChildInterface
        mPrefConfig = PrefConfig(context)
    }

    fun onBind(articlesList: List<Article>?, context: AppCompatActivity) {

        if (!articlesList.isNullOrEmpty()) {
            itemView.findViewById<CardView>(R.id.cv_top_news).apply {
                itemView.findViewById<LinearLayout>(R.id.ll_trending_news_shimmer).visibility =
                    View.GONE
                visibility = View.VISIBLE
            }
            itemView.findViewById<RecyclerView>(R.id.iv_discover_articles).apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(
                    DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider))
                )

                itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
                    discoverChildInterface?.onArticleSeeAll()
                }
                adapter = DiscoverArticlesAdapter(articlesList, true, commentClick)
            }
        }
    }
}