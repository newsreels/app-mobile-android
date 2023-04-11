package com.ziro.bullet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.model.notification.NewsNotificationItem;
import com.ziro.bullet.presenter.NotificationPresenter;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NewsNotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SECTION = 3423;
    private static final int TYPE_GENERAL = 3543;
    private static final int TYPE_ARTICLE = 3823;

    private static final String ARTICLE = "ARTICLE";
    private static final String REEL = "REEL";

    private Context context;
    private ArrayList<NewsNotificationItem> notificationList;
    private NotificationPresenter notificationPresenter;

    public NewsNotificationsAdapter(Context context, ArrayList<NewsNotificationItem> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        notificationPresenter = new NotificationPresenter((Activity) context,null);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifcation_news, parent, false);
        return new NewsNotificationsAdapter.ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsNotificationsAdapter.ArticleViewHolder) {
            ((NewsNotificationsAdapter.ArticleViewHolder) holder).bind(notificationList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        return TYPE_ARTICLE;
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView content, source, tvTime;
        private RoundedImageView imageView;

        public ArticleViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            tvTime = itemView.findViewById(R.id.time);
            source = itemView.findViewById(R.id.author_name);
            imageView = itemView.findViewById(R.id.image);
        }

        void bind(NewsNotificationItem notificationItem) {
            content.setText("" + notificationItem.getHeadline());

            source.setText(notificationItem.getSource());
            String time = Utils.getTimeAgo(Utils.getDate(notificationItem.getCreatedAt()), context);
            if (!TextUtils.isEmpty(time)) {
                tvTime.setText(time);
            }

            Glide.with(context)
                    .load(notificationItem.getImage())
                    .into(imageView);

            itemView.setOnClickListener(v -> {
                notificationPresenter.readNotification(notificationItem.getId());
                if (notificationItem.getType().equals(REEL)) {
                    Intent intent = new Intent(context, ReelInnerActivity.class);
                    intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, notificationItem.getDetailId());
                    intent.putExtra(ReelInnerActivity.REEL_F_TYPE,"notification");
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, BulletDetailActivity.class);
                    intent.putExtra("articleId", notificationItem.getDetailId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
