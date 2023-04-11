package com.ziro.bullet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.fragments.AuthorArticleFragment;
import com.ziro.bullet.fragments.AuthorReelsFragment;
import com.ziro.bullet.fragments.ChannelFragment;
import com.ziro.bullet.fragments.ProfileArticleFragment;
import com.ziro.bullet.fragments.ProfileReelsFragment;
import com.ziro.bullet.interfaces.ProfileApiCallback;
import com.ziro.bullet.interfaces.UpdateCallback;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HashTagDetailsActivity extends BaseActivity
        implements ProfileApiCallback, ProfileArticleFragment.OnFragmentInteractionListener,
        ProfileReelsFragment.OnFragmentInteractionListener {

    //TABS
    private static ArrayList<String> mTabTitleList = new ArrayList<>();
    private RelativeLayout back;
    private TextView desc;

    private PrefConfig mPrefConfig;
    private TopicPagerAdapter topicPagerAdapter;
    private ViewPager viewpager;
    private TabLayout tabLayout;
    private RelativeLayout progress;
    private TextView topic;

    private String mContext;
    private TYPE type;
    private boolean firstTimeLoading = true;
    private PictureLoadingDialog mLoadingDialog;
    private GestureDetectorCompat gestureDetectorCompat;
    private RelativeLayout main_layout;
    private CoordinatorLayout htab_maincontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Utils.checkAppModeColor(this);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_hash_tag);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gestureDetectorCompat = new GestureDetectorCompat(this, new SwipeGestureListener());

        bindViews();
        init();
        setData();
        setListener();
        initTabs();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
//        returnIntent.putExtra("source", mSource);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.left_to_right_500, R.anim.right_to_left_500);
    }

    private void setListener() {
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        main_layout.setOnTouchListener((v, event) -> {
            return gestureDetectorCompat.onTouchEvent(event);
            // return false;
        });
        htab_maincontent.setOnTouchListener((v, event) -> {
            return gestureDetectorCompat.onTouchEvent(event);
            // return false;
        });
    }

    private void initTabs() {
        mTabTitleList.clear();
        mTabTitleList.add(getString(R.string.reels));
        mTabTitleList.add(getString(R.string.articles));
        mTabTitleList.add(getString(R.string.channels));

        topicPagerAdapter = new TopicPagerAdapter(getSupportFragmentManager(), mTabTitleList, mContext, type, new UpdateCallback() {
            @Override
            public void onUpdate() {

            }
        });
        viewpager.setAdapter(topicPagerAdapter);
        viewpager.setOffscreenPageLimit(mTabTitleList.size());
        tabLayout.setupWithViewPager(viewpager);
    }

    private void setData() {
        if (getIntent() != null) {
            mContext = getIntent().getStringExtra("mContext");
            type = (TYPE) getIntent().getSerializableExtra("type");
            if (!TextUtils.isEmpty(getIntent().getStringExtra("name")))
                topic.setText("#" + getIntent().getStringExtra("name"));
        }
    }

    private void bindViews() {
        topic = findViewById(R.id.topic);
        progress = findViewById(R.id.progress);
        tabLayout = findViewById(R.id.tabLayout);
        viewpager = findViewById(R.id.viewpager);
        desc = findViewById(R.id.desc);
        back = findViewById(R.id.back);
        main_layout = findViewById(R.id.main_layout);
        htab_maincontent = findViewById(R.id.htab_maincontent);
    }

    private void init() {
        mPrefConfig = new PrefConfig(this);
    }

    @Override
    public void loaderShow(boolean flag) {
        if (firstTimeLoading) {
            progress.setVisibility(flag ? View.VISIBLE : View.GONE);
        }

        if (!flag)
            dismissProgressDialog();
    }

    @Override
    public void error(String error, String img) {

    }

    @Override
    public void success() {

    }

    /**
     * loading dialog
     */
    protected void showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(this);
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

    private int[] tabIconsActive = {
            R.drawable.reel_tab_active,
            R.drawable.article_tab_active,
            R.drawable.fav_tab_active,
    };

    private int[] tabIconsInActive = {
            R.drawable.reel_tab_inactive,
            R.drawable.article_tab_inactive,
            R.drawable.fav_tab_inactive,
    };

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText(tabIconsActive[0]);
        tabLayout.getTabAt(1).setText(tabIconsInActive[1]);
        tabLayout.getTabAt(2).setText(tabIconsInActive[2]);
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                tab.setIcon(tabIconsActive[tab.getPosition()]);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.setIcon(tabIconsInActive[tab.getPosition()]);
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }

    @Override
    public void onUploadClicked() {

    }


    private static class TopicPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<String> mTabTitleList = new ArrayList<>();
        private int NUM_PAGES = 3;
        private TYPE type;
        private String mContext;
        private ChannelFragment fragment;
        private AuthorArticleFragment fragment2;
        private UpdateCallback updateCallback;

        public TopicPagerAdapter(FragmentManager fm, ArrayList<String> mTabTitleList, String mContext, TYPE type, UpdateCallback updateCallback) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.NUM_PAGES = mTabTitleList.size();
            this.mTabTitleList = mTabTitleList;
            this.type = type;
            this.mContext = mContext;
            this.updateCallback = updateCallback;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return AuthorReelsFragment.newInstance(mContext);
            } else if (position == 1) {
                fragment2 = AuthorArticleFragment.newInstance(mContext);
                fragment2.setCallbackListener(updateCallback);
                return fragment2;
            } else {
                fragment = ChannelFragment.newInstance(mContext);
                return fragment;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitleList.get(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onBackPressed();
                            result = true;
                            //onSwipeRight();
                        } else {
                            //onSwipeLeft();
                        }
                        //result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        //onSwipeBottom();
                    } else {
                        //onSwipeTop();
                    }
                    //result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return result;
        }
    }
}