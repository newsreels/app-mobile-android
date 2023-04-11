package com.ziro.bullet.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BaseActivity;
import com.ziro.bullet.activities.SearchActivity;
import com.ziro.bullet.adapters.CommunityFeed.AuthorItemsAdapter;
import com.ziro.bullet.adapters.NewFeed.NewChannelAdapter;
import com.ziro.bullet.adapters.following.FollowingPlacesAdapter;
import com.ziro.bullet.adapters.following.FollowingTopicsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.flowlayout.FlowLayout;
import com.ziro.bullet.flowlayout.TagAdapter;
import com.ziro.bullet.flowlayout.TagFlowLayout;
import com.ziro.bullet.interfaces.SearchInterface;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.presenter.FollowUnfollowPresenter;
import com.ziro.bullet.presenter.SearchPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

import retrofit2.Response;

public class FollowingDetailActivity extends BaseActivity implements SearchInterface {
    private static String ARG_KEY = "arg_key";
    private static String ARG_SEARCH = "arg_search";

    private RelativeLayout ivBack;
    private TextView tvTitle;
    private TextView tvTitle2;
    private TextView find_more;
    private TextView save;
    private SearchView mSearchView;
    private RecyclerView rvFollowing, rvSuggested;
    private TagFlowLayout rvFollowingFlow, rvSuggestedFlow;
    private ProgressBar pbFollowing, pbSuggested;
    private RelativeLayout cardFollowing;
    private NestedScrollView nestedScrollView;
    private TextView tvSuggested;
    private TextView tvSuggested2;
    private TextView titleDesc;
    private TextView no_record;
    private RelativeLayout searchHeader;
    private RelativeLayout cardSuggested;

    private SearchPresenter mPresenter;
    private FollowUnfollowPresenter followUnfollowPresenter;

    private String mPage = "";

    private NewChannelAdapter mChannelsFollowingAdapter;
    private NewChannelAdapter mChannelsSuggestedAdapter;
    private ArrayList<Topics> mFollowedTopics = new ArrayList<>();
    private ArrayList<Topics> mSuggestedTopics = new ArrayList<>();

    private FollowingTopicsAdapter mTopicsFollowingAdapter;
    private FollowingTopicsAdapter mTopicsSuggestedAdapter;
    private ArrayList<Source> mFollowedChannels = new ArrayList<>();
    private ArrayList<Source> mSuggestedChannels = new ArrayList<>();

    private AuthorItemsAdapter mAuthorsFollowingAdapter;
    private AuthorItemsAdapter mAuthorsSuggestedAdapter;
    private ArrayList<Author> mFollowedAuthors = new ArrayList<>();
    private ArrayList<Author> mSuggestedAuthors = new ArrayList<>();

    private FollowingPlacesAdapter mPlacesFollowingAdapter;
    private FollowingPlacesAdapter mPlacesSuggestedAdapter;
    private ArrayList<Location> mFollowedLocations = new ArrayList<>();
    private ArrayList<Location> mSuggestedLocations = new ArrayList<>();

    private Handler handler = new Handler();
    private String mSearchChar = "";
    private String type;
    private Runnable input_finish_checker = this::search;
    private DbHandler cacheManager;
    private PrefConfig prefConfig;
    private TagAdapter<Topics> mTopicAdapter;
    private TagAdapter<Topics> mTopicSuggestedAdapter;

    public static void launchActivity(Activity activity, String type, String keyword) {
        Intent intent = new Intent(activity, FollowingDetailActivity.class);
        intent.putExtra(ARG_KEY, type);
        if (!TextUtils.isEmpty(keyword))
            intent.putExtra(ARG_SEARCH, keyword);
        activity.startActivity(intent);
//        FollowingDetailFragment fragment = new FollowingDetailFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_KEY, type);
//        fragment.setArguments(args);
//        return fragment;
    }

