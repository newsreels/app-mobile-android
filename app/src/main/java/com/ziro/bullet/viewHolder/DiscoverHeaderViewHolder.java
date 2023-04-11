package com.ziro.bullet.viewHolder;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.discover.ImageScrollAdapter;
import com.ziro.bullet.adapters.discover.ImageScrollTopicAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.model.NewDiscoverPage.Icon;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DiscoverHeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView text1;
    private TextView text2;
    private Activity activity;
    private PrefConfig mPrefConfig;
    private RecyclerView image_list;
    private RecyclerView image_list_2;
    private RecyclerView image_list_3;
    private Timer myTimer;
    private Callback callback = new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(Exception e) {

        }
    };
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

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
                        //openCustomDiscoverListActivity(type, item);
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

    public DiscoverHeaderViewHolder(View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
        image_list = itemView.findViewById(R.id.image_list);
        image_list_2 = itemView.findViewById(R.id.image_list_2);
        image_list_3 = itemView.findViewById(R.id.image_list_3);
        mPrefConfig = new PrefConfig(activity);
    }

    public void bind(int position, DiscoverItem item) {
        if (item != null && item.getData() != null) {
            text1.setText(item.getSubtitle());
            text2.setText(item.getTitle());

            LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            LinearLayoutManager layoutManager3 = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

            ArrayList<Icon> iconArrayList1 = new ArrayList<>();
            ArrayList<Icon> iconArrayList2 = new ArrayList<>();
            ArrayList<Icon> iconArrayList3 = new ArrayList<>();

            if (!TextUtils.isEmpty(item.getType()) && item.getType().equalsIgnoreCase("IMAGE_TOPICS")) {

                for (int i = 0; i < item.getData().getIcons().size(); i++) {
                    if (i % 3 == 0) {
                        iconArrayList1.add(item.getData().getIcons().get(i));
                    } else if (i % 3 == 1) {
                        iconArrayList2.add(item.getData().getIcons().get(i));
                    } else {
                        iconArrayList3.add(item.getData().getIcons().get(i));
                    }
                }
            }else{
                for (int i = 0; i < item.getData().getIcons().size(); i++) {
                    if (i % 2 == 0) {
                        iconArrayList1.add(item.getData().getIcons().get(i));
                    } else {
                        iconArrayList2.add(item.getData().getIcons().get(i));
                    }
                }
            }

            image_list.setLayoutManager(layoutManager);
            image_list.setItemAnimator(new DefaultItemAnimator());
            image_list.setOnTouchListener(onTouchListener);

            image_list_2.setLayoutManager(layoutManager2);
            image_list_2.setItemAnimator(new DefaultItemAnimator());
            image_list_2.setOnTouchListener(onTouchListener);



            if (!TextUtils.isEmpty(item.getType()) && item.getType().equalsIgnoreCase("IMAGE_TOPICS")) {
                image_list_3.setLayoutManager(layoutManager3);
                image_list_3.setItemAnimator(new DefaultItemAnimator());
                image_list_3.setOnTouchListener(onTouchListener);


                image_list.setAdapter(new ImageScrollTopicAdapter(iconArrayList1, callback));
                image_list_2.setAdapter(new ImageScrollTopicAdapter(iconArrayList2, callback));
                image_list_3.setAdapter(new ImageScrollTopicAdapter(iconArrayList3, callback));
                image_list_3.setVisibility(View.VISIBLE);
            } else {
                image_list.setAdapter(new ImageScrollAdapter(iconArrayList1, callback));
                image_list_2.setAdapter(new ImageScrollAdapter(iconArrayList2, callback));
                // image_list_3.setAdapter(new ImageScrollAdapter(iconArrayList3, callback));
                image_list_3.setVisibility(View.GONE);
            }

            startAnimator();
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
                    image_list.post(() -> image_list.scrollBy(1, 0));
                    image_list_2.post(() -> image_list_2.scrollBy(1, 0));
                    image_list_3.post(() -> image_list_3.scrollBy(1, 0));
                }
            }
        }, 0, 50);
    }
}
