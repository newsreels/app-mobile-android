package com.ziro.bullet.fragments;

import static android.app.Activity.RESULT_OK;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.background.BroadcastEmitter.TYPE_BACKGROUND_PROCESS;
import static com.ziro.bullet.utills.Constants.isApiCalling;
import static java.lang.Math.abs;

import android.Manifest;
import android.content.Context;
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
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.Helper.PopUpClass;
import com.ziro.bullet.Helper.SimpleOrientationListener;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.MainActivityNew;
import com.ziro.bullet.activities.NotificationActivity;
import com.ziro.bullet.activities.PostArticleActivity;
import com.ziro.bullet.activities.YoutubeFullScreenActivity;
import com.ziro.bullet.adapters.CommunityFeed.NewPostViewHolder;
import com.ziro.bullet.adapters.NewFeed.HomeAdapter;
import com.ziro.bullet.adapters.NewFeed.YoutubeViewHolderEdge;
import com.ziro.bullet.adapters.discover.DiscoverReelViewHolder;
import com.ziro.bullet.adapters.discover.DiscoverVideoViewHolder;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.background.BackgroundEvent;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.bottomSheet.ForYouReelSheet;
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
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.ene.toro.PlayerSelector;
import im.ene.toro.widget.Container;

public class ArticleFragment extends Fragment implements NewsCallback, ShareToMainInterface, ListAdapterListener {
    public static final int PERMISSION_REQUEST_NEW_REELS = 345;
    public static final int PERMISSION_REQUEST_NEW_MEDIA = 346;
    public static final int PERMISSION_REQUEST_REELS = 745;
    public static final int INTENT_FOLLOWING = 4324;
    private static final String TAG = "TempCategoryFragment";

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
    //   private static boolean isHome;
    private static GoHome goHomeMainActivity;
    private static GoHome goHomeTempHome;
    private static HomeLoaderCallback mLoaderCallback;
    final float MINIMUM_SCROLL_DIST = 2;
    int scrollDist = 0;
    boolean isVisible = true;
    private SimpleOrientationListener mOrientationListener;
    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private String type = "";
    private PrefConfig prefConfig;
    private boolean refreshLayout = false;
    private boolean isGotoFollowShow;
    private String mTopic = "", mContextId = "", mTitle = "";
    private String mNextPage = "", mCurrPage = "1";
    private String _myTag;
    private ArrayList<Article> contentArrayList = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    //    private CardView back;
    private ImageView gradient2;
    private CoordinatorLayout root;
    private RelativeLayout ll_reels_info;
    private LinearLayout noRecordFoundContainer;
    private Container mListRV;
    private RelativeLayout no_record_found;
    private CardView new_post;
    private ProgressBar progress;
    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private HomeAdapter mCardAdapter;
    private TempCategorySwipeListener swipeListener;
    private OnHomeFragmentInteractionListener listener;
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
    private CategoryFragmentModel categoryFragmentModel;
    private String lastModifiedTime;
    private boolean isCommunity;
    private boolean isDark;
    private static final String KEY_FOR_YOU = "for_you";
    private static final String KEY_FOLLOWING = "following";
    private String mode = KEY_FOR_YOU;
    private ImageView notification;
    private ForYouReelSheet forYouReelSheet;
    private TextView tvLabel;

    public static ArticleFragment newInstance(boolean isDark, GoHome goHomeMainActivity1, GoHome goHomeTempHomeListener) {
        ArticleFragment categoryFragment = new ArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDark", isDark);
        goHomeMainActivity = goHomeMainActivity1;
        goHomeTempHome = goHomeTempHomeListener;
        categoryFragment.setArguments(bundle);
        return categoryFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // bottom bar control vars reset
        scrollDist = 0;
        isVisible = true;
        fragmentVisible = true;
        fragmentResumed = true;

        updateMuteButton();
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefConfig = new PrefConfig(getContext());
        cacheManager = new DbHandler(getContext());
        lastModifiedTime = cacheManager.getHomeLastModifiedTimeById(mContextId);

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

        return inflater.inflate(R.layout.layout_article, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refresh = view.findViewById(R.id.refresh);
        mListRV = view.findViewById(R.id.recyclerview);
        root = view.findViewById(R.id.root);
        ll_reels_info = view.findViewById(R.id.ll_reels_info);
        gradient2 = view.findViewById(R.id.gradient2);
        noRecordFoundContainer = view.findViewById(R.id.no_record_found_container);
        no_record_found = view.findViewById(R.id.no_record_found);
        tvLabel = view.findViewById(R.id.tvLabel);
        new_post = view.findViewById(R.id.new_post);
        progress = view.findViewById(R.id.progress);

        notification = view.findViewById(R.id.notification);

        presenter = new NewsPresenter(getActivity(), this);

        initAdapter();
        setRvScrollListener();

        refresh.setOnRefreshListener(this::refreshCategory);

        ll_reels_info.setOnClickListener(view1 -> {
            forYouBottomSheet(mode);
        });
        notification.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });

