package com.ziro.bullet.fragments.searchNew.searchviewholder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.articles.Article

class SearchArticlesItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var rvTrendingArticle: RecyclerView

    private var isPostArticle = false
    private var adFailedListener: AdFailedListener? = null
    private var appCompatActivity: AppCompatActivity? = null
    private var shareToMainInterface: ShareToMainInterface? = null
    private var swipeListener: TempCategorySwipeListener? = null
    private var gotoChannelListener: OnGotoChannelListener? = null
    private var detailsActivityInterface: DetailsActivityInterface? = null
    private var goHomeMainActivity: GoHome? = null
    private var showOptionsLoaderCallback: ShowOptionsLoaderCallback? = null
    private var adapterCallback: AdapterCallback? = null
    private var mPrefConfig: PrefConfig? = null
    private var isGotoFollowShow = false
    private var type: String? = null
    private var commentClick: CommentClick? = null
    private var lifecycle: Lifecycle? = null

    fun onBind(
        position: Int,
        articlesList: List<Article>,
        searchFirstChildInterface: SearchFirstChildInterface
    ) {
        itemView.findViewById<LinearLayout>(R.id.ll_trending_channel_shimmer).visibility = View.GONE
        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = context.resources.getString(R.string.stories)
        }


        if (articlesList.size >= 5) {
            itemView.findViewById<TextView>(R.id.tv_more).apply {
                text = context.resources.getString(R.string.see_more)
            }
        } else {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
        }


//        fun String.toColor(): Int = Color.parseColor(this)
//        itemView.findViewById<TextView>(R.id.tv_more).setTextColor("#EB5165".toColor())

        itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
            searchFirstChildInterface.searchChildOnArticleClick(articlesList)
        }
        if (articlesList.isNotEmpty()) {

//            val searchArticleAdapter = SearchArticleAdapter()
            if (!::rvTrendingArticle.isInitialized) {
                rvTrendingArticle = itemView.findViewById(R.id.rv_trending_channels)
            }
//            searchArticleAdapter.addChildListener(searchFirstChildInterface)
            itemView.findViewById<RecyclerView>(R.id.rv_trending_channels).apply {
                visibility = View.VISIBLE
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

                adapter = SearchArticlesAdapter(articlesList).also {
                    it.setParameters(
                        commentClick!!,
                        isPostArticle,
                        appCompatActivity,
                        type,
                        isGotoFollowShow,
                        detailsActivityInterface,
                        goHomeMainActivity,
                        shareToMainInterface,
                        swipeListener,
                        gotoChannelListener,
                        showOptionsLoaderCallback,
                        adFailedListener,
                        lifecycle,
                        adapterCallback
                    )
                }

                val itemDecorator = DividerItemDecorator(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.divider
                    )
                )
                rvTrendingArticle.addItemDecoration(itemDecorator)
            }
        }

    }

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
        adapterCallback: AdapterCallback?
    ) {
        this.appCompatActivity = context
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
        this.adapterCallback = adapterCallback
        mPrefConfig = PrefConfig(context)
    }

}