package com.ziro.bullet.adapters.discover;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.CustomDiscoverListActivity;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.model.NewDiscoverPage.Icon;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ClickableViewAccessibility")
public class ImageCardTopicViewHolder extends RecyclerView.ViewHolder {

    private final TextView text1;
    private final TextView text2;
    private final LinearLayout card;
    private final RecyclerView image_list;
    private final RecyclerView image_list_2;
    private final RecyclerView image_list_3;
    private final Activity mContext;
    private final LinearLayout transformationLayout;
    private Timer myTimer;
    private final Callback callback;
    private String type;
    private DiscoverItem item;
    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        private float startX;
        private float startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    float endY = event.getY();
                    if (isAClick(startX, endX, startY, endY)) {
                        openCustomDiscoverListActivity(type, item);
                    }
                    break;
            }
            //v.getParent().requestDisallowInterceptTouchEvent(true);
            return true;
        }

        private boolean isAClick(float startX, float endX, float startY, float endY) {
            float differenceX = Math.abs(startX - endX);
            float differenceY = Math.abs(startY - endY);
            int CLICK_ACTION_THRESHOLD = 200;
            return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
        }
    };

    public ImageCardTopicViewHolder(@NonNull View itemView, Activity context, Callback callback) {
        super(itemView);
        this.mContext = context;
        this.callback = callback;
        text1 = itemView.findViewById(R.id.text1);
        card = itemView.findViewById(R.id.card);
        text2 = itemView.findViewById(R.id.text2);
        transformationLayout = itemView.findViewById(R.id.transformationLayout);
        image_list = itemView.findViewById(R.id.image_list);
        image_list_2 = itemView.findViewById(R.id.image_list_2);
        image_list_3 = itemView.findViewById(R.id.image_list_3);
    }

    public void bind(String type, DiscoverItem item) {
        this.type = type;
        this.item = item;

        if (mContext != null) {
            transformationLayout.setBackground(mContext.getResources().getDrawable(R.drawable.discover_card_shape));
            text1.setTextColor(mContext.getResources().getColor(R.color.discover_card_grey_text));
            text2.setTextColor(mContext.getResources().getColor(R.color.discover_card_title));
       }


        if (item != null && item.getData() != null) {
            if (!TextUtils.isEmpty(item.getSubtitle())) {
                text1.setText(item.getSubtitle());
            }
            if (!TextUtils.isEmpty(item.getTitle())) {
                text2.setText(item.getTitle());
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager layoutManager3 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

            ArrayList<Icon> iconArrayList1 = new ArrayList<>();
            ArrayList<Icon> iconArrayList2 = new ArrayList<>();
            ArrayList<Icon> iconArrayList3 = new ArrayList<>();

            for (int i = 0; i < item.getData().getIcons().size(); i++) {
                if (i % 3 == 0) {
                    iconArrayList1.add(item.getData().getIcons().get(i));
                } else if (i % 3 == 1) {
                    iconArrayList2.add(item.getData().getIcons().get(i));
                } else {
                    iconArrayList3.add(item.getData().getIcons().get(i));
                }
            }

            image_list.setLayoutManager(layoutManager);
            image_list.setItemAnimator(new DefaultItemAnimator());
            image_list.setOnTouchListener(onTouchListener);

            image_list_2.setLayoutManager(layoutManager2);
            image_list_2.setItemAnimator(new DefaultItemAnimator());
            image_list_2.setOnTouchListener(onTouchListener);

            image_list_3.setLayoutManager(layoutManager3);
            image_list_3.setItemAnimator(new DefaultItemAnimator());
            image_list_3.setOnTouchListener(onTouchListener);

            if (type.equalsIgnoreCase("IMAGE_TOPICS")) {
                image_list.setAdapter(new ImageScrollTopicAdapter(iconArrayList1, callback));
                image_list_2.setAdapter(new ImageScrollTopicAdapter(iconArrayList2, callback));
                image_list_3.setAdapter(new ImageScrollTopicAdapter(iconArrayList3, callback));
            } else {
                image_list.setAdapter(new ImageScrollAdapter(iconArrayList1, callback));
                image_list_2.setAdapter(new ImageScrollAdapter(iconArrayList2, callback));
                image_list_3.setAdapter(new ImageScrollAdapter(iconArrayList3, callback));
            }

            card.setOnClickListener(v -> openCustomDiscoverListActivity(type, item));


            resume();
        }
    }

    public void startAnimator() {
        resume();
    }

    public void stopAnimator() {
        pause();
    }

    private void pause() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
        }
    }

    private void resume() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
        }
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (image_list != null && image_list_2 != null && image_list_3 != null) {
                    image_list.post(() -> {
                        if (image_list != null)
                            image_list.scrollBy(1, 0);
                    });
                    image_list_2.post(() -> {
                        if (image_list_2 != null)
                            image_list_2.scrollBy(1, 0);
                    });
                    image_list_3.post(() -> {
                        if (image_list_3 != null)
                            image_list_3.scrollBy(1, 0);
                    });
                }
            }
        }, 0, 50);
    }

    private void openCustomDiscoverListActivity(String type, DiscoverItem item) {
        Intent intent = new Intent(mContext, CustomDiscoverListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("context", item.getData().getContext());
        intent.putExtra("data", new Gson().toJson(item));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                mContext,
                transformationLayout,
                "shared_element_container" // The transition name to be matched in Activity B.
        );
        mContext.startActivity(intent, options.toBundle());
        mContext.overridePendingTransition(0, 0);
    }
}
