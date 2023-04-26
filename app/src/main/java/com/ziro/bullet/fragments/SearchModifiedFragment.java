package com.ziro.bullet.fragments;

import static com.ziro.bullet.adapters.MainActivityAdapter.TYPE_SEARCH;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.adapters.discover.DiscoverAdapter;
import com.ziro.bullet.adapters.discover.DiscoverReelViewHolder;
import com.ziro.bullet.adapters.discover.DiscoverVideoViewHolder;
import com.ziro.bullet.adapters.discover.ImageCardViewHolder;
import com.ziro.bullet.adapters.discover.SearchPagerAdapter;
import com.ziro.bullet.adapters.discover.VideoCardViewHolder;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.ShareInfo;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.fragments.Search.SearchAllFragment;
import com.ziro.bullet.fragments.Search.SearchArticlesFragment;
import com.ziro.bullet.fragments.Search.SearchChannelsFragment;
import com.ziro.bullet.fragments.Search.SearchPlacesFragment;
import com.ziro.bullet.fragments.Search.SearchReelsFragment;
import com.ziro.bullet.fragments.Search.SearchTopicsFragment;
import com.ziro.bullet.interfaces.AdapterCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.SearchInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.UpdateCallback;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.model.News.Category;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.presenter.NewsPresenter;
import com.ziro.bullet.presenter.SearchPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.PlayerSelector;
import im.ene.toro.widget.Container;
import okhttp3.Headers;
import retrofit2.Response;

public class SearchModifiedFragment extends Fragment implements SearchInterface, AdapterCallback {
    private static final String TAG = SearchModifiedFragment.class.getSimpleName();
    private static GoHome goHomeMainActivity;
    final float MINIMUM_SCROLL_DIST = 300;
    PlayerSelector selector = PlayerSelector.DEFAULT;
    boolean isVisible = true;
    int scrollDist = 0;
    boolean moveToTrendings = false;
    boolean newData = false;
    String firstPageTimeStampOnly = null;
    private String theme = "dark";
    private ArrayList<Article> mContentArrayList = new ArrayList<>();
    private ArrayList<String> mTabTitleList = new ArrayList<>();
    private PrefConfig mPrefConfig;
    private SearchPresenter mPresenter;
    private OnFragmentInteractionListener listener;
    private Handler handler = new Handler();
    private SearchView mSearchView;
    private String discoverNextPage = "";
    private ArrayList<DiscoverItem> discoverItemList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout mLlNoResultsFound;
    private LinearLayout mLlArticlesContainer;
    private LinearLayout tabContainer;
    private RelativeLayout ivBack;
    private RelativeLayout headerView;
    private View view;
    private View gradient;
    private boolean isSearching = false;
    private boolean isDiscoverApiCalling = false;
    private String mPage = "";
    private String mCurrentType = "";
    private String mSearchChar = "";
    private ArrayList<Topics> mFollowedTopics = new ArrayList<>();
    private ArrayList<Author> mAuthorArrayList = new ArrayList<>();
    private ArrayList<Topics> mSuggestedTopics = new ArrayList<>();
    private ArrayList<Source> mFollowedChannels = new ArrayList<>();
    private ArrayList<Source> mSuggestedChannels = new ArrayList<>();
    private ArrayList<Location> mFollowedLocations = new ArrayList<>();
    private ArrayList<Location> mSuggestedLocations = new ArrayList<>();
    private int mArticlePosition = 0;
    private Container discover;
    private ProgressBar discoverProgress;
    private ViewSwitcher discoverViewSwitcher;
    private ShareBottomSheet shareBottomSheet;
    private NewsPresenter newsPresenter;
    private boolean adFailedStatus;
    private DiscoverAdapter discoverAdapter;
    private int curentYoutubePlayingPos = -1;
    private DbHandler cacheManager;
    private ArrayList<DiscoverItem> cacheDiscover;
    private SpeedyLinearLayoutManager speedyLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
    private String lastModifiedTime = "";
    private boolean isSuggestedLoaded = false;
    private TabLayout tabLayout;
    private ViewPager viewpager;
    private SearchPagerAdapter searchPagerAdapter;
    private Runnable input_finish_checker = this::search;

