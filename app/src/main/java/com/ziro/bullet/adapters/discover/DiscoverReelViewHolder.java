package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.DiscoverReelAdapter;
import com.ziro.bullet.discretescrollview.DSVOrientation;
import com.ziro.bullet.discretescrollview.DiscreteScrollView;
import com.ziro.bullet.discretescrollview.transform.Pivot;
import com.ziro.bullet.discretescrollview.transform.ScaleTransformer;
import com.ziro.bullet.model.Reel.ReelsItem;

import java.util.ArrayList;

import im.ene.toro.PlayerSelector;

public class DiscoverReelViewHolder extends RecyclerView.ViewHolder {

    private final float Y_BUFFER = 10;
    private Activity context;
    private TextView title;
    private LinearLayout rootCard;
    private DiscreteScrollView listView;
    private int prevPos = -1;
    private float preX = 0f;
    private float preY = 0f;

    public DiscoverReelViewHolder(View view, Activity context) {
        super(view);
        this.context = context;
        title = view.findViewById(R.id.title);
        listView = view.findViewById(R.id.items);
        rootCard = view.findViewById(R.id.rootCard);
    }

    public void bind(boolean selected, String mTitle, ArrayList<ReelsItem> data) {
        if (!TextUtils.isEmpty(mTitle)) {
            title.setVisibility(View.VISIBLE);
            title.setText(mTitle);
        } else {
            title.setVisibility(View.GONE);
        }
        title.setTextColor(context.getResources().getColor(R.color.bullet_text));
        rootCard.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
        DiscoverReelAdapter adapter = new DiscoverReelAdapter(context, data, "REEL");
        listView.setAdapter(adapter);
        listView.setOrientation(DSVOrientation.HORIZONTAL);
        listView.setOffscreenItems(1);
        listView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1f)
                .setMinScale(0.9f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.CENTER)
                .build());
        listView.setItemTransitionTimeMillis(100);
        if (selected)
            playVideos();
        else
            pauseVideos();
        listView.addOnItemChangedListener((viewHolder, adapterPosition) -> {
            Log.d("TAG", "onCurrentItemChanged: " + adapterPosition);

            if (prevPos > -1) {
                RecyclerView.ViewHolder prevHolder = listView.findViewHolderForAdapterPosition(prevPos);
                if (prevHolder instanceof ReelCardViewHolder) {
                    ((ReelCardViewHolder) prevHolder).disablePlayClick();
                }
            }

            if (viewHolder instanceof ReelCardViewHolder) {
                ((ReelCardViewHolder) viewHolder).enablePlayClick();
                ((ReelCardViewHolder) viewHolder).updateMuteButtons();
            }

            if (adapterPosition > -1)
                prevPos = adapterPosition;
        });

        listView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        listView.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
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

//        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == SCROLL_STATE_IDLE) {
//                    DiscreteScrollLayoutManager layoutManager = ((DiscreteScrollLayoutManager) listView.getLayoutManager());
//                    if (layoutManager != null) {
//                        final int firstPosition = layoutManager.getCurrentPosition();
//                        Log.d("onScrollStateChanged", "(firstPosition) [" + firstPosition + "]");
//                        if (firstPosition != -1) {
//                            adapter.selectCard(firstPosition);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

    }

    public void pauseVideos() {
        if (listView != null)
            listView.setPlayerSelector(PlayerSelector.NONE);
    }

    public void playVideos() {
        if (listView != null)
            listView.setPlayerSelector(PlayerSelector.DEFAULT);
    }
}
