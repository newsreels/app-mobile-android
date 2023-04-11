package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.activities.CustomDiscoverListActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.model.TCP.TCP;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Utils;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ImageTCPCardViewHolder extends RecyclerView.ViewHolder {

    private final TextView text1;
    private final TextView text2;
    private final ImageView image;
    private final ImageView imageBack;
    private final CardView card;
    private final LinearLayout unfollow;
    private final LinearLayout follow;
    private Activity mContext;
    private FollowUnfollowPresenter presenter;
    private PrefConfig mPrefConfig;

    public ImageTCPCardViewHolder(@NonNull View itemView, Activity context) {
        super(itemView);

        this.mContext = context;
        mPrefConfig = new PrefConfig(context);
        unfollow = itemView.findViewById(R.id.unfollow);
        follow = itemView.findViewById(R.id.follow);
        text1 = itemView.findViewById(R.id.text1);
        card = itemView.findViewById(R.id.card);
        text2 = itemView.findViewById(R.id.text2);
        image = itemView.findViewById(R.id.image);
        imageBack = itemView.findViewById(R.id.imageBack);
    }

    public void bind(String type, TCP item, FollowUnfollowPresenter presenter) {
        this.presenter = presenter;
        if (item != null) {
            if (!TextUtils.isEmpty(item.getFollower())) {
                text2.setVisibility(View.VISIBLE);
                text2.setText(item.getFollower());
            } else {
                text2.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(item.getName())) {
                text1.setVisibility(View.VISIBLE);
                text1.setText(item.getName());
            } else {
                text1.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.get()
                        .load(item.getImage())
                        .transform(new BlurTransformation(mContext,25,3))
                        .into(imageBack);

                Picasso.get()
                        .load(item.getImage())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(image);
            }

            if (item.isFavorite()) {
                unfollow.setVisibility(View.GONE);
                follow.setVisibility(View.VISIBLE);
            } else {
                unfollow.setVisibility(View.VISIBLE);
                follow.setVisibility(View.GONE);
            }

            card.setOnClickListener(v -> {
//                Intent intent = new Intent(mContext, CustomDiscoverListActivity.class);
//                intent.putExtra("type", "IMAGE_ARTICLES");
//                intent.putExtra("context", item.getContext());
//                intent.putExtra("data", new Gson().toJson(mCoverData));
//                mContext.startActivity(intent);
//                mContext.finish();

                Intent intent = null;
                if (type.equalsIgnoreCase("IMAGE_TOPICS")) {
                    intent = new Intent(mContext, ChannelPostActivity.class);
                    intent.putExtra("type", TYPE.TOPIC);
                } else if (type.equalsIgnoreCase("IMAGE_CHANNELS")) {
                    intent = new Intent(mContext, ChannelDetailsActivity.class);
                    intent.putExtra("type", TYPE.SOURCE);
                } else if (type.equalsIgnoreCase("IMAGE_PLACES")) {
                    intent = new Intent(mContext, ChannelPostActivity.class);
                    intent.putExtra("type", TYPE.LOCATION);
                }
                intent.putExtra("id", item.getId());
                intent.putExtra("context", item.getContext());
                intent.putExtra("name", item.getName());
                intent.putExtra("favorite", item.isFavorite());
                mContext.startActivity(intent);
            });

            follow.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(type)) {
                    if (type.equalsIgnoreCase("IMAGE_TOPICS")) {
                        presenter.unFollowTopic(item.getId(), 0, (position, flag) -> {
                            if(flag) {
                                item.setFavorite(false);
                                unfollow.setVisibility(View.VISIBLE);
                                follow.setVisibility(View.GONE);
                            }
                        });
                    } else if (type.equalsIgnoreCase("IMAGE_CHANNELS")) {
                        presenter.unFollowSource(item.getId(), 0, (position, flag) -> {
                            if(flag) {
                                item.setFavorite(false);
                                unfollow.setVisibility(View.VISIBLE);
                                follow.setVisibility(View.GONE);
                            }
                        });
                    } else if (type.equalsIgnoreCase("IMAGE_PLACES")) {
                        presenter.unFollowLocation(item.getId(), 0, (position, flag) -> {
                            if(flag) {
                                item.setFavorite(false);
                                unfollow.setVisibility(View.VISIBLE);
                                follow.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });

            unfollow.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(type)) {
                    if (type.equalsIgnoreCase("IMAGE_TOPICS")) {
                        presenter.followTopic(item.getId(), 0, (position, flag) -> {
                            if(flag) {
                                item.setFavorite(true);
                                unfollow.setVisibility(View.GONE);
                                follow.setVisibility(View.VISIBLE);
                            }
                        });
                    } else if (type.equalsIgnoreCase("IMAGE_CHANNELS")) {
                        presenter.followSource(item.getId(), 0, (position, flag) -> {
                            if(flag) {
                                item.setFavorite(true);
                                unfollow.setVisibility(View.GONE);
                                follow.setVisibility(View.VISIBLE);
                            }
                        });
                    } else if (type.equalsIgnoreCase("IMAGE_PLACES")) {
                        presenter.followLocation(item.getId(), 0, (position, flag) -> {
                            if(flag) {
                                item.setFavorite(true);
                                unfollow.setVisibility(View.GONE);
                                follow.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });

        }
    }
}
