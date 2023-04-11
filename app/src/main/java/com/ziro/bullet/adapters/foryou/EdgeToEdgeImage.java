package com.ziro.bullet.adapters.foryou;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.utills.Utils;

import java.util.HashMap;
import java.util.Map;

public class EdgeToEdgeImage extends RecyclerView.ViewHolder {

    private RelativeLayout card;
    private ImageView postImage;
    private TextView articleTitle;
    private LinearLayout source;
    private RoundedImageView source_image;
    private TextView source_name;
    private TextView time;
    private RelativeLayout post_display_container;
    private TextView postTime;
    private RelativeLayout banner;
    private TextView banner_text;
    private ImageView banner_image;
    private PrefConfig prefConfig;
    private Article article;
    private Activity context;
    private String articleId;
    private NewsCallback categoryCallback;
    private AdapterCallback adapterCallback;
    private OnGotoChannelListener gotoChannelListener;
    private GoHome goHomeMainActivity;
    private CommentClick mCommentClick;
    private String type;

    public EdgeToEdgeImage(CommentClick mCommentClick, View view, Activity context, NewsCallback categoryCallback, AdapterCallback adapterCallback, OnGotoChannelListener gotoChannelListener,
                           GoHome goHomeMainActivity, Lifecycle lifecycle, String type) {
        super(view);
        this.mCommentClick = mCommentClick;
        this.goHomeMainActivity = goHomeMainActivity;
        this.gotoChannelListener = gotoChannelListener;
        this.context = context;
        this.categoryCallback = categoryCallback;
        this.adapterCallback = adapterCallback;
        this.type  = type;
        this.prefConfig = new PrefConfig(context);

        post_display_container = view.findViewById(R.id.post_display_container);
        source = view.findViewById(R.id.source);
        source_image = view.findViewById(R.id.source_image);
        source_name = view.findViewById(R.id.source_name);
        time = view.findViewById(R.id.time);
        card = view.findViewById(R.id.card);
        banner = view.findViewById(R.id.banner);
        banner_text = view.findViewById(R.id.banner_text);
        banner_image = view.findViewById(R.id.banner_image);
        postImage = view.findViewById(R.id.post_image);
        articleTitle = view.findViewById(R.id.article_title);
        postTime = view.findViewById(R.id.post_time);
    }

    public void bind(Article articleLoaded) {
        article = articleLoaded;
        loadData();
    }

    private void loadData() {
        if (article != null) {
            articleId = article.getId();
//            card.setBackgroundColor(context.getResources().getColor(R.color.card_bg));
            articleTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
            if (Utils.getLanguageDirectionForView(article.getLanguageCode()) == View.TEXT_DIRECTION_LTR) {
                articleTitle.setPadding(
                        0,
                        context.getResources().getDimensionPixelOffset(R.dimen._5sdp),
                        context.getResources().getDimensionPixelOffset(R.dimen._35sdp),
                        0
                );
            } else {
                articleTitle.setPadding(
                        context.getResources().getDimensionPixelOffset(R.dimen._35sdp),
                        context.getResources().getDimensionPixelOffset(R.dimen._5sdp),
                        0,
                        0
                );
            }
            articleTitle.setTextDirection(Utils.getLanguageDirectionForView(article.getLanguageCode()));
            articleTitle.setText(article.getTitle());

            float val = Utils.getHeadlineDimens(prefConfig, context);
            if (val != -1) {
                articleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
            }


            if (prefConfig.isReaderMode()) {

                post_display_container.setVisibility(View.GONE);
                source.setVisibility(View.VISIBLE);

                if (!TextUtils.isEmpty(article.getPublishTime())) {
                    String timeT = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), context);
                    time.setText(timeT);
                }

                if (article.getSource() != null) {
                    if (!TextUtils.isEmpty(article.getSourceNameToDisplay())) {
                        source_name.setText(article.getSourceNameToDisplay());
                    }
                    if (!TextUtils.isEmpty(article.getSourceImageToDisplay())) {
                        Glide.with(source_image)
                                .load(article.getSourceImageToDisplay())
                                .into(source_image);
                    }
                }

            } else {

                post_display_container.setVisibility(View.VISIBLE);
                source.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(article.getImage())) {
                    Glide.with(postImage)
                            .load(article.getImage())
                            .into(postImage);
                }

