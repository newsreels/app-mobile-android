package com.ziro.bullet.fragments.searchNew.locationnew

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.PorterDuff
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.ziro.bullet.R
import com.ziro.bullet.activities.*
import com.ziro.bullet.adapters.feed.LargeCardViewHolder
import com.ziro.bullet.adapters.feed.SmallCardViewHolder
import com.ziro.bullet.adapters.feed.VideoViewHolder
import com.ziro.bullet.adapters.feed.YoutubeViewHolder
import com.ziro.bullet.analytics.AnalyticsEvents.logEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.bottomSheet.ShareBottomSheet
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.data.models.AuthorListResponse
import com.ziro.bullet.data.models.NewFeed.HomeResponse
import com.ziro.bullet.data.models.ShareInfo
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.AudioObject
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.model.articles.ArticleResponse
import com.ziro.bullet.model.articles.Bullet
import com.ziro.bullet.model.articles.MediaMeta
import com.ziro.bullet.presenter.CommunityPresenter
import com.ziro.bullet.presenter.FollowUnfollowPresenter
import com.ziro.bullet.presenter.LikePresenter
import com.ziro.bullet.presenter.ShareBottomSheetPresenter
import com.ziro.bullet.texttospeech.TextToAudioPlayerHelper
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.InternetCheckHelper
import com.ziro.bullet.utills.SpeedyLinearLayoutManager
import com.ziro.bullet.utills.Utils
import de.hdodenhof.circleimageview.CircleImageView
import im.ene.toro.PlayerSelector
import im.ene.toro.widget.Container
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlin.math.abs

class PlacesListFragmentNew3 : Fragment(), CommunityCallback {

    private var REEL_ID = "2a1a0ffd-4261-4a3e-aa23-53c4395064be"
    private var appBarLayout: AppBarLayout? = null
    private var mListRV: Container? = null
    private var mCardAdapter: HomeAdapterPlaces? = null
    private var progress: ProgressBar? = null
    var searchSkeleton: LinearLayout? = null
    var searchquery: String = ""
    var locationId: String = ""
    var locationName: String = ""
    var backimg: ImageView? = null
    private val contentArrayList = ArrayList<Article>()
    private val reels = ArrayList<ReelsItem>()
    private var presenter: CommunityPresenter? = null
    private var likePresenter: LikePresenter? = null
    private var mArticlePosition = 0
    private val swipeListener: TempCategorySwipeListener? = object : TempCategorySwipeListener {
        override fun swipe(enable: Boolean) {}
        override fun muteIcon(isShow: Boolean) {}
        override fun onFavoriteChanged(fav: Boolean) {}
        override fun selectTab(id: String) {}
        override fun selectTabOrDetailsPage(
            id: String,
            name: String,
            type: TYPE,
            footerType: String
        ) {
        }
    }
    private var gotoTop: CardView? = null
    private var viewArticle: CardView? = null
    private var tag: CardView? = null
    private var image: CircleImageView? = null
    private var menu: ConstraintLayout? = null
    private var back: RelativeLayout? = null

    //    private var back2: RelativeLayout? = null
    private var setting2: RelativeLayout? = null
    private lateinit var tv_search2: TextView
    private var setting: RelativeLayout? = null
    private var favIcon: ImageView? = null
    private var comment: ImageView? = null
    private var share: LinearLayout? = null
    private var follow: ConstraintLayout? = null
    private var follow_txt: TextView? = null
    private var followBottomBar: View? = null
    private var follow_progress: ProgressBar? = null
    private var favCount: TextView? = null
    private var commentCount: TextView? = null
    private var name: TextView? = null
    private var name2: TextView? = null
    private var desc: TextView? = null
    private var username: TextView? = null
    private var channel_btn: CardView? = null
    private var ll_no_results: LinearLayout? = null

    //    private var noData: TextView? = null
    private var image_bg: ImageView? = null
    private var cover: ImageView? = null

