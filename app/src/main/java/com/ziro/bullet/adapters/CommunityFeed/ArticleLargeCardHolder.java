package com.ziro.bullet.adapters.CommunityFeed;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ReportBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DeleteCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;

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
