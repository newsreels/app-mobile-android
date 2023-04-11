package com.ziro.bullet.fragments;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING;
import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.background.BroadcastEmitter.TYPE_BACKGROUND_PROCESS;
import static com.ziro.bullet.utills.Constants.isApiCalling;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.PostArticleActivity;
import com.ziro.bullet.activities.VideoFullScreenActivity;
import com.ziro.bullet.adapters.CommunityFeed.CommunityFeedAdapter;
import com.ziro.bullet.adapters.CommunityFeed.NewPostViewHolder;
import com.ziro.bullet.adapters.feed.LargeCardViewHolder;
import com.ziro.bullet.adapters.feed.SmallCardViewHolder;
import com.ziro.bullet.adapters.feed.VideoViewHolder;
import com.ziro.bullet.adapters.feed.YoutubeViewHolder;
import com.ziro.bullet.background.BackgroundEvent;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.data.POST_TYPE;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.AuthorListResponse;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.CommunityCallback;
import com.ziro.bullet.interfaces.CommunityItemCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.HomeLoaderCallback;
import com.ziro.bullet.interfaces.ListAdapterListener;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.onRefresh;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.ImageCrop;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.presenter.CommunityPresenter;
import com.ziro.bullet.utills.Constants;
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

public class CommunityFeedFragment extends Fragment implements CommunityCallback, onRefresh, ShareToMainInterface, ListAdapterListener {
    public static final int PERMISSION_REQUEST_REELS = 745;
    public static final int PERMISSION_REQUEST_MEDIA = 783;
    public static final int PERMISSION_REQUEST = 12;

    public static final int PERMISSION_REQUEST_NEW_REELS = 345;
    public static final int PERMISSION_REQUEST_NEW_MEDIA = 346;

    private static final String TAG = CommunityFeedFragment.class.getSimpleName();
    private static boolean isHome;
    private static GoHome goHomeMainActivity;
    private static GoHome goHomeTempHome;
    private static HomeLoaderCallback mLoaderCallback;
    final float MINIMUM_SCROLL_DIST = 2;
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    PlayerSelector selector = PlayerSelector.DEFAULT;
    // hide bottom bar while scrolling
    int scrollDist = 0;
    boolean isVisible = true;
    private PictureLoadingDialog mLoadingDialog;
    private String type = "";
    private PrefConfig prefConfig;
    private boolean refreshLayout = false;
    private boolean isGotoFollowShow;
    private String mNextPage = "", mCurrPage = "1";
    private String _myTag;
    private ArrayList<Article> contentArrayList = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private ProgressBar scrolling;
    private LinearLayout noRecordFoundContainer;
    private Container mListRV;
    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private CommunityFeedAdapter mCardAdapter;
    private TempCategorySwipeListener swipeListener;
    private OnGotoChannelListener listener;
    private CommunityPresenter presenter;
    private int mArticlePosition = 1;
    private boolean fragmentVisible = false;
    private boolean adFailedStatus;
    private ArrayList<Author> authors = new ArrayList<>();
    private ArrayList<ReelsItem> reels = new ArrayList<>();
    private DbHandler cacheManager;
    private ArrayList<Article> cacheArticles = new ArrayList<>();
    private boolean isPagination = false;
    private CardView new_post;
    private ShimmerFrameLayout shimmer_view_container;
    private boolean isCreatePostExpanded = false;
    private ArrayList<Article> newData = new ArrayList<>();

    public static CommunityFeedFragment newInstance(String contextId, String type1, boolean isHome1, GoHome goHomeMainActivity1, GoHome goHomeTempHomeListener, HomeLoaderCallback loaderCallback) {
        CommunityFeedFragment categoryFragment = new CommunityFeedFragment();
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(contextId))
            bundle.putString("context", contextId);
        if (!TextUtils.isEmpty(type1))
            bundle.putString("type", type1);
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
        scrollDist = 0;
        isVisible = true;
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
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("stop_destroy");
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefConfig = new PrefConfig(getContext());
        if (getArguments() != null && getArguments().containsKey("type")) {
            type = getArguments().getString("type");
        }
        return inflater.inflate(R.layout.layout_community, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BindViews(view);
        presenter = new CommunityPresenter(getActivity(), this);
        cacheManager = new DbHandler(getContext());
        expandCard();
        setRvScrollListener();
        setListeners();

        loadCacheData();
    }

