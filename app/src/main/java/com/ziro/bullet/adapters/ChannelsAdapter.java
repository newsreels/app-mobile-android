package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Source> channels;
    private ChannelCallback mCallback;
    private int type = Constants.MENU_CHANNELS;

    public ChannelsAdapter(Activity mContext, ArrayList<Source> channels, int type) {
        this.mContext = mContext;
        this.channels = channels;
        this.type = type;
    }

    public void setCallback(ChannelCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_channel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (channels != null && channels.size() > 0) {
            holder.image.setImageDrawable(null);
            Source source = channels.get(position);
            if (source != null) {
                if (source.getId().equalsIgnoreCase(Constants.CreateChannel)) {
                    holder.image.setImageDrawable(mContext.getDrawable(R.drawable.ic_add_channel));
                    holder.name.setTextColor(mContext.getResources().getColor(R.color.textSubTitleGrey));
                    holder.update.setVisibility(View.GONE);
                    holder.dot_.setVisibility(View.GONE);
                    holder.label.setVisibility(View.GONE);
                    holder.contactUs.setVisibility(View.GONE);
                    holder.channelMain.setOnClickListener(view -> {
                        if (mCallback != null)
                            mCallback.onCreateChannel();
                    });
                } else if (source.getId().equalsIgnoreCase(Constants.MoreChannel)) {
                    holder.contactUs.setVisibility(View.VISIBLE);
                    holder.channelMain.setVisibility(View.GONE);
                    holder.contactUs.setOnClickListener(view -> {
                        if (mCallback != null)
                            mCallback.onContactUs();
                    });
                } else {
                    holder.label.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(source.getIcon())) {
                        Glide.with(mContext).load(source.getIcon())
                                .placeholder(R.drawable.img_place_holder)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(holder.image);
                    } else {
                        holder.image.setImageResource(R.drawable.img_place_holder);
                    }
                    holder.name.setTextColor(mContext.getResources().getColor(R.color.title_bar_title));

                    if (type == Constants.MENU_CHANNELS) {
                        if (source.getUpdate_count() > 0) {
                            holder.update.setVisibility(View.VISIBLE);
                            holder.dot_.setVisibility(View.VISIBLE);
                            holder.update.setText(source.getUpdate_count() + " " + mContext.getResources().getString(R.string.updates));
                        } else {
                            holder.update.setVisibility(View.GONE);
                            holder.dot_.setVisibility(View.GONE);
                        }
                    } else {
                        holder.label.setVisibility(View.VISIBLE);
                        holder.label.setText(source.getUpdate_count() + " " + mContext.getString(R.string.followers));
                    }
                    holder.channelMain.setOnClickListener(view -> {
                        if (mCallback != null)
                            mCallback.onItemClicked(source);
                    });
                }
                if (!TextUtils.isEmpty(source.getName())) {
                    holder.name.setText(source.getName());
                }

                if (position == getItemCount() - 1) {
                    holder.separator_line.setVisibility(View.GONE);
                } else
                    holder.separator_line.setVisibility(View.VISIBLE);
                switch (type) {
                    case Constants.POST_TO_CHANNELS:
                        holder.channelMain.setBackgroundColor(mContext.getResources().getColor(R.color.bottombar_bg));
                        break;
                    case Constants.MENU_CHANNELS:
                        holder.channelMain.setBackgroundColor(mContext.getResources().getColor(R.color.card_bg));
                        holder.dot_.setVisibility(View.GONE);
                        holder.update.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public interface ChannelCallback {
        void onItemClicked(Source source);

        void onCreateChannel();

        void onContactUs();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout channelMain;
        private RoundedImageView image;
        private TextView name;
        private TextView label;
        private ImageView dot_;
        private TextView update;
        private LinearLayout contactUs;
        private View separator_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channelMain = itemView.findViewById(R.id.channelMain);
            contactUs = itemView.findViewById(R.id.contactUs);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            label = itemView.findViewById(R.id.label);
            dot_ = itemView.findViewById(R.id.dot_);
            update = itemView.findViewById(R.id.update);
            separator_line = itemView.findViewById(R.id.separator_line);
        }
    }
}