    public static SearchModifiedFragment newInstance(GoHome goHome1) {
        goHomeMainActivity = goHome1;
        return new SearchModifiedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_content, container, false);
        mPrefConfig = new PrefConfig(getContext());
        cacheManager = new DbHandler(getContext());

        bindViews();
        init();
        discoverAdapterInit();
        setListeners();
        setStatusBarColor();

        return view;
    }

    private void bindViews() {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewpager = view.findViewById(R.id.viewpager);
        discoverProgress = view.findViewById(R.id.discoverProgress);
        discover = view.findViewById(R.id.discover);
        gradient = view.findViewById(R.id.gradient);
        mSearchView = view.findViewById(R.id.svSearch);
        progressBar = view.findViewById(R.id.scrolling);
        mLlArticlesContainer = view.findViewById(R.id.llArticles);
        tabContainer = view.findViewById(R.id.tabs);
        mLlNoResultsFound = view.findViewById(R.id.ll_no_results);
        ivBack = view.findViewById(R.id.ivBack);
        headerView = view.findViewById(R.id.header);
        discoverViewSwitcher = view.findViewById(R.id.discover_view_switcher);
    }

    public void init() {
        newsPresenter = new NewsPresenter(getActivity(), null);
        mPresenter = new SearchPresenter(getActivity(), this);
        mCurrentType = getActivity().getString(R.string.discover);
        discoverItemList = new ArrayList<>();

        EditText searchEditText = mSearchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.discover_card_title_night));
        searchEditText.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.grey_light));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._10sdp));
        searchEditText.setTypeface(searchEditText.getTypeface(), Typeface.BOLD);
    }

    private void loadCacheData() {
        cacheDiscover = cacheManager.getDiscoverRecord();
        if (cacheDiscover != null && cacheDiscover.size() > 0) {
            loadDiscoverData(cacheDiscover);
        } else {
            if (!isDiscoverApiCalling) {
                isDiscoverApiCalling = true;
                mPresenter.getDirection(true, discoverNextPage, theme);
            }
        }
    }

    private void setListeners() {
        mSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.removeCallbacks(input_finish_checker);
                if (newText.trim().length() > 1) {
                    if (!mSearchChar.equals(newText.trim())) {
                        mSearchChar = newText.trim();
                        isSuggestedLoaded = true;
                        if (!isSearching) {
                            if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.relevant))) {
                                setRelevantSearchAdapter();
                            }
                        }
                        AnalyticsEvents.INSTANCE.logEvent(getContext(),
                                Events.SEARCH_ENTER);
                        handler.postDelayed(input_finish_checker, 500);
                    }
                } else if (newText.length() == 0) {
                    mSearchChar = "";
                    reset();

                    if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.discover))) {
                        if (!isDiscoverApiCalling) {
                            isDiscoverApiCalling = true;
                        }
                    }
                }
                return false;
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.discover))) {

                mCurrentType = getString(R.string.relevant);
                reset();

                gradient.setVisibility(View.VISIBLE);
                ivBack.setVisibility(View.VISIBLE);
                showDiscoverViewSwitcher(false);
                mSearchView.setEnabled(true);

                mLlArticlesContainer.setVisibility(View.VISIBLE);

                Constants.canAudioPlay = true;

                AnalyticsEvents.INSTANCE.logEvent(getContext(),
                        Events.SEARCH_ENTER);

                if (!TextUtils.isEmpty(mSearchChar)) {
                    setRelevantSearchAdapter();
                    isSuggestedLoaded = true;
                    search();
                }

                pauseDiscoverVideos();

                mSearchView.setFocusable(true);
                mSearchView.requestFocus();
                mSearchView.requestFocusFromTouch();

            }
        });

        mSearchView.setOnCloseListener(() -> {
            reset();
            return true;
        });

        ivBack.setOnClickListener(v -> {
            selectFirst();
        });

        discover.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isVisible && scrollDist > MINIMUM_SCROLL_DIST) {
                    hideSearchBar();
                    scrollDist = 0;
                    isVisible = false;
                } else if (!isVisible && scrollDist < -MINIMUM_SCROLL_DIST) {
                    showSearchBar();
                    scrollDist = 0;
                    isVisible = true;
                }

                if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                    scrollDist += dy;
                }

                if (discoverAdapter != null && discoverAdapter.getItems() != null && speedyLinearLayoutManager != null && mPresenter != null) {
                    if (discoverAdapter.getItems().size() - 2 <= speedyLinearLayoutManager.findLastVisibleItemPosition() && !TextUtils.isEmpty(discoverNextPage)) {
                        if (!isDiscoverApiCalling) {
                            isDiscoverApiCalling = true;
                            discoverProgress.setVisibility(View.VISIBLE);
                            mPresenter.getDirection(false, discoverNextPage, theme);
                        }
                    }
                }
            }
        });
    }

    public void reload() {
        //Resetting the audio data to avoid old article speech while loading new data
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Constants.canAudioPlay = false;
        Log.d("audiotest", "asdasdasdsa home : stop_destroy");
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("stop_destroy");

        reset();

        if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.discover))) {
            gradient.setVisibility(View.GONE);
            showDiscoverViewSwitcher(true);
            mSearchView.setEnabled(false);
            mSearchView.clearFocus();
            mSearchView.setQuery("", true);
            if (!isDiscoverApiCalling) {
                isDiscoverApiCalling = true;
                mPresenter.getDirection(true, discoverNextPage, theme);
            }
        } else if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.relevant))) {
            gradient.setVisibility(View.VISIBLE);
            showDiscoverViewSwitcher(false);
            mSearchView.setEnabled(true);
            mLlArticlesContainer.setVisibility(View.VISIBLE);
            Constants.canAudioPlay = true;

            if (!TextUtils.isEmpty(mSearchChar)) {
                setRelevantSearchAdapter();
                isSuggestedLoaded = true;
                search();
            }
        }

        if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.discover))) {
            ivBack.setVisibility(View.GONE);
        } else {
            ivBack.setVisibility(View.VISIBLE);
        }
    }

    private void showDiscoverViewSwitcher(boolean show) {
        discoverViewSwitcher.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void hideSearchBar() {
        ObjectAnimator animationNav = ObjectAnimator
                .ofFloat(headerView, View.TRANSLATION_Y, -headerView.getHeight());
        animationNav.setInterpolator(new AccelerateInterpolator(2));

        AnimatorSet set = new AnimatorSet();
        set.play(animationNav);
        set.start();
    }

    private void showSearchBar() {
        ObjectAnimator animationNav = ObjectAnimator
                .ofFloat(headerView, View.TRANSLATION_Y, 0);
        animationNav.setInterpolator(new DecelerateInterpolator(2));

        AnimatorSet set = new AnimatorSet();
        set.play(animationNav);
        set.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.articles)) || mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.relevant))) {
                Constants.canAudioPlay = true;
            }

            isVisible = true;
            scrollDist = 0;
            showSearchBar();

            startAndStopTimer(true);
        }
    }

    @Override
    public void onPause() {
        Log.e("@@@@###", "onPause");
        super.onPause();
        if (getActivity() != null) {
            if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.articles)) || mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.relevant))) {
                Constants.canAudioPlay = false;
                if (goHomeMainActivity != null)
                    goHomeMainActivity.sendAudioEvent("pause");
            } else if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.discover))) {
                PlayerSelector playerSelector = PlayerSelector.NONE;
//                discover.setPlayerSelector(playerSelector);
            }
            playAndStopYoutubeVideo(false);
            startAndStopTimer(false);
        }
    }

    private void startAndStopTimer(boolean start) {
        if (discover != null && discover.getAdapter() != null) {
            DiscoverAdapter discoverAdapter = (DiscoverAdapter) discover.getAdapter();
            if (discoverAdapter != null) {
                ArrayList<DiscoverItem> discoverItems = (ArrayList<DiscoverItem>) discoverAdapter.getItems();
                if (discoverItems != null && discoverItems.size() > 0) {
                    for (int i = 0; i < discoverItems.size(); i++) {
                        RecyclerView.ViewHolder viewHolder = discover.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof ImageCardViewHolder) {
                            ImageCardViewHolder imageCardViewHolder = (ImageCardViewHolder) viewHolder;
                            if (start) {
                                imageCardViewHolder.startAnimator();
                            } else {
                                imageCardViewHolder.stopAnimator();
                            }
                        }
                    }
                }
            }
        }
    }

    private void playAndStopYoutubeVideo(boolean play) {
//        if (discover != null && discover.getAdapter() != null) {
//            DiscoverAdapter discoverAdapter = (DiscoverAdapter) discover.getAdapter();
//            if (discoverAdapter != null) {
//                ArrayList<DiscoverItem> discoverItems = (ArrayList<DiscoverItem>) discoverAdapter.getItems();
//                if (discoverItems != null && discoverItems.size() > 0) {
//                    for (int i = 0; i < discoverItemList.size(); i++) {
//                        RecyclerView.ViewHolder viewHolder = discover.findViewHolderForAdapterPosition(i);
//                        if (viewHolder instanceof YoutubeCardViewHolder) {
//                            YoutubeCardViewHolder youtubeCardViewHolder = (YoutubeCardViewHolder) viewHolder;
//                            if (play) {
//                                youtubeCardViewHolder.playVideo(true);
//                            } else {
//                                youtubeCardViewHolder.pause();
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (getActivity() != null && mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.relevant))) {
//            if (mSearchRelevantMainAdapter != null && mContentArrayList != null && mContentArrayList.size() > 0) {
//                if (hidden) {
//                    if (!Constants.muted) {
//                        if (goHomeMainActivity != null)
//                            goHomeMainActivity.sendAudioEvent("pause");
//                    }
//                }
//            }
//        }
//        else {
//            if (hidden) {
//                PlayerSelector playerSelector = PlayerSelector.NONE;
////                discover.setPlayerSelector(playerSelector);
//            } else {
//                if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.discover))) {
////                    discover.setPlayerSelector(selector);
//                }
////                reload();
//            }
//        }
        if (hidden) {
            playAndStopYoutubeVideo(false);
        }

        if (hidden) {
            pauseDiscoverVideos();
        } else {
            if (mCurrentType.equals(getString(R.string.discover)))
                playDiscoverVideos();
        }
    }

    /**
     * pause all videos in discover list
     */
    private void pauseDiscoverVideos() {
        if (discoverAdapter != null && discoverAdapter.getItems() != null)
            for (int i = 0; i < discoverAdapter.getItems().size(); i++) {
                if (discover.findViewHolderForAdapterPosition(i) instanceof DiscoverReelViewHolder) {
                    DiscoverReelViewHolder holder = ((DiscoverReelViewHolder) discover.findViewHolderForAdapterPosition(i));
                    if (holder != null) {
                        holder.pauseVideos();
                    }
                } else if (discover.findViewHolderForAdapterPosition(i) instanceof DiscoverVideoViewHolder) {
                    DiscoverVideoViewHolder holder = ((DiscoverVideoViewHolder) discover.findViewHolderForAdapterPosition(i));
                    if (holder != null) {
                        holder.pauseVideos();
                    }
                } else if (discover.findViewHolderForAdapterPosition(i) instanceof VideoCardViewHolder) {
                    discover.setPlayerSelector(PlayerSelector.NONE);
                }
            }
    }

    /**
     * play all videos in discover list
     */
    private void playDiscoverVideos() {
        if (discoverAdapter != null && discoverAdapter.getItems() != null)
            for (int i = 0; i < discoverAdapter.getItems().size(); i++) {
                if (discover.findViewHolderForAdapterPosition(i) instanceof DiscoverReelViewHolder) {
                    DiscoverReelViewHolder holder = ((DiscoverReelViewHolder) discover.findViewHolderForAdapterPosition(i));
                    if (holder != null) {
                        holder.playVideos();
                    }
                } else if (discover.findViewHolderForAdapterPosition(i) instanceof DiscoverVideoViewHolder) {
                    DiscoverVideoViewHolder holder = ((DiscoverVideoViewHolder) discover.findViewHolderForAdapterPosition(i));
                    if (holder != null) {
                        holder.playVideos();
                    }
                } else if (discover.findViewHolderForAdapterPosition(i) instanceof VideoCardViewHolder) {
                    discover.setPlayerSelector(selector);
                }
            }
    }

    public void pauseArticles() {
        try {
//            SmallCardViewHolder holder = ((SmallCardViewHolder) mRvArticles.findViewHolderForAdapterPosition(mArticlePosition));
//            if (holder != null && holder.storiesProgressView != null) {
//                holder.storiesProgressView.pause();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearText() {
        if (mSearchView != null) {
            mSearchView.setQuery("", false);
            mSearchView.clearFocus();
        }
    }

    public void focus() {
        if (mSearchView != null) {
            mSearchView.setQuery("", false);
            mSearchView.requestFocus();
        }
    }

    public void selectFirst() {
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Constants.canAudioPlay = false;
        Log.d("audiotest", "asdasdasdsa home : stop_destroy");
        if (goHomeMainActivity != null)
            goHomeMainActivity.sendAudioEvent("stop_destroy");

        mCurrentType = getString(R.string.discover);
        reset();

        ivBack.setVisibility(View.GONE);
        gradient.setVisibility(View.GONE);
        showDiscoverViewSwitcher(true);
        mSearchView.setEnabled(false);
        mSearchView.clearFocus();
        mSearchView.setQuery("", true);

        loadCacheData();

        if (mLlNoResultsFound == null) return;
        mLlNoResultsFound.setVisibility(View.GONE);

        isVisible = true;
        scrollDist = 0;
        showSearchBar();
        playDiscoverVideos();

        mLlArticlesContainer.setVisibility(View.GONE);
        tabContainer.setVisibility(View.GONE);

        viewpager.setAdapter(null);
        searchPagerAdapter = null;
    }

    public void selectLast() {

        if (mLlNoResultsFound == null) return;
        mLlNoResultsFound.setVisibility(View.GONE);
    }

    private void reset() {
        mFollowedTopics.clear();
        mAuthorArrayList.clear();
        mFollowedChannels.clear();
        mFollowedLocations.clear();
        mSuggestedTopics.clear();
        mSuggestedChannels.clear();
        mSuggestedLocations.clear();
        mContentArrayList.clear();
        mPage = "";
        discoverNextPage = "";
        isSuggestedLoaded = false;
        isSearching = false;
        isDiscoverApiCalling = false;
        mLlNoResultsFound.setVisibility(View.GONE);
        mLlArticlesContainer.setVisibility(View.GONE);

        if (getActivity() != null && mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.relevant))) {
            Constants.articleId = "";
            Constants.speech = "";
            Constants.url = "";
            Log.d("audiotest", "ondestraefdfadfoy home : stop_destroy");
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("stop_destroy");

            mLlArticlesContainer.setVisibility(View.VISIBLE);
            if (searchPagerAdapter != null) {
                if (((SearchAllFragment) searchPagerAdapter.getFragment(0)) != null)
                    ((SearchAllFragment) searchPagerAdapter.getFragment(0)).searchQuery("");
                if (((SearchReelsFragment) searchPagerAdapter.getFragment(1)) != null)
                    ((SearchReelsFragment) searchPagerAdapter.getFragment(1)).searchQuery("");
                if (((SearchArticlesFragment) searchPagerAdapter.getFragment(2)) != null)
                    ((SearchArticlesFragment) searchPagerAdapter.getFragment(2)).searchQuery("");
                if (((SearchChannelsFragment) searchPagerAdapter.getFragment(3)) != null)
                    ((SearchChannelsFragment) searchPagerAdapter.getFragment(3)).searchQuery("");
                if (((SearchPlacesFragment) searchPagerAdapter.getFragment(4)) != null)
                    ((SearchPlacesFragment) searchPagerAdapter.getFragment(4)).searchQuery("");
                if (((SearchTopicsFragment) searchPagerAdapter.getFragment(5)) != null)
                    ((SearchTopicsFragment) searchPagerAdapter.getFragment(5)).searchQuery("");
            }
        }

        PlayerSelector playerSelector = PlayerSelector.NONE;
