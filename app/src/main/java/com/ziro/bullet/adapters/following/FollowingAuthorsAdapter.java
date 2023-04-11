package com.ziro.bullet.adapters.following;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingAuthorsAdapter extends RecyclerView.Adapter<FollowingAuthorsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Author> mFollowedAuthors;
    private FollowUnfollowPresenter presenter;
    private boolean forceDark;
    private PrefConfig prefConfig;

    public FollowingAuthorsAdapter(
            Context context, ArrayList<Author> mFollowedAuthors, boolean forceDark
    ) {
        this.context = context;
        this.mFollowedAuthors = mFollowedAuthors;
        presenter = new FollowUnfollowPresenter((Activity) context);
        this.forceDark = forceDark;
        prefConfig = new PrefConfig(context);
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_following_authors_v2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        if (position < mFollowedAuthors.size()) {
            holder.text.setText(String.format("%s %s", mFollowedAuthors.get(position).getFirst_name()
                    , mFollowedAuthors.get(position).getLast_name()));
            Picasso.get()
                    .load(mFollowedAuthors.get(position).getProfile_image())
                    .resize(Constants.targetWidth, Constants.targetHeight)
                    .onlyScaleDown()
                    .error(R.drawable.img_place_holder)
                    .into(holder.image);

            if (mFollowedAuthors.get(position).isFollow())
                holder.bookmark.setProgress(1f);
            else
                holder.bookmark.setProgress(0f);

            if (forceDark) {
                holder.text.setTextColor(ContextCompat.getColor(context, R.color.discover_title_night));
//                ImageViewCompat.setImageTintList(
//                        holder.bookmark,
//                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.discover_title_night))
//                );
                holder.bookmark.setAnimation(R.raw.tick_plus);
            } else {
                holder.text.setTextColor(ContextCompat.getColor(context, R.color.whiteblack));
//                ImageViewCompat.setImageTintList(
//                        holder.bookmark,
//                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.whiteblack))
//                );
                if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
                    holder.bookmark.setAnimation(R.raw.tick_plus_black);
                } else {
                    holder.bookmark.setAnimation(R.raw.tick_plus);
                }
            }

            holder.card.setOnClickListener(v -> {
                if (mFollowedAuthors.size() == 0 || position >= mFollowedAuthors.size()) {
                    return;
                }
                holder.card.setEnabled(false);
                Utils.followAnimation(holder.bookmark, 500);

                if (!mFollowedAuthors.get(position).isFollow()) {
                    presenter.followAuthor(mFollowedAuthors.get(position).getId(), position, new FollowUnfollowPresenter.FollowUnFollowApiCallback() {
                        @Override
                        public void onResponse(int position1, boolean flag) {
                            if (mFollowedAuthors.size() == 0 || position1 >= mFollowedAuthors.size()) {
                                return;
                            }
                            mFollowedAuthors.get(position1).setFollow(true);
                            holder.card.setEnabled(true);
//                        notifyItemChanged(position1);
                        }
                    });
                } else {
                    presenter.unFollowAuthor(mFollowedAuthors.get(position).getId(), position, new FollowUnfollowPresenter.FollowUnFollowApiCallback() {
                        @Override
                        public void onResponse(int position1, boolean flag) {
                            if (mFollowedAuthors.size() == 0 || position1 >= mFollowedAuthors.size()) {
                                return;
                            }
                            mFollowedAuthors.get(position1).setFollow(false);
                            holder.card.setEnabled(true);
//                        notifyItemChanged(position1);
                        }
                    });
                }
            });

            holder.image.setOnClickListener(v1 -> {
                if (mFollowedAuthors.size() == 0 || position >= mFollowedAuthors.size()) {
                    return;
                }
                Utils.openAuthor(context, mFollowedAuthors.get(position));
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFollowedAuthors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private LottieAnimationView bookmark;
        private TextView text;
        private ConstraintLayout card;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.title);
            bookmark = itemView.findViewById(R.id.bookmark);
            card = itemView.findViewById(R.id.card);
        }
    }
}