package com.ziro.bullet.adapters.CommunityFeed;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
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

import java.util.HashMap;
import java.util.Map;

public class VideoCardHolder extends VideoViewHolder {

    private CardView rootCard;
    private Activity context;
    private ImageView flag;
    private DeleteCallback deleteCallback;
    private boolean isWhiteOnly;

    public VideoCardHolder(boolean isWhiteOnly, DeleteCallback deleteCallback, CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull View itemView, Activity context, AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener, DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(mCommentClick, type, isPostArticle, itemView, context, adapterCallback, config, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
        this.context = context;
        this.isWhiteOnly = isWhiteOnly;
        this.deleteCallback = deleteCallback;
        flag = itemView.findViewById(R.id.flag);
        rootCard = itemView.findViewById(R.id.rootCard);
    }

    public void bind(int position, Article article) {
        super.bind(position, article);
        Log.d("card", "bind: pos = " + position);
        if (!isWhiteOnly) {
            invalidate();
            rootCard.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
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
        flag.setVisibility(View.GONE);
    }

    @Override
    protected void showFlagAndAudioBtn(boolean show) {
        super.showFlagAndAudioBtn(show);
        flag.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
