package com.newsreels.app.adapters.feed;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.newsreels.app.data.PrefConfig;

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

import org.jetbrains.annotations.NotNull;

public class LastLargeCardViewHolder extends LargeCardViewHolder{

    private TextView categoryName;
    private TextView categoryTag;
    private ConstraintLayout footerBtn;
    private TempCategorySwipeListener swipeListener;

    public LastLargeCardViewHolder(CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull @NotNull View itemView, Activity context, AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener, DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(false,mCommentClick, type, isPostArticle, itemView, context, adapterCallback, config, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback);
        this.swipeListener = swipeListener;
//        categoryName = itemView.findViewById(R.id.category_name);
//        categoryTag = itemView.findViewById(R.id.category_tag);
//        footerBtn = itemView.findViewById(R.id.footer_btn);
    }

    @Override
    public void bind(int position, Article article) {
        super.bind(position, article);
//        categoryName.setText(String.format("%s %s", article.getForYouTitle(), categoryName.getContext().getString(R.string.news)));
//        categoryTag.setText(String.format("%s ", categoryTag.getContext().getString(R.string.more)));
//
//        footerBtn.setOnClickListener(v -> {
//            if (swipeListener != null && article != null && !TextUtils.isEmpty(article.getTabId()))
//                swipeListener.selectTab(article.getTabId());
//        });
    }
}