    public static void launchActivityWithResult(Context context, String type, String keyword, int requestCode) {
        Intent intent = new Intent(context, FollowingDetailActivity.class);
        intent.putExtra(ARG_KEY, type);
        if (!TextUtils.isEmpty(keyword))
            intent.putExtra(ARG_SEARCH, keyword);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    private void search() {
        if (TextUtils.isEmpty(type))
            return;
        if (type.equalsIgnoreCase("topics")) {
            tvTitle.setText(getString(R.string.topics));
            tvTitle2.setText(getString(R.string.topics));
            mPresenter.searchTopics(mSearchChar, mPage);
            pbSuggested.setVisibility(View.VISIBLE);
        } else if (type.equalsIgnoreCase("authors")) {
            tvTitle.setText(getString(R.string.authors));
            tvTitle2.setText(getString(R.string.authors));
            mPresenter.searchAuthors(mSearchChar, mPage);
            pbSuggested.setVisibility(View.VISIBLE);
        } else if (type.equalsIgnoreCase("channels")) {
            tvTitle.setText(getString(R.string.channels));
            tvTitle2.setText(getString(R.string.channels));
            mPresenter.searchChannels(mSearchChar, mPage);
            pbSuggested.setVisibility(View.VISIBLE);
        } else if (type.equalsIgnoreCase("places")) {
            tvTitle.setText(getString(R.string.location));
            tvTitle2.setText(getString(R.string.location));
            mPresenter.searchLocations(mSearchChar, mPage);
            pbSuggested.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setBlackStatusBarColor(this);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.fragment_following_detail);

        mPresenter = new SearchPresenter(this, this);
        followUnfollowPresenter = new FollowUnfollowPresenter(this);
        cacheManager = new DbHandler(this);
        prefConfig = new PrefConfig(this);
        tvTitle = findViewById(R.id.title);
        tvTitle2 = findViewById(R.id.title2);
        find_more = findViewById(R.id.find_more);
        save = findViewById(R.id.save);
        mSearchView = findViewById(R.id.svSearch);
        rvFollowing = findViewById(R.id.rvFollowing);
        rvFollowingFlow = findViewById(R.id.rvFollowingFlow);
        rvSuggested = findViewById(R.id.rvSuggested);
        rvSuggestedFlow = findViewById(R.id.rvSuggestedFlow);
        ivBack = findViewById(R.id.ivBack);
        pbFollowing = findViewById(R.id.pbFollowing);
        pbSuggested = findViewById(R.id.pbSuggested);
        cardFollowing = findViewById(R.id.cardFollowing);
        tvSuggested = findViewById(R.id.tvSuggested);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        tvSuggested2 = findViewById(R.id.tvSuggested2);
        titleDesc = findViewById(R.id.titleDesc);
        no_record = findViewById(R.id.no_record);
        searchHeader = findViewById(R.id.search_header);
        cardSuggested = findViewById(R.id.cardSuggested);

        ivBack.setOnClickListener(v -> onBackPressed());
        save.setOnClickListener(v -> onBackPressed());

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
            if (hasFocus) {
                mSearchView.setFocusable(true);
                mSearchView.requestFocus();
                mSearchView.requestFocusFromTouch();
            }
        });

        mSearchView.setOnCloseListener(() -> {
            reset();
            return true;
        });

