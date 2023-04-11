package com.ziro.bullet.adapters.feed;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;

import com.ziro.bullet.data.PrefConfig;

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

import org.jetbrains.annotations.NotNull;

public class LastYoutubeViewHolder extends YoutubeViewHolder{

    private TextView categoryName;
    private TextView categoryTag;
    private ConstraintLayout footerBtn;
    private TempCategorySwipeListener swipeListener;

    public LastYoutubeViewHolder(CommentClick mCommentClick, String type, boolean isPostArticle, @NonNull @NotNull View itemView, AppCompatActivity context, AdapterCallback adapterCallback, PrefConfig config, GoHome goHomeMainActivity, ShareToMainInterface shareToMainInterface, TempCategorySwipeListener swipeListener, NewsCallback categoryCallback, OnGotoChannelListener gotoChannelListener, DetailsActivityInterface detailsActivityInterface, ShowOptionsLoaderCallback showOptionsLoaderCallback, Lifecycle lifecycle) {
        super(false,mCommentClick, type, isPostArticle, itemView, context, adapterCallback, config, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener, detailsActivityInterface, showOptionsLoaderCallback, lifecycle);
//        this.swipeListener = swipeListener;
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
