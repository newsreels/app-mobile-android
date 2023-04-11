package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;

import java.util.ArrayList;

public class ListCardViewHolder extends RecyclerView.ViewHolder {

    private final TextView text1;
    private final TextView text2;
    private final LinearLayout container;
    private final LinearLayout card;
    private Activity mContext;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private AdapterCallback adapterCallback;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private CommentClick commentClick;
    private LinearLayout transformationLayout;

    public ListCardViewHolder(CommentClick commentClick, @NonNull View itemView, Activity context, AdapterCallback adapterCallback, ShowOptionsLoaderCallback showOptionsLoaderCallback) {
        super(itemView);
        this.commentClick = commentClick;
        this.mContext = context;
        this.adapterCallback = adapterCallback;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
        text1 = itemView.findViewById(R.id.text1);
        text2 = itemView.findViewById(R.id.text2);
        container = itemView.findViewById(R.id.container);
        card = itemView.findViewById(R.id.card);
        transformationLayout = itemView.findViewById(R.id.transformationLayout);
    }

    public void bind(int position, DiscoverItem item) {

        if (mContext != null) {
            transformationLayout.setBackground(mContext.getResources().getDrawable(R.drawable.discover_card_shape));
            text1.setTextColor(mContext.getResources().getColor(R.color.discover_card_grey_text));
            text2.setTextColor(mContext.getResources().getColor(R.color.discover_card_title));
        }

        if (item != null && item.getData() != null) {
            if (!TextUtils.isEmpty(item.getSubtitle())) {
                text1.setText(item.getSubtitle());
            }
            if (!TextUtils.isEmpty(item.getTitle())) {
                text2.setText(item.getTitle());
            }
            container.removeAllViews();
            ArrayList<com.ziro.bullet.model.articles.Article> articles = item.getData().getArticles();
            if (articles != null && articles.size() > 0) {
                for (int i = 0; i < articles.size(); i++) {
                    com.ziro.bullet.model.articles.Article articlesItem = articles.get(i);
                    if (articlesItem != null) {
                        View view = LayoutInflater.from(mContext).inflate(R.layout.discover_item_small_card, null, false);
                        ListSmallCardViewHolder holder = new ListSmallCardViewHolder(commentClick, view, mContext, adapterCallback, shareBottomSheetPresenter, showOptionsLoaderCallback);
                        holder.bind(i, articlesItem);
                        holder.getLine().setVisibility(View.GONE);
                        holder.getCommentMain().setVisibility(View.VISIBLE);
                        holder.getLikeMain().setVisibility(View.VISIBLE);
                        if (i == articles.size() - 1) {
                            holder.getDivider().setVisibility(View.GONE);
                        }
                        container.addView(view);
                    }
                }
            }
        }

        card.setOnClickListener(v -> {
            if (item != null && mContext != null) {
                Intent intent = new Intent(mContext, ChannelPostActivity.class);
                intent.putExtra("type", TYPE.TRENDING);
                intent.putExtra("context", item.getData().getContext());
                intent.putExtra("name", item.getSubtitle());
                intent.putExtra("subtitle", item.getTitle());
                //mContext.startActivity(intent);

//                Bundle bundle = transformationLayout.withActivity(mContext, "myTransitionName");
//                //Intent intent = new Intent(this, DetailActivity.class);
//                intent.putExtra("TransformationParams", transformationLayout.getParcelableParams());
//                mContext.startActivity(intent, bundle);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        mContext,
                        transformationLayout,
                        "shared_element_container" // The transition name to be matched in Activity B.
                );
                mContext.startActivity(intent, options.toBundle());
            }
        });
    }
}
