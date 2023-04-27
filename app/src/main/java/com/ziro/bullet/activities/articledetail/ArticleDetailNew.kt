package com.ziro.bullet.activities.articledetail

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.ziro.bullet.R
import com.ziro.bullet.activities.*
import com.ziro.bullet.bottomSheet.ShareBottomSheet
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.data.models.NewFeed.HomeResponse
import com.ziro.bullet.data.models.ShareInfo
import com.ziro.bullet.following.ui.FollowingFragment.Companion.goHome
import com.ziro.bullet.fragments.BulletDetailFragment
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.articles.ArticleResponse
import com.ziro.bullet.presenter.LikePresenter
import com.ziro.bullet.presenter.NewsPresenter
import com.ziro.bullet.presenter.ShareBottomSheetPresenter
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.InternetCheckHelper
import com.ziro.bullet.utills.Utils


class ArticleDetailNew : BaseActivity(), NewsCallback, ArticleFragInterface {
    private lateinit var viewPager: ViewPager2
    var ivBack: ImageView? = null
    var dotImg: ImageView? = null
    private var adapterCallback: AdapterCallback? = null
    private var articlelist: ArrayList<Article>? = null
    private var articlerecived: Article? = null
    private var curarticle: Article? = null
    var matchedIndex = -1
    private val ARTICLE_LIST = "ARTICLE_LIST"
    private lateinit var articleAdapter: ArticleAdapter
    private var prefConfig: PrefConfig? = null
    private var articleID: String? = null
    private var type: String? = null

    private var isLastPage = false
    private var mNextPage: String? = null
    private var page: String? = ""
    private var mContextId: String? = ""
    private var newsPresenter: NewsPresenter? = null
    private var likePresenter: LikePresenter? = null
    private var shareBottomSheetPresenter: ShareBottomSheetPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.checkAppModeColor(this, false)
        setContentView(R.layout.activity_article_detail)

        prefConfig = PrefConfig(this)