        Utils.addNoDataErrorView(getContext(), noRecordFoundContainer, v -> {
            refreshCategory();
        }, null);

        if (mListRV != null)
            mListRV.scrollToPosition(0);


        mOrientationListener = new SimpleOrientationListener(getContext()) {

            @Override
            public void onSimpleOrientationChanged(int orientation) {
                if (!isHidden && (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) && getActivity() != null &&
                        getActivity() instanceof MainActivityNew
                        && ((MainActivityNew) getActivity()).getVisibleFragment() instanceof ArticleFragment
                        && getContext() != null && Settings.System.getInt(getContext().getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, 0) == 1 && checkVisibility() && fragmentResumed) {
                    try {
                        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                        if (holder != null) {
                            if (holder instanceof VideoViewHolder) {
                                ((VideoViewHolder) holder).showFullScreenVideo(orientation, false);
                            } else if (holder instanceof YoutubeViewHolder) {
                                ((YoutubeViewHolder) holder).showFullScreenVideo(orientation, mArticlePosition);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                        if (holder != null) {
                            if (holder instanceof VideoViewHolder && !((VideoViewHolder) holder).isManual()) {
                                PopUpClass popUpClass = ((VideoViewHolder) holder).getPopUpClass();
                                if (popUpClass != null && popUpClass.isShowing()) {
                                    popUpClass.dismiss();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mOrientationListener.enable();

        forYouSelect();

        mNextPage = "";
        mCurrPage = "1";
        refreshLayout = true;
        loadCacheData();
    }

    public void forYouBottomSheet(String mode) {
        forYouReelSheet = ForYouReelSheet.getInstance(mode, "", null, null, null, null);
        forYouReelSheet.show(getChildFragmentManager(), "id");
        forYouReelSheet.getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> {
            //when lifecycleOwner is null fragment is destroyed
            if (lifecycleOwner == null) {

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
    }

    public void refreshCategory() {
        if (mArticlePosition != 0) {
            scrollToTop();
        }
        refresh.setRefreshing(true);
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("pause");
        mArticlePosition = 0;
        mCurrPage = "1";
        mNextPage = "";
        refreshLayout = true;
        callData(type);
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

                        Log.d(TAG, "onActivityResult: " + position);
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

                                            if (info.isSource_followed()) {
                                                ((LargeCardViewHolder) holder).follow.setVisibility(View.GONE);
                                            } else {
                                                ((LargeCardViewHolder) holder).follow.setVisibility(View.VISIBLE);
                                            }

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

                                            if (info.isSource_followed()) {
                                                ((YoutubeViewHolder) holder).follow.setVisibility(View.GONE);
                                            } else {
                                                ((YoutubeViewHolder) holder).follow.setVisibility(View.VISIBLE);
                                            }
                                        } else if (holder instanceof VideoViewHolder) {
                                            Utils.checkLikeView(getContext(),
                                                    info.getLike_count(),
                                                    info.getComment_count(),
                                                    ((VideoViewHolder) holder).comment_count,
                                                    ((VideoViewHolder) holder).like_count,
                                                    ((VideoViewHolder) holder).like_icon,
                                                    info.isLiked()
                                            );

                                            if (info.isSource_followed()) {
                                                ((VideoViewHolder) holder).ivFollow.setVisibility(View.GONE);
                                            } else {
                                                ((VideoViewHolder) holder).ivFollow.setVisibility(View.VISIBLE);
                                            }
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
            } else if (requestCode == Constants.FollowRequestCode) {
                if (data != null) {
                    if (presenter != null && data.hasExtra("id")) {
                        String id = data.getStringExtra("id");
                        int position = data.getIntExtra("position", -1);

                        Log.d(TAG, "onActivityResult: " + mCardAdapter);
                        Log.d(TAG, "onActivityResult: " + mListRV);
                        Log.d(TAG, "onActivityResult: " + mListRV.findViewHolderForAdapterPosition(position));
                        if (!TextUtils.isEmpty(id) && position != -1) {

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

    private void forYouSelect() {
        mContextId = "VE9QX05FV1MqKioqKkZPUl9ZT1U=";
        mode = KEY_FOR_YOU;
        if (getActivity() != null) {
            prefConfig.setReelsType(Constants.REELS_FOR_YOU);
            tvLabel.setText(getString(R.string.for_you));
            reload();
        }
    }

    private void followingSelect() {
        mContextId = "Rk9SX1lPVSoqKioqTEFURVNU";
        mode = KEY_FOLLOWING;
        if (getActivity() != null) {
            prefConfig.setReelsType(Constants.REELS_FOLLOWING);
            tvLabel.setText(getString(R.string.following));
            reload();
        }
    }

    private void callData(String type) {
        Log.e("callData", "mContextId : " + mContextId);

        if (!TextUtils.isEmpty(mContextId)) {
            presenter.updatedArticles(mContextId, prefConfig.isReaderMode(), mNextPage, true);
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

                //pause bullet and audio while scrolling
                if (newState == SCROLL_STATE_DRAGGING) {
                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("pause");
                    pauseOnlyBullets();
                }

                if (newState == SCROLL_STATE_IDLE) {
                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
                            Events.ARTICLE_SWIPE);
                    Constants.auto_scroll = true;

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());

                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!fragmentVisible)
                        return;

                    new Handler().postDelayed(() -> {
                        if (fragmentVisible) {

                            if (firstPosition != -1 && mListRV != null) {
                                Rect rvRect = new Rect();
                                mListRV.getGlobalVisibleRect(rvRect);

                                Rect rowRect = new Rect();

                                if (layoutManager.findViewByPosition(firstPosition) == null)
                                    return;

                                layoutManager.findViewByPosition(firstPosition).getGlobalVisibleRect(rowRect);

                                int percentFirst;
                                if (rowRect.bottom >= rvRect.bottom) {
                                    int visibleHeightFirst = rvRect.bottom - rowRect.top;
                                    percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
                                } else {
                                    int visibleHeightFirst = rowRect.bottom - rvRect.top;
                                    percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
                                }

                                if (percentFirst > 100)
                                    percentFirst = 100;

                                int VISIBILITY_PERCENTAGE = 90;

                                int copyOfmArticlePosition = mArticlePosition;

                                Log.d("slections", "onScrollStateChanged: percentFirst = " + percentFirst);

                                /* based on percentage of item visibility, select current or next article
                                 *  if prev position is same as new pos then dont reset the article
                                 * */
                                if (percentFirst >= VISIBILITY_PERCENTAGE) {
                                    Log.d("slections", "onScrollStateChanged: percentage greater");
                                    mArticlePosition = firstPosition;
                                    if (mArticlePosition == 0) {

                                        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                        if (holder != null) {
                                            if (holder instanceof HomeAdapter.TitleViewHolder) {
                                                mArticlePosition = 1;
                                            }
                                        }

                                        selectCardPosition(mArticlePosition);

                                        if (mCardAdapter != null) {
                                            Log.e("@@##@@##", "notifyDataSetChanged 868");
                                            mCardAdapter.notifyDataSetChanged();
                                        }

                                    } else if (mArticlePosition == contentArrayList.size() - 1) {

                                        //on fast scrolling select the last one in the last
                                        selectCardPosition(mArticlePosition);

                                        if (mCardAdapter != null) {
                                            Log.e("@@##@@##", "notifyDataSetChanged 878");
                                            mCardAdapter.notifyDataSetChanged();
                                        }
                                    } else if (copyOfmArticlePosition == mArticlePosition) {
                                        Log.d("slections", "onScrollStateChanged: copy = new pos");
                                        //scroll rested on same article so resume audio and bullet
                                        try {
                                            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                            if (holder != null) {
                                                if (holder instanceof LargeCardViewHolder) {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("resume");
                                                    ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                                } else if (holder instanceof SmallCardViewHolder) {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("resume");
                                                    ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                                } else if (holder instanceof YoutubeViewHolder) {
                                                    ((YoutubeViewHolder) holder).youtubeResume();
                                                } else {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("stop_destroy");
                                                }
                                            } else {
                                                Log.d("audiotest", "scroll : stop_destroy");
                                                if (goHomeMainActivity != null)
                                                    goHomeMainActivity.sendAudioEvent("stop_destroy");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        if (copyOfmArticlePosition != mArticlePosition) {
                                            //scrolled to a new pos, so select new article
                                            selectCardPosition(mArticlePosition);

                                            if (mCardAdapter != null) {
                                                Log.e("@@##@@##", "notifyDataSetChanged 916");
                                                mCardAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                } else {
                                    mArticlePosition = firstPosition;
                                    mArticlePosition++;

                                    if (copyOfmArticlePosition != mArticlePosition) {
                                        //scrolled to a new pos, so select new article
                                        selectCardPosition(mArticlePosition);
                                        if (mCardAdapter != null) {
                                            Log.e("@@##@@##", "notifyDataSetChanged 929");
                                            mCardAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        //scroll rested on same article so resume audio and bullet
                                        try {
                                            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                                            if (holder != null) {
                                                if (holder instanceof LargeCardViewHolder) {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("resume");
                                                    ((LargeCardViewHolder) holder).storiesProgressView.resume();
                                                } else if (holder instanceof SmallCardViewHolder) {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("resume");
                                                    ((SmallCardViewHolder) holder).storiesProgressView.resume();
                                                } else if (holder instanceof YoutubeViewHolder) {
                                                    ((YoutubeViewHolder) holder).youtubeResume();
                                                } else {
                                                    if (goHomeMainActivity != null)
                                                        goHomeMainActivity.sendAudioEvent("stop_destroy");
                                                }
                                            } else {
                                                Log.d("audiotest", "scroll : stop_destroy");
                                                if (goHomeMainActivity != null)
                                                    goHomeMainActivity.sendAudioEvent("stop_destroy");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }, 500);

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (contentArrayList.size() - 20 <= cardLinearLayoutManager.findLastVisibleItemPosition() && !isLast()) {
                    if (!isApiCalling) {
                        isApiCalling = true;
                        callNextPage();
                    }
                }


                if (goHomeTempHome != null) {
                    if (dy > 0) {
//                        Log.e("RecyclerView_scrolled", "scroll up!");
                        goHomeTempHome.scrollUp();
                        //goHomeMainActivity.scrollUp();
                    } else {
//                        Log.e("RecyclerView_scrolled", "scroll down!");
                        goHomeTempHome.scrollDown();
                        //goHomeMainActivity.scrollDown();
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

            PlayerSelector playerSelector = PlayerSelector.NONE;
            mListRV.setPlayerSelector(playerSelector);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            mListRV.setPlayerSelector(selector);
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
        try {
            if (mListRV == null) return;
            mListRV.setPlayerSelector(selector);
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof VideoViewHolder) {
                    mListRV.setPlayerSelector(selector);
                } else if (holder instanceof DiscoverVideoViewHolder) {
                    ((DiscoverVideoViewHolder) holder).playVideos();
                } else if (holder instanceof DiscoverReelViewHolder) {
                    ((DiscoverReelViewHolder) holder).playVideos();
                } else {
                    if (mCardAdapter != null) {
                        Log.e("@@##@@##", "notifyDataSetChanged 1138");
                        mCardAdapter.notifyDataSetChanged();
                    }

                    nextPosition(mArticlePosition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

        mOrientationListener.disable();
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

        if (!refresh.isRefreshing()) {
            progress.setVisibility(flag ? View.VISIBLE : View.GONE);
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
        Log.e("@@##@@##", "loadCacheCategories");
        showNoDataErrorView(false);
        refresh.setRefreshing(false);

        if (homeResponse != null && homeResponse.getSections() != null && homeResponse.getSections().size() > 0) {
            no_record_found.setVisibility(View.GONE);
            contentArrayList.clear();
            Article line = new Article();
            line.setType("LINE");
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
                                    contentArrayList.add(line);
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
                                if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
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
                                    contentArrayList.add(line);
                                }
//                                if (section.getData().getFooter() != null) {
//                                    Article article = new Article();
//                                    article.setTabId(section.getData().getContext());
//                                    article.setFooter(section.getData().getFooter());
//                                    contentArrayList.add(article);
//                                }
                                break;
                            case ARTICLE_LIST:
                                if (!TextUtils.isEmpty(section.getData().getHeader())) {
                                    Article article = new Article();
                                    if (section.getData().getFooter() != null) {
                                        article.setTabId(section.getData().getContext());
                                        article.setFooter(section.getData().getFooter());
                                    }
                                    article.setTabTitle(section.getData().getHeader());
                                    contentArrayList.add(article);
                                }
                                if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                    contentArrayList.addAll(section.getData().getArticles());
                                }
                                contentArrayList.add(line);
//                                if (section.getData().getFooter() != null) {
//                                    Article article = new Article();
//                                    article.setTabId(section.getData().getContext());
//                                    article.setFooter(section.getData().getFooter());
//                                    contentArrayList.add(article);
//                                }
                                break;
                            case REELS:
//                                if (!TextUtils.isEmpty(section.getData().getHeader())) {
//                                    Article article = new Article();
//                                    article.setTabTitle(section.getData().getHeader());
//                                    contentArrayList.add(article);
//                                }
                                if (section.getData().getReels() != null && section.getData().getReels().size() > 0) {
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
//                                if (section.getData().getFooter() != null) {
//                                    Article article = new Article();
//                                    article.setTabId(section.getData().getContext());
//                                    article.setFooter(section.getData().getFooter());
//                                    contentArrayList.add(article);
//                                }
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
                                    contentArrayList.add(line);
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
                                    contentArrayList.add(line);
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
                                    contentArrayList.add(line);
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
                                if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                    Article article = new Article();
                                    if (section.getData().getFooter() != null) {
                                        article.setTabId(section.getData().getContext());
                                        article.setFooter(section.getData().getFooter());
                                    }
                                    article.setTitle(section.getData().getHeader());
                                    article.setType("ARTICLE_VIDEOS");
                                    article.setArticles(section.getData().getArticles());
                                    contentArrayList.add(article);
                                    contentArrayList.add(line);
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
                    no_record_found.setVisibility(View.VISIBLE);

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
        fragmentVisible = !isHidden;
        Log.e("@@@@", "onHiddenChanged " + hidden);
        Log.e("@@@@", "fragmentVisible " + fragmentVisible);
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
                Log.e("@@##@@##", "notifyDataSetChanged 1493");
                mCardAdapter.notifyDataSetChanged();
            }
            refreshLayout = false;
        }
        if (article != null) {
            no_record_found.setVisibility(View.GONE);
            contentArrayList.add(article);

            if (mCurrPage.equalsIgnoreCase("1")) {
                if (contentArrayList.size() > 0) {
                    contentArrayList.get(0).setSelected(true);
                    mCurrPage = "2";
                }
            }
            Log.e("LogcontentAr", "contentArrayList : " + contentArrayList.size());

            for (int i = 0; i < contentArrayList.size(); i++) {
                contentArrayList.get(i).setFragTag(getMyTag());
            }
            if (mCardAdapter != null) {
                Log.e("@@##@@##", "notifyDataSetChanged 1514");
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
                    no_record_found.setVisibility(View.GONE);
                    Article line = new Article();
                    line.setType("LINE");
                    int newItems = 0;
                    ArrayList<SectionsItem> sections = homeResponse.getSections();

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
                                            contentArrayList.add(line);
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
                                        if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
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
                                            contentArrayList.add(line);
                                        }
//                                        if (section.getData().getFooter() != null) {
//                                            Article article = new Article();
//                                            article.setFooter(section.getData().getFooter());
//                                            article.setTabId(section.getData().getContext());
//                                            contentArrayList.add(article);
//                                        }
                                        break;
                                    case ARTICLE_LIST:
                                        if (!TextUtils.isEmpty(section.getData().getHeader())) {
                                            Article article = new Article();
                                            if (section.getData().getFooter() != null) {
                                                article.setTabId(section.getData().getContext());
                                                article.setFooter(section.getData().getFooter());
                                            }
                                            article.setTabTitle(section.getData().getHeader());
                                            contentArrayList.add(article);
                                        }
                                        if (section.getData().getArticles() != null && section.getData().getArticles().size() > 0) {
                                            contentArrayList.addAll(section.getData().getArticles());
                                        }
                                        contentArrayList.add(line);
//                                        if (section.getData().getFooter() != null) {
//                                            Article article = new Article();
//                                            article.setFooter(section.getData().getFooter());
//                                            article.setTabId(section.getData().getContext());
//                                            contentArrayList.add(article);
//                                        }
                                        break;
                                    case REELS:
                                        if (section.getData().getReels() != null && section.getData().getReels().size() > 0) {
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
//                                        if (section.getData().getFooter() != null) {
//                                            Article article = new Article();
//                                            article.setFooter(section.getData().getFooter());
//                                            article.setTabId(section.getData().getContext());
//                                            contentArrayList.add(article);
//                                        }
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
                                            contentArrayList.add(line);
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
                                            contentArrayList.add(line);
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
                                            contentArrayList.add(line);
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
                                            contentArrayList.add(line);
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
//                            no_saved_articles.setVisibility(View.VISIBLE);
                        } else
                            no_record_found.setVisibility(View.VISIBLE);

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
            if (contentArrayList.size() == 1
                    && !TextUtils.isEmpty(contentArrayList.get(0).getType())
            ) {
                if (contentArrayList.get(0).getType().equals(FOLLOWING)) {
                    showNoDataErrorView(true);
                    no_record_found.setVisibility(View.GONE);
                    Utils.addNoFollowingView(getContext(), noRecordFoundContainer,
                            v -> FollowingActivity.launchActivity(this, INTENT_FOLLOWING), Constants.LIGHT);
                } else if (contentArrayList.get(0).getType().equals(NEW_POST)) {
                    no_record_found.setVisibility(View.VISIBLE);
                }
            }
        }

        refresh.setRefreshing(false);
        lastModifiedTime = tempLastModifiedTime;
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
                presenter.updatedArticles(mContextId, prefConfig.isReaderMode(), mNextPage, true);
            }
        }
    }

    public void initAdapter() {
        if (swipeListener != null) swipeListener.muteIcon(false);
        mListRV.setAdapter(null);
        mCardAdapter = new HomeAdapter(isDark, new CommentClick() {

            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getActivity(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("type", type);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {
                YoutubeFullScreenActivity.start(ArticleFragment.this, getActivity(), article.getLink(), mode, position, duration, isManual);
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
        }, goHomeMainActivity, this, swipeListener, this, new OnGotoChannelListener() {
            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
//                if (type != null && type.equals(TYPE.SOURCE)) {
//                    Intent intent = new Intent(getContext(), ChannelDetailsActivity.class);
//                    intent.putExtra("id", id);
//                    intent.putExtra("type", type);
//                    intent.putExtra("article_id", articleId);
//                    intent.putExtra("position", mCardAdapter.getArticlePosition());
//                    startActivityForResult(intent, Constants.CommentsRequestCode);
//                } else if (listener != null)
//                    listener.onItemClicked(type, id, name, favorite);
            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position) {
                if (type != null && type.equals(TYPE.SOURCE)) {
                    Intent intent = new Intent(getContext(), ChannelDetailsActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", type);
                    intent.putExtra("article_id", article.getId());
                    intent.putExtra("position", position);
                    startActivityForResult(intent, Constants.CommentsRequestCode);
                } else if (listener != null)
                    listener.onItemClicked(type, id, name, favorite);
            }

            @Override
            public void onArticleSelected(Article article) {

            }
        },
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
//        mCardAdapter.categoryFragment = this;
        cardLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
        mListRV.setLayoutManager(cardLinearLayoutManager);
        mListRV.setOnFlingListener(null);
        mListRV.setAdapter(mCardAdapter);
        mListRV.setCacheManager(mCardAdapter);
        mListRV.setPlayerSelector(selector);
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
        lastModifiedTime = "";
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.TYPE_VIDEO_PLAYING) {
            nextPositionNoScroll(event.getIntData(), false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BackgroundEvent event) {
        if (event.getType() == TYPE_BACKGROUND_PROCESS) {
            if (contentArrayList != null &&
                    contentArrayList.size() > 0) {
                RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(0);
                if (holder instanceof NewPostViewHolder) {
                    ((NewPostViewHolder) holder).updateBackgroundTask(event);
                }
            }
        }
    }

    public void reloadCurrent() {
        reload();
    }

    public void reloadCurrentBg() {
        reload();
    }

    public void invalidateViews() {
        if (getActivity() == null)
            return;
        notification.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        root.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
//        ll_reels_info.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.card_bg));
        gradient2.setImageResource(R.drawable.card_new_gradient_image);

        Utils.addNoDataErrorView(getContext(), noRecordFoundContainer, v -> reloadCurrent(), null);

        if (mCardAdapter != null)
            mCardAdapter.notifyDataSetChanged();
    }

    public void home() {

    }

    public void dataChanged() {

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        Log.e("LKASD", "onAttach");
        if (context instanceof OnHomeFragmentInteractionListener) {
            listener = (OnHomeFragmentInteractionListener) context;
        }

        if (context instanceof GoHome) {
            goHomeMainActivity = (GoHome) context;
        }
    }

    public interface OnHomeFragmentInteractionListener {
        void onItemClicked(TYPE type, String id, String name, boolean favorite);
    }

}
