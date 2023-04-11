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
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;

public class DiscoverBigAdapter extends RecyclerView.Adapter<DiscoverBigAdapter.ViewHolder> {
    private Activity mContext;
    private ArrayList<Source> mFollowingModels;
    private FollowingCallback mCallback;
    private PrefConfig mPrefConfig;
    private String mType;
    private FollowUnfollowPresenter presenter;
    private String locc = null;

    public DiscoverBigAdapter(Activity mContext, ArrayList<Source> mFollowingModels, String mType) {
        this.mContext = mContext;
        this.mType = mType;
        this.mFollowingModels = mFollowingModels;
        this.presenter = new FollowUnfollowPresenter(mContext);
        mPrefConfig = new PrefConfig(mContext);
    }

    public void setCallback(FollowingCallback callback) {
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public DiscoverBigAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.source_item_v2, parent, false);
        return new DiscoverBigAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverBigAdapter.ViewHolder holder, int position) {
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

        holder.right.setText("" + mFollowingModels.get(position).getLanguage());
        holder.left.setText("" + mFollowingModels.get(position).getCategory());
        locc = mFollowingModels.get(position).getCategory();

        if (!TextUtils.isEmpty(mFollowingModels.get(position).getName())) {
            holder.label.setVisibility(View.VISIBLE);
            holder.label.setText(mFollowingModels.get(position).getName());
        } else {
            holder.label.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mFollowingModels.get(position).getFollower())) {
            holder.sublabel.setVisibility(View.VISIBLE);
            holder.sublabel.setText(mFollowingModels.get(position).getFollower());
        } else {
            holder.sublabel.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (mPrefConfig != null) {
                mPrefConfig.setSrcLang(mFollowingModels.get(position).getLanguage());
                mPrefConfig.setSrcLoc(locc);
            }
            if (mCallback != null) {
                mCallback.onItemClicked(mFollowingModels.get(position), mType);
            }
        });
        holder.follow_click.setOnClickListener(v -> {
            if (presenter == null) return;
            if (!mFollowingModels.get(position).isFavorite()) {
                holder.progress.setVisibility(View.VISIBLE);
                holder.follow.setVisibility(View.INVISIBLE);
                holder.follow.setImageResource(R.drawable.ic_bookmark_active);
                mFollowingModels.get(position).setFavorite(true);
                switch (mType) {
                    case "topic":
                        presenter.followTopic(mFollowingModels.get(position).getId(), position, (position1, flag) -> {
                            holder.progress.setVisibility(View.GONE);
                            holder.follow.setVisibility(View.VISIBLE);
                            if (!flag) {
                                holder.follow.setImageResource(R.drawable.ic_bookmark_inactive);
                                mFollowingModels.get(position).setFavorite(false);
                                notifyDataSetChanged();
                            }
                        });
                        if (mCallback != null) {
                            mCallback.onItemChanged(mFollowingModels.get(position));
                        }
                        break;
                    case "source":
                        presenter.followSource(mFollowingModels.get(position).getId(), position, (position1, flag) -> {
                            holder.progress.setVisibility(View.GONE);
                            holder.follow.setVisibility(View.VISIBLE);
                            if (!flag) {
                                holder.follow.setImageResource(R.drawable.ic_bookmark_inactive);
                                mFollowingModels.get(position).setFavorite(false);
                                notifyDataSetChanged();
                            }
                        });
                        if (mCallback != null) {
                            mCallback.onItemChanged(mFollowingModels.get(position));
                        }
                        break;
                }
            } else {
                holder.follow.setImageResource(R.drawable.ic_bookmark_inactive);
                mFollowingModels.get(position).setFavorite(false);
                switch (mType) {
                    case "topic":
                        presenter.unFollowTopic(mFollowingModels.get(position).getId(), position, (position1, flag) -> {
                            holder.progress.setVisibility(View.GONE);
                            holder.follow.setVisibility(View.VISIBLE);
                            if (!flag) {
                                holder.follow.setImageResource(R.drawable.ic_bookmark_inactive);
                                mFollowingModels.get(position).setFavorite(true);
                                notifyDataSetChanged();
                            }
                        });
                        if (mCallback != null) {
                            mCallback.onItemChanged(mFollowingModels.get(position));
                        }
                        break;
                    case "source":
                        presenter.unFollowSource(mFollowingModels.get(position).getId(), position, (position1, flag) -> {
                            holder.progress.setVisibility(View.GONE);
                            holder.follow.setVisibility(View.VISIBLE);
                            if (!flag) {
                                holder.follow.setImageResource(R.drawable.ic_bookmark_active);
                                mFollowingModels.get(position).setFavorite(true);
                                notifyDataSetChanged();
                            }
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
        private TextView sublabel;
        private ImageView follow;
        private RelativeLayout follow_click;
        private ProgressBar progress;
        private TextView left, right;

        private ViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View view) {
            follow_click = view.findViewById(R.id.follow_click);
            follow = view.findViewById(R.id.follow);
            left = itemView.findViewById(R.id.left);
            right = itemView.findViewById(R.id.right);
            image = view.findViewById(R.id.image);
            label = view.findViewById(R.id.label);
            sublabel = view.findViewById(R.id.subLabel);
            progress = view.findViewById(R.id.progress);
        }
    }
}
