package com.ziro.bullet.adapters.foryou;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.adapters.NewFeed.YoutubeViewHolderEdge;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.utills.Utils;

import java.util.HashMap;
import java.util.Map;

public class EdgeToEdgeYoutube extends YoutubeViewHolderEdge {

    private RelativeLayout banner;
    private ImageView odd_image;
    private LinearLayout cardMain;
    private Handler handler = new Handler();
    private CommentClick mCommentClick;
    private Activity context;
    private OnGotoChannelListener gotoChannelListener;
    private PrefConfig prefConfig;
    private String type;

    public EdgeToEdgeYoutube(boolean isCommunity, CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull View itemView, AppCompatActivity context,
                             AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity,
                             ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener,
                             NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener,
                             DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                             Lifecycle lifecycle) {
        super(isCommunity, mCommentClick, type, isPostArticle, itemView, context, adapterCallback, config, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback, lifecycle);
        this.mCommentClick = mCommentClick;
        this.context = context;
        this.gotoChannelListener = gotoChannelListener;
        this.prefConfig = config;
        this.type = type;
        banner = itemView.findViewById(R.id.banner);
        cardMain = itemView.findViewById(R.id.cardMain);
        odd_image = itemView.findViewById(R.id.odd_image);
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

            odd_image.setOnClickListener(v -> {
                super.youtubeResume();
            });
        }

        cardMain.setOnClickListener(v -> {
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
