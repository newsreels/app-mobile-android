package com.ziro.bullet.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.model.Tabs.DataItem;

import java.util.ArrayList;

public class TabsItemAdapter extends RecyclerView.Adapter<TabsItemAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<DataItem> model;
    private ItemCallback mCallback;

    public TabsItemAdapter(Context mContext, ArrayList<DataItem> model) {
        this.mContext = mContext;
        this.model = model;
    }

    public void setCallback(ItemCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public TabsItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.for_you_top_news_inner_item, parent, false);
        return new TabsItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TabsItemAdapter.ViewHolder holder, int position) {
        DataItem item = model.get(position);
        if (item != null) {
            if (!TextUtils.isEmpty(item.getTitle())) {
                holder.title.setText(item.getTitle().trim());
            }
            if (!TextUtils.isEmpty(item.getImage())) {
                holder.gradient.setVisibility(View.VISIBLE);
                holder.title.setTextColor(mContext.getResources().getColor(R.color.white));
                Picasso.get()
                        .load(item.getImage())
                        .into(holder.image);
            } else {
                holder.gradient.setVisibility(View.GONE);
                holder.title.setTextColor(mContext.getResources().getColor(R.color.popup_item_text));
            }
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onItemClicked(item, position);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return model.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView container;
        private TextView title;
        private ImageView image;
        private RelativeLayout gradient;

        private ViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View view) {
            gradient = view.findViewById(R.id.gradient);
            container = view.findViewById(R.id.container);
            title = view.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface ItemCallback {
        void onItemClicked(DataItem item, int position);
    }
}
