package com.ziro.bullet.adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {

    private String[] tabTitles;
    private Context context;
    private ArrayList<Fragment> fragments;

    public FragmentAdapter(FragmentManager fm, Context context, ArrayList<Fragment> fragments, String[] tabTitles) {
        super(fm);
        this.context = context;
        this.tabTitles = tabTitles;
        this.fragments = fragments;
    }

    // overriding getPageTitle()
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

}