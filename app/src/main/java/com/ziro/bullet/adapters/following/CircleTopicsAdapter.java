package com.ziro.bullet.adapters.following;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.HashTagDetailsActivity;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CircleTopicsAdapter extends RecyclerView.Adapter<CircleTopicsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Topics> mFollowedTopics;
    private FollowUnfollowPresenter presenter;


    public CircleTopicsAdapter(Context context, ArrayList<Topics> mFollowedTopics) {
        this.context = context;
        this.mFollowedTopics = mFollowedTopics;
        presenter = new FollowUnfollowPresenter((Activity) context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.circle_topics_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        if (position < mFollowedTopics.size()) {
            holder.topicName.setText("#" + mFollowedTopics.get(position).getName());

            if (!TextUtils.isEmpty(mFollowedTopics.get(position).getImage())) {
                Glide.with(context)
                        .load(mFollowedTopics.get(position).getImage())
                        .override(200, 200)
                        .centerCrop()
                        .apply(new RequestOptions().placeholder(R.drawable.img_place_holder))
                        .into(holder.topicImage);
            }

            if (mFollowedTopics.get(position).isFavorite()) {
                holder.topicImage.setBorderColor(ContextCompat.getColor(context, R.color.grey_light));
                holder.topicImage.setBorderWidth(R.dimen._2sdp);
                holder.follow_btn_icon.setImageResource(R.drawable.ic_star_follow);
                holder.follow_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.follow_background));
            } else {
                holder.topicImage.setBorderColor(ContextCompat.getColor(context, R.color.grey_light));
                holder.topicImage.setBorderWidth(R.dimen._1sdp);
                holder.follow_btn_icon.setImageResource(R.drawable.ic_plus);
                holder.follow_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.unfollow_background));
            }

            holder.follow_btn.setOnClickListener(v -> {
                if (mFollowedTopics.size() == 0 || position >= mFollowedTopics.size()) {
                    return;
                }
                holder.follow_btn.setEnabled(false);
                holder.follow_btn_progress.setVisibility(View.VISIBLE);
                if (mFollowedTopics.size() == 0) {
                    return;
                }

                if (!mFollowedTopics.get(position).isFavorite()) {
                    presenter.followTopic(mFollowedTopics.get(position).getId(), position, (position1, flag) -> {
                        if (mFollowedTopics.size() == 0 || position1 >= mFollowedTopics.size()) {
                            return;
                        }
                        Constants.onFollowingChanges = true;
                        mFollowedTopics.get(position1).setFavorite(true);
                        holder.follow_btn.setEnabled(true);
                        holder.follow_btn_progress.setVisibility(View.GONE);
                        notifyItemChanged(position1);
                    });
                } else {
                    presenter.unFollowTopic(mFollowedTopics.get(position).getId(), position, (position1, flag) -> {
                        if (mFollowedTopics.size() == 0 || position1 >= mFollowedTopics.size()) {
                            return;
                        }
                        Constants.onFollowingChanges = true;
                        mFollowedTopics.get(position1).setFavorite(false);
                        holder.follow_btn.setEnabled(true);
                        holder.follow_btn_progress.setVisibility(View.GONE);
                        notifyItemChanged(position1);
                    });
                }
            });

            holder.itemView.setOnClickListener(v -> {
                if (mFollowedTopics.size() == 0 || position >= mFollowedTopics.size()) {
                    return;
                }
                Intent intent = new Intent(context, HashTagDetailsActivity.class);
                intent.putExtra("type", TYPE.TOPIC);
                intent.putExtra("id", mFollowedTopics.get(position).getId());
                intent.putExtra("mContext", mFollowedTopics.get(position).getContext());
                intent.putExtra("name", mFollowedTopics.get(position).getName());
                intent.putExtra("favorite", mFollowedTopics.get(position).isFavorite());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFollowedTopics.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView topicName;
        private RoundedImageView topicImage;
        private RelativeLayout follow_btn;
        private ImageView follow_btn_icon;
        private ProgressBar follow_btn_progress;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            topicName = itemView.findViewById(R.id.topicName);
            topicImage = itemView.findViewById(R.id.topicImage);
            follow_btn = itemView.findViewById(R.id.follow_btn);
            follow_btn_icon = itemView.findViewById(R.id.follow_btn_icon);
            follow_btn_progress = itemView.findViewById(R.id.follow_btn_progress);
        }
    }
}
