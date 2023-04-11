package com.ziro.bullet.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
import com.ziro.bullet.adapters.schedule.ScheduledListAdapter;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.fragments.LoadingDialog;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ScheduleTimerFinishListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.texttospeech.TextToAudioPlayerHelper;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

import im.ene.toro.PlayerSelector;
import im.ene.toro.widget.Container;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

public class DraftsListingActivity extends BaseActivity implements NewsCallback, ScheduleTimerFinishListener {

    public static final String SOURCE_ID = "source_id";
    private static final String TAG = "DraftActivity";
    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private Container mRecyclerView;

    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private ScheduledListAdapter mCardAdapter;

    private ArrayList<Article> mContentArrayList = new ArrayList<>();
    private int mArticlePosition = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String nextPage = "";
    private TextToAudioPlayerHelper textToAudio;
    private AudioCallback audioCallback;
    private boolean isUpdate = false;
    private NewsPresenter presenter;

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
                    intent = new Intent(DraftsListingActivity.this, ChannelDetailsActivity.class);
                } else {
                    intent = new Intent(DraftsListingActivity.this, ChannelPostActivity.class);
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
                    intent = new Intent(DraftsListingActivity.this, ChannelDetailsActivity.class);
                } else {
                    intent = new Intent(DraftsListingActivity.this, ChannelPostActivity.class);
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
    private String source;
    private BroadcastReceiver updateEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("method");
            if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase("update")) {
                updateArticles();
            }
        }
    };
    private LoadingDialog loadingDialog;
    private RelativeLayout btnBack;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_draft);

        if (getIntent().hasExtra(SOURCE_ID)) {
            source = getIntent().getStringExtra(SOURCE_ID);
        }

        mRecyclerView = findViewById(R.id.recyclerview);
        btnBack = findViewById(R.id.ivBack);
        tvEmpty = findViewById(R.id.tvEmpty);

        presenter = new NewsPresenter(this, this);
        textToAudio = new TextToAudioPlayerHelper(this);
//        prefConfig = new PrefConfig(getContext());
        LocalBroadcastManager.getInstance(this).registerReceiver(updateEvent, new IntentFilter(ACTION_UPDATE_EVENT));

        btnBack.setOnClickListener(v -> onBackPressed());
        setAdapter();
        setRvScrollListener();
        isUpdate = false;
        presenter.loadDraftedPosts(source, nextPage);
    }

    private void setAdapter() {
        mCardAdapter = new ScheduledListAdapter(this, ScheduledListAdapter.TYPE.DRAFT,this, mContentArrayList, this, new DetailsActivityInterface() {
            @Override
            public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {

            }

            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }
        });


        cardLinearLayoutManager = new SpeedyLinearLayoutManager(DraftsListingActivity.this);
        mRecyclerView.setLayoutManager(cardLinearLayoutManager);
        mRecyclerView.setOnFlingListener(null);
        mRecyclerView.setAdapter(mCardAdapter);
