package com.ziro.bullet.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.model.Tabs.NewsItem;
import com.ziro.bullet.utills.SpacesItemDecoration;

import java.util.ArrayList;

/**
 * Created by shine_joseph on 21/05/20.
 */

public class TabsAdapter extends RecyclerView.Adapter<TabsAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<NewsItem> model;
    private TabCallback mCallback;
    private PrefConfig prefConfig;

    public TabsAdapter(Context mContext, ArrayList<DataItem> model) {
        this.mContext = mContext;
//        this.model = model;
        this.prefConfig = new PrefConfig(mContext);
    }

    public void setCallback(TabCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.for_you_top_news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsItem item = model.get(position);
        if (item != null) {
            holder.heading.setText(item.getHeaderText());
            if (!TextUtils.isEmpty(item.getImage())) {
                Glide.with(mContext)
                        .load(item.getImage())
                        .into(holder.image);
            }
            GridLayoutManager manager = new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false);
            holder.recyclerView.addItemDecoration(new SpacesItemDecoration(10));
            holder.recyclerView.setLayoutManager(manager);
            TabsItemAdapter bigAdapter = new TabsItemAdapter(mContext, item.getData());
            bigAdapter.setCallback((item1, position1) -> {
                if (mCallback != null) {
                    mCallback.onItemSelect(item1, position, position1);
                }
            });
            holder.recyclerView.setAdapter(bigAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView heading;
        private RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            heading = itemView.findViewById(R.id.heading);
            recyclerView = itemView.findViewById(R.id.recycler);
        }
    }

    public interface TabCallback {
        void onItemSelect(DataItem item, int mainPosition, int innerPosition);
    }
}
