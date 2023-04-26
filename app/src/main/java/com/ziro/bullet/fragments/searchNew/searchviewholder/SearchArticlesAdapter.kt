package com.ziro.bullet.fragments.searchNew.searchviewholder

import android.app.Activity
import android.content.DialogInterface
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.analytics.AnalyticsEvents.logEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.ShareInfo
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.articles.Bullet
import com.ziro.bullet.presenter.ShareBottomSheetPresenter
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.Utils

class SearchArticlesAdapter(val searchedArticles: List<Article>?) :
    RecyclerView.Adapter<SearchArticlesAdapter.SearchArticleViewHolder>() {

    private var isPostArticle = false
    private var adFailedListener: AdFailedListener? = null
    private var appCompatActivity: AppCompatActivity? = null
    private var shareToMainInterface: ShareToMainInterface? = null
    private var swipeListener: TempCategorySwipeListener? = null
    private var gotoChannelListener: OnGotoChannelListener? = null
    private var detailsActivityInterface: DetailsActivityInterface? = null
    private var goHomeMainActivity: GoHome? = null
    private var showOptionsLoaderCallback: ShowOptionsLoaderCallback? = null
    private var shareBottomSheetPresenter: ShareBottomSheetPresenter? = null
    private var adapterCallback: AdapterCallback? = null
    private var mPrefConfig: PrefConfig? = null
    private var isGotoFollowShow = false
    private var type: String? = null
    private var commentClick: CommentClick? = null
    private var lifecycle: Lifecycle? = null

    inner class SearchArticleViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvBulletHeadline: TextView = itemView.findViewById(R.id.tv_bullet_headline)
        private val ivBulletCover: ImageView = itemView.findViewById(R.id.bullet_image)
        private val ivDots: ImageView = itemView.findViewById(R.id.dotImg)
        private val tvSource: TextView = itemView.findViewById(R.id.source_name)
        private val tvTimeSincePosted: TextView = itemView.findViewById(R.id.time)

        fun onBind(position: Int) {
            val article = searchedArticles!![position]

            val bulletArrayList: List<Bullet> = article.bullets
            if (bulletArrayList != null && bulletArrayList.isNotEmpty()) {
                tvBulletHeadline.text = article.bullets[0].data
                try {
                    val textSize = Utils.getBulletDimens(
                        PrefConfig(itemView.context),
                        itemView.context as Activity
                    )
                    tvBulletHeadline.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Glide.with(itemView.context)
                .load(article.image)
                .override(Constants.targetWidth, Constants.targetHeight)
                .into(ivBulletCover)

            if (article.source != null && !TextUtils.isEmpty(article.source.name)) {
                tvSource.text = article.source.name
            }

            val time = Utils.getTimeAgo(Utils.getDate(article.publishTime), itemView.context)
            if (!TextUtils.isEmpty(time)) {
                tvTimeSincePosted.text = time
            }

            itemView.setOnClickListener { view: View? ->
                val params: MutableMap<String, String> = HashMap()
                params[Events.KEYS.ARTICLE_ID] = article.id
                logEvent(
                    itemView.context,
                    params,
                    Events.ARTICLE_OPEN
                )
                commentClick?.onNewDetailClick(
                    position,
                    searchedArticles[position],
                    searchedArticles
                )
            }

            ivDots.setOnClickListener { v: View? ->
                val params: MutableMap<String, String> =
                    java.util.HashMap()
                params[Events.KEYS.ARTICLE_ID] = article.id
                logEvent(
                    itemView.context,
                    params,
                    Events.SHARE_CLICK
                )
                if (detailsActivityInterface != null) {
                    detailsActivityInterface?.pause()
                }
                if (shareBottomSheetPresenter != null) {
                    showOptionsLoaderCallback?.showLoader(true)
                    shareBottomSheetPresenter?.share_msg(
                        article.id,
                        object : ShareInfoInterface {
                            override fun response(shareInfo: ShareInfo) {
                                showOptionsLoaderCallback?.showLoader(false)
                                adapterCallback?.showShareBottomSheet(
                                    shareInfo,
                                    article
                                ) {
                                    if (detailsActivityInterface != null) {
                                        detailsActivityInterface?.resume()
                                    }
                                }
                            }

                            override fun error(error: String) {
                                showOptionsLoaderCallback?.showLoader(false)
                                adapterCallback?.showShareBottomSheet(
                                    null, article
                                ) { dialog: DialogInterface? ->
                                    if (detailsActivityInterface != null) {
                                        detailsActivityInterface?.resume()
                                    }
                                }
                            }
                        })
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchArticleViewHolder {
        return SearchArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.article_item_new, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchArticleViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return searchedArticles!!.size
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
        this.shareBottomSheetPresenter = ShareBottomSheetPresenter(appCompatActivity)
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