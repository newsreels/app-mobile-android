package com.ziro.bullet.fragments.test;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.downloader.PRDownloader;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AuthorActivity;
import com.ziro.bullet.activities.BaseActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.NotificationActivity;
import com.ziro.bullet.activities.ProfileActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ForYouReelSheet;
import com.ziro.bullet.bottomSheet.ReelViewMoreSheet;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.data.models.home.HomeModel;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.interfaces.BottomSheetInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.HomeCallback;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.StudioCallback;
import com.ziro.bullet.interfaces.VideoInterface;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.articles.MediaMeta;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.HomePresenter;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.presenter.ReelPresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.presenter.StudioPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.M3UParser;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReelInnerActivity extends BaseActivity implements VideoInterface, StudioCallback, M3UParser.VideoCacheListener, ReelFraInterface {
    public static final String REEL_F_TITLE = "title";
    public static final String REEL_F_PAGE = "page";
    public static final String REEL_F_AUTHOR_ID = "authorID";
    public static final String REEL_F_CONTEXT = "context";
    public static final String REEL_F_TYPE = "type";
    public static final String REEL_F_SOURCE_ID = "sourceID";
    public static final String REEL_F_HASHTAG = "hashtag";
    public static final String REEL_F_MODE = "mode";
    public static final String REEL_POSITION = "position";
    public static final String REEL_F_DATALIST = "reellist";
    private static final String TAG = "VideoFragment";
    private static final int INTENT_REELS_VERSION = 3424;
    private static final String KEY_FOR_YOU = "for_you";
    private static final String KEY_FOLLOWING = "following";
    private static final String HLS_DIR = "hls_files";
    private static final String VIDEOS_DIR = "NewsReels";
    private static GoHome goHome;
    private static SimpleExoPlayer exoPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    int maxCharacters = 40;
    long startPosition = 0;
    boolean volumechanged = true;
    int maxsize;
    int currentAdapterPosition;
    Handler handler2 = new Handler();
    boolean isExpanded = false;
    private Boolean isreelsheetopend = false;
    private boolean clickable = true;
    private LikePresenter presenter;
    private boolean playWhenReady = true;
    private int currentItem = 0;
    private long playbackPosition = 0L;
    private boolean isVideoReady = true;
    //    private SimpleOrientationListener mOrientationListener;
    private View view;
    private ViewPager2 viewPager;
    private final Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            if (isVideoReady == false) {
                viewPager.setUserInputEnabled(false);
            } else {
                viewPager.setUserInputEnabled(true);
            }
        }
    };
    private int prevPosition = -1;
    private int curPosition = 0;
    private SeekBar volumeseekbar;
    //    private VideoPagerAdapter pagerAdapter;
    private VideoAdapter pagerAdapter;
    private List<ReelsItem> videoItems = new ArrayList<>();
    private String authorID = null;
    private String sourceID = null;
    private AnimatedVectorDrawableCompat avd;
    private AnimatedVectorDrawable avd2;
    private boolean isBuffering = true;
    private boolean isVideoEnded = false;
    private ReelPresenter reelPresenter;
    private ReelsNewPresenter reelsNewPresenter;
    private String mNext = PAGE_START;
    private String mType = "";
    private LinearLayout noRecordFoundContainer;
    private PrefConfig prefConfig;
    private int mCurrentPosition = -1;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            showEndOfReelMsg(false);
            viewPager.setCurrentItem(mCurrentPosition - 1, true);
        }
    };
    private int prevReelsize = 0;
    private SwipeRefreshLayout refresh;
    private boolean isRefresh = false;
    private NewsPresenter newsPresenter;
    private HomePresenter homePresenter;
    private HomeModel homeModel;
    private final HomeCallback homeCallback = new HomeCallback() {
        @Override
        public void loaderShow(boolean flag) {

        }

        @Override
        public void error(String error) {

        }

        @Override
        public void success(HomeModel response) {
            homeModel = response;
        }

        @Override
        public void searchSuccess(CategoryResponse body, boolean isPagination) {

        }
    };
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private FragmentManager fm = null;
    private Fragment currentFragment = null;
    private String mPage = "";
    private ViewSwitcher reelsViewSwitcher;
    private boolean isHidden = false;
    private ConstraintLayout ll_reels_info;
    private TextView tvLabel;
    private TextView tvText;
    private ProgressBar progress_barreel;
    private ReelViewMoreSheet reelViewMoreSheet;
    private DbHandler cacheManager;
    private CardView new_post;
    private RelativeLayout ivBack;
    private ImageView notification;
    private PictureLoadingDialog loadingDialog;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private ShareBottomSheet shareBottomSheet;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private ForYouReelSheet forYouReelSheet;
    private String mode = null;
    private String hashtag = "";
    private int reelPosition = -1;
    private String context = "";
    private String title = null;
    private String channelName = "";
    //    private String desc = "";
    private Boolean fromfragment;
    private M3UParser m3UParser;
    private int cachePosition = -1, maxCacheLimit = -1;
    private List<ReelsItem> reelsToSave = new ArrayList<>();
    private boolean isCaching = false;
    private StudioPresenter studioPresenter;
    private boolean isFollowingShow = false;
    private RelativeLayout rvReelInfo;
    private LinearLayout llReelOptions;

    public static ReelInnerActivity newInstance(GoHome goHome1) {
        goHome = goHome1;
        return new ReelInnerActivity();
    }
