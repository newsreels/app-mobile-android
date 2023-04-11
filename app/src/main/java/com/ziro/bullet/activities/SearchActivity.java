package com.ziro.bullet.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.discover.SearchPagerAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.bottomSheet.ShareBottomSheet;
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
import com.ziro.bullet.interfaces.SearchInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.UpdateCallback;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.NewDiscoverPage.DiscoverItem;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.model.News.Category;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

import retrofit2.Response;


public class SearchActivity extends BaseActivity implements SearchInterface, AdapterCallback {

    private static final String TAG = SearchActivity.class.getSimpleName();
    private ArrayList<Article> mContentArrayList = new ArrayList<>();
    private ArrayList<String> mTabTitleList = new ArrayList<>();
    private Handler handler = new Handler();
    private SearchView mSearchView;
    private ProgressBar progressBar;
    private LinearLayout mLlNoResultsFound;
    private LinearLayout mLlArticlesContainer;
    private LinearLayout tabContainer;
    private RelativeLayout ivBack;
    private RelativeLayout headerView;
    private View gradient;
    private boolean isSearching = false;

    private String mSearchChar = "";
    private Runnable input_finish_checker = this::search;
    private ArrayList<Topics> mFollowedTopics = new ArrayList<>();
    private ArrayList<Author> mAuthorArrayList = new ArrayList<>();
    private ArrayList<Topics> mSuggestedTopics = new ArrayList<>();
    private ArrayList<Source> mFollowedChannels = new ArrayList<>();
    private ArrayList<Source> mSuggestedChannels = new ArrayList<>();
    private ArrayList<Location> mFollowedLocations = new ArrayList<>();
    private ArrayList<Location> mSuggestedLocations = new ArrayList<>();

