package com.ziro.bullet.adapters.CommunityFeed;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class AuthorItemsAdapter extends RecyclerView.Adapter<AuthorItemsAdapter.ViewHolder> {

    private final ArrayList<Author> data;
    private PrefConfig mPrefConfig;
    private Context context;
    private boolean white;
    private FollowUnfollowPresenter presenter;

    public AuthorItemsAdapter(Context context, ArrayList<Author> data) {
        this.data = data;
        this.context = context;
        mPrefConfig = new PrefConfig(context);
        presenter = new FollowUnfollowPresenter((Activity) context);
    }

    public AuthorItemsAdapter(Context context, ArrayList<Author> data, boolean white) {
        this.white = white;
        this.data = data;
        this.context = context;
        mPrefConfig = new PrefConfig(context);
        presenter = new FollowUnfollowPresenter((Activity) context);
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
            if (white) {
                holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.card.setCardBackgroundColor(context.getResources().getColor(R.color.bottom_sheet_bg));
            }
            Author item = data.get(position);
            if (!TextUtils.isEmpty(item.getProfile_image())) {
                Glide.with(context)
                        .load(item.getProfile_image())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(holder.image);
            } else {
                holder.image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            if (!TextUtils.isEmpty(item.getFirst_name()) && !TextUtils.isEmpty(item.getLast_name())) {
                holder.title.setText(item.getFirst_name() + " " + item.getLast_name());
            } else if (!TextUtils.isEmpty(item.getFirst_name())) {
                holder.title.setText(item.getFirst_name());
            }

            holder.card.setOnClickListener(v -> {
                Utils.openAuthor(context, item);
            });

            if (item.isFollow()) {
                holder.item_color.setBackgroundColor(context.getResources().getColor(R.color.disable_btn));
                holder.name.setText(context.getString(R.string.unfollow));
            } else {
                holder.item_color.setBackgroundColor(context.getResources().getColor(R.color.theme_color_1));
                holder.name.setText(context.getString(R.string.follow));
            }

            holder.bookmark.setOnClickListener(v -> {
                holder.bookmark.setEnabled(false);
                if (!item.isFollow()) {
                    presenter.followAuthor(item.getId(), position, (position1, flag) -> {
                        item.setFollow(true);
                        holder.bookmark.setEnabled(true);
                        notifyItemChanged(position);
                    });
                } else {
                    presenter.unFollowAuthor(item.getId(), position, (position1, flag) -> {
                        item.setFollow(false);
                        holder.bookmark.setEnabled(true);
                        notifyItemChanged(position);
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView card;
        private final RoundedImageView image;
        private final CardView bookmark;
        private final TextView title;
        private final TextView name;
        private final LinearLayout item_color;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_color = itemView.findViewById(R.id.item_color);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            bookmark = itemView.findViewById(R.id.bookmark);
        }
    }
}
