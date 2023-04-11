package com.ziro.bullet.mediapicker.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ziro.bullet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * Created by JessYan on 2020/6/24 17:27
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class VideoTrimmerAdapter extends RecyclerView.Adapter {

    private List<Bitmap> mBitmaps = new ArrayList<>();
    private int mItemCount;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(new FrameItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position < mBitmaps.size()) {
            ((FrameItemView) holder.itemView).setImage(mBitmaps.get(position));
        } else {
            ((FrameItemView) holder.itemView).setImageResource((R.drawable.img_place_holder));
        }
    }

    public void setItemCount(int itemCount) {
        mItemCount = itemCount;
    }

    @Override
    public int getItemCount() {
        return mItemCount > 0 ? mItemCount : mBitmaps.size();
    }

    public void addBitmaps(Bitmap bitmap) {
        mBitmaps.add(bitmap);
        notifyDataSetChanged();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
