package com.ziro.bullet.adapters.following;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowingTopicsAdapter extends RecyclerView.Adapter<FollowingTopicsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Topics> mFollowedTopics;
    private FollowUnfollowPresenter presenter;


    public FollowingTopicsAdapter(Context context, ArrayList<Topics> mFollowedTopics) {
        this.context = context;
        this.mFollowedTopics = mFollowedTopics;
        presenter = new FollowUnfollowPresenter((Activity) context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_topics_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        if (position < mFollowedTopics.size()) {
            holder.text.setText("#" + mFollowedTopics.get(position).getName());

            Glide.with(holder.itemView.getContext())
                    .load(mFollowedTopics.get(position).getImage())
                    .into(holder.ivTopicIcon);

            if (mFollowedTopics.get(position).isFavorite()) {
                holder.ivFollowTopic.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_tick));
//                holder.starIcon.setImageResource(R.drawable.ic_star_follow);
//                holder.pin.setCardBackgroundColor(context.getResources().getColor(R.color.theme_color_1));
//                holder.pin_icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                holder.ivFollowTopic.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_plus));
//                holder.starIcon.setImageResource(R.drawable.ic_star_unfollow);
//                holder.pin.setCardBackgroundColor(context.getResources().getColor(R.color.white));
//                holder.pin_icon.setColorFilter(ContextCompat.getColor(context, R.color.toolbar_icon), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            holder.ivFollowTopic.setOnClickListener(v -> {
                if (mFollowedTopics.size() == 0 || position >= mFollowedTopics.size()) {
                    return;
                }
                holder.ivFollowTopic.setEnabled(false);
                if (mFollowedTopics.size() == 0) {
                    return;
                }

                if (!mFollowedTopics.get(position).isFavorite()) {
                    presenter.followTopic(mFollowedTopics.get(position).getId(), position, (position1, flag) -> {
                        if (mFollowedTopics.size() == 0 || position1 >= mFollowedTopics.size()) {
                            return;
                        }
                        mFollowedTopics.get(position1).setFavorite(true);
                        holder.ivFollowTopic.setEnabled(true);
                        notifyItemChanged(position1);
                    });
                } else {
                    presenter.unFollowTopic(mFollowedTopics.get(position).getId(), position, (position1, flag) -> {
                        if (mFollowedTopics.size() == 0 || position1 >= mFollowedTopics.size()) {
                            return;
                        }
                        mFollowedTopics.get(position1).setFavorite(false);
                        holder.ivFollowTopic.setEnabled(true);
                        notifyItemChanged(position1);
                    });
                }
            });

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mFollowedTopics.size() == 0 || position >= mFollowedTopics.size()) {
//                        return;
//                    }
////                    Intent intent = new Intent(context, ChannelPostActivity.class);
//                    Intent intent = new Intent(context, HashTagDetailsActivity.class);
//                    intent.putExtra("type", TYPE.TOPIC);
//                    intent.putExtra("id", mFollowedTopics.get(position).getId());
//                    intent.putExtra("mContext", mFollowedTopics.get(position).getContext());
//                    intent.putExtra("name", mFollowedTopics.get(position).getName());
//                    intent.putExtra("favorite", mFollowedTopics.get(position).isFavorite());
//                    context.startActivity(intent);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return mFollowedTopics.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private ImageView ivFollowTopic;
        private ImageView ivTopicIcon;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_suggested_topics_title);
            ivFollowTopic = itemView.findViewById(R.id.iv_follow_topic);
            ivTopicIcon = itemView.findViewById(R.id.iv_suggested_topic_icon);
        }
    }
}
