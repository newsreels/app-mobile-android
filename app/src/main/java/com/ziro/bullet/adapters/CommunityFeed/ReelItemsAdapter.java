package com.ziro.bullet.adapters.CommunityFeed;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.fragments.Reels.ReelsPageInterface;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.articles.MediaMeta;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReelItemsAdapter extends RecyclerView.Adapter<ReelItemsAdapter.ViewHolder> {

    private final ArrayList<ReelsItem> reelsItemArrayList;
    private PrefConfig mPrefConfig;
    private Activity context;
    ReelsPageInterface reelsPageInterface;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private AdapterCallback adapterCallback;
    private DetailsActivityInterface listener;

    public ReelItemsAdapter(Activity context, ArrayList<ReelsItem> data) {
        this.reelsItemArrayList = data;
        this.context = context;
        mPrefConfig = new PrefConfig(context);
        this.shareBottomSheetPresenter = new ShareBottomSheetPresenter(context);
    }

    public void addShareListener(ShowOptionsLoaderCallback showOptionsLoaderCallback,
                                 AdapterCallback adapterCallback,
                                 DetailsActivityInterface listener) {
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.adapterCallback = adapterCallback;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_reel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (reelsItemArrayList != null && reelsItemArrayList.size() > 0) {

            ReelsItem item = reelsItemArrayList.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Glide.with(context)
                        .load(item.getImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.image);
            }

            if (!TextUtils.isEmpty(item.getInfo().getViewCount())) {
                holder.tvReelViews.setText(Utils.formatViews(Long.parseLong(item.getInfo().getViewCount())) + " views");
            }

            if (!TextUtils.isEmpty(item.getDescription())) {
                holder.tvReelTitle.setText(item.getDescription());
            }

            holder.card.setOnClickListener(v -> {
                int pos = position;
                try {
                    if (reelsItemArrayList.size() > position + 10) {
                        pos = position + 10;
                    } else {
                        pos = reelsItemArrayList.size();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ArrayList<ReelsItem> reelsItems = new ArrayList<ReelsItem>(reelsItemArrayList.subList(position, pos));
                //check for the other flow that opens through this adapter
                if (!Constants.reelfragment) {
                    if (Constants.rvm > 0) {
                        Constants.rvmdailogopen = true;
                        Log.e("rvm", "onBindViewHolder: 0");
                        context.finish();
                    }
                } else {
                    if (Constants.rvm < 2) {
                        Log.e("rvm", "onBindViewHolder: 1");
                    } else {
                        Log.e("rvm", "onBindViewHolder: 2 finish");
                        context.finish();
                    }
                }

                callReelActivity(item, reelsItems);
//                callReelActivity(item, reelsItemArrayList);
            });

            holder.ivReelOptions.setOnClickListener(v -> {
                Map<String, String> params = new HashMap<>();
                params.put(Events.KEYS.REEL_ID, item.getId());
                AnalyticsEvents.INSTANCE.logEvent(holder.itemView.getContext(),
                        params,
                        Events.SHARE_CLICK);
                if (listener != null) {
                    listener.pause();
                }
                if (shareBottomSheetPresenter != null) {
                    showOptionsLoaderCallback.showLoader(true);
                    shareBottomSheetPresenter.share_msg(item.getId(), new ShareInfoInterface() {
                        @Override
                        public void response(ShareInfo shareInfo) {
                            showOptionsLoaderCallback.showLoader(false);
                            adapterCallback.showShareBottomSheet(shareInfo, getArticleFromReels(item), new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (listener != null) {
                                        listener.resume();
                                    }
                                }
                            });
                        }

                        @Override
                        public void error(String error) {
                            showOptionsLoaderCallback.showLoader(false);
                            adapterCallback.showShareBottomSheet(null, getArticleFromReels(item), dialog -> {
                                if (listener != null) {
                                    listener.resume();
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

    private void callReelActivity(ReelsItem item, ArrayList<ReelsItem> reelsItems) {
        Log.e("testreel", "callReelActivity: ");
        Intent intent = new Intent(context, ReelInnerActivity.class);
        intent.putExtra(ReelInnerActivity.REEL_F_AUTHOR_ID, item.getId());
        intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, item.getContext());
        intent.putExtra(ReelInnerActivity.REEL_F_MODE, "public");
        intent.putExtra(ReelInnerActivity.REEL_POSITION, 0);

        intent.putParcelableArrayListExtra(ReelInnerActivity.REEL_F_DATALIST, reelsItems);
        context.startActivity(intent);
    }

    public void addinterface(ReelsPageInterface reelsPageInterface) {
        this.reelsPageInterface = reelsPageInterface;
    }

    @Override
    public int getItemCount() {
        if (reelsItemArrayList != null)
            return reelsItemArrayList.size();
        else return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout card;
        private final TextView tvReelViews;
        private final TextView tvReelTitle;
        private final ImageView image;
        private final ImageView ivReelOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReelViews = itemView.findViewById(R.id.tv_views_total);
            tvReelTitle = itemView.findViewById(R.id.tv_reel_title);
            card = itemView.findViewById(R.id.card);
            image = itemView.findViewById(R.id.image);
            ivReelOptions = itemView.findViewById(R.id.iv_reel_option);
        }
    }
}
