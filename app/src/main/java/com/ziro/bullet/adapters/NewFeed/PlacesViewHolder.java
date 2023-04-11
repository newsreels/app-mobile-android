package com.ziro.bullet.adapters.NewFeed;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.following.FollowingPlacesAdapter;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.fragments.SearchModifiedFragment;

import java.util.ArrayList;

public class PlacesViewHolder extends RecyclerView.ViewHolder {

    private Activity context;
    private GridLayoutManager layoutManager;
    private TextView title;
    private RecyclerView listView;
    private boolean isDiscover = false;
    private SearchModifiedFragment.OnFragmentInteractionListener listener;

    public PlacesViewHolder(View view, Activity context, boolean isDiscover, SearchModifiedFragment.OnFragmentInteractionListener listener) {
        super(view);
        this.listener = listener;
        this.context = context;
        this.isDiscover = isDiscover;
        title = view.findViewById(R.id.title);
        listView = view.findViewById(R.id.items);
    }

    public void bind(String mTitle, ArrayList<Location> data) {
        if (isDiscover) {
            title.setTextColor(context.getResources().getColor(R.color.white));
        }
        if (!TextUtils.isEmpty(mTitle)) {
            title.setVisibility(View.VISIBLE);
            title.setText(mTitle);
        } else {
            title.setVisibility(View.GONE);
        }
        if (data != null && data.size() > 0) {
            FollowingPlacesAdapter adapter = new FollowingPlacesAdapter(context, data, true);
            layoutManager = new GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false);
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(adapter);
        }
    }
}