        bindView()
        init()
    }


    fun bindView() {
        viewPager = findViewById(R.id.viewPager)
        ivBack = findViewById(R.id.ivBack)
        dotImg = findViewById(R.id.dot_img)
    }

    fun init() {
        newsPresenter = NewsPresenter(this, this)
        likePresenter = LikePresenter(this)
        shareBottomSheetPresenter = ShareBottomSheetPresenter(this)

        articlelist = intent.getParcelableArrayListExtra<Article>("myArrayList")//list of articles received
        articleID = intent.getStringExtra("articleID")
        type = intent.getStringExtra("type")
        mNextPage = intent.getStringExtra("NextPageApi")
        mContextId = intent.getStringExtra("mContextId")
        isLastPage = intent.getBooleanExtra("isLastPage",false)
//        if (mNextPage?.isNotEmpty() == true) {
//            page = mNextPage
//        }

        ivBack?.setOnClickListener { onBackPressed() }

        dotImg!!.setOnClickListener(View.OnClickListener {
            if (!InternetCheckHelper.isConnected()) {
                Toast.makeText(
                    this@ArticleDetailNew,
                    getString(R.string.internet_error),
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (curarticle != null && shareBottomSheetPresenter != null) {
                loaderShow(true)
                shareBottomSheetPresenter!!.share_msg(curarticle!!.getId(), object : ShareInfoInterface {
                    override fun response(shareInfo: ShareInfo) {
                        loaderShow(false)
                        showBottomSheetDialog(shareInfo, curarticle!!,
                            DialogInterface.OnDismissListener { })
                    }

                    override fun error(error: String) {
                        loaderShow(false)
                    }
                })
            }
        })


        if (intent.hasExtra("article")) {
            articlerecived =
                Gson().fromJson<Article>(
                    intent.getStringExtra("article"),
                    Article::class.java
                ) //article clicked
        }

        for (i in 0 until articlelist?.size!!) {
            if (articlelist!![i].id == articleID) { //article id matches from list
                matchedIndex = i
                break
            }
        }
        if (matchedIndex != -1) {
            val subList = articlelist?.subList(
                matchedIndex,
                articlelist?.size ?: 0
            ) // create sub-list from the matched index to end
            articlelist = ArrayList(subList)
        }

        //this part can be done on function
        articlelist = filterArray(articlelist!!)

        articleAdapter = prefConfig?.let {
            ArticleAdapter(
                this,
                it,
                this,
                likePresenter,
                shareBottomSheetPresenter
            )
        }!!

        articleAdapter?.setArticleList(articlelist!!)


        viewPager.adapter = articleAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
//        viewPager.setPageTransformer(VerticalPageTransformer())


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentPage = viewPager.currentItem
                curarticle = articlelist!![position]

                if (articlelist != null && articlelist!!.isNotEmpty() && newsPresenter != null && !isLastPage) {
                    if (InternetCheckHelper.isConnected()) {
                        if (articlelist!!.size - position < 10 ) {
                            newsPresenter!!.updatedArticles(
                                mContextId,
                                prefConfig!!.isReaderMode,
                                mNextPage,
                                true
                            )
                            page = mNextPage
                        }
                    }
                }

            }
        })

    }
    var onGotoChannelListener: OnGotoChannelListener = object : OnGotoChannelListener {
        override fun onItemClicked(type: TYPE, id: String, name: String, favorite: Boolean) {
            Constants.articleId = ""
            Constants.speech = ""
            Constants.url = ""
            goHome?.sendAudioEvent("stop_destroy")
            if (type != null && type == TYPE.MANAGE) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true
                var intent: Intent? = null
                intent = if (type == TYPE.SOURCE) {
                    Intent(this@ArticleDetailNew, ChannelDetailsActivity::class.java)
                } else {
                    Intent(this@ArticleDetailNew, ChannelPostActivity::class.java)
                }
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                intent.putExtra("favorite", favorite)
                startActivity(intent)
                // finish();
            }
        }

        override fun onItemClicked(
            type: TYPE,
            id: String,
            name: String,
            favorite: Boolean,
            article: Article,
            position: Int
        ) {
        }

        override fun onArticleSelected(article: Article) {}
    }
    private fun showBottomSheetDialog(
        shareInfo: ShareInfo,
        article: Article,
        onDismissListener: DialogInterface.OnDismissListener
    ) {
        var artyp = "_DETAILS"
        if (shareInfo.isArticle_archived) {
            artyp = ""
        }
        Log.e("utt", "showBottomSheetDialog:typ "+type )
        val shareBottomSheet = ShareBottomSheet(this, object : ShareToMainInterface {
            override fun removeItem(id: String, position: Int) {}
            override fun onItemClicked(type: TYPE, id: String, name: String, favorite: Boolean) {
                onGotoChannelListener.onItemClicked(type, id, name, favorite)
            }

            override fun unarchived() {}
        }, true, type + artyp)
        shareBottomSheet.show(article, onDismissListener, shareInfo)
    }
    private fun filterArray(filterarticles: ArrayList<Article>): ArrayList<Article> {
        val itemsToRemove = ArrayList<Article>()
        for (item in filterarticles!!) {
            if (item.type.equals("REELS") || item.type.equals("LINE")) {
                itemsToRemove.add(item)
            }
        }
        filterarticles.removeAll(itemsToRemove.toSet())

        return filterarticles
    }

    override fun loaderShow(flag: Boolean) {
    }

    override fun error(error: String?) {
    }

    override fun error404(error: String?) {
    }

    override fun success(response: ArticleResponse?, offlineData: Boolean) {
    }

    override fun successArticle(article: Article?) {
    }

    override fun homeSuccess(homeResponse: HomeResponse?, currentPage: String?) {

        if (homeResponse != null) {
            if (homeResponse.sections != null && homeResponse.sections.size > 0) {
                val sections = homeResponse.sections
                try {
                    for (section in sections) {
                        if (section != null) {
                            if (!TextUtils.isEmpty(section.type)) {
                                when (section.type) {
                                    ARTICLE_LIST -> if (section.data.articles != null) {
                                        //preloading large images articles
                                        if (section.data.articles[0].bullets != null && section.data.articles[0].bullets.size > 0) {
                                            for (bullet in section.data.articles[0].bullets) {
                                                Glide.with(this)
                                                    .load(bullet.image)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .preload()
                                            }
                                        }
                                        //pre loading small images article

                                        if (!::articleAdapter.isInitialized) {
                                            likePresenter = LikePresenter(this)
                                            shareBottomSheetPresenter =
                                                ShareBottomSheetPresenter(this)
                                            articleAdapter = prefConfig?.let {
                                                ArticleAdapter(
                                                    this,
                                                    it,
                                                    this,
                                                    likePresenter,
                                                    shareBottomSheetPresenter
                                                )
                                            }!!
//                                                articleAdapter.setArticleList(articlelist!!)
                                            viewPager.adapter = articleAdapter
                                            viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
                                        }


                                        if (section.data.articles != null && section.data.articles.size > 0 && articleAdapter != null) {
                                            articlelist?.addAll(filterArray(section.data.articles)) //addinf the response to arrylist
                                            articleAdapter.notifyDataSetChanged()
                                            for (article in section.data.articles) {
                                                Glide.with(this)
                                                    .load(article.image)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .preload()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
//                        rlProgress.setVisibility(View.GONE)
//                        showNoDataErrorView(true)
                    //                        no_record_found.setVisibility(View.VISIBLE);
                    return
                }

            }


            if (homeResponse.getMeta() != null) {
                mNextPage =homeResponse.getMeta().next
                if (TextUtils.isEmpty(mNextPage)) {
                    isLastPage = true
                    //                videoItems.add(null);
                }
            }
        }
    }


    override fun nextPosition(position: Int) {

    }

    override fun nextPositionNoScroll(position: Int, shouldNotify: Boolean) {

    }

    override fun onItemHeightMeasured(height: Int) {

    }

    override fun share(article: Article) {

        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(
                this,
                getString(R.string.internet_error),
                Toast.LENGTH_SHORT
            ).show()

            return
        }
        if (article == null) return
//            loaderShow(true)
        shareBottomSheetPresenter?.share_msg(article.id, object : ShareInfoInterface {
            override fun response(shareInfo: ShareInfo) {
                //                        loaderShow(false)
                if (shareInfo == null) {
                    return
                }
//                    logEvent(
//                        this,
//                        Events.REEL_SHARE_CLICK
//                    )
                val article = Article()
                if (article != null) {
                    val mediaMeta = article.mediaMeta
                    article.mediaMeta = mediaMeta
                }
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                if (shareInfo != null) {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.share_message)
                }
                sendIntent.type = "text/plain"
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

            override fun error(error: String) {}
        })

    }

    override fun viewFullArticle(article: Article) {
        if (article != null && !TextUtils.isEmpty(article.originalLink)) {
            val customTabsPackages = BulletDetailFragment.getCustomTabsPackages(this)
            var link = ""
            //                if (article.getType().toLowerCase().contains("youtube")) {
//                    link = "https://www.youtube.com/watch?v=" + article.getOriginalLink();
//                } else {
            link = article.originalLink
            //                }
            if (BulletDetailFragment.isCustomTabSupported(this, Uri.parse(link))
                && customTabsPackages.size > 0
            ) {
                try {
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder
                        .setShowTitle(true)
                        .build()
                    if (customTabsIntent != null) {
                        Log.d("TAGlink", "link = $link")
                        Log.d("TAGlink", "Type = " + article.type)
                        customTabsIntent.launchUrl(this, Uri.parse(link))
                    }
                } catch (exception: ActivityNotFoundException) {
                    val intent = Intent(this, WebViewActivity::class.java)
                    if (article.source != null && !TextUtils.isEmpty(article.source.name)) intent.putExtra(
                        "title",
                        article.source.name
                    )
                    intent.putExtra("url", link)
                    this.startActivity(intent)
                }
            } else {
                val intent = Intent(this, WebViewActivity::class.java)
                if (article.source != null && !TextUtils.isEmpty(article.source.name)) intent.putExtra(
                    "title",
                    article.source.name
                )
                intent.putExtra("url", link)
                this.startActivity(intent)
            }
        }
    }

    override fun commentsPage(article: Article) {
        val intent = Intent(this, CommentsActivity::class.java)
        intent.putExtra("article_id", article.id)
        startActivityForResult(intent, Constants.CommentsRequestCode)
    }

}
