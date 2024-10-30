package com.newsreels.app.adapters.NewFeed.newHomeArticle;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.newsreels.app.R;
import com.newsreels.app.analytics.AnalyticsEvents;
import com.newsreels.app.analytics.Events;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.data.models.ShareInfo;
import com.newsreels.app.interfaces.AdapterCallback;
import com.newsreels.app.interfaces.CommentClick;
import com.newsreels.app.interfaces.DetailsActivityInterface;
import com.newsreels.app.interfaces.ShareInfoInterface;
import com.newsreels.app.interfaces.ShowOptionsLoaderCallback;
import com.newsreels.app.model.articles.Article;
import com.newsreels.app.model.articles.Bullet;
import com.newsreels.app.presenter.ShareBottomSheetPresenter;
import com.newsreels.app.utills.Constants;
import com.newsreels.app.utills.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewHomeArticlesViewHolder extends RecyclerView.ViewHolder {

    public TextView tvBulletHeadline;
    private AppCompatActivity context;
    private View itemView;
    private ImageView ivBulletCover;
    private ImageView ivDots;
    private TextView tvSource;
    private TextView tvTimeSincePosted;
    private CommentClick commentClick;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private AdapterCallback adapterCallback;
    private DetailsActivityInterface listener;

    public NewHomeArticlesViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public NewHomeArticlesViewHolder(AppCompatActivity context, @NonNull View itemView, CommentClick commentClick, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                                     AdapterCallback adapterCallback,
                                     DetailsActivityInterface listener) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
        this.commentClick = commentClick;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
        this.adapterCallback = adapterCallback;
        this.listener = listener;
        tvBulletHeadline = itemView.findViewById(R.id.tv_bullet_headline);
        ivBulletCover = itemView.findViewById(R.id.bullet_image);
        tvSource = itemView.findViewById(R.id.source_name);
        tvTimeSincePosted = itemView.findViewById(R.id.time);
        ivDots = itemView.findViewById(R.id.dotImg);
    }

    public void onBind(Article article, int position, List<Article> articlelist) {

        if (article != null && article.getType() != null) {
            Log.d("DEBUG_TAG", "onBind: ArticleType:: " + article.getType());
            List<Bullet> bulletArrayList = article.getBullets();

            if (bulletArrayList != null && !bulletArrayList.isEmpty()) {
                tvBulletHeadline.setText(bulletArrayList.get(0).getData());
                float textSize = Utils.getBulletDimens(new PrefConfig(itemView.getContext()), context);
                tvBulletHeadline.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            int width = (int) (70 * itemView.getContext().getResources().getDisplayMetrics().density);
            int height = (int) (70 * itemView.getContext().getResources().getDisplayMetrics().density);
            try {
                Glide.with(itemView.getContext())
                        .load(article.getImage())
                        .encodeQuality(50)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(width, height)
                        .skipMemoryCache(false) // enable memory caching
                        .priority(Priority.HIGH)
                        .into(ivBulletCover);
            } catch (Exception e) {
                Picasso.get()
                        .load(article.getImage())
                        .resize(width, height)
                        .centerCrop()
                        .into(ivBulletCover);
            }

            if (article.getSource() != null && !TextUtils.isEmpty(article.getSource().getName())) {
                tvSource.setText(article.getSource().getName());
            }

            String time = Utils.getTimeAgo(Utils.getDate(article.getPublishTime()), itemView.getContext());
            if (!TextUtils.isEmpty(time)) {
                tvTimeSincePosted.setText(time);
            }

            itemView.setOnClickListener(v -> {
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID, article.getId());
                AnalyticsEvents.INSTANCE.logEvent(itemView.getContext(),
                        params,
                        Events.ARTICLE_OPEN);

                if (position + 10 < articlelist.size()) {
                    List<Article> copyArray = new ArrayList<>(articlelist.subList(position, position + 10));
                    commentClick.onNewDetailClick(position, article, copyArray);
                } else {
                    List<Article> copyArray = new ArrayList<>(articlelist.subList(position, articlelist.size()));
                    commentClick.onNewDetailClick(position, article, copyArray);
                }
            });

            ivDots.setOnClickListener(v -> {

                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.ARTICLE_ID, article.getId());
                AnalyticsEvents.INSTANCE.logEvent(itemView.getContext(),
                        params,
                        Events.SHARE_CLICK);
                if (listener != null) {
                    listener.pause();
                }

                if (shareBottomSheetPresenter != null) {
                    showOptionsLoaderCallback.showLoader(true);
                    shareBottomSheetPresenter.share_msg(article.getId(), new ShareInfoInterface() {
                        @Override
                        public void response(ShareInfo shareInfo) {
                            showOptionsLoaderCallback.showLoader(false);
                            adapterCallback.showShareBottomSheet(shareInfo, article, new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (listener != null) {
                                        Constants.sharePgNotVisible = true;
                                        listener.resume();
                                    }
                                }
                            });
                        }

                        @Override
                        public void error(String error) {
                            showOptionsLoaderCallback.showLoader(false);
                            adapterCallback.showShareBottomSheet(null, article, dialog -> {
                                if (listener != null) {
                                    listener.resume();
                                    Constants.sharePgNotVisible = true;
                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
