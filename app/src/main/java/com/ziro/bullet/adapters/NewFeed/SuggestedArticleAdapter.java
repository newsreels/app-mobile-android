package com.ziro.bullet.adapters.NewFeed;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.utills.Utils;

import java.util.List;

public class SuggestedArticleAdapter extends RecyclerView.Adapter<SuggestedArticleAdapter.ViewHolder> {

    private final List<Article> data;
    private PrefConfig mPrefConfig;
    private Activity context;
    private boolean isCounter;
    private boolean isHome;
    private OnGotoChannelListener gotoChannelListener;
    private TempCategorySwipeListener swipeListener;

    public SuggestedArticleAdapter(Activity context, List<Article> data, boolean isCounter, boolean isHome, OnGotoChannelListener gotoChannelListener, TempCategorySwipeListener swipeListener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_article_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data != null && data.size() > 0) {
            Article item = data.get(position);

            if (mPrefConfig.isReaderMode()) {
                holder.imageMain.setVisibility(View.GONE);
                holder.card.setBackgroundColor(context.getResources().getColor(R.color.article_bg));

//                if (isCounter) {
//                    if (!isHome) {
//                        //Discover
//                        if (!TextUtils.isEmpty(mPrefConfig.getAppTheme())) {
////                            if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
////                                holder.card.setBackgroundColor(context.getResources().getColor(R.color.article_blk));
////                            }
//                        }
//                        holder.card.setBackgroundColor(context.getResources().getColor(R.color.article_bg));
//
//                    } else {
//                        //Home
//                        holder.card.setBackgroundColor(context.getResources().getColor(R.color.article_bg));
//                    }
//                    int count = position;
//                    count++;
//                    holder.counter2.setText(String.valueOf(count));
//                    holder.counter2.setVisibility(View.VISIBLE);
//                } else {
//                    holder.counter2.setVisibility(View.GONE);
//                    if (!isHome) {
//                        //Discover
//                        if (!TextUtils.isEmpty(mPrefConfig.getAppTheme())) {
//                            if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.LIGHT)) {
//                                holder.card.setBackgroundColor(context.getResources().getColor(R.color.article_blk));
//                            }
//                        }
//                    } else {
//                        //Home
//                        holder.card.setBackgroundColor(context.getResources().getColor(R.color.article_bg));
//                    }
//                }

                holder.title.setMaxLines(8);
                holder.title.setMinHeight(context.getResources().getDimensionPixelSize(R.dimen._130sdp));
                holder.margin1.setPadding(context.getResources().getDimensionPixelSize(R.dimen._5sdp), context.getResources().getDimensionPixelSize(R.dimen._5sdp), context.getResources().getDimensionPixelSize(R.dimen._5sdp), 0);
                holder.margin2.setPadding(context.getResources().getDimensionPixelSize(R.dimen._5sdp), 0, context.getResources().getDimensionPixelSize(R.dimen._5sdp), 0);
            } else {
                holder.margin1.setPadding(0, 0, 0, 0);
                holder.margin2.setPadding(0, 0, 0, 0);
                holder.imageMain.setVisibility(View.VISIBLE);
                holder.card.setBackground(null);

                if (!TextUtils.isEmpty(item.getImage())) {
                    Glide.with(context)
                            .load(item.getImage())
                            .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .into(holder.image);
                    Glide.with(context)
                            .load(item.getImage())
                            .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .into(holder.imageLarge);
                } else {
                    holder.image.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                    holder.imageLarge.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }

                if (isCounter) {
                    int count = position;
                    count++;
                    holder.counter.setText(String.valueOf(count));
                    holder.counter.setVisibility(View.VISIBLE);
                    holder.image.setVisibility(View.VISIBLE);
                    holder.imageLarge.setVisibility(View.GONE);
                } else {
                    holder.counter.setVisibility(View.INVISIBLE);
                    holder.image.setVisibility(View.GONE);
                    holder.imageLarge.setVisibility(View.VISIBLE);
                }
                holder.title.setMaxLines(4);
                holder.title.setMinHeight(context.getResources().getDimensionPixelSize(R.dimen._25sdp));
            }

            if (!TextUtils.isEmpty(item.getType())) {
                if (item.getType().equalsIgnoreCase("video") || item.getType().equalsIgnoreCase("youtube")) {
                    holder.play_image.setVisibility(View.VISIBLE);
                } else {
                    holder.play_image.setVisibility(View.GONE);
                }
            }

//            if (!isHome) {
//                holder.title.setTextColor(context.getResources().getColor(R.color.white));
//                holder.source_name.setTextColor(context.getResources().getColor(R.color.white));
//            }
            if (!TextUtils.isEmpty(item.getTitle())) {
                holder.title.setText(item.getTitle());
            }
            if (!TextUtils.isEmpty(item.getSourceNameToDisplay())) {
                holder.source_name.setText(item.getSourceNameToDisplay());
            }

            String timee = Utils.getTimeAgo(Utils.getDate(item.getPublishTime()), context);
            if (!TextUtils.isEmpty(timee)) {
                holder.timeTv.setText(timee);
            }

            if (!TextUtils.isEmpty(item.getSourceImageToDisplay())) {
                holder.source_icon.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(item.getSourceImageToDisplay())
                        .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                        .into(holder.source_icon);
            } else {
                holder.source_icon.setVisibility(View.GONE);
            }

            holder.margin2.setOnClickListener(v -> {
                //Channel Details
                detailsPage(item);
            });

            if ((position == data.size() - 1) && item.isTopicAdded()) {

                holder.article.setVisibility(View.GONE);
                holder.lastItem.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(item.getImage())) {
                    Glide.with(context)
                            .load(item.getImage())
                            .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .into(holder.image2);
                } else {
                    holder.image2.setImageResource(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()));
                }

