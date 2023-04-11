package com.ziro.bullet.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ziro.bullet.R;
import com.ziro.bullet.fragments.CommunityFeedFragment;

import com.ziro.bullet.interfaces.HomeLoaderCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CommunityFeedPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 1;
    private ArrayList<String> mTabTitleList = new ArrayList<>();

    private OnGotoChannelListener listener;

    private String type;
    private GoHome goHomeMainActivityListener;
    private GoHome goHomeTempHomeListener;
    private FragmentManager fragmentManager;
    private TempCategorySwipeListener swipeListener;
    private HomeLoaderCallback loaderCallback;
    private CommunityFeedFragment resto;


    public CommunityFeedPagerAdapter(@NonNull FragmentManager fm, Context context, TempCategorySwipeListener swipeListener, String type, GoHome goHomeTempHomeListener, GoHome goHomeMainActivityListener, OnGotoChannelListener listener, HomeLoaderCallback loaderCallback) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTabTitleList.add(context.getResources().getString(R.string.community));
        this.fragmentManager = fm;
        this.swipeListener = swipeListener;
        this.type = type;
        this.goHomeMainActivityListener = goHomeMainActivityListener;
        this.goHomeTempHomeListener = goHomeTempHomeListener;
        this.listener = listener;
        this.loaderCallback = loaderCallback;
    }

    @Override
    public Fragment getItem(int position) {
        resto = CommunityFeedFragment.newInstance(null, "COMMUNITY", false, goHomeMainActivityListener, goHomeTempHomeListener, loaderCallback);
        if (listener != null) {
            resto.setGotoChannelListener(swipeListener, listener, true);
        } else {
            resto.setGotoChannelListener(swipeListener, null, false);
        }
        return resto;
    }

    public void invalidate() {
        if (resto != null)
            resto.invalidateViews();
    }

    public void resetFirstCardWithMenu() {
        if (resto != null)
            resto.resetFirstCardWithMenu();
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

    public CommunityFeedFragment getFragment() {
        return resto;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (getFragment() != object) {
            resto = (CommunityFeedFragment) object;
        }
        super.setPrimaryItem(container, position, object);
    }
}