//    public static ReelInnerActivity getInstance(String query, GoHome goHome1) {
//        goHome = goHome1;
//        ReelInnerActivity reelInnerFragment = new ReelInnerActivity();
//        Bundle bundle = new Bundle();
//        bundle.putString(BUNDLE_QUERY, query);
//        reelInnerFragment.setArguments(bundle);
////        return allFragment; check and add this
//        return reelInnerFragment;
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new Components().blackStatusBar(this, "black");
        setContentView(R.layout.fragment_reel);
        videoItems.clear();
        prefConfig = new PrefConfig(this);
        bindViews();
        getBundle();
        init();

    }


    private void getBundle() {
        if (getIntent() != null) {
            videoItems = getIntent().getParcelableArrayListExtra(REEL_F_DATALIST);
            authorID = getIntent().getStringExtra(REEL_F_AUTHOR_ID);
            sourceID = getIntent().getStringExtra(REEL_F_SOURCE_ID);
            context = getIntent().getStringExtra(REEL_F_CONTEXT);
            mode = getIntent().getStringExtra(REEL_F_MODE);
            hashtag = getIntent().getStringExtra(REEL_F_HASHTAG);
            title = getIntent().getStringExtra(REEL_F_TITLE);
            fromfragment = getIntent().getBooleanExtra("fromfragment", true);
            mNext = getIntent().getStringExtra(REEL_F_PAGE);
            mType = getIntent().getStringExtra(REEL_F_TYPE);
            reelPosition = getIntent().getIntExtra(REEL_POSITION, -1);
            mPage = "0";
        }
    }

    private void noRecordFound() {
//        Utils.addNoFollowingView(getContext(), noRecordFoundContainer, v -> {
//                    forYouBottomSheet("menu");
//                }
//                , Constants.DARK);
    }


    private void bindViews() {
        notification = findViewById(R.id.notification);
        new_post = findViewById(R.id.new_post);
        refresh = findViewById(R.id.refresh);
        viewPager = findViewById(R.id.pager);
        ivBack = findViewById(R.id.ivBack);
        reelsViewSwitcher = findViewById(R.id.reels_view_switcher);
        noRecordFoundContainer = findViewById(R.id.no_record_found_container);
        ll_reels_info = findViewById(R.id.constraintLayoutReel);
        tvLabel = findViewById(R.id.tvLabel);
        progress_barreel = findViewById(R.id.progress_barreel);
        volumeseekbar = findViewById(R.id.volumeseekbar);
        llReelOptions = findViewById(R.id.right);
    }


    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        fm = getSupportFragmentManager();
        currentFragment = fm.findFragmentByTag("f0");
        reelPresenter = new ReelPresenter(this, this);
        reelsNewPresenter = new ReelsNewPresenter(this, this);
        newsPresenter = new NewsPresenter(this, null);
        followUnfollowPresenter = new FollowUnfollowPresenter(this);
        shareBottomSheetPresenter = new ShareBottomSheetPresenter(this);
        studioPresenter = new StudioPresenter(this, this);
        homePresenter = new HomePresenter(this, homeCallback);
        homePresenter.getHome(Constants.CAT_TYPE_REELS);
        presenter = new LikePresenter(this);
        pagerAdapter = new VideoAdapter(viewPager, this, this, reelsNewPresenter);
        viewPager.setAdapter(pagerAdapter);
        Constants.reelfragment = false;

        ivBack.setOnClickListener(v -> onBackPressed());


        if (videoItems != null) {
            if (pagerAdapter != null) {
                pagerAdapter.setVideoList((ArrayList<ReelsItem>) videoItems);
            }
        } else {
            if (!isLastPage) {
                isLoading = true;
                if (TextUtils.isEmpty(mode)) {
                    reelPresenter.getVideos("", context, mNext, false, false, hashtag);
                } else if (mode.equalsIgnoreCase("public") && !TextUtils.isEmpty(mNext)) {
                    reelPresenter.getVideos("", context, mNext, false, false, hashtag);
                } else if (mode.equalsIgnoreCase("saved")) {
                    studioPresenter.loadSavedReels(mNext);
                }
            }
        }

        double availableRam = NetworkSpeedUtils.Companion.availableRam(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (availableRam >= 3) {
                viewPager.setOffscreenPageLimit(5);
            } else {
                viewPager.setOffscreenPageLimit(2);
            }
        } else {
            if (availableRam >= 3) {
                viewPager.setOffscreenPageLimit(4);
            } else {
                viewPager.setOffscreenPageLimit(2);
            }
        }

        handler2.postDelayed(runnable2, 1000);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

