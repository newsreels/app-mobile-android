package com.ziro.bullet.adapters.CommunityFeed;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.fragments.Reels.ReelsPageInterface;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.model.Reel.ReelsItem;

import java.util.ArrayList;

public class SuggestedReelsHolder extends RecyclerView.ViewHolder {

    private Activity context;
    public LinearLayoutManager layoutManager;
    private TextView title;
    private RelativeLayout titleMain;
    private LinearLayout rootCard;
    private RecyclerView listView;
    private boolean isDiscover;
    private boolean fromPlaces = false;
    ReelsPageInterface reelsPageInterface;
    private float preX = 0f;
    private float preY = 0f;
    private float x1, x2;
    private final float Y_BUFFER = 10;

    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private AdapterCallback adapterCallback;
    private DetailsActivityInterface listener;


    public SuggestedReelsHolder(View view, Activity context, boolean isDiscover, boolean fromPlaces) {
        super(view);
        this.context = context;
        this.fromPlaces = fromPlaces;
        this.isDiscover = isDiscover;
        titleMain = view.findViewById(R.id.titleMain);
        title = view.findViewById(R.id.title);
        listView = view.findViewById(R.id.items);
        rootCard = view.findViewById(R.id.rootCard);
    }

    public void addShareListener(ShowOptionsLoaderCallback showOptionsLoaderCallback,
                                 AdapterCallback adapterCallback,
                                 DetailsActivityInterface listener) {
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.adapterCallback = adapterCallback;
        this.listener = listener;
    }

    public void bind(String mTitle, ArrayList<ReelsItem> data, RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset) {
        titleMain.setVisibility(View.GONE);
        if (fromPlaces) {
            title.setTextColor(context.getResources().getColor(R.color.bullet_text));
        } else {
            if (isDiscover) {
                title.setTextColor(context.getResources().getColor(R.color.white));
            }
            title.setTextColor(context.getResources().getColor(R.color.theme_color_red));
//            rootCard.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
        }

        ReelItemsAdapter adapter = new ReelItemsAdapter(context, data);
        adapter.addShareListener(showOptionsLoaderCallback, adapterCallback, listener);
        adapter.addinterface(reelsPageInterface);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);
        adapter.addShareListener(showOptionsLoaderCallback, adapterCallback, listener);


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

    public void addinteface(ReelsPageInterface reelsPageInterface) {
        this.reelsPageInterface = reelsPageInterface;
    }
}
