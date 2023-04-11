package com.ziro.bullet.adapters.discover;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ArticleCardViewHolder extends RecyclerView.ViewHolder {

    private final TextView text2;
    private final TextView source_name;
    private final TextView headline;
//    private final TextView name;
    private final TextView timeTxt;
    private final ImageView image;
    private final RelativeLayout dotImg;
    private final CircleImageView source_image;
    private final LinearLayout card;
    private Activity mContext;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private AdapterCallback adapterCallback;
    private PrefConfig mPrefConfig;

    public ArticleCardViewHolder(@NonNull View itemView, Activity context, AdapterCallback adapterCallback) {
        super(itemView);
        this.adapterCallback = adapterCallback;
        this.mContext = context;
        shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
        mPrefConfig = new PrefConfig(context);
        dotImg = itemView.findViewById(R.id.dotImg);
        card = itemView.findViewById(R.id.card);
        text2 = itemView.findViewById(R.id.text2);
        source_name = itemView.findViewById(R.id.source_name);
        headline = itemView.findViewById(R.id.headline);
//        name = itemView.findViewById(R.id.name);
        timeTxt = itemView.findViewById(R.id.time);
        image = itemView.findViewById(R.id.image);
        source_image = itemView.findViewById(R.id.source_image);
    }

    public void bind(String title, DiscoverItem item) {

        if (item != null && item.getData() != null) {

            if (!TextUtils.isEmpty(title)) {
                text2.setText(title);
            }

            com.ziro.bullet.model.articles.Article article = item.getData().getArticle();
            if (article != null) {

                if (article.getSourceImageToDisplay() != null && !article.getSourceImageToDisplay().equals("")) {
                    Picasso.get()
                            .load(article.getSourceImageToDisplay())
                            .resize(Constants.targetWidth, Constants.targetHeight)
                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .transform(new CropCircleTransformation())
                            .into(source_image);
                } else {
                    Picasso.get()
                            .load(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .resize(Constants.targetWidth, Constants.targetHeight)
                            .transform(new CropCircleTransformation())
                            .into(source_image);
                }
                if (!article.getAuthorNameToDisplay().equals("")) {
                    source_name.setText(article.getAuthorNameToDisplay());
                } else {
                    source_name.setText(article.getSourceNameToDisplay());
                }

                headline.setText(article.getTitle());

                if (!TextUtils.isEmpty(article.getImage())) {
                    Picasso.get()
                            .load(article.getImage())
                            .resize(Constants.targetWidth, Constants.targetHeight)
                            .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                            .into(image);
                }

                if (!TextUtils.isEmpty(article.getPublishTime())) {
                    String time = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), mContext);
                    if (!TextUtils.isEmpty(time)) {
                        timeTxt.setText(time);
                    }
                }
                if (article.getSource() != null) {
                    if (!TextUtils.isEmpty(article.getSource().getName())) {
                        source_name.setText(article.getSource().getName());
                    }
                    if (!TextUtils.isEmpty(article.getSource().getIcon())) {
                        Picasso.get()
                                .load(article.getSource().getIcon())
                                .resize(Constants.targetWidth, Constants.targetHeight)
                                .transform(new CropCircleTransformation())
                                .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                                .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                                .into(source_image);
                    }
                } else if (article.getAuthor() != null && article.getAuthor().size() > 0) {
                    if (article.getAuthor().get(0) != null) {
                        if (!TextUtils.isEmpty(article.getAuthor().get(0).getName())) {
                            source_name.setText(article.getAuthor().get(0).getName());
                        }
                        if (!TextUtils.isEmpty(article.getAuthor().get(0).getImage())) {
                            Picasso.get()
                                    .load(article.getAuthor().get(0).getImage())
                                    .resize(Constants.targetWidth, Constants.targetHeight)
                                    .transform(new CropCircleTransformation())
                                    .error(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                                    .placeholder(Utils.getPlaceholderForTheme(mPrefConfig.getAppTheme()))
                                    .into(source_image);
                        }
                    }
                }

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, BulletDetailActivity.class);
                    intent.putExtra("article", new Gson().toJson(article));
                    mContext.startActivity(intent);
                });

//                dotImg.setOnClickListener(v -> {
//                    shareBottomSheetPresenter.share_msg(article.getId(), new ShareInfoInterface() {
//                        @Override
//                        public void response(ShareInfo shareInfo) {
//                            adapterCallback.showShareBottomSheet(shareInfo, article, dialog -> {
//
//                            });
//                        }
//
//                        @Override
//                        public void error(String error) {
//
//                        }
//                    });
//                });
            }
        }
    }
}
