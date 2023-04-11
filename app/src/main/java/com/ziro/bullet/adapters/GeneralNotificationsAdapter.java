package com.ziro.bullet.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.model.notification.NotificationItem;
import com.ziro.bullet.presenter.NotificationPresenter;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GeneralNotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SECTION = 3423;
    private static final int TYPE_GENERAL = 3543;
    private static final int TYPE_ARTICLE = 3823;

    private static final String ARTICLE = "ARTICLE";
    private static final String FOLLOW = "FOLLOW";
    private static final String ARTICLE_LIKE = "ARTICLE_LIKE";
    private static final String ARTICLE_COMMENT = "ARTICLE_COMMENT";

    private static final String REEL = "REEL";
    private static final String REEL_LIKE = "REEL_LIKE";
    private static final String REEL_COMMENT = "REEL_COMMENT";

    private Context context;
    private ArrayList<NotificationItem> notificationList;
    private NotificationPresenter notificationPresenter;


    public GeneralNotificationsAdapter(Context context, ArrayList<NotificationItem> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        notificationPresenter = new NotificationPresenter((Activity) context, null);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifcation_section, parent, false);
            return new SectionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifcation, parent, false);
            return new NotificationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SectionViewHolder) {
            ((SectionViewHolder) holder).bind(notificationList.get(position));
        } else {
            ((NotificationViewHolder) holder).bind(notificationList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(notificationList.get(position).getHeader())) {
//            if (TextUtils.isEmpty(notificationList.get(position).getType()) ||
//                    !notificationList.get(position).getType().equals(ARTICLE))
            return TYPE_GENERAL;
//            else
//                return TYPE_ARTICLE;
        } else
            return TYPE_SECTION;
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView content;

        public SectionViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
        }

        void bind(NotificationItem notificationItem) {
            content.setText(String.format("%s", notificationItem.getHeader()));
        }
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView imageView, imageView1;
        private final ImageView icon;
        private final RoundedImageView image_article;
        private final TextView content, tvTime;

        public NotificationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            imageView1 = itemView.findViewById(R.id.image1);
            content = itemView.findViewById(R.id.content);
            icon = itemView.findViewById(R.id.icon);
            image_article = itemView.findViewById(R.id.image_article);
            tvTime = itemView.findViewById(R.id.time);
        }

        void bind(NotificationItem notificationItem) {
            try {
                Spanned spanned = HtmlCompat.fromHtml(notificationItem.getDetails(), HtmlCompat.FROM_HTML_MODE_COMPACT);
                SpannableStringBuilder sb = new SpannableStringBuilder();

                sb.append(spanned);

                content.setText(sb);
            } catch (Exception e) {
                content.setText("" + notificationItem.getDetails());
            }

            String time = Utils.getTimeAgo(Utils.getDate(notificationItem.getCreatedAt()), context);
            tvTime.setText(time);

            if (notificationItem.getImages() != null && notificationItem.getImages().size() > 0) {
                if (!TextUtils.isEmpty(notificationItem.getImages().get(0))) {
                    Glide.with(context)
                            .load(notificationItem.getImages().get(0))
                            .circleCrop()
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.ic_male_avatar);
                }

                if (notificationItem.getImages().size() == 2) {
                    imageView1.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(notificationItem.getImages().get(1))
                            .circleCrop()
                            .into(imageView1);
                } else {
                    imageView1.setVisibility(View.GONE);
                }
            } else {
                imageView.setImageResource(R.drawable.ic_male_avatar);
            }

            if (!TextUtils.isEmpty(notificationItem.getType())) {
                switch (notificationItem.getType()) {
                    case FOLLOW:
                        icon.setImageResource(R.drawable.ic_notification_follow);
                        break;
                    case ARTICLE_LIKE:
                    case REEL_LIKE:
                        icon.setImageResource(R.drawable.ic_notification_like);
                        break;
                    case ARTICLE_COMMENT:
                    case REEL_COMMENT:
                        icon.setImageResource(R.drawable.ic_notification_comment);
                        break;
                    default:
                        icon.setImageResource(R.drawable.ic_notification_other);
                }
            }

            if (!TextUtils.isEmpty(notificationItem.getDetailImage())) {
                image_article.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(notificationItem.getDetailImage())
                        .into(image_article);
            } else {
                image_article.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                notificationPresenter.readNotification(notificationItem.getId());
                if (!TextUtils.isEmpty(notificationItem.getType())) {
                    if ((notificationItem.getType().equals(ARTICLE)) ||
                            (notificationItem.getType().equals(ARTICLE_LIKE)) ||
                            (notificationItem.getType().equals(ARTICLE_COMMENT))) {
                        Intent intent = new Intent(context, BulletDetailActivity.class);
                        intent.putExtra("articleId", notificationItem.getDetailId());
                        context.startActivity(intent);
                    } else if ((notificationItem.getType().equals(REEL)) ||
                            (notificationItem.getType().equals(REEL_LIKE)) ||
                            (notificationItem.getType().equals(REEL_COMMENT))) {
                        Intent intent = new Intent(context, ReelInnerActivity.class);
                        intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, notificationItem.getDetailId());
                        intent.putExtra(ReelInnerActivity.REEL_F_TYPE,"notification");
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