    //    private RelativeLayout ivBack;
    private var audioCallback: AudioCallback? = null
    private val selector = PlayerSelector.DEFAULT
    private val _myTag: String? = null
    private var textToAudio: TextToAudioPlayerHelper? = null
    private val goHome: GoHome? = object : GoHome {
        override fun home() {}
        override fun sendAudioToTempHome(
            audioCallback1: AudioCallback,
            fragTag: String?,
            status: String,
            audio: AudioObject
        ) {
            audioCallback = audioCallback1
            if (audio != null) {
                Log.e("sendAudioToTemp", "=================HOME===================")
                Log.e("sendAudioToTemp", "fragTag : $fragTag")
                Log.e("sendAudioToTemp", "speech : " + audio.text)
                Log.e("sendAudioToTemp", "speech : " + audio.id)
                Log.e("sendAudioToTemp", "bullet_position : " + audio.index)
                if (Constants.canAudioPlay) {
                    if (!Constants.muted) {
                        if (textToAudio != null) {
                            textToAudio!!.stop()
                            //                    textToAudio.play(articleId, bullet_position, speech);
                            textToAudio!!.isPlaying(audio, audioCallback)
                        }
                    }
                }
            }
        }

        override fun scrollUp() {}
        override fun scrollDown() {}
        override fun sendAudioEvent(event: String) {
            Log.e("ACTION-", "ACTION : $event")
            if (textToAudio != null && !TextUtils.isEmpty(event)) {
                when (event) {
                    "pause" -> {
                        Log.d("audiotest", "sendAudioEvent: pause")
                        textToAudio!!.pause()
                    }
                    "resume" -> {
                        Log.d("audiotest", "sendAudioEvent: resume")
                        if (Constants.canAudioPlay) {
                            if (!Constants.muted) {
                                textToAudio!!.resume()
                            }
                        }
                    }
                    "stop_destroy" -> {
                        Log.d("audiotest", "sendAudioEvent: stop_destroy")
                        textToAudio!!.stop()
                        textToAudio!!.destroy()
                    }
                    "stop" -> {
                        Log.d("audiotest", "sendAudioEvent: stop")
                        textToAudio!!.stop()
                    }
                    "destroy" -> {
                        Log.d("audiotest", "sendAudioEvent: destroy")
                        textToAudio!!.destroy()
                    }
                    "mute" -> {
                        Log.d("audiotest", "sendAudioEvent: mute")
                        textToAudio!!.mute()
                        textToAudio!!.stop()
                        textToAudio!!.destroy()
                    }
                    "unmute" -> textToAudio!!.unmute()
                    "isSpeaking" -> {
                        Log.d("audiotest", "sendAudioEvent: isSpeaking")
                        if (!textToAudio!!.isSpeaking) {
                            if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(
                                    Constants.speech
                                )
                            ) {
                                sendAudioToTempHome(
                                    audioCallback!!, "isSpeaking", "", AudioObject(
                                        Constants.articleId,
                                        Constants.speech,
                                        Constants.url,
                                        Constants.bullet_position,
                                        Constants.bullet_duration
                                    )
                                )
                            }
                        }
                    }
                    "play" -> {
                        Log.d("audiotest", "sendAudioEvent: play")
                        textToAudio!!.stop()
                        if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(
                                Constants.speech
                            )
                        ) {
                            sendAudioToTempHome(
                                audioCallback!!,
                                "play",
                                "",
                                AudioObject(
                                    Constants.articleId,
                                    Constants.speech,
                                    Constants.url,
                                    Constants.bullet_position,
                                    Constants.bullet_duration
                                )
                            )
                        }
                    }
                    "homeTab" -> {}
                }
            }
        }
    }
    private val shareToMainInterface: ShareToMainInterface = object : ShareToMainInterface {
        override fun removeItem(id: String, position: Int) {}
        override fun onItemClicked(type: TYPE, id: String, name: String, favorite: Boolean) {
            Constants.articleId = ""
            Constants.speech = ""
            Constants.url = ""
            goHome!!.sendAudioEvent("stop_destroy")
            // Utils.hideKeyboard(getContext(), mRoot);
            if (type != null && type == TYPE.MANAGE) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true
                Log.e("expandCard", "newInstance 3")
                var intent: Intent? = null
                intent = if (type == TYPE.SOURCE) {
                    Intent(context, ChannelDetailsActivity::class.java)
                } else {
                    Intent(context, ChannelPostActivity::class.java)
                }
                intent.putExtra("type", type)
                intent.putExtra("id", id)
                intent.putExtra("name", name)
                intent.putExtra("favorite", favorite)
                startActivity(intent)
            }
        }

        override fun unarchived() {}
    }
    var onGotoChannelListener: OnGotoChannelListener = object : OnGotoChannelListener {
        override fun onItemClicked(type: TYPE, id: String, name: String, favorite: Boolean) {
            Log.e("@@@", "ITEM CLICKED")

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = ""
            Constants.speech = ""
            Constants.url = ""
            Log.d("audiotest", " onitemclick : stop_destroy")
            goHome!!.sendAudioEvent("stop_destroy")
            // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);
            if (type != null && type == TYPE.MANAGE) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true
                Log.e("expandCard", "newInstance 3")
                var intent: Intent? = null
                intent = if (type == TYPE.SOURCE) {
                    Intent(context, ChannelDetailsActivity::class.java)
                } else {
                    Intent(context, ChannelPostActivity::class.java)
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
    private var cardLinearLayoutManager: SpeedyLinearLayoutManager? = null
    private var mNextPage = ""
    private var mReelsItem: ReelsItem? = null
    private var prefConfig: PrefConfig? = null
    private var bottomSheet: FrameLayout? = null
    private var behavior: BottomSheetBehavior<*>? = null
    private var followUnfollowPresenter: FollowUnfollowPresenter? = null
    private var shareBottomSheetPresenter: ShareBottomSheetPresenter? = null
    private var shareBottomSheet: ShareBottomSheet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.view_more_reel_places, container, false)

        if (arguments != null && arguments!!.containsKey(KEY_REEL_ITEM)) {
            mReelsItem = arguments!!.getParcelable(KEY_REEL_ITEM)
        }
        if (arguments != null && arguments!!.containsKey(REEL_POSITION)) {
            position = arguments!!.getInt(REEL_POSITION)
        }
        presenter = CommunityPresenter(activity, this)
        likePresenter = LikePresenter(activity)
        shareBottomSheetPresenter = ShareBottomSheetPresenter(activity)
        followUnfollowPresenter = FollowUnfollowPresenter(activity)
        prefConfig = PrefConfig(context)
        initView(view)
        setData()
        setRvScrollListener()
//        setListener()
        logEvent(
            context,
            Events.VIEW_MORE_CLICK
        )
        presenter!!.loadLocationArticle(
            mNextPage,
            locationId,
            prefConfig!!.isReaderMode
        )
        return view
    }

    private fun updateFollowColor(isFavorite: Boolean) {
        if (isFavorite) {
            follow_txt!!.text = activity!!.getString(R.string.following)
            follow_txt!!.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.following_text_color
                )
            )
            follow_txt!!.setCompoundDrawables(null, null, null, null)
            followBottomBar!!.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.following_text_color
                )
            )
        } else {
            follow_txt!!.text = activity!!.getString(R.string.follow)
            follow_txt!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryRed))
            followBottomBar!!.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primaryRed
                )
            )
            follow_txt!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus, 0, 0, 0)
            for (drawable in follow_txt!!.compoundDrawables) {
                if (drawable != null) {
                    DrawableCompat.setTint(
                        drawable,
                        ContextCompat.getColor(requireContext(), R.color.primaryRed)
                    )
                }
            }
        }
    }

    private fun setListener() {
//        ivBack.setOnClickListener(v -> {
//            dismiss();
//        });
        viewArticle!!.setOnClickListener { v: View? ->
            if (context == null) return@setOnClickListener
            logEvent(
                context,
                Events.VIEW_ORIGINAL_ARTICLE_CLICK
            )
            val customTabsPackages =
                getCustomTabsPackages(
                    context
                )
            if (customTabsPackages.size > 0) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder
                    .setShowTitle(true)
                    .build()
                customTabsIntent.launchUrl(
                    context!!,
                    Uri.parse(mReelsItem!!.link)
                )
            } else {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra(
                    "title",
                    context!!.resources.getString(R.string.view_original_article)
                )
                intent.putExtra("url", mReelsItem!!.link)
                context!!.startActivity(intent)
            }
//            dismiss()
        }
        gotoTop!!.setOnClickListener { v: View? ->
            gotoTop!!.visibility = View.GONE
            scrollToTop()
            if (bottomSheet != null) BottomSheetBehavior.from(bottomSheet!!).state =
                BottomSheetBehavior.STATE_COLLAPSED
        }
