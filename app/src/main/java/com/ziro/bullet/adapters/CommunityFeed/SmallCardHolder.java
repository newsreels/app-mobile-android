package com.ziro.bullet.adapters.CommunityFeed;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DeleteCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.HandleFlag;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;

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
