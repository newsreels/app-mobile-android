package com.ziro.bullet.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ziro.bullet.fragments.AuthorArticleFragment;
import com.ziro.bullet.fragments.AuthorReelsFragment;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class AuthorPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 2;
    private ArrayList<String> mTabTitleList = null;
    private String authorID;

    public AuthorPagerAdapter(FragmentManager fm, ArrayList<String> mTabTitleList, String authorID) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mTabTitleList = mTabTitleList;
        this.authorID = authorID;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return AuthorReelsFragment.newInstance(authorID);
        } else {
            return  AuthorArticleFragment.newInstance(authorID);
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitleList.get(position);
    }
}