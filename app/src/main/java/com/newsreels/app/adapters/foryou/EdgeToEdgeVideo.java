package com.newsreels.app.adapters.foryou;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.newsreels.app.R;
import com.newsreels.app.activities.ChannelDetailsActivity;
import com.newsreels.app.adapters.feed.VideoViewHolder;
import com.newsreels.app.analytics.AnalyticsEvents;
import com.newsreels.app.analytics.Events;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.data.TYPE;
import com.newsreels.app.interfaces.AdapterCallback;
import com.newsreels.app.interfaces.CommentClick;
import com.newsreels.app.interfaces.DetailsActivityInterface;
import com.newsreels.app.interfaces.NewsCallback;
import com.newsreels.app.interfaces.OnGotoChannelListener;
import com.newsreels.app.interfaces.ShareToMainInterface;
import com.newsreels.app.interfaces.ShowOptionsLoaderCallback;
import com.newsreels.app.interfaces.TempCategorySwipeListener;
import com.newsreels.app.interfaces.GoHome;
import com.newsreels.app.model.articles.Article;
import com.newsreels.app.utills.Utils;

import java.util.HashMap;
import java.util.Map;

public class EdgeToEdgeVideo extends VideoViewHolder {

    private RelativeLayout banner;
    private Handler handler = new Handler();
    private CommentClick mCommentClick;
    private LinearLayout card;
    private Activity context;
    private OnGotoChannelListener gotoChannelListener;
    private PrefConfig prefConfig;
    private String type;

    public EdgeToEdgeVideo(CommentClick mCommentClick, String type, boolean isPostArticle, View itemView, Activity context,
                           AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity,
                           ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener,
                           NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener,
                           DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(mCommentClick, type, isPostArticle, itemView, context, adapterCallback, config, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
        this.mCommentClick = mCommentClick;
        this.context = context;
        this.gotoChannelListener = gotoChannelListener;
        this.prefConfig = config;
        this.type = type;
        banner = itemView.findViewById(R.id.banner);
        card = itemView.findViewById(R.id.card);
    }

    public void bind(int position, Article article) {
        super.bind(position, article);

        if (article.isSelected()) {
            Map<String,String> params = new HashMap<>();
            params.put(Events.KEYS.ARTICLE_ID,article.getId());
            AnalyticsEvents.INSTANCE.logEvent(context,
                    params,
                    Events.ARTICLE_HERO);

            banner.animate().translationX(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                        handler.postDelayed(() -> banner.animate().translationX(-banner.getWidth()), 2500);
                }
            });
        }
        card.setOnClickListener(v -> {
            if (mCommentClick != null)
                mCommentClick.onDetailClick(0, article);
        });


        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsPage(article);
            }
        });
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