        find_more.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("case", type);
            startActivity(intent);
        });

        rvSuggested.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.hideKeyboard(FollowingDetailActivity.this, rvSuggested);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchChar = mSearchView.getQuery().toString();
            mSearchView.setQuery(mSearchChar, false);
            cardFollowing.setVisibility(View.GONE);
            tvSuggested.setText(getString(R.string.search_results));
            search();
        } else {
            if (getIntent() != null && getIntent().hasExtra(ARG_KEY)) {
                type = getIntent().getStringExtra(ARG_KEY);
                if (getIntent().hasExtra(ARG_SEARCH)) {
                    mSearchChar = getIntent().getStringExtra(ARG_SEARCH);
                    searchHeader.setVisibility(View.GONE);
                    mSearchView.setQuery(mSearchChar, false);
                    cardFollowing.setVisibility(View.GONE);
                    tvSuggested.setText(getString(R.string.search_results));
                    search();
                } else {
                    reset();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideKeyboard(FollowingDetailActivity.this, rvSuggested);
    }

    private void clear() {
        mFollowedTopics.clear();
        mSuggestedTopics.clear();

        mFollowedChannels.clear();
        mSuggestedChannels.clear();

        mFollowedAuthors.clear();
        mSuggestedAuthors.clear();

        mFollowedLocations.clear();
        mSuggestedLocations.clear();

        no_record.setVisibility(View.GONE);
    }

    private void reset() {
        if (type == null) return;
//        clear();
//        mPage = "";
        switch (type) {
            case "channels":
                tvTitle.setText(getString(R.string.channels));
                tvTitle2.setText(getString(R.string.channels));
                mPresenter.getFollowingChannels(mPage);
                mPresenter.getSuggestedChannels(false);
                rvFollowing.setVisibility(View.VISIBLE);
                rvFollowingFlow.setVisibility(View.GONE);
                rvSuggested.setVisibility(View.VISIBLE);
                rvSuggestedFlow.setVisibility(View.GONE);
                tvSuggested.setText(getString(R.string.suggested_channels));
                titleDesc.setText(getString(R.string.we_ll_present_more_stories_from_your_selection));
                tvSuggested2.setText(getString(R.string.we_ll_present_more_stories_from_your_selection));
                nestedScrollView.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case "topics":
                tvTitle.setText(getString(R.string.topics));
                tvTitle2.setText(getString(R.string.topics));
                mPresenter.getFollowingTopics(mPage);
                mPresenter.getSuggestedTopics();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                tvSuggested.setLayoutParams(params);
                nestedScrollView.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_bg));
                tvSuggested.setText(getString(R.string.suggested_topics));

                titleDesc.setText(getString(R.string.we_ll_present_more_stories_from_your_topics));
                tvSuggested2.setText(getString(R.string.we_ll_present_more_stories_from_your_topics));
                cardFollowing.setVisibility(View.VISIBLE);
                cardSuggested.setVisibility(View.VISIBLE);
                searchHeader.setVisibility(View.GONE);
                rvFollowing.setVisibility(View.GONE);
                rvFollowingFlow.setVisibility(View.VISIBLE);
                rvSuggested.setVisibility(View.GONE);
                rvSuggestedFlow.setVisibility(View.VISIBLE);
                break;
            case "authors":
                tvTitle.setText(getString(R.string.authors));
                tvTitle2.setText(getString(R.string.authors));
                mPresenter.getFollowingAuthors(mPage);
                mPresenter.getSuggestedAuthors(false);
                rvFollowing.setVisibility(View.VISIBLE);
                rvFollowingFlow.setVisibility(View.GONE);
                rvSuggested.setVisibility(View.VISIBLE);
                rvSuggestedFlow.setVisibility(View.GONE);
                titleDesc.setText(getString(R.string.we_ll_present_more_stories_from_your_selection));
                tvSuggested2.setText(getString(R.string.we_ll_present_more_stories_from_your_selection));
                tvSuggested.setText(getString(R.string.suggested_authors));
                break;
            case "places":
                tvTitle.setText(getString(R.string.add_location));
                tvTitle2.setText(getString(R.string.add_location));
//                mPresenter.getFollowingLocations(mPage);
                mPresenter.getSuggestedLocations();

                nestedScrollView.setBackgroundColor(getResources().getColor(R.color.white));
                tvSuggested.setText(getString(R.string.suggested_location));
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params2.gravity = Gravity.CENTER_VERTICAL;
                tvSuggested.setLayoutParams(params2);
                titleDesc.setText("");
                tvSuggested2.setText("");
                cardFollowing.setVisibility(View.GONE);
                searchHeader.setVisibility(View.VISIBLE);
                cardSuggested.setVisibility(View.VISIBLE);
                rvFollowing.setVisibility(View.VISIBLE);
                rvFollowingFlow.setVisibility(View.GONE);
                rvSuggested.setVisibility(View.VISIBLE);
                rvSuggestedFlow.setVisibility(View.GONE);
                break;
        }
    }

    private void clearDb() {
        if (cacheManager != null) {
            Constants.homeDataUpdate = true;
            Constants.menuDataUpdate = true;
            Constants.reelDataUpdate = true;
            if (prefConfig != null)
                prefConfig.setAppStateHomeTabs("");
            cacheManager.clearDb();
        }
    }

    @Override
    public void loaderShow(boolean flag) {

    }

    @Override
    public void error(String error, int load) {
        pbFollowing.setVisibility(View.GONE);
        pbSuggested.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(error)) {
            if (error.equalsIgnoreCase("Canceled") || error.contains("reset") || error.contains("closed"))
                return;
            if (!isFinishing()) {
                Utils.showSnacky(getWindow().getDecorView().getRootView(), "" + error);
            }
        }
    }

    @Override
    public void onFollowedTopicsSuccess(TopicsModel response) {
        if (isFinishing())
            return;
        pbFollowing.setVisibility(View.GONE);
        mFollowedTopics.clear();
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getTopics().size() > 0) {
                cardFollowing.setVisibility(View.VISIBLE);
                mFollowedTopics.addAll(response.getTopics());

//                if (response.getTopics().size() > 2)
//                    rvFollowing.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));
//                else
//                    rvFollowing.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.HORIZONTAL, false));
//
//                mTopicsFollowingAdapter = new FollowingTopicsAdapter(FollowingDetailActivity.this, mFollowedTopics);
//                rvFollowing.setAdapter(mTopicsFollowingAdapter);

                setTopicFollowingFlow();

            } else {
                cardFollowing.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFollowedChannelsSuccess(SourceModel response) {
        if (isFinishing())
            return;
        pbFollowing.setVisibility(View.GONE);
        mFollowedChannels.clear();
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getSources().size() > 0) {
                cardFollowing.setVisibility(View.VISIBLE);
//                if (response.getSources().size() > 2) {
//                    if (response.getSources().size() < 8) {
//                        //2 recyclerview is for showing list as a grid with 2 rows and ordering in horizontal
////                        rvFollowing.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.HORIZONTAL, false));
////                        rvFollowing2.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.HORIZONTAL, false));
//
//                        rvFollowing.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));
//                        rvFollowing2.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));
//
//                        mFollowedChannels.addAll(response.getSources().subList(0, 4));
//
//                        mChannelsFollowingAdapter = new FollowingChannelsAdapter(FollowingDetailActivity.this, mFollowedChannels);
//                        rvFollowing.setAdapter(mChannelsFollowingAdapter);
//
//                        ArrayList<Source> mFollowedChannels2 = new ArrayList<>();
//                        if (response.getSources().size() == 5)
//                            mFollowedChannels2.add(response.getSources().get(4));
//                        else
//                            mFollowedChannels2 = new ArrayList<>(response.getSources().subList(4, response.getSources().size()));
//
//                        FollowingChannelsAdapter followingChannelsAdapter2 = new FollowingChannelsAdapter(FollowingDetailActivity.this, mFollowedChannels2);
//                        rvFollowing2.setAdapter(followingChannelsAdapter2);
//                    } else {
//                    mFollowedChannels.addAll(response.getSources());
//                    rvFollowing.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));
//                    mChannelsFollowingAdapter = new NewChannelAdapter(FollowingDetailActivity.this, mFollowedChannels, false);
//                    rvFollowing.setAdapter(mChannelsFollowingAdapter);
////                    }
//                } else {
                    mFollowedChannels.addAll(response.getSources());
                    rvFollowing.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.HORIZONTAL, false));
                    mChannelsFollowingAdapter = new NewChannelAdapter(FollowingDetailActivity.this, mFollowedChannels, false);
                    rvFollowing.setAdapter(mChannelsFollowingAdapter);
