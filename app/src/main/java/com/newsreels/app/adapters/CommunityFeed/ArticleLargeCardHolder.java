package com.newsreels.app.adapters.CommunityFeed;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.newsreels.app.R;
import com.newsreels.app.adapters.feed.LargeCardViewHolder;
import com.newsreels.app.analytics.AnalyticsEvents;
import com.newsreels.app.analytics.Events;
import com.newsreels.app.bottomSheet.ReportBottomSheet;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.interfaces.AdapterCallback;
import com.newsreels.app.interfaces.CommentClick;
import com.newsreels.app.interfaces.DeleteCallback;
import com.newsreels.app.interfaces.DetailsActivityInterface;
import com.newsreels.app.interfaces.NewsCallback;
import com.newsreels.app.interfaces.OnGotoChannelListener;
import com.newsreels.app.interfaces.ShareToMainInterface;
import com.newsreels.app.interfaces.ShowOptionsLoaderCallback;
import com.newsreels.app.interfaces.TempCategorySwipeListener;
import com.newsreels.app.interfaces.GoHome;
import com.newsreels.app.model.articles.Article;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ArticleLargeCardHolder extends LargeCardViewHolder {

    private LinearLayout rootCard;
    private ImageView deny, accept, flag;
    private Activity context;
    private DeleteCallback deleteCallback;
    private boolean isWhiteOnly;

    public ArticleLargeCardHolder(boolean isWhiteOnly, DeleteCallback deleteCallback, CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull @NotNull View view, Activity context, AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener, DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(isWhiteOnly, mCommentClick, type, isPostArticle, view, context, adapterCallback, config, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
        rootCard = view.findViewById(R.id.rootCard);
        deny = view.findViewById(R.id.deny);
        accept = view.findViewById(R.id.accept);
        flag = view.findViewById(R.id.flag);
        this.context = context;
        this.isWhiteOnly = isWhiteOnly;
        this.deleteCallback = deleteCallback;
    }

    public void bind(int position, Article article) {
        super.bind(position, article);
        Log.d("largecard", "bind: pos = " + position);
        if (!isWhiteOnly) {
            invalidate();
            rootCard.setBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
        } else {
            forReelBottomSheet();
        }
        if (article != null) {
            flag.setOnClickListener(v -> {
                Map<String,String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID,article.getId());
                AnalyticsEvents.INSTANCE.logEvent(context,
                        params,
                        Events.CF_REPORT_CLICK);
                ReportBottomSheet reportBottomSheet = new ReportBottomSheet(context, flag -> {
                    if (flag) {
                        if (deleteCallback != null) deleteCallback.deleteItem(position);
                    }
                });
                reportBottomSheet.show(article.getId(), "articles");
            });
        }
    }
}
