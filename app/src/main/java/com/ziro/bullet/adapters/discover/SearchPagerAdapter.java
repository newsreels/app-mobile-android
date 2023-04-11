package com.ziro.bullet.adapters.discover;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.fragments.Search.SearchAllFragment;
import com.ziro.bullet.fragments.Search.SearchArticlesFragment;
import com.ziro.bullet.fragments.Search.SearchChannelsFragment;
import com.ziro.bullet.fragments.Search.SearchPlacesFragment;
import com.ziro.bullet.fragments.Search.SearchReelsFragment;
import com.ziro.bullet.fragments.Search.SearchTopicsFragment;
import com.ziro.bullet.interfaces.UpdateCallback;
import com.ziro.bullet.interfaces.GoHome;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SearchPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> mTabTitleList = new ArrayList<>();
    private int NUM_PAGES = 6;
    private TYPE type;
    private String mSearchQuery;
    private String mCase;
    private SearchChannelsFragment mSearchChannelFragment;
    private SearchReelsFragment mSearchReelsFragment;
    private SearchArticlesFragment mSearchArticleFragment;
    private SearchAllFragment mSearchAllFragment;
    private UpdateCallback updateCallback;
    private GoHome goHome1;
    private SearchPlacesFragment mSearchPlacesFragment;
    private SearchTopicsFragment mSearchTopicsFragment;

    public SearchPagerAdapter(FragmentManager fm, ArrayList<String> mTabTitleList, String mSearchQuery, TYPE type, UpdateCallback updateCallback, GoHome goHome1, String mCase) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.NUM_PAGES = mTabTitleList.size();
        this.mTabTitleList = mTabTitleList;
        this.type = type;
        this.mCase = mCase;
        this.goHome1 = goHome1;
        this.mSearchQuery = mSearchQuery;
        this.updateCallback = updateCallback;
    }

    public Fragment getFragment(int position) {

        if (!TextUtils.isEmpty(mCase)) {
            if (mCase.equalsIgnoreCase("topics")) {
                return mSearchTopicsFragment;
            } else if (mCase.equalsIgnoreCase("places")) {
                return mSearchPlacesFragment;
            } else if (mCase.equalsIgnoreCase("channels")) {
                return mSearchChannelFragment;
            } else if (mCase.equalsIgnoreCase("reels")) {
                return mSearchReelsFragment;
            } else return null;
        } else {
            if (position == 0) {
                return mSearchAllFragment;
            } else if (position == 1) {
                return mSearchReelsFragment;
            } else if (position == 2) {
                return mSearchArticleFragment;
            } else if (position == 3) {
                return mSearchChannelFragment;
            } else if (position == 4) {
                return mSearchPlacesFragment;
            } else {
                return mSearchTopicsFragment;
            }
        }
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        if (!TextUtils.isEmpty(mCase)) {
            if (mCase.equalsIgnoreCase("topics")) {
                mSearchTopicsFragment = SearchTopicsFragment.newInstance(mSearchQuery);
                return mSearchTopicsFragment;
            } else if (mCase.equalsIgnoreCase("places")) {
                mSearchPlacesFragment = SearchPlacesFragment.newInstance(mSearchQuery);
                return mSearchPlacesFragment;
            } else if (mCase.equalsIgnoreCase("channels")) {
                mSearchChannelFragment = SearchChannelsFragment.newInstance(mSearchQuery);
                return mSearchChannelFragment;
            } else if (mCase.equalsIgnoreCase("reels")) {
                mSearchReelsFragment = SearchReelsFragment.newInstance(mSearchQuery);
                return mSearchReelsFragment;
            } else return null;
        } else {
            if (position == 0) {
                mSearchAllFragment = SearchAllFragment.newInstance(mSearchQuery, goHome1);
                return mSearchAllFragment;
            } else if (position == 1) {
                mSearchReelsFragment = SearchReelsFragment.newInstance(mSearchQuery);
                return mSearchReelsFragment;
            } else if (position == 2) {
                mSearchArticleFragment = SearchArticlesFragment.newInstance(mSearchQuery, goHome1);
                return mSearchArticleFragment;
            } else if (position == 3) {
                mSearchChannelFragment = SearchChannelsFragment.newInstance(mSearchQuery);
                return mSearchChannelFragment;
            } else if (position == 4) {
                mSearchPlacesFragment = SearchPlacesFragment.newInstance(mSearchQuery);
                return mSearchPlacesFragment;
            } else {
                mSearchTopicsFragment = SearchTopicsFragment.newInstance(mSearchQuery);
                return mSearchTopicsFragment;
            }
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
