package com.ziro.bullet.activities;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.background.BroadcastEmitter.BG_ERROR;
import static com.ziro.bullet.background.BroadcastEmitter.BG_PUBLISHED;
import static com.ziro.bullet.background.BroadcastEmitter.TYPE_BACKGROUND_PROCESS;
import static com.ziro.bullet.utills.Constants.ACTION_UPDATE_EVENT;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.schedule.ArticleViewHolder;
import com.ziro.bullet.adapters.schedule.ReelViewHolder;
import com.ziro.bullet.adapters.schedule.ScheduledListAdapter;
import com.ziro.bullet.adapters.schedule.VideoViewHolder;
import com.ziro.bullet.adapters.schedule.YoutubeViewHolder;
import com.ziro.bullet.background.BackgroundEvent;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.bottomSheet.PostArticleBottomSheet;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.fragments.CommentPopup;
import com.ziro.bullet.fragments.CreateYoutubePopup;
import com.ziro.bullet.fragments.GuidelinePopup;
import com.ziro.bullet.fragments.LoadingDialog;
import com.ziro.bullet.fragments.SchedulePickerDialog;
import com.ziro.bullet.interfaces.ApiResponseInterface;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.ScheduleTimerFinishListener;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.ImageCrop;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.presenter.MenuFragmentPresenter;
import com.ziro.bullet.presenter.SchedulePresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import im.ene.toro.PlayerSelector;
import im.ene.toro.widget.Container;

public class ScheduleListingActivity extends BaseActivity implements SchedulePresenter.ScheduledCallback, NewsCallback, ScheduleTimerFinishListener {
    public static final int PERMISSION_REQUEST_REELS = 745;
    public static final int PERMISSION_REQUEST = 12;
    public static final String SOURCE_ID = "source_id";
    private static final String TAG = "ScheduleListingActivity";
    private static final int NEW_POST_REQUEST = 2434;
    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private Container mRvScheduledPosts;
    private ConstraintLayout mPostArticleBtn;
    private SchedulePresenter schedulePresenter;
    private String mNextPage = "";
    private LoadingDialog loadingDialog;
    private ArrayList<Article> mPostsList = new ArrayList<>();
    private ScheduledListAdapter mAdapter;
    private SpeedyLinearLayoutManager speedyLinearLayoutManager;
    private int mArticlePosition = 0;
    private PrefConfig mPrefConfig;
    private MenuFragmentPresenter presenter;
    private PostArticleBottomSheet postArticleBottomSheet = null;
    private boolean isArticle = false;
    private CommentPopup commentPopup;
    private String schedule;
    private boolean isLoading = false;