//                }


            } else {
                cardFollowing.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFollowedLocationSuccess(LocationModel response) {
        if (isFinishing())
            return;
        pbFollowing.setVisibility(View.GONE);
        mFollowedLocations.clear();
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getLocations().size() > 0) {

                cardFollowing.setVisibility(View.VISIBLE);
                if (response.getLocations().size() > 2) {
//                    if (response.getLocations().size() < 8) {
//                        //2 recyclerview is for showing list as a grid with 2 rows and ordering in horizontal
////                        rvFollowing.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.HORIZONTAL, false));
////                        rvFollowing2.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.HORIZONTAL, false));
//
//                        rvFollowing.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));
//                        rvFollowing2.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));
//
//                        mFollowedLocations.addAll(response.getLocations().subList(0, 4));
//
//                        mPlacesFollowingAdapter = new FollowingPlacesAdapter(FollowingDetailActivity.this, mFollowedLocations);
//                        rvFollowing.setAdapter(mPlacesFollowingAdapter);
//
//                        ArrayList<Location> mFollowedLocations2 = new ArrayList<>();
//                        if (response.getLocations().size() == 5)
//                            mFollowedLocations2.add(response.getLocations().get(4));
//                        else
//                            mFollowedLocations2 = new ArrayList<>(response.getLocations().subList(4, response.getLocations().size()));
//
//                        FollowingPlacesAdapter followingPlacesAdapter2 = new FollowingPlacesAdapter(FollowingDetailActivity.this, mFollowedLocations2);
//                        rvFollowing2.setAdapter(followingPlacesAdapter2);
//                    } else {
                    mFollowedLocations.addAll(response.getLocations());
                    rvFollowing.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));
                    mPlacesFollowingAdapter = new FollowingPlacesAdapter(FollowingDetailActivity.this, mFollowedLocations, true);
                    rvFollowing.setAdapter(mPlacesFollowingAdapter);
//                    }
                } else {
                    mFollowedLocations.addAll(response.getLocations());
                    rvFollowing.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.HORIZONTAL, false));
                    mPlacesFollowingAdapter = new FollowingPlacesAdapter(FollowingDetailActivity.this, mFollowedLocations, true);
                    rvFollowing.setAdapter(mPlacesFollowingAdapter);
                }
            } else {
                cardFollowing.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSuggestedTopicsSuccess(TopicsModel response) {
        if (isFinishing())
            return;
        pbSuggested.setVisibility(View.GONE);
        mSuggestedTopics.clear();
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getTopics().size() > 0) {
                tvSuggested.setText(getString(R.string.suggested_topics));
                mSuggestedTopics.addAll(response.getTopics());

////                if (response.getTopics().size() > 2)
//                rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 4, RecyclerView.HORIZONTAL, false));
////                else
////                    rvSuggested.setLayoutManager(new LinearLayoutManager(getContext()));
//
//                mTopicsSuggestedAdapter = new FollowingTopicsAdapter(FollowingDetailActivity.this, mSuggestedTopics);
//                rvSuggested.setAdapter(mTopicsSuggestedAdapter);

                setTopicSuggestedFlow();
            }
        }
    }

    @Override
    public void onSuggestedChannelsSuccess(SourceModel response) {
        if (isFinishing())
            return;
        pbSuggested.setVisibility(View.GONE);
        mSuggestedChannels.clear();
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getSources().size() > 0) {
                tvSuggested.setText(getString(R.string.suggested_channels));
                mSuggestedChannels.addAll(response.getSources());

//                if (response.getSources().size() > 5)
//                    rvSuggested.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
//                else
                rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));

                mChannelsSuggestedAdapter = new NewChannelAdapter(FollowingDetailActivity.this, mSuggestedChannels, false);
                rvSuggested.setAdapter(mChannelsSuggestedAdapter);
            }
        }
    }

    @Override
    public void onSuggestedLocationSuccess(LocationModel response) {
        if (isFinishing())
            return;
        pbSuggested.setVisibility(View.GONE);
        mSuggestedLocations.clear();
        if (response != null) {
            if (response.getLocations().size() > 0) {
                tvSuggested.setText(getString(R.string.suggested_location));
                mSuggestedLocations.addAll(response.getLocations());
                rvSuggested.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.VERTICAL, false));
                mPlacesSuggestedAdapter = new FollowingPlacesAdapter(FollowingDetailActivity.this, mSuggestedLocations, true);
                rvSuggested.setAdapter(mPlacesSuggestedAdapter);
            }
        }
    }

    @Override
    public void onSearchTopicsSuccess(TopicsModel response) {
        if (isFinishing())
            return;
        clear();
        pbFollowing.setVisibility(View.GONE);
        pbSuggested.setVisibility(View.GONE);
        mSuggestedTopics.clear();
        if (response != null && response.getTopics() != null) {
            tvSuggested.setText(getString(R.string.search_results));
            cardFollowing.setVisibility(View.GONE);

            if (response.getTopics().size() > 0) {
                no_record.setVisibility(View.GONE);
            } else {
                no_record.setVisibility(View.VISIBLE);
            }

            mSuggestedTopics.addAll(response.getTopics());

            if (mSuggestedTopics.size() > 0)
                if (mSuggestedTopics.size() < 10) {
                    rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.VERTICAL, false));
                } else {
                    rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 4, RecyclerView.HORIZONTAL, false));
                }
            mTopicsSuggestedAdapter = new FollowingTopicsAdapter(FollowingDetailActivity.this, mSuggestedTopics);
            rvSuggested.setAdapter(mTopicsSuggestedAdapter);

        } else {
            no_record.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchChannelsSuccess(SourceModel response) {
        if (isFinishing())
            return;
        clear();
        pbFollowing.setVisibility(View.GONE);
        pbSuggested.setVisibility(View.GONE);
        mSuggestedChannels.clear();
        if (response != null && response.getSources() != null) {
            tvSuggested.setText(getString(R.string.search_results));
            cardFollowing.setVisibility(View.GONE);

            if (response.getSources().size() > 0) {
                no_record.setVisibility(View.GONE);
            } else {
                no_record.setVisibility(View.VISIBLE);
            }

            mSuggestedChannels.addAll(response.getSources());

            if (mSuggestedChannels.size() > 0)
                if (mSuggestedChannels.size() < 10) {
                    rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.VERTICAL, false));
                } else {
                    rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 4, RecyclerView.HORIZONTAL, false));
                }

            mChannelsSuggestedAdapter = new NewChannelAdapter(FollowingDetailActivity.this, mSuggestedChannels, false);
            rvSuggested.setAdapter(mChannelsSuggestedAdapter);
        } else {
            no_record.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchLocationSuccess(LocationModel response) {
        if (isFinishing())
            return;
        clear();
        pbFollowing.setVisibility(View.GONE);
        pbSuggested.setVisibility(View.GONE);
        mSuggestedLocations.clear();
        if (response != null && response.getLocations() != null) {

            tvSuggested.setText(getString(R.string.search_results));
            cardFollowing.setVisibility(View.GONE);

            if (response.getLocations().size() > 0) {
                no_record.setVisibility(View.GONE);
            } else {
                no_record.setVisibility(View.VISIBLE);
            }

            mSuggestedLocations.addAll(response.getLocations());
            rvSuggested.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this,  RecyclerView.VERTICAL, false));

            mPlacesSuggestedAdapter = new FollowingPlacesAdapter(FollowingDetailActivity.this, mSuggestedLocations, true);
            rvSuggested.setAdapter(mPlacesSuggestedAdapter);
        } else {
            no_record.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDiscoverSuccess(Response<NewDiscoverResponse> response) {

    }

    @Override
    public void onSearchArticleSuccess(ArticleResponse response,Boolean isPagination) {

    }

    @Override
    public void onRelevantSuccess(RelevantResponse response) {

    }

    @Override
    public void onRelevantArticlesSuccess(ArticleResponse response) {

    }

    @Override
    public void onSearchAuthorsSuccess(AuthorSearchResponse response) {
        if (isFinishing())
            return;
        clear();
        pbFollowing.setVisibility(View.GONE);
        pbSuggested.setVisibility(View.GONE);
        mSuggestedAuthors.clear();
        if (response != null && response.getAuthors() != null) {
            cardFollowing.setVisibility(View.GONE);
            tvSuggested.setText(getString(R.string.search_results));

            if (response.getAuthors().size() > 0) {
                no_record.setVisibility(View.GONE);
            } else {
                no_record.setVisibility(View.VISIBLE);
            }

            mSuggestedAuthors.addAll(response.getAuthors());
            if (mSuggestedAuthors.size() > 0)
                if (mSuggestedAuthors.size() < 10) {
                    rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.VERTICAL, false));
                } else {
                    rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 4, RecyclerView.HORIZONTAL, false));
                }
            mAuthorsSuggestedAdapter = new AuthorItemsAdapter(FollowingDetailActivity.this, mSuggestedAuthors, true);
            rvSuggested.setAdapter(mAuthorsSuggestedAdapter);
        } else {
            no_record.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFollowedAuthorsSuccess(AuthorSearchResponse response) {
        if (isFinishing())
            return;
        pbFollowing.setVisibility(View.GONE);
        mFollowedAuthors.clear();
        if (response != null) {
            if (response.getAuthors() != null && response.getAuthors().size() > 0) {
                cardFollowing.setVisibility(View.VISIBLE);
                mFollowedAuthors.addAll(response.getAuthors());

                if (response.getAuthors().size() > 4)
                    rvFollowing.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 2, RecyclerView.HORIZONTAL, false));
                else
                    rvFollowing.setLayoutManager(new LinearLayoutManager(FollowingDetailActivity.this, RecyclerView.HORIZONTAL, false));

                mAuthorsFollowingAdapter = new AuthorItemsAdapter(FollowingDetailActivity.this, mFollowedAuthors, true);
                rvFollowing.setAdapter(mAuthorsFollowingAdapter);
            } else {
                cardFollowing.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onSuggestedAuthorsSuccess(AuthorSearchResponse response) {
        if (isFinishing())
            return;
        pbSuggested.setVisibility(View.GONE);
        mSuggestedAuthors.clear();
        if (response != null) {
            if (response.getAuthors() != null && response.getAuthors().size() > 0) {
                tvSuggested.setText(getString(R.string.suggested_authors));
                mSuggestedAuthors.addAll(response.getAuthors());

                rvSuggested.setLayoutManager(new GridLayoutManager(FollowingDetailActivity.this, 3, RecyclerView.HORIZONTAL, false));

                mAuthorsSuggestedAdapter = new AuthorItemsAdapter(FollowingDetailActivity.this, mSuggestedAuthors, true);
                rvSuggested.setAdapter(mAuthorsSuggestedAdapter);
            }
        }
    }

    private void setTopicFollowingFlow() {
        LayoutInflater mInflater = LayoutInflater.from(FollowingDetailActivity.this);
        mTopicAdapter = new TagAdapter<Topics>(mFollowedTopics) {
            @Override
            public View getView(FlowLayout parent, int position, Topics location) {
                View view = mInflater.inflate(R.layout.new_topic_item, rvFollowingFlow, false);
                TextView name = view.findViewById(R.id.name);
                LinearLayout item_color = view.findViewById(R.id.item_color);
                ImageView cross = view.findViewById(R.id.cross);
                name.setText("#"+location.getName());
                if (location.getName().equalsIgnoreCase("Other")) {
                    cross.setImageResource(R.drawable.white_add);
                    item_color.setBackgroundColor(
                            ContextCompat.getColor(
                                    FollowingDetailActivity.this,
                                    R.color.theme_color_1
                            )
                    );
                    name.setTextColor(
                            ContextCompat.getColor(
                                    FollowingDetailActivity.this,
                                    R.color.white
                            )
                    );
                    cross.setVisibility(View.VISIBLE);
                } else {
                    if (location.isFavorite()) {
                        item_color.setBackgroundColor(ContextCompat.getColor(FollowingDetailActivity.this, R.color.white));
                        name.setTextColor(
                                ContextCompat.getColor(
                                        FollowingDetailActivity.this, R.color.black));
                        cross.setVisibility(View.VISIBLE);
                    } else {
                        item_color.setBackgroundColor(
                                ContextCompat.getColor(
                                        FollowingDetailActivity.this,
                                        R.color.theme_color_1
                                )
                        );
                        name.setTextColor(
                                ContextCompat.getColor(
                                        FollowingDetailActivity.this,
                                        R.color.white
                                )
                        );
                        cross.setVisibility(View.GONE);
                    }
                }
                return view;
            }
        };
        rvFollowingFlow.setAdapter(mTopicAdapter);
        rvFollowingFlow.setOnTagClickListener((view, position, parent) -> {
            if (!mFollowedTopics.get(position).isFavorite()) {
                followUnfollowPresenter.followTopic(mFollowedTopics.get(position).getId(), position, (position1, flag) -> {
                    if (mFollowedTopics.size() == 0 || position1 >= mFollowedTopics.size()) {
                        return;
                    }
                    mFollowedTopics.get(position1).setFavorite(true);
                    mTopicAdapter.notifyDataChanged();
                });
            } else {
                followUnfollowPresenter.unFollowTopic(mFollowedTopics.get(position).getId(), position, (position1, flag) -> {
                    if (mFollowedTopics.size() == 0 || position1 >= mFollowedTopics.size()) {
                        return;
                    }
                    mFollowedTopics.get(position1).setFavorite(false);
                    mTopicAdapter.notifyDataChanged();
                });
            }
            return false;
        });
    }

    private void setTopicSuggestedFlow() {

        LayoutInflater mInflater = LayoutInflater.from(FollowingDetailActivity.this);
        mTopicSuggestedAdapter = new TagAdapter<Topics>(mSuggestedTopics) {
            @Override
            public View getView(FlowLayout parent, int position, Topics location) {
                View view = mInflater.inflate(R.layout.new_topic_item, rvSuggestedFlow, false);
                TextView name = view.findViewById(R.id.name);
                LinearLayout item_color = view.findViewById(R.id.item_color);
                ImageView cross = view.findViewById(R.id.cross);
                name.setText("#"+location.getName());
                if (location.getName().equalsIgnoreCase("Other")) {
                    cross.setImageResource(R.drawable.white_add);
                    item_color.setBackgroundColor(
                            ContextCompat.getColor(
                                    FollowingDetailActivity.this,
                                    R.color.theme_color_1
                            )
                    );
                    name.setTextColor(
                            ContextCompat.getColor(
                                    FollowingDetailActivity.this,
                                    R.color.white
                            )
                    );
                    cross.setVisibility(View.VISIBLE);
                } else {
                    if (location.isFavorite()) {
                        item_color.setBackgroundColor(ContextCompat.getColor(FollowingDetailActivity.this, R.color.white));
                        name.setTextColor(
                                ContextCompat.getColor(
                                        FollowingDetailActivity.this, R.color.black));
                        cross.setVisibility(View.VISIBLE);
                    } else {
                        item_color.setBackgroundColor(
                                ContextCompat.getColor(
                                        FollowingDetailActivity.this,
                                        R.color.theme_color_1
                                )
                        );
                        name.setTextColor(
                                ContextCompat.getColor(
                                        FollowingDetailActivity.this,
                                        R.color.white
                                )
                        );
                        cross.setVisibility(View.GONE);
                    }
                }
                return view;
            }
        };
        rvSuggestedFlow.setAdapter(mTopicSuggestedAdapter);
        rvSuggestedFlow.setOnTagClickListener((view, position, parent) -> {
            if (!mSuggestedTopics.get(position).isFavorite()) {
                followUnfollowPresenter.followTopic(mSuggestedTopics.get(position).getId(), position, (position1, flag) -> {
                    if (mSuggestedTopics.size() == 0 || position1 >= mSuggestedTopics.size()) {
                        return;
                    }
                    mSuggestedTopics.get(position1).setFavorite(true);
                    mTopicSuggestedAdapter.notifyDataChanged();
                });
            } else {
                followUnfollowPresenter.unFollowTopic(mSuggestedTopics.get(position).getId(), position, (position1, flag) -> {
                    if (mSuggestedTopics.size() == 0 || position1 >= mSuggestedTopics.size()) {
                        return;
                    }
                    mSuggestedTopics.get(position1).setFavorite(false);
                    mTopicSuggestedAdapter.notifyDataChanged();
                });
            }
            return false;
        });
    }

}