    private ShareBottomSheet shareBottomSheet;
    private TabLayout tabLayout;
    private ViewPager viewpager;
    private SearchPagerAdapter searchPagerAdapter;
    private String mPage = "";
    private String mCase = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_search_main);

        bindViews();
        init();
        setListeners();
    }


    private void bindViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewpager = findViewById(R.id.viewpager);
        gradient = findViewById(R.id.gradient);
        mSearchView = findViewById(R.id.svSearch);
        progressBar = findViewById(R.id.scrolling);
        mLlArticlesContainer = findViewById(R.id.llArticles);
        tabContainer = findViewById(R.id.tabs);
        mLlNoResultsFound = findViewById(R.id.ll_no_results);
        ivBack = findViewById(R.id.ivBack);
        headerView = findViewById(R.id.header);
    }

    public void init() {
        if (getIntent() != null) {
            mCase = getIntent().getStringExtra("case");
        }
        EditText searchEditText = mSearchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.discover_card_title_night));
        searchEditText.setHintTextColor(ContextCompat.getColor(SearchActivity.this, R.color.grey_light));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._10sdp));
        searchEditText.setTypeface(searchEditText.getTypeface(), Typeface.BOLD);
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

                        if (!isSearching) {
                            setRelevantSearchAdapter();
                        }
                        AnalyticsEvents.INSTANCE.logEvent(SearchActivity.this,
                                Events.SEARCH_ENTER);
                        handler.postDelayed(input_finish_checker, 500);
                    }
                } else if (newText.length() == 0) {
                    mSearchChar = "";
                    reset();

                }
                return false;
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {

        });

        mSearchView.setOnCloseListener(() -> {
            reset();
            return true;
        });

        ivBack.setOnClickListener(v -> {
            finish();
        });
    }

    public void reload() {
        //Resetting the audio data to avoid old article speech while loading new data
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Constants.canAudioPlay = false;
        Log.d("audiotest", "asdasdasdsa home : stop_destroy");
        reset();

        gradient.setVisibility(View.VISIBLE);

        mSearchView.setEnabled(true);
        mLlArticlesContainer.setVisibility(View.VISIBLE);
        Constants.canAudioPlay = true;

        if (!TextUtils.isEmpty(mSearchChar)) {
            setRelevantSearchAdapter();
            search();
        }

        ivBack.setVisibility(View.VISIBLE);
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
        Constants.canAudioPlay = true;

        showSearchBar();
    }

    @Override
    public void onPause() {
        Log.e("@@@@###", "onPause");
        super.onPause();
        Constants.canAudioPlay = false;
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
        isSearching = false;

        mLlNoResultsFound.setVisibility(View.GONE);
        mLlArticlesContainer.setVisibility(View.GONE);

        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", "ondestraefdfadfoy home : stop_destroy");

        mLlArticlesContainer.setVisibility(View.VISIBLE);
        if (searchPagerAdapter != null) {
            if (!TextUtils.isEmpty(mCase)) {
                if (mCase.equalsIgnoreCase("topics")) {
                    if (((SearchTopicsFragment) searchPagerAdapter.getFragment(0)) != null)
                        ((SearchTopicsFragment) searchPagerAdapter.getFragment(0)).searchQuery("");
                } else if (mCase.equalsIgnoreCase("places")) {
                    if (((SearchPlacesFragment) searchPagerAdapter.getFragment(0)) != null)
                        ((SearchPlacesFragment) searchPagerAdapter.getFragment(0)).searchQuery("");
                } else if (mCase.equalsIgnoreCase("channels")) {
                    if (((SearchChannelsFragment) searchPagerAdapter.getFragment(0)) != null)
                        ((SearchChannelsFragment) searchPagerAdapter.getFragment(0)).searchQuery("");
                } else if (mCase.equalsIgnoreCase("reels")) {
                    if (((SearchReelsFragment) searchPagerAdapter.getFragment(0)) != null)
                        ((SearchReelsFragment) searchPagerAdapter.getFragment(0)).searchQuery("");
                }
            } else {
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
    }

    private void search() {
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", "sfsgvsdvs home : stop_destroy");
        mLlNoResultsFound.setVisibility(View.GONE);

        mContentArrayList.clear();
        if (searchPagerAdapter != null) {

            if (!TextUtils.isEmpty(mCase)) {
                if (mCase.equalsIgnoreCase("topics")) {
                    if (((SearchTopicsFragment) searchPagerAdapter.getFragment(0)) != null)
                        ((SearchTopicsFragment) searchPagerAdapter.getFragment(0)).searchQuery(mSearchChar);
                } else if (mCase.equalsIgnoreCase("places")) {
                    if (((SearchPlacesFragment) searchPagerAdapter.getFragment(0)) != null)
                        ((SearchPlacesFragment) searchPagerAdapter.getFragment(0)).searchQuery(mSearchChar);
                } else if (mCase.equalsIgnoreCase("channels")) {
                    if (((SearchChannelsFragment) searchPagerAdapter.getFragment(0)) != null)
                        ((SearchChannelsFragment) searchPagerAdapter.getFragment(0)).searchQuery(mSearchChar);
                } else if (mCase.equalsIgnoreCase("reels")) {
                    if (((SearchReelsFragment) searchPagerAdapter.getFragment(0)) != null)
                        ((SearchReelsFragment) searchPagerAdapter.getFragment(0)).searchQuery(mSearchChar);
                }
            } else {
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
        progressBar.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    /*
     *
     * TOPICS
     *
     */
    @Override
    public void error(String error, int load) {
        if (!TextUtils.isEmpty(error)) {
            if (error.equalsIgnoreCase("Canceled") || error.contains("reset") || error.contains("closed"))
                return;
            Utils.showSnacky(SearchActivity.this.getWindow().getDecorView().getRootView(), "" + error);
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

    }

    @Override
    public void onDiscoverSuccess(Response<NewDiscoverResponse> response) {

    }

    private void loadDiscoverData(ArrayList<DiscoverItem> discoverItems) {

    }

    private void showBottomSheetDialog(ShareInfo shareInfo, Article article, DialogInterface.OnDismissListener onDismissListener) {
        if (shareBottomSheet == null) {
            shareBottomSheet = new ShareBottomSheet(SearchActivity.this, new ShareToMainInterface() {
                @Override
                public void removeItem(String id, int position) {

                }

                @Override
                public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

                }

                @Override
                public void unarchived() {

                }
            }, true, "");
        }
        shareBottomSheet.show(article, onDismissListener, shareInfo);
    }

    private void setRelevantSearchAdapter() {
        isSearching = true;
        initTabs();
    }

    private void initTabs() {

        mLlArticlesContainer.setVisibility(View.VISIBLE);
        tabContainer.setVisibility(View.VISIBLE);

        mTabTitleList.clear();

        if (!TextUtils.isEmpty(mCase)) {
            if (mCase.equalsIgnoreCase("topics")) {
                mTabTitleList.add(getString(R.string.topics));
            } else if (mCase.equalsIgnoreCase("places")) {
                mTabTitleList.add(getString(R.string.places));
            } else if (mCase.equalsIgnoreCase("channels")) {
                mTabTitleList.add(getString(R.string.channels));
            } else if (mCase.equalsIgnoreCase("reels")) {
                mTabTitleList.add(getString(R.string.reels));
            }
        } else {
            mTabTitleList.add(getString(R.string.all));
            mTabTitleList.add(getString(R.string.reels));
            mTabTitleList.add(getString(R.string.articles));
            mTabTitleList.add(getString(R.string.channels));
            mTabTitleList.add(getString(R.string.places));
            mTabTitleList.add(getString(R.string.topics));
        }


        if (searchPagerAdapter == null) {
            searchPagerAdapter = new SearchPagerAdapter(SearchActivity.this.getSupportFragmentManager(), mTabTitleList, mSearchChar, TYPE.FEED, new UpdateCallback() {
                @Override
                public void onUpdate() {

                }
            }, null, mCase);
            viewpager.setAdapter(searchPagerAdapter);
            viewpager.setOffscreenPageLimit(mTabTitleList.size());
            tabLayout.setupWithViewPager(viewpager);

            if (mTabTitleList.size() <= 1) {
                tabLayout.setVisibility(View.GONE);
            }
        }
    }

    public void setStatusBarColor() {
        Window window = SearchActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(SearchActivity.this, R.color.white));
    }

    /*
     *
     * ARTICLE SEARCH
     *
     */

    @Override
    public void onSearchArticleSuccess(ArticleResponse response,Boolean isPagination) {

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

    public interface OnFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void onItemClicked(TYPE type, String context, String id, String name, boolean favorite);
    }
}