//        mRecyclerView.setCacheManager(mCardAdapter);

        mRecyclerView.setPlayerSelector(selector);
    }

    private void setRvScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == SCROLL_STATE_IDLE) {
                    Constants.auto_scroll = true;

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mRecyclerView.getLayoutManager());

                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();

                    if (firstPosition != -1) {

                        Rect rvRect = new Rect();
                        mRecyclerView.getGlobalVisibleRect(rvRect);

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

                        int VISIBILITY_PERCENTAGE = 90;

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

                                if (mCardAdapter != null)
                                    mCardAdapter.notifyDataSetChanged();
                                Log.d("youtubePlayer", "notify onScrollStateChanged");
//                                }
                            } else if (mArticlePosition == mContentArrayList.size() - 1) {
                                Log.d("slections", "onScrollStateChanged: last = 0");

                                //on fast scrolling select the last one in the last
                                selectCardPosition(mArticlePosition);

                                if (mCardAdapter != null)
                                    mCardAdapter.notifyDataSetChanged();
                                Log.d("youtubePlayer", "notify onScrollStateChanged");
                            } else if (copyOfmArticlePosition == mArticlePosition) {
                                Log.d("slections", "onScrollStateChanged: copy = new pos");
                                //scroll rested on same article so resume audio and bullet
                                try {
                                    RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(mArticlePosition);
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
                                    Log.d("youtubePlayer", "notify onScrollStateChanged");
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

                                if (mCardAdapter != null)
                                    mCardAdapter.notifyDataSetChanged();
                                Log.d("youtubePlayer", "notify onScrollStateChanged");
                            } else {
                                Log.d("slections", "onScrollStateChanged: else");
                                //scroll rested on same article so resume audio and bullet
                                try {
                                    RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(mArticlePosition);
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
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mContentArrayList.size() - 3 <= cardLinearLayoutManager.findLastVisibleItemPosition() && !TextUtils.isEmpty(nextPage)) {
                    isUpdate = false;
                    if (!isLoading)
                        presenter.loadDraftedPosts( source, nextPage);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateEvent);
    }

    public void updateArticles() {
        isUpdate = true;
        if (presenter == null) return;
        nextPage = "";
        mContentArrayList.clear();
        presenter.loadDraftedPosts(source, nextPage);
    }

    public void selectCardPosition(int position) {
        if (position > -1) {
            mArticlePosition = position;
            if (mCardAdapter != null)
                mCardAdapter.setCurrentArticlePosition(mArticlePosition);
            if (mContentArrayList.size() > 0 && position < mContentArrayList.size()) {
                for (int i = 0; i < mContentArrayList.size(); i++) {
                    mContentArrayList.get(i).setSelected(false);
                }
                //Log.e("isAudioLoaded", "selectCardPosition : " + position + " TAG : " + this.getMyTag());
                String status = mContentArrayList.get(position).getStatus();
                if (TextUtils.isEmpty(status)) {
                    mContentArrayList.get(position).setSelected(true);
                } else {
                    if (!status.equalsIgnoreCase(Constants.ARTICLE_SCHEDULED) && !status.equalsIgnoreCase(Constants.ARTICLE_PROCESSING)) {
                        mContentArrayList.get(position).setSelected(true);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.canAudioPlay = true;
        if (!Constants.muted) {
            if (goHome != null)
                goHome.sendAudioEvent("resume");
        }
        resetCurrentBullet();
    }

    @Override
    public void onPause() {
        super.onPause();
        Constants.canAudioPlay = false;
        if (goHome != null)
            goHome.sendAudioEvent("pause");
        pause();
    }

    private void pause() {
        try {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof VideoViewHolder) {
                    ((VideoViewHolder) holder).pause();
                } else if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).bulletPause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCurrentBullet() {

        try {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof VideoViewHolder) {
                    selectCardPosition(mArticlePosition);
                    ((VideoViewHolder) holder).play();
                } else if (holder instanceof YoutubeViewHolder) {
                    nextPosition(mArticlePosition);
                    if (mCardAdapter != null)
                        mCardAdapter.notifyDataSetChanged();
                } else {
                    nextPosition(mArticlePosition);
                    if (mCardAdapter != null)
                        mCardAdapter.notifyDataSetChanged();
                }

                Log.d("youtubePlayer", "notify resetCurrentBullet");
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
        showLoadingView(flag);
    }

    @Override
    public void error(String error) {
        if (getWindow() != null && getWindow().getDecorView() != null) {
            Utils.showSnacky(getWindow().getDecorView().getRootView(), error);
        }
    }

    @Override
    public void error404(String error) {

    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {
        if (isFinishing()) {
            return;
        }
        if (response.getArticles() != null && response.getArticles().size() > 0) {
            if (TextUtils.isEmpty(nextPage)) {
                mContentArrayList.clear();
                setAdapter();
            }
            int preCount = mContentArrayList.size();
            mContentArrayList.addAll(response.getArticles());

            selectCardPosition(mArticlePosition);

            mCardAdapter.notifyDataSetChanged();
            Log.d("youtubePlayer", "notify success");

        }

        nextPage = response.getMeta().getNext();

        if (mContentArrayList.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            if (isUpdate) {
                mCardAdapter.notifyDataSetChanged();
                Log.d("youtubePlayer", "notify success");
            }
            mRecyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void successArticle(Article article) {

    }

    @Override
    public void homeSuccess(HomeResponse homeResponse, String currentPage) {

    }

    @Override
    public void nextPosition(int position) {
        if (mContentArrayList.size() > 0 && position < mContentArrayList.size()) {
            mArticlePosition = position;
            selectCardPosition(mArticlePosition);
            if (mArticlePosition == 0) {
                goHome.sendAudioEvent("stop_destroy");
            }
            if (cardLinearLayoutManager != null)
                cardLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);
            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();
            Log.d("youtubePlayer", "notify nextPosition");
        }
    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {
//        if (mContentArrayList.size() > 0 && position < mContentArrayList.size()) {
//            mArticlePosition = position;
//            selectCardPosition(mArticlePosition);
//            if (mArticlePosition == 0) {
//                goHome.sendAudioEvent("stop_destroy");
//            }
//            if (shouldNotify && mCardAdapter != null)
//                mCardAdapter.notifyDataSetChanged();
//            Log.d("youtubePlayer", "notify nextPosition");
//        }
    }

    @Override
    public void onItemHeightMeasured(int height) {
        ViewTreeObserver viewTreeObserver = mRecyclerView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mRecyclerView != null && mRecyclerView != null) {
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewHeight = mRecyclerView.getHeight();

                        mRecyclerView.setPadding(0, 0, 0, viewHeight - height);
                    }
                }
            });
        }
    }

    private void showLoadingView(boolean isShow) {
        if (isFinishing()) {
            return;
        }
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(this);
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null
                    && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    @Override
    public void removeArticle(int position) {
        new Handler().postDelayed(() -> {
            if (mCardAdapter != null) {
                if (position < mContentArrayList.size()) {
                    mContentArrayList.remove(position);
                }
                mCardAdapter.notifyDataSetChanged();
            }
        }, 4000);
    }
}