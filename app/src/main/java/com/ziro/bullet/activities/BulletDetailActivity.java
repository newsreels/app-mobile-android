package com.ziro.bullet.activities;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.utills.Constants.isApiCalling;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.zoomhelper.ZoomHelper;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.feed.DetailViewHolder;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.RelatedFeedAdapter;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.BulletDetailCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.LikeInterface;
import com.ziro.bullet.interfaces.MainInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.MainPresenter;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.texttospeech.TextToAudioPlayerHelper;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import im.ene.toro.PlayerSelector;

public class BulletDetailActivity extends BaseActivity implements NewsCallback, BulletDetailCallback, ShowOptionsLoaderCallback {

    PlayerSelector selector = PlayerSelector.DEFAULT;
    private String _myTag;
    private TextToAudioPlayerHelper textToAudio;
    private AudioCallback audioCallback;

    private Article article;

    private NewsPresenter presenter;
    private MainPresenter mainPresenter;
    private PrefConfig prefConfig;

    private RelativeLayout ivBack;
    private ConstraintLayout header;
    private RecyclerView mListRV;
    private RelatedFeedAdapter mCardAdapter;
    private SpeedyLinearLayoutManager cardLinearLayoutManager;

    private ArrayList<Article> contentArrayList = new ArrayList<>();
    private String nextPage;
    private String articleId;
    private String type;

