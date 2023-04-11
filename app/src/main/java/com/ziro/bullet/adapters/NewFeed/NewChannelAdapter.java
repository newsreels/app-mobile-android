package com.ziro.bullet.adapters.NewFeed;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class NewChannelAdapter extends RecyclerView.Adapter<NewChannelAdapter.ViewHolder> {

    private final ArrayList<Source> data;
    private PrefConfig mPrefConfig;
    private Activity context;
    private boolean myChannel;
    private FollowUnfollowPresenter presenter;

    public NewChannelAdapter(Activity context, ArrayList<Source> data, boolean myChannel) {
        this.myChannel = myChannel;
        this.data = data;
        this.context = context;
        mPrefConfig = new PrefConfig(context);
        presenter = new FollowUnfollowPresenter(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_author_item, parent, false);
        return new ViewHolder(view);
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

            holder.title.setText(item.getName());
            if (!TextUtils.isEmpty(item.getUsername())) {
                holder.username.setText("@" + item.getUsername());
                holder.username.setVisibility(View.VISIBLE);
            } else {
                holder.username.setVisibility(View.INVISIBLE);
            }
            if (item.isFavorite()) {
                holder.channel_btn_color.setBackgroundColor(context.getResources().getColor(R.color.edittextHint));
                holder.channel_btn_txt.setText(context.getString(R.string.following));

            } else {
                holder.channel_btn_color.setBackgroundColor(context.getResources().getColor(R.color.theme_color_1));
                holder.channel_btn_txt.setText(context.getString(R.string.follow));
            }

            holder.channel_btn.setOnClickListener(v -> {
                holder.channel_btn.setEnabled(false);
                holder.channel_btn_txt.setVisibility(View.INVISIBLE);
                holder.channel_btn_progress.setVisibility(View.VISIBLE);

                if (!item.isFavorite()) {
                    presenter.followSource(item.getId(), position, (position1, flag) -> {
                        item.setFavorite(true);
                        Constants.onFollowingChanges = true;
                        holder.channel_btn.setEnabled(true);
                        holder.channel_btn_txt.setText(context.getString(R.string.following));
                        holder.channel_btn_progress.setVisibility(View.GONE);
                        holder.channel_btn_txt.setVisibility(View.VISIBLE);
                        notifyItemChanged(position);
                    });
                } else {
                    presenter.unFollowSource(item.getId(), position, (position1, flag) -> {
                        item.setFavorite(false);
                        Constants.onFollowingChanges = true;
                        holder.channel_btn.setEnabled(true);
                        holder.channel_btn_txt.setText(context.getString(R.string.follow));
                        holder.channel_btn_progress.setVisibility(View.GONE);
                        holder.channel_btn_txt.setVisibility(View.VISIBLE);

                        notifyItemChanged(position);
                    });
                }
            });

            holder.card.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChannelDetailsActivity.class);
                intent.putExtra("id", item.getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView channel_btn;
        private final LinearLayout channel_btn_color;
        private final RoundedImageView image;
        private final CardView card;
        private final TextView title;
        private final TextView username;
        private final TextView channel_btn_txt;
        private final ProgressBar channel_btn_progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channel_btn_txt = itemView.findViewById(R.id.name);
            channel_btn_color = itemView.findViewById(R.id.item_color);
            channel_btn = itemView.findViewById(R.id.bookmark);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            channel_btn_progress = itemView.findViewById(R.id.progress);
            card = itemView.findViewById(R.id.card);
        }
    }
}
