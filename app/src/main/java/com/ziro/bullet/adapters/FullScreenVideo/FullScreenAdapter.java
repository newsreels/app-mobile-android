package com.ziro.bullet.adapters.FullScreenVideo;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.DurationCallback;
import com.ziro.bullet.model.FullScreenVideo;

import java.util.ArrayList;

import im.ene.toro.CacheManager;


public class FullScreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CacheManager {

    private static final int TYPE_YOUTUBE = 0;
    private static final int TYPE_VIDEO = 1;
    private Activity context;
    private DurationCallback callback;
    private ArrayList<FullScreenVideo> items;

    public FullScreenAdapter(Activity context, ArrayList<FullScreenVideo> items, DurationCallback callback) {
        this.context = context;
        this.callback = callback;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_YOUTUBE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
                view.findViewById(R.id.youtube).setVisibility(View.VISIBLE);
                view.findViewById(R.id.video_player).setVisibility(View.GONE);
                view.findViewById(R.id.bottomSpace).setVisibility(View.VISIBLE);
                return new YoutubeViewHolder(view, context, callback);

            case TYPE_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
                view.findViewById(R.id.youtube).setVisibility(View.GONE);
                view.findViewById(R.id.video_player).setVisibility(View.VISIBLE);
                view.findViewById(R.id.bottomSpace).setVisibility(View.GONE);
                return new VideoViewHolder(view, context, callback);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FullScreenVideo video = items.get(position);
        if (holder instanceof YoutubeViewHolder) {
            ((YoutubeViewHolder) holder).bind(position, video);
        } else if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).bind(position, video);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FullScreenVideo fullScreenVideo = null;
        if (items.size() > position && position >= 0) {
            fullScreenVideo = items.get(position);
        }
        if (fullScreenVideo != null && !TextUtils.isEmpty(fullScreenVideo.getType())) {
            if (fullScreenVideo.getType().equalsIgnoreCase("YOUTUBE")) {
                return TYPE_YOUTUBE;
            } else return TYPE_VIDEO;
        }
        return TYPE_VIDEO;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(0);
        if (holder instanceof YoutubeViewHolder) {
            ((YoutubeViewHolder) holder).release();
        }
    }

    @Nullable
    @Override
    public Object getKeyForOrder(int order) {
        return null;
    }

    @Nullable
    @Override
    public Integer getOrderForKey(@NonNull Object key) {
        return null;
    }

    public void remove(int position) {
        if (items != null && position < items.size()) {
            items.remove(position);
        }
    }
}
