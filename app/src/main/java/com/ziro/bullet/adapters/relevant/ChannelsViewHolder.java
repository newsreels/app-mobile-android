package com.ziro.bullet.adapters.relevant;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.NewFeed.SuggestedChannelAdapter;
import com.ziro.bullet.adapters.relevant.callbacks.SourceFollowingCallback;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.fragments.FollowingDetailActivity;
import com.ziro.bullet.interfaces.ViewItemClickListener;

import java.util.ArrayList;

public class ChannelsViewHolder extends RecyclerView.ViewHolder {

    private RecyclerView listView;
    private TextView seeAll;
    private TextView titleTv;
    public GridLayoutManager layoutManager;

    public ChannelsViewHolder(@NonNull View itemView, boolean isDiscover) {
        super(itemView);
        listView = itemView.findViewById(R.id.items);
        titleTv = itemView.findViewById(R.id.title);
        seeAll = itemView.findViewById(R.id.see_all);
    }

    public void bind(String title, Activity context, ArrayList<Source> sourceArrayList,
                     RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset) {

        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
            seeAll.setVisibility(sourceArrayList.size() > 1 ? View.VISIBLE : View.GONE);
        } else {
            titleTv.setVisibility(View.GONE);
            seeAll.setVisibility(View.GONE);
        }
        SuggestedChannelAdapter adapter = new SuggestedChannelAdapter(context, sourceArrayList, false);
        layoutManager = new GridLayoutManager(context, 3, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
        listView.addOnScrollListener(onScrollListener);

        seeAll.setOnClickListener(v -> FollowingDetailActivity.launchActivityWithResult(context, "channels", "", 222));

    }
}
