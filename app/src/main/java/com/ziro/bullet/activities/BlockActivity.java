package com.ziro.bullet.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.adapters.FragmentAdapter;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.fragments.BlockAuthorsFragment;
import com.ziro.bullet.fragments.BlockSourcesFragment;
import com.ziro.bullet.fragments.BlockedTopicsFragment;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.data.TYPE;

import java.util.ArrayList;

public class BlockActivity extends BaseActivity implements BlockedTopicsFragment.OnBlockedFragmentInteractionListener,
        BlockSourcesFragment.OnBlockedSourceFragmentInteractionListener {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private TextView help;
    private RelativeLayout back;
    private TabLayout tabs;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_block);

        bindView();
        setFragments();
        setListeners();
    }

    private void setListeners() {
        back.setOnClickListener(v -> finish());
        help.setOnClickListener(v -> Utils.openHelpView(this, BuildConfig.HelpUrl, "Help"));
    }

    private void setFragments() {
        fragments = new ArrayList<>();
//        fragments.add(new BlockedTopicsFragment());
        fragments.add(new BlockSourcesFragment());
//        fragments.add(new BlockAuthorsFragment());
        String[] tabTitles = new String[]{ getString(R.string.channels)};
//        String[] tabTitles = new String[]{getString(R.string.topics), getString(R.string.channels), getString(R.string.authors)};
        FragmentAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager(), getApplicationContext(), fragments, tabTitles);
        viewpager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewpager, false);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (viewpager != null) {
                    viewpager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ViewGroup slidingTabStrip = (ViewGroup) tabs.getChildAt(0);
        for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
            View v = slidingTabStrip.getChildAt(i);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.leftMargin = 10;
            params.rightMargin = 66;
        }
    }

    private void bindView() {
        help = findViewById(R.id.help);
        back = findViewById(R.id.back);
        tabs = findViewById(R.id.tablayout);
        viewpager = findViewById(R.id.viewpager);
    }

    @Override
    public void dataChanged(TYPE type, String id) {

    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void onItemClicked(TYPE type, String id, String name) {

    }
}