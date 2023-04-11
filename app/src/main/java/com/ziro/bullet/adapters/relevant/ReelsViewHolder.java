package com.ziro.bullet.adapters.relevant;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CommunityFeed.ReelItemsAdapter;
import com.ziro.bullet.interfaces.ViewItemClickListener;
import com.ziro.bullet.model.Reel.ReelsItem;

import java.util.ArrayList;

public class ReelsViewHolder extends RecyclerView.ViewHolder {

    private final RecyclerView listView;
//    private final TextView seeAll;
    public LinearLayoutManager layoutManager;

    public ReelsViewHolder(@NonNull View itemView) {
        super(itemView);
        listView = itemView.findViewById(R.id.list_view);
//        seeAll = itemView.findViewById(R.id.see_all);

    }

    public void bind(Activity context, ArrayList<ReelsItem> reelsArrayList,
                     ViewItemClickListener viewItemClickListenerAuthor,
                     RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset, ViewItemClickListener viewItemClickListener) {

        ReelItemsAdapter adapter = new ReelItemsAdapter(context, reelsArrayList);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);

        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);

        listView.addOnScrollListener(onScrollListener);
//        seeAll.setVisibility(reelsArrayList.size() > 1 ? View.VISIBLE : View.GONE);
//        seeAll.setOnClickListener(v -> viewItemClickListener.itemClickedData(v, ViewItemClickListener.TYPE_RELEVANT_AUTHOR_SEE_ALL, null));
    }
}
