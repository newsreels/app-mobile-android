package com.ziro.bullet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.ReelAdViewHolder;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.reels.ReelsRemoveAdvt;

public class ReelAdFragment extends Fragment {

    private View view;
    private ReelsRemoveAdvt reelsRemoveAdvt;
    private PrefConfig mPrefConfig;
    private ReelAdViewHolder viewHolder;
    private int position;

    public ReelAdFragment(ReelsRemoveAdvt reelsRemoveAdvt, int position) {
        this.reelsRemoveAdvt = reelsRemoveAdvt;
        this.position = position;
    }

    public ReelAdFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reel_ad_main, container, false);
        mPrefConfig = new PrefConfig(getContext());
        viewHolder = new ReelAdViewHolder(view, getContext(), mPrefConfig, reelsRemoveAdvt, position);
        return view;
    }
}
