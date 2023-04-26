package com.ziro.bullet.fragments;

import static android.app.Activity.RESULT_OK;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.VideoFullScreenActivity;
import com.ziro.bullet.adapters.feed.FeedAdapter;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.UpdateCallback;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.texttospeech.TextToAudioPlayerHelper;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.PlayerSelector;
import im.ene.toro.widget.Container;

public class SaveArticleFragment extends Fragment implements NewsCallback {

    private static final String TAG = "ProfileArticleFragment";
    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private Container mRecyclerView;
    private LinearLayout llCard;
    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private FeedAdapter mCardAdapter;
    private ArrayList<Article> mContentArrayList = new ArrayList<>();
    private int mArticlePosition = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String nextPage = "";
    private TextToAudioPlayerHelper textToAudio;
    private boolean fragmentVisible = false;
    private boolean isUpdate = false;
    private PrefConfig prefConfig;
    private NewsPresenter presenter;
    private AudioCallback audioCallback;

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
                Log.e("expandCard", "newInstance 3");

                Intent intent = new Intent(getContext(), ChannelDetailsActivity.class);
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
            refresh();
//            mCardAdapter.notifyDataSetChanged();
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
                Log.e("expandCard", "newInstance 3");

                Intent intent = new Intent(getContext(), ChannelDetailsActivity.class);
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
    private BroadcastReceiver updateEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("method");
            if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase("update")) {
                updateArticles();
            }
        }
    };
    private OnFragmentInteractionListener listener;
    private String sourceId;
    private boolean adFailedStatus;
    private UpdateCallback updateCallback;

    public SaveArticleFragment(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setCallbackListener(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK) {
                if (requestCode == Constants.CommentsRequestCode) {
                    if (presenter != null && data != null && data.hasExtra("id")) {
                        String id = data.getStringExtra("id");
                        int position = data.getIntExtra("position", -1);
                        if (!TextUtils.isEmpty(id) && position != -1) {
                            presenter.counters(id, info -> {
                                try {
                                    RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
                                    if (holder != null && info != null) {
                                        if (holder instanceof LargeCardViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((LargeCardViewHolder) holder).comment_count,
                                                    ((LargeCardViewHolder) holder).like_count,
                                                    ((LargeCardViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );
                                        } else if (holder instanceof SmallCardViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((SmallCardViewHolder) holder).comment_count,
                                                    ((SmallCardViewHolder) holder).like_count,
                                                    ((SmallCardViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );
                                        } else if (holder instanceof YoutubeViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((YoutubeViewHolder) holder).comment_count,
                                                    ((YoutubeViewHolder) holder).like_count,
                                                    ((YoutubeViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );
                                        } else if (holder instanceof VideoViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((VideoViewHolder) holder).comment_count,
                                                    ((VideoViewHolder) holder).like_count,
                                                    ((VideoViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );
                                        }

                                        if (mContentArrayList != null && mContentArrayList.size() > 0 && position < mContentArrayList.size()) {
                                            mContentArrayList.get(position).setInfo(info);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                } else if (requestCode == Constants.VideoDurationRequestCode) {
                    int position = data.getIntExtra("position", -1);
                    long duration = data.getLongExtra("duration", 0);
                    if (position != -1 && duration > 0) {
                        try {
                            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(position);
                            if (holder != null) {
                                if (holder instanceof YoutubeViewHolder) {
                                    ((YoutubeViewHolder) holder).seekTo(duration);
                                } else if (holder instanceof VideoViewHolder) {
                                    ((VideoViewHolder) holder).seekTo(duration);
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateEvent);
    }

    public void updateArticles() {
        isUpdate = true;
        if (presenter == null) return;
        nextPage = "";
        mContentArrayList.clear();
        presenter.archiveArticles(nextPage);
    }

    public void refresh() {
        nextPage = "";
        mContentArrayList.clear();
        if (presenter != null)
            presenter.archiveArticles(nextPage);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new NewsPresenter(getActivity(), this);
        textToAudio = new TextToAudioPlayerHelper(getContext());
        prefConfig = new PrefConfig(getContext());
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateEvent, new IntentFilter(ACTION_UPDATE_EVENT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recyclerview);
        llCard = view.findViewById(R.id.llCard);
        TextView from_label = view.findViewById(R.id.from_label);
        from_label.setText(getString(R.string.no_saved_stories));
        setAdapter();
        setRvScrollListener();
        isUpdate = false;

        presenter.archiveArticles(nextPage);
    }

    private void setAdapter() {
        mCardAdapter = new FeedAdapter(new CommentClick() {
            @Override
            public void commentClick(int position, String id) {
                Intent intent = new Intent(getActivity(), CommentsActivity.class);
                intent.putExtra("article_id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("type", "ARTICLES");
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void onNewDetailClick(int position, Article article, List<Article> articlelist) {

            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {
                Intent intent = new Intent(getContext(), VideoFullScreenActivity.class);
                intent.putExtra("url", article.getLink());
                intent.putExtra("mode", mode);
                intent.putExtra("position", position);
                intent.putExtra("duration", duration);
                startActivityForResult(intent, Constants.VideoDurationRequestCode);
            }
        }, false, (AppCompatActivity) getActivity(), mContentArrayList, "MY_SAVE_ARTICLES", false, new DetailsActivityInterface() {
            @Override
            public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {
                if (goHome != null) {
                    goHome.sendAudioToTempHome(audioCallback, fragTag, "", audio);
                }
            }

            @Override
            public void pause() {
                SaveArticleFragment.this.pause();
            }

            @Override
            public void resume() {
                resetCurrentBullet();
                if (updateCallback != null) {
                    updateCallback.onUpdate();
                }
            }
        }, goHome, shareToMainInterface, swipeListener, this, onGotoChannelListener,
                new ShowOptionsLoaderCallback() {
                    @Override
                    public void showLoader(boolean show) {

                    }
                },
                new AdFailedListener() {
                    @Override
                    public void onAdFailed() {
                        removeAdItem();
                        adFailedStatus = true;
                    }
                }, getLifecycle()
        );

        cardLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(cardLinearLayoutManager);
        mRecyclerView.setOnFlingListener(null);
        mRecyclerView.setAdapter(mCardAdapter);
        mRecyclerView.setCacheManager(mCardAdapter);

        mRecyclerView.setPlayerSelector(selector);
    }

    private void removeAdItem() {
        for (int i = 0; i < mContentArrayList.size(); i++) {
            if (mContentArrayList.get(i).getType().equals("FB_Ad") || mContentArrayList.get(i).getType().equals("G_Ad")) {
                mContentArrayList.remove(i);
                mCardAdapter.notifyItemRemoved(i);
                removeAdItem();
                return;
            }
        }
    }

    public void pauseOnlyBullets() {
        try {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof VideoViewHolder) {

//                    ((VideoViewHolder) holder).pause();
                } else if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).bulletPause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRvScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //pause bullet and audio while scrolling
                if (newState == SCROLL_STATE_DRAGGING) {
                    if (goHome != null)
                        goHome.sendAudioEvent("pause");
                    pauseOnlyBullets();
                }

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
                                        } else if (holder instanceof YoutubeViewHolder) {
                                            ((YoutubeViewHolder) holder).youtubeResume();
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
                                        } else if (holder instanceof YoutubeViewHolder) {
                                            ((YoutubeViewHolder) holder).youtubeResume();
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
                        presenter.archiveArticles(nextPage);
                }
            }
        });
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
        if (fragmentVisible) {
            Constants.canAudioPlay = true;
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("resume");
            }
            resetCurrentBullet();
        }
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        Constants.canAudioPlay = false;
        if (goHome != null)
            goHome.sendAudioEvent("pause");
        pause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("stop");
            }
            pause();
        } else {
            Constants.canAudioPlay = true;
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("resume");
            }
            resetCurrentBullet();
        }
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
    }

    @Override
    public void error(String error) {
        if (getActivity() != null && getActivity().getWindow() != null && getActivity().getWindow().getDecorView() != null) {
            Utils.showSnacky(getActivity().getWindow().getDecorView().getRootView(), error);
        }
    }

    @Override
    public void error404(String error) {

    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        if (response.getArticles() != null && response.getArticles().size() > 0) {
            if (TextUtils.isEmpty(nextPage)) {
                mContentArrayList.clear();
                setAdapter();
            }

            for (int position = 0; position < response.getArticles().size(); position++) {
                Article article = response.getArticles().get(position);
                if (prefConfig != null && prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                    int interval = 10;
                    if (prefConfig.getAds().getInterval() != 0) {
                        interval = prefConfig.getAds().getInterval();
                    }
                    int newCount = mContentArrayList.size();
                    if (newCount != 0 && newCount % interval == 0 && !adFailedStatus) {
                        Log.e("ADS", "AD Added");
                        Article adArticle1 = new Article();
                        if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                            adArticle1.setType("FB_Ad");
                        } else {
                            adArticle1.setType("G_Ad");
                        }
                        mContentArrayList.add(adArticle1);
                    }
                }
                mContentArrayList.add(article);
            }

            if (fragmentVisible) {
                selectCardPosition(mArticlePosition);
            }
            mCardAdapter.notifyDataSetChanged();
            Log.d("youtubePlayer", "notify success");
        }

        if (mContentArrayList.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            llCard.setVisibility(View.GONE);
        } else {
            if (isUpdate) {
                mCardAdapter.notifyDataSetChanged();
                Log.d("youtubePlayer", "notify success");
            }
            mRecyclerView.setVisibility(View.GONE);
            llCard.setVisibility(View.VISIBLE);
        }

        if (response.getMeta() != null)
            nextPage = response.getMeta().getNext();
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

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        fragmentVisible = menuVisible;
        if (!fragmentVisible) {
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("stop");
            }
            pause();
        } else {
            Constants.canAudioPlay = true;
            if (!Constants.muted) {
                if (goHome != null)
                    goHome.sendAudioEvent("resume");
            }

            try {
                RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(mArticlePosition);
                if (holder != null) {
                    if (holder instanceof VideoViewHolder) {
//                        selectCardPosition(mArticlePosition);
                        ((VideoViewHolder) holder).play();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


//            new Handler().postDelayed(() -> resetCurrentBullet(), 300);
        }
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        Log.e("LKASD", "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    public interface OnFragmentInteractionListener {
        void onUploadClicked();
    }
}