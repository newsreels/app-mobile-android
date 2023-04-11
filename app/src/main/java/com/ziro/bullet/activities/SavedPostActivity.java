package com.ziro.bullet.activities;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.fragments.SaveArticleFragment;
import com.ziro.bullet.fragments.SaveReelsFragment;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SavedPostActivity extends BaseActivity {

    private RelativeLayout ivBack;
    private TabLayout tabLayout;
    private ViewPager viewpager;
    private ArrayList<String> mTabTitleList = new ArrayList<>();
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.setStatusBarColor(this);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_saved_post);
        bindView();

        mTabTitleList.add(getString(R.string.newsreels));
        mTabTitleList.add(getString(R.string.articles));

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabTitleList);
        viewpager.setAdapter(pagerAdapter);
        viewpager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewpager);
    }

    private void bindView() {
        ivBack = findViewById(R.id.ivBack);
        tabLayout = findViewById(R.id.tabLayout);
        viewpager = findViewById(R.id.viewpager);
        ivBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private static class PagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> mTabTitleList;

        public PagerAdapter(FragmentManager fm, ArrayList<String> mTabTitleList) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mTabTitleList = mTabTitleList;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new SaveReelsFragment();
            } else {
                return new SaveArticleFragment("");
            }
        }

        @Override
        public int getCount() {
            return mTabTitleList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitleList.get(position);
        }
    }
}