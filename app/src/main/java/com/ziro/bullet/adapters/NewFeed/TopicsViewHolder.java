package com.ziro.bullet.adapters.NewFeed;

import android.app.Activity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.changereels.ReelTopicsAdapter;
import com.ziro.bullet.adapters.CommunityFeed.ReelItemsAdapter;
import com.ziro.bullet.adapters.OnboardingTopicsAdapter;
import com.ziro.bullet.adapters.following.FollowingTopicsAdapter;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.fragments.FollowingDetailActivity;
import com.ziro.bullet.model.Reel.ReelsItem;

import java.util.ArrayList;

public class TopicsViewHolder extends RecyclerView.ViewHolder {

    private Activity context;
    public GridLayoutManager layoutManager;
    private TextView title;
    private LinearLayout rootCard;
    private RecyclerView listView;
    private TextView see_all;
    private boolean large = false;
    private boolean isDiscover = false;
    private float preX = 0f;
    private float preY = 0f;
    private float x1, x2;
    private final float Y_BUFFER = 10;


    public TopicsViewHolder(View view, Activity context, boolean large, boolean isDiscover) {
        super(view);
        this.context = context;
        this.large = large;
        this.isDiscover = isDiscover;
        rootCard = view.findViewById(R.id.rootCard);
        title = view.findViewById(R.id.title);
        listView = view.findViewById(R.id.items);
        see_all = view.findViewById(R.id.see_all);
        see_all.setVisibility(View.VISIBLE);
    }

    private void openDetail(String type) {
        FollowingDetailActivity.launchActivityWithResult(context, type, "", 222);
    }

    public void bind(String mTitle, ArrayList<Topics> data, RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset) {

        see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetail("topics");
            }
        });
        if (!TextUtils.isEmpty(mTitle)) {
            title.setVisibility(View.VISIBLE);
            title.setText(mTitle);
        } else {
            title.setVisibility(View.GONE);
        }
        if (data != null && data.size() > 0) {
            FollowingTopicsAdapter adapter = new FollowingTopicsAdapter(context, data);
            layoutManager = new GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false);
            layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(adapter);
            listView.addOnScrollListener(onScrollListener);
            listView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1 = e.getX();
                            listView.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (Math.abs(e.getX() - preX) > Math.abs(e.getY() - preY)) {
                                listView.getParent().requestDisallowInterceptTouchEvent(true);
                            } else if (Math.abs(e.getY() - preY) > Y_BUFFER) {
                                listView.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            break;
                    }
                    preX = e.getX();
                    preY = e.getY();
                    return false;
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
        }
    }
}
