package com.ziro.bullet.adapters.following;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.HashTagDetailsActivity;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowingPlacesAdapter extends RecyclerView.Adapter<FollowingPlacesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Location> mFollowedLocations;
    private FollowUnfollowPresenter presenter;
    private boolean darkModeOnly;

    public FollowingPlacesAdapter(Context context, ArrayList<Location> mFollowedLocations, boolean darkModeOnly) {
        this.darkModeOnly = darkModeOnly;
        this.context = context;
        this.mFollowedLocations = mFollowedLocations;
        presenter = new FollowUnfollowPresenter((Activity) context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.relevant_subitem_topics, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position < mFollowedLocations.size()) {
            holder.text.setText(mFollowedLocations.get(position).getName());

            Picasso.get()
                    .load(mFollowedLocations.get(position).getImage())
                    .error(R.drawable.img_place_holder)
                    .into(holder.locationImage);

            if (mFollowedLocations.get(position).isFavorite()) {
                holder.locationImage.setBorderColor(ContextCompat.getColor(context, R.color.grey_light));
                holder.locationImage.setBorderWidth(R.dimen._2sdp);
                holder.follow_btn_icon.setImageResource(R.drawable.ic_star_follow);
                holder.follow_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.follow_background));
            } else {
                holder.locationImage.setBorderColor(ContextCompat.getColor(context, R.color.grey_light));
                holder.locationImage.setBorderWidth(R.dimen._1sdp);
                holder.follow_btn_icon.setImageResource(R.drawable.ic_plus);
                holder.follow_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.unfollow_background));
            }

            holder.follow_btn.setOnClickListener(v -> {
                if (mFollowedLocations.size() == 0 || position >= mFollowedLocations.size()) {
                    return;
                }

                if (position < mFollowedLocations.size()) {
                    holder.follow_btn.setEnabled(false);
                    holder.follow_btn_progress.setVisibility(View.VISIBLE);

                    if (!mFollowedLocations.get(position).isFavorite()) {
                        presenter.followLocation(mFollowedLocations.get(position).getId(), position, (position1, flag) -> {
                            if (mFollowedLocations.size() == 0 || position1 >= mFollowedLocations.size()) {
                                return;
                            }
                            mFollowedLocations.get(position1).setFavorite(true);
                            holder.follow_btn.setEnabled(true);
                            holder.follow_btn_progress.setVisibility(View.GONE);

                            notifyItemChanged(position1);
                        });
                    } else {
                        presenter.unFollowLocation(mFollowedLocations.get(position).getId(), position, (position1, flag) -> {
                            if (mFollowedLocations.size() == 0 || position1 >= mFollowedLocations.size()) {
                                return;
                            }
                            mFollowedLocations.get(position1).setFavorite(false);
                            holder.follow_btn.setEnabled(true);
                            holder.follow_btn_progress.setVisibility(View.GONE);
                            notifyItemChanged(position1);
                        });
                    }
                }
            });

            holder.itemView.setOnClickListener(v -> {
                if (mFollowedLocations.size() == 0 || position >= mFollowedLocations.size()) {
                    return;
                }
//                    if (position < mFollowedLocations.size()) {
////                    holder.rlCheckContainer.setEnabled(false);
////                    Utils.followAnimation(holder.bookmark, 500);
//
//                        if (!mFollowedLocations.get(position).isFavorite()) {
//                            presenter.followLocation(mFollowedLocations.get(position).getId(), position, (position1, flag) -> {
//                                if (mFollowedLocations.size() == 0 || position1 >= mFollowedLocations.size()) {
//                                    return;
//                                }
//                                mFollowedLocations.get(position1).setFavorite(true);
////                            holder.rlCheckContainer.setEnabled(true);
//                                notifyItemChanged(position1);
//                            });
//                        } else {
//                            presenter.unFollowLocation(mFollowedLocations.get(position).getId(), position, (position1, flag) -> {
//                                if (mFollowedLocations.size() == 0 || position1 >= mFollowedLocations.size()) {
//                                    return;
//                                }
//                                mFollowedLocations.get(position1).setFavorite(false);
////                            holder.rlCheckContainer.setEnabled(true);
//                                notifyItemChanged(position1);
//                            });
//                        }
//                    }
//                    if (mFollowedLocations.size() == 0 || position >= mFollowedLocations.size()) {
//                        return;
//                    }
                Intent intent = new Intent(context, HashTagDetailsActivity.class);
                intent.putExtra("type", TYPE.LOCATION);
                intent.putExtra("id", mFollowedLocations.get(position).getId());
                intent.putExtra("mContext", mFollowedLocations.get(position).getContext());
                intent.putExtra("name", mFollowedLocations.get(position).getNameToShow());
                intent.putExtra("favorite", mFollowedLocations.get(position).isFavorite());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFollowedLocations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView text;
        private RoundedImageView locationImage;
        private RelativeLayout follow_btn;
        private ImageView follow_btn_icon;
        private ProgressBar follow_btn_progress;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.title);
            locationImage = itemView.findViewById(R.id.pin_img);
            follow_btn = itemView.findViewById(R.id.follow_btn);
            follow_btn_icon = itemView.findViewById(R.id.follow_btn_icon);
            follow_btn_progress = itemView.findViewById(R.id.follow_btn_progress);
        }
    }
}