//        back!!.setOnClickListener { view: View? -> dismiss() }
//        back2!!.setOnClickListener { view: View? -> dismiss() }
        share!!.setOnClickListener { view: View? -> if (listener != null) listener!!.onShareClicked() }
        follow!!.setOnClickListener(View.OnClickListener {
            if (!InternetCheckHelper.isConnected()) {
                if (context != null) {
                    Toast.makeText(
                        context,
                        context!!.getString(R.string.internet_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@OnClickListener
            }
            follow_txt!!.visibility = View.INVISIBLE
            follow_progress!!.visibility = View.VISIBLE
            follow!!.isEnabled = false
            if (mReelsItem!!.source != null) {
                if (!mReelsItem!!.source.isFavorite) {
                    followUnfollowPresenter!!.followSource(
                        mReelsItem!!.source.id, 0
                    ) { position, flag ->
                        follow!!.isEnabled = true
                        if (flag) {
                            mReelsItem!!.source.isFavorite = true
                            if (listener != null) listener!!.onFollow(
                                mReelsItem
                            )
                            follow_txt!!.visibility = View.VISIBLE
                            follow_progress!!.visibility = View.INVISIBLE
                            updateFollowColor(mReelsItem!!.source.isFavorite)
                            //                                    follow_txt.setText(getString(R.string.unfollow));
                            //                                    follow.setCardBackgroundColor(getResources().getColor(R.color.edittextHint));
                        }
                    }
                } else {
                    followUnfollowPresenter!!.unFollowSource(
                        mReelsItem!!.source.id, 0
                    ) { position, flag ->
                        follow!!.isEnabled = true
                        if (flag) {
                            mReelsItem!!.source.isFavorite = false
                            follow_txt!!.visibility = View.VISIBLE
                            if (listener != null) listener!!.onFollow(
                                mReelsItem
                            )
                            follow_progress!!.visibility = View.INVISIBLE
                            updateFollowColor(mReelsItem!!.source.isFavorite)
                            //                                    follow_txt.setText(getString(R.string.follow));
                            //                                    follow.setCardBackgroundColor(getResources().getColor(R.color.theme_color_1));
                        }
                    }
                }
            } else {
                follow!!.isEnabled = true
            }
        })
        setting!!.setOnClickListener { v: View? -> more() }
        setting2!!.setOnClickListener { v: View? -> more() }
    }

    private fun more() {
        if (shareBottomSheetPresenter != null) {
            shareBottomSheetPresenter!!.share_msg(mReelsItem!!.id, object : ShareInfoInterface {
                override fun response(shareInfo: ShareInfo) {
                    if (shareBottomSheet == null) {
                        shareBottomSheet = ShareBottomSheet(activity, null, true, "REEL_INNER")
                    }
                    shareBottomSheet!!.show(
                        getArticleFromReels(mReelsItem),
                        { }, shareInfo
                    )
                }

                override fun error(error: String) {}
            })
        }
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

    private fun setData() {
        if (mReelsItem != null) {
            name2!!.text = mReelsItem!!.sourceNameToDisplay
            name!!.text = mReelsItem!!.sourceNameToDisplay
            showVerifiedIcon(name, mReelsItem!!)
            showVerifiedIcon(name2, mReelsItem!!)
            desc!!.text = mReelsItem!!.description
            username!!.text = ""
            if (!TextUtils.isEmpty(mReelsItem!!.sourceImageToDisplay)) {
                Glide.with(this).load(mReelsItem!!.sourceImageToDisplay)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(image!!)
            }
            if (!TextUtils.isEmpty(mReelsItem!!.image)) {
                Glide.with(this).load(mReelsItem!!.image)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(image_bg!!)
                Picasso.get()
                    .load(mReelsItem!!.image)
                    .transform(BlurTransformation(context, 25, 3))
                    .into(cover)
            }
            if (mReelsItem!!.info != null) {
                commentCount!!.text = "" + mReelsItem!!.info.comment_count
                favCount!!.text = "" + mReelsItem!!.info.like_count
                if (mReelsItem!!.info.isLiked) {
                    favIcon!!.setImageResource(R.drawable.ic_reel_like_active)
                    favCount!!.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.theme_color_1
                        )
                    )
                    favIcon!!.setColorFilter(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.theme_color_1
                        ), PorterDuff.Mode.SRC_IN
                    )
                } else {
                    favIcon!!.setImageResource(R.drawable.ic_reel_like_inactive)
                    favCount!!.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.greyad
                        )
                    )
                    favIcon!!.setColorFilter(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.greyad
                        )
                    )
                }
                comment!!.setOnClickListener { v: View? ->
                    if (!InternetCheckHelper.isConnected()) {
                        if (context != null) {
                            Toast.makeText(
                                context,
                                context!!.getString(R.string.internet_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@setOnClickListener
                    }
                    if (activity != null) {
                        val params: MutableMap<String, String> =
                            HashMap()
                        params[Events.KEYS.REEL_ID] = REEL_ID
                        logEvent(
                            context,
                            params,
                            Events.REELS_COMMENT
                        )
                        val intent = Intent(activity, CommentsActivity::class.java)
                        intent.putExtra("article_id", REEL_ID)
                        intent.putExtra(
                            "position",
                            position
                        )
                        startActivityForResult(
                            intent,
                            Constants.CommentsRequestCode
                        )
                    }
                }
                favIcon!!.setOnClickListener { v: View? ->
                    if (!InternetCheckHelper.isConnected()) {
                        if (context != null) {
                            Toast.makeText(
                                context,
                                context!!.getString(R.string.internet_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@setOnClickListener
                    }
                    favIcon!!.isEnabled = false
                    val flag = mReelsItem!!.info.isLiked
                    if (!flag) {
                        favCount!!.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.theme_color_1
                            )
                        )
                        favIcon!!.setImageResource(R.drawable.ic_reel_like_active)
                        favIcon!!.setColorFilter(
                            ContextCompat.getColor(
                                context!!,
                                R.color.theme_color_1
                            ), PorterDuff.Mode.MULTIPLY
                        )
                        favCount!!.text = "" + (mReelsItem!!.info.like_count + 1)
                    } else {
                        favCount!!.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.greyad
                            )
                        )
                        favIcon!!.setColorFilter(
                            ContextCompat.getColor(
                                context!!,
                                R.color.greyad
                            ), PorterDuff.Mode.MULTIPLY
                        )
                        favIcon!!.setImageResource(R.drawable.ic_reel_like_inactive)
                        favCount!!.text = "" + (mReelsItem!!.info.like_count - 1)
                    }
                    val params: MutableMap<String, String> =
                        HashMap()
                    params[Events.KEYS.REEL_ID] = REEL_ID
                    logEvent(
                        context,
                        params,
                        Events.REELS_LIKE
                    )
                    likePresenter!!.like(mReelsItem!!.id, object : LikeInterface {
                        override fun success(like: Boolean) {
//                            if (context == null) return@setOnClickListener
                            favIcon!!.isEnabled = true
                            mReelsItem!!.info.isLiked = like
                            var counter = mReelsItem!!.info.like_count
                            if (like) {
                                counter++
                            } else {
                                if (counter > 0) {
                                    counter--
                                } else {
                                    counter = 0
                                }
                            }
                            mReelsItem!!.info.like_count = counter
                            favCount!!.text = "" + counter
                            if (mReelsItem!!.info.isLiked) {
                                favCount!!.setTextColor(
                                    ContextCompat.getColor(
                                        context!!,
                                        R.color.like_heart_filled
                                    )
                                )
                                favIcon!!.setImageResource(R.drawable.ic_reel_like_active)
                                favIcon!!.setColorFilter(
                                    ContextCompat.getColor(
                                        context!!,
                                        R.color.theme_color_1
                                    ), PorterDuff.Mode.MULTIPLY
                                )
                            } else {
                                favCount!!.setTextColor(
                                    ContextCompat.getColor(
                                        context!!,
                                        R.color.greyad
                                    )
                                )
                                favIcon!!.setImageResource(R.drawable.ic_reel_like_inactive)
                                favIcon!!.setColorFilter(
                                    ContextCompat.getColor(
                                        context!!,
                                        R.color.greyad
                                    ), PorterDuff.Mode.MULTIPLY
                                )
                            }
                        }

                        override fun failure() {
                            favIcon!!.isEnabled = true
                        }
                    }, !mReelsItem!!.info.isLiked)
                }
            }
            if (mReelsItem!!.source != null) {
                if (mReelsItem!!.source.isFavorite) {
                    follow_txt!!.visibility = View.VISIBLE
                    follow_progress!!.visibility = View.INVISIBLE
                    updateFollowColor(mReelsItem!!.source.isFavorite)
                    //                    follow_txt.setText(getString(R.string.unfollow));
//                    follow.setCardBackgroundColor(getResources().getColor(R.color.edittextHint));
                } else {
                    follow_txt!!.visibility = View.VISIBLE
                    follow_progress!!.visibility = View.INVISIBLE
                    updateFollowColor(mReelsItem!!.source.isFavorite)
                    //                    follow_txt.setText(getString(R.string.follow));
//                    follow.setCardBackgroundColor(getResources().getColor(R.color.theme_color_1));
                }
            }
        }

        //SET DATA
        textToAudio = TextToAudioPlayerHelper(context)
        mCardAdapter = HomeAdapterPlaces(
            false,
            object : CommentClick {
                override fun onDetailClick(position: Int, article: Article) {
                    val intent = Intent(context, BulletDetailActivity::class.java)
                    intent.putExtra("article", Gson().toJson(article))
                    intent.putExtra("type", "")
                    intent.putExtra("position", position)
                    startActivityForResult(intent, Constants.CommentsRequestCode)
                }

                override fun onNewDetailClick(
                    position: Int,
                    article: Article?,
                    articlelist: MutableList<Article>?
                ) {
                    TODO("Not yet implemented")
                }

                override fun fullscreen(
                    position: Int, article: Article, duration: Long, mode: String,
                    isManual: Boolean
                ) {
                    val intent = Intent(context, VideoFullScreenActivity::class.java)
                    intent.putExtra("url", article.link)
                    intent.putExtra("mode", mode)
                    intent.putExtra("position", position)
                    intent.putExtra("duration", duration)
                    startActivityForResult(intent, Constants.VideoDurationRequestCode)
                }

                override fun commentClick(position: Int, id: String) {
                    val intent = Intent(context, CommentsActivity::class.java)
                    intent.putExtra("article_id", id)
                    intent.putExtra("position", position)
                    startActivityForResult(intent, Constants.CommentsRequestCode)
                }
            },
            false,
            activity as AppCompatActivity?,
            contentArrayList,
            "",
            true,
            object : DetailsActivityInterface {
                override fun playAudio(
                    audioCallback: AudioCallback,
                    fragTag: String?,
                    audio: AudioObject
                ) {
                    goHome?.sendAudioToTempHome(audioCallback, fragTag, "", audio)
                }

                override fun pause() {
                    Pause()
                }

                override fun resume() {
                    resumeCurrentBullet()
                }
            },
            goHome,
            shareToMainInterface,
            swipeListener,
            this,
            onGotoChannelListener,
            { },
            { },
            lifecycle
        ) { option, article -> }
        cardLinearLayoutManager = SpeedyLinearLayoutManager(context)
        mListRV!!.layoutManager = cardLinearLayoutManager
        mListRV!!.onFlingListener = null
        mListRV!!.adapter = mCardAdapter
        mListRV!!.cacheManager = mCardAdapter
        mListRV!!.playerSelector = selector

