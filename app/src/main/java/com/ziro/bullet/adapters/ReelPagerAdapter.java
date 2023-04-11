package com.ziro.bullet.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ziro.bullet.fragments.VideoInnerFragment;

public class ReelPagerAdapter extends FragmentStatePagerAdapter {

    private int NUM_PAGES = 0;

    public ReelPagerAdapter(FragmentManager fm, int size) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new VideoInnerFragment();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}