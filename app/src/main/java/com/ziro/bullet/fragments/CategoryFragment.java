package com.ziro.bullet.fragments;

import static android.app.Activity.RESULT_OK;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.utills.Constants.isApiCalling;
import static java.lang.Math.abs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.Helper.PopUpClass;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.PostArticleActivity;
import com.ziro.bullet.activities.YoutubeFullScreenActivity;
import com.ziro.bullet.activities.articledetail.ArticleDetailNew;
import com.ziro.bullet.adapters.NewFeed.HomeAdapter;
import com.ziro.bullet.adapters.NewFeed.YoutubeViewHolderEdge;
import com.ziro.bullet.adapters.NewFeed.newHomeArticle.HomeArticlesAdapterNew;
import com.ziro.bullet.adapters.discover.DiscoverReelViewHolder;
import com.ziro.bullet.adapters.discover.DiscoverVideoViewHolder;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.data.models.NewFeed.SectionsItem;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.CommunityItemCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.HomeLoaderCallback;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.ImageCrop;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.CategoryFragmentModel;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.Tabs.SubItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.ene.toro.PlayerSelector;

public class CategoryFragment extends Fragment implements NewsCallback, ShareToMainInterface, ListAdapterListener {
    public static final int PERMISSION_REQUEST_NEW_REELS = 345;
    public static final int PERMISSION_REQUEST_NEW_MEDIA = 346;
    public static final int PERMISSION_REQUEST_REELS = 745;
    public static final int INTENT_FOLLOWING = 4324;
    private static final String TAG = "TempCategoryFrag_TAG";

    private static final String FOLLOWING = "FOLLOWING";
    private static final String NEW_POST = "NEW_POST";
    private static final String CHANNELS = "CHANNELS";
    private static final String TOPICS = "TOPICS";
    private static final String ADS = "ADS";
    private static final String REELS = "REELS";
    private static final String ARTICLE_LIST = "ARTICLE_LIST";
    private static final String ARTICLE_HORIZONTAL = "ARTICLE_HORIZONTAL";
    private static final String ARTICLE_HERO = "ARTICLE_HERO";
    private static final String LARGE_REELS = "LARGE_REELS";
    private static final String ARTICLE_VIDEOS = "ARTICLE_VIDEOS";
    private static final String LINE = "LINE";
    private static boolean isHome;
    private static GoHome goHomeMainActivity;
    private static GoHome goHomeTempHome;
    private static HomeLoaderCallback mLoaderCallback;
    final float MINIMUM_SCROLL_DIST = 2;
    int scrollDist = 0;
    boolean isVisible = true;
    //    private SimpleOrientationListener mOrientationListener;
    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private String type = "";
    private PrefConfig prefConfig;
    private boolean refreshLayout = false;
    private boolean isGotoFollowShow;
    private String mTopic = "", mContextId = "", mTitle = "", from = "";
    private String mNextPage = "", mCurrPage = "1";
    private String _myTag;
    private ArrayList<Article> contentArrayList = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private CardView back;
    private List<Article> articlelistNew;
    private LinearLayout noRecordFoundContainer;
    private RecyclerView mListRV;
    //    private Container mListRV;
    //    private RelativeLayout no_record_found;
    private CardView new_post;
    //    private RelativeLayout no_saved_articles;
    private RelativeLayout rlProgress;
    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private HomeAdapter mCardAdapter;
    private HomeArticlesAdapterNew homeArticlesAdapterNew;
    private TempCategorySwipeListener swipeListener;
    private OnGotoChannelListener listener;
    private NewsPresenter presenter;
    private int mArticlePosition = 0;
    private boolean fragmentVisible = false;
    private boolean adFailedStatus;
    private PictureLoadingDialog mLoadingDialog;
    private DbHandler cacheManager;
    private HomeResponse cacheArticles;
    private boolean isPagination = false;
    //    private ArrayList<Article> newData = new ArrayList<>();
    private boolean isCreatePostExpanded = false;
    private boolean menuVisible;
    private boolean isHidden;
    private boolean fragmentResumed;
    private TabLayout tabs;
    private View subMenuLine;
    private CategoryFragmentModel categoryFragmentModel;
    private String lastModifiedTime;
    private boolean isCommunity;
    private boolean isDark;

    public static CategoryFragment newInstance(String from, boolean isDark, String title, String topicId, String sourceId, String headLineId,
                                               String articleId, String locationId, String contextId, String type1, boolean specificSourceTopic1,
                                               boolean isHome1, GoHome goHomeMainActivity1, GoHome goHomeTempHomeListener,
                                               HomeLoaderCallback loaderCallback, CategoryFragmentModel categoryFragmentModel) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDark", isDark);
        bundle.putString("from", from);
        if (!TextUtils.isEmpty(title))
            bundle.putString("title", title);
        if (!TextUtils.isEmpty(topicId))
            bundle.putString("topic", topicId);
        if (!TextUtils.isEmpty(sourceId))
            bundle.putString("source", sourceId);
        if (!TextUtils.isEmpty(headLineId))
            bundle.putString("headline", headLineId);
        if (!TextUtils.isEmpty(articleId))
            bundle.putString("article", articleId);
        if (!TextUtils.isEmpty(locationId))
            bundle.putString("location", locationId);
        if (!TextUtils.isEmpty(contextId)) {
            bundle.putString("context", contextId);
        }
        if (!TextUtils.isEmpty(type1))
            bundle.putString("type", type1);

        bundle.putParcelable("categoryFragmentModel", categoryFragmentModel);

