package com.ziro.bullet.activities;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.CustomDiscoverListAdapter;
import com.ziro.bullet.adapters.CustomDiscoverTCPListAdapter;
import com.ziro.bullet.adapters.discover.ListSmallCardViewHolder;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TCPInterface;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.model.TCP.TCP;
import com.ziro.bullet.model.TCP.TCPResponse;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.presenter.TCPPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.PaginationScrollListener;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CustomDiscoverListActivity extends BaseActivity implements NewsCallback, AdapterCallback, TCPInterface {

    private RelativeLayout close;
    private RecyclerView mListRV;
    private NewsPresenter presenter;
    private PrefConfig prefConfig;
    private DiscoverItem discoverItem;
    private ArrayList<Article> articles = new ArrayList<>();
    private ArrayList<TCP> tcpModel = new ArrayList<>();
    private CustomDiscoverListAdapter adapter;
    private CustomDiscoverTCPListAdapter tcpAdapter;
    private boolean isLoading = false;
    private String nextPage = PAGE_START;
    private boolean isLastPage = false;
    private String context = null;
    private String type = null;
    private ShareBottomSheet shareBottomSheet;
    private TCPPresenter tcpPresenter;

    private TextView title;
    private TextView subtitle;

    private ViewSwitcher loaderSwitcher;
    private RecyclerView image_list;
    // private NestedScrollView scroll_view;

    private Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        findViewById(android.R.id.content).setTransitionName("shared_element_container");
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());

        MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
        materialContainerTransform.addTarget(android.R.id.content);
        materialContainerTransform.setDuration(550L);

        getWindow().setSharedElementEnterTransition(materialContainerTransform);

        MaterialContainerTransform materialContainerTransform1 = new MaterialContainerTransform();
        materialContainerTransform1.addTarget(android.R.id.content);
        materialContainerTransform1.setDuration(550L);

        getWindow().setSharedElementReturnTransition(materialContainerTransform1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_discover_list);


        loadIntentData();
        init();
        bindViews();

        final View decor = getWindow().getDecorView();
        decor.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                startPostponedEnterTransition();
            }
        });

        nextPage = "";
        getBundles();

