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
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.articles.Article;

import java.util.List;

public class SuggestedArticlesHolder extends RecyclerView.ViewHolder {

    private final float Y_BUFFER = 10;
    public LinearLayoutManager layoutManager;
    private Activity context;
    private RecyclerView listView;
    private TextView title;
    private LinearLayout rootCard;
    private boolean isHome;
    private OnGotoChannelListener gotoChannelListener;
    private float preX = 0f;
    private float preY = 0f;
    private float x1, x2;
    private TempCategorySwipeListener swipeListener;

    public SuggestedArticlesHolder(View view, Activity context, boolean isHome, OnGotoChannelListener gotoChannelListener, TempCategorySwipeListener swipeListener) {
        super(view);
        this.context = context;
        this.isHome = isHome;
        this.swipeListener = swipeListener;
        this.gotoChannelListener = gotoChannelListener;
        listView = view.findViewById(R.id.items);
        title = view.findViewById(R.id.title);
        rootCard = view.findViewById(R.id.rootCard);
    }

    public void bind(String mTitle, List<Article> data, boolean isCounter, RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset) {
        if (data == null || data.size() == 0)
            return;
        if (isHome) {
            title.setVisibility(View.GONE);
        } else {
//            title.setTextColor(context.getResources().getColor(R.color.white));
            if (!TextUtils.isEmpty(mTitle)) {
                title.setVisibility(View.VISIBLE);
                title.setText(mTitle);
            } else {
                title.setVisibility(View.GONE);
            }
        }
//        title.setTextColor(context.getResources().getColor(R.color.bullet_text));
//        rootCard.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
        NewSuggestedArticleAdapter adapter = new NewSuggestedArticleAdapter(context, data, isCounter, isHome, gotoChannelListener, swipeListener);
        if (data.size() < 1) {
            layoutManager = new GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false);
        } else {
            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        }
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