//
//                if (!Constants.mViewRecycled) {
                Log.e("TAGfv", "onPageSelected:true " + position);

                curPosition = position;
                if (prevPosition > -1) {
                    pagerAdapter.pausePrevPlayback(prevPosition);
                }
                pagerAdapter.resumePlayback(curPosition);
                prevPosition = curPosition;
                //next api call
                if (videoItems != null && videoItems.size() > 0 && reelPresenter != null) {
                    if (InternetCheckHelper.isConnected()) {
                        reelPresenter.event(videoItems.get(position).getId());
                        if ((videoItems.size() - curPosition) < 10 && !Objects.equals(mPage, mNext) && !isLastPage) {
                            reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, true, "");
                            mPage = mNext;
                        }
                    }
                }

                viewPager.setUserInputEnabled(false);
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setUserInputEnabled(true);
                        clickable = true;
                    }
                }, 350);


            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                viewPager.setPageTransformer(new SlowPageTransformer(viewPager));

            }
        });


        refresh.setOnRefreshListener(this::reload);

        notification.setOnClickListener(view -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        isreelsheetopend = false;

//    write in adapter
//                ReelsItem reeli = null;
//                reeli = videoItemsList.get(index).getReelsItem();
//                if (reeli != null) {
//                    if (exoPlayer != null) {
//                        Map<String, String> params = new HashMap<>();
//                        params.put(Events.KEYS.REEL_ID, reeli.getId());
//                        long endPosition = exoPlayer.getCurrentPosition();
//                        long elapsedTime = endPosition - startPosition;
//                        params.put(Events.KEYS.DURATION, String.valueOf(elapsedTime));
//                        AnalyticsEvents.INSTANCE.reelDurationEvent(ReelInnerActivity.this, params, Events.REEL_DURATION, reeli.getId());
//                        Log.e(TAG, "onPause:ssss RF backpress" + reeli.getDescription() + params + mCurrentPosition);
//                    }

        super.onBackPressed();
        Constants.notishare = false;
        if (Constants.reelfragment) {
            Constants.rvmdailogopen = true;
        }
    }

    private void showBottomSheet(ReelsItem reelsItem, int position) {
        if (reelsItem != null) {
            reelViewMoreSheet = ReelViewMoreSheet.getInstance(reelsItem, position, new ReelViewMoreSheet.OnShareListener() {
                @Override
                public void onShareClicked() {
                    share(reelsItem);
                }

                @Override
                public void onFollow(ReelsItem mReelsItem) {
                    if (mReelsItem == null) return;
                    com.ziro.bullet.data.models.sources.Source source = mReelsItem.getSource();
                    if (source != null) {
                        reelsItem.getSource().setFavorite(source.isFavorite());
                        if (reelsItem.getSource().isFavorite()) {
//                            userFollowIcon.setVisibility(View.INVISIBLE);
//                            userFollowBtn.setEnabled(false);
                        } else {
//                            userFollowIcon.setVisibility(View.VISIBLE);
//                            userFollowBtn.setEnabled(true);
                        }
                    }
                }

                @Override
                public void viewMoreDismissed() {
                    Log.e(TAG, "viewMoreDismissed: ");
                    if (!isFinishing())
                        onResume();
                }
            });


            reelViewMoreSheet.show(getSupportFragmentManager(), reelsItem.getId());

//
//            reelViewMoreSheet.getViewLifecycleOwnerLiveData().observe(this, new Observer<LifecycleOwner>() {
//                @Override
//                public void onChanged(LifecycleOwner lifecycleOwner) {
//                    //when lifecycleOwner is null fragment is destroyed
//                    Log.e("rvm", "onChanged:A ");
//                    Constants.reelfragment = false;
//                    if (lifecycleOwner == null) {
//                        Constants.onResumeReels = true;
//                        isreelsheetopend = false;
//                        onResume();
//                    }
//                }
//            });
        }
    }

    public void forYouBottomSheet() {
        if (homeModel == null || homeModel.getData().isEmpty()) {
//            Utils.showSnacky(ReelInnerActivity.this, "Loading Categories...");
            return;
        }

        Constants.onResumeReels = false;

        onPause();

        forYouReelSheet = ForYouReelSheet.getInstance("reels", Constants.CAT_TYPE_REELS, context, homeModel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }, new BottomSheetInterface() {
            @Override
            public void onForYouSelect() {
                if (forYouReelSheet != null) {
                    forYouReelSheet.dismiss();
                }
            }

            @Override
            public void onFollowingSelect() {
                if (forYouReelSheet != null) {
                    forYouReelSheet.dismiss();
                }
            }

            @Override
            public void onHomeTab(DataItem item) {
                context = item.getId();

                if (forYouReelSheet != null) {
                    forYouReelSheet.dismiss();
                }
                tvLabel.setText(title);

                refresh.setRefreshing(true);
                reload();

            }

            @Override
            public void onSearch() {
                if (goHome != null && forYouReelSheet != null) {
                    goHome.sendAudioEvent("search");
                    forYouReelSheet.dismiss();
                }
            }

            @Override
            public void onSequenceUpdated(ArrayList<DataItem> mCategoriesList) {

            }

            @Override
            public void updateTabs(ArrayList<DataItem> mCategoriesList) {

            }

            @Override
            public void dialogDismissListener() {

            }
        });

        forYouReelSheet.show(getSupportFragmentManager(), "id");

