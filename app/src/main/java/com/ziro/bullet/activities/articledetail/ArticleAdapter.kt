package com.ziro.bullet.activities.articledetail

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.interfaces.LikeInterface
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.articles.Bullet
import com.ziro.bullet.presenter.LikePresenter
import com.ziro.bullet.presenter.ShareBottomSheetPresenter
import com.ziro.bullet.utills.Utils
import jp.wasabeef.picasso.transformations.BlurTransformation

class ArticleAdapter(
    private val context: Context,
    private val mprefConfig: PrefConfig,
    private var articleFragInterface: ArticleFragInterface?,
    private var likePresenter: LikePresenter?,
    private var shareBottomSheetPresenter: ShareBottomSheetPresenter?
) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    private var articleId: String? = null
    private var prefConfig: PrefConfig? = null
    private var articleList = ArrayList<Article>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        this.prefConfig = mprefConfig
        val article = articleList[position]
        holder.sourceName.text = article.sourceNameToDisplay
        holder.loadData(article, context)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    fun setArticleList(articleList: ArrayList<Article>) {
        this.articleList = articleList
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView
        var backImg: ImageView
        var sourceImage: ImageView
        var favIcon: ImageView
        var articleTitle: TextView
        var commentCount: TextView
        var viewCount: TextView
        var postTime: TextView
        var viewFullArticle: TextView
        var favCount: TextView
        var authorName: TextView
        var sourceName: TextView
        var share: LinearLayout
        var llFavIcon: LinearLayout
        var bulletContainer: LinearLayout
        var comment: LinearLayout
        var bottom_rl: RelativeLayout

        init {
            postImage = itemView.findViewById(R.id.post_image)
            backImg = itemView.findViewById(R.id.back_img)
            bottom_rl = itemView.findViewById(R.id.bottom_rl)
            articleTitle = itemView.findViewById(R.id.article_title)
            share = itemView.findViewById(R.id.share)
            comment = itemView.findViewById(R.id.comment)
            commentCount = itemView.findViewById(R.id.commentCount)
            favIcon = itemView.findViewById(R.id.favIcon)
            llFavIcon = itemView.findViewById(R.id.ll_favorite)
            favCount = itemView.findViewById(R.id.favCount)
            postImage = itemView.findViewById(R.id.post_image)
            articleTitle = itemView.findViewById(R.id.article_title)
            bulletContainer = itemView.findViewById(R.id.bullet_container)
            sourceName = itemView.findViewById(R.id.source_name)
            sourceImage = itemView.findViewById(R.id.source_image)
            postTime = itemView.findViewById(R.id.post_time)
            viewCount = itemView.findViewById(R.id.view_count)
            viewFullArticle = itemView.findViewById(R.id.view_full_article)
            authorName = itemView.findViewById(R.id.author_name)
        }

        @Throws(Exception::class)
        fun loadData(article: Article, context: Context) {
            if (article != null) {
//                showLoaderInActivity(false)
                articleId = article.id

                Glide.with(postImage)
                    .load(article.image)
                    .into(postImage)
                try {
//                    updateFollowColor(article.getSource().isFavorite())
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                if (article.info.isLiked) {
                    favIcon.setImageResource(R.drawable.ic_reel_like_active)
                    favCount.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.theme_color_1
                        )
                    )

                    DrawableCompat.setTint(
                        favIcon.drawable,
                        context.resources.getColor(R.color.theme_color_1)
                    )
                } else {
                    favCount.setTextColor(ContextCompat.getColor(context, R.color.greyad))
                    favIcon.setImageResource(R.drawable.ic_heartborderwhite)
                    DrawableCompat.setTint(
                        favIcon.drawable,
                        context.resources.getColor(R.color.greyad)
                    )
                }
                if (article.info != null) {
                    commentCount.text = "" + article.info.comment_count
                    favCount.text = "" + article.info.like_count

                    commentCount.visibility =
                        if (article.info.comment_count > 0) View.VISIBLE else View.GONE
                    favCount.visibility =
                        if (article.info.like_count > 0) View.VISIBLE else View.GONE
                }

                Glide.with(sourceImage)
                    .load(article.sourceImageToDisplay)
                    .into(sourceImage)

                sourceName.text = article.sourceNameToDisplay
                if (Utils.getLanguageDirectionForView(article.languageCode) == View.TEXT_DIRECTION_LTR) {
                    articleTitle.setPadding(
                        0,
                        context.resources.getDimensionPixelOffset(R.dimen._10sdp),
                        context.resources.getDimensionPixelOffset(R.dimen._35sdp),
                        0
                    )
                } else {
                    articleTitle.setPadding(
                        context.resources.getDimensionPixelOffset(R.dimen._35sdp),
                        context.resources.getDimensionPixelOffset(R.dimen._10sdp),
                        0,
                        0
                    )
                }
                articleTitle.textDirection =
                    Utils.getLanguageDirectionForView(article.languageCode)
                articleTitle.text = article.title
                authorName.text = "By " + article.sourceNameToDisplay

                val size = Utils.getHeadlineDimens(prefConfig, context as Activity?)
                if (size != -1f) {
                    articleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
                }
                val time = Utils.getTimeAgo(Utils.getDate(article.publishTime), context)
                postTime.text = time
                if (article.info != null) {
                    //viewCount.setText(article.getInfo().getViewCount());
                    if (article.info.viewCount != "1") {
                        viewCount.text = context.getString(
                            R.string.view_counts,
                            article.info.viewCount
                        )
                    } else {
                        viewCount.text = context.getString(
                            R.string.view_count,
                            article.info.viewCount
                        )
                    }
                }
                loadImagePost(article)

                addBullets(article)

            }
            share.setOnClickListener {
                articleFragInterface?.share(article)
            }

            viewFullArticle.setOnClickListener {
                articleFragInterface?.viewFullArticle(article)
            }

            comment.setOnClickListener {
                articleFragInterface?.commentsPage(article)

            }

            llFavIcon.setOnClickListener {
                llFavIcon.isEnabled = false
                likePresenter?.like(article.id, object : LikeInterface {
                    override fun success(like: Boolean) {
                        llFavIcon.isEnabled = true
                        article.info.isLiked = like
                        var counter = article.info.like_count
                        if (like) {
                            counter++
                        } else {
                            if (counter > 0) {
                                counter--
                            } else {
                                counter = 0
                            }
                        }
                        article.info.like_count = counter
                        favCount.text = "" + counter
                        favCount.visibility =
                            if (article.info.like_count > 0) View.VISIBLE else View.VISIBLE

                        if (article.info.isLiked) {
                            favIcon.setImageResource(R.drawable.ic_reel_like_active)
                            favCount.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.theme_color_1
                                )
                            )
                            DrawableCompat.setTint(
                                favIcon.drawable,
                                context.resources.getColor(R.color.theme_color_1)
                            )
                        } else {
                            favIcon.setImageResource(R.drawable.ic_reel_like_inactive)
                            favCount.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.greyad
                                )
                            )
                            DrawableCompat.setTint(
                                favIcon.drawable,
                                context.resources.getColor(R.color.greyad)
                            )
                        }
                    }

                    override fun failure() {
                        llFavIcon.isEnabled = true
                    }
                }, !article.info.isLiked)
            }


        }


        private fun addBullets(article: Article) {
            bulletContainer.removeAllViews()
            for ((i, bullet) in article.bullets.withIndex()) {
//                if (i < 2) {
                if (i != 0 || (bullet.data.trim { it <= ' ' } == article.title.trim { it <= ' ' } || bullet.data.trim { it <= ' ' } != article.getTitle()
                        .trim { it <= ' ' })) {
                    bulletContainer.addView(createBullet(bullet, article.languageCode, article))
                }
//                }
            }

        }

        private fun loadImagePost(article: Article) {

            if (article != null && !TextUtils.isEmpty(article.originalLink)) viewFullArticle.visibility =
                View.VISIBLE else viewFullArticle.visibility = View.GONE


            Picasso.get()
                .load(article.image)
                .transform(BlurTransformation(context, 45, 3))
                .into(backImg, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        // Image successfully loaded

                        bottom_rl.background = backImg.drawable

                    }


                    override fun onError(e: Exception?) {
                        // Handle error if needed
                    }
                })
        }

        private fun createBullet(bullet: Bullet, langCode: String, article: Article): View {
            val vi =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v: View =
                if (Utils.getLanguageDirectionForView(langCode) == View.TEXT_DIRECTION_LTR) {
                    vi.inflate(R.layout.detail_bullet_item, null)
                } else {
                    vi.inflate(R.layout.detail_bullet_item_rtl, null)
                }
            val bulletText = v.findViewById<TextView>(R.id.bullet_text)
            bulletText.text = bullet.data
            bulletText.textDirection = Utils.getLanguageDirectionForView(article.languageCode)
            val `val` = Utils.getBulletDimens(prefConfig, context as Activity?)
            if (`val` != -1f) {
                bulletText.setTextSize(TypedValue.COMPLEX_UNIT_PX, `val`)
            }
            return v
        }

    }


}