        bundle.putString("TAG", topicId);
        isHome = isHome1;
        goHomeMainActivity = goHomeMainActivity1;
        goHomeTempHome = goHomeTempHomeListener;
        mLoaderCallback = loaderCallback;
        categoryFragment.setArguments(bundle);
        return categoryFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // bottom bar control vars reset
//        scrollDist = 0;
//        isVisible = true;
        fragmentVisible = true;
        fragmentResumed = true;
//        updateMuteButton();
    }

    private void updateMuteButton() {
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof VideoViewHolder) {
                    ((VideoViewHolder) holder).updateMuteButtons();
                } else if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).updateMuteButtons();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //Added this in case of opening article from widgets, if u go to background and open another article then headline only breaking.
        Constants.auto_scroll = true;
        //Audio overlapping issue from widgets only,
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", "onStop : stop_destroy");
        if (fragmentVisible)
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
    }

    public String getMyTag() {
        return _myTag;
    }

    public void setMyTag(String value) {
        if ("".equals(value))
            return;
        _myTag = value;
    }

    public void setGotoChannelListener(TempCategorySwipeListener swipeListener, OnGotoChannelListener listener, boolean isGotoFollowShow) {
        this.swipeListener = swipeListener;
        this.listener = listener;
        this.isGotoFollowShow = isGotoFollowShow;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefConfig = new PrefConfig(getContext());
        cacheManager = new DbHandler(getContext());
        lastModifiedTime = cacheManager.getHomeLastModifiedTimeById(mContextId);

        if (getArguments() != null && getArguments().containsKey("from")) {
            from = getArguments().getString("from");
        }

        if (getArguments() != null && getArguments().containsKey("title")) {
            mTitle = getArguments().getString("title");
        }

        if (getArguments() != null && getArguments().containsKey("topic")) {
            mContextId = getArguments().getString("topic");
        }

        if (getArguments() != null && getArguments().containsKey("source")) {
            mContextId = getArguments().getString("source");
        }

        if (getArguments() != null && getArguments().containsKey("headline")) {
            mContextId = getArguments().getString("headline");
        }

        if (getArguments() != null && getArguments().containsKey("article")) {
            mTopic = getArguments().getString("article");
        }

        if (getArguments() != null && getArguments().containsKey("location")) {
            mContextId = getArguments().getString("location");
        }
        if (getArguments() != null && getArguments().containsKey("context")) {
            mContextId = getArguments().getString("context");
        }
        if (getArguments() != null && getArguments().containsKey("type")) {
            type = getArguments().getString("type");
        }
        if (getArguments() != null && getArguments().containsKey("categoryFragmentModel")) {
            categoryFragmentModel = getArguments().getParcelable("categoryFragmentModel");
        }
        if (getArguments() != null && getArguments().containsKey("isDark")) {
            isDark = getArguments().getBoolean("isDark");
        }

        Log.e(TAG, "onCreateView() mContextId :: " + mContextId + "  ===  " + this.getMyTag());

        return inflater.inflate(R.layout.layout_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refresh = view.findViewById(R.id.refresh);
        back = view.findViewById(R.id.back);
        mListRV = view.findViewById(R.id.recyclerview);
        noRecordFoundContainer = view.findViewById(R.id.ll_no_results);
        rlProgress = view.findViewById(R.id.progress);
        tabs = view.findViewById(R.id.sub_menu_tabs);
        subMenuLine = view.findViewById(R.id.sub_menu_line);
        new_post = view.findViewById(R.id.new_post);
        presenter = new NewsPresenter(getActivity(), this);

        initAdapter();
        setRvScrollListener();

        if (!isHome) {
            back.setVisibility(View.VISIBLE);
        } else {
            if (contentArrayList != null && contentArrayList.size() > 0 && contentArrayList.get(0) != null) {
                if (contentArrayList.get(0).getId().equalsIgnoreCase("")) {
                    //All Tab
                    back.setVisibility(View.GONE);
                }
            }
        }

        back.setOnClickListener(v -> {
            back.setEnabled(false);
//            if (goHomeMainActivity != null) {
//                goHomeMainActivity.sendAudioEvent("homeTab");
//            }
            if (goHomeTempHome != null) {
                goHomeTempHome.home();
            }
            new Handler().postDelayed(() -> {
                back.setEnabled(true);
            }, 1000);
        });
        refresh.setOnRefreshListener(this::refreshCategory);

        if (mListRV != null)
            mListRV.scrollToPosition(0);

        setUpSubMenuTabs();

        mNextPage = "";
        mCurrPage = "1";
        refreshLayout = true;
        loadCacheData();
    }

    private void setUpSubMenuTabs() {
        if (categoryFragmentModel != null && categoryFragmentModel.getSubItems() != null && categoryFragmentModel.getSubItems().size() > 0) {
            for (int i = 0; i < categoryFragmentModel.getSubItems().size(); i++) {
                SubItem subItem = categoryFragmentModel.getSubItems().get(i);
                TabLayout.Tab tab = tabs.newTab();
                tab.setText(subItem.getTitle());
                tab.setTag(subItem.getId());
                tabs.addTab(tab);
            }
            tabs.setVisibility(View.VISIBLE);
            subMenuLine.setVisibility(View.VISIBLE);
        } else {
            tabs.setVisibility(View.GONE);
            subMenuLine.setVisibility(View.GONE);
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mNextPage = "";
                mCurrPage = "1";
                refreshLayout = true;
                mContextId = (String) tab.getTag();
                loadCacheData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadCacheData() {
        Log.d(TAG, "loadCacheData() called " + this._myTag + "  :Context : " + mContextId);
        if (!TextUtils.isEmpty(mContextId)) {
            cacheArticles = cacheManager.GetHomeRecordById(mContextId);
            if (cacheArticles != null && cacheArticles.getSections() != null && cacheArticles.getSections().size() > 0) {
                loadCacheCategories(cacheArticles);
            } else {
                callData(type);
            }
        }
        callData(type);
    }

    public void refreshCategory() {
        if (mArticlePosition != 0) {
            scrollToTop();
        }
//        new_post.setVisibility(View.GONE);
        refresh.setRefreshing(true);
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("pause");
        mArticlePosition = 0;
        mCurrPage = "1";
        mNextPage = "";
        refreshLayout = true;
        callData(type);
    }

    public void categoryFirstCallAfterGettingNetwork() {
        refresh.setRefreshing(true);
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("pause");
        mArticlePosition = 0;
        mCurrPage = "1";
        mNextPage = "";
        callData(type);
    }

    public int getCategoryDataCount() {
        if (contentArrayList == null) return 0;
        return contentArrayList.size();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_NEW_REELS) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Reels();
            }
        }
        if (requestCode == PERMISSION_REQUEST_NEW_MEDIA) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Media();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode = " + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.CommentsRequestCode) {
                if (data != null) {
                    if (presenter != null && data.hasExtra("id")) {
                        String id = data.getStringExtra("id");
                        int position = data.getIntExtra("position", -1);

                        Log.d(TAG, "onActivityResult: " + mCardAdapter);
                        Log.d(TAG, "onActivityResult: " + mListRV);
                        Log.d(TAG, "onActivityResult: " + mListRV.findViewHolderForAdapterPosition(position));
                        if (!TextUtils.isEmpty(id) && position != -1) {
                            presenter.counters(id, info -> {
                                try {
                                    RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);
                                    if (holder != null && info != null) {
                                        if (holder instanceof LargeCardViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((LargeCardViewHolder) holder).comment_count,
                                                    ((LargeCardViewHolder) holder).like_count,
                                                    ((LargeCardViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );
                                        } else if (holder instanceof SmallCardViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((SmallCardViewHolder) holder).comment_count,
                                                    ((SmallCardViewHolder) holder).like_count,
                                                    ((SmallCardViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );
                                        } else if (holder instanceof YoutubeViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((YoutubeViewHolder) holder).comment_count,
                                                    ((YoutubeViewHolder) holder).like_count,
                                                    ((YoutubeViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );
                                        } else if (holder instanceof VideoViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((VideoViewHolder) holder).comment_count,
                                                    ((VideoViewHolder) holder).like_count,
                                                    ((VideoViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );
                                        }
                                    }

                                    if (contentArrayList != null && contentArrayList.size() > 0 && position < contentArrayList.size()) {
                                        contentArrayList.get(position).setInfo(info);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            } else if (requestCode == Constants.VideoDurationRequestCode) {
                if (data != null) {
                    int position = data.getIntExtra("position", -1);
                    long duration = data.getLongExtra("duration", 0);
                    if (position != -1 && duration > 0) {
                        try {
                            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);
                            if (holder != null) {
                                if (holder instanceof YoutubeViewHolder) {
                                    ((YoutubeViewHolder) holder).seekTo(duration);
                                } else if (holder instanceof VideoViewHolder) {
                                    ((VideoViewHolder) holder).seekTo(duration);
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
                        }
                    }
                }
            } else if (requestCode == PERMISSION_REQUEST_REELS) {
                if (data != null) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList.size() == 0) {
                        VideoInfo videoInfo = data.getParcelableExtra("video_info");

                        if (getActivity() == null || getActivity().isFinishing()) return;
                        Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                        intent.putExtra("POST_TYPE", POST_TYPE.REELS);
                        intent.putExtra("MODE", MODE.ADD);
                        intent.putExtra("file", new File(videoInfo.getPath()));
                        intent.putExtra("uri", videoInfo.getPath());
                        intent.putExtra("video_info", videoInfo);
                        getActivity().startActivity(intent);
                    } else
                        loadSelectedVideo(selectList.get(0));
                }
            } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                if (data != null) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList.size() == 0) {
                        VideoInfo videoInfo = data.getParcelableExtra("video_info");

                        if (getActivity() == null || getActivity().isFinishing()) return;
                        Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                        intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
                        intent.putExtra("MODE", MODE.ADD);
                        intent.putExtra("file", new File(videoInfo.getPath()));
                        intent.putExtra("uri", videoInfo.getPath());
                        intent.putExtra("video_info", videoInfo);
                        getActivity().startActivity(intent);
                    } else {
                        loadSelectedImage(selectList.get(0));
                    }
                }
            } else if (requestCode == ImageCrop.REQUEST_CROP) {
                if (data != null) {
                    if (getActivity() == null) return;
                    Uri resultUri = ImageCrop.getOutput(data);
                    String cutPath = resultUri.getPath();

                    Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
                    intent.putExtra("MODE", MODE.ADD);
                    intent.putExtra("file", new File(cutPath));
                    intent.putExtra("uri", cutPath);
                    getActivity().startActivity(intent);
                }
            } else if (requestCode == INTENT_FOLLOWING) {
                reload();
            }
        }
    }


    private void loadSelectedVideo(LocalMedia localMedia) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), PostArticleActivity.class);
        intent.putExtra("POST_TYPE", POST_TYPE.REELS);
        intent.putExtra("MODE", MODE.ADD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
            intent.putExtra("uri", localMedia.getAndroidQToPath());
        } else {
            intent.putExtra("file", new File(String.valueOf(localMedia.getPath())));
            intent.putExtra("uri", localMedia.getPath());
        }
        intent.putExtra("localMedia", new Gson().toJson(localMedia));
        getActivity().startActivity(intent);
    }

    private void loadSelectedImage(LocalMedia localMedia) {
        if (!localMedia.getMimeType().contains("video")) {
//            showProgressDialog();
            File f = new File(getContext().getCacheDir(), localMedia.getFileName() + System.currentTimeMillis());
            try {
                f.createNewFile();
                Uri url;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    url = Uri.fromFile(new File((localMedia.getAndroidQToPath())));
                } else {
                    url = Uri.fromFile(new File((localMedia.getPath())));
                }

                ImageCrop.of(
                                url,
                                Uri.fromFile(f)
                        )
                        .start(getContext(), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (getActivity() == null) return;
            Intent intent = new Intent(getActivity(), PostArticleActivity.class);
            if (localMedia.getMimeType().contains("video")) {
                intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
            } else {
                intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
            }
            intent.putExtra("MODE", MODE.ADD);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
                intent.putExtra("uri", localMedia.getAndroidQToPath());
            } else {
                intent.putExtra("file", new File(String.valueOf(localMedia.getPath())));
                intent.putExtra("uri", localMedia.getPath());
            }
            intent.putExtra("localMedia", new Gson().toJson(localMedia));
            getActivity().startActivity(intent);
        }

    }

    private void callData(String type) {
        Log.e("callData", "type : " + type);
        if (type != null) {
            switch (type) {
                case "ARCHIVE":
                    presenter.archiveArticles(mNextPage);
                    Log.e("callData", "ARCHIVE");
                    break;
                case "SOURCE_PUSH":
                    //this is article ID in mTopic
                    presenter.loadSingleArticle(mTopic);
                    Log.e("callData", "SOURCE_PUSH");
                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("home");
                    break;
                case "TOPIC":
                    presenter.updatedArticles(mContextId, prefConfig.isReaderMode(), mNextPage, true);
                    Log.e("callData", "TOPIC ::>> " + mContextId);
                    break;
                case "SOURCE":
                    presenter.updatedArticles(mContextId, prefConfig.isReaderMode(), mNextPage, true);
                    Log.e("callData", "SOURCE");
                    break;
                default:
                    if (!TextUtils.isEmpty(mContextId)) {
                        presenter.updatedArticles(mContextId, prefConfig.isReaderMode(), mNextPage, true);
                    }
                    Log.e("callData", "default");
            }
        } else {
            if (!TextUtils.isEmpty(mContextId)) {
                presenter.updatedArticles(mContextId, prefConfig.isReaderMode(), mNextPage, true);
            }
            Log.e("callData", "else");
        }
    }

    public void selectFirstItemOnCardViewMode() {
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            if (isCommunity) {
                mArticlePosition = 1;
            }
            selectCardPosition(mArticlePosition);
            if (mCardAdapter != null) {
                Log.e("@@##@@##", "notifyDataSetChanged 744");
                mCardAdapter.notifyDataSetChanged();
            }
        }
    }

    public void selectCardPosition(int position) {
        if (position > -1) {
            mArticlePosition = position;
            if (mCardAdapter != null)
                mCardAdapter.setCurrentArticlePosition(mArticlePosition);
            if (contentArrayList.size() > 0 && position < contentArrayList.size()) {
                for (int i = 0; i < contentArrayList.size(); i++) {
                    contentArrayList.get(i).setSelected(false);
                }
                Log.e("isAudioLoaded", "selectCardPosition : " + position + " TAG : " + this.getMyTag());
                contentArrayList.get(position).setSelected(true);
            }
        }
    }

    public void unSelectAllItems() {
        if (!TextUtils.isEmpty(mContextId)) {
            if (contentArrayList.size() > 0) {
                for (int i = 0; i < contentArrayList.size(); i++) {
                    contentArrayList.get(i).setSelected(false);
                }
                if (mCardAdapter != null) {
                    Log.e("@@##@@##", "notifyDataSetChanged 772");
                    mCardAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    private void addItemSeparators() {
        int[] ATTRS = new int[]{android.R.attr.listDivider};

        TypedArray a = getContext().obtainStyledAttributes(ATTRS);
        Drawable divider = a.getDrawable(0);

        int inset = getResources().getDimensionPixelSize(R.dimen._15sdp);

        InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, inset, 0);
        a.recycle();

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(insetDivider);
        mListRV.addItemDecoration(itemDecoration);
    }

    private void setRvScrollListener() {
        mListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == SCROLL_STATE_IDLE) {
                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
                            Events.ARTICLE_SWIPE);
                    Constants.auto_scroll = true;

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());

                    int firstVisibleChild = cardLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int lastVisibleChild = cardLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                    long totalVisibleChild = Math.max(0, (lastVisibleChild - firstVisibleChild));
                    if (totalVisibleChild > 0 && totalVisibleChild < contentArrayList.size()) {
                        for (int i = 0; i <= totalVisibleChild; i++) {
                            Article article = contentArrayList.get(firstVisibleChild + i);
                            if (article.getType() != null && (article.getType().equals("EXTENDED") || article.getType().equals("SIMPLE"))) {
                                AnalyticsEvents.INSTANCE.articleViewEvent(requireContext(), article.getId());
                                Log.e(TAG, "onScrollStateChanged: here");
                                Glide.with(requireContext())
                                        .load(article.getImage())
                                        .encodeQuality(50)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .priority(Priority.IMMEDIATE)
                                        .preload();
                            }
                        }
                    }

                    if (!fragmentVisible)
                        return;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                Log.d(TAG, "onScrolled: DyValue: " + dy);


                if (contentArrayList.size() - 20 <= cardLinearLayoutManager.findLastVisibleItemPosition() && !isLast()) {
                    if (!isApiCalling) {
                        isApiCalling = true;
                        callNextPage();
                    }
                }


                if (goHomeTempHome != null && goHomeMainActivity != null) {
                    if (dy > 0) {
                        goHomeTempHome.scrollUp();
//                        goHomeMainActivity.scrollUp();
                    } else {
                        goHomeTempHome.scrollDown();
//                        goHomeMainActivity.scrollDown();
                    }
                }

                // hide bottom bar while scrolling
                if (goHomeMainActivity != null) {
                    if (isVisible && scrollDist > MINIMUM_SCROLL_DIST) {
                        goHomeMainActivity.scrollUp();
                        scrollDist = 0;
                        isVisible = false;
                    } else if (!isVisible && scrollDist < -MINIMUM_SCROLL_DIST) {
                        goHomeMainActivity.scrollDown();
                        scrollDist = 0;
                        isVisible = true;
                    }

                    if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                        scrollDist += dy;
                    }
                }

            }
        });
    }

    private void setListRecyclerListeners() {

        mListRV.setOnFlingListener(new RecyclerView.OnFlingListener() {

            @Override
            public boolean onFling(int velocityX, int velocityY) {
                Log.d(TAG, "onFling: " + abs(velocityY));

                if (abs(velocityY) > 15000) {
                    velocityY = 6400 * (int) Math.signum((double) velocityY);
                    mListRV.fling(velocityX, velocityY);
                    return true;
                } else if (abs(velocityY) > 10000 && abs(velocityY) != 6400) {
                    velocityY = 5400 * (int) Math.signum((double) velocityY);
                    mListRV.fling(velocityX, velocityY);
                    return true;
                } else if (abs(velocityY) > 5000 && abs(velocityY) != 6400 && abs(velocityY) != 5400) {
                    velocityY = 3000 * (int) Math.signum((double) velocityY);
                    mListRV.fling(velocityX, velocityY);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Pause current bullets, youtube videos
     * Normal videos are by setting selector NONE, so dont forget to set DEFAULT selector on resume otherwise videos wont play even if resumed
     */
    public void Pause() {
        fragmentVisible = false;
        Log.d("youtubePlayer", "Pause = " + mArticlePosition);
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            Log.d("youtubePlayer", "Pauseholder = " + holder);
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof VideoViewHolder) {

//                    ((VideoViewHolder) holder).pause();
                } else if (holder instanceof YoutubeViewHolder) {
                    Log.d("youtubePlayer", "Pause utube");
                    ((YoutubeViewHolder) holder).bulletPause();
                } else if (holder instanceof YoutubeViewHolderEdge) {
                    Log.d("youtubePlayer", "Pause utube");
                    ((YoutubeViewHolderEdge) holder).bulletPause();
                } else if (holder instanceof DiscoverVideoViewHolder) {
                    ((DiscoverVideoViewHolder) holder).pauseVideos();
                } else if (holder instanceof DiscoverReelViewHolder) {
                    ((DiscoverReelViewHolder) holder).pauseVideos();
                }
            }

//            PlayerSelector playerSelector = PlayerSelector.NONE;
//            mListRV.setPlayerSelector(playerSelector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void preloadImage(String image) {
        Glide.with(this)
                .load(image)
                .encodeQuality(50)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
    }

    public void pauseOnlyBullets() {
        try {
            if (mListRV == null) return;
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.pause();
                } else if (holder instanceof VideoViewHolder) {
                    ((VideoViewHolder) holder).pause();
                } else if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).bulletPause();
                } else if (holder instanceof YoutubeViewHolderEdge) {
                    Log.d("youtubePlayer", "Pause utube");
                    ((YoutubeViewHolderEdge) holder).bulletPause();
                }
            }
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resumeCurrentBullet() {
        try {
            if (mListRV == null) return;
//            mListRV.setPlayerSelector(selector);
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.resume();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.resume();
                } else if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).bulletResume();
                } else if (holder instanceof YoutubeViewHolderEdge) {
                    Log.d("youtubePlayer", "Pause utube");
                    ((YoutubeViewHolderEdge) holder).bulletPause();
                } else if (holder instanceof DiscoverVideoViewHolder) {
                    ((DiscoverVideoViewHolder) holder).playVideos();
                } else if (holder instanceof DiscoverReelViewHolder) {
                    ((DiscoverReelViewHolder) holder).playVideos();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCurrentBullet() {
        fragmentVisible = true;
        if (mCardAdapter != null) {
            mCardAdapter.notifyDataSetChanged();
        }
//        try {
//            if (mListRV == null) return;
////            mListRV.setPlayerSelector(selector);
//            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
//            if (holder != null) {
//                if (holder instanceof VideoViewHolder) {
////                    mListRV.setPlayerSelector(selector);
//                } else if (holder instanceof DiscoverVideoViewHolder) {
//                    ((DiscoverVideoViewHolder) holder).playVideos();
//                } else if (holder instanceof DiscoverReelViewHolder) {
//                    ((DiscoverReelViewHolder) holder).playVideos();
//                } else {
//                    if (mCardAdapter != null) {
//                        Log.e("@@##@@##", "notifyDataSetChanged 1138");
//                        mCardAdapter.notifyDataSetChanged();
//                    }
//
//                    nextPosition(mArticlePosition);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (mListRV == null) return;
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).release();
                } else if (holder instanceof YoutubeViewHolderEdge) {
                    Log.d("youtubePlayer", "Pause utube");
                    ((YoutubeViewHolderEdge) holder).bulletPause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCardAdapter = null;
        mListRV.setAdapter(null);

//        mOrientationListener.disable();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mListRV == null) return;
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).release();
                } else if (holder instanceof YoutubeViewHolderEdge) {
                    Log.d("youtubePlayer", "Pause utube");
                    ((YoutubeViewHolderEdge) holder).bulletPause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCardAdapter = null;
        mListRV.setAdapter(null);
    }


    public void scrollToTop() {
        if (contentArrayList.size() > 0) {
            if (cardLinearLayoutManager != null)
                cardLinearLayoutManager.scrollToPositionWithOffset(0, 0);
//        if (mListRV != null)
//            mListRV.scrollToPosition(0);
        }
    }

    private boolean isLast() {
        return mNextPage.equalsIgnoreCase("");
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag && !refresh.isRefreshing() && menuVisible) {
            new_post.setVisibility(View.VISIBLE);
        } else {
            new_post.setVisibility(View.GONE);
        }
        if (flag && (contentArrayList.size() > 0)) {
            flag = false;
        }
        if (mLoaderCallback != null)
            mLoaderCallback.onProgressChanged(flag);
    }

    @Override
    public void error(String error) {
        if (!TextUtils.isEmpty(error) && error.toLowerCase().contains("cancel")) {
            return;
        }
        new_post.setVisibility(View.GONE);
        refresh.setRefreshing(false);
        if ((mCurrPage.equals("") || mCurrPage.equals("1"))) {
            if (!TextUtils.isEmpty(mContextId)) {
                if (contentArrayList.size() == 0) {
                    showNoDataErrorView(true);
                }
            }
        }
    }

    @Override
    public void error404(String error) {
        refresh.setRefreshing(false);
        new_post.setVisibility(View.GONE);
        if (mCurrPage.equals("") || mCurrPage.equals("1")) {
            if (!TextUtils.isEmpty(mContextId)) {
                if (contentArrayList.size() == 0) {
                    showNoDataErrorView(true);
                }
            }
        }
    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {
    }

    public void loadCacheCategories(HomeResponse homeResponse) {
        showNoDataErrorView(false);
        refresh.setRefreshing(false);

        if (homeResponse != null && homeResponse.getSections() != null && homeResponse.getSections().size() > 0) {
//            no_record_found.setVisibility(View.GONE);
            contentArrayList.clear();
            Article line = new Article();
            line.setType(LINE);
            try {
                for (SectionsItem section : homeResponse.getSections()) {
                    if (section != null) {
                        if (!TextUtils.isEmpty(section.getType())) {
                            switch (section.getType()) {
                                case ARTICLE_HERO:
                                    if (section.getData() != null && section.getData().getArticle() != null) {
                                        Article article = section.getData().getArticle();
                                        if (!TextUtils.isEmpty(article.getType())) {
                                            switch (article.getType()) {
                                                case "VIDEO":
                                                    article.setType("FIRST_VIDEO");
                                                    break;
                                                case "YOUTUBE":
                                                    article.setType("FIRST_YOUTUBE");
                                                    break;
                                                default:
                                                    article.setType("FIRST_IMAGE");
                                            }
                                        }
                                        contentArrayList.add(article);
//                                        contentArrayList.add(line);
                                    }
                                    break;
                                case ARTICLE_HORIZONTAL:
                                    if (!TextUtils.isEmpty(section.getData().getHeader())) {
                                        Article article = new Article();
                                        article.setTabTitle(section.getData().getHeader());
                                        article.setSubHeader(section.getData().getSubheader());
                                        if (section.getData().getFooter() != null) {
                                            article.setTabId(section.getData().getContext());
                                            article.setFooter(section.getData().getFooter());
                                        }
                                        contentArrayList.add(article);
                                    }
                                    //preloading large images articles
                                    if (section.getData().getArticles().get(0).getBullets() != null && section.getData().getArticles().get(0).getBullets().size() > 0) {
                                        for (Bullet bullet : section.getData().getArticles().get(0).getBullets()) {
                                            if (getActivity() != null && !getActivity().isDestroyed()) {
                                                Glide.with(this)
                                                        .load(bullet.getImage())
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL).preload();
                                            }
                                        }
                                    }
                                    //pre loading small images article
                                    if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                        for (Article article : section.getData().getArticles()) {
                                            if (getActivity() != null && !getActivity().isDestroyed()) {
                                                Glide.with(this)
                                                        .load(article.getImage())
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL).preload();
                                            }

                                        }
                                        Article article = new Article();
                                        article.setTitle(section.getData().getHeader());
                                        article.setType("HORIZONTAL_ARTICLES");
                                        ArrayList<Article> articles = section.getData().getArticles();
                                        if (section.getData().getTopic() != null) {
                                            Article article1 = new Article();
                                            article1.setId(section.getData().getTopic().getId());
                                            article1.setImage(section.getData().getTopic().getImage());
                                            article1.setFollowed(section.getData().getTopic().isFollowed());
                                            article1.setFollowed_text(section.getData().getTopic().getFollowed_text());
                                            article1.setUnfollowed_text(section.getData().getTopic().getUnfollowed_text());
                                            article1.setTitle(section.getData().getTopic().getName());
                                            article1.setTopicAdded(true);
                                            articles.add(article1);
                                        }
                                        article.setArticles(articles);
                                        contentArrayList.add(article);
//                                        contentArrayList.add(line);
                                    }
                                    //                                if (section.getData().getFooter() != null) {
                                    //                                    Article article = new Article();
                                    //                                    article.setTabId(section.getData().getContext());
                                    //                                    article.setFooter(section.getData().getFooter());
                                    //                                    contentArrayList.add(article);
                                    //                                }
                                    break;
                                case ARTICLE_LIST:
                                    if (section.getData().getArticles() != null) {
                                        //preloading large images articles
                                        if (section.getData().getArticles().get(0).getBullets() != null && section.getData().getArticles().get(0).getBullets().size() > 0) {
                                            for (Bullet bullet : section.getData().getArticles().get(0).getBullets()) {
                                                if (getActivity() != null && !getActivity().isDestroyed()) {
                                                    preloadImage(bullet.getImage());
                                                }
                                            }
                                        }
                                        //pre loading small images article
                                        if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                            contentArrayList.addAll(section.getData().getArticles());
                                            for (Article article : section.getData().getArticles()) {
                                                if (getActivity() != null && !getActivity().isDestroyed()) {
                                                    preloadImage(article.getImage());
                                                }
                                            }
                                        }
                                    }
//                                    contentArrayList.add(line);
                                    break;
                                case REELS:
                                    if (section.getData().getReels() != null && section.getData().getReels().size() > 0) {
                                        for (ReelsItem item : section.getData().getReels()) {
                                            Glide.with(this)
                                                    .load(item.getImage())
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .preload();
                                        }
                                        Article article = new Article();
                                        if (section.getData().getFooter() != null) {
                                            article.setTabId(section.getData().getContext());
                                            article.setFooter(section.getData().getFooter());
                                        }
                                        article.setType("REELS");
                                        article.setTitle(section.getData().getHeader());
                                        article.setReels(section.getData().getReels());
                                        contentArrayList.add(article);
                                        contentArrayList.add(line);
                                    }
                                    break;
                                case ADS:
                                    if (!adFailedStatus) {
                                        Log.e("ADS", "AD Added");
                                        Article adArticle1 = new Article();
                                        if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                                            adArticle1.setType("FB_Ad");
                                        } else {
                                            adArticle1.setType("G_Ad");
                                        }
                                        contentArrayList.add(adArticle1);
                                        contentArrayList.add(line);
                                    }
                                    break;
                                case TOPICS:
                                    if (section.getData().getTopics() != null && section.getData().getTopics().size() > 0) {
                                        Article article = new Article();
                                        if (section.getData().getFooter() != null) {
                                            article.setTabId(section.getData().getContext());
                                            article.setFooter(section.getData().getFooter());
                                        }
                                        article.setType("TOPICS");
                                        article.setTitle(section.getData().getHeader());
                                        article.setTopicsList(section.getData().getTopics());
                                        contentArrayList.add(article);
//                                        contentArrayList.add(line);
                                    }
                                    //                                if (section.getData().getFooter() != null) {
                                    //                                    Article article = new Article();
                                    //                                    article.setTabId(section.getData().getContext());
                                    //                                    article.setFooter(section.getData().getFooter());
                                    //                                    contentArrayList.add(article);
                                    //                                }
                                    break;
                                case CHANNELS:
                                    if (section.getData().getChannels() != null && section.getData().getChannels().size() > 0) {
                                        Article article = new Article();
                                        if (section.getData().getFooter() != null) {
                                            article.setTabId(section.getData().getContext());
                                            article.setFooter(section.getData().getFooter());
                                        }
                                        article.setType("CHANNELS");
                                        article.setTitle(section.getData().getHeader());
                                        article.setChannels(section.getData().getChannels());
                                        contentArrayList.add(article);
//                                        contentArrayList.add(line);
                                    }
                                    //                                if (section.getData().getFooter() != null) {
                                    //                                    Article article = new Article();
                                    //                                    article.setTabId(section.getData().getContext());
                                    //                                    article.setFooter(section.getData().getFooter());
                                    //                                    contentArrayList.add(article);
                                    //                                }
                                    break;
                                case LARGE_REELS:
                                    if (section.getData().getReels() != null && section.getData().getReels().size() > 0) {
                                        Article article = new Article();
                                        if (section.getData().getFooter() != null) {
                                            article.setTabId(section.getData().getContext());
                                            article.setFooter(section.getData().getFooter());
                                        }
                                        article.setType("LARGE_REEL");
                                        article.setTitle(section.getData().getHeader());
                                        article.setReels(section.getData().getReels());
                                        contentArrayList.add(article);
//                                        contentArrayList.add(line);
                                    }
                                    //                                if (section.getData().getFooter() != null) {
                                    //                                    Article article = new Article();
                                    //                                    article.setTabId(section.getData().getContext());
                                    //                                    article.setFooter(section.getData().getFooter());
                                    //                                    article.setFooterType("LARGE_REEL");
                                    //                                    contentArrayList.add(article);
                                    //                                }
                                    break;
                                case ARTICLE_VIDEOS:
                                    //preloading large images articles
                                    if (section.getData().getArticles().get(0).getBullets() != null && section.getData().getArticles().get(0).getBullets().size() > 0) {
                                        for (Bullet bullet : section.getData().getArticles().get(0).getBullets()) {
                                            if (getActivity() != null) {
                                                Glide.with(this)
                                                        .load(bullet.getImage())
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL).preload();
                                            }
                                        }
                                    }
                                    //pre loading small images article
                                    if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                        //Adding the preload for images
                                        for (Article article : section.getData().getArticles()) {
                                            if (getActivity() != null) {
                                                Glide.with(this)
                                                        .load(article.getImage())
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL).preload();
                                            }
                                        }
                                        Article article = new Article();
                                        if (section.getData().getFooter() != null) {
                                            article.setTabId(section.getData().getContext());
                                            article.setFooter(section.getData().getFooter());
                                        }
                                        article.setTitle(section.getData().getHeader());
                                        article.setType("ARTICLE_VIDEOS");
                                        article.setArticles(section.getData().getArticles());
                                        contentArrayList.add(article);
//                                        contentArrayList.add(line);
                                    }
                                    //                                if (section.getData().getFooter() != null) {
                                    //                                    Article article = new Article();
                                    //                                    article.setTabId(section.getData().getContext());
                                    //                                    article.setFooter(section.getData().getFooter());
                                    //                                    contentArrayList.add(article);
                                    //                                }
                                    break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                refresh.setRefreshing(false);
                callData(type);
                return;
            }

            if (mCardAdapter != null) {
                Log.e("@@##@@##", "notifyDataSetChanged 1407");
                mCardAdapter.notifyDataSetChanged();
            }

            if (fragmentVisible) {
                if (goHomeTempHome != null)
                    goHomeTempHome.sendAudioEvent("select_first_article");
            }
            if (swipeListener != null) {
                swipeListener.muteIcon(true);
            }
        } else {
            if (contentArrayList.size() == 0) {
                if (swipeListener != null) {
                    swipeListener.muteIcon(false);
                }
                if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("ARCHIVE")) {
//                    no_saved_articles.setVisibility(View.VISIBLE);
                } else
//                    no_record_found.setVisibility(View.VISIBLE);

                    contentArrayList.clear();
                if (mCardAdapter != null) {
                    Log.e("@@##@@##", "notifyDataSetChanged 1430");
                    mCardAdapter.notifyDataSetChanged();
                }
            }
        }
        callData(type);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (!menuVisible)
            pauseOnlyBullets();
        fragmentVisible = menuVisible;
        this.menuVisible = menuVisible;
    }

    public boolean checkVisibility() {
//        if (getActivity() instanceof MainActivityNew) {
//            if (((MainActivityNew) getActivity()).getHomeFragment().isPopupVisible()) {
//                return false;
//            }
//        }
        return menuVisible && fragmentResumed;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        Log.e("@@@@", "onHiddenChanged " + hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof VideoViewHolder) {
                    PopUpClass popUpClass = ((VideoViewHolder) holder).getPopUpClass();
                    if (popUpClass != null && popUpClass.isShowing()) {
                        popUpClass.dismiss();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mCardAdapter != null) mCardAdapter.dismissBottomSheet();
        fragmentResumed = false;
    }


    @Override
    public void successArticle(Article article) {
        //FOR PUSH NOTIFICATION SHOW SINGLE ARTICLE
        showNoDataErrorView(false);
        refresh.setRefreshing(false);
        if (refreshLayout) {
            contentArrayList.clear();
            if (mCardAdapter != null) {
                mCardAdapter.notifyDataSetChanged();
            }
            refreshLayout = false;
        }
        if (article != null) {
//            no_record_found.setVisibility(View.GONE);
            contentArrayList.add(article);

            if (mCurrPage.equalsIgnoreCase("1")) {
                if (contentArrayList.size() > 0) {
                    contentArrayList.get(0).setSelected(true);
                    mCurrPage = "2";
                }
            }

            for (int i = 0; i < contentArrayList.size(); i++) {
                contentArrayList.get(i).setFragTag(getMyTag());
            }
            if (mCardAdapter != null) {
                mCardAdapter.notifyDataSetChanged();
            }

            if (swipeListener != null) {
                if (contentArrayList.size() > 0) {
                    swipeListener.muteIcon(true);
                } else {
                    swipeListener.muteIcon(false);
                }
                swipeListener.onFavoriteChanged(article.getSource().isFavorite());
            }
        } else {
            if (swipeListener != null) {
                swipeListener.muteIcon(false);
            }
        }
    }

    @Override
    public void homeSuccess(HomeResponse homeResponse, String currentPage) {
        rlProgress.setVisibility(View.GONE);
        String tempLastModifiedTime = cacheManager.getHomeLastModifiedTimeById(mContextId);
        int notifyInitCount = contentArrayList.size();


        if (!TextUtils.isEmpty(mNextPage) || refresh.isRefreshing() || TextUtils.isEmpty(tempLastModifiedTime) || TextUtils.isEmpty(lastModifiedTime) || !tempLastModifiedTime.equals(lastModifiedTime)) {
            showNoDataErrorView(false);
            if (TextUtils.isEmpty(currentPage)) {
                contentArrayList.clear();
                notifyInitCount = 0;
            }
//            boolean newPosts = false;
            if (homeResponse != null) {
                if (homeResponse.getSections() != null && homeResponse.getSections().size() > 0) {
//                    no_record_found.setVisibility(View.GONE);
                    Article line = new Article();
                    line.setType(LINE);
                    int newItems = 0;
                    ArrayList<SectionsItem> sections = homeResponse.getSections();

                    try {
                        for (SectionsItem section : sections) {
                            if (section != null) {
                                if (!TextUtils.isEmpty(section.getType())) {
                                    switch (section.getType()) {
                                        case ARTICLE_HERO:
                                            if (section.getData() != null && section.getData().getArticle() != null) {
                                                Article article = section.getData().getArticle();
                                                if (!TextUtils.isEmpty(article.getType())) {
                                                    switch (article.getType()) {
                                                        case "VIDEO":
                                                            article.setType("FIRST_VIDEO");
                                                            break;
                                                        case "YOUTUBE":
                                                            article.setType("FIRST_YOUTUBE");
                                                            break;
                                                        default:
                                                            article.setType("FIRST_IMAGE");
                                                    }
                                                }
                                                contentArrayList.add(article);
//                                                contentArrayList.add(line);
                                            }
                                            break;
                                        case ARTICLE_HORIZONTAL:
                                            if (!TextUtils.isEmpty(section.getData().getHeader())) {
                                                Article article = new Article();
                                                article.setTabTitle(section.getData().getHeader());
                                                article.setSubHeader(section.getData().getSubheader());
                                                if (section.getData().getFooter() != null) {
                                                    article.setTabId(section.getData().getContext());
                                                    article.setFooter(section.getData().getFooter());
                                                }
                                                contentArrayList.add(article);
                                            }
                                            //preloading large bullets
                                            if (section.getData().getArticles().get(0).getBullets() != null && section.getData().getArticles().get(0).getBullets().size() > 0) {
                                                for (Bullet bullet : section.getData().getArticles().get(0).getBullets()) {
                                                    if (getActivity() != null) {
                                                        preloadImage(bullet.getImage());
                                                    }
                                                }
                                            }

                                            //preloading small images
                                            if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                                for (Article article : section.getData().getArticles()) {
                                                    if (getActivity() != null) {
                                                        preloadImage(article.getImage());
                                                    }
                                                }
                                                Article article = new Article();
                                                article.setTitle(section.getData().getHeader());
                                                article.setType("HORIZONTAL_ARTICLES");
                                                ArrayList<Article> articles = section.getData().getArticles();
                                                if (section.getData().getTopic() != null) {
                                                    Article article1 = new Article();
                                                    article1.setId(section.getData().getTopic().getId());
                                                    article1.setImage(section.getData().getTopic().getImage());
                                                    article1.setFollowed(section.getData().getTopic().isFollowed());
                                                    article1.setFollowed_text(section.getData().getTopic().getFollowed_text());
                                                    article1.setUnfollowed_text(section.getData().getTopic().getUnfollowed_text());
                                                    article1.setTitle(section.getData().getTopic().getName());
                                                    article1.setTopicAdded(true);
                                                    articles.add(article1);
                                                }
                                                article.setArticles(articles);
                                                contentArrayList.add(article);
//                                                contentArrayList.add(line);
                                            }
                                            //                                        if (section.getData().getFooter() != null) {
                                            //                                            Article article = new Article();
                                            //                                            article.setFooter(section.getData().getFooter());
                                            //                                            article.setTabId(section.getData().getContext());
                                            //                                            contentArrayList.add(article);
                                            //                                        }
                                            break;
                                        case ARTICLE_LIST:
                                            if (section.getData().getArticles() != null) {
                                                //preloading large images articles
                                                if (section.getData().getArticles().get(0).getBullets() != null && section.getData().getArticles().get(0).getBullets().size() > 0) {
                                                    for (Bullet bullet : section.getData().getArticles().get(0).getBullets()) {
                                                        if (getActivity() != null) {
                                                            preloadImage(bullet.getImage());
                                                        }
                                                    }
                                                }
                                                //pre loading small images article
                                                if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                                    contentArrayList.addAll(section.getData().getArticles());
                                                    for (Article article : section.getData().getArticles()) {
                                                        if (getActivity() != null) {
                                                            preloadImage(article.getImage());
                                                        }
                                                    }
                                                }
                                            }
//                                            contentArrayList.add(line);
                                            break;
                                        case REELS:
                                            if (section.getData().getReels() != null && section.getData().getReels().size() > 0) {
                                                for (ReelsItem item : section.getData().getReels()) {
                                                    Glide.with(this)
                                                            .load(item.getImage())
                                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                            .preload();
                                                }
                                                Article article = new Article();
                                                if (section.getData().getFooter() != null) {
                                                    article.setTabId(section.getData().getContext());
                                                    article.setFooter(section.getData().getFooter());
                                                }
                                                article.setType(REELS);
                                                article.setTitle(section.getData().getHeader());
                                                article.setReels(section.getData().getReels());
                                                contentArrayList.add(article);
                                            }
                                            contentArrayList.add(line);
                                            break;
                                        case ADS:
                                            if (!adFailedStatus) {
                                                Log.e("ADS", "AD Added");
                                                Article adArticle1 = new Article();
                                                if (prefConfig.getAds() != null && !TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                                                    adArticle1.setType("FB_Ad");
                                                } else {
                                                    adArticle1.setType("G_Ad");
                                                }
                                                contentArrayList.add(adArticle1);
                                                contentArrayList.add(line);
                                            }
                                            break;
                                        case TOPICS:
                                            if (section.getData().getTopics() != null && section.getData().getTopics().size() > 0) {
                                                Article article = new Article();
                                                if (section.getData().getFooter() != null) {
                                                    article.setTabId(section.getData().getContext());
                                                    article.setFooter(section.getData().getFooter());
                                                }
                                                article.setType(TOPICS);
                                                article.setTitle(section.getData().getHeader());
                                                article.setTopicsList(section.getData().getTopics());
                                                contentArrayList.add(article);
//                                                contentArrayList.add(line);
                                            }
                                            //                                        if (section.getData().getFooter() != null) {
                                            //                                            Article article = new Article();
                                            //                                            article.setFooter(section.getData().getFooter());
                                            //                                            article.setTabId(section.getData().getContext());
                                            //                                            contentArrayList.add(article);
                                            //                                        }
                                            break;
                                        case CHANNELS:
                                            if (section.getData().getChannels() != null && section.getData().getChannels().size() > 0) {
                                                Article article = new Article();
                                                if (section.getData().getFooter() != null) {
                                                    article.setTabId(section.getData().getContext());
                                                    article.setFooter(section.getData().getFooter());
                                                }
                                                article.setType(CHANNELS);
                                                article.setTitle(section.getData().getHeader());
                                                article.setChannels(section.getData().getChannels());
                                                contentArrayList.add(article);
//                                                contentArrayList.add(line);
                                            }
                                            //                                        if (section.getData().getFooter() != null) {
                                            //                                            Article article = new Article();
                                            //                                            article.setFooter(section.getData().getFooter());
                                            //                                            article.setTabId(section.getData().getContext());
                                            //                                            contentArrayList.add(article);
                                            //                                        }
                                            break;
                                        case NEW_POST:
                                            Article article = new Article();
                                            article.setType(NEW_POST);
                                            contentArrayList.add(0, article);
                                            notifyInitCount = 0;
                                            isCommunity = true;
                                            break;
                                        case FOLLOWING:
                                            Article articleFollowing = new Article();
                                            articleFollowing.setType(FOLLOWING);
                                            contentArrayList.add(0, articleFollowing);
                                            notifyInitCount = 0;
                                            break;
                                        case LARGE_REELS:
                                            if (section.getData().getReels() != null && section.getData().getReels().size() > 0) {
                                                Article article2 = new Article();
                                                if (section.getData().getFooter() != null) {
                                                    article2.setTabId(section.getData().getContext());
                                                    article2.setFooter(section.getData().getFooter());
                                                }
                                                article2.setType("LARGE_REEL");
                                                article2.setTitle(section.getData().getHeader());
                                                article2.setReels(section.getData().getReels());
                                                contentArrayList.add(article2);
//                                                contentArrayList.add(line);
                                            }
                                            //                                        if (section.getData().getFooter() != null) {
                                            //                                            Article article2 = new Article();
                                            //                                            article2.setTabId(section.getData().getContext());
                                            //                                            article2.setFooter(section.getData().getFooter());
                                            //                                            article2.setFooterType("LARGE_REEL");
                                            //                                            contentArrayList.add(article2);
                                            //                                        }
                                            break;
                                        case ARTICLE_VIDEOS:
                                            if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                                Article article3 = new Article();
                                                if (section.getData().getFooter() != null) {
                                                    article3.setTabId(section.getData().getContext());
                                                    article3.setFooter(section.getData().getFooter());
                                                }
                                                article3.setTitle(section.getData().getHeader());
                                                article3.setType("ARTICLE_VIDEOS");
                                                article3.setArticles(section.getData().getArticles());
                                                contentArrayList.add(article3);
//                                                contentArrayList.add(line);
                                            }
                                            //                                        if (section.getData().getFooter() != null) {
                                            //                                            Article article3 = new Article();
                                            //                                            article3.setTabId(section.getData().getContext());
                                            //                                            article3.setFooter(section.getData().getFooter());
                                            //                                            contentArrayList.add(article3);
                                            //                                        }
                                            break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        rlProgress.setVisibility(View.GONE);
                        showNoDataErrorView(true);
//                        no_record_found.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (fragmentVisible) {
                        if (cacheArticles == null || TextUtils.isEmpty(mNextPage)) {
                            if (goHomeTempHome != null) {
                                Log.e("@@##@@##", "select_first_article");
                                goHomeTempHome.sendAudioEvent("select_first_article");
                            }
                        }
                    }

                    if (fragmentVisible) {
                        if (mCardAdapter != null) {
                            if (notifyInitCount == 0) {
                                Log.e("@@##@@##", "notifyDataSetChanged 1721");
                                mCardAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("@@##@@##", "Range " + notifyInitCount + " " + (contentArrayList.size() - notifyInitCount));
                                mCardAdapter.notifyItemRangeInserted(notifyInitCount, contentArrayList.size() - notifyInitCount);
                            }
                        }
                    }

                    if (swipeListener != null) {
                        swipeListener.muteIcon(true);
                    }
                } else {
                    if (contentArrayList.size() == 0) {
                        if (swipeListener != null) {
                            swipeListener.muteIcon(false);
                        }
                        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("ARCHIVE")) {
                            showNoDataErrorView(true);
//                            no_saved_articles.setVisibility(View.VISIBLE);
                        } else {
                            showNoDataErrorView(true);
//                            no_record_found.setVisibility(View.VISIBLE);
                        }

                        contentArrayList.clear();
                        if (mCardAdapter != null) {
                            mCardAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if (homeResponse.getMeta() != null) {
                    mNextPage = homeResponse.getMeta().getNext();
                }
            }
            refreshLayout = false;
            isPagination = false;
        }

        if (getActivity() != null && !getActivity().isFinishing()) {
            if (contentArrayList.size() == 1 && !TextUtils.isEmpty(contentArrayList.get(0).getType())
            ) {
                if (contentArrayList.get(0).getType().equals(FOLLOWING)) {
                    showNoDataErrorView(true);
                } else if (contentArrayList.get(0).getType().equals(LINE)) {
                    showNoDataErrorView(true);
                }
            }
        }

        refresh.setRefreshing(false);
        lastModifiedTime = tempLastModifiedTime;
        if (cardLinearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 && (contentArrayList != null && contentArrayList.size() > 0)) {
            int firstVisibleChild = cardLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
            int lastVisibleChild = cardLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            long totalVisibleChild = Math.max(0, (lastVisibleChild - firstVisibleChild));
            if (totalVisibleChild > 0 && totalVisibleChild < contentArrayList.size()) {
                for (int i = 0; i < totalVisibleChild; i++) {
                    Article article = contentArrayList.get(firstVisibleChild + i);
                    if (article.getType() != null && (article.getType().equals("EXTENDED") || article.getType().equals("SIMPLE"))) {
                        AnalyticsEvents.INSTANCE.articleViewEvent(requireContext(), article.getId());
                    }
                }
            }
        }
    }

    @Override
    public void nextPosition(int position) {
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("pause");
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            if (contentArrayList.size() > 0 && position < contentArrayList.size() && position > -1) {
                mArticlePosition = position;
                contentArrayList.get(position).setLastPosition(0);
                selectCardPosition(mArticlePosition);
                if (cardLinearLayoutManager != null && mArticlePosition < contentArrayList.size())
                    cardLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);

                if (mCardAdapter != null) {
                    mCardAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {
        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);

        int oldPos = mArticlePosition;
        Article article = contentArrayList.get(position);

        if (holder instanceof LargeCardViewHolder)
            ((LargeCardViewHolder) holder).selectUnselectedItem(position, article);
        else if (holder instanceof SmallCardViewHolder)
            ((SmallCardViewHolder) holder).selectUnselectedItem(position, article);
        else if (holder instanceof DiscoverVideoViewHolder)
            ((DiscoverVideoViewHolder) holder).playVideos();
        else if (holder instanceof DiscoverReelViewHolder)
            ((DiscoverReelViewHolder) holder).playVideos();

        selectCardPosition(position);

        RecyclerView.ViewHolder holderOld = mListRV.findViewHolderForAdapterPosition(oldPos);
        if (holderOld instanceof SmallCardViewHolder)
            ((SmallCardViewHolder) holderOld).unselect(article);
        if (holderOld instanceof LargeCardViewHolder)
            ((LargeCardViewHolder) holderOld).unselect(article);
        else if (holderOld instanceof VideoViewHolder)
            mCardAdapter.notifyItemChanged(oldPos);
        else if (holderOld instanceof YoutubeViewHolder)
            mCardAdapter.notifyItemChanged(oldPos);
        else if (holderOld instanceof DiscoverVideoViewHolder)
            ((DiscoverVideoViewHolder) holderOld).pauseVideos();
        else if (holderOld instanceof DiscoverReelViewHolder)
            ((DiscoverReelViewHolder) holderOld).pauseVideos();
    }

    private void callNextPage() {
        Log.e("@@##@@##", "callNextPage");
        if (InternetCheckHelper.isConnected()) {
            isPagination = true;
            if (!TextUtils.isEmpty(mContextId)) {
                rlProgress.setVisibility(View.VISIBLE);
                presenter.updatedArticles(mContextId, prefConfig.isReaderMode(), mNextPage, true);
            }
        }
    }

    public void initAdapter() {
        if (swipeListener != null) swipeListener.muteIcon(false);
        mListRV.setAdapter(null);

        homeArticlesAdapterNew = new HomeArticlesAdapterNew((AppCompatActivity) getActivity(), contentArrayList, new CommentClick() {
            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("type", type);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {
//                YoutubeFullScreenActivity.start(CategoryFragment.this, getActivity(), article.getLink(), mode, position, duration, isManual);
            }

            @Override
            public void commentClick(int position, String id) {
//                Intent intent = new Intent(getContext(), CommentsActivity.class);
//                intent.putExtra("article_id", id);
//                intent.putExtra("position", position);
//                startActivityForResult(intent, Constants.CommentsRequestCode);
            }


            @Override
            public void onNewDetailClick(int position, Article article, List<Article> articlelist) {

            }
        });

        mCardAdapter = new HomeAdapter(isDark, new CommentClick() {

            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("type", type);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void onNewDetailClick(int position, Article article, List<Article> articlelist) {
                //shifa

                Intent intent = new Intent(getContext(), ArticleDetailNew.class);
                ArrayList<Article> itemsa = new ArrayList<>(); // your ArrayList of Article objects
                itemsa = (ArrayList<Article>) articlelist;
                intent.putExtra("myArrayList", new Gson().toJson(itemsa));
                intent.putExtra("type", type);
                intent.putExtra("articleID", article.getId());
                intent.putExtra("position", position);
                Log.e(TAG, "onNewDetailClick: " + mContextId);
                intent.putExtra("mContextId", mContextId);
                intent.putExtra("NextPageApi", mNextPage);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {
                YoutubeFullScreenActivity.start(CategoryFragment.this, getActivity(), article.getLink(), mode, position, duration, isManual);
            }

            @Override
            public void commentClick(int position, String id) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("article_id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }
        }, false, (AppCompatActivity) getActivity(), contentArrayList, type, isGotoFollowShow, new DetailsActivityInterface() {
            @Override
            public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {
                if (goHomeTempHome != null) {
                    goHomeTempHome.sendAudioToTempHome(audioCallback, fragTag, "", audio);
                }
            }

            @Override
            public void pause() {
                Pause();
            }

            @Override
            public void resume() {
                resumeCurrentBullet();
            }
        }, goHomeMainActivity, this, swipeListener, this, listener,
                new ShowOptionsLoaderCallback() {
                    @Override
                    public void showLoader(boolean show) {
                        if (show) {
                            showProgressDialog();
                        } else {
                            dismissProgressDialog();
                        }
                    }
                },
                new AdFailedListener() {
                    @Override
                    public void onAdFailed() {
                        removeAdItem();
                        adFailedStatus = true;
                    }
                },
                getLifecycle(), new CommunityItemCallback() {
            @Override
            public void onItemClick(String option, Article article) {
                if (!TextUtils.isEmpty(option)) {
                    switch (option) {
                        case "YOUTUBE":
                            pauseOnlyBullets();
                            showYoutubeUploadView(article.getId());
                            break;
                        case "REELS":
                            Reels();
                            break;
                        case "MEDIA":
                            Media();
                            break;
                        case "collapse":
                            isCreatePostExpanded = false;
//                            mArticlePosition = 1;
//                            CommunityFeedFragment.this.selectCardPosition(mArticlePosition);
//                            if (mCardAdapter != null) mCardAdapter.notifyDataSetChanged();
                            resumeCurrentBullet();
                            break;
                        case "expand":
                            isCreatePostExpanded = true;
//                            mArticlePosition = 0;
//                            CommunityFeedFragment.this.selectCardPosition(mArticlePosition);
//                            if (mCardAdapter != null) mCardAdapter.notifyDataSetChanged();
                            Pause();
                            break;
                        case "back_to_top":
                            mListRV.scrollToPosition(0);
                            selectCardPosition(1);
                            if (goHomeMainActivity != null) {
                                goHomeMainActivity.scrollDown();
                                scrollDist = 0;
                                isVisible = true;
                            }
                            break;
                    }
                }
            }
        }
        );
        mCardAdapter.categoryFragment = this;
        cardLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
        mListRV.setLayoutManager(cardLinearLayoutManager);
//        mListRV.setOnFlingListener(null);
        mCardAdapter.setHasStableIds(true);
        mListRV.setAdapter(mCardAdapter);
//        mListRV.setItemViewCacheSize(9);
//        mListRV.setCacheManager(mCardAdapter);
//        mListRV.setPlayerSelector(selector);
    }

    private void Media() {
        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryWithAll(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>());
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_NEW_MEDIA);
        }
    }

    private void Reels() {
        if (getActivity() == null) return;

        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryOnlyVideo(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST_REELS);
        } else {
            PermissionChecker.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_NEW_REELS);
        }
    }

    private void showYoutubeUploadView(String id) {
        if (getActivity() != null) {
            CreateYoutubePopup createYoutubePopup = new CreateYoutubePopup(getActivity());
            createYoutubePopup.showDialog(article -> {
                if (article != null) {
                    Log.e("uploadImageVideo", "-createSuccess here-> " + new Gson().toJson(article));
                    if (getActivity() == null) return;
                    Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.YOUTUBE);
                    intent.putExtra("MODE", MODE.ADD);
                    intent.putExtra("article", new Gson().toJson(article));
                    getActivity().startActivity(intent);
                }
            });
        }
    }

    private void removeAdItem() {
        if (contentArrayList != null && contentArrayList.size() > 0) {
            for (int i = 0; i < contentArrayList.size(); i++) {
                if (contentArrayList.get(i) != null && !TextUtils.isEmpty(contentArrayList.get(i).getType())) {
                    if (contentArrayList.get(i).getType().equals("FB_Ad") || contentArrayList.get(i).getType().equals("G_Ad")) {
                        contentArrayList.remove(i);
                        if (mCardAdapter != null)
                            mCardAdapter.notifyItemRemoved(i);
                        removeAdItem();
                        return;
                    }
                }
            }
        }
    }

    public void reload() {
        mNextPage = "";
        contentArrayList.clear();
        mCurrPage = "1";
        if (mCardAdapter != null) {
            Log.e("@@##@@##", "notifyDataSetChanged 2003");
            mCardAdapter.notifyDataSetChanged();
        }
        initAdapter();
        callData(type);
    }

    @Override
    public void removeItem(String id, int position) {

    }

    @Override
    public void unarchived() {
        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("ARCHIVE")) {
            Log.d("audiotest", "unarchve home : stop_destroy");
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("stop_destroy");
            reload();
        }
    }

    @Override
    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
        if (listener != null) {
            listener.onItemClicked(type, id, name, favorite);
        }
    }

    @Override
    public void verticalScrollList(boolean isEnable) {
        if (cardLinearLayoutManager != null)
            cardLinearLayoutManager.setScrollEnabled(isEnable);
    }

    @Override
    public void nextArticle(int position) {
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            if (contentArrayList != null && position < contentArrayList.size()) {
                int pos = position;
                pos++;
                if (mListRV != null)
                    mListRV.smoothScrollToPosition(pos);
            }
        }
    }

    @Override
    public void prevArticle(int position) {
        if (!TextUtils.isEmpty(mContextId) || (type != null && type.equals("ARCHIVE"))) {
            if (position > 0) {
                int pos = position;
                pos--;
                if (mListRV != null)
                    mListRV.smoothScrollToPosition(pos);
            }
        }
    }

    @Override
    public void clickArticle(int position) {

    }

    @Override
    public void onItemHeightMeasured(int height) {
//        ViewTreeObserver viewTreeObserver = mListRV.getViewTreeObserver();
//        if (viewTreeObserver.isAlive()) {
//            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    if (mListRV != null) {
//                        mListRV.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        int viewHeight = mListRV.getHeight();
//
//                        LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());
//                        if (forYouArrayList != null && forYouArrayList.size() > 0) {
//                            View view = layoutManager.findViewByPosition(forYouArrayList.size());
//                            if (view != null)
//                                mListRV.setPadding(0, 0, 0, viewHeight - height - view.getHeight());
//                            else
//                                mListRV.setPadding(0, 0, 0, viewHeight - height);
//                        } else {
//                            mListRV.setPadding(0, 0, 0, viewHeight - height);
//                        }
//                    }
//                }
//            });
//        }
    }

    public void showNoDataErrorView(boolean show) {
        noRecordFoundContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private int statusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    /**
     * loading dialog
     */
    protected void showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(getContext());
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissProgressDialog() {
        try {
            if (mLoadingDialog != null
                    && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            mLoadingDialog = null;
            e.printStackTrace();
        }
    }


//    public interface OnGotoChannelListener {
//        void onItemClicked(TYPE type, String id, String name, boolean favorite);
//        void onArticleSelected(Article article);
//    }
}
