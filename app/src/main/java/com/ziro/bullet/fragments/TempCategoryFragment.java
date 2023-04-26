package com.ziro.bullet.fragments;

import static android.app.Activity.RESULT_OK;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.utills.Constants.isApiCalling;
import static java.lang.Math.abs;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.VideoFullScreenActivity;
import com.ziro.bullet.adapters.feed.FeedAdapter;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
import com.ziro.bullet.adapters.foryou.ForYouAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.NewFeed.Footer;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.HomeLoaderCallback;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.ForYou;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.PlayerSelector;
import im.ene.toro.widget.Container;

public class TempCategoryFragment extends Fragment implements NewsCallback, ShareToMainInterface, ListAdapterListener {
    private static final String TAG = "TempCategoryFragment";
    private static boolean isHome;
    private static GoHome goHomeMainActivity;
    private static GoHome goHomeTempHome;
    private static HomeLoaderCallback mLoaderCallback;
    final float MINIMUM_SCROLL_DIST = 2;
    PlayerSelector selector = PlayerSelector.DEFAULT;
    int scrollDist = 0;
    boolean isVisible = true;
    private String type = "";
    private PrefConfig prefConfig;
    private boolean refreshLayout = false;
    private boolean isGotoFollowShow;
    private String mTopic = "", mContextId = "";
    private String mNextPage = "", mCurrPage = "1";
    private String _myTag;
    private ArrayList<Article> contentArrayList = new ArrayList<>();
    private ArrayList<Article> forYouArrayList = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private CardView back;
    private LinearLayout noRecordFoundContainer;
    private Container mListRV;
    private RelativeLayout no_record_found;
    private CardView new_post;
    private RelativeLayout no_saved_articles;
    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private FeedAdapter mCardAdapter;
    private ForYouAdapter mForYouAdapter;
    private TempCategorySwipeListener swipeListener;
    private OnGotoChannelListener listener;
    private NewsPresenter presenter;
    private int mArticlePosition = 0;
    private boolean fragmentVisible = false;
    private boolean adFailedStatus;
    private PictureLoadingDialog mLoadingDialog;
    private DbHandler cacheManager;
    private ArrayList<Article> cacheArticles;
    private ForYou cacheForYouArticles;
    private boolean isPagination = false;
    private ArrayList<Article> newData = new ArrayList<>();
    private ArrayList<Article> forYouData = new ArrayList<>();

    private boolean menuVisible;

