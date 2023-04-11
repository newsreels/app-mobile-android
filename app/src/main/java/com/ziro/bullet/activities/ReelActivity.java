package com.ziro.bullet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.downloader.PRDownloader;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.VideoPagerAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.fragments.VideoInnerFragment;
import com.ziro.bullet.interfaces.StudioCallback;
import com.ziro.bullet.interfaces.VideoInterface;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.presenter.ReelPresenter;
import com.ziro.bullet.presenter.StudioPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.M3UParser;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReelActivity extends BaseActivity implements VideoInterface, StudioCallback, M3UParser.VideoCacheListener {

    public static final String REEL_AUTHOR_ID = "authorID";
    public static final String REEL_CONTEXT = "context";
    public static final String REEL_HASHTAG = "hashtag";
    public static final String REEL_TITLE = "title";
    public static final String REEL_MODE = "mode";
    public static final String REEL_POSITION = "position";
    public static final String REEL_PAGE = "page";
    public static final String API_CALL = "apicall";
    public static final String REEL_TYPE = "type";
    public static final String REEL_DATALIST = "datalist";
    public static final String REEL_SOURCE_ID = "sourceID";
    public static final String REELS_DIRECTORY = "reels_temp";
    public static final String HLS_DIRECTORY = "hls_temp";
    private static final int RESULT_INTENT_SETTING_CHANGES = 3211;
    //caching variables//
    private static final String HLS_DIR2 = "hls_files2";
    private static final String VIDEOS_DIR2 = "NewsReels2";
    private ProgressBar progress_barreel;
    private DbHandler cacheManager;
    private M3UParser m3UParser;
    private int cachePosition = -1, maxCacheLimit = -1;
    private List<ReelsItem> reelsToSave = new ArrayList<>();
    private boolean isCaching = false;
    //end//
    private ViewPager2 viewPager;
    private List<ReelsItem> videoItems = new ArrayList<>();
    private ReelPresenter presenter;
    private String mType = "";
    private String mNext = "";
    private RelativeLayout back;
    private TextView mTitle;
    private PrefConfig prefConfig;
    private String context = null;
    private String hashtag = null;
    private String authorID = null;
    private String sourceID = null;
    private String title = null;
    private Boolean apicall = false;
    private String mode = null;
    private int reelPosition = -1;
    private StudioPresenter studioPresenter;
    private SwipeRefreshLayout refresh;
    private boolean isRefresh = false;
    private VideoPagerAdapter pagerAdapter;
    private FragmentManager fm = null;
    private Fragment currentPage = null;
    private LinearLayoutManager manager;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int mCurrentPosition = -1;
    private NewsPresenter newsPresenter;
    private ViewSwitcher reelsViewSwitcher;
    private boolean isActive = true;
    private LinearLayout noRecordFoundContainer;
    private boolean noSkeletonView;

    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.HomeSelectedFragment != Constants.BOTTOM_TAB_VIDEO) {
            Constants.onResumeReels = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new Components().blackStatusBar(this, "black");
        setContentView(R.layout.activity_reel);

        Constants.onResumeReels = true;

        prefConfig = new PrefConfig(this);
        newsPresenter = new NewsPresenter(this, null);
        getBundle();
        bindViews();
        init();
    }

    //volume btn handle
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (currentPage == null && fm != null)
                currentPage = fm.findFragmentByTag("f" + mCurrentPosition);
            if (currentPage != null && currentPage instanceof VideoInnerFragment) {
                ((VideoInnerFragment) currentPage).onMyKeyUp(keyCode, event);
            }
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (currentPage == null && fm != null)
                currentPage = fm.findFragmentByTag("f" + mCurrentPosition);
            if (currentPage != null && currentPage instanceof VideoInnerFragment) {
                ((VideoInnerFragment) currentPage).onMyKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (currentPage == null && fm != null)
                currentPage = fm.findFragmentByTag("f" + mCurrentPosition);
            if (currentPage != null && currentPage instanceof VideoInnerFragment) {
                ((VideoInnerFragment) currentPage).onMyKeyUp(keyCode, event);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (currentPage == null && fm != null)
                currentPage = fm.findFragmentByTag("f" + mCurrentPosition);
            if (currentPage != null && currentPage instanceof VideoInnerFragment) {
                ((VideoInnerFragment) currentPage).onMyKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void getBundle() {
        if (getIntent() != null) {
            authorID = getIntent().getStringExtra(REEL_AUTHOR_ID);
            sourceID = getIntent().getStringExtra(REEL_SOURCE_ID);
            context = getIntent().getStringExtra(REEL_CONTEXT);
            hashtag = getIntent().getStringExtra(REEL_HASHTAG);
            title = getIntent().getStringExtra(REEL_TITLE);
            apicall = getIntent().getExtras().getBoolean(API_CALL);
            mode = getIntent().getStringExtra(REEL_MODE);
            reelPosition = getIntent().getIntExtra(REEL_POSITION, -1);
//            cachePosition = reelPosition;
            mNext = getIntent().getStringExtra(REEL_PAGE);
            mType = getIntent().getStringExtra(REEL_TYPE);
            if (getIntent().getParcelableArrayListExtra(REEL_DATALIST) != null)
                videoItems = getIntent().getParcelableArrayListExtra(REEL_DATALIST);
//            if (videoItems != null && !videoItems.isEmpty())
//                lastCachePosition = videoItems.size() - 1;

            Log.d("TAG", "getBundle() called");
        }
    }

    public void invalidateViews() {

    }

    private void init() {
        m3UParser = new M3UParser(this);
        cacheManager = new DbHandler(this);
        updateCacheData();
        fm = getSupportFragmentManager();
        studioPresenter = new StudioPresenter(this, this);
        presenter = new ReelPresenter(this, this);
        pagerAdapter = new VideoPagerAdapter(this, videoItems, this, mode,
                pos -> {
                    if (videoItems.size() > pos && pos >= 0) {
                        videoItems.remove(pos);
                        if (pagerAdapter != null) {
                            pagerAdapter.notifyItemRemoved(pos);
                        }
                    }
                }, (mode != null && (mode.equalsIgnoreCase("profile") || mode.equalsIgnoreCase("public"))));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPosition = position;
                if (fm != null) {
                    currentPage = fm.findFragmentByTag("f" + position);
//                    if (currentPage instanceof VideoInnerFragment) {
//                        ((VideoInnerFragment) currentPage).setTag(isActive);
//                    }
                }
                if (maxCacheLimit == -1) {
                    maxCacheLimit = cachePosition + 10;
                }
                //For Search query not to do futher api call
//                if (!apicall) {
                if (videoItems != null && videoItems.size() > 0 && position < videoItems.size() && presenter != null && (TextUtils.isEmpty(mType))) {
                    //cache updating max limit based on current position and cache poistion having +10 size
                    if ((cachePosition - mCurrentPosition) < 10) {
                        maxCacheLimit += 1;
                        if ((cachePosition + 1) < videoItems.size()) {
                            if (!isCaching && videoItems.get(cachePosition + 1) != null && !videoItems.get(cachePosition + 1).isCached()) {
                                isCaching = true;
                                cacheVideo(videoItems.get(cachePosition + 1), cachePosition + 1);
                            }
                        }
                    }


                    presenter.event(videoItems.get(position).getId());
                    if (position == videoItems.size() - 1) {
                        Log.e("DEBUG_SHIFA", "onPageSelected:position "+ position );
                        Log.e("DEBUG_SHIFA", "onPageSelected:size "+ videoItems.size() );

                        Log.e("pagination", "page - " + position + isLastPage + isLoading);
                        if (!isLastPage) {
                            isLoading = true;
                            if (TextUtils.isEmpty(mode)) {
                                presenter.getVideos("", context, mNext, false, false, hashtag);
                            } else if (mode.equalsIgnoreCase("profile") || mode.equalsIgnoreCase("reels")) {
                                studioPresenter.loadMyReels(sourceID, mNext);
                            } else if (mode.equalsIgnoreCase("public") && !TextUtils.isEmpty(mNext)) {
                                presenter.getVideos("", context, mNext, false, false, hashtag);
                            } else if (mode.equalsIgnoreCase("saved")) {
                                studioPresenter.loadSavedReels(mNext);
                            }
//                            }
                        }
                    }
//                } else {
//                    Log.e("Search", "No api call: ");
//                }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.TYPE_MUTE_REELS_SCROLLING);
                    messageEvent.setIntData(0);

                    EventBus.getDefault().post(messageEvent);
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    MessageEvent messageEvent = new MessageEvent();
                    messageEvent.setType(MessageEvent.TYPE_MUTE_REELS_SCROLLING);
                    messageEvent.setIntData(1);

                    EventBus.getDefault().post(messageEvent);
                }
            }
        });

        Utils.loadSkeletonLoaderReels(reelsViewSwitcher, false);
        if (TextUtils.isEmpty(mode))
            presenter.getVideos("", context, mNext, false, false, hashtag);
        else if (mode.equalsIgnoreCase("profile") || mode.equalsIgnoreCase("reels"))
            if (videoItems != null && videoItems.size() > 0) {
                pagerAdapter.notifyDataSetChanged();
                if (reelPosition != -1) {
                    viewPager.setCurrentItem(reelPosition, false);
                    reelPosition = -1;
                }
                noSkeletonView = true;
            } else
                studioPresenter.loadMyReels("", mNext);
        else if (mode.equalsIgnoreCase("public")) {
            if (videoItems != null && videoItems.size() > 0) {
                pagerAdapter.notifyDataSetChanged();
                if (reelPosition != -1) {
                    viewPager.setCurrentItem(reelPosition, false);
                    reelPosition = -1;
                }
                noSkeletonView = true;
            } else
                presenter.getVideos("", context, mNext, false, false, hashtag);
        } else if (mode.equalsIgnoreCase("saved")) {
            if (videoItems != null && videoItems.size() > 0) {
                pagerAdapter.notifyDataSetChanged();
                if (reelPosition != -1) {
                    viewPager.setCurrentItem(reelPosition, false);
                    reelPosition = -1;
                }
                noSkeletonView = true;
            } else
                studioPresenter.loadSavedReels(mNext);
        }
//        cacheVideo(videoItems.get(cachePosition), cachePosition);

        m3UParser.addVideoCacheListener(this);//passing the interface
        if (maxCacheLimit == -1) {
            maxCacheLimit = cachePosition + 10;
        }
        loadCacheData(); //cache
    }

    private void updateCacheData() {
//clearing the directory with exisitng cached videos
        try {
            File cachePath = new File(this.getExternalFilesDir(HLS_DIR2).getAbsolutePath());
            if (cachePath.isDirectory()) {
                File[] filesList = cachePath.listFiles();
                for (File file : filesList) {
                    if (file.exists()) {
                        file.delete();
                        Log.d("cacheclear1", "updateCacheData: Deleting file");
                    }
                }
            }
            File videoPath = new File(this.getExternalFilesDir(VIDEOS_DIR2).getAbsolutePath());
            if (videoPath.isDirectory()) {
                File[] videosList = videoPath.listFiles();
                for (File file : videosList) {
                    if (file.exists()) {
                        Log.d("cacheclear2", "updateCacheData: deleting video");
                        file.delete();
                    }
                }
            }
//            ReelResponse reelResponse = cacheManager.getReelList("ReelsList");
//            if (reelResponse != null) {
//                List<ReelsItem> cachedVideo = reelResponse.getReels();
//                List<String> nonCommon = new ArrayList<>();
//                boolean isCommon = false;
//                if (cachedVideo != null && !cachedVideo.isEmpty()) {
//                    for (File file : totalFiles) {
//                        String[] fullPath = file.getAbsolutePath().split("/");
//                        for (ReelsItem reelsItem : cachedVideo) {
//                            if (reelsItem.getMedia() != null) {
//                                String[] cachedFileName = reelsItem.getMedia().split("/");
//                                if (cachedFileName[cachedFileName.length - 1].equals(fullPath[fullPath.length - 1])) {
//                                    Log.d("Searching_Files", "updateCacheData: File found.");
//                                    isCommon = true;
//                                }
//                            }
//                        }
//                        if (!isCommon) {
//                            nonCommon.add(file.getAbsolutePath());
//                        }
//                        if (isCommon) {
//                            isCommon = false;
//                        }
//                    }
//                    if (!nonCommon.isEmpty()) {
//                        for (String fileToDel : nonCommon) {
//                            File file = new File(fileToDel);
//                            if (file.exists()) {
//                                Log.d("Searching_Files", "updateCacheData: Deleting::>>" + fileToDel);
//                                file.delete();
//                                String directory = fileToDel.replace("hls_files", VIDEOS_DIR);
//                                String video = directory.replace(".m3u8", ".ts");
//                                File videoFile = new File(video);
//                                if (videoFile.exists()) {
//                                    videoFile.delete();
//                                    Log.d("Searching_Files", "updateCacheData: Deleting::>>" + video);
//                                }
//                            }
//                        }
//                        Log.d("Searching_Files", "Non Common Items:: " + nonCommon.size());
//                    }
//                }
//            } else {
//                Log.d("Searching_Files", "Reel Cache is null");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*try {
            File reelDir = new File(requireContext().getExternalFilesDir(REELS_DIRECTORY).getAbsolutePath());
            File hlsDir = new File(requireContext().getExternalFilesDir(HLS_DIRECTORY).getAbsolutePath());
            if (reelDir.isDirectory()) {
                String[] reelChildren = reelDir.list();
                if (reelChildren != null && reelChildren.length > 0) {
                    for (String path : reelChildren) {
                        new File(path).delete();
                    }
                    reelDir.delete();
                }
            }
            if (hlsDir.isDirectory()) {
                String[] hlsChildren = reelDir.list();
                if (hlsChildren != null && hlsChildren.length > 0) {
                    for (String path : hlsChildren) {
                        new File(path).delete();
                    }
                    reelDir.delete();
                }
                hlsDir.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void loadCacheData() {
        /*
        //To load videos from cache
        if (cacheManager != null && !refresh.isRefreshing()) {
            ReelResponse data = cacheManager.getReelList("ReelsList");
            if (data != null) {
                List<ReelsItem> dataItemArrayList = data.getReels();
                if (dataItemArrayList != null && dataItemArrayList.size() > 0) {
                    Log.d(TAG, "loadCacheData: size = " + dataItemArrayList.size());
                    ReelResponse cacheReelResponse = new ReelResponse();
                    cacheReelResponse.setReels(dataItemArrayList);
                    reelsToSave.clear();
                    reelsToSave.addAll(dataItemArrayList);
                    cachePosition = dataItemArrayList.size() - 1;
                    maxCacheLimit = cachePosition + 10;
                    Log.d(TAG, "loadCacheData: MaxLimit before populating:: " + maxCacheLimit);
                    populateReels(cacheReelResponse);
                } else {
                    if (maxCacheLimit == -1) {
                        maxCacheLimit = cachePosition + 10;
                    }
                    loaderShow(true);
                    reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, false, "");
                }
            } else {
                if (maxCacheLimit == -1) {
                    maxCacheLimit = cachePosition + 10;
                }
                loaderShow(true);
                reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, false, "");
            }
        } else {
            if (maxCacheLimit == -1) {
                maxCacheLimit = cachePosition + 10;
            }
            loaderShow(true);
            reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, false, "");
        }
        if (maxCacheLimit == -1) {
            maxCacheLimit = cachePosition + 10;
        }*/
        if (maxCacheLimit == -1) {
            maxCacheLimit = cachePosition + 10;
        }
//        loaderShow(true);
        //why api call here
//        presenter.getVideos("", context, mNext, false, false, hashtag);
    }

    private void bindViews() {
        reelsViewSwitcher = findViewById(R.id.reels_view_switcher);
        noRecordFoundContainer = findViewById(R.id.no_record_found_container);
        refresh = findViewById(R.id.refresh);
        mTitle = findViewById(R.id.headerText);
        progress_barreel = findViewById(R.id.progress_barreel);

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        back = findViewById(R.id.ivBack);
        viewPager = findViewById(R.id.pager);

        if (hashtag != null) {
            mTitle.setText(hashtag);
        }

        back.setOnClickListener(v -> onBackPressed());
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //this constant causes reel not playing when going back from reel activity in view more bottom sheet
//        Constants.onResumeReels = false;
        Intent intent = new Intent();
        intent.putExtra("change", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        PRDownloader.cancelAll();
    }

    @Override
    protected void onResume() {
         super.onResume();
        if (currentPage == null && fm != null)
            currentPage = fm.findFragmentByTag("f" + mCurrentPosition);
        if (currentPage != null && currentPage instanceof VideoInnerFragment) {
            ((VideoInnerFragment) currentPage).startVideo(0);
        }
    }

    @Override
    public void loaderShow(boolean flag) {

        if (flag)
            isLoading = flag;

        if (flag && refresh.isRefreshing()) {
            viewPager.setUserInputEnabled(false);
            if (currentPage instanceof VideoInnerFragment) {
                ((VideoInnerFragment) currentPage).clickEnabling(false);
            }
        } else {
            viewPager.setUserInputEnabled(true);
            if (currentPage instanceof VideoInnerFragment) {
                ((VideoInnerFragment) currentPage).clickEnabling(true);
            }
        }

        if (!noSkeletonView && (mNext == null || (mNext.equals("") && !refresh.isRefreshing()))) {
            Utils.loadSkeletonLoaderReels(reelsViewSwitcher, flag);
        } else {
            Utils.loadSkeletonLoaderReels(reelsViewSwitcher, false);
        }

        if (!flag) {
            refresh.setRefreshing(false);
        }
    }

    @Override
    public void error(String error) {
        isLoading = false;
    }

    @Override
    public void success(ReelResponse reelResponse) {
        isLoading = false;
//        Constants.onResumeReels = true;
//        refresh.setRefreshing(false);
        if (isRefresh) {
            isRefresh = false;
            videoItems.clear();
            viewPager.setAdapter(pagerAdapter);
            pagerAdapter.notifyDataSetChanged();
        }
        if (reelResponse != null && reelResponse.getReels() != null && reelResponse.getReels().size() > 0 && pagerAdapter != null) {
//            if (TextUtils.isEmpty(mType)) {
            if (reelResponse.getMeta() != null) {
                mNext = reelResponse.getMeta().getNext();
                if (TextUtils.isEmpty(mNext)) {
                    isLastPage = true;
                }
            }
            for (int position = 0; position < reelResponse.getReels().size(); position++) {
                ReelsItem reelsItem = reelResponse.getReels().get(position);
                if (prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                    int interval = 10;
                    if (prefConfig.getAds().getInterval() != 0) {
                        interval = prefConfig.getAds().getInterval();
                    }
                    int newCount = videoItems.size();
                    if (newCount != 0 && newCount % interval == 0) {
                        //if (ad == interval) {
                        Log.e("ADS", "AD Added");
                        ReelsItem reelsItem1 = new ReelsItem();
                        if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                            reelsItem1.setType("FB_Ad");
                        } else {
                            reelsItem1.setType("G_Ad");
                        }
                        videoItems.add(reelsItem1);
                    }
                }
                videoItems.add(reelsItem);
            }
//            } else {
//            videosViewPager.setItemViewCacheSize(videoItems.size());
            pagerAdapter.notifyDataSetChanged();

//            if (reload) {
//                // If user reload reels in first position then fragment onResume not calling
//                // because its already resumed so that's why calling Start to play video
//                if (currentPage != null && currentPage instanceof VideoInnerFragment) {
//                    ((VideoInnerFragment) currentPage).Start(0);
//                }
//            }
            if (reelPosition != -1) {
                viewPager.setCurrentItem(reelPosition, false);
                reelPosition = -1;
            }
        }
        if (videoItems.size() == 0) {
            if (!isDestroyed()) {
                noRecordFoundContainer.setVisibility(View.VISIBLE);
            }
        }

        try {
            if (!videoItems.isEmpty() && cachePosition < maxCacheLimit && !isCaching &&
                    videoItems.get(cachePosition + 1) != null && !videoItems.get(cachePosition + 1).isCached()) {
                isCaching = true;
                cacheVideo(videoItems.get(cachePosition + 1), cachePosition + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error404(String error) {
        isLoading = false;
    }

    @Override
    public void success(ReelResponse reelResponse, boolean reload) {
        isLoading = false;
        Constants.onResumeReels = true;
        refresh.setRefreshing(false);
        if (isRefresh) {
            isRefresh = false;
//            videoItems.clear();
            viewPager.setAdapter(pagerAdapter);
            pagerAdapter.notifyDataSetChanged();
        }
        if (reelResponse != null && reelResponse.getReels() != null && reelResponse.getReels().size() > 0 && pagerAdapter != null) {
            if (TextUtils.isEmpty(mType)) {
                if (reelResponse.getMeta() != null) {
                    mNext = reelResponse.getMeta().getNext();
                    if (TextUtils.isEmpty(mNext)) {
                        isLastPage = true;
                    }
                }
                for (int position = 0; position < reelResponse.getReels().size(); position++) {
                    ReelsItem reelsItem = reelResponse.getReels().get(position);
                    if (prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                        int interval = 10;
                        if (prefConfig.getAds().getInterval() != 0) {
                            interval = prefConfig.getAds().getInterval();
                        }
                        int newCount = videoItems.size();
                        if (newCount != 0 && newCount % interval == 0) {
                            //if (ad == interval) {
                            Log.e("ADS", "AD Added");
                            ReelsItem reelsItem1 = new ReelsItem();
                            if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                                reelsItem1.setType("FB_Ad");
                            } else {
                                reelsItem1.setType("G_Ad");
                            }
                            videoItems.add(reelsItem1);
                        }
                    }
                    videoItems.add(reelsItem);
                }
            } else {
                if (reelResponse.getReels() != null) {
                    videoItems.add(reelResponse.getReels().get(0));
                }
            }
//            videosViewPager.setItemViewCacheSize(videoItems.size());
            pagerAdapter.notifyDataSetChanged();

            if (reload) {
                // If user reload reels in first position then fragment onResume not calling
                // because its already resumed so that's why calling Start to play video
                if (currentPage != null && currentPage instanceof VideoInnerFragment) {
                    ((VideoInnerFragment) currentPage).startVideo(0);
                }
            }
            if (reelPosition != -1) {
                viewPager.setCurrentItem(reelPosition, false);
                reelPosition = -1;
            }
        }
        if (videoItems.size() == 0) {
            if (!isDestroyed()) {
                noRecordFoundContainer.setVisibility(View.VISIBLE);
//                Utils.addNoDataView(this, noRecordFoundContainer, Constants.DARK);
//                noRecordFoundContainer.setVisibility(View.VISIBLE);
            }
        }

        try {
            if (!videoItems.isEmpty() && cachePosition < maxCacheLimit && !isCaching && videoItems.get(cachePosition + 1) != null && !videoItems.get(cachePosition + 1).isCached()) {
                isCaching = true;
                cacheVideo(videoItems.get(cachePosition + 1), cachePosition + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextVideo(int postion) {
        if (postion > -1) {
            Log.e("VideoInner", "position : " + postion);
            int next = postion;
            next++;
            if (next < videoItems.size()) {
                viewPager.setCurrentItem(next, true);
            }
        }
    }

    public void reload() {
//        refresh.setRefreshing(true);
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            refresh.setRefreshing(false);
            return;
        }
        if (presenter != null) {
            if (fm != null && videoItems != null) {
                if (videoItems.size() > mCurrentPosition && mCurrentPosition >= 0) {
                    currentPage = fm.findFragmentByTag("f" + mCurrentPosition);
                }
                if (refresh.isRefreshing()) {
                    if (currentPage != null && currentPage instanceof VideoInnerFragment) {
                        ((VideoInnerFragment) currentPage).onPause();
                    }
                    viewPager.setAdapter(null);
                    videoItems.clear();
                    pagerAdapter.notifyDataSetChanged();
                }
            }
            isRefresh = true;
            mNext = "";

            viewPager.setAdapter(null);
            videoItems.clear();
            reelsToSave.clear();
            cachePosition = -1;
            maxCacheLimit = -1;
            isCaching = false;
            mNext = "";
            PRDownloader.cancelAll();
            pagerAdapter.notifyDataSetChanged();
//            mCurrentPage = "";
//            new_post.setVisibility(View.GONE);
            loadCacheData();

            if (TextUtils.isEmpty(mode))
                presenter.getVideos("", context, mNext, true, false, hashtag);
            else if (mode.equalsIgnoreCase("profile") || mode.equalsIgnoreCase("reels"))
                studioPresenter.loadMyReels(sourceID, mNext);
            else if (mode.equalsIgnoreCase("public")) {
                presenter.getVideos("", context, mNext, true, false, hashtag);
            } else if (mode.equalsIgnoreCase("saved")) {
                studioPresenter.loadSavedReels(mNext);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (newsPresenter != null && data != null && data.hasExtra("id")) {
            String id = data.getStringExtra("id");
            int position = data.getIntExtra("position", -1);
        }
    }

    private void cacheVideo(ReelsItem reelsItem, int position) {
        Log.d("cachecreatedir", "cacheFirstVideo: Video Caching Started for:: " + position + "- ID::" + reelsItem.getId() + "- Title::" + reelsItem.getDescription());
        m3UParser.updateFilePrefs("video_" + reelsItem.getId() + ".ts", new File(this.getExternalFilesDir(HLS_DIR2),
                "video_" + reelsItem.getId() + ".m3u8"), this.getExternalFilesDir(VIDEOS_DIR2).getAbsolutePath());
        m3UParser.cacheVideo(reelsItem, position);
    }

    @Override
    public void videoCached(ReelsItem reelsItem, int position) {
        this.cachePosition = position;
        Log.d("cache", "success: Last Cache position::: " + cachePosition);
        try {
            if (reelsItem != null) {
                videoItems.remove(position);
                videoItems.add(position, reelsItem);
                if (mCurrentPosition != cachePosition) {
                    pagerAdapter.notifyItemChanged(cachePosition);
                }
            }
            if (position < maxCacheLimit && (position + 1) < videoItems.size()) {
                if (videoItems.get(position + 1) != null && videoItems.get(position + 1).getType() == null &&
                        videoItems.get(position + 1).getMedia().startsWith("http") && !videoItems.get(position + 1).isCached()) {
                    cacheVideo(videoItems.get(position + 1), position + 1);
                } else {
                    isCaching = false;
                }
            } else {
                isCaching = false;
                Log.d("CACHE_TAG", "videoCached: Videos completed in current queue");
            }
        } catch (Exception e) {
            isCaching = false;
            e.printStackTrace();
            Log.d(
                    "CACHE_TAG",
                    "onDownloadComplete:" + e.getLocalizedMessage()
            );
        }
    }
}