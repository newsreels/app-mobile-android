package com.ziro.bullet.fragments.Reels;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.downloader.PRDownloader;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AuthorActivity;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.activities.NotificationActivity;
import com.ziro.bullet.activities.ProfileActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ForYouReelSheet;
import com.ziro.bullet.bottomSheet.ReelViewMoreSheet;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.data.models.home.HomeModel;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.fragments.test.NetworkSpeedUtils;
import com.ziro.bullet.fragments.test.ReelFraInterface;
import com.ziro.bullet.fragments.test.ReelsNewPresenter;
import com.ziro.bullet.fragments.test.VideoAdapter;
import com.ziro.bullet.interfaces.BottomSheetInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.HomeCallback;
import com.ziro.bullet.interfaces.ShareInfoInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.VideoInterface;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.articles.MediaMeta;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.HomePresenter;
import com.ziro.bullet.presenter.LikePresenter;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.presenter.ReelPresenter;
import com.ziro.bullet.presenter.ShareBottomSheetPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.M3UParser;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReelFragment extends Fragment implements VideoInterface, M3UParser.VideoCacheListener, ReelFraInterface {
    private static final String TAG = "VideoFragment";
    private static final int INTENT_REELS_VERSION = 3424;
    private static final String KEY_FOR_YOU = "for_you";
    private static final String KEY_FOLLOWING = "following";

    private static GoHome goHome;
    private LikePresenter presenter;
    private boolean clickable = false;
    private View view;
    private ViewPager2 viewPager;
    private int prevPosition = -1;
    private int curPosition = 0;
    private VideoAdapter pagerAdapter;
    private ReelPresenter reelPresenter;
    private String mNext = PAGE_START;
    private LinearLayout noRecordFoundContainer;
    private PrefConfig prefConfig;
    private int mCurrentPosition = -1;
    private int prevReelsize = 0;
    private SwipeRefreshLayout refresh;
    private boolean isRefresh = false;
    private List<ReelsItem> videoItems = new ArrayList<>();
    private NewsPresenter newsPresenter;
    private HomePresenter homePresenter;
    private HomeModel homeModel;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private FragmentManager fm = null;
    private String page = "";
    private ViewSwitcher reelsViewSwitcher;
    private TextView tvLabel;
    private boolean isHidden = false;
    private ConstraintLayout ll_reels_info;
    private ProgressBar progbar;
    private ReelViewMoreSheet reelViewMoreSheet;
    private CardView new_post;
    private ImageView notification;
    private ImageView dots;
    private PictureLoadingDialog loadingDialog;
    private FollowUnfollowPresenter followUnfollowPresenter;
    private ReelsNewPresenter reelsNewPresenter;
    private ShareBottomSheet shareBottomSheet;
    private ShareBottomSheetPresenter shareBottomSheetPresenter;
    private ForYouReelSheet forYouReelSheet;
    private String mode = KEY_FOR_YOU;
    private String context = "";
    private final HomeCallback homeCallback = new HomeCallback() {
        @Override
        public void loaderShow(boolean flag) {

        }

        @Override
        public void error(String error) {

        }

        @Override
        public void success(HomeModel response) {
            homeModel = response;
            if (homeModel != null && homeModel.getData() != null && !homeModel.getData().isEmpty()) {
                context = homeModel.getData().get(0).getId();
                tvLabel.setText(homeModel.getData().get(0).getTitle());
                loadCacheData();
            }
        }

        @Override
        public void searchSuccess(CategoryResponse body, boolean isPagination) {

        }
    };
    private List<ReelsItem> reelsToSave = new ArrayList<>();
    private boolean isCaching = false;
    private boolean isSequenceUpdated = false;
    private boolean isHomeTabClicked = false;
    private boolean isFollowingShow = false;

    public static ReelFragment newInstance(GoHome goHome1) {
        goHome = goHome1;
        return new ReelFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, container, false);
        prefConfig = new PrefConfig(getActivity());
        bindViews(view);

        init();

//        noRecordFound();

//        mOrientationListener = new SimpleOrientationListener(getContext()) {
//
//            @Override
//            public void onSimpleOrientationChanged(int orientation) {
//                if (mCurrentPosition >= 0 && !isHidden) {
//                    currentPage = fm.findFragmentByTag("f" + mCurrentPosition);
//                    if (currentPage instanceof VideoInnerFragment) {
//                        ((VideoInnerFragment) currentPage).setOrientation(orientation);
//                    }
//                }
//            }
//        };
//        mOrientationListener.enable();

//        switch (prefConfig.getReelsType()) {
//            case Constants.REELS_FOR_YOU:
//                forYouSelect();
//                break;
//            case Constants.REELS_FOLLOWING:
//                followingSelect();
//                break;
//            default:
//                tvLabel.setText(getString(R.string.community_));
//        }

        return view;
    }

    private void noRecordFound() {
//        Utils.addNoFollowingView(getContext(), noRecordFoundContainer, v -> {
//                    forYouBottomSheet("menu");
//                }
//                , Constants.DARK);
        noRecordFoundContainer.setVisibility(View.VISIBLE);
    }

    private void forYouSelect() {
        mode = KEY_FOR_YOU;
        if (getActivity() != null) {
            tvLabel.setText(getString(R.string.for_you));
            prefConfig.setReelsType(Constants.REELS_FOR_YOU);
            Constants.onResumeReels = false;
//            reload();
        }
    }

    private void followingSelect() {
        mode = KEY_FOLLOWING;
        if (getActivity() != null) {
            tvLabel.setText(getString(R.string.following));
            prefConfig.setReelsType(Constants.REELS_FOLLOWING);
            Constants.onResumeReels = false;
//            reload();
        }
    }

    private void bindViews(View view) {
        notification = view.findViewById(R.id.notification);
        new_post = view.findViewById(R.id.new_post);
        refresh = view.findViewById(R.id.refresh);
        viewPager = view.findViewById(R.id.pager);
        reelsViewSwitcher = view.findViewById(R.id.reels_view_switcher);
        noRecordFoundContainer = view.findViewById(R.id.no_record_found_container);
        ll_reels_info = view.findViewById(R.id.constraintLayoutReel);
        tvLabel = view.findViewById(R.id.tvLabel);
        progbar = view.findViewById(R.id.progbar);
    }

    public void invalidateViews() {
        if (getActivity() == null)
            return;
        noRecordFound();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        fm = getChildFragmentManager();
        reelPresenter = new ReelPresenter(getActivity(), this);
        newsPresenter = new NewsPresenter(getActivity(), null);
        followUnfollowPresenter = new FollowUnfollowPresenter(getActivity());
        reelsNewPresenter = new ReelsNewPresenter(getActivity(), this);
        shareBottomSheetPresenter = new ShareBottomSheetPresenter(getActivity());
        homePresenter = new HomePresenter(getActivity(), homeCallback);
        homePresenter.getHome(Constants.CAT_TYPE_REELS);
        presenter = new LikePresenter(getActivity());
        pagerAdapter = new VideoAdapter(viewPager, requireContext(), this, reelsNewPresenter);
//        pagerAdapter = new VideoAdapter(viewPager, requireContext(),this,followUnfollowPresenter);
        viewPager.setAdapter(pagerAdapter);
        Constants.reelfragment = true;
//        viewPager.setOffscreenPageLimit(2);
//        viewPager.setPageTransformer(new SlowPageTransformer(viewPager));


        double availableRam = NetworkSpeedUtils.Companion.availableRam(requireContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (availableRam >= 3) {
                viewPager.setOffscreenPageLimit(5);
            } else {
                viewPager.setOffscreenPageLimit(2);
            }
        } else {
            if (availableRam >= 3) {
                viewPager.setOffscreenPageLimit(4);
            } else {
                viewPager.setOffscreenPageLimit(2);
            }
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

//
//                if (!Constants.mViewRecycled) {
                Log.e("TAGfv", "onPageSelected:true " + position);

                curPosition = position;
                if (prevPosition > -1) {
                    pagerAdapter.pausePrevPlayback(prevPosition);
                }
                pagerAdapter.resumePlayback(curPosition);
                prevPosition = curPosition;
                //next api call
                if (videoItems != null && videoItems.size() > 0 && reelPresenter != null) {
                    if (InternetCheckHelper.isConnected()) {
                        reelPresenter.event(videoItems.get(position).getId());
                        if ((videoItems.size() - curPosition) < 10 && !Objects.equals(page, mNext) && !isLastPage) {
                            reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, true, "");
                            page = mNext;
                        }
                    }
                }

                //slow the scroll by adding delay
                viewPager.setUserInputEnabled(false);
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setUserInputEnabled(true);
                        clickable = true;
                    }
                }, 350);
