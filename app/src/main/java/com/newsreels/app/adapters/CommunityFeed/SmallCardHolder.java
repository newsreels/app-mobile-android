package com.newsreels.app.adapters.CommunityFeed;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.newsreels.app.adapters.feed.SmallCardViewHolder;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.interfaces.AdapterCallback;
import com.newsreels.app.interfaces.CommentClick;
import com.newsreels.app.interfaces.DeleteCallback;
import com.newsreels.app.interfaces.DetailsActivityInterface;
import com.newsreels.app.interfaces.HandleFlag;
import com.newsreels.app.interfaces.NewsCallback;
import com.newsreels.app.interfaces.OnGotoChannelListener;
import com.newsreels.app.interfaces.ShareToMainInterface;
import com.newsreels.app.interfaces.ShowOptionsLoaderCallback;
import com.newsreels.app.interfaces.TempCategorySwipeListener;
import com.newsreels.app.interfaces.GoHome;
import com.newsreels.app.model.articles.Article;

public class SmallCardHolder extends SmallCardViewHolder implements HandleFlag {

    private CardView rootCard;
    private ImageView flag;
    private boolean isWhiteOnly;
    private Activity context;
    private DeleteCallback deleteCallback;

    public SmallCardHolder(boolean isWhiteOnly, DeleteCallback deleteCallback, CommentClick mCommentClick, String type, View itemView, Activity context, AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener, DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(isWhiteOnly, mCommentClick, type, itemView, context, adapterCallback, config, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
//        this.context = context;
//        this.isWhiteOnly = isWhiteOnly;
//        this.deleteCallback = deleteCallback;
//        rootCard = itemView.findViewById(R.id.rootCard);
//        flag = itemView.findViewById(R.id.flag);
//        setFlagListener(this);
    }

    public void bind(int position, Article article) {
        super.bind(position, article);
//        Log.d("largecard", "bind: pos = " + position);
//        if (!isWhiteOnly) {
//            invalidate();
//            rootCard.setCardBackgroundColor(context.getResources().getColor(R.color.discover_card_bg));
//        } else {
            forReelBottomSheet();
//        }
//        if (article != null) {
//            flag.setOnClickListener(v -> {
//                Utils.logEvent(context, "cf_report_click");
//                ReportBottomSheet reportBottomSheet = new ReportBottomSheet(context, flag -> {
//                    if (flag) {
//                        if (deleteCallback != null) deleteCallback.deleteItem(position);
//                    }
//                });
//                reportBottomSheet.show(article.getId());
//            });
//        }
    }

    @Override
    public void flag(boolean show) {
//        if (flag != null) {
//            if (show) {
//                flag.setVisibility(View.VISIBLE);
//            } else {
//                flag.setVisibility(View.GONE);
//            }
//        }
    }
}