//        getBundles();
        setStatusBarColor();
    }

    public void timerPause() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
        }
    }

    public void timerResume() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
        }
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (image_list != null) {
                    image_list.post(new Runnable() {
                        @Override
                        public void run() {
                            image_list.scrollBy(1, 0);
                        }
                    });
                }
            }
        }, 0, 50);
    }

    private void loadIntentData() {
        discoverItem = new Gson().fromJson(getIntent().getStringExtra("data"), DiscoverItem.class);
        context = getIntent().getStringExtra("context");
        type = getIntent().getStringExtra("type");
    }

    private void init() {
        tcpPresenter = new TCPPresenter(this, this);
        presenter = new NewsPresenter(this, this);
        prefConfig = new PrefConfig(this);
    }

    private void bindViews() {
        mListRV = findViewById(R.id.list);
        close = findViewById(R.id.close);
        close.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "");
            setResult(Activity.RESULT_OK, returnIntent);
            finishAfterTransition();
        });

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);

        loaderSwitcher = findViewById(R.id.loader_switcher);
        image_list = findViewById(R.id.image_list);
    }

    private void showSkeletonLoader(boolean show) {
        //Utils.loadSkeletonLoader(loaderSwitcher, show);
    }

    @Override
    protected void onResume() {
        super.onResume();

        timerResume();

        if (!TextUtils.isEmpty(type)) {
            nextPage = "";
            if (type.equalsIgnoreCase("IMAGE_ARTICLES")) {
                presenter.updateNews(context, prefConfig.isReaderMode(), nextPage);
            } else {
                //For Topics, Channels, Locations
                tcpPresenter.tcpData(context, type, nextPage);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerPause();
    }

    public void setStatusBarColor() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        Window window = getWindow();

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.discover_card_bg));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.discover_card_bg));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (presenter != null && data != null && data.hasExtra("id")) {
            String id = data.getStringExtra("id");
            int position = data.getIntExtra("position", -1);
            if (!TextUtils.isEmpty(id) && position != -1) {
                presenter.counters(id, info -> {
                    try {
                        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);
                        if (holder != null && info != null) {
                            if (holder instanceof ListSmallCardViewHolder) {
                                Utils.checkLikeView(this,
                                        info.getLike_count(),
                                        info.getComment_count(),
                                        ((ListSmallCardViewHolder) holder).comment_count,
                                        ((ListSmallCardViewHolder) holder).like_count,
                                        ((ListSmallCardViewHolder) holder).like_icon,
                                        info.isLiked()
                                );
                            }
                            if (articles != null && articles.size() > 0 && position < articles.size()) {
                                articles.get(position).setInfo(info);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void getBundles() {
        if (getIntent() != null) {
            articles.add(new Article("", "HEADER"));
            articles.add(new Article("", "LOADER"));
            //setData(discoverItem);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            if (!TextUtils.isEmpty(type)) {
                if (type.equalsIgnoreCase("IMAGE_ARTICLES")) {
                    adapter = new CustomDiscoverListAdapter(new CommentClick() {

                        @Override
                        public void onDetailClick(int position, Article article) {
                            Intent intent = new Intent(CustomDiscoverListActivity.this, BulletDetailActivity.class);
                            intent.putExtra("article", new Gson().toJson(article));
                            intent.putExtra("type", type);
                            intent.putExtra("position", position);
                            startActivityForResult(intent, Constants.CommentsRequestCode);
                        }

                        @Override
                        public void onNewDetailClick(int position, Article article, List<Article> articlelist) {

                        }

                        @Override
                        public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {

                        }

                        @Override
                        public void commentClick(int position, String id) {
                            Intent intent = new Intent(CustomDiscoverListActivity.this, CommentsActivity.class);
                            intent.putExtra("article_id", id);
                            intent.putExtra("position", position);
                            startActivityForResult(intent, Constants.CommentsRequestCode);
                        }
                    }, this, articles, this, discoverItem,
                            new ShowOptionsLoaderCallback() {
                                @Override
                                public void showLoader(boolean show) {
                                    loaderShow(show);
                                }
                            }
                    );
                    mListRV.setAdapter(adapter);
                    isLoading = true;
//                    presenter.updateNews(context, prefConfig.isReaderMode(), nextPage);
                } else {
                    tcpModel.add(new TCP("HEADER"));
                    tcpModel.add(new TCP("LOADER"));
                    tcpAdapter = new CustomDiscoverTCPListAdapter(type, this, tcpModel, discoverItem);
                    mListRV.setAdapter(tcpAdapter);
                    //For Topics, Channels, Locations
//                    tcpPresenter.tcpData(context, type, nextPage);
                }
            }
            setPagination(mListRV, manager);
        }
    }

    private void setPagination(RecyclerView mRvRecyclerView, LinearLayoutManager manager) {

        mListRV.setNestedScrollingEnabled(false);

        mListRV.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                if (!TextUtils.isEmpty(context)) {
                    if (!TextUtils.isEmpty(type)) {
                        if (type.equalsIgnoreCase("IMAGE_ARTICLES")) {
                            isLoading = true;
                            presenter.updateNews(context, prefConfig.isReaderMode(), nextPage);
                        } else {
                            isLoading = true;
                            //For Topics, Channels, Locations
                            tcpPresenter.tcpData(context, type, nextPage);
                        }
                    }
                }
            }

            @Override
            protected void onScroll() {

            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        mRvRecyclerView.setLayoutManager(manager);
    }

    @Override
    public void loaderShow(boolean flag) {
        // isLoading = true;
        if (nextPage.equals("")) {
            showSkeletonLoader(flag);
        }
    }

    @Override
    public void onTCPSuccess(String type, TCPResponse response) {
        if (TextUtils.isEmpty(nextPage)) {
            this.tcpModel.clear();
            tcpModel.add(new TCP("HEADER"));
            tcpModel.add(new TCP("LOADER"));
        }
        isLoading = false;
        if (response.getMeta() != null) {
            nextPage = response.getMeta().getNext();
            isLastPage = TextUtils.isEmpty(nextPage);
        }

        if (tcpModel.size() <= 2) {
            if (tcpModel.size() > 1)
                tcpModel.remove(1);
            if (response.getLocations() != null && response.getLocations().size() > 0) {
                this.tcpModel.addAll(response.getLocations());
            } else if (response.getSources() != null && response.getSources().size() > 0) {
                this.tcpModel.addAll(response.getSources());
            } else if (response.getTopics() != null && response.getTopics().size() > 0) {
                this.tcpModel.addAll(response.getTopics());
            }
            if (tcpAdapter != null) {
                if (tcpModel.size() > 0) {
                    tcpAdapter.notifyItemRangeChanged(1, tcpModel.size() - 1);
                } else {
                    tcpAdapter.notifyItemRemoved(1);
                }
            }
        }
    }

    @Override
    public void onTCPSuccessPagination(String type, TCPResponse response) {
        isLoading = false;
        if (response.getMeta() != null) {
            nextPage = response.getMeta().getNext();
            isLastPage = TextUtils.isEmpty(nextPage);
        }

        if (response.getLocations() != null && response.getLocations().size() > 0) {
            this.tcpModel.addAll(response.getLocations());
        } else if (response.getSources() != null && response.getSources().size() > 0) {
            this.tcpModel.addAll(response.getSources());
        } else if (response.getTopics() != null && response.getTopics().size() > 0) {
            this.tcpModel.addAll(response.getTopics());
        }

        if (tcpAdapter != null)
            tcpAdapter.notifyDataSetChanged();
    }

    @Override
    public void error(String error) {
        isLoading = false;
    }

    @Override
    public void error404(String error) {
        isLoading = false;
    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {
        boolean isFirstPage = nextPage == null || nextPage.equals("");
        isLoading = false;
        if (response.getMeta() != null) {
            nextPage = response.getMeta().getNext();
            if (TextUtils.isEmpty(nextPage)) {
                isLastPage = true;
            } else {
                isLastPage = false;
            }
        }
        if (response.getArticles() != null && response.getArticles().size() > 0) {
            if (isFirstPage) {
                this.articles.remove(1);
            }
            this.articles.addAll(response.getArticles());
            if (adapter != null) {
                if (isFirstPage) {
                    adapter.notifyItemRangeChanged(1, articles.size() - 1);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void successArticle(Article article) {
        isLoading = false;
    }

    @Override
    public void homeSuccess(HomeResponse homeResponse, String currentPage) {

    }

    @Override
    public void nextPosition(int position) {

    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {

    }

    @Override
    public void onItemHeightMeasured(int height) {

    }

    @Override
    public int getArticlePosition() {
        return 0;
    }

    @Override
    public void showShareBottomSheet(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        showBottomSheetDialog(shareInfo, article, onDismissListener);
    }

    @Override
    public void onItemClick(int position, boolean setCurrentView) {

    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(this, null, false, null);
        }
        shareBottomSheet.show(article, onDismissListener, shareInfo);
    }
}