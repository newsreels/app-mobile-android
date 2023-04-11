package com.ziro.bullet.adapters.relevant;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.following.CircleTopicsAdapter;
import com.ziro.bullet.adapters.following.FollowingTopicsAdapter;
import com.ziro.bullet.adapters.relevant.callbacks.TopicsFollowingCallback;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.fragments.FollowingDetailActivity;
import com.ziro.bullet.interfaces.ViewItemClickListener;

import java.util.ArrayList;

public class CircleTopicsViewHolder extends RecyclerView.ViewHolder {

    private Activity context;
    private boolean isDiscover = false;
    private final RecyclerView listView;
    private final RelativeLayout titleMain;
    private final TextView seeAll;
    private final TextView title;
    public LinearLayoutManager layoutManager;

    public CircleTopicsViewHolder(@NonNull View itemView, Activity context, boolean isDiscover) {
        super(itemView);
        this.context = context;
        this.isDiscover = isDiscover;
        titleMain = itemView.findViewById(R.id.titleMain);
        title = itemView.findViewById(R.id.title);
        listView = itemView.findViewById(R.id.items);
        seeAll = itemView.findViewById(R.id.see_all);
    }

    private void openDetail(String type) {
        FollowingDetailActivity.launchActivityWithResult(context, type, "", 222);
    }

    public void bind(String mTitle, ArrayList<Topics> data, RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset) {

        if (isDiscover) {
            seeAll.setOnClickListener(view -> openDetail("topics"));
            if (!TextUtils.isEmpty(mTitle)) {
                titleMain.setVisibility(View.VISIBLE);
                title.setText(mTitle);
            } else {
                titleMain.setVisibility(View.GONE);
            }
        } else {
            titleMain.setVisibility(View.GONE);
        }

        CircleTopicsAdapter adapter = new CircleTopicsAdapter(context, data);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
        listView.addOnScrollListener(onScrollListener);
    }
}
