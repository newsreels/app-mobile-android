package com.ziro.bullet.viewHolder;

import android.content.Context;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.interfaces.CommunityItemCallback;

public class LastItemViewHolder extends RecyclerView.ViewHolder {

    private CommunityItemCallback mCommunityItemCallback;
    private Context context;
    private CardView backToTop;

    public LastItemViewHolder(View view, Context context, CommunityItemCallback mCommunityItemCallback) {
        super(view);
        this.mCommunityItemCallback = mCommunityItemCallback;
        this.context = context;
        backToTop = view.findViewById(R.id.backToTop);
    }

    public void listener() {
        backToTop.setOnClickListener(v -> {
            if (mCommunityItemCallback != null)
                mCommunityItemCallback.onItemClick("back_to_top", null);
        });
    }
}