//                } else {
//                    Log.e("TAGfv", "onPageSelected:false " );
//                    Constants.mViewRecycled = false;
//                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                viewPager.setPageTransformer(new SlowPageTransformer(viewPager));
//                pagerAdapter.reelAnalyticsApical();

            }
        });

        ll_reels_info.setOnClickListener(view -> {
//            if (!Constants.rvmdailogopen)
            disableClick(view);
            forYouBottomSheet();

        });

        refresh.setOnRefreshListener(this::reload);

        notification.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });


    }


    private void disableClick(View view) {
        view.setEnabled(false);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 500);
    }

    private void showBottomSheet(ReelsItem reelsItem, int position) {
        if (reelsItem != null && isAdded()) {
            reelViewMoreSheet = ReelViewMoreSheet.getInstance(reelsItem, position, new ReelViewMoreSheet.OnShareListener() {
                @Override
                public void onShareClicked() {
                    share(reelsItem);
                }

                @Override
                public void onFollow(ReelsItem mReelsItem) {
                    if (mReelsItem == null) return;
                    com.ziro.bullet.data.models.sources.Source source = mReelsItem.getSource();
                    if (source != null) {
                        reelsItem.getSource().setFavorite(source.isFavorite());
                        if (reelsItem.getSource().isFavorite()) {
//                            userFollowIcon.setVisibility(View.INVISIBLE);
//                            userFollowBtn.setEnabled(false);
                        } else {
//                            userFollowIcon.setVisibility(View.VISIBLE);
//                            userFollowBtn.setEnabled(true);
                        }
                    }
                }

                @Override
                public void viewMoreDismissed() {

                }
            });
            reelViewMoreSheet.show(getChildFragmentManager(), reelsItem.getId());

            reelViewMoreSheet.getViewLifecycleOwnerLiveData().observe(this, new Observer<LifecycleOwner>() {
                @Override
                public void onChanged(LifecycleOwner lifecycleOwner) {
                    //when lifecycleOwner is null fragment is destroyed
                    Log.e("rvm", "onChanged:F ");
                    Constants.reelfragment = true;
                    if (lifecycleOwner == null) {
                        Constants.rvmdailogopen = false;
                        Constants.onResumeReels = true;
                        onResume();
                    } else {
                        Constants.rvmdailogopen = true;
                    }
                }
            });
        }
    }

    public void forYouBottomSheet() {
        if (homeModel == null || homeModel.getData().isEmpty()) {
            Utils.showSnacky(getView(), "Loading Categories...");
            return;
        }
        Constants.onResumeReels = false;
        Constants.rvmdailogopen = true;

        onPause();

        forYouReelSheet = ForYouReelSheet.getInstance("reels", Constants.CAT_TYPE_REELS, context, homeModel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }, new BottomSheetInterface() {
            @Override
            public void onForYouSelect() {
                if (forYouReelSheet != null) {
                    forYouReelSheet.dismiss();
                }
            }

            @Override
            public void onFollowingSelect() {
                if (forYouReelSheet != null) {
                    forYouReelSheet.dismiss();
                }
            }

            @Override
            public void onHomeTab(DataItem item) {
                isHomeTabClicked = true;
                context = item.getId();
                if (forYouReelSheet != null) {
                    forYouReelSheet.dismiss();
                }
                tvLabel.setText(item.getTitle());
                refresh.setRefreshing(true);
                reload();
            }

            @Override
            public void onSearch() {
                if (goHome != null && forYouReelSheet != null) {
                    goHome.sendAudioEvent("search");
                    forYouReelSheet.dismiss();
                }
            }

            @Override
            public void onSequenceUpdated(ArrayList<DataItem> mCategoriesList) {
                isSequenceUpdated = true;
                homeModel.setData(mCategoriesList);
                List<String> catIdsList = new ArrayList<>();
                if (homeModel.getData() != null && homeModel.getData().size() > 0) {
                    for (DataItem dataItem : homeModel.getData()) {
                        if (dataItem.isLocked() || dataItem.isFollowed()) {
                            catIdsList.add(dataItem.getRawId());
                        }
                    }
                    homePresenter.updateArticleCatSequence(catIdsList, Constants.CAT_TYPE_REELS);
                }
            }

            @Override
            public void updateTabs(ArrayList<DataItem> mCategoriesList) {

            }

            @Override
            public void dialogDismissListener() {
                if (isSequenceUpdated) {
                    isSequenceUpdated = false;
                    if (!isHomeTabClicked) {
                        tvLabel.setText(homeModel.getData().get(0).getTitle());
                        context = homeModel.getData().get(0).getId();
                        reload();
                    }
                }
            }
        });

        forYouReelSheet.show(getChildFragmentManager(), "id");
        isHomeTabClicked = false;

        forYouReelSheet.getViewLifecycleOwnerLiveData().observe(getViewLifecycleOwner(), lifecycleOwner -> {
            //when lifecycleOwner is null fragment is destroyed

//            Constants.fromfragment = true;
            if (lifecycleOwner == null) {
                Constants.rvmdailogopen = false;
                Constants.onResumeReels = true;
                Constants.notishare = false;
                onResume();
            } else {
                Constants.rvmdailogopen = true;
                Constants.notishare = true;
            }
        });
    }

    private void showLoadingView(boolean isShow) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = new PictureLoadingDialog(getActivity());
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    public void pause() {
        Constants.onResumeReels = false;
        onPause();
    }

    public void reelpause() {
        Log.e("TAGfv", "reelpause: ");
//        Log.e("rvm3", "pause:F");
//        Constants.notishare = true;
//        Constants.onResumeReels = false;
//        if (exoPlayer != null) {
//            exoPlayer.setPlayWhenReady(false);
//        }
        onPause();
    }

    public void resume() {
        Log.e("TAGfv", "resume: ");
        Constants.onResumeReels = true;
        onResume();
//        Log.e("rvm3", "resume:F e=rvm dailogopen " + Constants.rvmdailogopen);
//        Constants.onResumeReels = true;
//        if (exoPlayer != null) {
//            Log.e("rvm3", "resume: ");
//            exoPlayer.setPlayWhenReady(true);
//        }
//        if (!Constants.rvmdailogopen) {
//            onResume();
//        }

    }


    private void loadCacheData() {
        Log.e("TAGfv", "loadCacheData: ");

//        loaderShow(true);
        progbar.setVisibility(View.VISIBLE);
        reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, false, "");
    }

    public void showNoDataErrorView(boolean show) {
        noRecordFoundContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void showErrorView(boolean show) {
        noRecordFoundContainer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void showEndOfReelMsg(boolean show) {
        ll_reels_info.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("TAGfv", "onHiddenChanged: ");

        if (hidden) {
            onPause();
        } else {
            onResume();
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            progbar.setVisibility(View.VISIBLE);
        } else {
            progbar.setVisibility(View.GONE);
        }


        if (flag) isLoading = flag;

//        if (flag && refresh.isRefreshing()) {
//            viewPager.setUserInputEnabled(false);
//            if (currentFragment instanceof VideoInnerFragment) {
//                ((VideoInnerFragment) currentFragment).clickEnabling(false);
//            }
//        } else {
//            viewPager.setUserInputEnabled(true);
//            if (currentFragment instanceof VideoInnerFragment) {
//                ((VideoInnerFragment) currentFragment).clickEnabling(true);
//            }
//        }
//        if (mNext.equals("")) {
//        if (mNext != null && mNext.equals("") && !refresh.isRefreshing()) {
////        if (videoItems == null || videoItems.size() <= 0) {
//            Utils.loadSkeletonLoaderReels(reelsViewSwitcher, flag);
//        } else {
//            Utils.loadSkeletonLoaderReels(reelsViewSwitcher, false);
//        }

//        if (!flag) {
//            refresh.setRefreshing(false);
//        }
    }

    @Override
    public void error(String error) {
        Log.e("TAGfv", "error : ");

        if (videoItems == null || videoItems.isEmpty()) {
            if (pagerAdapter != null) {
                pagerAdapter.pauseCurPlayback(curPosition);
            }
            page = "";
            showErrorView(true);
            progbar.setVisibility(View.GONE);
            isLoading = false;
            Utils.loadSkeletonLoaderReels(reelsViewSwitcher, false);
            if (!TextUtils.isEmpty(error)) {
                if (error.equalsIgnoreCase("Canceled") || error.contains("reset") || error.contains("closed"))
                    return;
                if (getActivity() != null) {
                    Utils.showSnacky(getActivity().getWindow().getDecorView().getRootView(), "" + error);
                }
            }
        }
    }

    @Override
    public void error404(String error) {
        showErrorView(true);
        isLoading = false;
        page = "";
        Utils.loadSkeletonLoaderReels(reelsViewSwitcher, false);
    }

    @Override
    public void success(ReelResponse reelResponse, boolean reload) {
        Log.e("TAGfv", "success: " + curPosition);
        progbar.setVisibility(View.GONE);
        isLoading = false;
        Constants.onResumeReels = true;
        if (isRefresh) {
            videoItems.clear();
            viewPager.setAdapter(pagerAdapter);
            pagerAdapter.notifyDataSetChanged();
            isRefresh = false;
        }

        showNoDataErrorView(false);
        refresh.setRefreshing(false);
        if (reelResponse != null && reelResponse.getReels() != null && reelResponse.getReels().size() > 0 && pagerAdapter != null) {
            if (videoItems.size() <= 0) {
                videoItems.addAll(0, reelResponse.getReels());
                pagerAdapter.setVideoList((ArrayList<ReelsItem>) videoItems);
            } else if (!mNext.equals(reelResponse.getMeta().getNext())) {
                prevReelsize = videoItems.size();
                videoItems.addAll(prevReelsize, (reelResponse.getReels()));
                pagerAdapter.notifyItemRangeInserted(prevReelsize, reelResponse.getReels().size());
                prevReelsize = videoItems.size();
            }
        }

        if (reelResponse.getMeta() != null) {
            mNext = reelResponse.getMeta().getNext();
            if (TextUtils.isEmpty(mNext)) {
                isLastPage = true;
//                videoItems.add(null);
            }
        }

        if (reelResponse != null && !isLastPage) {
            if (reelResponse.getReels() != null) {
                if (reelResponse.getReels().size() < 5 && (videoItems.size() - curPosition) <= 5) {
                    reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, true, "");
                }
            }
        }

        if (videoItems.size() == 0) {
            showNoDataErrorView(true);
        }
    }

    @Override
    public void nextVideo(int position) {
//        if (position > -1) {
//            int next = position;
//            next++;
//            if (next < videoItems.size()) {
//                viewPager.setCurrentItem(next, true);
//            }
//        }
    }

    public void scrollToTop() {
//        new_post.setVisibility(View.GONE);
//        if (viewPager.getCurrentItem() == 0) {
//            refresh.setRefreshing(true);
//            viewPager.setAdapter(null);
//            videoReelsItems.clear();
//            reelsToSave.clear();
//            cachePosition = -1;
//            maxCacheLimit = -1;
//            isCaching = false;
//            mNext = "";
//            page = "";
//            PRDownloader.cancelAll();
//            pagerAdapter.notifyDataSetChanged();
//            reload();
//            loaderShow(true);
//        } else {
//            viewPager.setCurrentItem(0, false);
//        }
    }

    public void reload() {
        Log.e("TAGfv", "reload: ");

        if (!InternetCheckHelper.isConnected()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
            refresh.setRefreshing(false);
            return;
        }

//            refresh.setRefreshing(true);
//            currentAdapterPosition = viewPager.getCurrentItem();
//            viewPager.setCurrentItem(0, true);

        viewPager.setAdapter(null);
//            viewPager.removeAllViews();
        videoItems.clear();
        reelsToSave.clear();
        isCaching = false;
        mNext = "";
        PRDownloader.cancelAll();
//            pagerAdapter.notifyDataSetChanged();
        isRefresh = true;
        mNext = "";
        page = "";
        new_post.setVisibility(View.GONE);
        if (homeModel != null) {
            reelPresenter.getReelsHome(prefConfig.getReelsType(), context, mNext, false, true, false, "");
        } else {
            homePresenter.getHome(Constants.CAT_TYPE_REELS);
        }

//            loadCacheData();//api cal
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REELS_VERSION) {
            if (resultCode == Activity.RESULT_OK) {
                Constants.onResumeReels = false;
                reload();
            }
        } else if (newsPresenter != null && data != null && data.hasExtra("id")) {
            String id = data.getStringExtra("id");
        }
    }

    @Override
    public void onDestroy() {
        Log.e("TAGfv", "onDestroy: ");

        Log.e("testreel", "onDestroy ");
        super.onDestroy();
        pagerAdapter.releasePlayers();

    }

    @Override
    public void onPause() {
        if (prefConfig != null) {
            prefConfig.setReelTime(System.currentTimeMillis());
        }
        Constants.onResumeReels = false;
        super.onPause();
        pagerAdapter.pauseCurPlayback(curPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pagerAdapter.releasePlayers();

    }

    @Override
    public void onResume() {
        Log.e("TAGfv", "onResume: ");

        super.onResume();
        //shifa check this
        if (Constants.onResumeReels && Constants.HomeSelectedFragment == Constants.BOTTOM_TAB_VIDEO && Constants.sharePgNotVisible) {
            {
                if (reelViewMoreSheet == null) {
                    pagerAdapter.resumePlayback(curPosition);
                } else if (reelViewMoreSheet != null && !reelViewMoreSheet.isVisible()) {
                    pagerAdapter.resumePlayback(curPosition);
                }
            }
        }


    }

    @Override
    public void videoCached(ReelsItem reelsItem, int position) {

    }


    @Override
    public void followChannelClick(@NonNull ReelsItem reelsItem) {
        if (!InternetCheckHelper.isConnected()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (reelsItem.getSource() != null) {
            if (reelsItem.getSource().isFavorite()) {
                followUnfollowPresenter.unFollowSource(reelsItem.getSource().getId(), 0, null);
                reelsItem.getSource().setFavorite(false);
            } else {
                followUnfollowPresenter.followSource(reelsItem.getSource().getId(), 0, null);
                reelsItem.getSource().setFavorite(true);
            }
        }

//        if (videoItems != null && !videoItems.isEmpty()) {
//            for (int i = 0; i < videoItems.size(); i++) {
//                if (videoItems.get(i).getSource().getName().equals(reelsItem.getSource().getName())) {
//                    if (reelsItem.getSource().isFavorite()) {
//                        videoItems.get(i).getSource().setFavorite(true);
//                        pagerAdapter.notifyItemChanged(i);
//                    }
//                }
//            }
//        }

    }

    @Override
    public void likeicon(@NonNull ReelsItem reelsItem) {
        Map<String, String> params = new HashMap<>();
        params.put(Events.KEYS.REEL_ID, reelsItem.getId());
        AnalyticsEvents.INSTANCE.logEvent(getContext(), params, Events.REELS_LIKE);
    }

    @Override
    public void loadingReelInfo(boolean isLoading) {

    }

    @Override
    public void errorr(@NonNull String error, @NonNull String topic) {

    }

    @Override
    public void commentsclick(@NonNull ReelsItem reelsItem) {
        if (!InternetCheckHelper.isConnected()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (getActivity() != null) {
            Map<String, String> params = new HashMap<>();
            params.put(Events.KEYS.REEL_ID, reelsItem.getId());
            AnalyticsEvents.INSTANCE.logEvent(getContext(),
                    params,
                    Events.REELS_COMMENT);
            Intent intent = new Intent(getActivity(), CommentsActivity.class);
            intent.putExtra("article_id", reelsItem.getId());
            intent.putExtra("position", mCurrentPosition);
            startActivityForResult(intent, Constants.CommentsRequestCode);
        }

    }

    @Override
    public void share(@NonNull ReelsItem reelsItem) {
        if (!InternetCheckHelper.isConnected()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (getContext() == null) return;
        if (shareBottomSheetPresenter != null) {
            final boolean[] isIgnoreDismiss = {false};
            showLoadingView(true);
            onPause();
            shareBottomSheetPresenter.share_msg(reelsItem.getId(), new ShareInfoInterface() {
                @Override
                public void response(ShareInfo shareInfo) {
                    showLoadingView(false);
                    onPause();
//                        share_icon.setVisibility(View.VISIBLE);
                    if (shareInfo == null) {
                        return;
                    }

                    AnalyticsEvents.INSTANCE.logEvent(getContext(), Events.REEL_SHARE_CLICK);

                    Article article = new Article();
                    if (reelsItem != null) {
                        MediaMeta mediaMeta = reelsItem.getMediaMeta();
                        article.setMediaMeta(mediaMeta);
                    }
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    if (shareInfo != null) {
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shareInfo.getShare_message());
                    }
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    getActivity().startActivity(shareIntent);

//                    reelsBottomSheet = new MediaShare.Builder(getActivity())
//                            .setId(reelsItem.getId())
//                            .isArticle(false)
//                            .setShareInfo(shareInfo)
//                            .setFragmentContextVal(VideoInnerFragment.this)
//                            .setArticle(article)
//                            .setonDismissListener(dialog -> {
//                                if (!isIgnoreDismiss[0])
//                                    onResume();
//                            })
//                            .setReelBottomSheetCallback(new ReelBottomSheetCallback() {
//                                @Override
//                                public void onReport() {
//                                    isIgnoreDismiss[0] = true;
//                                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
//                                            Events.REPORT_CLICK);
//                                    if (reelsItem == null) {
//                                        return;
//                                    }
//                                    ReportBottomSheet reportBottomSheet = new ReportBottomSheet(getActivity(), new DismissBottomSheet() {
//                                        @Override
//                                        public void dismiss(boolean flag) {
//                                            if (flag) {
//                                                // on hide Bottom sheet
//                                                onResume();
//                                            }
//                                        }
//                                    });
//                                    reportBottomSheet.show(reelsItem.getId(), "articles");
//                                }
//
//                                @Override
//                                public void onSave    () {
//                                    if (!TextUtils.isEmpty(reelsItem.getId())) {
//                                        if (sharePresenter != null)
//                                            sharePresenter.archive(reelsItem.getId(), shareInfo.isArticle_archived());
//                                    }
//                                    onResume();
//                                }
//
//                                @Override
//                                public void onNotInterested() {
//                                    if (!TextUtils.isEmpty(reelsItem.getId())) {
//                                        if (sharePresenter != null)
//                                            sharePresenter.less(reelsItem.getId());
//                                    }
//                                    onResume();
//                                }
//
//                                @Override
//                                public void onIgnore() {
//                                    isIgnoreDismiss[0] = true;
//                                }
//                            })
//                            .build();
//                    reelsBottomSheet.show();

                }

                @Override
                public void error(String error) {
                    showLoadingView(false);
                    onResume();
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

    private void updateCaption() {
        if (prefConfig.isReelsCaption()) {
        }
    }

    @Override
    public void dotsCkickOpen(@NonNull ReelsItem reelsItem) {
        if (shareBottomSheetPresenter != null) {
            shareBottomSheetPresenter.share_msg(getArticleFromReels(reelsItem).getId(), new ShareInfoInterface() {
                @Override
                public void response(ShareInfo shareInfo) {
                    onPause();
                    if (shareBottomSheet == null) {
                        shareBottomSheet = new ShareBottomSheet(getActivity(), new ShareToMainInterface() {
                            @Override
                            public void removeItem(String id, int position) {

                            }

                            @Override
                            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

                            }

                            @Override
                            public void unarchived() {
                                // for updating caption
                                updateCaption();
                            }
                        }, true, "REEL_MAIN");
                    }

                    shareBottomSheet.show(getArticleFromReels(reelsItem), new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Constants.onResumeReels = true;
                            Constants.sharePgNotVisible = true;
                            onResume();
                        }
                    }, shareInfo);
                }

                @Override
                public void error(String error) {

                }
            });
        }
    }


    @Override
    public void openReelviewmore(@NonNull ReelsItem reelsItem) {
        if (clickable) {
            onPause();
            if (reelsItem != null) {
                if (reelViewMoreSheet != null) {
                    if (!reelViewMoreSheet.isVisible()) {
                        showBottomSheet(reelsItem, mCurrentPosition);
                    }
                } else {
                    showBottomSheet(reelsItem, mCurrentPosition);
                }

            }

        }
    }

    @Override
    public void doubleClickLike(@NonNull ReelsItem reelsItem) {

    }


    @Override
    public void userIconClick(@NonNull ReelsItem reelsItem) {
        if (!InternetCheckHelper.isConnected()) {
            if (getContext() != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
            return;
        }

        Intent intent;
        if (reelsItem.getSource() != null) {
            intent = new Intent(getContext(), ChannelDetailsActivity.class);
            intent.putExtra("id", reelsItem.getSource().getId());
        } else {
            User user = new PrefConfig(getContext()).isUserObject();
            if (user != null && !TextUtils.isEmpty(user.getId()) && user.getId().equalsIgnoreCase(reelsItem.getAuthor().get(0).getId())) {
                intent = new Intent(getContext(), ProfileActivity.class);
            } else {
                intent = new Intent(getContext(), AuthorActivity.class);
            }
            intent.putExtra("authorID", reelsItem.getAuthor().get(0).getId());
            intent.putExtra("authorContext", reelsItem.getAuthor().get(0).getContext());
        }
        startActivityForResult(intent, ChannelDetailsActivity.FOLLOW_REQUEST);
        // getActivity().startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.enter_500, R.anim.exit_500);
    }

    @Override
    public void nextReelVideo(int pos) {
        Log.e("TAGf", "nextReelVideo: ");
        if (pos > -1) {
            int next = pos;
            next++;
            if (next < videoItems.size()) {
                viewPager.setCurrentItem(next, true);
            }
        }
    }

    @Override
    public void updateView() {

        if (reelViewMoreSheet != null && reelViewMoreSheet.isVisible()) {
            Log.e(TAG, "updateView: ");
            pagerAdapter.pauseCurPlayback(curPosition);
        }

    }
}