    private RelativeLayout commentMain;
    private TextView comment_count;
    private ImageView comment_icon;
    private RelativeLayout likeMain;
    private TextView like_count;
    private ImageView like_icon;
    private RelativeLayout dotImg;
    private LikePresenter likePresenter;
    private RelativeLayout progress;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private int position = -1;
    private int mArticlePosition = 0;
    private boolean activityVisible;
    private GoHome goHome = new GoHome() {
        @Override
        public void home() {

        }

        @Override
        public void sendAudioToTempHome(AudioCallback audioCallback1, String fragTag, String status, AudioObject audio) {
            audioCallback = audioCallback1;
            if (audio != null) {
                Log.e("sendAudioToTemp", "=================HOME===================");
                Log.e("sendAudioToTemp", "fragTag : " + fragTag);
                Log.e("sendAudioToTemp", "speech : " + audio.getText());
                Log.e("sendAudioToTemp", "speech : " + audio.getId());
                Log.e("sendAudioToTemp", "bullet_position : " + audio.getIndex());
                if (Constants.canAudioPlay) {
                    if (!Constants.muted) {
                        if (textToAudio != null) {
                            textToAudio.stop();
//                    textToAudio.play(articleId, bullet_position, speech);
                            textToAudio.isPlaying(audio, audioCallback);
                        }
                    }
                }
            }
        }

        @Override
        public void scrollUp() {

        }

        @Override
        public void scrollDown() {

        }

        @Override
        public void sendAudioEvent(String event) {

            Log.e("ACTION-", "ACTION : " + event);
            if (textToAudio != null && !TextUtils.isEmpty(event)) {
                switch (event) {
                    case "pause":
                        Log.d("audiotest", "sendAudioEvent: pause");
                        textToAudio.pause();
                        break;
                    case "resume":
                        Log.d("audiotest", "sendAudioEvent: resume");
                        if (Constants.canAudioPlay) {
                            if (!Constants.muted) {
                                textToAudio.resume();
                            }
                        }
                        break;
                    case "stop_destroy":
                        Log.d("audiotest", "sendAudioEvent: stop_destroy");
                        textToAudio.stop();
                        textToAudio.destroy();
                        break;
                    case "stop":
                        Log.d("audiotest", "sendAudioEvent: stop");
                        textToAudio.stop();
                        break;
                    case "destroy":
                        Log.d("audiotest", "sendAudioEvent: destroy");
                        textToAudio.destroy();
                        break;
                    case "mute":
                        Log.d("audiotest", "sendAudioEvent: mute");
                        textToAudio.mute();
                        textToAudio.stop();
                        textToAudio.destroy();
                        break;
                    case "unmute":
                        textToAudio.unmute();
                        break;
                    case "isSpeaking":
                        Log.d("audiotest", "sendAudioEvent: isSpeaking");
                        if (!textToAudio.isSpeaking()) {
                            if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(Constants.speech)) {
                                sendAudioToTempHome(audioCallback, "isSpeaking", "", new AudioObject(Constants.articleId, Constants.speech, Constants.url, Constants.bullet_position, Constants.bullet_duration));
                            }
                        }
                        break;
                    case "play":
                        Log.d("audiotest", "sendAudioEvent: play");
                        textToAudio.stop();
                        if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(Constants.speech)) {
                            sendAudioToTempHome(audioCallback, "play", "", new AudioObject(Constants.articleId, Constants.speech, Constants.url, Constants.bullet_position, Constants.bullet_duration));
                        }
                        break;
                    case "homeTab":
//                        if (bottomNavigationView != null)
//                            bottomNavigationView.setSelectedItemId(R.id.action_home);
                        break;
                }
            }

        }
    };
    public OnGotoChannelListener onGotoChannelListener = new OnGotoChannelListener() {
        @Override
        public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
            Log.e("@@@", "ITEM CLICKED");

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = "";
            Constants.speech = "";
            Constants.url = "";
            Log.d("audiotest", " onitemclick : stop_destroy");
            goHome.sendAudioEvent("stop_destroy");
            // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);

            if (type != null && type.equals(TYPE.MANAGE)) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true;

                Intent intent = null;
                if (type.equals(TYPE.SOURCE)) {
                    intent = new Intent(BulletDetailActivity.this, ChannelDetailsActivity.class);
                } else {
                    intent = new Intent(BulletDetailActivity.this, ChannelPostActivity.class);
                }
                intent.putExtra("type", type);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("favorite", favorite);
                startActivity(intent);
                // finish();
            }
        }

        @Override
        public void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position) {

        }

        @Override
        public void onArticleSelected(Article article) {

        }
    };
    private ShareToMainInterface shareToMainInterface = new ShareToMainInterface() {

        @Override
        public void removeItem(String id, int position) {

        }

        @Override
        public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
            Log.e("@@@", "ITEM CLICKED");

            //Resetting the audio data to avoid old article speech while loading new data
            Constants.articleId = "";
            Constants.speech = "";
            Constants.url = "";
            Log.d("audiotest", " onitemclick : stop_destroy");
            goHome.sendAudioEvent("stop_destroy");
            // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);

            if (type != null && type.equals(TYPE.MANAGE)) {
                //selectSearch(name);
            } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
                Constants.canAudioPlay = true;

                Intent intent = null;
                if (type.equals(TYPE.SOURCE)) {
                    intent = new Intent(BulletDetailActivity.this, ChannelDetailsActivity.class);
                } else {
                    intent = new Intent(BulletDetailActivity.this, ChannelPostActivity.class);
                }
                intent.putExtra("type", type);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("favorite", favorite);
                startActivity(intent);
            }
        }

        @Override
        public void unarchived() {

        }
    };
    private TempCategorySwipeListener swipeListener = new TempCategorySwipeListener() {
        @Override
        public void swipe(boolean enable) {

        }

        @Override
        public void muteIcon(boolean isShow) {

        }

        @Override
        public void onFavoriteChanged(boolean fav) {

        }

        @Override
        public void selectTab(String id) {

        }

        @Override
        public void selectTabOrDetailsPage(String id, String name, TYPE type, String footerType) {

        }
    };
    private long mLastClickTime = 0;
    private boolean adFailedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_bullet_detail);

        Constants.canAudioPlay = true;
        prefConfig = new PrefConfig(this);
        textToAudio = new TextToAudioPlayerHelper(this);
        likePresenter = new LikePresenter(this);
        shareBottomSheetPresenter = new ShareBottomSheetPresenter(this);

        presenter = new NewsPresenter(this, this);
        mainPresenter = new MainPresenter(this, new MainInterface() {
            @Override
            public void loaderShow(boolean flag) {

            }

            @Override
            public void error(String error, int load) {

            }

            @Override
            public void error404(String error) {

            }

            @Override
            public void success(ArticleResponse response, int load, int category, String topic) {

            }

            @Override
            public void success(CategoryResponse response) {

            }

            @Override
            public void UserTopicSuccess(ArrayList<Topics> response) {

            }

            @Override
            public void UserInfoSuccess(Info info) {

            }

            @Override
            public void UserSourceSuccess(SourceModel response) {

            }
        });

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        if (getIntent().hasExtra("article")) {
            article = new Gson().fromJson(getIntent().getStringExtra("article"), Article.class);
            if (article == null) return;
            articleId = article.getId();
            Map<String, String> params = new HashMap<>();
            params.put(Events.KEYS.ARTICLE_ID, article.getId());
            AnalyticsEvents.INSTANCE.logEvent(this,
                    params,
                    Events.ARTICLE_DETAIL_PAGE);
            presenter.loadSingleArticle(articleId);
        } else if (getIntent().hasExtra("articleId")) {
            articleId = getIntent().getStringExtra("articleId");
            presenter.loadSingleArticle(articleId);
            Map<String, String> params = new HashMap<>();
            params.put(Events.KEYS.ARTICLE_ID, articleId);
            AnalyticsEvents.INSTANCE.logEvent(this,
                    params,
                    Events.ARTICLE_DETAIL_PAGE);
        }

        if (getIntent().hasExtra("type")) {
            type = getIntent().getStringExtra("type");
        }

        if (getIntent().hasExtra("position")) {
            position = getIntent().getIntExtra("position", -1);
        }

        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("MY_ARTICLES")) {
            // don't call related api...
        } else {
            if (articleId != null && !articleId.equals("")) {
//                presenter.loadRelatedArticle(articleId, nextPage != null ? nextPage : "");
            }
        }

        bindViews();
        if (getIntent().hasExtra("article")) {
            likeAndCommentVisibility(true);
            loadLkeAndComments();
        }

        loadRelatedPostView();
        // addItemSeparators();
        setCardRecyclerAdapter();
        setRvScrollListener();

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void loadLkeAndComments() {
        commentMain.setOnClickListener(v -> {
            Intent intent = new Intent(BulletDetailActivity.this, CommentsActivity.class);
            intent.putExtra("article_id", article.getId());
            startActivityForResult(intent, Constants.CommentsRequestCode);
        });

        if (article != null && article.getInfo() != null) {
            comment_count.setText("" + article.getInfo().getComment_count());
            like_count.setText("" + article.getInfo().getLike_count());

            if (article.getInfo().isLiked()) {
                like_count.setTextColor(ContextCompat.getColor(BulletDetailActivity.this, R.color.like_heart_filled));
                like_icon.setImageResource(R.drawable.ic_like_heart_filled);
            } else {
                like_count.setTextColor(ContextCompat.getColor(BulletDetailActivity.this, R.color.like_disable_text_color));
                like_icon.setImageResource(R.drawable.ic_like_heart);
            }

            likeMain.setOnClickListener(v -> {
                likeMain.setEnabled(false);
                likePresenter.like(article.getId(), new LikeInterface() {
                    @Override
                    public void success(boolean like) {
                        //if (getContext() == null) return;
                        likeMain.setEnabled(true);
                        article.getInfo().setLiked(like);
                        int counter = article.getInfo().getLike_count();
                        if (like) {
                            counter++;
                        } else {
                            if (counter > 0) {
                                counter--;
                            } else {
                                counter = 0;
                            }
                        }
                        article.getInfo().setLike_count(counter);
                        like_count.setText("" + counter);

                        if (article.getInfo().isLiked()) {
                            like_count.setTextColor(ContextCompat.getColor(BulletDetailActivity.this, R.color.like_heart_filled));
                            like_icon.setImageResource(R.drawable.ic_like_heart_filled);
                        } else {
                            like_count.setTextColor(ContextCompat.getColor(BulletDetailActivity.this, R.color.like_disable_text_color));
                            like_icon.setImageResource(R.drawable.ic_like_heart);
                        }
                    }

                    @Override
                    public void failure() {
                        likeMain.setEnabled(true);
                    }
                }, !article.getInfo().isLiked());
            });

            dotImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!InternetCheckHelper.isConnected()) {
                        Toast.makeText(BulletDetailActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (article != null && shareBottomSheetPresenter != null) {
                        loaderShow(true);
                        shareBottomSheetPresenter.share_msg(article.getId(), new ShareInfoInterface() {
                            @Override
                            public void response(ShareInfo shareInfo) {
                                loaderShow(false);
                                showBottomSheetDialog(shareInfo, article, new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                    }
                                });
                            }

                            @Override
                            public void error(String error) {
                                loaderShow(false);
                            }
                        });
                    }
                }
            });
        }
    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article
            article, DialogInterface.OnDismissListener onDismissListener) {
        String artyp = "_DETAILS";

        if (shareInfo.isArticle_archived()) {
            artyp = "";
        }

        ShareBottomSheet shareBottomSheet = new ShareBottomSheet(this, new ShareToMainInterface() {
            @Override
            public void removeItem(String id, int position) {

            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
                onGotoChannelListener.onItemClicked(type, id, name, favorite);
            }

            @Override
            public void unarchived() {

            }
        }, true, type + artyp);
        shareBottomSheet.show(article, onDismissListener, shareInfo);

    }

    @Override
    protected void onStart() {
        activityVisible = true;
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        activityVisible = false;
        EventBus.getDefault().unregister(this);
        super.onStop();

        //Added this in case of opening article from widgets, if u go to background and open another article then headline only breaking.
        Constants.auto_scroll = true;
        //Audio overlapping issue from widgets only,
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", "onStop : stop_destroy");
        if (goHome != null)
            goHome.sendAudioEvent("stop_destroy");
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().unregister(this);
        Intent intent = new Intent();
        intent.putExtra("id", articleId);
        intent.putExtra("position", position);
        intent.putExtra("change", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.TYPE_COUNT_API_CALL) {
            if (mainPresenter != null) {
                mainPresenter.articleViewCountUpdate(event.getStringData());
            }
        }
//        else if (event.getType() == MessageEvent.TYPE_SHOW_DETAIL_VIEW) {
//
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                return;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//
//            if (activityVisible) {
//
//                Intent intent = new Intent(BulletDetailActivity.this, BulletDetailActivity.class);
//                intent.putExtra("article", (Article) event.getObjectData());
//                startActivity(intent);
//                finish();
//            }
//            //}
//        }
    }

    private void bindViews() {
        progress = findViewById(R.id.progress);
        mListRV = findViewById(R.id.recyclerview);
        ivBack = findViewById(R.id.ivBack);
        header = findViewById(R.id.header);

        comment_icon = findViewById(R.id.comment_icon);
        comment_count = findViewById(R.id.comment_count);
        commentMain = findViewById(R.id.commentMain);
        like_icon = findViewById(R.id.like_icon);
        like_count = findViewById(R.id.like_count);
        likeMain = findViewById(R.id.likeMain);
        dotImg = findViewById(R.id.setting2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CommentsRequestCode) {
            if (data != null) {
                if (resultCode == RESULT_OK) {
                    if (presenter != null && data.hasExtra("id")) {
                        String id = data.getStringExtra("id");
                        if (!TextUtils.isEmpty(id)) {
                            presenter.counters(id, info -> {
                                Utils.checkLikeView(BulletDetailActivity.this,
                                        info.getLike_count(),
                                        info.getComment_count(),
                                        comment_count,
                                        like_count,
                                        like_icon,
                                        info.isLiked()
                                );
                            });
                        }
                    }

                }
            }
        } else if (presenter != null && data != null && data.hasExtra("id")) {
            String id = data.getStringExtra("id");
            int position = data.getIntExtra("position", -1);
            if (!TextUtils.isEmpty(id) && position != -1) {
                presenter.counters(id, info -> {
                    try {
                        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);
                        if (holder != null && info != null) {
                            if (holder instanceof LargeCardViewHolder) {
                                Utils.checkLikeView(this,
                                        info.getLike_count(),
                                        info.getComment_count(),
                                        ((LargeCardViewHolder) holder).comment_count,
                                        ((LargeCardViewHolder) holder).like_count,
                                        ((LargeCardViewHolder) holder).like_icon,
                                        info.isLiked()
                                );
                            } else if (holder instanceof SmallCardViewHolder) {
                                Utils.checkLikeView(this,
                                        info.getLike_count(),
                                        info.getComment_count(),
                                        ((SmallCardViewHolder) holder).comment_count,
                                        ((SmallCardViewHolder) holder).like_count,
                                        ((SmallCardViewHolder) holder).like_icon,
                                        info.isLiked()
                                );
                            } else if (holder instanceof YoutubeViewHolder) {
                                Utils.checkLikeView(this,
                                        info.getLike_count(),
                                        info.getComment_count(),
                                        ((YoutubeViewHolder) holder).comment_count,
                                        ((YoutubeViewHolder) holder).like_count,
                                        ((YoutubeViewHolder) holder).like_icon,
                                        info.isLiked()
                                );
                            } else if (holder instanceof VideoViewHolder) {
                                Utils.checkLikeView(this,
                                        info.getLike_count(),
                                        info.getComment_count(),
                                        ((VideoViewHolder) holder).comment_count,
                                        ((VideoViewHolder) holder).like_count,
                                        ((VideoViewHolder) holder).like_icon,
                                        info.isLiked()
                                );
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void loadRelatedPostView() {
        if (swipeListener != null) swipeListener.muteIcon(false);
        mListRV.setAdapter(null);
        selectCardPosition(0);
        contentArrayList.add(new Article("", "DETAILS"));
        mCardAdapter = new RelatedFeedAdapter(new CommentClick() {
            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(BulletDetailActivity.this, BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("type", type);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
                finish();
            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {
                Intent intent = new Intent(BulletDetailActivity.this, VideoFullScreenActivity.class);
                intent.putExtra("url", article.getLink());
                intent.putExtra("mode", mode);
                intent.putExtra("position", position);
                intent.putExtra("duration", duration);
                startActivityForResult(intent, Constants.VideoDurationRequestCode);
            }

            @Override
            public void commentClick(int position, String id) {
                Intent intent = new Intent(BulletDetailActivity.this, CommentsActivity.class);
                intent.putExtra("article_id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }
        }, this, contentArrayList, type, true, new DetailsActivityInterface() {
            @Override
            public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {
                if (goHome != null) {
                    goHome.sendAudioToTempHome(audioCallback, fragTag, "", audio);
                }
            }

            @Override
            public void pause() {
                BulletDetailActivity.this.pause(false);
            }

            @Override
            public void resume() {
                resetCurrentBullet();
            }
        }, goHome, shareToMainInterface, swipeListener, this,
                onGotoChannelListener, this,
                () -> {
                    removeAdItem();
                    adFailedStatus = true;
                }, getLifecycle());

        if (article != null) {
            mCardAdapter.setCurrentArticle(article);
        }
    }

    private void removeAdItem() {
        for (int i = 0; i < contentArrayList.size(); i++) {
            if (contentArrayList.get(i).getType().equals("FB_Ad") || contentArrayList.get(i).getType().equals("G_Ad")) {
                contentArrayList.remove(i);
                mCardAdapter.notifyItemRemoved(i);
                removeAdItem();
                return;
            }
        }
    }

    public void resetCurrentBullet() {

        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);

//            mListRV.setPlayerSelector(selector);
            if (holder != null) {
                if (holder instanceof VideoViewHolder) {
//                    ((VideoViewHolder) holder).play();
                } else {
                    if (mArticlePosition != 0) {
                        if (mCardAdapter != null)
                            mCardAdapter.notifyDataSetChanged();

                        nextPosition(mArticlePosition);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCardRecyclerAdapter() {
        cardLinearLayoutManager = new SpeedyLinearLayoutManager(this);
        mListRV.setLayoutManager(cardLinearLayoutManager);
        mListRV.setOnFlingListener(null);
        mListRV.setAdapter(mCardAdapter);

//        mListRV.setCacheManager(mCardAdapter);
//
//        mListRV.setPlayerSelector(selector);

        if (mCardAdapter != null) {
            if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("MY_ARTICLES")) {
                mCardAdapter.setShowLoader(false);
            } else {
                mCardAdapter.setShowLoader(true);
            }
            mCardAdapter.notifyDataSetChanged();
        }
    }

    private void addItemSeparators() {
        int[] ATTRS = new int[]{android.R.attr.listDivider};

        TypedArray a = obtainStyledAttributes(ATTRS);
        Drawable divider = a.getDrawable(0);

        int inset = getResources().getDimensionPixelSize(R.dimen._15sdp);

        InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, inset, 0);
        a.recycle();

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(insetDivider);
        mListRV.addItemDecoration(itemDecoration);
    }

    private void setRvScrollListener() {
        mListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //pause bullet and audio while scrolling
                if (newState == SCROLL_STATE_DRAGGING) {
                    if (goHome != null)
                        goHome.sendAudioEvent("pause");
                    pause(true);
                }

                if (newState == SCROLL_STATE_IDLE) {
                    Map<String, String> params = new HashMap<>();
                    if (article != null && !TextUtils.isEmpty(article.getId()))
                        params.put(Events.KEYS.ARTICLE_ID, article.getId());
                    AnalyticsEvents.INSTANCE.logEvent(BulletDetailActivity.this,
                            params,
                            Events.ARTICLE_SWIPE);
                    Constants.auto_scroll = true;

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());

                    int firstPosition = -1;

                    if (layoutManager != null) {
                        firstPosition = layoutManager.findFirstVisibleItemPosition();
                    }

                    if (firstPosition != -1 && mListRV != null) {
                        mCardAdapter.setDetailViewSelected(false);

                        Rect rvRect = new Rect();
                        mListRV.getGlobalVisibleRect(rvRect);

                        Rect rowRect = new Rect();
                        layoutManager.findViewByPosition(firstPosition).getGlobalVisibleRect(rowRect);

                        int percentFirst;
                        if (rowRect.bottom >= rvRect.bottom) {
                            int visibleHeightFirst = rvRect.bottom - rowRect.top;
                            percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
                        } else {
                            int visibleHeightFirst = rowRect.bottom - rvRect.top;
                            percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
                        }

                        if (percentFirst > 100)
                            percentFirst = 100;

                        int VISIBILITY_PERCENTAGE;
                        if (mArticlePosition == 0)
                            VISIBILITY_PERCENTAGE = 10;
                        else
                            VISIBILITY_PERCENTAGE = 80;

                        int copyOfmArticlePosition = mArticlePosition;

                        Log.d("slections", "onScrollStateChanged: percentFirst = " + percentFirst);

                        /* based on percentage of item visibility, select current or next article
                         *  if prev position is same as new pos then dont reset the article
                         * */
                        if (percentFirst >= VISIBILITY_PERCENTAGE) {
                            Log.d("slections", "onScrollStateChanged: percentage greater");
                            mArticlePosition = firstPosition;
                            if (mArticlePosition == 0) {
                                Log.d("slections", "onScrollStateChanged: mArticlePosition = 0");
                                selectCardPosition(mArticlePosition);

                            } else if (mArticlePosition == contentArrayList.size() - 1) {
                                Log.d("slections", "onScrollStateChanged: last = 0");

                                //on fast scrolling select the last one in the last
                                selectCardPosition(mArticlePosition);

//                                if (prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
                                if (mCardAdapter != null)
                                    mCardAdapter.notifyDataSetChanged();
                            } else if (copyOfmArticlePosition == mArticlePosition) {
                                Log.d("slections", "onScrollStateChanged: copy = new pos");
                                //scroll rested on same article so resume audio and bullet
                                try {
                                    RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                    if (holder != null) {
                                        if (holder instanceof LargeCardViewHolder) {
                                            if (goHome != null)
                                                goHome.sendAudioEvent("resume");
                                            ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                        } else if (holder instanceof SmallCardViewHolder) {
                                            if (goHome != null)
                                                goHome.sendAudioEvent("resume");
                                            ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                        } else {
                                            if (goHome != null)
                                                goHome.sendAudioEvent("stop_destroy");
                                        }
                                    } else {
                                        Log.d("audiotest", "scroll : stop_destroy");
                                        if (goHome != null)
                                            goHome.sendAudioEvent("stop_destroy");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (copyOfmArticlePosition != mArticlePosition) {
                                    Log.d("slections", "onScrollStateChanged: select new");
                                    //scrolled to a new pos, so select new article
                                    selectCardPosition(mArticlePosition);

                                    if (mCardAdapter != null)
                                        mCardAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.d("slections", "onScrollStateChanged: percentage less");
                            mArticlePosition = firstPosition;
                            mArticlePosition++;

                            if (copyOfmArticlePosition != mArticlePosition) {
                                Log.d("slections", "onScrollStateChanged: select new");
                                //scrolled to a new pos, so select new article
                                selectCardPosition(mArticlePosition);

//                                if (prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
                                if (mCardAdapter != null)
                                    mCardAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("slections", "onScrollStateChanged: else");
                                //scroll rested on same article so resume audio and bullet
                                try {
                                    RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                    if (holder != null) {
                                        if (holder instanceof LargeCardViewHolder) {
                                            if (goHome != null)
                                                goHome.sendAudioEvent("resume");
                                            ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                        } else if (holder instanceof SmallCardViewHolder) {
                                            if (goHome != null)
                                                goHome.sendAudioEvent("resume");
                                            ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                        } else {
                                            if (goHome != null)
                                                goHome.sendAudioEvent("stop_destroy");
                                        }
                                    } else {
                                        Log.d("audiotest", "scroll : stop_destroy");
                                        if (goHome != null)
                                            goHome.sendAudioEvent("stop_destroy");
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } else {
                        if (mCardAdapter != null) {
                            unSelectAllItems();
                            mCardAdapter.setDetailViewSelected(true);
                            mCardAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                likeAndCommentWhileScrolling();


                int visibleItemCount = cardLinearLayoutManager.getChildCount();
                int totalItemCount = cardLinearLayoutManager.getItemCount();
                int firstVisibleItemPosition = cardLinearLayoutManager.findFirstVisibleItemPosition();

                if (!isApiCalling && nextPage != null && !nextPage.equals("")) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        if (articleId != null && !articleId.equals("")) {
//                            presenter.loadRelatedArticle(articleId, nextPage);
                        }
                    }
                }

                if (goHome != null) {
                    if (dy > 0) {
                        goHome.scrollUp();
                    } else {
                        goHome.scrollDown();
                    }
                }
//
//                // hide bottom bar while scrolling
//                if (goHomeMainActivity != null) {
//                    if (isVisible && scrollDist > MINIMUM_SCROLL_DIST) {
//                        goHomeMainActivity.scrollUp();
//                        scrollDist = 0;
//                        isVisible = false;
//                    } else if (!isVisible && scrollDist < -MINIMUM_SCROLL_DIST) {
//                        goHomeMainActivity.scrollDown();
//                        scrollDist = 0;
//                        isVisible = true;
//                    }
//
//                    if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
//                        scrollDist += dy;
//                    }
//                }
            }
        });
    }

    private void likeAndCommentWhileScrolling() {
        LinearLayoutManager layoutManager1 = ((LinearLayoutManager) mListRV.getLayoutManager());
        if (layoutManager1 != null) {

            View view = layoutManager1.findViewByPosition(0);
            if (view != null) {
                int height = view.getHeight();
                int heightRecyclerView = mListRV.computeVerticalScrollOffset();

                Log.e("#@#@#@#@#", heightRecyclerView + " " + height + " " + Utils.getScreenWidth());

                if ((heightRecyclerView < height - Utils.getScreenWidth() - 1400) || heightRecyclerView <= 300) {
                    likeAndCommentVisibility(true);
                } else {
                    likeAndCommentVisibility(false);
                }
            } else {
                likeAndCommentVisibility(false);
            }
        } else {
            likeAndCommentVisibility(false);
        }
    }

    private void likeAndCommentVisibility(boolean visible) {
//        header.setVisibility(visible ? View.GONE : View.VISIBLE);
//        commentMain.setVisibility(visible ? View.VISIBLE : View.GONE);
//        dotImg.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void selectCardPosition(int position) {
        if (position > -1) {
            mArticlePosition = position;
            if (mCardAdapter != null)
                mCardAdapter.setCurrentArticlePosition(mArticlePosition);
            if (position != 0) {
                if (article != null)
                    article.setSelected(false);
                if (contentArrayList.size() > 0 && position < contentArrayList.size()) {
                    for (int i = 0; i < contentArrayList.size(); i++) {
                        contentArrayList.get(i).setSelected(false);
                    }
                    //Log.e("isAudioLoaded", "selectCardPosition : " + position + " TAG : " + this.getMyTag());
                    contentArrayList.get(position).setSelected(true);
                }
            } else {
                if (article != null)
                    article.setSelected(true);
            }

//            try {
//                RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//                if (holder != null) {
//                    if (holder instanceof VideoViewHolder) {
//                        mListRV.setPlayerSelector(selector);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    public void unSelectAllItems() {
        if (contentArrayList.size() > 0) {
            for (int i = 0; i < contentArrayList.size(); i++) {
                contentArrayList.get(i).setSelected(false);
            }
            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();
        }
    }

    private void playTextAudio(AudioCallback audioCallback, AudioObject audio) {
        if (goHome != null) {
            goHome.sendAudioToTempHome(audioCallback, "TempHome", "", audio);
        }
    }

    public String getMyTag() {
        return _myTag;
    }

    public void setMyTag(String value) {
        if ("".equals(value))
            return;
        _myTag = value;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Utils.checkAppModeColor(this, false);

        Constants.canAudioPlay = true;
        if (!Constants.muted) {
            if (goHome != null)
                goHome.sendAudioEvent("resume");
        }
        resetCurrentBullet();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Constants.canAudioPlay = false;
        if (goHome != null)
            goHome.sendAudioEvent("pause");
        pause(false);
    }

    private void pause(boolean exceptMainVideo) {
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder == null) {
                holder = mListRV.findViewHolderForAdapterPosition(0);
            }
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof VideoViewHolder) {
                    ((VideoViewHolder) holder).pause();
                } else if (holder instanceof YoutubeViewHolder) {
//                    ((YoutubeViewHolder) holder).bulletPause();
                } else if (holder instanceof DetailViewHolder && !exceptMainVideo) {
                    ((DetailViewHolder) holder).pause();
                }
            }
//
//            PlayerSelector playerSelector = PlayerSelector.NONE;
//            mListRV.setPlayerSelector(playerSelector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loaderShow(boolean flag) {
//        if(mCardAdapter != null && !flag && mCardAdapter.isShowLoader()){
//            mCardAdapter.setShowLoader(false);
//        }
        if (progress != null) {
//            progress.setVisibility(flag ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void error(String error) {
        if (mCardAdapter != null) {
            mCardAdapter.setShowLoader(false);
            mCardAdapter.notifyItemRemoved(1);
        }
    }

    @Override
    public void error404(String error) {
    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {
        if (mCardAdapter != null)
            mCardAdapter.setShowLoader(false);
        if (nextPage == null || nextPage.equals("")) {
            if (prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                    contentArrayList.add(new Article("", "FB_Ad"));
                } else {
                    contentArrayList.add(new Article("", "G_Ad"));
                }
            }
        }

        if (response != null) {

            if (response.getmGroupArticles() != null && response.getmGroupArticles().size() > 0) {
                if (nextPage == null || nextPage.equals("")) {
                    //TITLE
                    Article articleTitle = new Article();
                    articleTitle.setTabTitle(getString(R.string.similar_articles));
                    articleTitle.setType("TITLE");
                    contentArrayList.add(articleTitle);

                    //GROUP ARTICLES
                    Article article = new Article();
                    article.setTitle(getString(R.string.similar_articles));
                    article.setType("GROUP_ARTICLES");
                    article.setArticles(response.getmGroupArticles());
                    contentArrayList.add(article);
                }
            }

            if (response.getArticles() != null && response.getArticles().size() > 0) {
                if (nextPage == null || nextPage.equals("")) {
                    //TITLE
                    Article articleTitle = new Article();
                    articleTitle.setTabTitle(getString(R.string.related_articles));
                    articleTitle.setType("TITLE");
                    contentArrayList.add(articleTitle);
                }
                Log.e("@@@@", response.toString());
                for (int position = 0; position < response.getArticles().size(); position++) {
                    Article article = response.getArticles().get(position);
                    if (prefConfig != null && prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                        int interval = 10;
                        if (prefConfig.getAds().getInterval() != 0) {
                            interval = prefConfig.getAds().getInterval();
                        }
                        int newCount = contentArrayList.size() - 3;
                        if (newCount != 0 && newCount % interval == 0 && !adFailedStatus) {
                            Log.e("ADS", "AD Added");
                            Article adArticle1 = new Article();
                            if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                                adArticle1.setType("FB_Ad");
                            } else {
                                adArticle1.setType("G_Ad");
                            }
                            Log.e("#####", interval + " " + contentArrayList.size() + " ");
                            contentArrayList.add(adArticle1);
                        }
                    }

                    article.setFragTag(getMyTag());
                    contentArrayList.add(article);
                }
//                if (mCardAdapter != null)
//                    mCardAdapter.notifyItemRangeChanged(1, contentArrayList.size() - 1);

                if (response.getMeta() != null) {
                    nextPage = response.getMeta().getNext();
                }
            }
        }

        if (mCardAdapter != null && contentArrayList.size() > 1)
            mCardAdapter.notifyItemRangeChanged(1, contentArrayList.size() - 1);
    }

    @Override
    public void successArticle(Article article) {
        if (mCardAdapter != null && article != null) {
            mCardAdapter.setCurrentArticle(article);
            this.article = article;
            likeAndCommentVisibility(true);
            loadLkeAndComments();
        } else {
            finish();
        }
    }

    @Override
    public void homeSuccess(HomeResponse homeResponse, String currentPage) {

    }

    @Override
    public void nextPosition(int position) {
        if (contentArrayList.size() > 0 && position < contentArrayList.size()) {
            mArticlePosition = position;
            selectCardPosition(mArticlePosition);
            if (mArticlePosition == 0) {
                goHome.sendAudioEvent("stop_destroy");
            }
            if (cardLinearLayoutManager != null)
                cardLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);
            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();
        } else {
            if (!isApiCalling && nextPage != null && !nextPage.equals("")) {
                if (articleId != null && !articleId.equals("")) {
//                    presenter.loadRelatedArticle(articleId, nextPage);
                }
            }
        }
    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {
//        if (contentArrayList.size() > 0 && position < contentArrayList.size()) {
//            mArticlePosition = position;
//            selectCardPosition(mArticlePosition);
//            if (mArticlePosition == 0) {
//                goHome.sendAudioEvent("stop_destroy");
//            }
//
//            if (shouldNotify && mCardAdapter != null)
//                mCardAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onItemHeightMeasured(int height) {
        ViewTreeObserver viewTreeObserver = mListRV.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mListRV != null) {
                        mListRV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewHeight = mListRV.getHeight();

                        mListRV.setPadding(0, 0, 0, viewHeight);
                    }
                }
            });
        }
    }

    private int statusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    @Override
    public void onChannelItemClicked(TYPE type, String id, String name, boolean favorite) {
        //Resetting the audio data to avoid old article speech while loading new data
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", " onitemclick : stop_destroy");
        goHome.sendAudioEvent("stop_destroy");
        // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);

        if (type != null && type.equals(TYPE.MANAGE)) {
            //selectSearch(name);
        } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
            Constants.canAudioPlay = true;

            Intent intent = null;
            if (type.equals(TYPE.SOURCE)) {
                intent = new Intent(BulletDetailActivity.this, ChannelDetailsActivity.class);
            } else {
                intent = new Intent(BulletDetailActivity.this, ChannelPostActivity.class);
            }
            intent.putExtra("type", type);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("favorite", favorite);
            startActivity(intent);
            // finish();
        }
    }

    @Override
    public void showLoader(boolean show) {
        if (progress != null) {
//            progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return ZoomHelper.Companion.getInstance().dispatchTouchEvent(ev, this) || super.dispatchTouchEvent(ev);
//    }
}