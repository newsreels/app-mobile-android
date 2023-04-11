package com.ziro.bullet.adapters.discover_new

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.ShareInfo
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.fragments.searchNew.interfaces.SearchFirstChildInterface
import com.ziro.bullet.interfaces.AdapterCallback
import com.ziro.bullet.interfaces.DetailsActivityInterface
import com.ziro.bullet.interfaces.ShareInfoInterface
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.articles.Bullet
import com.ziro.bullet.model.articles.MediaMeta
import com.ziro.bullet.presenter.ShareBottomSheetPresenter
import com.ziro.bullet.utills.Utils

class DiscoverTrendingReelsAdapter(val context: AppCompatActivity? = null) :
    RecyclerView.Adapter<DiscoverTrendingReelsAdapter.TrendingReelsViewHolder>() {

    private var trendingReels = listOf<ReelsItem>()
    private lateinit var searchFirstChildInterface: SearchFirstChildInterface
    private lateinit var discoverChildInterface: DiscoverChildInterface
    var isDiscoverPage: Boolean = false
    private var showOptionsLoaderCallback: ShowOptionsLoaderCallback? = null
    private var adapterCallback: AdapterCallback? = null
    private var listener: DetailsActivityInterface? = null
    private val shareBottomSheetPresenter: ShareBottomSheetPresenter =
        ShareBottomSheetPresenter(context)
    private lateinit var mPrefConfig: PrefConfig

    inner class TrendingReelsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            mPrefConfig = PrefConfig(itemView.context)
        }

        fun onBind(discoverNewReels: ReelsItem) {

            Glide.with(itemView.context)
                .load(discoverNewReels.image)
                .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.appTheme))
                .into(itemView.findViewById(R.id.image))

            if (!TextUtils.isEmpty(discoverNewReels.info.viewCount)) {
                itemView.findViewById<TextView>(R.id.tv_views_total).text =
                    "${Utils.formatViews(discoverNewReels.info.viewCount.toLong())} views"
            }

//            if (discoverNewReels.isNativetitle) {
            itemView.findViewById<TextView>(R.id.tv_reel_title).text =
                discoverNewReels.description.toString()
//            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingReelsViewHolder {
        return TrendingReelsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.trending_reel_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TrendingReelsViewHolder, position: Int) {
        holder.onBind(trendingReels[position])

        holder.itemView.setOnClickListener {
            var pos = position
            try {
                pos = if (trendingReels.size > position + 10) {
                    position + 10
                } else {
                    trendingReels.size
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val reelsItems: ArrayList<ReelsItem> =
                ArrayList<ReelsItem>(trendingReels.subList(position, pos))
            if (::discoverChildInterface.isInitialized) {
                if (isDiscoverPage) {
                    discoverChildInterface.searchChildSecondOnClick(
                        trendingReels[position],
                        reelsItems,
                        position
                    )
                }
            }
            if (::searchFirstChildInterface.isInitialized) {
                if (!isDiscoverPage) {
                    searchFirstChildInterface.searchChildSecondOnClick(
                        trendingReels[position],
                        reelsItems,
                        position
                    )
                }
            }

            holder.itemView.findViewById<ImageView>(R.id.iv_reel_option)
                .setOnClickListener {
                    if (shareBottomSheetPresenter != null) {
                        showOptionsLoaderCallback!!.showLoader(true)
                        shareBottomSheetPresenter.share_msg(
                            getArticleFromReels(trendingReels[position]).id,
                            object : ShareInfoInterface {
                                override fun response(shareInfo: ShareInfo) {
                                    showOptionsLoaderCallback?.showLoader(false)
                                    adapterCallback?.showShareBottomSheet(
                                        shareInfo,
                                        getArticleFromReels(trendingReels[position])
                                    ) {
                                        if (listener != null) {
                                            listener?.resume()
                                        }
                                    }
                                }

                                override fun error(error: String) {
                                    showOptionsLoaderCallback?.showLoader(false)
                                    adapterCallback!!.showShareBottomSheet(
                                        null,
                                        getArticleFromReels(trendingReels[position])
                                    ) {
                                        if (listener != null) {
                                            listener!!.resume()
                                        }
                                    }
                                }
                            })
                    }
                }
        }
    }

    override fun getItemCount(): Int {
        return if (trendingReels.size >= 6)
            6
        else
            trendingReels.size
    }

    fun addChildListener(
        searchFirstChildInterface: SearchFirstChildInterface,
        isDiscover: Boolean,
    ) {
        this.searchFirstChildInterface = searchFirstChildInterface
    }

    fun addChildListenerDiscover(
        discoverChildInterface: DiscoverChildInterface,
        isDiscover: Boolean
    ) {
        this.discoverChildInterface = discoverChildInterface
        this.isDiscoverPage = isDiscover
    }

    fun addShareBottomSheetCallback(
        showOptionsLoaderCallback: ShowOptionsLoaderCallback,
        adapterCallback: AdapterCallback,
        listener: DetailsActivityInterface
    ) {
        this.showOptionsLoaderCallback = showOptionsLoaderCallback
        this.adapterCallback = adapterCallback
        this.listener = listener
    }

    fun updateTrendingReels(trendingReels: List<ReelsItem>) {
        this.trendingReels = trendingReels
        notifyDataSetChanged()
    }

    private fun getArticleFromReels(reels: ReelsItem?): Article {
        val article = Article()
        if (reels != null) {
            article.id = reels.id
            article.title = reels.description
            article.link = reels.media
            article.publishTime = reels.publishTime
            article.type = "REELS"
            article.source = reels.source
            article.author = reels.author
            val mediaMeta = MediaMeta()
            if (reels.mediaMeta != null) {
                mediaMeta.duration = reels.mediaMeta.duration
                mediaMeta.height = reels.mediaMeta.height
                mediaMeta.width = reels.mediaMeta.width
            }
            article.mediaMeta = mediaMeta
            val bullets = ArrayList<Bullet>()
            val bullet = Bullet()
            bullet.image = reels.image
            bullet.data = reels.description
            bullets.add(bullet)
            article.bullets = bullets
            article.isSelected = true
        }
        return article
    }
}
