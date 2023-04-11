package com.ziro.bullet.adapters.relevant;

import android.annotation.SuppressLint;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.activities.HashTagDetailsActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlaceItemsAdapter extends RecyclerView.Adapter<PlaceItemsAdapter.ViewHolder> {

    private final PrefConfig mPrefConfig;
    private ArrayList<Location> mFollowedLocations;
    private final SearchRelevantMainAdapter.FollowingCallback followingCallback1;
    private FollowUnfollowPresenter presenter;
    private Context context;

    public PlaceItemsAdapter(Context context, ArrayList<Location> locationArrayList, SearchRelevantMainAdapter.FollowingCallback followingCallback1, boolean isDarkOnly) {
        this.mFollowedLocations = locationArrayList;
        this.context = context;
        this.followingCallback1 = followingCallback1;
        mPrefConfig = new PrefConfig(context);
        presenter = new FollowUnfollowPresenter((Activity) context);
    }

    @NonNull
    @Override
    public PlaceItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_item_discover, parent, false);
        return new PlaceItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceItemsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (mFollowedLocations != null && mFollowedLocations.size() > 0) {
            Location location = mFollowedLocations.get(position);
            if (location != null) {

                if (!TextUtils.isEmpty(location.getImage())) {
                    Glide.with(context)
                            .load(location.getImage())
                            .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .into(holder.image);
                } else {
                    holder.image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }

                holder.name.setText(location.getName());
                holder.username.setVisibility(View.GONE);

                if (location.isFavorite()) {
                    holder.place_btn.setCardBackgroundColor(context.getResources().getColor(R.color.edittextHint));
                    holder.place_btn_txt.setText(context.getString(R.string.following));
                } else {
                    holder.place_btn.setCardBackgroundColor(context.getResources().getColor(R.color.theme_color_1));
                    holder.place_btn_txt.setText(context.getString(R.string.follow));
                }

                holder.place_btn.setOnClickListener(v -> {
                    if (mFollowedLocations.size() == 0 || position >= mFollowedLocations.size()) {
                        return;
                    }
                    holder.place_btn.setEnabled(false);
                    holder.place_btn_txt.setVisibility(View.GONE);
                    holder.place_btn_progress.setVisibility(View.VISIBLE);
                    if (position < mFollowedLocations.size()) {

                        if (!location.isFavorite()) {
                            presenter.followLocation(location.getId(), position, (position1, flag) -> {
                                location.setFavorite(true);
                                holder.place_btn.setEnabled(true);
                                holder.place_btn_txt.setText(context.getString(R.string.following));
                                holder.place_btn_progress.setVisibility(View.GONE);
                                holder.place_btn_txt.setVisibility(View.VISIBLE);
                                notifyItemChanged(position1);
                            });
                        } else {
                            presenter.unFollowLocation(location.getId(), position, (position1, flag) -> {
                                location.setFavorite(false);
                                holder.place_btn.setEnabled(true);
                                holder.place_btn_txt.setText(context.getString(R.string.follow));
                                holder.place_btn_progress.setVisibility(View.GONE);
                                holder.place_btn_txt.setVisibility(View.VISIBLE);
                                notifyItemChanged(position1);
                            });
                        }
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mFollowedLocations.size() == 0 || position >= mFollowedLocations.size()) {
                            return;
                        }
                        Intent intent = new Intent(context, HashTagDetailsActivity.class);
                        intent.putExtra("type", TYPE.LOCATION);
                        intent.putExtra("id", location.getId());
                        intent.putExtra("mContext", location.getContext());
                        intent.putExtra("name", location.getName());
                        intent.putExtra("favorite", location.isFavorite());
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFollowedLocations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView place_btn;
        private final ImageView image;
        private final RelativeLayout main;
        private final TextView name;
        private final TextView username;
        private final TextView place_btn_txt;
        private final ProgressBar place_btn_progress;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            place_btn = itemView.findViewById(R.id.place_btn);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            place_btn_txt = itemView.findViewById(R.id.place_btn_txt);
            place_btn_progress = itemView.findViewById(R.id.place_btn_progress);
            name = itemView.findViewById(R.id.name);
            main = itemView.findViewById(R.id.main);
        }
    }
}
