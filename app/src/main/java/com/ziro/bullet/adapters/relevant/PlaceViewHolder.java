package com.ziro.bullet.adapters.relevant;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.following.CirclePlaces2Adapter;
import com.ziro.bullet.data.models.relevant.Location;
import com.ziro.bullet.interfaces.ViewItemClickListener;

import java.util.ArrayList;

public class PlaceViewHolder extends RecyclerView.ViewHolder {

    private final RecyclerView listView;
    public LinearLayoutManager layoutManager;

    public PlaceViewHolder(@NonNull View itemView) {
        super(itemView);
        listView = itemView.findViewById(R.id.list_view);
    }

    public void bind(Context context, ArrayList<Location> locationArrayList,
                     SearchRelevantMainAdapter.FollowingCallback followingCallback1,
                     RecyclerView.OnScrollListener onScrollListener,
                     int lastSeenFirstPosition, float offset, ViewItemClickListener viewItemClickListener) {

        CirclePlaces2Adapter adapter = new CirclePlaces2Adapter(context, locationArrayList);
        layoutManager = new LinearLayoutManager(context,  LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(lastSeenFirstPosition, (int) offset);
        listView.setLayoutManager(layoutManager);
        listView.setAdapter(adapter);
        listView.addOnScrollListener(onScrollListener);

    }
}