                String time = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), context);
                postTime.setText(time);

                banner_text.setVisibility(View.VISIBLE);
                if (article.getSource() != null) {

                    if (TextUtils.isEmpty(article.getSource().getImage())) {
                        banner_text.setText(article.getSource().getName() + " ");
                        banner_text.setVisibility(View.VISIBLE);
                        banner_image.setVisibility(View.GONE);
                    } else {
                        Glide.with(postImage)
                                .load(article.getSource().getImage())
                                .into(banner_image);
                        banner_text.setVisibility(View.GONE);
                        banner_image.setVisibility(View.VISIBLE);
                    }
                }
            }


            if (article.isSelected()) {
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID,article.getId());
                AnalyticsEvents.INSTANCE.logEvent(context,
                        params,
                        Events.ARTICLE_HERO);

                if (gotoChannelListener != null) {
                    gotoChannelListener.onArticleSelected(article);
                }

                banner.animate().translationX(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
//                        handler.postDelayed(() -> banner.animate().translationX(-banner.getWidth()), 2500);
                    }
                });
            }

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCommentClick != null)
                        mCommentClick.onDetailClick(0, article);
                }
            });

            banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailsPage(article);
                }
            });
        }
    }

    private void addBullets() {
//        bulletContainer.removeAllViews();
//        int i = 0;
//        for (Bullet bullet : article.getBullets()) {
//            // checking first bullet same as title
//            if (i != 0 || !(bullet.getData().trim().equals(article.getTitle().trim()) || bullet.getData().trim().equals(article.getTitle().trim() + "."))) {
////                if (article.getSource() != null) {
//                bulletContainer.addView(createBullet(bullet, article.getLanguageCode()));
////                } else {
////                    bulletContainer.addView(createBullet(bullet, ""));
////                }
//            }
//            i++;
//        }˚
    }

    private View createBullet(Bullet bullet, String langCode) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if (Utils.getLanguageDirectionForView(langCode) == View.TEXT_DIRECTION_LTR) {
            v = vi.inflate(R.layout.detail_bullet_item, null);
        } else {
            v = vi.inflate(R.layout.detail_bullet_item_rtl, null);
        }
        TextView bulletText = v.findViewById(R.id.bullet_text);
        bulletText.setText(bullet.getData());
        bulletText.setTextDirection(Utils.getLanguageDirectionForView(article.getLanguageCode()));
        float val = Utils.getBulletDimens(prefConfig, context);
        if (val != -1) {
            bulletText.setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
        }
        return v;
    }

    private void resizeVideoView(ViewGroup view, double mVideoHeight, double mVideoWidth) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int cardWidth = displayMetrics.widthPixels - (2 * context.getResources().getDimensionPixelSize(R.dimen._15sdp));

        int screenHeight;
//        if (TextUtils.isEmpty(type))
        screenHeight = displayMetrics.heightPixels - (context.getResources().getDimensionPixelSize(R.dimen._150sdp));
//        else
//            screenHeight = displayMetrics.heightPixels - (getResources().getDimensionPixelSize(R.dimen._220sdp));
        int cardHeight = (int) ((mVideoHeight * cardWidth) / mVideoWidth);
        if (cardHeight > screenHeight) {
            cardHeight = screenHeight;
        }
        view.getLayoutParams().height = cardHeight;
        view.requestLayout();
    }

    private void detailsPage(Article content) {
        if (context instanceof ChannelDetailsActivity) {
            return;
        }
        if (content != null) {
//            if (isGotoFollowShow && !type.equalsIgnoreCase("ARCHIVE")) {
            if (gotoChannelListener != null && content.getSource() != null) {
                if (prefConfig != null) {
                    prefConfig.setSrcLang(content.getSource().getLanguage());
                    prefConfig.setSrcLoc(content.getSource().getCategory());
                }
                gotoChannelListener.onItemClicked(TYPE.SOURCE, content.getSource().getId(), content.getSource().getName(), content.getSource().isFavorite());
//                }
            } else if (content.getAuthor() != null && content.getAuthor().size() > 0) {
                if (TextUtils.isEmpty(type) || !type.equalsIgnoreCase("MY_ARTICLES")) {
                    if (content.getAuthor().get(0) != null) {
                        Utils.openAuthor(context, content.getAuthor().get(0));
                    }
                }
            }
        }
    }

}
