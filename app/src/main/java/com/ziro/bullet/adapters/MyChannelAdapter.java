package com.ziro.bullet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.utills.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;


public class MyChannelAdapter extends RecyclerView.Adapter<MyChannelAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Source> list;

    public MyChannelAdapter(Context context, ArrayList<Source> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_channels, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView channelImage;
        private final TextView channelName;
        private final TextView followCount;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            channelImage = itemView.findViewById(R.id.channel_image);
            channelName = itemView.findViewById(R.id.channel_name);
            followCount = itemView.findViewById(R.id.follow_count);
        }

        private void bind(Source source) {
            if (source != null) {
                Glide.with(context)
                        .load(source.getIcon())
                        .circleCrop()
                        .override(Constants.targetWidth,Constants.targetHeight)
                        .into(channelImage);
                channelName.setText(String.format("%s", source.getName()));
//                followCount.setText(String.format(Locale.getDefault(), "%d %s", source.getFollower_count(), context.getString(R.string.followers)));
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ChannelDetailsActivity.class);
                    intent.putExtra("id", source.getId());
                    context.startActivity(intent);
                });
//                showVerifiedIcon(authorViewHolder, author);
            }
        }
    }
}