//        if (bottomSheet != null) {
//            BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                @Override
//                public void onStateChanged(@NonNull View bottomSheet, int newState) {
//
//                }
//
//                @Override
//                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                    int bottomSheetVisibleHeight = bottomSheet.getHeight();
//                    pinned_bottom.setTranslationY((bottomSheetVisibleHeight - pinned_bottom.getHeight()));
//                }
//            });
//        }
    }

    private fun showVerifiedIcon(view: TextView?, reelsItem: ReelsItem) {
        if (reelsItem.isVerified) {
            if (Utils.isRTL()) {
                view!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0)
            } else {
                view!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0)
            }
        }
    }

    fun resumeCurrentBullet() {
        try {
            mListRV!!.playerSelector = selector
            val holder = mListRV!!.findViewHolderForAdapterPosition(mArticlePosition)
            if (holder != null) {
                if (holder is LargeCardViewHolder) {
                    holder.storiesProgressView.resume()
                } else if (holder is SmallCardViewHolder) {
                    holder.storiesProgressView.resume()
                } else if (holder is YoutubeViewHolder) {
                    holder.youtubeResume()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * / **
     * Pause current bullets, youtube videos
     * Normal videos are by setting selector NONE, so dont forget to set DEFAULT selector on resume otherwise videos wont play even if resumed
     */
    fun Pause() {
        Log.d("youtubePlayer", "Pause = $mArticlePosition")
        try {
            val holder = mListRV!!.findViewHolderForAdapterPosition(mArticlePosition)
            Log.d("youtubePlayer", "Pauseholder = $holder")
            if (holder != null) {
                if (holder is LargeCardViewHolder) {
                    holder.storiesProgressView.pause()
                } else if (holder is SmallCardViewHolder) {
                    holder.storiesProgressView.pause()
                } else if (holder is VideoViewHolder) {

//                    ((VideoViewHolder) holder).pause();
                } else if (holder is YoutubeViewHolder) {
                    Log.d("youtubePlayer", "Pause utube")
                    holder.bulletPause()
                }
            }
            val playerSelector = PlayerSelector.NONE
            mListRV!!.playerSelector = playerSelector
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView(view: View) {
//        back2 = view.findViewById(R.id.back2)
        setting2 = view.findViewById(R.id.setting2)
        tv_search2 = view.findViewById(R.id.tv_search2)
        setting = view.findViewById(R.id.setting)
        tag = view.findViewById(R.id.tag)
        back = view.findViewById(R.id.back)
        menu = view.findViewById(R.id.menu)
        favCount = view.findViewById(R.id.favCount)
        follow_txt = view.findViewById(R.id.follow_txt)
        followBottomBar = view.findViewById(R.id.follow_bottom_bar)
        follow_progress = view.findViewById(R.id.follow_progress)
        follow = view.findViewById(R.id.cl_follow)
        favIcon = view.findViewById(R.id.favIcon)
        commentCount = view.findViewById(R.id.commentCount)
        share = view.findViewById(R.id.share)
        comment = view.findViewById(R.id.comment)
        image_bg = view.findViewById(R.id.image_bg)
        cover = view.findViewById(R.id.cover)
        image = view.findViewById(R.id.image)
        name = view.findViewById(R.id.name)
        name2 = view.findViewById(R.id.name2)
        desc = view.findViewById(R.id.desc)
        username = view.findViewById(R.id.username)
        channel_btn = view.findViewById(R.id.channel_btn)
        viewArticle = view.findViewById(R.id.viewArticle)
        ll_no_results = view.findViewById(R.id.ll_no_results)
//        noData = view.findViewById(R.id.noData)
        mListRV = view.findViewById(R.id.viewMoreList)
        gotoTop = view.findViewById(R.id.gotoTop)
        appBarLayout = view.findViewById(R.id.appBarLayout)
        progress = view.findViewById(R.id.progress)
        searchSkeleton = view.findViewById(R.id.ll_skeleton)
        backimg = view.findViewById(R.id.back_img)

        val bundle1 = arguments
        if (bundle1!!.getString("query") != null) {
            searchquery = bundle1.getString("query").toString()
            locationId = bundle1.getString("locationId").toString()
            locationName = bundle1.getString("locationName").toString()
        }
        if(!locationName.isNullOrEmpty()){
            tv_search2.text = locationName
        }

        backimg!!.setOnClickListener { activity!!.onBackPressed() }
        appBarLayout?.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                //Collapsed
                back?.visibility = View.VISIBLE
//                back2?.visibility = View.GONE
                setting2?.visibility = View.VISIBLE
                setting?.visibility = View.INVISIBLE
                tag?.visibility = View.INVISIBLE
                name2?.visibility = View.VISIBLE
                menu?.visibility = View.GONE
            } else {
                //Expanded
                back?.visibility = View.GONE
                name2?.visibility = View.GONE
                menu?.visibility = View.VISIBLE
//                back2?.visibility = View.VISIBLE
                setting2?.visibility = View.GONE
                setting?.visibility = View.VISIBLE
                tag?.visibility = View.INVISIBLE
            }
        })
//        val time = Utils.getTimeAgo(
//            Utils.getDate(
//                mReelsItem!!.publishTime
//            ), context
//        )
//        if (!TextUtils.isEmpty(mReelsItem!!.link)) {
//            viewArticle?.visibility = View.VISIBLE
//        } else {
//            viewArticle?.visibility = View.INVISIBLE
//        }
//        views_text.setText(mReelsItem.getInfo().getViewCount() + " " + getString(R.string.views));
        val tvTime = view.findViewById<TextView>(R.id.time)
//        if (!TextUtils.isEmpty(time)) {
//            tvTime.text = time
//        }
    }

    override fun authors(response: AuthorListResponse) {}
    override fun reels(response: ReelResponse) {}
    override fun loaderShow(flag: Boolean) {
        if (flag) {
            searchSkeleton!!.visibility = View.VISIBLE
        } else {
            searchSkeleton!!.visibility = View.GONE
        }
    }

    override fun error(error: String) {
        mListRV?.visibility = View.GONE
        searchSkeleton?.visibility = View.GONE
        ll_no_results?.visibility = View.VISIBLE
    }
    override fun error404(error: String) {
        mListRV?.visibility = View.GONE
        searchSkeleton?.visibility = View.GONE
        ll_no_results?.visibility = View.VISIBLE
    }
    override fun success(response: ArticleResponse, offlineData: Boolean) {
        if (activity == null) return
        if (response != null && response.articles != null && response.articles.size > 0) {
            ll_no_results!!.visibility = View.GONE
            searchSkeleton?.visibility = View.GONE
            mListRV?.visibility = View.VISIBLE

            if (TextUtils.isEmpty(mNextPage)) {
                if (response.reels != null && response.reels.size > 0) {
                    val article2 = Article()
                    article2.title = activity!!.getString(R.string.reels)
                    article2.tabTitle = activity!!.getString(R.string.reels)
                    article2.type = "TITLE"
                    contentArrayList.add(article2)
                    reels.clear()
                    reels.addAll(response.reels)
                    val adArticle2 = Article()
                    //                    adArticle2.setTitle(getActivity().getString(R.string.suggested_reels));
                    adArticle2.type = "REELS"
                    adArticle2.reels = reels
                    contentArrayList.add(adArticle2)
                }
                val article1 = Article()
                article1.title = activity!!.getString(R.string.stories)
                article1.tabTitle = activity!!.getString(R.string.stories)
                article1.type = "TITLE"
                contentArrayList.add(article1)
            }
            if (response.getMeta() != null) {
                mNextPage = response.getMeta().next
            }
            for (position in response.articles.indices) {
                val article = response.articles[position]
                if (prefConfig!!.ads != null && prefConfig!!.ads.isEnabled) {
                    var interval = 10
                    if (prefConfig!!.ads.interval != 0) {
                        interval = prefConfig!!.ads.interval
                    }
                    if (contentArrayList.size != 0 && contentArrayList.size % interval == 0) {
                        Log.e("ADS", "AD Added")
                        val adArticle1 = Article()
                        if (!TextUtils.isEmpty(prefConfig!!.ads.type) && prefConfig!!.ads.type.equals(
                                "facebook",
                                ignoreCase = true
                            )
                        ) {
                            adArticle1.type = "FB_Ad"
                        } else {
                            adArticle1.type = "G_Ad"
                        }
                        contentArrayList.add(adArticle1)
                    }
                }
                contentArrayList.add(article)
            }
            if (mCardAdapter != null) mCardAdapter!!.notifyDataSetChanged()
            selectCardPosition(mArticlePosition)
            swipeListener?.muteIcon(true)
        } else {
            if (response.articles.size <= 0) {
                searchSkeleton?.visibility = View.GONE
                mListRV?.visibility = View.GONE
                ll_no_results?.visibility = View.VISIBLE
            } else {
                ll_no_results?.visibility = View.GONE
            }
//            if (TextUtils.isEmpty(mNextPage)) noData!!.visibility = View.VISIBLE
            if (contentArrayList.size == 0) {
                swipeListener?.muteIcon(false)
                contentArrayList.clear()
                if (mCardAdapter != null) mCardAdapter!!.notifyDataSetChanged()
            }
        }
    }

    fun scrollToTop() {
        if (mListRV != null) //            mListRV.scrollToPosition(0);
            if (cardLinearLayoutManager != null) cardLinearLayoutManager!!.scrollToPositionWithOffset(
                0,
                0
            )
    }

    private fun callNextPage() {
        if (presenter != null) presenter!!.loadLocationArticle(
            mNextPage,
            locationId,
            prefConfig!!.isReaderMode
        )
    }

    fun selectCardPosition(position: Int) {
        if (position > -1) {
            mArticlePosition = position
            if (mCardAdapter != null) mCardAdapter!!.setCurrentArticlePosition(mArticlePosition)
            if (contentArrayList.size > 0 && position < contentArrayList.size) {
                for (i in contentArrayList.indices) {
                    contentArrayList[i].isSelected = false
                }
                contentArrayList[position].isSelected = true
            }
        }
    }

    fun pauseOnlyBullets() {
        try {
            val holder = mListRV!!.findViewHolderForAdapterPosition(mArticlePosition)
            if (holder != null) {
                if (holder is LargeCardViewHolder) {
                    holder.storiesProgressView.pause()
                } else if (holder is SmallCardViewHolder) {
                    holder.storiesProgressView.pause()
                } else if (holder is VideoViewHolder) {

//                    ((VideoViewHolder) holder).pause();
                } else if (holder is YoutubeViewHolder) {
                    holder.bulletPause()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setRvScrollListener() {
        mListRV!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = mListRV!!.layoutManager as LinearLayoutManager?
                if (layoutManager != null && behavior != null) {
                    val firstPosition = layoutManager.findFirstVisibleItemPosition()
                    if (firstPosition > 2) {
                        if (behavior!!.state != BottomSheetBehavior.STATE_EXPANDED) {
                            if (bottomSheet != null) BottomSheetBehavior.from(
                                bottomSheet!!
                            ).state = BottomSheetBehavior.STATE_EXPANDED
                        }
                        gotoTop!!.visibility = View.VISIBLE
                    } else {
                        gotoTop!!.visibility = View.GONE
                    }
                }

                //pause bullet and audio while scrolling
                if (newState == ViewPager2.SCROLL_STATE_DRAGGING) {
                    goHome?.sendAudioEvent("pause")
                    pauseOnlyBullets()
                }
                if (newState == ViewPager2.SCROLL_STATE_IDLE) {
                    logEvent(
                        context,
                        Events.ARTICLE_SWIPE
                    )
                    Constants.auto_scroll = true
                    val firstPosition = layoutManager!!.findFirstVisibleItemPosition()
                    if (firstPosition != -1) {
                        val rvRect = Rect()
                        mListRV!!.getGlobalVisibleRect(rvRect)
                        val rowRect = Rect()
                        layoutManager.findViewByPosition(firstPosition)!!
                            .getGlobalVisibleRect(rowRect)
                        var percentFirst: Int
                        percentFirst = if (rowRect.bottom >= rvRect.bottom) {
                            val visibleHeightFirst = rvRect.bottom - rowRect.top
                            visibleHeightFirst * 100 / layoutManager.findViewByPosition(
                                firstPosition
                            )!!
                                .height
                        } else {
                            val visibleHeightFirst = rowRect.bottom - rvRect.top
                            visibleHeightFirst * 100 / layoutManager.findViewByPosition(
                                firstPosition
                            )!!
                                .height
                        }
                        if (percentFirst > 100) percentFirst = 100
                        val VISIBILITY_PERCENTAGE = 90
                        val copyOfmArticlePosition = mArticlePosition
                        Log.d(
                            "slections",
                            "onScrollStateChanged: percentFirst = $percentFirst"
                        )

                        /* based on percentage of item visibility, select current or next article
                         *  if prev position is same as new pos then dont reset the article
                         * */if (percentFirst >= VISIBILITY_PERCENTAGE) {
                            Log.d("slections", "onScrollStateChanged: percentage greater")
                            mArticlePosition = firstPosition
                            if (mArticlePosition == 0) {
//                                mArticlePosition++;
//                                Log.d("slections", "onScrollStateChanged: mArticlePosition = 0");
//                                selectCardPosition(mArticlePosition);
//
//                                if (mCardAdapter != null)
//                                    mCardAdapter.notifyDataSetChanged();
////                                }
                            } else if (mArticlePosition == contentArrayList.size - 1) {
                                Log.d("slections", "onScrollStateChanged: last = 0")

                                //on fast scrolling select the last one in the last
                                selectCardPosition(mArticlePosition)
                                if (mCardAdapter != null) mCardAdapter!!.notifyDataSetChanged()
                            } else if (copyOfmArticlePosition == mArticlePosition) {
                                Log.d("slections", "onScrollStateChanged: copy = new pos")
                                //scroll rested on same article so resume audio and bullet
                                try {
                                    val holder =
                                        mListRV!!.findViewHolderForAdapterPosition(mArticlePosition)
                                    if (holder != null) {
                                        if (holder is LargeCardViewHolder) {
                                            goHome?.sendAudioEvent("resume")
                                            holder.storiesProgressView.resume()
                                        } else if (holder is SmallCardViewHolder) {
                                            goHome?.sendAudioEvent("resume")
                                            holder.storiesProgressView.resume()
                                        } else if (holder is YoutubeViewHolder) {
                                            holder.youtubeResume()
                                        } else {
                                            goHome?.sendAudioEvent("stop_destroy")
                                        }
                                    } else {
                                        Log.d("audiotest", "scroll : stop_destroy")
                                        goHome?.sendAudioEvent("stop_destroy")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                if (copyOfmArticlePosition != mArticlePosition) {
                                    Log.d("slections", "onScrollStateChanged: select new")
                                    //scrolled to a new pos, so select new article
                                    selectCardPosition(mArticlePosition)
                                    if (mCardAdapter != null) mCardAdapter!!.notifyDataSetChanged()
                                }
                            }
                        } else {
                            Log.d("slections", "onScrollStateChanged: percentage less")
                            mArticlePosition = firstPosition
                            mArticlePosition++
                            if (copyOfmArticlePosition != mArticlePosition) {
                                Log.d("slections", "onScrollStateChanged: select new")
                                //scrolled to a new pos, so select new article
                                selectCardPosition(mArticlePosition)
                                if (mCardAdapter != null) mCardAdapter!!.notifyDataSetChanged()
                            } else {
                                Log.d("slections", "onScrollStateChanged: else")
                                //scroll rested on same article so resume audio and bullet
                                try {
                                    val holder =
                                        mListRV!!.findViewHolderForAdapterPosition(mArticlePosition)
                                    if (holder != null) {
                                        if (holder is LargeCardViewHolder) {
                                            goHome?.sendAudioEvent("resume")
                                            holder.storiesProgressView.resume()
                                        } else if (holder is SmallCardViewHolder) {
                                            goHome?.sendAudioEvent("resume")
                                            holder.storiesProgressView.resume()
                                        } else if (holder is YoutubeViewHolder) {
                                            holder.youtubeResume()
                                        } else {
                                            goHome?.sendAudioEvent("stop_destroy")
                                        }
                                    } else {
                                        Log.d("audiotest", "scroll : stop_destroy")
                                        goHome?.sendAudioEvent("stop_destroy")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (contentArrayList.size - 3 <= cardLinearLayoutManager!!.findLastVisibleItemPosition() && !isLast) {
                    if (!Constants.isApiCalling) {
                        Constants.isApiCalling = true
                        callNextPage()
                    }
                }
            }
        })
    }

    private val isLast: Boolean
        private get() = mNextPage.equals("", ignoreCase = true)

    override fun successArticle(article: Article) {}
    override fun homeSuccess(homeResponse: HomeResponse, currentPage: String) {}
    override fun nextPosition(position: Int) {
        goHome?.sendAudioEvent("pause")
        if (contentArrayList.size > 0 && position < contentArrayList.size && position > -1) {
            mArticlePosition = position
            val type = contentArrayList[mArticlePosition].type
            if (!TextUtils.isEmpty(type) && (type.equals(
                    "suggested_reels",
                    ignoreCase = true
                ) || type.equals("suggested_authors", ignoreCase = true))
            ) {
                mArticlePosition++
            }
            selectCardPosition(mArticlePosition)
            if (cardLinearLayoutManager != null) cardLinearLayoutManager!!.scrollToPositionWithOffset(
                mArticlePosition,
                0
            )
            if (mCardAdapter != null) mCardAdapter!!.notifyDataSetChanged()
        }
    }

    override fun nextPositionNoScroll(position: Int, shouldNotify: Boolean) {
        goHome?.sendAudioEvent("pause")
        val holder = mListRV!!.findViewHolderForAdapterPosition(position)
        val oldPos = mArticlePosition
        if (holder is LargeCardViewHolder) holder.selectUnselectedItem(
            position,
            contentArrayList[position]
        ) else if (holder is SmallCardViewHolder) holder.selectUnselectedItem(
            position,
            contentArrayList[position]
        )
        selectCardPosition(position)
        val holderOld = mListRV!!.findViewHolderForAdapterPosition(oldPos)
        if (holderOld is SmallCardViewHolder) holderOld.unselect(
            contentArrayList[oldPos]
        ) else if (holderOld is LargeCardViewHolder) holderOld.unselect(
            contentArrayList[oldPos]
        ) else if (holderOld is VideoViewHolder) mCardAdapter!!.notifyItemChanged(oldPos) else if (holderOld is YoutubeViewHolder) mCardAdapter!!.notifyItemChanged(
            oldPos
        )
    }

    override fun onItemHeightMeasured(height: Int) {}
    interface OnShareListener {
        fun onShareClicked()
        fun onFollow(mReelsItem: ReelsItem?)
    }

    companion object {
        private const val KEY_REEL_ITEM = "KEY_REEL_ITEM"
        private const val REEL_POSITION = "REEL_POSITION"
        private var position = 0
        private var listener: OnShareListener? = null

        private var placesListFragmentNew3: PlacesListFragmentNew3? = null

        private var goHome: GoHome? = null
        fun getInstancenew(bundle: Bundle, goHome: GoHome?): PlacesListFragmentNew3 {
            if (placesListFragmentNew3 == null)
                placesListFragmentNew3 = PlacesListFragmentNew3()
            placesListFragmentNew3!!.arguments = bundle
            this.goHome = goHome
            return placesListFragmentNew3!!
        }


        fun getInstance(
            reelsItem: ReelsItem?,
            position1: Int,
            listener1: OnShareListener?
        ): PlacesListFragmentNew3 {
            val reelViewMoreSheet = PlacesListFragmentNew3()
            val bundle = Bundle()
            bundle.putParcelable(KEY_REEL_ITEM, reelsItem)
            bundle.putInt(REEL_POSITION, position1)
            reelViewMoreSheet.arguments = bundle
            listener = listener1
            return reelViewMoreSheet
        }

        /**
         * Returns a list of packages that support Custom Tabs.
         */
        fun getCustomTabsPackages(context: Context?): ArrayList<ResolveInfo> {
            val pm = context!!.packageManager
            // Get default VIEW intent handler.
            val activityIntent = Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.fromParts("http", "", null))

            // Get all apps that can handle VIEW intents.
            val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
            val packagesSupportingCustomTabs = ArrayList<ResolveInfo>()
            for (info in resolvedActivityList) {
                val serviceIntent = Intent()
                //            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
                serviceIntent.setPackage(info.activityInfo.packageName)
                // Check if this package also resolves the Custom Tabs service.
                if (pm.resolveService(serviceIntent, 0) != null) {
                    packagesSupportingCustomTabs.add(info)
                }
            }
            return packagesSupportingCustomTabs
        }
    }
}