    private void loadCacheData() {
        String JSON = cacheManager.GetCommunityRecord("community");
        if (!TextUtils.isEmpty(JSON)) {
            ArticleResponse response = new Gson().fromJson(JSON, ArticleResponse.class);
            if (response != null) {
                cacheArticles.addAll(response.getArticles());
                loadCommunityData(response);
            } else {
                callData(type);
            }
        } else {
            callData(type);
        }
    }

    private void setListeners() {
        new_post.setOnClickListener(v -> {
            new_post.setVisibility(View.GONE);
            if (newData.size() > 0) {
                contentArrayList.addAll(1, newData);
                newData.clear();
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
            }
            selectCardPosition(1);
            scrollToTop();
        });

        refresh.setOnRefreshListener(() -> {
            resetFirstCardWithMenu();
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("pause");
            fragmentVisible = true;
            mArticlePosition = 1;
            mCurrPage = "1";
            mNextPage = "";
            refreshLayout = true;
            callData(type);
        });

        Utils.addNoDataErrorView(getContext(), noRecordFoundContainer, v -> {
            mArticlePosition = 1;
            mCurrPage = "1";
            mNextPage = "";
            refreshLayout = true;
            callData(type);
        }, null);
    }

    public void resetFirstCardWithMenu() {
        if (contentArrayList != null && contentArrayList.size() > 0) {
            contentArrayList.get(0).setSelected(false);
            if (mCardAdapter != null)
                mCardAdapter.notifyItemChanged(0);
        }
    }

    public void reload() {
        resetFirstCardWithMenu();
        if (mArticlePosition > 1) {
            mArticlePosition = 1;
            selectCardPosition(mArticlePosition);
            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();
            scrollToTop();
        } else {
            refresh.setRefreshing(true);
            new_post.setVisibility(View.GONE);
            mNextPage = "";
//        contentArrayList.clear();
            mCurrPage = "1";
            mArticlePosition = 1;
            refreshLayout = true;
//        if (mCardAdapter != null)
//            mCardAdapter.notifyDataSetChanged();
//        expandCard();
            callData(type);
        }
    }

