package com.ziro.bullet.adapters.following;

import android.app.Activity;
import android.content.Context;
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

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowingChannelsAdapter extends RecyclerView.Adapter<FollowingChannelsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Source> mFollowedChannels;
    private FollowUnfollowPresenter presenter;
    private PrefConfig mPrefConfig;
    private boolean fromReels;

    public FollowingChannelsAdapter(Context context, ArrayList<Source> mFollowedChannels, boolean fromReels) {
        this.context = context;
        this.mFollowedChannels = mFollowedChannels;
        this.fromReels = fromReels;
        presenter = new FollowUnfollowPresenter((Activity) context);
        mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_channels_v2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        if (position < mFollowedChannels.size()) {
            holder.text.setText(mFollowedChannels.get(position).getName());
            Picasso.get()
                    .load(mFollowedChannels.get(position).getImagePortraitOrNormal())
                    .resize(Constants.targetWidth, Constants.targetHeight)
                    .onlyScaleDown()
                    .error(R.drawable.img_place_holder)
                    .into(holder.image);

            if (mFollowedChannels.get(position).isFavorite()) {
                holder.bookmark.setProgress(1f);
            } else {
                holder.bookmark.setProgress(0f);
            }

            if (!TextUtils.isEmpty(mFollowedChannels.get(position).getName_image())) {
                holder.source.setVisibility(View.VISIBLE);
                holder.text.setVisibility(View.GONE);
                Glide.with(context)
                        .load(mFollowedChannels.get(position).getName_image())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(holder.source);
            } else {
                holder.source.setVisibility(View.GONE);
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setText(mFollowedChannels.get(position).getName());
            }

            holder.markMain.setOnClickListener(v -> {
                if (mFollowedChannels.size() == 0 || position >= mFollowedChannels.size()) {
                    return;
                }
                holder.bookmark.setEnabled(false);
                Utils.followAnimation(holder.bookmark, 500);

                if (!mFollowedChannels.get(position).isFavorite()) {
                    presenter.followSource(mFollowedChannels.get(position).getId(), position, (position1, flag) -> {
                        if (mFollowedChannels.size() == 0 || position1 >= mFollowedChannels.size()) {
                            return;
                        }
                        mFollowedChannels.get(position1).setFavorite(true);
                        holder.bookmark.setEnabled(true);
                    });
                } else {
                    presenter.unFollowSource(mFollowedChannels.get(position).getId(), position, (position1, flag) -> {
                        if (mFollowedChannels.size() == 0 || position1 >= mFollowedChannels.size()) {
                            return;
                        }
                        mFollowedChannels.get(position1).setFavorite(false);
                        holder.bookmark.setEnabled(true);
                    });
                }
            });

            holder.card.setOnClickListener(v -> {
                if (mFollowedChannels.size() == 0 || position >= mFollowedChannels.size()) {
                    return;
                }

                Intent intent;
                if (fromReels) {
                    intent = new Intent(context, ReelInnerActivity.class);
                    intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, mFollowedChannels.get(position).getContext());
                    intent.putExtra(ReelInnerActivity.REEL_F_TITLE, mFollowedChannels.get(position).getName());
                } else {
                    intent = new Intent(context, ChannelDetailsActivity.class);
                    intent.putExtra("type", TYPE.SOURCE);
                    intent.putExtra("id", mFollowedChannels.get(position).getId());
                    intent.putExtra("name", mFollowedChannels.get(position).getName());
                    intent.putExtra("favorite", mFollowedChannels.get(position).isFavorite());
                }
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFollowedChannels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private RelativeLayout markMain;
        private ImageView image;
        private ImageView source;
        private TextView text;
        private LottieAnimationView bookmark;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            markMain = itemView.findViewById(R.id.main);
            image = itemView.findViewById(R.id.image);
            source = itemView.findViewById(R.id.source);
            text = itemView.findViewById(R.id.label);
            bookmark = itemView.findViewById(R.id.bookmark);
        }
    }
}