    private LinearLayout llEmpty;
    RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {


        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            Log.d(TAG, "onItemRangeRemoved() called with: positionStart = [" + positionStart + "], itemCount = [" + itemCount + "]");
            if (mPostsList.size() > 0) {
                mPostArticleBtn.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
                mRvScheduledPosts.setVisibility(View.VISIBLE);
            } else {
                mPostArticleBtn.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
                mRvScheduledPosts.setVisibility(View.GONE);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            if (mPostsList.size() > 0) {
                mPostArticleBtn.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
                mRvScheduledPosts.setVisibility(View.VISIBLE);
            } else {
                mPostArticleBtn.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
                mRvScheduledPosts.setVisibility(View.GONE);
            }
        }


        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged: "+mPostsList.size());
            super.onChanged();
            if (mPostsList.size() > 0) {
                mPostArticleBtn.setVisibility(View.VISIBLE);
                llEmpty.setVisibility(View.GONE);
                mRvScheduledPosts.setVisibility(View.VISIBLE);
            } else {
                mPostArticleBtn.setVisibility(View.GONE);
                llEmpty.setVisibility(View.VISIBLE);
                mRvScheduledPosts.setVisibility(View.GONE);
            }
        }
    };
    private ConstraintLayout rlSchedule;
    private RelativeLayout btnBack;

    private String sourceId = "";

    private BroadcastReceiver updateEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("method");
            if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase("update")) {
                updateArticles();
            }
        }
    };
    private PictureLoadingDialog mIndefiniteLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_schedule_listing);
        if (getIntent().hasExtra(SOURCE_ID)) {
            sourceId = getIntent().getStringExtra(SOURCE_ID);
        }
        bindViews();
        init();
        LocalBroadcastManager.getInstance(this).registerReceiver(updateEvent, new IntentFilter(ACTION_UPDATE_EVENT));
        EventBus.getDefault().register(this);
    }

    public void updateArticles() {
        if (presenter == null) return;
        mNextPage = "";
        mPostsList.clear();
        getScheduledPosts();
    }

    private void bindViews() {
        mRvScheduledPosts = findViewById(R.id.rvSchedule);
        mPostArticleBtn = findViewById(R.id.post_article_btn);
        llEmpty = findViewById(R.id.llEmpty);
        rlSchedule = findViewById(R.id.done);
        btnBack = findViewById(R.id.ivBack);
    }

    private void init() {
        schedulePresenter = new SchedulePresenter(this);
        presenter = new MenuFragmentPresenter(this);
        mPrefConfig = new PrefConfig(this);

        btnBack.setOnClickListener(v -> onBackPressed());

        mAdapter = new ScheduledListAdapter(this, ScheduledListAdapter.TYPE.SCHEDULE, this, mPostsList, this, new DetailsActivityInterface() {
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
        mAdapter.registerAdapterDataObserver(adapterDataObserver);

        speedyLinearLayoutManager = new SpeedyLinearLayoutManager(this);
        mRvScheduledPosts.setLayoutManager(speedyLinearLayoutManager);
        mRvScheduledPosts.setAdapter(mAdapter);
        mRvScheduledPosts.setPlayerSelector(selector);


        mRvScheduledPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == SCROLL_STATE_IDLE) {
                    Constants.auto_scroll = true;

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mRvScheduledPosts.getLayoutManager());

                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();

                    if (firstPosition != -1) {

                        Rect rvRect = new Rect();
                        mRvScheduledPosts.getGlobalVisibleRect(rvRect);

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

                                if (mAdapter != null)
                                    mAdapter.notifyDataSetChanged();
                                Log.d("youtubePlayer", "notify onScrollStateChanged");
//                                }
                            } else if (mArticlePosition == mPostsList.size() - 1) {
                                Log.d("slections", "onScrollStateChanged: last = 0");

                                //on fast scrolling select the last one in the last
                                selectCardPosition(mArticlePosition);

                                if (mAdapter != null)
                                    mAdapter.notifyDataSetChanged();
                                Log.d("youtubePlayer", "notify onScrollStateChanged");
                            } else if (copyOfmArticlePosition == mArticlePosition) {
                                Log.d("slections", "onScrollStateChanged: copy = new pos");
                                //scroll rested on same article so resume audio and bullet
                                try {
                                    RecyclerView.ViewHolder holder = mRvScheduledPosts.findViewHolderForAdapterPosition(mArticlePosition);
                                    if (holder != null) {
                                        if (holder instanceof LargeCardViewHolder) {
                                            ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                        } else if (holder instanceof SmallCardViewHolder) {
                                            ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (copyOfmArticlePosition != mArticlePosition) {
                                    Log.d("slections", "onScrollStateChanged: select new");
                                    //scrolled to a new pos, so select new article
                                    selectCardPosition(mArticlePosition);

                                    if (mAdapter != null)
                                        mAdapter.notifyDataSetChanged();
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

                                if (mAdapter != null)
                                    mAdapter.notifyDataSetChanged();
                                Log.d("youtubePlayer", "notify onScrollStateChanged");
                            } else {
                                Log.d("slections", "onScrollStateChanged: else");
                                //scroll rested on same article so resume audio and bullet
                                try {
                                    RecyclerView.ViewHolder holder = mRvScheduledPosts.findViewHolderForAdapterPosition(mArticlePosition);
                                    if (holder != null) {
                                        if (holder instanceof LargeCardViewHolder) {
                                            ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                        } else if (holder instanceof SmallCardViewHolder) {
                                            ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                        }
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

                if (mPostsList.size() - 3 <= speedyLinearLayoutManager.findLastVisibleItemPosition() && !TextUtils.isEmpty(mNextPage)) {
                    getScheduledPosts();
                }
            }
        });


        getScheduledPosts();

        mPostArticleBtn.setOnClickListener(v -> {
            if (mPrefConfig == null) {
                return;
            }
            if (mPrefConfig.isGuestUser()) {
                LoginPopupActivity.start(ScheduleListingActivity.this);
            } else {
                SchedulePickerDialog schedulePickerDialog = new SchedulePickerDialog(ScheduleListingActivity.this);
                schedulePickerDialog.showDialog(new SchedulePickerDialog.ScheduleSelectionCallback() {
                    @Override
                    public void onScheduleSelected(Calendar selectedCalendar) {
                        schedule = Utils.convertLocalCalendarToUTC(selectedCalendar);
                        showPostPopup();
                    }

                    @Override
                    public void onCleared() {

                    }
                }, false);
            }
        });

        rlSchedule.setOnClickListener(v -> {
            if (mPrefConfig == null) {
                return;
            }
            if (mPrefConfig.isGuestUser()) {
                LoginPopupActivity.start(ScheduleListingActivity.this);
            } else {
                SchedulePickerDialog schedulePickerDialog = new SchedulePickerDialog(ScheduleListingActivity.this);
                schedulePickerDialog.showDialog(new SchedulePickerDialog.ScheduleSelectionCallback() {
                    @Override
                    public void onScheduleSelected(Calendar selectedCalendar) {
                        schedule = Utils.convertLocalCalendarToUTC(selectedCalendar);
                        showPostPopup();
                    }

                    @Override
                    public void onCleared() {

                    }
                }, false);
            }
        });
    }

    private void getScheduledPosts() {
        if (!isLoading)
            schedulePresenter.getScheduledPosts(sourceId, mNextPage);
    }

    private void showPostPopup() {
        //if (mPrefConfig != null) {
        if (mPrefConfig != null && mPrefConfig.isUserObject() != null && mPrefConfig.isUserObject().isSetup()) {
            if (!mPrefConfig.getGuidelines()) {
                GuidelinePopup popup = new GuidelinePopup(ScheduleListingActivity.this, (flag, msg) -> {
                    if (flag) {
                        if (presenter != null) {
                            presenter.acceptGuidelines(new ApiResponseInterface() {
                                @Override
                                public void _success() {
                                    if (mPrefConfig != null)
                                        mPrefConfig.setGuidelines(true);
                                    if (postArticleBottomSheet == null) {
                                        postArticleBottomSheet = new PostArticleBottomSheet(ScheduleListingActivity.this, option -> {
                                            switch (option) {
                                                case "YOUTUBE":
                                                    showYoutubeUploadView();
                                                    break;
                                                case "REELS":
                                                    postReels();
                                                    break;
                                                case "MEDIA":
                                                    postMedia();
                                                    break;
                                            }
                                        });
                                    }
                                    postArticleBottomSheet.show(dialog -> {

                                    });
                                }

                                @Override
                                public void _other(int code) {

                                }
                            });
                        }
                    }
                });
                popup.showDialog();
            } else {
                if (postArticleBottomSheet == null) {
                    postArticleBottomSheet = new PostArticleBottomSheet(ScheduleListingActivity.this, option -> {
                        switch (option) {
                            case "YOUTUBE":
                                showYoutubeUploadView();
                                break;
                            case "REELS":
//                                        if (!checkPermissions(PERMISSIONS) && getActivity() != null) {
//                                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_REQUEST_REELS);
//                                            error(getActivity().getString(R.string.permision));
//                                            return;
//                                        }
//                                        Reels();
                                postReels();
                                break;
                            case "MEDIA":
//                                        if (!checkPermissions(PERMISSIONS) && getActivity() != null) {
//                                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_REQUEST_MEDIA);
//                                            error(getActivity().getString(R.string.permision));
//                                            return;
//                                        }
                                postMedia();
                                break;
                        }
                    });
                }
                postArticleBottomSheet.show(dialog -> {

                });
            }
        } else {
            if (commentPopup == null) {
                commentPopup = new CommentPopup(ScheduleListingActivity.this);
            } else {
                commentPopup.showDialog(getString(R.string.before_you_can_post_you_need_to_update_your_profile));
            }
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
        showLoadingView(flag);
    }

    @Override
    public void error(String error) {
        Utils.showSnacky(getWindow().getDecorView().getRootView(), "" + error);
    }

    @Override
    public void error404(String error) {
        Utils.showSnacky(getWindow().getDecorView().getRootView(), "" + error);
    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {

    }

    @Override
    public void success(ArticleResponse response) {
        if (response != null && response.getMeta() != null) {
            mNextPage = response.getMeta().getNext();
        }

        if (response != null && response.getArticles() != null && response.getArticles().size() > 0) {

            mPostsList.addAll(response.getArticles());

            selectFirstItemOnCardViewMode();
        }

        if (mPostsList.size() > 0) {
            mPostArticleBtn.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
            mRvScheduledPosts.setVisibility(View.VISIBLE);
        } else {
            mPostArticleBtn.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
            mRvScheduledPosts.setVisibility(View.GONE);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
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
        if (mPostsList.size() > 0 && position < mPostsList.size() && position > -1) {
            mArticlePosition = position;
            selectCardPosition(mArticlePosition);
            if (speedyLinearLayoutManager != null)
                speedyLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {
//        if (mPostsList.size() > 0 && position < mPostsList.size() && position > -1) {
//            mArticlePosition = position;
//            selectCardPosition(mArticlePosition);
//            if (shouldNotify && mAdapter != null)
//                mAdapter.notifyDataSetChanged();
//
//        }
    }

    @Override
    public void onItemHeightMeasured(int height) {
        ViewTreeObserver viewTreeObserver = mRvScheduledPosts.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mRvScheduledPosts != null) {
                        mRvScheduledPosts.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int viewHeight = mRvScheduledPosts.getHeight();

                        LinearLayoutManager layoutManager = ((LinearLayoutManager) mRvScheduledPosts.getLayoutManager());
                        mRvScheduledPosts.setPadding(0, 0, 0, viewHeight - height);
                    }
                }
            });
        }
    }

    public void selectCardPosition(int position) {
        if (position > -1) {
            mArticlePosition = position;
            if (mAdapter != null)
                mAdapter.setCurrentArticlePosition(mArticlePosition);
            if (mPostsList.size() > 0 && position < mPostsList.size()) {
                for (int i = 0; i < mPostsList.size(); i++) {
                    mPostsList.get(i).setSelected(false);
                }
                mPostsList.get(position).setSelected(true);
            }
        }
    }

    public void selectFirstItemOnCardViewMode() {
        selectCardPosition(mArticlePosition);
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

    private void showYoutubeUploadView() {
        if (!isFinishing()) {
            CreateYoutubePopup createYoutubePopup = new CreateYoutubePopup(ScheduleListingActivity.this);
            createYoutubePopup.showDialog(article -> {
                if (article != null) {
                    Log.e("uploadImageVideo", "-createSuccess here-> " + new Gson().toJson(article));
//                    if (postArticleParams != null) {
//                        postArticleParams.setId(article.getId());
//                    }
                    Intent intent = new Intent(ScheduleListingActivity.this, PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.YOUTUBE);
                    intent.putExtra("MODE", MODE.ADD);
                    intent.putExtra("SCHEDULE", schedule);
                    intent.putExtra("article", new Gson().toJson(article));
                    startActivityForResult(intent, NEW_POST_REQUEST);
                }
            });
        }
    }


    private void postReels() {
        if (PermissionChecker
                .checkSelfPermission(ScheduleListingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(ScheduleListingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryOnlyVideo(this, true, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST_REELS);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    private void postMedia() {
        isArticle = true;

        if (PermissionChecker
                .checkSelfPermission(ScheduleListingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(ScheduleListingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryWithAll(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>());
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
//        fromGallery(isVideoArticle);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isArticle)
                    postMedia();
                else postReels();
            } else {
                Utils.showSnacky(getWindow().getDecorView().getRootView(), "" + getString(R.string.permision));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode);
        if (requestCode == PERMISSION_REQUEST_REELS) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() == 0) {
                    VideoInfo videoInfo = data.getParcelableExtra("video_info");

                    if (isFinishing()) return;
                    Intent intent = new Intent(this, PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.REELS);
                    intent.putExtra("MODE", MODE.ADD);

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
//                        intent.putExtra("uri", localMedia.getAndroidQToPath());
//                    } else {
                    intent.putExtra("file", new File(videoInfo.getPath()));
                    intent.putExtra("uri", videoInfo.getPath());
//                    }
                    intent.putExtra("video_info", videoInfo);
                    intent.putExtra("SCHEDULE", schedule);
                    startActivity(intent);
                } else
                    loadSelectedVideo(selectList.get(0));
            }
        } else if (requestCode == NEW_POST_REQUEST) {
//            updateArticles();
        } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() == 0) {
                    VideoInfo videoInfo = data.getParcelableExtra("video_info");

                    if (isFinishing()) return;
                    Intent intent = new Intent(this, PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
                    intent.putExtra("MODE", MODE.ADD);

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
//                        intent.putExtra("uri", localMedia.getAndroidQToPath());
//                    } else {
                    intent.putExtra("file", new File(videoInfo.getPath()));
                    intent.putExtra("uri", videoInfo.getPath());
//                    }
                    intent.putExtra("SCHEDULE", schedule);

                    intent.putExtra("video_info", videoInfo);
                    startActivity(intent);
                } else {
                    loadSelectedImage(selectList.get(0));
                }
            }
        } else if (requestCode == ImageCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                if (isFinishing()) return;
                Uri resultUri = ImageCrop.getOutput(data);
                String cutPath = resultUri.getPath();

                Intent intent = new Intent(this, PostArticleActivity.class);
//                if (localMedia.getMimeType().contains("video")) {
//                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
//                } else {
                intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
//                }
                intent.putExtra("MODE", MODE.ADD);
                intent.putExtra("file", new File(cutPath));
                intent.putExtra("uri", cutPath);
                intent.putExtra("SCHEDULE", schedule);

//                intent.putExtra("localMedia", new Gson().toJson(localMedia));
                startActivity(intent);
            }
        }
    }


    private void loadSelectedVideo(LocalMedia localMedia) {
        Intent intent = new Intent(ScheduleListingActivity.this, PostArticleActivity.class);
        intent.putExtra("POST_TYPE", POST_TYPE.REELS);
        intent.putExtra("MODE", MODE.ADD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
            intent.putExtra("uri", localMedia.getAndroidQToPath());
        } else {
            intent.putExtra("file", new File(String.valueOf(localMedia.getPath())));
            intent.putExtra("uri", localMedia.getPath());
        }
        intent.putExtra("SCHEDULE", schedule);
        intent.putExtra("localMedia", new Gson().toJson(localMedia));
        startActivityForResult(intent, NEW_POST_REQUEST);
    }

    private void loadSelectedImage(LocalMedia localMedia) {
        if (!localMedia.getMimeType().contains("video")) {
            File f = new File(getCacheDir(), localMedia.getFileName() + System.currentTimeMillis());
            try {
                f.createNewFile();
                Uri url;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    url = Uri.fromFile(new File((localMedia.getAndroidQToPath())));
                } else {
                    url = Uri.fromFile(new File((localMedia.getPath())));
                }
                ImageCrop.of(url, Uri.parse(f.getPath()))
                        .start(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(ScheduleListingActivity.this, PostArticleActivity.class);
            if (localMedia.getMimeType().contains("video")) {
                intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
            } else {
                intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
            }
            intent.putExtra("MODE", MODE.ADD);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
                intent.putExtra("uri", localMedia.getAndroidQToPath());
            } else {
                intent.putExtra("file", new File(String.valueOf(localMedia.getPath())));
                intent.putExtra("uri", localMedia.getPath());
            }
            intent.putExtra("SCHEDULE", schedule);
            intent.putExtra("localMedia", new Gson().toJson(localMedia));
            startActivityForResult(intent, NEW_POST_REQUEST);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BackgroundEvent event) {
        if (event.getType() == TYPE_BACKGROUND_PROCESS) {
            if (event.getData().equals(BG_PUBLISHED)) {
                updateArticles();
                Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.posted_successfully));
            }else if (event.getData().equals(BG_ERROR)) {
                Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.post_failed));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateEvent);

        try {
            View child;
            for (int i = 0; i < mRvScheduledPosts.getChildCount(); i++) {
                child = mRvScheduledPosts.getChildAt(i);
                //In case you need to access ViewHolder:
                RecyclerView.ViewHolder holder = mRvScheduledPosts.getChildViewHolder(child);
                if (holder instanceof VideoViewHolder) {
                    ((VideoViewHolder) holder).destroy();
                } else if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).destroy();
                } else if (holder instanceof ReelViewHolder) {
                    ((ReelViewHolder) holder).destroy();
                } else if (holder instanceof ArticleViewHolder) {
                    ((ArticleViewHolder) holder).destroy();
                }
            }
            if (mAdapter.hasObservers())
                mAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void removeArticle(int position) {
        new Handler().postDelayed(() -> {
            if (mAdapter != null) {
                if (position < mPostsList.size()) {
                    mPostsList.remove(position);
                }
                mAdapter.notifyDataSetChanged();
            }
        }, 4000);
    }

    /**
     * loading dialog
     */
    protected void showIndefiniteProgressDialog() {
        try {
            if (mIndefiniteLoadingDialog == null) {
                mIndefiniteLoadingDialog = new PictureLoadingDialog(this);
            }
            if (mIndefiniteLoadingDialog.isShowing()) {
                mIndefiniteLoadingDialog.dismiss();
            }
            mIndefiniteLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissIndefiniteProgressDialog() {
        try {
            if (mIndefiniteLoadingDialog != null
                    && mIndefiniteLoadingDialog.isShowing()) {
                mIndefiniteLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            mIndefiniteLoadingDialog = null;
            e.printStackTrace();
        }
    }
}