//        discover.setPlayerSelector(playerSelector);
    }

    private void search() {
        if (getActivity() == null)
            return;
        if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.relevant))) {
            Constants.articleId = "";
            Constants.speech = "";
            Constants.url = "";
            Log.d("audiotest", "sfsgvsdvs home : stop_destroy");
            if (goHomeMainActivity != null)
                goHomeMainActivity.sendAudioEvent("stop_destroy");
            mLlNoResultsFound.setVisibility(View.GONE);

            mPage = "";
            mContentArrayList.clear();
            if (searchPagerAdapter != null) {
                if (((SearchAllFragment) searchPagerAdapter.getFragment(0)) != null)
                    ((SearchAllFragment) searchPagerAdapter.getFragment(0)).searchQuery(mSearchChar);
                if (((SearchReelsFragment) searchPagerAdapter.getFragment(1)) != null)
                    ((SearchReelsFragment) searchPagerAdapter.getFragment(1)).searchQuery(mSearchChar);
                if (((SearchArticlesFragment) searchPagerAdapter.getFragment(2)) != null)
                    ((SearchArticlesFragment) searchPagerAdapter.getFragment(2)).searchQuery(mSearchChar);
                if (((SearchChannelsFragment) searchPagerAdapter.getFragment(3)) != null)
                    ((SearchChannelsFragment) searchPagerAdapter.getFragment(3)).searchQuery(mSearchChar);
                if (((SearchPlacesFragment) searchPagerAdapter.getFragment(4)) != null)
                    ((SearchPlacesFragment) searchPagerAdapter.getFragment(4)).searchQuery(mSearchChar);
                if (((SearchTopicsFragment) searchPagerAdapter.getFragment(5)) != null)
                    ((SearchTopicsFragment) searchPagerAdapter.getFragment(5)).searchQuery(mSearchChar);
            }
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        Log.e("####", "loaderShow " + flag);
        playAndStopYoutubeVideo(false);

        if (getActivity() != null && mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.discover))) {
            progressBar.setVisibility(View.GONE);
            if (discoverAdapter != null && discoverAdapter.getItems() != null) {
                if (discoverAdapter.getItems().size() == 0)
                    Utils.loadSkeletonLoader(discoverViewSwitcher, flag);
            }
        } else {
            progressBar.setVisibility(flag ? View.VISIBLE : View.GONE);
        }
    }

    /*
     *
     * TOPICS
     *
     */
    @Override
    public void error(String error, int load) {
        isDiscoverApiCalling = false;
        if (!TextUtils.isEmpty(error)) {
            if (error.equalsIgnoreCase("Canceled") || error.contains("reset") || error.contains("closed"))
                return;
            if (getActivity() != null) {
                Utils.showSnacky(getActivity().getWindow().getDecorView().getRootView(), "" + error);
            }
        }
    }

    @Override
    public void onFollowedTopicsSuccess(TopicsModel response) {

    }

    @Override
    public void onSuggestedTopicsSuccess(TopicsModel response) {

    }

    @Override
    public void onSearchTopicsSuccess(TopicsModel response) {

    }

    @Override
    public void onFollowedChannelsSuccess(SourceModel response) {

    }

    @Override
    public void onSuggestedChannelsSuccess(SourceModel response) {

    }

    @Override
    public void onSearchChannelsSuccess(SourceModel response) {

    }

    @Override
    public void onFollowedLocationSuccess(LocationModel response) {

    }

    @Override
    public void onSuggestedLocationSuccess(LocationModel response) {

    }

    @Override
    public void onSearchLocationSuccess(LocationModel response) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getActivity() == null) return;
        if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.discover))) {
            moveToTrendings = true;
            if (!isDiscoverApiCalling) {
                isDiscoverApiCalling = true;
                mPresenter.getDirection(false, discoverNextPage, theme);
            }
        } else if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.articles))) {
            if (newsPresenter != null && data != null && data.hasExtra("id")) {
                String id = data.getStringExtra("id");
                int position = data.getIntExtra("position", -1);
                if (!TextUtils.isEmpty(id) && position != -1) {
//                    newsPresenter.counters(id, info -> {
//                        try {
//                            RecyclerView.ViewHolder holder = mRvArticles.findViewHolderForAdapterPosition(position);
//                            if (holder != null) {
//                                if (holder instanceof SmallCardViewHolder) {
//                                    Utils.checkLikeView(getContext(),
//                                            info.getLike_count(),
//                                            info.getComment_count(),
//                                            ((SmallCardViewHolder) holder).comment_count,
//                                            ((SmallCardViewHolder) holder).like_count,
//                                            ((SmallCardViewHolder) holder).like_icon,
//                                            info.isLiked()
//                                    );
//                                }
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
                }
            }
        }
    }

    @Override
    public void onDiscoverSuccess(Response<NewDiscoverResponse> response) {
        if (response == null || response.body() == null) return;
        if (getContext() == null) return;

        Headers headers = response.headers();

        discoverProgress.setVisibility(View.GONE);
        if (TextUtils.isEmpty(discoverNextPage)) {
            if (headers != null && cacheManager != null) {
                lastModifiedTime = cacheManager.getDiscoverLastModifiedTimeById();
                firstPageTimeStampOnly = headers.get("Last-Modified");
            }
            if (TextUtils.isEmpty(firstPageTimeStampOnly) ||
                    TextUtils.isEmpty(lastModifiedTime) ||
                    !firstPageTimeStampOnly.equals(lastModifiedTime)) {
                newData = true;
            }
        }

        Log.e("DISCOVERTIME", "=================================================");
        Log.d("DISCOVERTIME", "tempLastModified = [" + firstPageTimeStampOnly + "]");
        Log.d("DISCOVERTIME", "lastModifiedTime = [" + lastModifiedTime + "]");


        isDiscoverApiCalling = false;

        if (newData) {
            if (discoverAdapter != null) {
                if (TextUtils.isEmpty(discoverNextPage)) {
                    discoverAdapter.clear();
                    discoverItemList.clear();
                    discoverAdapter.notifyDataSetChanged();
                }
                if (response.body().getDiscover() != null) {
                    discoverAdapter.updateData(response.body().getDiscover());
                    discoverItemList = discoverAdapter.getItems();
                    discoverAdapter.notifyDataSetChanged();
                }
            }

            if (response.body().getMeta() != null) {
                discoverNextPage = response.body().getMeta().getNext();
                if (TextUtils.isEmpty(discoverNextPage))
                    newData = false;
                if (cacheManager != null) {
                    cacheManager.insertDiscoverData("discover", new Gson().toJson(discoverItemList), firstPageTimeStampOnly);
                }
            }
        }

//        lastModifiedTime = firstPageTimeStampOnly;
    }

    private void loadDiscoverData(ArrayList<DiscoverItem> discoverItems) {
        if (discoverItems == null) return;
        if (getContext() == null) return;
        boolean showShimmer = false;

        if (discoverItems.size() > 0) {
            if (discoverAdapter != null) {
                discoverAdapter.clear();
                discoverItemList.clear();
                Utils.loadSkeletonLoader(discoverViewSwitcher, false);
                discoverAdapter.updateData(discoverItems);
                discoverItemList = discoverAdapter.getItems();
                discoverAdapter.notifyDataSetChanged();
            }
        }
        if (discoverAdapter != null && discoverAdapter.getItems().size() == 0) {
            showShimmer = true;
        }
        if (!isDiscoverApiCalling) {
            isDiscoverApiCalling = true;
            mPresenter.getDirection(showShimmer, discoverNextPage, theme);
        }
    }

    private void discoverAdapterInit() {

        lastModifiedTime = cacheManager.getDiscoverLastModifiedTimeById();

        mLlNoResultsFound.setVisibility(View.GONE);
        discover.setLayoutManager(speedyLinearLayoutManager);
        discover.setNestedScrollingEnabled(false);
        discoverAdapter = new DiscoverAdapter(new CommentClick() {

            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("type", TYPE_SEARCH);
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
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("article_id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }
        }, getActivity(), discoverItemList, this,
                show -> loaderShow(show),
                new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                }, height -> {
            return 0;
        }, listener);
        discover.setAdapter(discoverAdapter);
    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(getActivity(), new ShareToMainInterface() {
                @Override
                public void removeItem(String id, int position) {

                }

                @Override
                public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
                    if (listener != null) {
                        listener.onItemClicked(type, "", id, name, favorite);
                    }
                }

                @Override
                public void unarchived() {

                }
            }, true, "");
        }
        shareBottomSheet.show(article, onDismissListener, shareInfo);
    }

    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("HOMELIFE", "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnHomeFragmentInteractionListener");
        }
    }

    private void setRelevantSearchAdapter() {
        isSearching = true;
        pauseDiscoverVideos();
        initTabs();
    }

    private void initTabs() {

        mLlArticlesContainer.setVisibility(View.VISIBLE);
        tabContainer.setVisibility(View.VISIBLE);

        mTabTitleList.clear();
        mTabTitleList.add(getString(R.string.all));
        mTabTitleList.add(getString(R.string.reels));
        mTabTitleList.add(getString(R.string.articles));
        mTabTitleList.add(getString(R.string.channels));
        mTabTitleList.add(getString(R.string.places));
        mTabTitleList.add(getString(R.string.topics));

        if (getActivity() == null) return;
        if (searchPagerAdapter == null) {
            searchPagerAdapter = new SearchPagerAdapter(getActivity().getSupportFragmentManager(), mTabTitleList, mSearchChar, TYPE.FEED, new UpdateCallback() {
                @Override
                public void onUpdate() {

                }
            }, goHomeMainActivity, "");
            viewpager.setAdapter(searchPagerAdapter);
            viewpager.setOffscreenPageLimit(mTabTitleList.size());
            tabLayout.setupWithViewPager(viewpager);
        }
    }

    public void setStatusBarColor() {
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.white));
        }
    }

    private void setThemeString() {
        theme = "dark";

    }


    /*
     *
     * ARTICLE SEARCH
     *
     */

    @Override
    public void onSearchArticleSuccess(ArticleResponse response, Boolean isPagination) {

    }

    @Override
    public void onRelevantSuccess(RelevantResponse response) {
        mContentArrayList.clear();

        if (isSearching) {
            if (response != null) {
                Category category1 = new Category();
                if (response.getArticles() != null && response.getArticles().size() > 0) {
                    category1.setContents((ArrayList<Article>) response.getArticles());
                } else {
                    category1.setContents(new ArrayList<>());
                }

                if (response.getArticlePage() != null) {
                    mPage = response.getArticlePage().getNext();
                }

                mContentArrayList.addAll(category1.getContents());

                for (int i = 0; i < mContentArrayList.size(); i++) {
                    mContentArrayList.get(i).setFragTag("id_0");
                }
            }

            Log.d("shinee", "getRelevant: mContentArrayList = " + mContentArrayList.size());

            if ((mContentArrayList != null && mContentArrayList.size() > 0)) {
                mLlNoResultsFound.setVisibility(View.GONE);
                mLlArticlesContainer.setVisibility(View.VISIBLE);
                tabContainer.setVisibility(View.VISIBLE);
            } else {
                mLlNoResultsFound.setVisibility(View.VISIBLE);
                mLlArticlesContainer.setVisibility(View.GONE);
                tabContainer.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onRelevantArticlesSuccess(ArticleResponse response) {

        if (isSearching && response != null) {
            if (response.getMeta() != null) {
                mPage = response.getMeta().getNext();

                for (int i = 0; i < response.getArticles().size(); i++) {
                    response.getArticles().get(i).setFragTag("id_0");
                }

                mContentArrayList.addAll(response.getArticles());

            }
        }
    }

    @Override
    public void onSearchAuthorsSuccess(AuthorSearchResponse response) {

    }

    @Override
    public void onFollowedAuthorsSuccess(AuthorSearchResponse response) {

    }

    @Override
    public void onSuggestedAuthorsSuccess(AuthorSearchResponse response) {

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

    public boolean isDiscoverVisible() {
        return (mCurrentType.equals(getString(R.string.relevant)));
    }

    public interface OnFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void onItemClicked(TYPE type, String context, String id, String name, boolean favorite);
    }
}
