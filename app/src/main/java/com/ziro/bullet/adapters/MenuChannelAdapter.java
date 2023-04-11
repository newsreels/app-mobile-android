package com.ziro.bullet.adapters;

import android.app.Activity;
import android.content.Intent;
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
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class MenuChannelAdapter extends RecyclerView.Adapter<MenuChannelAdapter.ViewHolder> {

    private ArrayList<Source> data;
    private PrefConfig mPrefConfig;
    private Activity context;

    public MenuChannelAdapter(ArrayList<Source> data, Activity context) {
        this.data = data;
        this.context = context;
        mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_channel_list_item, parent, false);
        return new MenuChannelAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data != null && data.size() > 0) {
            Source item = data.get(position);

            if (!TextUtils.isEmpty(item.getImagePortraitOrNormal())) {
                Glide.with(context)
                        .load(item.getImagePortraitOrNormal())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(holder.image);
            } else {
                holder.image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            if (!TextUtils.isEmpty(item.getName_image())) {
                holder.source.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(item.getName_image())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .centerCrop()
                        .into(holder.source);
            } else {
                holder.source.setVisibility(View.GONE);
                holder.tvName.setText(item.getName());
            }

            if (item.isFavorite()) {
                holder.mark.setImageResource(R.drawable.ic_tick);
            } else {
                holder.mark.setImageResource(R.drawable.ic_plus);
            }

            holder.card.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChannelDetailsActivity.class);
                intent.putExtra("id", item.getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView card;
        private final ImageView image;
        private final ImageView source;
        private final ImageView mark;
        private final RelativeLayout markMain;
        private final TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            image = itemView.findViewById(R.id.image);
            mark = itemView.findViewById(R.id.mark);
            source = itemView.findViewById(R.id.source);
            markMain = itemView.findViewById(R.id.markMain);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