                if (item.isFollowed()) {
                    holder.btnImg.setImageResource(R.drawable.ic_tick);
                    holder.title2.setText(item.getFollowed_text());
                } else {
                    holder.btnImg.setImageResource(R.drawable.ic_plus);
                    holder.title2.setText(item.getUnfollowed_text());
                }

                holder.btn.setOnClickListener(v -> {
                    if (item.isFollowed()) {
                        holder.btnImg.setImageResource(R.drawable.ic_plus);
                        holder.title2.setText(item.getUnfollowed_text());
                        item.setFollowed(false);
                    } else {
                        holder.btnImg.setImageResource(R.drawable.ic_tick);
                        holder.title2.setText(item.getFollowed_text());
                        item.setFollowed(true);
                    }
                });

                holder.card.setOnClickListener(v -> {
                    if (swipeListener != null && !TextUtils.isEmpty(item.getId()) && !TextUtils.isEmpty(item.getTitle()))
                        swipeListener.selectTabOrDetailsPage(item.getId(), item.getTitle(), TYPE.TOPIC, item.getFooterType());
                });
            } else {
                holder.article.setVisibility(View.VISIBLE);
                holder.lastItem.setVisibility(View.GONE);

                holder.card.setOnClickListener(v -> {
                    Intent intent = new Intent(context, BulletDetailActivity.class);
                    intent.putExtra("article", new Gson().toJson(item));
                    intent.putExtra("type", item.getType());
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                });
            }
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
        private final ImageView image;
        private final ImageView image2;
        private final ImageView imageLarge;
        private final ImageView btnImg;
        private final TextView title2;
        private final TextView counter;
        private final TextView counter2;
        private final RelativeLayout btn;
        private final ImageView source_Image;
        private final ImageView play_image;
        private final TextView title;
        private final TextView timeTv;
        private final TextView source_name;
        private final RoundedImageView source_icon;
        private final RelativeLayout card;
        private final RelativeLayout margin1, margin2;
        private final RelativeLayout imageMain;
        private final LinearLayout article;
        private final RelativeLayout lastItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            margin1 = itemView.findViewById(R.id.margin1);
            margin2 = itemView.findViewById(R.id.margin2);
            counter2 = itemView.findViewById(R.id.counter2);
            play_image = itemView.findViewById(R.id.play_image);
            imageLarge = itemView.findViewById(R.id.imageLarge);
            imageMain = itemView.findViewById(R.id.imageMain);
            counter = itemView.findViewById(R.id.counter);
            title2 = itemView.findViewById(R.id.title2);
            btn = itemView.findViewById(R.id.btn);
            image2 = itemView.findViewById(R.id.image2);
            btnImg = itemView.findViewById(R.id.btnImg);
            article = itemView.findViewById(R.id.article);
            lastItem = itemView.findViewById(R.id.lastItem);
            card = itemView.findViewById(R.id.card);
            source_name = itemView.findViewById(R.id.source_name);
            title = itemView.findViewById(R.id.title);
            timeTv = itemView.findViewById(R.id.time);
            image = itemView.findViewById(R.id.image);
            source_icon = itemView.findViewById(R.id.source_icon);
            source_Image = itemView.findViewById(R.id.source_Image);
        }
    }
}
