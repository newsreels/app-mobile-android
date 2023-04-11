package com.ziro.bullet.adapters;

import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ziro.bullet.fragments.ReelAdFragment;
import com.ziro.bullet.fragments.Reels.EndOfReelsFragment;
import com.ziro.bullet.fragments.VideoInnerFragment;
import com.ziro.bullet.interfaces.VideoInterface;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.reels.ReelsRemoveAdvt;

import java.util.List;

public class VideoPagerAdapter extends FragmentStateAdapter {

    private List<ReelsItem> videoItems;
    private VideoInterface listener;
    private String mode;
    private ReelsRemoveAdvt reelsRemoveAdvt;
    private boolean checkReelTab = false;
    private VideoInnerFragment videoInnerFragment;
    private boolean currentFragmentVisible = false;
    private boolean disableSwipingRight;
    private boolean isReelActivity = false;

    public VideoPagerAdapter(Fragment fa, List<ReelsItem> videoItems, VideoInterface listener, String mode, ReelsRemoveAdvt reelsRemoveAdvt, boolean disableSwipingRight) {
        super(fa);
        this.videoItems = videoItems;
        this.listener = listener;
        this.mode = mode;
        this.reelsRemoveAdvt = reelsRemoveAdvt;
        this.disableSwipingRight = disableSwipingRight;
        checkReelTab = true;
        isReelActivity = false;
    }

    public VideoPagerAdapter(FragmentActivity fa, List<ReelsItem> videoItems, VideoInterface listener, String mode, ReelsRemoveAdvt reelsRemoveAdvt, boolean disableSwipingRight) {
        super(fa);
        this.videoItems = videoItems;
        this.listener = listener;
        this.mode = mode;
        this.reelsRemoveAdvt = reelsRemoveAdvt;
        this.disableSwipingRight = disableSwipingRight;
        checkReelTab = false;
        isReelActivity = true;
    }

    public void onHiddenChanged(boolean currentFragmentVisible) {
        this.currentFragmentVisible = currentFragmentVisible;
        if (videoInnerFragment != null) {
            videoInnerFragment.onHiddenChanged(currentFragmentVisible);
        }
    }

    public void onPlayPause(boolean isPlay) {
        if (videoInnerFragment != null) {
            if (isPlay) {
                videoInnerFragment.onResume();
            } else {
                videoInnerFragment.onPause();
            }

        }
    }

    @Override
    public Fragment createFragment(int position) {
        ReelsItem reelsItem = null;
        if (videoItems.size() > position && position >= 0) {
            reelsItem = videoItems.get(position);
        }
        if (reelsItem != null) {
            if (!TextUtils.isEmpty(reelsItem.getType()) && reelsItem.getType().contains("_Ad")) {
                return new ReelAdFragment(reelsRemoveAdvt, position);
            } else {
                videoInnerFragment = VideoInnerFragment.getInstance(videoItems.get(position), position, mode, checkReelTab, disableSwipingRight, isReelActivity);
//                videoInnerFragment = VideoInnerFragment.getInstance(videoItems.get(position), listener, position, mode, checkReelTab, disableSwipingRight);
            }
        } else {
            return new EndOfReelsFragment();
        }
        if (videoInnerFragment != null)
            videoInnerFragment.setVideoCallback(listener);
        return videoInnerFragment;
    }

    @Override
    public int getItemCount() {
        if (videoItems != null) {
            return videoItems.size();
        } else {
            return 0;
        }
    }
}