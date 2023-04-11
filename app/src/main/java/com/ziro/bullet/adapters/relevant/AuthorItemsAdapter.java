package com.ziro.bullet.adapters.relevant;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;

import com.ziro.bullet.interfaces.ViewItemClickListener;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class AuthorItemsAdapter extends RecyclerView.Adapter<AuthorItemsAdapter.ViewHolder> {

    private final ArrayList<Author> authorSearchArrayList;
    private Context mContext;
    private final ViewItemClickListener viewItemClickListener;
    private PrefConfig mPrefConfig;
    private FollowUnfollowPresenter presenter;

    public AuthorItemsAdapter(ArrayList<Author> authorSearchArrayList, Context mContext, ViewItemClickListener viewItemClickListener) {
        this.authorSearchArrayList = authorSearchArrayList;
        this.mContext = mContext;
        this.viewItemClickListener = viewItemClickListener;
        mPrefConfig = new PrefConfig(mContext);
        presenter = new FollowUnfollowPresenter((Activity) mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.relevant_subitem_authors, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (authorSearchArrayList != null && authorSearchArrayList.size() > 0) {
            Author item = authorSearchArrayList.get(position);
            if (!TextUtils.isEmpty(item.getProfile_image())) {
                Glide.with(mContext)
                        .load(item.getProfile_image())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(holder.image);
            } else {
                holder.image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            holder.title.setText(item.getName());
            if (!TextUtils.isEmpty(item.getUsername())) {
                holder.username.setText("@" + item.getUsername());
                holder.username.setVisibility(View.VISIBLE);
            } else {
                holder.username.setVisibility(View.INVISIBLE);
            }
            if (item.isFollow()) {
                holder.channel_btn_color.setBackgroundColor(mContext.getResources().getColor(R.color.edittextHint));
                holder.channel_btn_txt.setText(mContext.getString(R.string.following));

            } else {
                holder.channel_btn_color.setBackgroundColor(mContext.getResources().getColor(R.color.theme_color_1));
                holder.channel_btn_txt.setText(mContext.getString(R.string.follow));
            }

            holder.channel_btn.setOnClickListener(v -> {
                holder.channel_btn.setEnabled(false);
                holder.channel_btn_txt.setVisibility(View.INVISIBLE);
                holder.channel_btn_progress.setVisibility(View.VISIBLE);

                if (!item.isFollow()) {
                    presenter.followSource(item.getId(), position, (position1, flag) -> {
                        item.setFollow(true);
                        holder.channel_btn.setEnabled(true);
                        holder.channel_btn_txt.setText(mContext.getString(R.string.following));
                        holder.channel_btn_progress.setVisibility(View.GONE);
                        holder.channel_btn_txt.setVisibility(View.VISIBLE);
                        notifyItemChanged(position);
                    });
                } else {
                    presenter.unFollowSource(item.getId(), position, (position1, flag) -> {
                        item.setFollow(false);
                        holder.channel_btn.setEnabled(true);
                        holder.channel_btn_txt.setText(mContext.getString(R.string.follow));
                        holder.channel_btn_progress.setVisibility(View.GONE);
                        holder.channel_btn_txt.setVisibility(View.VISIBLE);

                        notifyItemChanged(position);
                    });
                }
            });

            holder.card.setOnClickListener(v -> {
                if (viewItemClickListener != null) {
                    viewItemClickListener.itemClickedData(v, ViewItemClickListener.TYPE_RELEVANT_AUTHOR_ITEM, item);
                }
            });
        }
    }

    private void showVerifiedIcon(ViewHolder holder, Author sourceObj) {
        if (sourceObj.isVerified()) {
            if (Utils.isRTL()) {
//                holder.name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0);
            } else {
//                holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return authorSearchArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView channel_btn;
        private final LinearLayout channel_btn_color;
        private final RoundedImageView image;
        private final CardView card;
        private final TextView title;
        private final TextView username;
        private final TextView channel_btn_txt;
        private final ProgressBar channel_btn_progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            channel_btn_txt = itemView.findViewById(R.id.name);
            channel_btn_color = itemView.findViewById(R.id.item_color);
            channel_btn = itemView.findViewById(R.id.bookmark);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            channel_btn_progress = itemView.findViewById(R.id.progress);
            card = itemView.findViewById(R.id.card);
        }
    }
}