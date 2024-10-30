package com.newsreels.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.newsreels.app.R;
import com.newsreels.app.analytics.AnalyticsEvents;
import com.newsreels.app.analytics.Events;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.data.models.ShareInfo;
import com.newsreels.app.fragments.test.ReelInnerActivity;
import com.newsreels.app.interfaces.AdapterCallback;
import com.newsreels.app.interfaces.DetailsActivityInterface;
import com.newsreels.app.interfaces.ShareInfoInterface;
import com.newsreels.app.interfaces.ShowOptionsLoaderCallback;
import com.newsreels.app.model.Reel.ReelsItem;
import com.newsreels.app.model.articles.Article;
import com.newsreels.app.model.articles.Bullet;
import com.newsreels.app.model.articles.MediaMeta;
import com.newsreels.app.presenter.ShareBottomSheetPresenter;
import com.newsreels.app.utills.Constants;
import com.newsreels.app.utills.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileReelsAdapter extends RecyclerView.Adapter<ProfileReelsAdapter.ViewHolder> {
    private ArrayList<ReelsItem> arrayList;
    private Context context;
    private String mMode;
    private PrefConfig prefConfig;
    private String page;
    private String userContext;

    private Activity activity;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private AdapterCallback adapterCallback;
    private DetailsActivityInterface listener;

    public ProfileReelsAdapter(ArrayList<ReelsItem> arrayList, Context context, String mode, String userContext) {
        this.arrayList = arrayList;
        this.context = context;
        this.userContext = userContext;
        this.mMode = mode;
        prefConfig = new PrefConfig(context);
    }

    public void addShareListener(Activity activity, ShowOptionsLoaderCallback showOptionsLoaderCallback,
                                 AdapterCallback adapterCallback,
                                 DetailsActivityInterface listener) {
        this.activity = activity;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.adapterCallback = adapterCallback;
        this.listener = listener;
    }

    public void updateRecords(ArrayList<ReelsItem> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_reels, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(arrayList.get(position), position);
    }

    public void setNextPageParam(String page) {
        this.page = page;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView tvViewCount;
        private final TextView tvReelDescription;
        private final ImageView ivReelOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            tvViewCount = itemView.findViewById(R.id.tv_views_total);
            tvReelDescription = itemView.findViewById(R.id.tv_reel_title);
            ivReelOptions = itemView.findViewById(R.id.iv_reel_option);
            shareBottomSheetPresenter = new ShareBottomSheetPresenter(activity);
        }

        private void bind(ReelsItem reelsItem, int position) {
            if (reelsItem != null) {
                if (!TextUtils.isEmpty(reelsItem.getImage())) {
                    Glide.with(context)
                            .load(reelsItem.getImage())
                            .placeholder(R.drawable.ad_card_button_shape)
                            .override(Constants.targetWidth, Constants.targetHeight)
                            .into(imageView);
                }

                if (!TextUtils.isEmpty(reelsItem.getInfo().getViewCount())) {
                    tvViewCount.setText(Utils.formatViews(Long.valueOf(reelsItem.getInfo().getViewCount())) + " views");
                }

                if (reelsItem.getDescription() != null) {
                    tvReelDescription.setText(reelsItem.getDescription());
                }

                itemView.setOnClickListener(v -> {
                    int pos;
                    if (arrayList.size() > position + 12) {
                        pos = position + 12;
                    } else {
                        pos = arrayList.size();
                    }
                    ArrayList<ReelsItem> reelsItems = new ArrayList<ReelsItem>(arrayList.subList(position, pos));

                    Intent intent = new Intent(context, ReelInnerActivity.class);
                    if (reelsItem.getAuthor() != null && reelsItem.getAuthor().size() > 0) {
                        intent.putExtra(ReelInnerActivity.REEL_F_AUTHOR_ID, reelsItem.getAuthor().get(0).getId());
                        intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, userContext);
                    }
                    if (reelsItem.getSource() != null) {
                        intent.putExtra(ReelInnerActivity.REEL_F_SOURCE_ID, reelsItem.getSource().getId());
                        intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, userContext);
                    }
//                    intent.putExtra(ReelActivity.REEL_MODE, "public");
                    intent.putExtra(ReelInnerActivity.REEL_F_MODE, mMode);
                    //sending sublist of reels to reel activity with start position as 0 ie selected position
                    intent.putExtra(ReelInnerActivity.REEL_POSITION, 0);
                    intent.putExtra(ReelInnerActivity.REEL_F_PAGE, page);
                    intent.putParcelableArrayListExtra(ReelInnerActivity.REEL_F_DATALIST, reelsItems);
                    context.startActivity(intent);
                });

                ivReelOptions.setOnClickListener(v -> {
                    Map<String, String> params = new HashMap<>();
                    params.put(Events.KEYS.REEL_ID, reelsItem.getId());
                    AnalyticsEvents.INSTANCE.logEvent(itemView.getContext(),
                            params,
                            Events.SHARE_CLICK);
                    if (listener != null) {
                        listener.pause();
                    }
                    if (shareBottomSheetPresenter != null) {
                        showOptionsLoaderCallback.showLoader(true);
                        shareBottomSheetPresenter.share_msg(reelsItem.getId(), new ShareInfoInterface() {
                            @Override
                            public void response(ShareInfo shareInfo) {
                                showOptionsLoaderCallback.showLoader(false);
                                adapterCallback.showShareBottomSheet(shareInfo, getArticleFromReels(reelsItem), new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        if (listener != null) {
                                            listener.resume();
                                            Constants.sharePgNotVisible = true;
                                        }
                                    }
                                });
                            }

                            @Override
                            public void error(String error) {
                                showOptionsLoaderCallback.showLoader(false);
                                adapterCallback.showShareBottomSheet(null, getArticleFromReels(reelsItem), dialog -> {
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

        private Article getArticleFromReels(ReelsItem reels) {
            Article article = new Article();
            if (reels != null) {
                article.setId(reels.getId());
                article.setTitle(reels.getDescription());
                article.setLink(reels.getMedia());
                article.setPublishTime(reels.getPublishTime());
                article.setType("REELS");
                article.setSource(reels.getSource());
                article.setAuthor(reels.getAuthor());
                MediaMeta mediaMeta = new MediaMeta();
                if (reels.getMediaMeta() != null) {
                    mediaMeta.setDuration(reels.getMediaMeta().getDuration());
                    mediaMeta.setHeight(reels.getMediaMeta().getHeight());
                    mediaMeta.setWidth(reels.getMediaMeta().getWidth());
                }
                article.setMediaMeta(mediaMeta);
                ArrayList<Bullet> bullets = new ArrayList<>();
                Bullet bullet = new Bullet();
                bullet.setImage(reels.getImage());
                bullet.setData(reels.getDescription());
                bullets.add(bullet);
                article.setBullets(bullets);
                article.setSelected(true);
            }
            return article;
        }

    }
}
