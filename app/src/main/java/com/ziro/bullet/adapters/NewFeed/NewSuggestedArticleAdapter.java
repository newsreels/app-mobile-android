package com.ziro.bullet.adapters.NewFeed;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.List;

public class NewSuggestedArticleAdapter extends RecyclerView.Adapter<NewSuggestedArticleAdapter.ViewHolder> {

    private final List<Article> data;
    private PrefConfig mPrefConfig;
    private Activity context;
    private boolean isCounter;
    private boolean isHome;
    private OnGotoChannelListener gotoChannelListener;
    private TempCategorySwipeListener swipeListener;

    public NewSuggestedArticleAdapter(Activity context, List<Article> data, boolean isCounter, boolean isHome, OnGotoChannelListener gotoChannelListener, TempCategorySwipeListener swipeListener) {
        this.isCounter = isCounter;
        this.data = data;
        this.swipeListener = swipeListener;
        this.isHome = isHome;
        this.context = context;
        this.gotoChannelListener = gotoChannelListener;
        mPrefConfig = new PrefConfig(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_article_small, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data != null && data.size() > 0) {
            Article item = data.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Glide.with(context)
                        .load(item.getImage())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(holder.bullet_image);
            } else {
                holder.bullet_image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
            }

            if (!TextUtils.isEmpty(item.getType())) {
                if (item.getType().equalsIgnoreCase("video") || item.getType().equalsIgnoreCase("youtube")) {
                    holder.play_image.setVisibility(View.VISIBLE);
                } else {
                    holder.play_image.setVisibility(View.GONE);
                }
            }

            if (item.getBullets() != null && item.getBullets().get(0) != null) {
                if (!TextUtils.isEmpty(item.getBullets().get(0).getData()))
                    holder.bullet.setText(item.getBullets().get(0).getData());
            }
            if (!TextUtils.isEmpty(item.getSourceNameToDisplay())) {
                holder.source_name.setText(item.getSourceNameToDisplay());
            }

            String timee = Utils.getTimeAgo(Utils.getDate(item.getPublishTime()), context);
            if (!TextUtils.isEmpty(timee)) {
                holder.timeTv.setText(timee);
            }

//            if (!TextUtils.isEmpty(item.getSourceImageToDisplay())) {
//                holder.source_icon.setVisibility(View.VISIBLE);
//                Glide.with(context)
//                        .load(item.getSourceImageToDisplay())
//                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
//                        .into(holder.source_icon);
//            } else {
//                holder.source_icon.setVisibility(View.GONE);
//            }

            holder.source_name.setOnClickListener(v -> {
                //Channel Details
                detailsPage(item);
            });

            holder.cardData.setOnClickListener(v -> {
                //Article Details
                if(Constants.reelfragment){
                    Constants.rvmdailogopen=true;
                }
                Intent intent = new Intent(context, BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(item));
                intent.putExtra("type", item.getType());
                intent.putExtra("position", position);
                context.startActivity(intent);
            });

        }
    }

    private void detailsPage(Article content) {
        if (context instanceof ChannelDetailsActivity) {
            return;
        }
        if (content != null) {
//            if (isGotoFollowShow && !type.equalsIgnoreCase("ARCHIVE")) {
            if (gotoChannelListener != null && content.getSource() != null) {
                if (mPrefConfig != null) {
                    mPrefConfig.setSrcLang(content.getSource().getLanguage());
                    mPrefConfig.setSrcLoc(content.getSource().getCategory());
                }
                gotoChannelListener.onItemClicked(TYPE.SOURCE, content.getSource().getId(), content.getSource().getName(), content.getSource().isFavorite());
//                }
            } else if (content.getAuthor() != null && content.getAuthor().size() > 0) {
                if (content.getAuthor().get(0) != null) {
                    Utils.openAuthor(context, content.getAuthor().get(0));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView source_name;
        private final TextView bullet;
        private final ImageView bullet_image;
        private final ImageView play_image;
        private final TextView timeTv;
        private final TextView author_name;
        private final ImageView separator_dot;
        private final ConstraintLayout cardData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            source_name = itemView.findViewById(R.id.source_name);
            bullet = itemView.findViewById(R.id.bullet);
            bullet_image = itemView.findViewById(R.id.bullet_image);
            play_image = itemView.findViewById(R.id.play_image);
            timeTv = itemView.findViewById(R.id.time);
            separator_dot = itemView.findViewById(R.id.separator_dot);
            author_name = itemView.findViewById(R.id.author_name);
            cardData = itemView.findViewById(R.id.cardData);
        }
    }
}
