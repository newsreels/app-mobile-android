package com.ziro.bullet.adapters.schedule;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.PostArticleActivity;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.fragments.LoadingDialog;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnScheduleCallback;
import com.ziro.bullet.interfaces.ScheduleTimerFinishListener;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.presenter.SharePresenter;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ScheduledListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterCallback, OnScheduleCallback {
    private static final int TYPE_VIDEO = 142;
    private static final int TYPE_YOUTUBE = 152;
    private static final int TYPE_REEL = 162;
    private static final int TYPE_ARTICLE = 172;
    private Activity mContext;
    private ArrayList<Article> mPostsList;

    private int currentArticlePosition = 0;

    private PrefConfig prefConfig;
    private NewsCallback categoryCallback;
    private DetailsActivityInterface detailsActivityInterface;
    private SharePresenter sharePresenter;
    private LoadingDialog loadingDialog;
    private ScheduleTimerFinishListener timerFinishListener;
    private TYPE type;


    public ScheduledListAdapter(ScheduleTimerFinishListener timerFinishListener, TYPE type, Activity mContext, ArrayList<Article> mPostsList,
                                NewsCallback categoryCallback, DetailsActivityInterface detailsActivityInterface) {
        this.timerFinishListener = timerFinishListener;
        this.type = type;
        this.mContext = mContext;
        this.mPostsList = mPostsList;
        this.categoryCallback = categoryCallback;
        this.detailsActivityInterface = detailsActivityInterface;

        prefConfig = new PrefConfig(mContext);
        sharePresenter = new SharePresenter(mContext, null, null);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_VIDEO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_video_card, parent, false);
                VideoViewHolder videoViewHolder = new VideoViewHolder(timerFinishListener, view, prefConfig, mContext, this, categoryCallback, detailsActivityInterface, this, type);

                ViewTreeObserver viewTreeObserverVideo = videoViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserverVideo.isAlive()) {
                    viewTreeObserverVideo.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            videoViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = videoViewHolder.itemView.getHeight();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }

                return videoViewHolder;
            case TYPE_YOUTUBE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_youtube_card, parent, false);
                YoutubeViewHolder youtubeViewHolder = new YoutubeViewHolder(false, timerFinishListener, view, prefConfig, mContext, this, categoryCallback, this, type);

                ViewTreeObserver viewTreeObserverYoutube = youtubeViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserverYoutube.isAlive()) {
                    viewTreeObserverYoutube.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            youtubeViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = youtubeViewHolder.itemView.getHeight();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }

                return youtubeViewHolder;
            case TYPE_REEL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_reel_card, parent, false);
                ReelViewHolder reelViewHolder = new ReelViewHolder(timerFinishListener, view, prefConfig, mContext, this, categoryCallback, detailsActivityInterface, this, type);

                ViewTreeObserver viewTreeObserverReel = reelViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserverReel.isAlive()) {
                    viewTreeObserverReel.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            reelViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = reelViewHolder.itemView.getHeight();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }

                return reelViewHolder;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_large_card, parent, false);
                ArticleViewHolder articleViewHolder = new ArticleViewHolder(false,timerFinishListener, view, prefConfig, mContext, this, categoryCallback, detailsActivityInterface, this, type);

                ViewTreeObserver viewTreeObserver = articleViewHolder.itemView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            articleViewHolder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewHeight = articleViewHolder.itemView.getHeight();
                            if (categoryCallback != null)
                                categoryCallback.onItemHeightMeasured(viewHeight);
                        }
                    });
                }
                return articleViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        Article article = mPostsList.get(position);
        if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).bind(position, article);
        } else if (holder instanceof YoutubeViewHolder) {
            ((YoutubeViewHolder) holder).bind(position, article);
        } else if (holder instanceof ReelViewHolder) {
            ((ReelViewHolder) holder).bind(position, article);
        } else if (holder instanceof ArticleViewHolder) {
            ((ArticleViewHolder) holder).bind(position, article);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull @NotNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).destroy();
        } else if (holder instanceof YoutubeViewHolder) {
            ((YoutubeViewHolder) holder).destroy();
        } else if (holder instanceof ReelViewHolder) {
            ((ReelViewHolder) holder).destroy();
        } else if (holder instanceof ArticleViewHolder) {
            ((ArticleViewHolder) holder).destroy();
        }
    }

    @Override
    public int getItemViewType(int position) {
        Article article = mPostsList.get(position);
        if (article != null) {
            if (article.getType().equalsIgnoreCase("IMAGE")) {
                return TYPE_ARTICLE;
            } else if (article.getType().equalsIgnoreCase("YOUTUBE")) {
                return TYPE_YOUTUBE;
            } else if (article.getType().equalsIgnoreCase("VIDEO")) {
                return TYPE_VIDEO;
            } else if (article.getType().equalsIgnoreCase("REEL")) {
                return TYPE_REEL;
            } else {
                return TYPE_ARTICLE;
            }
        }
        return TYPE_ARTICLE;
    }

    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    @Override
    public int getArticlePosition() {
        return currentArticlePosition;
    }

    public void setCurrentArticlePosition(int position) {
        this.currentArticlePosition = position;
    }

    @Override
    public void showShareBottomSheet(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {

    }

    @Override
    public void onItemClick(int position, boolean setCurrentView) {
        Log.e("onItemClick", "onItemClick : " + position);
        if (position > -1) {
            int temp = position;

            for (int i = 4; i >= 0; i--) {
                //previous position article
                temp--;
                resetArticle(temp, false, setCurrentView);
            }
            temp = position;
            //current position article
            resetArticle(temp, true, setCurrentView);

            //next position article
            temp++;
            resetArticle(temp, false, setCurrentView);
        }
    }

    private void resetArticle(int position, boolean flag, boolean youtube) {
        if (position > -1 && mPostsList != null && position < mPostsList.size()) {
            mPostsList.get(position).setYoutubePlaying(flag);
            mPostsList.get(position).setSelected(flag);
            if (!flag)
                notifyItemChanged(position);
        }
    }

    @Override
    public void onPost(int position) {
        notifyDataSetChanged();
        Article article = mPostsList.get(position);
        sharePresenter.changeArticleStatus(article.getId(), "PUBLISHED", new SharePresenter.SchedulePresenterCallback() {
            @Override
            public void loading(boolean flag) {
                showLoadingView(flag);
            }

            @Override
            public void success() {
                mPostsList.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void error(String error) {
                Utils.showSnacky(mContext.getWindow().getDecorView().getRootView(), "" + error);
            }
        });
    }

    @Override
    public void onEdit(int position) {
        Article article = mPostsList.get(position);
        if (article != null) {
            Intent intent = new Intent(mContext, PostArticleActivity.class);
            switch (article.getType()) {
                case "IMAGE":
                    intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
                    break;
                case "VIDEO":
                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
                    break;
                case "YOUTUBE":
                    intent.putExtra("POST_TYPE", POST_TYPE.YOUTUBE);
                    break;
                case "REEL":
                    intent.putExtra("POST_TYPE", POST_TYPE.REELS);
                    break;
            }
            intent.putExtra("MODE", MODE.EDIT);

            if (type == TYPE.SCHEDULE)
                intent.putExtra("SCHEDULE", article.getPublishTime());
            intent.putExtra("article", new Gson().toJson(article));
            ((Activity) mContext).startActivityForResult(intent, 123);
//        if (type.equalsIgnoreCase("MY_ARTICLES_DETAILS"))
//            ((Activity) context).finish();
        }
    }

    @Override
    public void onDelete(int position) {
        notifyDataSetChanged();
        if (position < mPostsList.size()) {
            Article article = mPostsList.get(position);
            sharePresenter.changeArticleStatus(article.getId(), "UNPUBLISHED", new SharePresenter.SchedulePresenterCallback() {
                @Override
                public void loading(boolean flag) {
                    showLoadingView(flag);
                }

                @Override
                public void success() {
                    mPostsList.remove(position);
                    notifyDataSetChanged();
                }

                @Override
                public void error(String error) {
                    Utils.showSnacky(mContext.getWindow().getDecorView().getRootView(), "" + error);
                }
            });
        }
    }

    private void showLoadingView(boolean isShow) {
        if (mContext.isFinishing()) {
            return;
        }
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(mContext);
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null
                    && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    public enum TYPE {
        SCHEDULE, DRAFT, PREVIEW
    }

}