    public static TempCategoryFragment newInstance(String topicId, String sourceId, String headLineId,
                                                   String articleId, String locationId, String contextId, String type1,
                                                   boolean specificSourceTopic1, boolean isHome1, GoHome goHomeMainActivity1,
                                                   GoHome goHomeTempHomeListener, HomeLoaderCallback loaderCallback) {
        TempCategoryFragment categoryFragment = new TempCategoryFragment();
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(topicId))
            bundle.putString("topic", topicId);
        if (!TextUtils.isEmpty(sourceId))
            bundle.putString("source", sourceId);
        if (!TextUtils.isEmpty(headLineId))
            bundle.putString("headline", headLineId);
        if (!TextUtils.isEmpty(articleId))
            bundle.putString("article", articleId);
        if (!TextUtils.isEmpty(locationId))
            bundle.putString("location", locationId);
        if (!TextUtils.isEmpty(contextId)) {
            bundle.putString("context", contextId);
        }
        if (!TextUtils.isEmpty(type1))
            bundle.putString("type", type1);
        bundle.putString("TAG", topicId);
        isHome = isHome1;
        goHomeMainActivity = goHomeMainActivity1;
        goHomeTempHome = goHomeTempHomeListener;
        mLoaderCallback = loaderCallback;
        categoryFragment.setArguments(bundle);
        return categoryFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // bottom bar control vars reset
        scrollDist = 0;
        isVisible = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        //Added this in case of opening article from widgets, if u go to background and open another article then headline only breaking.
        Constants.auto_scroll = true;
        //Audio overlapping issue from widgets only,
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", "onStop : stop_destroy");
        if (fragmentVisible)
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
    }

    public String getMyTag() {
        return _myTag;
    }

    public void setMyTag(String value) {
        if ("".equals(value))
            return;
        _myTag = value;
    }

    public void setGotoChannelListener(TempCategorySwipeListener swipeListener, OnGotoChannelListener listener, boolean isGotoFollowShow) {
        this.swipeListener = swipeListener;
        this.listener = listener;
        this.isGotoFollowShow = isGotoFollowShow;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefConfig = new PrefConfig(getContext());
        cacheManager = new DbHandler(getContext());
        if (getArguments() != null && getArguments().containsKey("topic")) {
            mContextId = getArguments().getString("topic");
        }

        if (getArguments() != null && getArguments().containsKey("source")) {
            mContextId = getArguments().getString("source");
        }

        if (getArguments() != null && getArguments().containsKey("headline")) {
            mContextId = getArguments().getString("headline");
        }

        if (getArguments() != null && getArguments().containsKey("article")) {
            mTopic = getArguments().getString("article");
        }

        if (getArguments() != null && getArguments().containsKey("location")) {
            mContextId = getArguments().getString("location");
        }
        if (getArguments() != null && getArguments().containsKey("context")) {
            mContextId = getArguments().getString("context");
        }
        if (getArguments() != null && getArguments().containsKey("type")) {
            type = getArguments().getString("type");
        }

        Log.e(TAG, "onCreateView() mContextId :: " + mContextId + "  ===  " + this.getMyTag());

        return inflater.inflate(R.layout.layout_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refresh = view.findViewById(R.id.refresh);
        back = view.findViewById(R.id.back);
        mListRV = view.findViewById(R.id.recyclerview);
        noRecordFoundContainer = view.findViewById(R.id.no_record_found_container);
        no_record_found = view.findViewById(R.id.no_record_found);
        no_saved_articles = view.findViewById(R.id.no_saved_articles);
        new_post = view.findViewById(R.id.new_post);

        presenter = new NewsPresenter(getActivity(), this);


        Log.e("Height", "====================================");
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            expandCard();
            // addItemSeparators();
            setRvScrollListener();
        } else {
            if (swipeListener != null) swipeListener.muteIcon(false);

            mForYouAdapter = new ForYouAdapter(new CommentClick() {
                @Override
                public void onDetailClick(int position, Article article) {
                    Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                    intent.putExtra("article", new Gson().toJson(article));
                    intent.putExtra("type", type);
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

                @Override
                public void commentClick(int position, String id) {
                    Intent intent = new Intent(getContext(), CommentsActivity.class);
                    intent.putExtra("article_id", id);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, Constants.CommentsRequestCode);
                }
            }, (AppCompatActivity) getActivity(), forYouArrayList, type, isGotoFollowShow, new DetailsActivityInterface() {
                @Override
                public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {
                    if (goHomeTempHome != null) {
                        goHomeTempHome.sendAudioToTempHome(audioCallback, fragTag, "", audio);
                    }
                }

                @Override
                public void pause() {
                    Pause();
                }

                @Override
                public void resume() {
                    resumeCurrentBullet();
                }
            }, goHomeMainActivity, this, swipeListener, this, listener,
                    new ShowOptionsLoaderCallback() {
                        @Override
                        public void showLoader(boolean show) {
                            Log.e("#######", "Show loading " + show);
                            if (show) {
                                showProgressDialog();
                            } else {
                                dismissProgressDialog();
                            }
                        }
                    }, getLifecycle()
            );

            cardLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
            mListRV.setLayoutManager(cardLinearLayoutManager);
            mListRV.setOnFlingListener(null);
            mListRV.setAdapter(mForYouAdapter);


            mListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    //pause bullet and audio while scrolling
                    if (newState == SCROLL_STATE_DRAGGING) {
                        if (goHomeMainActivity != null)
                            goHomeMainActivity.sendAudioEvent("pause");
                        pauseOnlyBullets();
                    }

                    if (newState == SCROLL_STATE_IDLE) {
                        Constants.auto_scroll = true;

                        LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());

                        final int firstPosition;

                        if (layoutManager == null) {
                            return;
                        }

                        if (!fragmentVisible)
                            return;


                        firstPosition = layoutManager.findFirstVisibleItemPosition();


                        new Handler().postDelayed(() -> {
                            if (fragmentVisible) {
                                if (firstPosition != -1) {

                                    Rect rvRect = new Rect();
                                    mListRV.getGlobalVisibleRect(rvRect);

                                    Rect rowRect = new Rect();

                                    if (layoutManager.findViewByPosition(firstPosition) == null)
                                        return;

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
                                            selectForYouCardPosition(mArticlePosition, false);

                                            if (mForYouAdapter != null)
                                                mForYouAdapter.notifyDataSetChanged();
//                                }
                                        } else if (mArticlePosition == contentArrayList.size() - 1) {

                                            //on fast scrolling select the last one in the last
                                            selectForYouCardPosition(mArticlePosition, false);

                                            if (mForYouAdapter != null)
                                                mForYouAdapter.notifyDataSetChanged();
                                        } else if (copyOfmArticlePosition == mArticlePosition) {
                                            Log.d("slections", "onScrollStateChanged: copy = new pos");
                                            //scroll rested on same article so resume audio and bullet
                                            try {
                                                RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                                if (holder != null) {
                                                    if (holder instanceof LargeCardViewHolder) {
                                                        if (goHomeMainActivity != null)
                                                            goHomeMainActivity.sendAudioEvent("resume");
                                                        ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                                    } else if (holder instanceof SmallCardViewHolder) {
                                                        if (goHomeMainActivity != null)
                                                            goHomeMainActivity.sendAudioEvent("resume");
                                                        ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                                    } else if (holder instanceof YoutubeViewHolder) {
                                                        ((YoutubeViewHolder) holder).youtubeResume();
                                                    } else {
                                                        if (goHomeMainActivity != null)
                                                            goHomeMainActivity.sendAudioEvent("stop_destroy");
                                                    }
                                                } else {
                                                    Log.d("audiotest", "scroll : stop_destroy");
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("stop_destroy");
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            if (copyOfmArticlePosition != mArticlePosition) {
                                                //scrolled to a new pos, so select new article
                                                selectForYouCardPosition(mArticlePosition, false);

                                                if (mForYouAdapter != null)
                                                    mForYouAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    } else {
                                        mArticlePosition = firstPosition;
                                        mArticlePosition++;

                                        if (copyOfmArticlePosition != mArticlePosition) {
                                            //scrolled to a new pos, so select new article
                                            selectForYouCardPosition(mArticlePosition, false);

                                            if (mForYouAdapter != null)
                                                mForYouAdapter.notifyDataSetChanged();
                                        } else {
                                            //scroll rested on same article so resume audio and bullet
                                            try {
                                                RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                                if (holder != null) {
                                                    if (holder instanceof LargeCardViewHolder) {
                                                        if (goHomeMainActivity != null)
                                                            goHomeMainActivity.sendAudioEvent("resume");
                                                        ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                                    } else if (holder instanceof SmallCardViewHolder) {
                                                        if (goHomeMainActivity != null)
                                                            goHomeMainActivity.sendAudioEvent("resume");
                                                        ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                                    } else if (holder instanceof YoutubeViewHolder) {
                                                        ((YoutubeViewHolder) holder).youtubeResume();
                                                    } else {
                                                        if (goHomeMainActivity != null)
                                                            goHomeMainActivity.sendAudioEvent("stop_destroy");
                                                    }
                                                } else {
                                                    Log.d("audiotest", "scroll : stop_destroy");
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("stop_destroy");
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }, 500);
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (forYouArrayList.size() - 3 <= cardLinearLayoutManager.findLastVisibleItemPosition() && !isLast()) {
                        if (!isApiCalling) {
                            isApiCalling = true;
                            callNextPage();
                        }
                    }

                    if (goHomeTempHome != null && goHomeMainActivity != null) {
                        if (dy > 0) {
//                        Log.e("RecyclerView_scrolled", "scroll up!");
                            goHomeTempHome.scrollUp();
                            //goHomeMainActivity.scrollUp();
                        } else {
//                        Log.e("RecyclerView_scrolled", "scroll down!");
                            goHomeTempHome.scrollDown();
                            //goHomeMainActivity.scrollDown();
                        }
                    }

                    // hide bottom bar while scrolling
                    if (goHomeMainActivity != null) {
                        if (isVisible && scrollDist > MINIMUM_SCROLL_DIST) {
                            goHomeMainActivity.scrollUp();
                            scrollDist = 0;
                            isVisible = false;
                        } else if (!isVisible && scrollDist < -MINIMUM_SCROLL_DIST) {
                            goHomeMainActivity.scrollDown();
                            scrollDist = 0;
                            isVisible = true;
                        }

                        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                            scrollDist += dy;
                        }
                    }
                }
            });
        }

        if (!isHome) {
            back.setVisibility(View.VISIBLE);
        } else {
            if (contentArrayList != null && contentArrayList.size() > 0 && contentArrayList.get(0) != null) {
                if (contentArrayList.get(0).getId().equalsIgnoreCase("")) {
                    //All Tab
                    back.setVisibility(View.GONE);
                }
            }
        }

        back.setOnClickListener(v -> {
            back.setEnabled(false);
//            if (goHomeMainActivity != null) {
//                goHomeMainActivity.sendAudioEvent("homeTab");
//            }
            if (goHomeTempHome != null) {
                goHomeTempHome.home();
            }
            new Handler().postDelayed(() -> {
                back.setEnabled(true);
            }, 1000);
        });
        new_post.setOnClickListener(v -> {
            new_post.setVisibility(View.GONE);
            if (newData.size() > 0) {
                contentArrayList.addAll(0, newData);
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
                newData.clear();
            } else if (forYouData.size() > 0) {
                forYouArrayList.addAll(0, forYouData);
                if (mForYouAdapter != null)
                    mForYouAdapter.notifyDataSetChanged();
                forYouData.clear();
            }
            selectCardPosition(0);
            scrollToTop();
        });
        refresh.setOnRefreshListener(this::refreshCategory);


        Utils.addNoDataErrorView(getContext(), noRecordFoundContainer, v -> {
            refreshCategory();
        }, null);

        if (mListRV != null)
            mListRV.scrollToPosition(0);

        loadCacheData();
    }

    private void loadCacheData() {
        Log.d(TAG, "loadCacheData() called " + this._myTag + "  :Context : " + mContextId);
        if (!TextUtils.isEmpty(mContextId)) {
//            cacheArticles = cacheManager.GetHomeRecordById(mContextId);
            Log.d(TAG, "cacheArticles = " + cacheArticles + " TAG : " + this._myTag);
            if (cacheArticles != null && cacheArticles.size() > 0) {
                Log.d(TAG, "cacheArticles = " + cacheArticles.size() + " TAG : " + this._myTag);
                ArticleResponse articleResponse = new ArticleResponse();
                articleResponse.setmArticles(cacheArticles);
                loadCacheCategories(articleResponse);
            } else {
                callData(type);
            }
        } else {
            cacheForYouArticles = cacheManager.GetHomeRecordByForYou("for_you");
            Log.d(TAG, "cacheArticles = " + cacheForYouArticles + " TAG : " + this._myTag);
            if (cacheForYouArticles != null) {
                Log.d(TAG, "cacheForYouArticles = " + new Gson().toJson(cacheForYouArticles));
                loadCacheForYou(cacheForYouArticles);
            } else {
                callData(type);
            }
        }
    }

    public void refreshCategory() {
        if (mArticlePosition != 0) {
            scrollToTop();
        }
        new_post.setVisibility(View.GONE);
        refresh.setRefreshing(true);
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("pause");
        mArticlePosition = 0;
        mCurrPage = "1";
        mNextPage = "";
        refreshLayout = true;
        callData(type);
    }

    public void categoryFirstCallAfterGettingNetwork() {
        refresh.setRefreshing(true);
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("pause");
        mArticlePosition = 0;
        mCurrPage = "1";
        mNextPage = "";
        callData(type);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK) {
                if (requestCode == Constants.CommentsRequestCode) {
                    if (presenter != null && data.hasExtra("id")) {
                        String id = data.getStringExtra("id");
                        int position = data.getIntExtra("position", -1);

                        Log.d(TAG, "onActivityResult: " + mForYouAdapter);
                        Log.d(TAG, "onActivityResult: " + mCardAdapter);
                        Log.d(TAG, "onActivityResult: " + mListRV);
                        Log.d(TAG, "onActivityResult: " + mListRV.findViewHolderForAdapterPosition(position));
                        if (!TextUtils.isEmpty(id) && position != -1) {
                            presenter.counters(id, info -> {
                                try {
                                    RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);
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
                                    }

                                    if (contentArrayList != null && contentArrayList.size() > 0 && position < contentArrayList.size()) {
                                        contentArrayList.get(position).setInfo(info);
                                    }

                                    if (forYouArrayList != null && forYouArrayList.size() > 0 && position < forYouArrayList.size()) {
                                        forYouArrayList.get(position).setInfo(info);
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
                            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);
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

    private void callData(String type) {
        Log.e("callData", "type : " + type);
        if (type != null) {
            switch (type) {
                case "ARCHIVE":
                    presenter.archiveArticles(mNextPage);
                    Log.e("callData", "ARCHIVE");
                    break;
                case "SOURCE_PUSH":
                    //this is article ID in mTopic
                    presenter.loadSingleArticle(mTopic);
                    Log.e("callData", "SOURCE_PUSH");
                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("home");
                    break;
                case "TOPIC":
                    presenter.updateNews(mContextId, prefConfig.isReaderMode(), mNextPage);
                    Log.e("callData", "TOPIC");
                    break;
                case "SOURCE":
                    presenter.updateNews(mContextId, prefConfig.isReaderMode(), mNextPage);
                    Log.e("callData", "SOURCE");
                    break;
                default:
                    if (!TextUtils.isEmpty(mContextId)) {
                        presenter.updateNews(mContextId, prefConfig.isReaderMode(), mNextPage);
                    } else {
                        presenter.getArticlesForYou(mNextPage);
                    }
                    Log.e("callData", "default");
            }
        } else {
            if (!TextUtils.isEmpty(mContextId)) {
                presenter.updateNews(mContextId, prefConfig.isReaderMode(), mNextPage);
            } else {
                presenter.getArticlesForYou(mNextPage);
            }
            Log.e("callData", "else");
        }
    }

    public void selectFirstItemOnCardViewMode() {
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            selectCardPosition(mArticlePosition);
            if (mCardAdapter != null) {
                mCardAdapter.notifyDataSetChanged();
            }
        } else {
            selectForYouCardPosition(mArticlePosition, false);
//            mListRV.scrollToPosition(0);
            if (mForYouAdapter != null) {
                mForYouAdapter.notifyDataSetChanged();
            }
        }
    }

    public void selectCardPosition(int position) {
        if (position > -1) {
            mArticlePosition = position;
            if (mCardAdapter != null)
                mCardAdapter.setCurrentArticlePosition(mArticlePosition);
            if (contentArrayList.size() > 0 && position < contentArrayList.size()) {
                for (int i = 0; i < contentArrayList.size(); i++) {
                    contentArrayList.get(i).setSelected(false);
                }
                Log.e("isAudioLoaded", "selectCardPosition : " + position + " TAG : " + this.getMyTag());
                contentArrayList.get(position).setSelected(true);
            }
        }
    }

    private void selectForYouCardPosition(int position, boolean isPrev) {
        if (forYouArrayList == null || forYouArrayList.size() == 0)
            return;

        if (position > -1 && position < forYouArrayList.size() && forYouArrayList.get(position).getHeader() != null) {
            if (isPrev)
                position--;
            else
                position++;
        }

        if (position > -1 && position < forYouArrayList.size() && forYouArrayList.get(position).getTabTitle() != null) {
            if (isPrev)
                position--;
            else
                position++;
        }

        if (position > -1 && position < forYouArrayList.size()) {
            mArticlePosition = position;
            if (mForYouAdapter != null)
                mForYouAdapter.setCurrentArticlePosition(mArticlePosition);
            if (forYouArrayList.size() > 0 && position < forYouArrayList.size()) {
                for (int i = 0; i < forYouArrayList.size(); i++) {
                    forYouArrayList.get(i).setSelected(false);
                }
                forYouArrayList.get(position).setSelected(true);
            }
        }
    }

    public void unSelectAllItems() {
        if (!TextUtils.isEmpty(mContextId)) {
            if (contentArrayList.size() > 0) {
                for (int i = 0; i < contentArrayList.size(); i++) {
                    contentArrayList.get(i).setSelected(false);
                }
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
            }
        } else {
            if (forYouArrayList != null && forYouArrayList.size() > 0) {
                for (int i = 0; i < forYouArrayList.size(); i++) {
                    forYouArrayList.get(i).setSelected(false);
                }
                if (mForYouAdapter != null)
                    mForYouAdapter.notifyDataSetChanged();
            }
        }
    }


    private void addItemSeparators() {
        int[] ATTRS = new int[]{android.R.attr.listDivider};

        TypedArray a = getContext().obtainStyledAttributes(ATTRS);
        Drawable divider = a.getDrawable(0);

        int inset = getResources().getDimensionPixelSize(R.dimen._15sdp);

        InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, inset, 0);
        a.recycle();

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
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
                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("pause");
                    pauseOnlyBullets();
                }

                if (newState == SCROLL_STATE_IDLE) {
                    Constants.auto_scroll = true;

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());

                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!fragmentVisible)
                        return;

                    new Handler().postDelayed(() -> {
                        if (fragmentVisible) {

                            if (firstPosition != -1 && mListRV != null) {
                                Rect rvRect = new Rect();
                                mListRV.getGlobalVisibleRect(rvRect);

                                Rect rowRect = new Rect();

                                if (layoutManager.findViewByPosition(firstPosition) == null)
                                    return;

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
                                        selectCardPosition(mArticlePosition);

                                        if (mCardAdapter != null)
                                            mCardAdapter.notifyDataSetChanged();
//                                }
                                    } else if (mArticlePosition == contentArrayList.size() - 1) {

                                        //on fast scrolling select the last one in the last
                                        selectCardPosition(mArticlePosition);

                                        if (mCardAdapter != null)
                                            mCardAdapter.notifyDataSetChanged();
                                    } else if (copyOfmArticlePosition == mArticlePosition) {
                                        Log.d("slections", "onScrollStateChanged: copy = new pos");
                                        //scroll rested on same article so resume audio and bullet
                                        try {
                                            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                            if (holder != null) {
                                                if (holder instanceof LargeCardViewHolder) {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("resume");
                                                    ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                                } else if (holder instanceof SmallCardViewHolder) {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("resume");
                                                    ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                                } else if (holder instanceof YoutubeViewHolder) {
                                                    ((YoutubeViewHolder) holder).youtubeResume();
                                                } else {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("stop_destroy");
                                                }
                                            } else {
                                                Log.d("audiotest", "scroll : stop_destroy");
                                                if (goHomeMainActivity != null)
                                                    goHomeMainActivity.sendAudioEvent("stop_destroy");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        if (copyOfmArticlePosition != mArticlePosition) {
                                            //scrolled to a new pos, so select new article
                                            selectCardPosition(mArticlePosition);

                                            if (mCardAdapter != null)
                                                mCardAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } else {
                                    mArticlePosition = firstPosition;
                                    mArticlePosition++;

                                    if (copyOfmArticlePosition != mArticlePosition) {
                                        //scrolled to a new pos, so select new article
                                        selectCardPosition(mArticlePosition);
                                        if (mCardAdapter != null)
                                            mCardAdapter.notifyDataSetChanged();
                                    } else {
                                        //scroll rested on same article so resume audio and bullet
                                        try {
                                            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                            if (holder != null) {
                                                if (holder instanceof LargeCardViewHolder) {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("resume");
                                                    ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                                } else if (holder instanceof SmallCardViewHolder) {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("resume");
                                                    ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                                } else if (holder instanceof YoutubeViewHolder) {
                                                    ((YoutubeViewHolder) holder).youtubeResume();
                                                } else {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("stop_destroy");
                                                }
                                            } else {
                                                Log.d("audiotest", "scroll : stop_destroy");
                                                if (goHomeMainActivity != null)
                                                    goHomeMainActivity.sendAudioEvent("stop_destroy");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }, 500);

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (contentArrayList.size() - 3 <= cardLinearLayoutManager.findLastVisibleItemPosition() && !isLast()) {
                    if (!isApiCalling) {
                        isApiCalling = true;
                        callNextPage();
                    }
                }


                if (goHomeTempHome != null && goHomeMainActivity != null) {
                    if (dy > 0) {
//                        Log.e("RecyclerView_scrolled", "scroll up!");
                        goHomeTempHome.scrollUp();
                        //goHomeMainActivity.scrollUp();
                    } else {
//                        Log.e("RecyclerView_scrolled", "scroll down!");
                        goHomeTempHome.scrollDown();
                        //goHomeMainActivity.scrollDown();
                    }
                }

                // hide bottom bar while scrolling
                if (goHomeMainActivity != null) {
                    if (isVisible && scrollDist > MINIMUM_SCROLL_DIST) {
                        goHomeMainActivity.scrollUp();
                        scrollDist = 0;
                        isVisible = false;
                    } else if (!isVisible && scrollDist < -MINIMUM_SCROLL_DIST) {
                        goHomeMainActivity.scrollDown();
                        scrollDist = 0;
                        isVisible = true;
                    }

                    if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                        scrollDist += dy;
                    }
                }

            }
        });
    }

    private void setListRecyclerListeners() {

        mListRV.setOnFlingListener(new RecyclerView.OnFlingListener() {

            @Override
            public boolean onFling(int velocityX, int velocityY) {
                Log.d(TAG, "onFling: " + abs(velocityY));

                if (abs(velocityY) > 15000) {
                    velocityY = 6400 * (int) Math.signum((double) velocityY);
                    mListRV.fling(velocityX, velocityY);
                    return true;
                } else if (abs(velocityY) > 10000 && abs(velocityY) != 6400) {
                    velocityY = 5400 * (int) Math.signum((double) velocityY);
                    mListRV.fling(velocityX, velocityY);
                    return true;
                } else if (abs(velocityY) > 5000 && abs(velocityY) != 6400 && abs(velocityY) != 5400) {
                    velocityY = 3000 * (int) Math.signum((double) velocityY);
                    mListRV.fling(velocityX, velocityY);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Pause current bullets, youtube videos
     * Normal videos are by setting selector NONE, so dont forget to set DEFAULT selector on resume otherwise videos wont play even if resumed
     */
    public void Pause() {
        fragmentVisible = false;
        Log.d("youtubePlayer", "Pause = " + mArticlePosition);
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            Log.d("youtubePlayer", "Pauseholder = " + holder);
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof VideoViewHolder) {

//                    ((VideoViewHolder) holder).pause();
                } else if (holder instanceof YoutubeViewHolder) {
                    Log.d("youtubePlayer", "Pause utube");
                    ((YoutubeViewHolder) holder).bulletPause();
                }
            }

            PlayerSelector playerSelector = PlayerSelector.NONE;
            mListRV.setPlayerSelector(playerSelector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseOnlyBullets() {
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
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
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resumeCurrentBullet() {
        try {
            mListRV.setPlayerSelector(selector);
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.resume();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.resume();
                } else if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).bulletResume();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCurrentBullet() {
        fragmentVisible = true;
        try {
            mListRV.setPlayerSelector(selector);
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof VideoViewHolder) {
                    mListRV.setPlayerSelector(selector);
                } else {
                    if (mCardAdapter != null)
                        mCardAdapter.notifyDataSetChanged();

                    nextPosition(mArticlePosition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCardAdapter = null;
        mForYouAdapter = null;
        mListRV.setAdapter(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCardAdapter = null;
        mForYouAdapter = null;
        mListRV.setAdapter(null);
    }


    public void scrollToTop() {
        if (cardLinearLayoutManager != null)
            cardLinearLayoutManager.scrollToPositionWithOffset(0, 0);
//        if (mListRV != null)
//            mListRV.scrollToPosition(0);
    }

    private boolean isLast() {
        return mNextPage.equalsIgnoreCase("");
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag && (contentArrayList.size() > 0 || forYouArrayList.size() > 0)) {
            flag = false;
        }
        if (mLoaderCallback != null && fragmentVisible)
            mLoaderCallback.onProgressChanged(flag);
    }

    @Override
    public void error(String error) {
        if (!TextUtils.isEmpty(error) && error.toLowerCase().contains("cancel")) {
            return;
        }
        refresh.setRefreshing(false);
        if ((mCurrPage.equals("") || mCurrPage.equals("1"))) {
            if (!TextUtils.isEmpty(mContextId)) {
                if (contentArrayList.size() == 0) {
                    showNoDataErrorView(true);
                }
            } else {
                if (forYouArrayList.size() == 0) {
                    showNoDataErrorView(true);
                }
            }
        }
    }

    @Override
    public void error404(String error) {
        refresh.setRefreshing(false);
        if (mCurrPage.equals("") || mCurrPage.equals("1")) {
            if (!TextUtils.isEmpty(mContextId)) {
                if (contentArrayList.size() == 0) {
                    showNoDataErrorView(true);
                }
            } else {
                if (forYouArrayList.size() == 0) {
                    showNoDataErrorView(true);
                }
            }
        }
    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {
        showNoDataErrorView(false);
        refresh.setRefreshing(false);
        if (refreshLayout) {
            contentArrayList.clear();
        }
        boolean newPosts = false;
        if (response != null && response.getArticles() != null && response.getArticles().size() > 0) {
            no_record_found.setVisibility(View.GONE);

            int newItems = 0;
            ArrayList<Article> articles = null;
            if (refreshLayout || cacheArticles == null || cacheArticles.size() == 0) {
                articles = response.getArticles();
            } else {
                if (TextUtils.isEmpty(mNextPage)) {
//                    articles = Utils.checkArticleDataIsSame(cacheArticles, response.getArticles());
                    if (articles.size() > 0 && !isPagination && !refreshLayout) {
                        newPosts = true;
                    }
                } else {
                    articles = response.getArticles();
                }
            }
            if (response.getMeta() != null) {
                mNextPage = response.getMeta().getNext();
            }

            for (int position = 0; position < articles.size(); position++) {
                Article article = articles.get(position);
                if (prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                    int interval = 10;
                    if (prefConfig.getAds().getInterval() != 0) {
                        interval = prefConfig.getAds().getInterval();
                    }

                    if (newPosts) {
//                        if (articles.size() != 0 && articles.size() % interval == 0 && !adFailedStatus) {
//                            Log.e("ADS", "AD Added");
//                            Article adArticle1 = new Article();
//                            if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
//                                adArticle1.setType("FB_Ad");
//                            } else {
//                                adArticle1.setType("G_Ad");
//                            }
//                            newData.add(adArticle1);
//                        }
                    } else {
                        if (contentArrayList.size() != 0 && contentArrayList.size() % interval == 0 && !adFailedStatus) {
                            Log.e("ADS", "AD Added");
                            Article adArticle1 = new Article();
                            if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                                adArticle1.setType("FB_Ad");
                            } else {
                                adArticle1.setType("G_Ad");
                            }
                            contentArrayList.add(adArticle1);
                        }
                    }
                }

                article.setFragTag(getMyTag());
                if (newPosts) {
                    newData.add(article);
                } else {
                    contentArrayList.add(article);
                }
            }

            if (newPosts) {
                if (newData.size() > 0) {
                    new_post.setVisibility(View.VISIBLE);
                }
            } else {
                if (fragmentVisible) {
                    if (cacheArticles == null || !TextUtils.isEmpty(mNextPage)) {
                        if (goHomeTempHome != null)
                            goHomeTempHome.sendAudioEvent("select_first_article");
                    }
                }
            }
            if (fragmentVisible) {
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
            }

            if (swipeListener != null) {
                swipeListener.muteIcon(true);
            }
        } else {
            if (contentArrayList.size() == 0) {
                if (swipeListener != null) {
                    swipeListener.muteIcon(false);
                }
                if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("ARCHIVE")) {
                    no_saved_articles.setVisibility(View.VISIBLE);
                } else
                    no_record_found.setVisibility(View.VISIBLE);

                contentArrayList.clear();
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
            }
        }
        refreshLayout = false;
        isPagination = false;
    }

//    @Override
//    public void successForYou(ForYou forYou, boolean offlineData) {
////        if (mNextPage.equals("")) {
////            mListRV.scrollToPosition(0);
////        }
//        showNoDataErrorView(false);
//        refresh.setRefreshing(false);
//        if (refreshLayout) {
//            forYouArrayList.clear();
//        }
//
//        boolean newPosts = false;
//        if (forYou != null) {
//            if (forYou.getArticleResponse() != null && forYou.getArticleResponse().size() > 0) {
//                no_record_found.setVisibility(View.GONE);
//                ArrayList<ArticleResponse> articleResponses = null;
//                if (refreshLayout || cacheForYouArticles == null || cacheForYouArticles.getArticleResponse().size() == 0) {
//                    articleResponses = forYou.getArticleResponse();
//                } else {
//                    if (TextUtils.isEmpty(mNextPage)) {
//                        articleResponses = checkDataIsSame(cacheForYouArticles.getArticleResponse(), forYou.getArticleResponse());
//                        if (articleResponses.size() > 0 && !isPagination && !refreshLayout) {
//                            newPosts = true;
//                        }
//                    } else {
//                        articleResponses = forYou.getArticleResponse();
//                    }
//                }
//                if (articleResponses != null) {
//
//                    int newItems = 0;
//
//                    for (ArticleResponse articleResponse : articleResponses) {
//                        if (articleResponse != null) {
//                            Log.d(TAG, " ID " + articleResponse.getId());
//                            if (!TextUtils.isEmpty(articleResponse.getHeader())) {
//                                Article articleHeader = new Article();
//                                articleHeader.setHeader(articleResponse.getHeader());
//                                if (newPosts) {
//                                    forYouData.add(articleHeader);
//                                    newItems++;
//                                } else {
//                                    forYouArrayList.add(articleHeader);
//                                }
//                            }
//
//                            if (!TextUtils.isEmpty(articleResponse.getCategoryName())) {
//                                Article articleTitle = new Article();
//                                articleTitle.setTabTitle(articleResponse.getCategoryName());
//                                articleTitle.setTabId(articleResponse.getId());
//                                articleTitle.setTabIcon(articleResponse.getIcon());
//                                if (newPosts) {
//                                    forYouData.add(articleTitle);
//                                    newItems++;
//                                } else {
//                                    forYouArrayList.add(articleTitle);
//                                }
//                            }
//                            if (articleResponse.getArticles() != null && articleResponse.getArticles().size() > 0) {
//                                for (int i = 0; i < articleResponse.getArticles().size(); i++) {
//                                    Article articleItem = articleResponse.getArticles().get(i);
//                                    if (i == articleResponse.getArticles().size() - 1) {
//                                        articleItem.setForYouLastItem(true);
//                                        if (!TextUtils.isEmpty(articleResponse.getFooter())) {
//                                            articleItem.setTabId(articleResponse.getId());
//                                            articleItem.setFooter(articleResponse.getFooter());
//                                            articleItem.setForYouTitle(articleResponse.getCategoryName());
//                                        }
//                                    }
//                                    if (newPosts) {
//                                        forYouData.add(articleItem);
//                                        newItems++;
//                                    } else {
//                                        forYouArrayList.add(articleItem);
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    for (int i = 0; i < forYouArrayList.size(); i++) {
//                        forYouArrayList.get(i).setFragTag(getMyTag());
//                    }
//
//                    if (newPosts) {
//                        if (forYouData.size() > 0) {
////                            if (fragmentVisible) {
//                            new_post.setVisibility(View.VISIBLE);
////                            }
//                        }
//                    } else {
//                        new_post.setVisibility(View.GONE);
//                        if (fragmentVisible) {
//                            if (cacheForYouArticles == null || !TextUtils.isEmpty(mNextPage)) {
//                                if (goHomeTempHome != null)
//                                    goHomeTempHome.sendAudioEvent("select_first_article");
//                            }
//                        }
//                    }
//                }
//            } else {
//                if (forYouArrayList.size() == 0) {
//                    if (swipeListener != null) {
//                        swipeListener.muteIcon(false);
//                    }
//                    no_record_found.setVisibility(View.VISIBLE);
//                }
//            }
//            if (forYou.getMeta() != null) {
//                mNextPage = forYou.getMeta().getNext();
//            }
//        }
//        isPagination = false;
//        refreshLayout = false;
//    }

    public void loadCacheCategories(ArticleResponse response) {
        showNoDataErrorView(false);
        refresh.setRefreshing(false);
        if (response != null && response.getArticles() != null && response.getArticles().size() > 0) {
            no_record_found.setVisibility(View.GONE);

            for (int position = 0; position < response.getArticles().size(); position++) {
                Article article = response.getArticles().get(position);
                if (prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                    int interval = 10;
                    if (prefConfig.getAds().getInterval() != 0) {
                        interval = prefConfig.getAds().getInterval();
                    }

                    if (contentArrayList.size() != 0 && contentArrayList.size() % interval == 0 && !adFailedStatus) {
                        Log.e("ADS", "AD Added");
                        Article adArticle1 = new Article();
                        if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                            adArticle1.setType("FB_Ad");
                        } else {
                            adArticle1.setType("G_Ad");
                        }
                        contentArrayList.add(adArticle1);
                    }
                }

                article.setFragTag(getMyTag());
                contentArrayList.add(article);
            }
            new_post.setVisibility(View.GONE);

            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();

            if (fragmentVisible) {
                if (goHomeTempHome != null)
                    goHomeTempHome.sendAudioEvent("select_first_article");
            }
            if (swipeListener != null) {
                swipeListener.muteIcon(true);
            }
        } else {
            if (contentArrayList.size() == 0) {
                if (swipeListener != null) {
                    swipeListener.muteIcon(false);
                }
                if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("ARCHIVE")) {
                    no_saved_articles.setVisibility(View.VISIBLE);
                } else
                    no_record_found.setVisibility(View.VISIBLE);

                contentArrayList.clear();
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
            }
        }
        callData(type);
    }

    public void loadCacheForYou(ForYou forYou) {
        showNoDataErrorView(false);
        refresh.setRefreshing(false);
        if (forYou != null && cacheForYouArticles != null && forYou.getArticleResponse() != null && forYou.getArticleResponse().size() > 0) {

            ArrayList<ArticleResponse> articleResponses = cacheForYouArticles.getArticleResponse();

            for (ArticleResponse articleResponse : articleResponses) {
                if (articleResponse != null) {
                    Log.d(TAG, " ID " + articleResponse.getId());
                    if (!TextUtils.isEmpty(articleResponse.getHeader())) {
                        Article articleHeader = new Article();
                        articleHeader.setHeader(articleResponse.getHeader());
                        forYouArrayList.add(articleHeader);
                    }

                    if (!TextUtils.isEmpty(articleResponse.getCategoryName())) {
                        Article articleTitle = new Article();
                        articleTitle.setTabTitle(articleResponse.getCategoryName());
                        articleTitle.setTabId(articleResponse.getId());
                        articleTitle.setTabIcon(articleResponse.getIcon());
                        forYouArrayList.add(articleTitle);
                    }
                    if (articleResponse.getArticles() != null && articleResponse.getArticles().size() > 0) {
                        for (int i = 0; i < articleResponse.getArticles().size(); i++) {
                            Article articleItem = articleResponse.getArticles().get(i);
                            if (i == articleResponse.getArticles().size() - 1) {
                                articleItem.setForYouLastItem(true);
                                if (!TextUtils.isEmpty(articleResponse.getFooter())) {
                                    articleItem.setTabId(articleResponse.getId());
                                    articleItem.setFooter(new Footer(articleResponse.getFooter()));
                                    articleItem.setForYouTitle(articleResponse.getCategoryName());
                                }
                            }
                            forYouArrayList.add(articleItem);
                        }
                    }
                }
            }
            new_post.setVisibility(View.GONE);

            for (int i = 0; i < forYouArrayList.size(); i++) {
                forYouArrayList.get(i).setFragTag(getMyTag());
            }

            if (fragmentVisible) {
                if (goHomeTempHome != null)
                    goHomeTempHome.sendAudioEvent("select_first_article");
            }

        } else {
            if (forYouArrayList.size() == 0) {
                if (swipeListener != null) {
                    swipeListener.muteIcon(false);
                }
                no_record_found.setVisibility(View.VISIBLE);
            }
        }
        callData(type);
    }

    private ArrayList<ArticleResponse> checkDataIsSame(ArrayList<ArticleResponse> cacheRecord, ArrayList<ArticleResponse> apiRecord) {
        ArrayList<ArticleResponse> newData = new ArrayList<>();
        if (cacheRecord != null && cacheRecord.size() > 0 && apiRecord != null && apiRecord.size() > 0) {
            for (ArticleResponse api : apiRecord) {
                boolean found = false;
                for (ArticleResponse cache : cacheRecord) {
                    if (cache.getId().equalsIgnoreCase(api.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newData.add(api);
                }
            }
        }
        return newData;
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (!menuVisible)
            pauseOnlyBullets();
        fragmentVisible = menuVisible;
        this.menuVisible = menuVisible;
    }

    public boolean checkVisibility() {
//        if(getActivity() instanceof MainActivityNew){
//            if(((MainActivityNew)getActivity()).getHomeFragment().isPopupVisible()){
//                return false;
//            }
//        }
        return menuVisible;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (mListAdapter != null) mListAdapter.dismissBottomSheet();
        if (mCardAdapter != null) mCardAdapter.dismissBottomSheet();
        if (mForYouAdapter != null) mForYouAdapter.dismissBottomSheet();
    }

//    public void selectFirstCard(boolean flag) {
////        cardMain.setVisibility(View.VISIBLE);
////        loaderShow(true);
////        if (tempCategoryHelper != null) {
////            tempCategoryHelper.setCardData();
////        }
//    }

    @Override
    public void successArticle(Article article) {
        //FOR PUSH NOTIFICATION SHOW SINGLE ARTICLE
        showNoDataErrorView(false);
        refresh.setRefreshing(false);
        if (refreshLayout) {
            contentArrayList.clear();
//            if (mListAdapter != null)
//                mListAdapter.notifyDataSetChanged();
            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();
            refreshLayout = false;
        }
        if (article != null) {
            no_record_found.setVisibility(View.GONE);
            contentArrayList.add(article);

            if (mCurrPage.equalsIgnoreCase("1")) {
                if (contentArrayList.size() > 0) {
                    contentArrayList.get(0).setSelected(true);
                    mCurrPage = "2";
                }
            }
            Log.e("LogcontentAr", "contentArrayList : " + contentArrayList.size());

            for (int i = 0; i < contentArrayList.size(); i++) {
                contentArrayList.get(i).setFragTag(getMyTag());
            }
//            if (mListAdapter != null)
//                mListAdapter.notifyDataSetChanged();
            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();

            if (swipeListener != null) {
                if (contentArrayList.size() > 0) {
                    swipeListener.muteIcon(true);
                } else {
                    swipeListener.muteIcon(false);
                }
                swipeListener.onFavoriteChanged(article.getSource().isFavorite());
            }
        } else {
            if (swipeListener != null) {
                swipeListener.muteIcon(false);
            }
        }
    }

    @Override
    public void homeSuccess(HomeResponse homeResponse, String currentPage) {

    }

    @Override
    public void nextPosition(int position) {
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("pause");
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            if (contentArrayList.size() > 0 && position < contentArrayList.size() && position > -1) {
                mArticlePosition = position;
                contentArrayList.get(position).setLastPosition(0);
                selectCardPosition(mArticlePosition);
                if (cardLinearLayoutManager != null)
                    cardLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);

                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
            }
        } else {
            if (forYouArrayList.size() > 0 && position < forYouArrayList.size() && position > -1) {
                boolean isPrev = false;
                if (mArticlePosition > position) {
                    isPrev = true;
                }

                mArticlePosition = position;
                selectForYouCardPosition(mArticlePosition, isPrev);
                if (cardLinearLayoutManager != null)
                    cardLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);

                if (mForYouAdapter != null)
                    mForYouAdapter.notifyDataSetChanged();

            }
        }
    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {
        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);

        int oldPos = mArticlePosition;
        Article article;

        if (TextUtils.isEmpty(mContextId)) {
            article = forYouArrayList.get(position);
        } else {
            article = contentArrayList.get(position);
        }

        if (holder instanceof LargeCardViewHolder)
            ((LargeCardViewHolder) holder).selectUnselectedItem(position, article);
        else if (holder instanceof SmallCardViewHolder)
            ((SmallCardViewHolder) holder).selectUnselectedItem(position, article);

        if (TextUtils.isEmpty(mContextId)) {
            selectForYouCardPosition(position, false);
        } else {
            selectCardPosition(position);
        }

        RecyclerView.ViewHolder holderOld = mListRV.findViewHolderForAdapterPosition(oldPos);
        if (holderOld instanceof SmallCardViewHolder)
            ((SmallCardViewHolder) holderOld).unselect(article);
        if (holderOld instanceof LargeCardViewHolder)
            ((LargeCardViewHolder) holderOld).unselect(article);
        else if (holderOld instanceof VideoViewHolder)
            if (TextUtils.isEmpty(mContextId)) {
                mForYouAdapter.notifyItemChanged(oldPos);
            } else {
                mCardAdapter.notifyItemChanged(oldPos);
            }
        else if (holderOld instanceof YoutubeViewHolder)
            if (TextUtils.isEmpty(mContextId)) {
                mForYouAdapter.notifyItemChanged(oldPos);
            } else {
                mCardAdapter.notifyItemChanged(oldPos);
            }
    }

    private void callNextPage() {
        if (InternetCheckHelper.isConnected()) {
            isPagination = true;
            if (!TextUtils.isEmpty(mContextId)) {
                presenter.updateNews(mContextId, prefConfig.isReaderMode(), mNextPage);
            } else {
                presenter.getArticlesForYou(mNextPage);
            }
        }
    }

//    public void collapseCard() {
//        if (swipeListener != null) swipeListener.muteIcon(true);
//        merge = false;
//        mListRV.setAdapter(null);
//        selectCardPosition(0);
//        mListAdapter = new MainActivityAdapter(goHomeMainActivity, this, swipeListener, this, listener, R.layout.collapse_card_item, getActivity(), contentArrayList, isGotoFollowShow, type);
//        setRecyclerAdapter();
//    }

    public void expandCard() {
        if (swipeListener != null) swipeListener.muteIcon(false);
        mListRV.setAdapter(null);
        mCardAdapter = new FeedAdapter(new CommentClick() {

            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("type", type);
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

            @Override
            public void commentClick(int position, String id) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("article_id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }
        }, false, (AppCompatActivity) getActivity(), contentArrayList, type, isGotoFollowShow, new DetailsActivityInterface() {
            @Override
            public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {
                if (goHomeTempHome != null) {
                    goHomeTempHome.sendAudioToTempHome(audioCallback, fragTag, "", audio);
                }
            }

            @Override
            public void pause() {
                Pause();
            }

            @Override
            public void resume() {
                resumeCurrentBullet();
            }
        }, goHomeMainActivity, this, swipeListener, this, listener,
                new ShowOptionsLoaderCallback() {
                    @Override
                    public void showLoader(boolean show) {
                        if (show) {
                            showProgressDialog();
                        } else {
                            dismissProgressDialog();
                        }
                    }
                },
                new AdFailedListener() {
                    @Override
                    public void onAdFailed() {
                        removeAdItem();
                        adFailedStatus = true;
                    }
                },
                getLifecycle()
        );
//        mCardAdapter.tempCategoryFragment = this;
        cardLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
        mListRV.setLayoutManager(cardLinearLayoutManager);
        mListRV.setOnFlingListener(null);
        mListRV.setAdapter(mCardAdapter);
        mListRV.setCacheManager(mCardAdapter);
        mListRV.setPlayerSelector(selector);
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

    public void reload() {
        mNextPage = "";
        contentArrayList.clear();
        mCurrPage = "1";
//        if (mListAdapter != null)
//            mListAdapter.notifyDataSetChanged();
        if (mCardAdapter != null)
            mCardAdapter.notifyDataSetChanged();
//        if (prefConfig.getMenuViewMode().equalsIgnoreCase(MENU_EXTENDED_VIEW)) {
//            Log.e("expandCard", "-expandCard-> ");
        expandCard();
//        } else {
//            Log.e("expandCard", "-collapseCard-> ");
//            collapseCard();
//        }
        callData(type);
    }

    @Override
    public void removeItem(String id, int position) {

    }

    @Override
    public void unarchived() {
        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("ARCHIVE")) {
            Log.d("audiotest", "unarchve home : stop_destroy");
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("stop_destroy");
            reload();
        }
    }

    @Override
    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
        if (listener != null) {
            listener.onItemClicked(type, id, name, favorite);
        }
    }

    @Override
    public void verticalScrollList(boolean isEnable) {
        if (cardLinearLayoutManager != null)
            cardLinearLayoutManager.setScrollEnabled(isEnable);
    }

    @Override
    public void nextArticle(int position) {
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            if (contentArrayList != null && position < contentArrayList.size()) {
                int pos = position;
                pos++;
                if (mListRV != null)
                    mListRV.smoothScrollToPosition(pos);
            }
        } else {
            if (forYouArrayList != null && position < forYouArrayList.size()) {
                int pos = position;
                pos++;
                if (mListRV != null)
                    mListRV.smoothScrollToPosition(pos);
            }
        }
    }

    @Override
    public void prevArticle(int position) {
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            if (position > 0) {
                int pos = position;
                pos--;
                if (mListRV != null)
                    mListRV.smoothScrollToPosition(pos);
            }
        } else {
            if (forYouArrayList != null && position < forYouArrayList.size()) {
                int pos = position;
                pos--;
                if (mListRV != null)
                    mListRV.smoothScrollToPosition(pos);
            }
        }
    }

    @Override
    public void clickArticle(int position) {

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

                        LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());
                        if (forYouArrayList != null && forYouArrayList.size() > 0) {
                            View view = layoutManager.findViewByPosition(forYouArrayList.size());
                            if (view != null)
                                mListRV.setPadding(0, 0, 0, viewHeight - height - view.getHeight());
                            else
                                mListRV.setPadding(0, 0, 0, viewHeight - height);
                        } else {
                            mListRV.setPadding(0, 0, 0, viewHeight - height);
                        }
                    }
                }
            });
        }
    }

    public void showNoDataErrorView(boolean show) {
        noRecordFoundContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private int statusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    /**
     * loading dialog
     */
    protected void showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(getContext());
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissProgressDialog() {
        try {
            if (mLoadingDialog != null
                    && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            mLoadingDialog = null;
            e.printStackTrace();
        }
    }


//    public interface OnGotoChannelListener {
//        void onItemClicked(TYPE type, String id, String name, boolean favorite);
//        void onArticleSelected(Article article);
//    }
}