    private void BindViews(@NotNull View view) {
        shimmer_view_container = view.findViewById(R.id.shimmer_view_container);
        new_post = view.findViewById(R.id.new_post);
        scrolling = view.findViewById(R.id.scrolling);
        refresh = view.findViewById(R.id.refresh);
        mListRV = view.findViewById(R.id.recyclerview);
        noRecordFoundContainer = view.findViewById(R.id.no_record_found_container);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
        if (presenter != null && data != null && data.hasExtra("id")) {
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
        } else if (requestCode == PERMISSION_REQUEST_REELS) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() == 0) {
                    VideoInfo videoInfo = data.getParcelableExtra("video_info");

                    if (getActivity() == null || getActivity().isFinishing()) return;
                    Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.REELS);
                    intent.putExtra("MODE", MODE.ADD);

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
//                        intent.putExtra("uri", localMedia.getAndroidQToPath());
//                    } else {
                    intent.putExtra("file", new File(videoInfo.getPath()));
                    intent.putExtra("uri", videoInfo.getPath());
//                    }
                    intent.putExtra("video_info", videoInfo);
                    getActivity().startActivity(intent);
                } else
                    loadSelectedVideo(selectList.get(0));
            }
        } else if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() == 0) {
                    VideoInfo videoInfo = data.getParcelableExtra("video_info");

                    if (getActivity() == null || getActivity().isFinishing()) return;
                    Intent intent = new Intent(getActivity(), PostArticleActivity.class);
                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
                    intent.putExtra("MODE", MODE.ADD);

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        intent.putExtra("file", new File(String.valueOf(localMedia.getAndroidQToPath())));
//                        intent.putExtra("uri", localMedia.getAndroidQToPath());
//                    } else {
                    intent.putExtra("file", new File(videoInfo.getPath()));
                    intent.putExtra("uri", videoInfo.getPath());
//                    }
                    intent.putExtra("video_info", videoInfo);
                    getActivity().startActivity(intent);
                } else {
                    loadSelectedImage(selectList.get(0));
                }

            }
        } else if (requestCode == ImageCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                if (getActivity() == null) return;
                Uri resultUri = ImageCrop.getOutput(data);
                String cutPath = resultUri.getPath();

                Intent intent = new Intent(getActivity(), PostArticleActivity.class);
//                if (localMedia.getMimeType().contains("video")) {
//                    intent.putExtra("POST_TYPE", POST_TYPE.VIDEO_ARTICLE);
//                } else {
                intent.putExtra("POST_TYPE", POST_TYPE.ARTICLE);
//                }
                intent.putExtra("MODE", MODE.ADD);
                intent.putExtra("file", new File(cutPath));
                intent.putExtra("uri", cutPath);
//                intent.putExtra("localMedia", new Gson().toJson(localMedia));
                getActivity().startActivity(intent);
            }
        } else if (requestCode == Constants.VideoDurationRequestCode) {
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
    }

    private void callData(String type) {
        presenter.getCommunityFeed(mNextPage, false);
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
        if (contentArrayList.size() > 0) {
            for (int i = 0; i < contentArrayList.size(); i++) {
                contentArrayList.get(i).setSelected(false);
            }
            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();
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
                    Constants.auto_scroll = true;

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mListRV.getLayoutManager());

                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();

                    if (firstPosition != -1) {
                        Rect rvRect = new Rect();
                        mListRV.getGlobalVisibleRect(rvRect);

                        Rect rowRect = new Rect();
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
                                mArticlePosition++;
                                Log.d("slections", "onScrollStateChanged: mArticlePosition = 0");
                                selectCardPosition(mArticlePosition);

                                if (mCardAdapter != null)
                                    mCardAdapter.notifyDataSetChanged();
//                                }
                            } else if (mArticlePosition == contentArrayList.size() - 1) {
                                Log.d("slections", "onScrollStateChanged: last = 0");

                                //on fast scrolling select the last one in the last
                                selectCardPosition(mArticlePosition);

                                if (mCardAdapter != null)
                                    mCardAdapter.notifyDataSetChanged();
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
                                    Log.d("slections", "onScrollStateChanged: select new");
                                    //scrolled to a new pos, so select new article
                                    selectCardPosition(mArticlePosition);

                                    if (mCardAdapter != null)
                                        mCardAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.d("slections", "onScrollStateChanged: percentage less");
                            mArticlePosition = firstPosition;
                            mArticlePosition++;

                            if (copyOfmArticlePosition != mArticlePosition) {
                                Log.d("slections", "onScrollStateChanged: select new");
                                //scrolled to a new pos, so select new article
                                selectCardPosition(mArticlePosition);

                                if (mCardAdapter != null)
                                    mCardAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("slections", "onScrollStateChanged: else");
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
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (contentArrayList.size() - 3 <= cardLinearLayoutManager.findLastVisibleItemPosition() && !isLast()) {
                    if (!isApiCalling) {
                        isApiCalling = true;
                        scrolling.setVisibility(View.VISIBLE);
                        callNextPage();
                    }
                }


                if (goHomeTempHome != null && goHomeMainActivity != null) {
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BackgroundEvent event) {
        if (event.getType() == TYPE_BACKGROUND_PROCESS) {
            if (contentArrayList != null &&
                    contentArrayList.size() > 0 &&
                    contentArrayList.get(0).getType().equals("new")) {
                RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(0);
                if (holder instanceof NewPostViewHolder) {
                    ((NewPostViewHolder) holder).updateBackgroundTask(event);
                }
            }
        }
    }

    /**
     * /**
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resumeCurrentBullet() {
        try {
            mListRV.setPlayerSelector(selector);
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof LargeCardViewHolder) {
                    ((LargeCardViewHolder) holder).storiesProgressView.resume();
                } else if (holder instanceof SmallCardViewHolder) {
                    ((SmallCardViewHolder) holder).storiesProgressView.resume();
                } else if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).youtubeResume();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetCurrentBullet() {
        resetFirstCardWithMenu();
        fragmentVisible = true;
        try {
            mListRV.setPlayerSelector(selector);
            if (!isCreatePostExpanded) {
                RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
                if (holder != null) {
                    if (mCardAdapter != null)
                        mCardAdapter.notifyDataSetChanged();
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
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCardAdapter = null;
        mListRV.setAdapter(null);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(mArticlePosition);
            if (holder != null) {
                if (holder instanceof YoutubeViewHolder) {
                    ((YoutubeViewHolder) holder).release();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCardAdapter = null;
        mListRV.setAdapter(null);
    }

    public void invalidateViews() {
        if (mCardAdapter != null)
            mCardAdapter.notifyDataSetChanged();
    }

    public void scrollToTop() {
        if (mListRV != null)
//            mListRV.scrollToPosition(0);
            if (cardLinearLayoutManager != null)
                cardLinearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    private boolean isLast() {
        return mNextPage.equalsIgnoreCase("");
    }

    @Override
    public void loaderShow(boolean flag) {
        if (mLoaderCallback != null && contentArrayList.size() == 0)
            mLoaderCallback.onProgressChanged(flag);

        if (flag && contentArrayList.size() == 0) {
            shimmer_view_container.setVisibility(View.VISIBLE);
        } else {
            shimmer_view_container.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        isApiCalling = false;
        scrolling.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(error) && error.toLowerCase().contains("cancel")) {
            return;
        }
        refresh.setRefreshing(false);
        loaderShow(false);
        showNoDataErrorView(true);
    }

    @Override
    public void error404(String error) {
        isApiCalling = false;
        refresh.setRefreshing(false);
        scrolling.setVisibility(View.GONE);
        showNoDataErrorView(true);
        loaderShow(false);
    }

    @Override
    public void success(ArticleResponse response, boolean offlineData) {
        isApiCalling = false;
        showNoDataErrorView(false);
        refresh.setRefreshing(false);
        scrolling.setVisibility(View.GONE);
        if (refreshLayout) {
            contentArrayList.clear();
        }

        if (contentArrayList.size() == 0) {
            contentArrayList.add(new Article("", "new"));
        }

        boolean newPosts = false;
        if (response != null && response.getArticles() != null && response.getArticles().size() > 0) {
            removeExtraLayout("last_item");

            int newItems = 0;
            ArrayList<Article> articles = null;
            if (refreshLayout || cacheArticles == null || cacheArticles.size() == 0) {
                articles = response.getArticles();
            } else {
                if (TextUtils.isEmpty(mNextPage)) {
//                    articles = Utils.checkArticleDataIsSame(cacheArticles, response.getArticles());
                    if (articles.size() > 0 && !isPagination && !refreshLayout) {
                        newPosts = true;
                    }
                } else {
                    articles = response.getArticles();
                }
            }
            isPagination = false;

            if (response.getMeta() != null) {
                mNextPage = response.getMeta().getNext();
            }


            for (int position = 0; position < articles.size(); position++) {
                Article article = articles.get(position);
                if (prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                    int interval = 10;
                    if (prefConfig.getAds().getInterval() != 0) {
                        interval = prefConfig.getAds().getInterval();
                    }

                    if (newPosts) {
//                        if (articles.size() != 0 && articles.size() % interval == 0 && !adFailedStatus) {
//                            Log.e("ADS", "AD Added");
//                            Article adArticle1 = new Article();
//                            if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
//                                adArticle1.setType("FB_Ad");
//                            } else {
//                                adArticle1.setType("G_Ad");
//                            }
//                            newData.add(adArticle1);
//                        }
                    } else {

                        if (contentArrayList.size() != 0 && contentArrayList.size() % interval == 0 && !adFailedStatus) {
                            Log.e("ADS", "AD Added");
                            Article adArticle1 = new Article();
                            if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                                adArticle1.setType("FB_Ad");
                            } else {
                                adArticle1.setType("G_Ad");
                            }
                            contentArrayList.add(adArticle1);
                        }
                    }
                }

                article.setFragTag(getMyTag());
                if (newPosts) {
                    newData.add(article);

                } else {
                    contentArrayList.add(article);
                }

                if (response.getSuggested_authors() != -1) {
                    int author_ = response.getSuggested_authors();
                    if (position == author_) {
                        authors.clear();
                        if (response.getAuthors() != null && response.getAuthors().size() > 0) {
                            authors.addAll(response.getAuthors());
                        }
                        Article adArticle1 = new Article();
                        adArticle1.setTitle(getActivity().getString(R.string.suggested_authors));
                        adArticle1.setType("suggested_authors");
                        adArticle1.setAuthor(authors);
                        if (newPosts) {
                            newData.add(adArticle1);

                        } else {
                            contentArrayList.add(adArticle1);
                        }
                    }
                }

                if (response.getSuggested_reels() != -1) {
                    int reels_ = response.getSuggested_reels();
                    if (position == reels_) {
                        reels.clear();
                        if (response.getReels() != null && response.getReels().size() > 0) {
                            reels.addAll(response.getReels());
                        }
                        Article adArticle2 = new Article();
                        adArticle2.setTitle(getActivity().getString(R.string.suggested_reels));
                        adArticle2.setType("suggested_reels");
                        adArticle2.setReels(reels);
                        if (newPosts) {
                            newData.add(adArticle2);
                        } else {
                            contentArrayList.add(adArticle2);
                        }
                    }
                }
            }

            if (TextUtils.isEmpty(mNextPage)) {
                removeExtraLayout("last_item");
                Article lastItem = new Article();
                lastItem.setType("last_item");
                if (newPosts) {

                } else {
                    contentArrayList.add(lastItem);
                }
            }

            if (newPosts) {
                if (newData.size() > 0) {
                    new_post.setVisibility(View.VISIBLE);
                }
            }

            if (cacheArticles == null && !TextUtils.isEmpty(mNextPage))
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();

            if (fragmentVisible) {
                selectCardPosition(mArticlePosition);
                if (mArticlePosition == 1) {
                    scrollToTop();
                }
            }
            if (swipeListener != null) {
                swipeListener.muteIcon(true);
            }
        } else {

            removeExtraLayout("no_post");
            if (contentArrayList.size() == 1) {
                contentArrayList.add(new Article("", "no_post"));
            } else {

                if (response != null && response.getMeta() != null) {
                    mNextPage = response.getMeta().getNext();
                }

                if (TextUtils.isEmpty(mNextPage)) {
                    removeExtraLayout("last_item");
                    Article lastItem = new Article();
                    lastItem.setType("last_item");
                    contentArrayList.add(lastItem);

                }
            }

            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();
        }

        refreshLayout = false;
        isPagination = false;
    }

    public void loadCommunityData(ArticleResponse response) {
        loaderShow(false);
        isApiCalling = false;
        isPagination = false;
        showNoDataErrorView(false);
        refresh.setRefreshing(false);
        scrolling.setVisibility(View.GONE);

        if (contentArrayList.size() == 0) {
            contentArrayList.add(new Article("", "new"));
        }

        if (response != null && response.getArticles() != null && response.getArticles().size() > 0) {

            for (int position = 0; position < response.getArticles().size(); position++) {
                Article article = response.getArticles().get(position);
                if (prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
                    int interval = 10;
                    if (prefConfig.getAds().getInterval() != 0) {
                        interval = prefConfig.getAds().getInterval();
                    }

                    if (contentArrayList.size() != 0 && contentArrayList.size() % interval == 0 && !adFailedStatus) {
                        Log.e("ADS", "AD Added");
                        Article adArticle1 = new Article();
                        if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
                            adArticle1.setType("FB_Ad");
                        } else {
                            adArticle1.setType("G_Ad");
                        }
                        contentArrayList.add(adArticle1);
                    }
                }

                article.setFragTag(getMyTag());
                contentArrayList.add(article);

                if (response.getSuggested_authors() != -1) {
                    int author_ = response.getSuggested_authors();
                    if (position == author_) {
                        authors.clear();
                        if (response.getAuthors() != null && response.getAuthors().size() > 0) {
                            authors.addAll(response.getAuthors());
                        }
                        Article adArticle1 = new Article();
                        adArticle1.setTitle(getActivity().getString(R.string.suggested_authors));
                        adArticle1.setType("suggested_authors");
                        adArticle1.setAuthor(authors);
                        contentArrayList.add(adArticle1);
                    }
                }

                if (response.getSuggested_reels() != -1) {
                    int reels_ = response.getSuggested_reels();
                    if (position == reels_) {
                        reels.clear();
                        if (response.getReels() != null && response.getReels().size() > 0) {
                            reels.addAll(response.getReels());
                        }
                        Article adArticle2 = new Article();
                        adArticle2.setTitle(getActivity().getString(R.string.suggested_reels));
                        adArticle2.setType("suggested_reels");
                        adArticle2.setReels(reels);
                        contentArrayList.add(adArticle2);
                    }
                }
            }

            if (TextUtils.isEmpty(mNextPage)) {
                removeExtraLayout("last_item");
                Article lastItem = new Article();
                lastItem.setType("last_item");
                contentArrayList.add(lastItem);
            }

            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();

            if (fragmentVisible) {
                selectCardPosition(mArticlePosition);
            }
            if (swipeListener != null) {
                swipeListener.muteIcon(true);
            }
        } else {
            if (contentArrayList.size() == 1) {
                removeExtraLayout("no_post");
                contentArrayList.add(new Article("", "no_post"));
                if (mCardAdapter != null)
                    mCardAdapter.notifyDataSetChanged();
            }
        }

        callData(type);
    }

    public void removeExtraLayout(String layout) {
        if (contentArrayList != null && contentArrayList.size() > 0) {
            for (int position = 0; position < contentArrayList.size(); position++) {
                Article article = contentArrayList.get(position);
                if (!TextUtils.isEmpty(article.getType()) && !TextUtils.isEmpty(layout)) {
                    if (layout.equalsIgnoreCase(article.getType())) {
                        contentArrayList.remove(position);
                        if (mCardAdapter != null)
                            mCardAdapter.notifyItemChanged(position);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void successArticle(Article article) {

    }

    @Override
    public void homeSuccess(HomeResponse homeResponse, String currentPage) {

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        fragmentVisible = menuVisible;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCardAdapter != null) mCardAdapter.dismissBottomSheet();
    }

    @Override
    public void nextPosition(int position) {
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("pause");
        if (contentArrayList.size() > 0 && position < contentArrayList.size() && position > -1) {
            mArticlePosition = position;
            String type = contentArrayList.get(mArticlePosition).getType();
            if (!TextUtils.isEmpty(type) && (type.equalsIgnoreCase("suggested_reels") || type.equalsIgnoreCase("suggested_authors"))) {
                mArticlePosition++;
            }
            selectCardPosition(mArticlePosition);
            if (cardLinearLayoutManager != null)
                cardLinearLayoutManager.scrollToPositionWithOffset(mArticlePosition, 0);
            if (mCardAdapter != null)
                mCardAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void nextPositionNoScroll(int position, boolean shouldNotify) {
        RecyclerView.ViewHolder holder = mListRV.findViewHolderForAdapterPosition(position);
        int oldPos = mArticlePosition;
        if (holder instanceof LargeCardViewHolder)
            ((LargeCardViewHolder) holder).selectUnselectedItem(position, contentArrayList.get(position));
        else if (holder instanceof SmallCardViewHolder)
            ((SmallCardViewHolder) holder).selectUnselectedItem(position, contentArrayList.get(position));

        selectCardPosition(position);

        RecyclerView.ViewHolder holderOld = mListRV.findViewHolderForAdapterPosition(oldPos);
        if (holderOld instanceof SmallCardViewHolder)
            ((SmallCardViewHolder) holderOld).unselect(contentArrayList.get(oldPos));
        else if (holderOld instanceof LargeCardViewHolder)
            ((LargeCardViewHolder) holderOld).unselect(contentArrayList.get(oldPos));
        else if (holderOld instanceof VideoViewHolder)
            mCardAdapter.notifyItemChanged(oldPos);
//            ((VideoViewHolder) holderOld).pause();
        else if (holderOld instanceof YoutubeViewHolder)
            mCardAdapter.notifyItemChanged(oldPos);
//            ((YoutubeViewHolder) holderOld).bulletPause();
    }

    private void callNextPage() {
        if (presenter != null)
            presenter.getCommunityFeed(mNextPage, true);
    }

    @Override
    public void refresh() {
        contentArrayList.clear();
        mCurrPage = "1";
        mNextPage = "";
        if (mCardAdapter != null)
            mCardAdapter.notifyDataSetChanged();
        if (presenter != null)
            presenter.getCommunityFeed("", false);
    }

    public void expandCard() {
        if (swipeListener != null) swipeListener.muteIcon(false);
        mListRV.setAdapter(null);

        mCardAdapter = new CommunityFeedAdapter(new CommentClick() {

            @Override
            public void onDetailClick(int position, Article article) {
                if (!contentArrayList.get(position).isSelected()) {
                    nextPositionNoScroll(position, false);
                }
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("type", type);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {
                Intent intent = new Intent(getContext(), VideoFullScreenActivity.class);
                intent.putExtra("url", article.getLink());
                intent.putExtra("mode", mode);
                intent.putExtra("position", position);
                intent.putExtra("duration", duration);
                startActivityForResult(intent, Constants.VideoDurationRequestCode);
            }

            @Override
            public void commentClick(int position, String id) {
                if (!contentArrayList.get(position).isSelected()) {
                    nextPositionNoScroll(position, false);
                }
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
                }
                , new CommunityItemCallback() {
            @Override
            public void onItemClick(String option, Article article) {
                if (!TextUtils.isEmpty(option)) {
                    switch (option) {
                        case "YOUTUBE":
                            pauseOnlyBullets();
                            CommunityFeedFragment.this.showYoutubeUploadView(article.getId());
                            break;
                        case "REELS":
                            CommunityFeedFragment.this.Reels();
                            break;
                        case "MEDIA":
                            CommunityFeedFragment.this.Media();
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
        },
                () -> {
                    removeAdItem();
                    adFailedStatus = true;
                }, false, null, getLifecycle());
        cardLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
        mListRV.setLayoutManager(cardLinearLayoutManager);
        mListRV.setOnFlingListener(null);
        mListRV.setAdapter(mCardAdapter);

        mListRV.setCacheManager(mCardAdapter);

        mListRV.setPlayerSelector(selector);
    }

    private void removeAdItem() {
        for (int i = 0; i < contentArrayList.size(); i++) {
            if (contentArrayList.get(i).getType().equals("FB_Ad") || contentArrayList.get(i).getType().equals("G_Ad")) {
                contentArrayList.remove(i);
                mCardAdapter.notifyItemRemoved(i);
                removeAdItem();
                return;
            }
        }
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
                        .start(getContext(), CommunityFeedFragment.this);
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
        if (contentArrayList != null && position < contentArrayList.size()) {
            int pos = position;
            pos++;
            if (mListRV != null)
                mListRV.smoothScrollToPosition(pos);
        }
    }

    @Override
    public void prevArticle(int position) {
        if (position > 0) {
            int pos = position;
            pos--;
            if (mListRV != null)
                mListRV.smoothScrollToPosition(pos);
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
//                        mListRV.setPadding(0, 0, 0, viewHeight - height);
//                    }
//                }
//            });
//        }
    }

    public void showNoDataErrorView(boolean show) {
        noRecordFoundContainer.setVisibility(show ? View.INVISIBLE : View.INVISIBLE);
    }

    private int statusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    @Override
    public void authors(AuthorListResponse response) {

    }

    @Override
    public void reels(ReelResponse response) {

    }
}
