package com.ziro.bullet.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.R;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;

public class DiscoverSmallAdapter extends RecyclerView.Adapter<DiscoverSmallAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Source> mFollowingModels;
    private FollowingCallback mCallback;
    private String mType;
    private FollowUnfollowPresenter presenter;

    public DiscoverSmallAdapter(Activity mContext, ArrayList<Source> mFollowingModels, String mType) {
        this.mContext = mContext;
        this.mFollowingModels = mFollowingModels;
        this.mType = mType;
        this.presenter = new FollowUnfollowPresenter(mContext);
    }

    public void setCallback(FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public DiscoverSmallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.topic_item_v2, parent, false);
        return new DiscoverSmallAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverSmallAdapter.ViewHolder holder, int position) {
        if (mFollowingModels.get(position).isFavorite()) {
            holder.follow.setImageResource(R.drawable.ic_bookmark_active);
        } else {
            holder.follow.setImageResource(R.drawable.ic_bookmark_inactive);
        }

        if (!TextUtils.isEmpty(mFollowingModels.get(position).getImage())) {
            Picasso.get()
                    .load(mFollowingModels.get(position).getImage())
                    .resize(Constants.targetWidth, Constants.targetHeight)
                    .onlyScaleDown()
                    .error(R.drawable.img_place_holder)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.img_place_holder);
        }
        holder.label.setText(mFollowingModels.get(position).getName());
        if (!TextUtils.isEmpty(mFollowingModels.get(position).getFollower())) {
            holder.subLabel.setVisibility(View.VISIBLE);
            holder.subLabel.setText(mFollowingModels.get(position).getFollower());
        } else {
            holder.subLabel.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (mCallback != null) {
                mCallback.onItemClicked(mFollowingModels.get(position), mType);
            }
        });
        holder.follow_click.setOnClickListener(v -> {
            if (presenter == null) return;
            holder.progress.setVisibility(View.VISIBLE);
            holder.follow.setVisibility(View.INVISIBLE);
            if (!mFollowingModels.get(position).isFavorite()) {
                mFollowingModels.get(position).setFavorite(true);
                switch (mType) {
                    case "topic":
                        presenter.followTopic(mFollowingModels.get(position).getId(), position, (position1, flag) -> {
                            holder.progress.setVisibility(View.GONE);
                            holder.follow.setVisibility(View.VISIBLE);
//                            if (!flag) {
                            mFollowingModels.get(position).setFavorite(flag);
                            notifyDataSetChanged();
//                            }
                            if (mCallback != null) {
                                mCallback.onItemChanged(mFollowingModels.get(position));
                            }
                        });
                        break;
                    case "source":
                        presenter.followSource(mFollowingModels.get(position).getId(), position, (position1, flag) -> {
                            holder.progress.setVisibility(View.GONE);
                            holder.follow.setVisibility(View.VISIBLE);
//                            if (!flag) {
                            mFollowingModels.get(position).setFavorite(flag);
                            notifyDataSetChanged();
//                            }
                        });
                        if (mCallback != null) {
                            mCallback.onItemChanged(mFollowingModels.get(position));
                        }
                        break;
                }
            } else {
                mFollowingModels.get(position).setFavorite(false);
                switch (mType) {
                    case "topic":
                        presenter.unFollowTopic(mFollowingModels.get(position).getId(), position, (position1, flag) -> {
                            holder.progress.setVisibility(View.GONE);
                            holder.follow.setVisibility(View.VISIBLE);
//                            if (!flag) {
                            mFollowingModels.get(position).setFavorite(!flag);
                            notifyDataSetChanged();
//                            }
                        });
                        if (mCallback != null) {
                            mCallback.onItemChanged(mFollowingModels.get(position));
                        }
                        break;
                    case "source":
                        presenter.unFollowSource(mFollowingModels.get(position).getId(), position, (position1, flag) -> {
                            holder.progress.setVisibility(View.GONE);
                            holder.follow.setVisibility(View.VISIBLE);
//                            if (!flag) {
                            mFollowingModels.get(position).setFavorite(!flag);
                            notifyDataSetChanged();
//                            }
                        });
                        if (mCallback != null) {
                            mCallback.onItemChanged(mFollowingModels.get(position));
                        }
                        break;
                }
            }
        });
    }

    public ArrayList<Source> getSelected() {
        ArrayList<Source> list = new ArrayList<>();
        if (mFollowingModels != null && mFollowingModels.size() > 0) {
            for (Source item : mFollowingModels) {
                if (item.isFavorite()) {
                    list.add(item);
                }
            }
        }
        return list;
    }


    @Override
    public int getItemCount() {
        return mFollowingModels.size();
    }

    public interface FollowingCallback {
        void onItemChanged(Source source);

        void onItemClicked(Source source, String type);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView label;
        private TextView subLabel;
        private ImageView follow;
        private RelativeLayout follow_click;
        private ProgressBar progress;

        private ViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View view) {
            follow_click = view.findViewById(R.id.follow_click);
            follow = view.findViewById(R.id.follow);
            image = view.findViewById(R.id.img);
            label = view.findViewById(R.id.label);
            subLabel = view.findViewById(R.id.subLabel);
            progress = view.findViewById(R.id.progress);
        }
    }
}
