package com.ziro.bullet.fragments.searchNew.searchviewholder;

import android.app.Activity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.changereels.ReelTopicsAdapter;
import com.ziro.bullet.adapters.NewFeed.TopicsAdapter;
import com.ziro.bullet.data.models.topics.Topics;

import java.util.ArrayList;

public class TopicsNewViewHolder extends RecyclerView.ViewHolder {

    private Activity context;
    public GridLayoutManager layoutManager;
    private TextView title;
    private RecyclerView listView;
    private boolean large = false;
    private boolean isDiscover = false;
    private float preX = 0f;
    private float preY = 0f;
    private float x1, x2;
    private final float Y_BUFFER = 10;


    public TopicsNewViewHolder(View view, Activity context, boolean large, boolean isDiscover) {
        super(view);
        this.context = context;
        this.large = large;
        this.isDiscover = isDiscover;
        title = view.findViewById(R.id.title);
        listView = view.findViewById(R.id.items);
    }

    public void bind(String mTitle, ArrayList<Topics> data, RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset) {
        if (isDiscover) {
            title.setTextColor(context.getResources().getColor(R.color.white));
        }
        if (!TextUtils.isEmpty(mTitle)) {
            title.setVisibility(View.VISIBLE);
            title.setText(mTitle);
        } else {
            title.setVisibility(View.GONE);
        }
        if (data != null && data.size() > 0) {
            if (large) {
                ReelTopicsAdapter adapter = new ReelTopicsAdapter(context, data, false);
                layoutManager = new GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false);
                layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);

                listView.setLayoutManager(layoutManager);
                listView.setAdapter(adapter);
            } else {
                TopicsAdapter adapter = new TopicsAdapter(context, data);
                layoutManager = new GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false);
                layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);

                listView.setLayoutManager(layoutManager);
                listView.setAdapter(adapter);
            }
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
