package com.ziro.bullet.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.fragments.ArticleNotificationsFragment;
import com.ziro.bullet.fragments.GeneralNotificationsFragment;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NotificationActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mPager;
    private RelativeLayout rlBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_notification);
        bindViews();
        init();
    }

    private void bindViews() {
        mTabLayout = findViewById(R.id.tabLayout);
        mPager = findViewById(R.id.viewpager);
        rlBack = findViewById(R.id.ivBack);
    }

    private void init() {
        rlBack.setOnClickListener(v -> onBackPressed());
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(adapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position==0) {
                    AnalyticsEvents.INSTANCE.logEvent(NotificationActivity.this,
                            Events.NOTIFICATION_GENERAL_CLICK);
                }else {
                    AnalyticsEvents.INSTANCE.logEvent(NotificationActivity.this,
                            Events.NOTIFICATION_NEWS_CLICK);
                }
            }
        });
        mTabLayout.setupWithViewPager(mPager);
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> tittleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            tittleList.add(context.getString(R.string.general));
            tittleList.add(context.getString(R.string.news));
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return GeneralNotificationsFragment.createInstance();
            } else {
                return ArticleNotificationsFragment.createInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tittleList.get(position);
        }
    }
}