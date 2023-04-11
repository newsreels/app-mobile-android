package com.ziro.bullet.adapters.discover_new

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.adapters.feed.FeedAdapter
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.discoverNew.DiscoverNew

class PlacesArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
        lifecycle: Lifecycle?
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
        mPrefConfig = PrefConfig(context)
    }

    fun onBind(articlesList: List<Article>?, position: Int,
               discoverItem: DiscoverNew, context: AppCompatActivity) {

        if (articlesList != null) {
            itemView.findViewById<RecyclerView>(R.id.iv_discover_articles).apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(
                    DividerItemDecoration(
                        itemView.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                adapter = FeedAdapter(
                    commentClick,
                    isPostArticle,
                    context,
                    articlesList,
                    type,
                    isGotoFollowShow,
                    detailsActivityInterface,
                    goHomeMainActivity,
                    shareToMainInterface,
                    swipeListener,
                    null,
                    gotoChannelListener,
                    showOptionsLoaderCallback,
                    adFailedListener,
                    lifecycle
                )
            }
        }
    }
}