//        forYouReelSheet.getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> {
//            //when lifecycleOwner is null fragment is destroyed
//            if (lifecycleOwner == null) {
//                Log.e(TAG, "forYouBottomSheet: destroy" );
////                onDestroy();
//            }
//        });
    }


    private void showLoadingView(boolean isShow) {
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = new PictureLoadingDialog(this);
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }


    public void showNoDataErrorView(boolean show) {
        noRecordFoundContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void showErrorView(boolean show) {
        noRecordFoundContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void showEndOfReelMsg(boolean show) {
//        rvReelInfo.setVisibility(show ? View.GONE : View.VISIBLE);
//        llReelOptions.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void loaderShow(boolean flag) {
        progress_barreel.setVisibility(View.GONE);

        if (flag) isLoading = flag;

//        if (flag && refresh.isRefreshing()) {
//            viewPager.setUserInputEnabled(false);
//            if (currentFragment instanceof VideoInnerFragment) {
//                ((VideoInnerFragment) currentFragment).clickEnabling(false);
//            }
//        } else {
//            viewPager.setUserInputEnabled(true);
//            if (currentFragment instanceof VideoInnerFragment) {
//                ((VideoInnerFragment) currentFragment).clickEnabling(true);
//            }
//        }
//        if (mNext.equals("")) {
//        if (mNext != null && mNext.equals("") && !refresh.isRefreshing()) {
////        if (videoItems == null || videoItems.size() <= 0) {
//            Utils.loadSkeletonLoaderReels(reelsViewSwitcher, flag);
//        } else {
//            Utils.loadSkeletonLoaderReels(reelsViewSwitcher, false);
//        }

//        if (!flag) {
//            refresh.setRefreshing(false);
//        }
    }

    @Override
    public void error(String error) {
        mPage = "";
        showErrorView(true);
        progress_barreel.setVisibility(View.GONE);
        isLoading = false;
        Utils.loadSkeletonLoaderReels(reelsViewSwitcher, false);
        if (!TextUtils.isEmpty(error)) {
            if (error.equalsIgnoreCase("Canceled") || error.contains("reset") || error.contains("closed"))
                return;
            Utils.showSnacky(ReelInnerActivity.this.getWindow().getDecorView().getRootView(), "" + error);
        }
    }

    @Override
    public void success(ReelResponse reelResponse) {
        progress_barreel.setVisibility(View.GONE);
        isLoading = false;
        Constants.onResumeReels = true;

        if (isRefresh) {
            isRefresh = false;
//            videoItems.clear();

            viewPager.setAdapter(pagerAdapter);

//            pagerAdapter.notifyDataSetChanged();
        }
        refresh.setRefreshing(false);
        showNoDataErrorView(false);
        if (reelResponse != null && reelResponse.getReels() != null && reelResponse.getReels().size() > 0 && pagerAdapter != null) {
            if (videoItems.size() == 0) {
                videoItems.addAll(0, reelResponse.getReels());
                pagerAdapter.setVideoList((ArrayList<ReelsItem>) videoItems);
            } else if (!mNext.equals(reelResponse.getMeta().getNext())) {
                prevReelsize = pagerAdapter.getItemCount() /*videoReelsItems.size()*/;
                videoItems.addAll(prevReelsize, (reelResponse.getReels()));
                pagerAdapter.notifyItemRangeInserted(prevReelsize, reelResponse.getReels().size());
                prevReelsize = videoItems.size();
            }
        }
        if (reelResponse.getMeta() != null) {
            mNext = reelResponse.getMeta().getNext();
            if (TextUtils.isEmpty(mNext)) {
                isLastPage = true;
            }
        }

        if (reelResponse != null && !isLastPage) {
            if (reelResponse.getReels() != null) {
                if (reelResponse.getReels().size() < 5) {
                    reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, true, "");
                }
            }
        }

        if (videoItems.size() == 0) {
            showNoDataErrorView(true);
        }
    }

    @Override
    public void error404(String error) {
        showErrorView(true);
        isLoading = false;
        mPage = "";
        Utils.loadSkeletonLoaderReels(reelsViewSwitcher, false);
    }

    @Override
    public void success(ReelResponse reelResponse, boolean reload) {
        Log.e("TAGf", "success: " + curPosition);
        isLoading = false;
        Constants.onResumeReels = true;

        if (isRefresh) {
            videoItems.clear();
            viewPager.setAdapter(pagerAdapter);
            pagerAdapter.notifyDataSetChanged();
            isRefresh = false;
        }

        showNoDataErrorView(false);
        refresh.setRefreshing(false);

        if (reelResponse != null && reelResponse.getReels() != null && reelResponse.getReels().size() > 0 && pagerAdapter != null) {
            if (videoItems != null) {
                prevReelsize = pagerAdapter.getItemCount()/*videoReelsItems.size()*/;
                videoItems.addAll(prevReelsize, (reelResponse.getReels()));
                pagerAdapter.notifyItemRangeInserted(prevReelsize, reelResponse.getReels().size());
                prevReelsize = videoItems.size();
            } else if (mNext == null || !mNext.equals(reelResponse.getMeta().getNext())) {
                videoItems = (ArrayList<ReelsItem>) reelResponse.getReels();
                pagerAdapter.setVideoList((ArrayList<ReelsItem>) videoItems);
            }
        }

        if (reelResponse.getMeta() != null) {
            mNext = reelResponse.getMeta().getNext();
            if (TextUtils.isEmpty(mNext)) {
                isLastPage = true;
//                videoItems.add(null);
            }
        }

        if (reelResponse != null && !isLastPage) {
            if (reelResponse.getReels() != null) {
                if (reelResponse.getReels().size() < 5) {
                    reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, true, "");
                }
            }
        }

        if (videoItems.size() == 0) {
            showNoDataErrorView(true);
        }
    }

    @Override
    public void nextVideo(int position) {

    }

    public void reload() {
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(ReelInnerActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();

            refresh.setRefreshing(false);
            return;
        }

//        if (!isLoading) {
        if (pagerAdapter != null && reelPresenter != null) {
            if (videoItems != null && videoItems != null) {
                onPause();
            }

//            refresh.setRefreshing(true);
//            currentAdapterPosition = viewPager.getCurrentItem();
//            viewPager.setCurrentItem(0, true);


            viewPager.setAdapter(null);
//            viewPager.removeAllViews();
            reelsToSave.clear();
            cachePosition = -1;
            maxCacheLimit = -1;
            isCaching = false;
            mNext = "";
            PRDownloader.cancelAll();
            pagerAdapter.notifyDataSetChanged();
            isRefresh = true;
            mNext = "";
            mPage = "";
            new_post.setVisibility(View.GONE);
            reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, false, "");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REELS_VERSION) {
            if (resultCode == Activity.RESULT_OK) {
                Constants.onResumeReels = false;
                reload();
            }
        } else if (newsPresenter != null && data != null && data.hasExtra("id")) {
            String id = data.getStringExtra("id");
        }
    }

    @Override
    public void onDestroy() {
        Log.e("testreel", "onDestroy ");
        super.onDestroy();
        pagerAdapter.releasePlayers();

    }

    @Override
    public void onPause() {
        Log.e("testreel", "onPause: " + curPosition);
        Constants.onResumeReels = false;
        super.onPause();
        pagerAdapter.pauseCurPlayback(curPosition);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("testreel", "onResume: " + curPosition);
//        pagerAdapter.resumePlayback(curPosition);
//        if (Constants.onResumeReels) {
//            {
//                if(reelViewMoreSheet == null){
//                    pagerAdapter.resumePlayback(curPosition);
//                }else if(reelViewMoreSheet != null && !reelViewMoreSheet.isVisible()){
//                    pagerAdapter.resumePlayback(curPosition);
//                }
//
//            }}
        pagerAdapter.resumePlayback(curPosition);
//        if((reelViewMoreSheet == null || !reelViewMoreSheet.isVisible()) && (shareBottomSheet == null || !shareBottomSheet.isShowing() )){
//            pagerAdapter.resumePlayback(curPosition);
//        }
    }

    @Override
    public void videoCached(ReelsItem reelsItem, int position) {
        this.cachePosition = position;
    }

    private Article getArticleFromReels(ReelsItem reels) {
        Article article = new Article();
        if (reels != null) {
            article.setId(reels.getId());
            article.setTitle(reels.getDescription());
            article.setLink(reels.getMedia());
            article.setPublishTime(reels.getPublishTime());
            article.setType("REELS");
            article.setSource(reels.getSource());
            article.setAuthor(reels.getAuthor());
            MediaMeta mediaMeta = new MediaMeta();
            if (reels.getMediaMeta() != null) {
                mediaMeta.setDuration(reels.getMediaMeta().getDuration());
                mediaMeta.setHeight(reels.getMediaMeta().getHeight());
                mediaMeta.setWidth(reels.getMediaMeta().getWidth());
            }
            article.setMediaMeta(mediaMeta);
            ArrayList<Bullet> bullets = new ArrayList<>();
            Bullet bullet = new Bullet();
            bullet.setImage(reels.getImage());
            bullet.setData(reels.getDescription());
            bullets.add(bullet);
            article.setBullets(bullets);
            article.setSelected(true);
        }
        return article;
    }

    private void updateCaption() {
        if (prefConfig.isReelsCaption()) {
            if (exoPlayer != null) {
//                getCaption(player.getCurrentPosition()); caption to be added when required
            }
        } else {
//            removeCaptions(true);
        }
    }

    @Override
    public void dotsCkickOpen(@NonNull ReelsItem reelsItem) {
        if (shareBottomSheetPresenter != null) {
            shareBottomSheetPresenter.share_msg(getArticleFromReels(reelsItem).getId(), new ShareInfoInterface() {
                @Override
                public void response(ShareInfo shareInfo) {
                    onPause();
                    if (shareBottomSheet == null) {
                        shareBottomSheet = new ShareBottomSheet(ReelInnerActivity.this, new ShareToMainInterface() {
                            @Override
                            public void removeItem(String id, int position) {

                            }

                            @Override
                            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

                            }

                            @Override
                            public void unarchived() {
                                // for updating caption
                                updateCaption();
                            }
                        }, true, "REEL_MAIN");
                    }

                    shareBottomSheet.show(getArticleFromReels(reelsItem), new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Constants.onResumeReels = true;
                            Constants.sharePgNotVisible = true;
                            onResume();
                        }
                    }, shareInfo);
                }

                @Override
                public void error(String error) {

                }
            });
        }
    }


    @Override
    public void loadingReelInfo(boolean isLoading) {

    }

    @Override
    public void errorr(@NonNull String error, @NonNull String topic) {

    }

    @Override
    public void likeicon(@NonNull ReelsItem reelsItem) {
        Map<String, String> params = new HashMap<>();
        params.put(Events.KEYS.REEL_ID, reelsItem.getId());
        AnalyticsEvents.INSTANCE.logEvent(this, params, Events.REELS_LIKE);
    }

    @Override
    public void commentsclick(@NonNull ReelsItem reelsItem) {
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(ReelInnerActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put(Events.KEYS.REEL_ID, reelsItem.getId());
        AnalyticsEvents.INSTANCE.logEvent(this,
                params,
                Events.REELS_COMMENT);
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtra("article_id", reelsItem.getId());
        intent.putExtra("position", mCurrentPosition);
        startActivityForResult(intent, Constants.CommentsRequestCode);
    }

    @Override
    public void openReelviewmore(@NonNull ReelsItem reelsItem) {
        if (clickable) {
            onPause();
            if (reelsItem != null) {
                if (reelViewMoreSheet != null) {
                    if (!reelViewMoreSheet.isVisible()) {
                        showBottomSheet(reelsItem, mCurrentPosition);
                    }
                } else {
                    showBottomSheet(reelsItem, mCurrentPosition);
                }
            }
        }
    }

    @Override
    public void doubleClickLike(@NonNull ReelsItem reelsItem) {

    }

    @Override
    public void share(@NonNull ReelsItem reelsItem) {
        if (!InternetCheckHelper.isConnected()) {

            Toast.makeText(ReelInnerActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();

            return;
        }
        if (shareBottomSheetPresenter != null) {
            final boolean[] isIgnoreDismiss = {false};
            showLoadingView(true);
            onPause();
            shareBottomSheetPresenter.share_msg(reelsItem.getId(), new ShareInfoInterface() {
                @Override
                public void response(ShareInfo shareInfo) {
                    showLoadingView(false);
                    onPause();
//                        share_icon.setVisibility(View.VISIBLE);
                    if (shareInfo == null) {
                        return;
                    }

                    AnalyticsEvents.INSTANCE.logEvent(ReelInnerActivity.this, Events.REEL_SHARE_CLICK);

                    Article article = new Article();
                    if (reelsItem != null) {
                        MediaMeta mediaMeta = reelsItem.getMediaMeta();
                        article.setMediaMeta(mediaMeta);
                    }
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    if (shareInfo != null) {
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
                    }
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    ReelInnerActivity.this.startActivity(shareIntent);

//                    reelsBottomSheet = new MediaShare.Builder(getActivity())
//                            .setId(reelsItem.getId())
//                            .isArticle(false)
//                            .setShareInfo(shareInfo)
//                            .setFragmentContextVal(VideoInnerFragment.this)
//                            .setArticle(article)
//                            .setonDismissListener(dialog -> {
//                                if (!isIgnoreDismiss[0])
//                                    onResume();
//                            })
//                            .setReelBottomSheetCallback(new ReelBottomSheetCallback() {
//                                @Override
//                                public void onReport() {
//                                    isIgnoreDismiss[0] = true;
//                                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
//                                            Events.REPORT_CLICK);
//                                    if (reelsItem == null) {
//                                        return;
//                                    }
//                                    ReportBottomSheet reportBottomSheet = new ReportBottomSheet(getActivity(), new DismissBottomSheet() {
//                                        @Override
//                                        public void dismiss(boolean flag) {
//                                            if (flag) {
//                                                // on hide Bottom sheet
//                                                onResume();
//                                            }
//                                        }
//                                    });
//                                    reportBottomSheet.show(reelsItem.getId(), "articles");
//                                }
//
//                                @Override
//                                public void onSave    () {
//                                    if (!TextUtils.isEmpty(reelsItem.getId())) {
//                                        if (sharePresenter != null)
//                                            sharePresenter.archive(reelsItem.getId(), shareInfo.isArticle_archived());
//                                    }
//                                    onResume();
//                                }
//
//                                @Override
//                                public void onNotInterested() {
//                                    if (!TextUtils.isEmpty(reelsItem.getId())) {
//                                        if (sharePresenter != null)
//                                            sharePresenter.less(reelsItem.getId());
//                                    }
//                                    onResume();
//                                }
//
//                                @Override
//                                public void onIgnore() {
//                                    isIgnoreDismiss[0] = true;
//                                }
//                            })
//                            .build();
//                    reelsBottomSheet.show();

                }

                @Override
                public void error(String error) {
                    showLoadingView(false);
                    onResume();
                }
            });
        }
    }

    @Override
    public void followChannelClick(@NonNull ReelsItem reelsItem) {
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(ReelInnerActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (reelsItem.getSource() != null) {
            if (reelsItem.getSource().isFavorite()) {
                followUnfollowPresenter.unFollowSource(reelsItem.getSource().getId(), 0, null);
                reelsItem.getSource().setFavorite(false);
            } else {
                followUnfollowPresenter.followSource(reelsItem.getSource().getId(), 0, null);
                reelsItem.getSource().setFavorite(true);
            }
        }
    }

    @Override
    public void userIconClick(@NonNull ReelsItem reelsItem) {
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(ReelInnerActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();

            return;
        }

        Intent intent;
        if (reelsItem.getSource() != null) {
            intent = new Intent(this, ChannelDetailsActivity.class);
            intent.putExtra("id", reelsItem.getSource().getId());
        } else {
            User user = new PrefConfig(this).isUserObject();
            if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(reelsItem.getAuthor().get(0).getId())) {
                intent = new Intent(this, ProfileActivity.class);
            } else {
                intent = new Intent(this, AuthorActivity.class);
            }
            intent.putExtra("authorID", reelsItem.getAuthor().get(0).getId());
            intent.putExtra("authorContext", reelsItem.getAuthor().get(0).getContext());
        }
        startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
    }

    @Override
    public void nextReelVideo(int pos) {
        Log.d("NextVideo_TAG", "nextVideo: " + pos);
        if (pos > -1) {
            int next = pos;
            next++;
            if (next < videoItems.size()) {
                viewPager.setCurrentItem(next, true);
            }
        }
    }

    @Override
    public void updateView() {

        if (reelViewMoreSheet != null && reelViewMoreSheet.isVisible()) {
            Log.e(TAG, "updateView: ");
            pagerAdapter.pauseCurPlayback(curPosition);
        }
    }
}