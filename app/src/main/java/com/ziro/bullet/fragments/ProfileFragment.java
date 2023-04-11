package com.ziro.bullet.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AccSettingActivity;
import com.ziro.bullet.activities.EditionActivity;
import com.ziro.bullet.activities.MainActivityNew;
import com.ziro.bullet.adapters.EditionsAdapter;
import com.ziro.bullet.adapters.ManageLocationAdapter;
import com.ziro.bullet.adapters.SearchModiSourceAdapter;
import com.ziro.bullet.adapters.SearchTopicsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.interfaces.EditionInterface;
import com.ziro.bullet.interfaces.SearchInterface;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.Edition.EditionsItem;
import com.ziro.bullet.model.Edition.ResponseEdition;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.presenter.EditionPresenter;
import com.ziro.bullet.presenter.SearchPresenter;
import com.ziro.bullet.utills.SpacesItemDecoration;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

import retrofit2.Response;

import static com.ziro.bullet.activities.MainActivityNew.*;
//import static com.ziro.bullet.activities.MainActivityNew.RESULT_INTENT_CHANGE_THEME;
import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

public class ProfileFragment extends Fragment implements SearchInterface, EditionInterface {
    private static final String TAG = "ProfileFragment";

    private PrefConfig mPrefConfig;
    private SearchPresenter mPresenter;
    private EditionPresenter mEditionsPresenter;
    private OnFragmentInteractionListener listener;

    private LinearLayout space;
    private TabLayout mTabLayout;
    private RecyclerView mRecyclerView, mRvSuggested;
    private RelativeLayout progressBar;
    private TextView mTvProgress;
    private View mDivider;
    private TextView mLabel, mLabelFollowed;
    private NestedScrollView mNestedScrollView;
    private LinearLayout llSavedArticles;
    private TextView tvEmail;
    private TextView mTvEditionContent;
    private EditText search;
    private TextView tvSavedArticleLabel;
    private ImageView ivSavedArticle;
    private View view;
    private ImageView accountSettings;
    private TextView header;


    private boolean isSuggestedLoaded = false;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String mPage = "";
    private String mCurrentType = "";
    private String searchStr = "";
    private SearchTopicsAdapter mSearchTopicsAdapter;
    private SearchTopicsAdapter mSuggestedTopicsAdapter;
    private SearchModiSourceAdapter mSearchChannelsAdapter;
    private SearchModiSourceAdapter mSuggestedChannelsAdapter;
    private ManageLocationAdapter mSearchLocationAdapter;
    private ManageLocationAdapter mSuggestedLocationAdapter;
    private EditionsAdapter mEditionsAdapter;
    private Handler handler = new Handler();

    private ArrayList<Topics> mFollowedTopics = new ArrayList<>();
    private ArrayList<Topics> mSuggestedTopics = new ArrayList<>();
    private ArrayList<Source> mFollowedChannels = new ArrayList<>();
    private ArrayList<Source> mSuggestedChannels = new ArrayList<>();
    private ArrayList<Location> mFollowedLocations = new ArrayList<>();
    private ArrayList<Location> mSuggestedLocations = new ArrayList<>();
    private ArrayList<EditionsItem> editions = new ArrayList<>();

    private long delay = 500; // 1 seconds after user stops typing
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            ApiClient.cancelAll();
            editions.clear();
            mEditionsAdapter.notifyDataSetChanged();
            mEditionsPresenter.getEdition(searchStr, PAGE_START);
        }
    };
    private ImageView loader_gif;
    private SpacesItemDecoration spaces;
    private SpacesItemDecoration spacesChannel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mPrefConfig = new PrefConfig(getContext());
        bindViews();
        init();
        setListeners();
        return view;
    }

    private void bindViews() {
        space = view.findViewById(R.id.space);
        mTabLayout = view.findViewById(R.id.tabLayout);
        mRecyclerView = view.findViewById(R.id.epoxyRecyclerView);
        progressBar = view.findViewById(R.id.progress);
        mTvProgress = view.findViewById(R.id.textView);
        mRvSuggested = view.findViewById(R.id.rvSuggested);
        mDivider = view.findViewById(R.id.view);
        mLabel = view.findViewById(R.id.title);
        mLabelFollowed = view.findViewById(R.id.titleFollowed);
        mNestedScrollView = view.findViewById(R.id.scrollView);
        llSavedArticles = view.findViewById(R.id.llSavedArticles);
        tvEmail = view.findViewById(R.id.tvEmail);
        mTvEditionContent = view.findViewById(R.id.content);
        search = view.findViewById(R.id.search);
        tvSavedArticleLabel = view.findViewById(R.id.tvSavedArticleLabel);
        ivSavedArticle = view.findViewById(R.id.ivSavedArticle);
        loader_gif = view.findViewById(R.id.loader);
        accountSettings = view.findViewById(R.id.account_settings);
        header = view.findViewById(R.id.header);

//        Glide.with(getContext())
//                .load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme()))
//                .into(loader_gif);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mPrefConfig.getUserEmail())) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(mPrefConfig.getUserEmail());
        } else {
            tvEmail.setVisibility(View.GONE);
        }

    }

    public void invalidateViews() {
        if (getActivity() == null)
            return;
        mTabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), R.color.tabbar_text_unselected), ContextCompat.getColor(getActivity(), R.color.static_tabbar_text_selected_new));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.static_tabbar_text_selected));
        tvEmail.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_category_text_color));
        tvSavedArticleLabel.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_category_text_color));
        search.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.splash_text));
        mLabel.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_category_text_color));
        mLabelFollowed.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_category_text_color));
        ivSavedArticle.setColorFilter(ContextCompat.getColor(getActivity(), R.color.main_category_text_color), android.graphics.PorterDuff.Mode.SRC_IN);
        view.findViewById(R.id.root).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        header.setTextColor(ContextCompat.getColor(getActivity(), R.color.main_category_text_color));
        llSavedArticles.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.round_green));
        final ColorStateList green = ColorStateList.valueOf(getResources().getColor(R.color.primaryRed));
        search.setBackgroundTintList(green);
        if (mSearchTopicsAdapter != null) mSearchTopicsAdapter.notifyDataSetChanged();
        if (mSuggestedTopicsAdapter != null) mSuggestedTopicsAdapter.notifyDataSetChanged();
        if (mSearchChannelsAdapter != null) mSearchChannelsAdapter.notifyDataSetChanged();
        if (mSuggestedChannelsAdapter != null) mSuggestedChannelsAdapter.notifyDataSetChanged();
        if (mSearchLocationAdapter != null) mSearchLocationAdapter.notifyDataSetChanged();
        if (mSuggestedLocationAdapter != null) mSuggestedLocationAdapter.notifyDataSetChanged();


//        Glide.with(getContext())
//                .load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme()))
//                .into(loader_gif);
    }

    public void init() {
        spaces =  new SpacesItemDecoration(20);
        spacesChannel =  new SpacesItemDecoration(18);
        mPresenter = new SearchPresenter(getActivity(), this);
        mEditionsPresenter = new EditionPresenter(getActivity(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.clearOnScrollListeners();

        if (!TextUtils.isEmpty(mPrefConfig.getUserEmail())) {
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(mPrefConfig.getUserEmail());
        } else {
            tvEmail.setVisibility(View.GONE);
        }

        loadTopics();

        llSavedArticles.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemClicked(TYPE.ARCHIVE, "", getString(R.string.saved_articles), false);
        });

//        search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                handler.removeCallbacks(input_finish_checker);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.e("TECT", "--> " + s.toString());
//                handler.removeCallbacks(input_finish_checker);
//                if (s.length() > 0) {
//                    searchStr = s.toString();
//                    handler.postDelayed(input_finish_checker, delay);
//                } else if (s.length() == 0) {
////                    ApiClient.cancelAll();
//                    editions.clear();
//                    if (mEditionsAdapter != null)
//                        mEditionsAdapter.notifyDataSetChanged();
//                    searchStr = "";
//                    mPage = PAGE_START;
//                    isLastPage = false;
//                    isLoading = false;
//                    mEditionsPresenter.getEdition(searchStr, mPage);
//                }
//            }
//        });

        search.setOnTouchListener((v, event) -> {

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if (getActivity() != null) {
                    if (listener != null) {
                        String name = "";
                        if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.topics))) {
                            name = "topics";
                        } else if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.channels))) {
                            name = "channels";
                        } else if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.location))) {
                            name = "locations";
                        }
                        if (!TextUtils.isEmpty(name))
                            listener.onItemClicked(TYPE.MANAGE, "", name, false);
                        else {
                            Intent intent = new Intent(getActivity(), EditionActivity.class);
                            intent.putExtra("flow", "setting");
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            getActivity().startActivityForResult(intent, MainActivityNew.RESULT_INTENT_CHANGE_EDITION);
//                        getActivity().startActivity(new Intent(getActivity(), EditionActivity.class));
                        }
                    }
                }
            }
            return false;
        });

        search.setInputType(InputType.TYPE_NULL);
    }

    public void loadTopics() {
        if (!isLoading) {
            reset();
            mTabLayout.selectTab(mTabLayout.getTabAt(0));
            mCurrentType = getActivity().getString(R.string.topics);
            mPresenter.getFollowingTopics(mPage);
            setTopicsAdapter();
        }
    }

    private void setListeners() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                ApiClient.cancelAll();
                mCurrentType = tab.getText().toString();
                search.setInputType(InputType.TYPE_NULL);
                mTvEditionContent.setVisibility(View.GONE);
                Utils.hideKeyboard(getActivity(), mTabLayout);

                reset();
//                clearText();
                if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.edition))) {
                    setEditionsAdapter();
                    mEditionsPresenter.getEdition(searchStr, mPage);
                } else if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.topics))) {
                    setTopicsAdapter();
                    mPresenter.getFollowingTopics(mPage);
                } else if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.channels))) {
                    setChannelsAdapter();
                    mPresenter.getFollowingChannels(mPage);
                } else if (mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.location))) {
                    setLocationsAdapter();
                    mPresenter.getFollowingLocations(mPage);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mNestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.hideKeyboard(getActivity(), mNestedScrollView);
                return false;
            }
        });

        accountSettings.setOnClickListener(v -> getActivity().startActivityForResult(new Intent(getActivity(), AccSettingActivity.class), RESULT_INTENT_CHANGE_THEME));

    }

    private void reset() {

        mRecyclerView.removeItemDecoration(spaces);
        mRvSuggested.removeItemDecoration(spaces);
        mRecyclerView.removeItemDecoration(spacesChannel);
        mRvSuggested.removeItemDecoration(spacesChannel);

        mFollowedTopics.clear();
        mFollowedChannels.clear();
        mFollowedLocations.clear();
        mSuggestedTopics.clear();
        mSuggestedChannels.clear();
        mSuggestedLocations.clear();
        editions.clear();
        mPage = "";

//        mSearchChar = "";
        isSuggestedLoaded = false;
        if (mSearchTopicsAdapter != null) mSearchTopicsAdapter.notifyDataSetChanged();
        if (mSuggestedTopicsAdapter != null) mSuggestedTopicsAdapter.notifyDataSetChanged();
        if (mSearchChannelsAdapter != null) mSearchChannelsAdapter.notifyDataSetChanged();
        if (mSuggestedChannelsAdapter != null) mSuggestedChannelsAdapter.notifyDataSetChanged();
        if (mSearchLocationAdapter != null) mSearchLocationAdapter.notifyDataSetChanged();
        if (mSuggestedLocationAdapter != null) mSuggestedLocationAdapter.notifyDataSetChanged();
        if (mEditionsAdapter != null) mEditionsAdapter.notifyDataSetChanged();
        mTvProgress.setVisibility(View.GONE);
    }

    private void showOrHideSuggested(boolean flag) {
        mRvSuggested.setVisibility(flag ? View.VISIBLE : View.GONE);
//        mDivider.setVisibility(flag ? View.VISIBLE : View.GONE);
        mLabel.setVisibility(flag ? View.VISIBLE : View.GONE);
    }


    private void showFollowedTitle(String txt) {
        if (TextUtils.isEmpty(txt)) {
            mLabelFollowed.setVisibility(View.GONE);
            mDivider.setVisibility(View.GONE);
        } else {
            mLabelFollowed.setText(txt);
            mLabelFollowed.setVisibility(View.VISIBLE);
            mDivider.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
        progressBar.setVisibility(flag ? View.VISIBLE : View.GONE);
    }


    @Override
    public void error(String error, int load) {
        if (!TextUtils.isEmpty(error)) {
            if (error.equalsIgnoreCase("Canceled") || error.contains("reset")|| error.contains("closed"))
                return;
            if (getActivity() != null) {
                Utils.showSnacky(getActivity().getWindow().getDecorView().getRootView(),""+error);
            }
        }
    }


    /*
     *
     * TOPICS
     *
     */

    private void setTopicsAdapter() {
        space.setVisibility(View.VISIBLE);
        mRecyclerView.removeItemDecoration(spaces);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.addItemDecoration(spaces);
        mSearchTopicsAdapter = new SearchTopicsAdapter(getActivity(), mFollowedTopics, true);
        mRecyclerView.setAdapter(mSearchTopicsAdapter);
//        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.clearOnScrollListeners();

        mSearchTopicsAdapter.setCallback(new SearchTopicsAdapter.FollowingCallback() {
            @Override
            public void onItemFollowed(Topics topic) {
                listener.dataChanged(TYPE.TOPIC, "");
            }

            @Override
            public void onItemUnfollowed(Topics topic) {
                listener.dataChanged(TYPE.TOPIC, "");
            }

            @Override
            public void onItemClicked(Topics topic) {
                if (listener != null)
                    listener.onItemClicked(TYPE.TOPIC, topic.getId(), topic.getName(), topic.isFavorite());
            }
        });

        mNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                Log.i(TAG, "Scroll DOWN");

                if (!TextUtils.isEmpty(mPage) && !isLoading) {

                    if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                        int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                        int currentTotalCount = layoutManager.getItemCount();
                        if (!isLoading && !isLastPage && currentTotalCount <= lastItem + 3) {
                            //show your loading view
                            // load content in background
                            Log.i(TAG, "API CALLED");
                            mPresenter.getFollowingTopics(mPage);
                        }
                    }

//                    if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                        int visibleItemCount = mRecyclerView.getLayoutManager().getChildCount();
//                        int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
//                        int pastVisibleItems = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//                        Log.i(TAG, "=====================");
//                        Log.i(TAG, "visibleItemCount = " + visibleItemCount);
//                        Log.i(TAG, "totalItemCount = " + totalItemCount);
//                        Log.i(TAG, "pastVisibleItems = " + pastVisibleItems);
//                        if (pastVisibleItems + visibleItemCount >= totalItemCount && pastVisibleItems >= 0) {
//                            Log.i(TAG, "API CALLED");
//                            mPresenter.getFollowingTopics(mPage);
//                        }
//                        Log.i(TAG, "=====================");
//                    }
                } else if (TextUtils.isEmpty(mPage)) {
                    if (!isSuggestedLoaded) {
                        isSuggestedLoaded = true;
                        mPresenter.getSuggestedTopics();

                    }
                }
            }
        });

        showOrHideSuggested(false);
        showFollowedTitle("");

        mRvSuggested.removeItemDecoration(spaces);
        mRvSuggested.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRvSuggested.addItemDecoration(spaces);
        mSuggestedTopicsAdapter = new SearchTopicsAdapter(getActivity(), mSuggestedTopics, false);
        mRvSuggested.setAdapter(mSuggestedTopicsAdapter);

        mSuggestedTopicsAdapter.setCallback(new SearchTopicsAdapter.FollowingCallback() {
            @Override
            public void onItemFollowed(Topics topic) {
                //add topic to followed list
//                topic.setFavorite(true);
//                mFollowedTopics.add(topic);
//                mSearchTopicsAdapter.notifyDataSetChanged();
                listener.dataChanged(TYPE.TOPIC, "");
            }

            @Override
            public void onItemUnfollowed(Topics topic) {
//                try {
//                    mFollowedTopics.remove(topic);
//                    mSearchTopicsAdapter.notifyDataSetChanged();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                listener.dataChanged(TYPE.TOPIC, "");
            }

            @Override
            public void onItemClicked(Topics topic) {
                if (listener != null)
                    listener.onItemClicked(TYPE.TOPIC, topic.getId(), topic.getName(), topic.isFavorite());
            }
        });
    }


    @Override
    public void onFollowedTopicsSuccess(TopicsModel response) {
        if (response != null) {
            mPage = response.meta.getNext();
            if (TextUtils.isEmpty(mPage)) {
                isLastPage = true;
            }
            if (response.getTopics().size() > 0) {
                if (getActivity() != null)
                    showFollowedTitle(getActivity().getString(R.string.following));
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(false);
                mFollowedTopics.addAll(response.getTopics());
                mSearchTopicsAdapter.notifyDataSetChanged();
                if (TextUtils.isEmpty(mPage) && !isSuggestedLoaded) {
                    mPresenter.getSuggestedTopics();
                }
            } else {
                if (mFollowedTopics.size() == 0) {
                    showFollowedTitle("");
                    mPresenter.getSuggestedTopics();
                    isSuggestedLoaded = true;
                }
//                else mTvProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSuggestedTopicsSuccess(TopicsModel response) {
        if (response != null && response.getTopics().size() > 0) {
            if (mSuggestedTopics.size() == 0) {
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(true);
                mSuggestedTopics.addAll(response.getTopics());
                mSuggestedTopicsAdapter.notifyDataSetChanged();
            }
        }
//        else mTvProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchTopicsSuccess(TopicsModel response) {
        if (response != null) {
            mPage = response.meta.getNext();
            if (TextUtils.isEmpty(mPage)) {
                isLastPage = true;
            }
            if (response.getTopics().size() > 0) {
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(false);
                mFollowedTopics.clear();
                mFollowedTopics.addAll(response.getTopics());
                mSearchTopicsAdapter.notifyDataSetChanged();
            } else mTvProgress.setVisibility(View.VISIBLE);
        } else mTvProgress.setVisibility(View.VISIBLE);
    }

    ////////////////////////////////////////////////////////////////////////


    /*
     *
     * CHANNELS
     *
     */

    private void setChannelsAdapter() {
        space.setVisibility(View.VISIBLE);
        mRecyclerView.removeItemDecoration(spacesChannel);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.addItemDecoration(spacesChannel);
        mSearchChannelsAdapter = new SearchModiSourceAdapter(getActivity(), mFollowedChannels, true);
        mRecyclerView.setAdapter(mSearchChannelsAdapter);
        mRecyclerView.clearOnScrollListeners();

        mNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                Log.i(TAG, "Scroll DOWN");

                if (!TextUtils.isEmpty(mPage) && !isLoading) {
                    if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                        int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                        int currentTotalCount = layoutManager.getItemCount();
                        if (!isLoading && !isLastPage && currentTotalCount <= lastItem + 3) {
                            //show your loading view
                            // load content in background
                            mPresenter.getFollowingChannels(mPage);
                        }
                    }
                } else if (TextUtils.isEmpty(mPage)) {
                    if (!isSuggestedLoaded) {
                        isSuggestedLoaded = true;
                        mPresenter.getSuggestedChannels(false);
                    }
                }
            }
        });


        mSearchChannelsAdapter.setCallback(new SearchModiSourceAdapter.FollowingCallback() {
            @Override
            public void onItemFollowed(Source source) {
                listener.dataChanged(TYPE.SOURCE, "");
            }

            @Override
            public void onItemUnfollowed(Source source) {
                listener.dataChanged(TYPE.SOURCE, "");
            }

            @Override
            public void onItemClicked(Source source) {
                if (listener != null)
                    listener.onItemClicked(TYPE.SOURCE, source.getId(), source.getName(), source.isFavorite());
            }
        });

        mRvSuggested.removeItemDecoration(spacesChannel);
        mRvSuggested.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRvSuggested.addItemDecoration(spacesChannel);
        mSuggestedChannelsAdapter = new SearchModiSourceAdapter(getActivity(), mSuggestedChannels, false);
        mRvSuggested.setAdapter(mSuggestedChannelsAdapter);
        mSuggestedChannelsAdapter.setCallback(new SearchModiSourceAdapter.FollowingCallback() {
            @Override
            public void onItemFollowed(Source source) {
                //add channel to followed list
//                source.setFavorite(true);
//                mFollowedChannels.add(source);
//                mSearchChannelsAdapter.notifyDataSetChanged();
                listener.dataChanged(TYPE.SOURCE, "");
            }

            @Override
            public void onItemUnfollowed(Source source) {
//                try {
//                    mFollowedChannels.remove(source);
//                    mSearchChannelsAdapter.notifyDataSetChanged();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                listener.dataChanged(TYPE.SOURCE, "");
            }

            @Override
            public void onItemClicked(Source source) {
                if (listener != null)
                    listener.onItemClicked(TYPE.SOURCE, source.getId(), source.getName(), source.isFavorite());
            }
        });
        showOrHideSuggested(false);
        showFollowedTitle("");
    }


    @Override
    public void onFollowedChannelsSuccess(SourceModel response) {
        if (response != null) {
            mPage = response.meta.getNext();
            if (TextUtils.isEmpty(mPage)) {
                isLastPage = true;
            }
            if (response.getSources().size() > 0) {
                if (getActivity() != null)
                    showFollowedTitle(getActivity().getString(R.string.following));
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(false);
                mFollowedChannels.addAll(response.getSources());
                mSearchChannelsAdapter.notifyDataSetChanged();
                if (TextUtils.isEmpty(mPage) && !isSuggestedLoaded) {
                    mPresenter.getSuggestedChannels(false);
                }
            } else {
                if (mFollowedChannels.size() == 0) {
                    showFollowedTitle("");
                    mPresenter.getSuggestedChannels(false);
                    isSuggestedLoaded = true;
                }
//                else mTvProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSuggestedChannelsSuccess(SourceModel response) {
        if (response != null && response.getSources().size() > 0) {
            if (mSuggestedChannels.size() == 0) {
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(true);
                mSuggestedChannels.addAll(response.getSources());
                mSuggestedChannelsAdapter.notifyDataSetChanged();
            }
        }
//        else mTvProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchChannelsSuccess(SourceModel response) {
        if (response != null) {
            mPage = response.meta.getNext();
            if (TextUtils.isEmpty(mPage)) {
                isLastPage = true;
            }
            if (response.getSources().size() > 0) {
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(false);
                mFollowedChannels.clear();
                mFollowedChannels.addAll(response.getSources());
                mSearchChannelsAdapter.notifyDataSetChanged();
            } else mTvProgress.setVisibility(View.VISIBLE);
        } else mTvProgress.setVisibility(View.VISIBLE);
    }

    ////////////////////////////////////////////////////////////////////////


    /*
     *
     * LOCATIONS
     *
     */

    private void setLocationsAdapter() {
        space.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchLocationAdapter = new ManageLocationAdapter(getActivity(), mFollowedLocations);
        mRecyclerView.setAdapter(mSearchLocationAdapter);
        mRecyclerView.clearOnScrollListeners();


        mNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                Log.i(TAG, "Scroll DOWN");

                if (!TextUtils.isEmpty(mPage) && !isLoading) {
                    if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        int visibleItemCount = mRecyclerView.getLayoutManager().getChildCount();
                        int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                        int pastVisibleItems = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        Log.i(TAG, "=====================");
                        Log.i(TAG, "visibleItemCount = " + visibleItemCount);
                        Log.i(TAG, "totalItemCount = " + totalItemCount);
                        Log.i(TAG, "pastVisibleItems = " + pastVisibleItems);
                        if (pastVisibleItems + visibleItemCount >= totalItemCount && pastVisibleItems >= 0) {
                            Log.i(TAG, "API CALLED");
                            mPresenter.getFollowingLocations(mPage);
                        }
                        Log.i(TAG, "=====================");
                    }
                } else if (TextUtils.isEmpty(mPage)) {
                    if (!isSuggestedLoaded) {
                        isSuggestedLoaded = true;
                        mPresenter.getSuggestedLocations();

                    }
                }
            }
        });


        mSearchLocationAdapter.setClickListener(new ManageLocationAdapter.LocationCallback() {
            @Override
            public void onItemFollowed(Location location) {
                listener.dataChanged(TYPE.LOCATION, "");
            }

            @Override
            public void onItemUnfollowed(Location location) {
                listener.dataChanged(TYPE.LOCATION, "");
            }

            @Override
            public void onItemClicked(Location location) {
                if (listener != null)
                    listener.onItemClicked(TYPE.LOCATION, location.getId(), location.getNameToShow(), location.isFavorite());
            }

            @Override
            public void onAddTopicClicked() {

            }
        });

        mRvSuggested.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSuggestedLocationAdapter = new ManageLocationAdapter(getActivity(), mSuggestedLocations);
        mRvSuggested.setAdapter(mSuggestedLocationAdapter);
        mSuggestedLocationAdapter.setClickListener(new ManageLocationAdapter.LocationCallback() {
            @Override
            public void onItemFollowed(Location location) {
//                location.setFavorite(true);
//                mFollowedLocations.add(location);
//                mSearchLocationAdapter.notifyDataSetChanged();
                listener.dataChanged(TYPE.LOCATION, "");
            }

            @Override
            public void onItemUnfollowed(Location location) {
//                try {
//                    mFollowedLocations.remove(location);
//                    mSearchLocationAdapter.notifyDataSetChanged();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                listener.dataChanged(TYPE.LOCATION, "");
            }

            @Override
            public void onItemClicked(Location location) {
                if (listener != null)
                    listener.onItemClicked(TYPE.LOCATION, location.getId(), location.getNameToShow(), location.isFavorite());
            }

            @Override
            public void onAddTopicClicked() {

            }
        });
        showOrHideSuggested(false);
        showFollowedTitle("");
    }


    @Override
    public void onFollowedLocationSuccess(LocationModel response) {
        if (response != null) {
            mPage = response.meta.getNext();
            if (TextUtils.isEmpty(mPage)) {
                isLastPage = true;
            }
            if (response.getLocations().size() > 0) {
                if (getActivity() != null)
                    showFollowedTitle(getActivity().getString(R.string.your_locations));
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(false);
                mFollowedLocations.addAll(response.getLocations());
                mSearchLocationAdapter.notifyDataSetChanged();
                if (TextUtils.isEmpty(mPage) && !isSuggestedLoaded) {
                    mPresenter.getSuggestedLocations();
                }
            } else {
                if (mFollowedLocations.size() == 0) {
                    showFollowedTitle("");
                    mPresenter.getSuggestedLocations();
                    isSuggestedLoaded = true;
                }
//                else mTvProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSuggestedLocationSuccess(LocationModel response) {
        if (response != null && response.getLocations().size() > 0) {
            if (mSuggestedLocations.size() == 0) {
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(true);
                mSuggestedLocations.addAll(response.getLocations());
                mSuggestedLocationAdapter.notifyDataSetChanged();
            }
        }
//        else mTvProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchLocationSuccess(LocationModel response) {
        if (response != null) {
            mPage = response.meta.getNext();
            if (TextUtils.isEmpty(mPage)) {
                isLastPage = true;
            }
            if (response.getLocations().size() > 0) {
                mTvProgress.setVisibility(View.GONE);
                showOrHideSuggested(false);
                mFollowedLocations.clear();
                mFollowedLocations.addAll(response.getLocations());
                mSearchLocationAdapter.notifyDataSetChanged();
            } else
                mTvProgress.setVisibility(View.VISIBLE);
        } else
            mTvProgress.setVisibility(View.VISIBLE);
    }
    /*
     *
     * DISCOVER
     *
     */

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

    }

    @Override
    public void onFollowedAuthorsSuccess(AuthorSearchResponse response) {

    }

    @Override
    public void onSuggestedAuthorsSuccess(AuthorSearchResponse response) {

    }

    public void reloadEditions() {
        reset();
        if (getActivity() != null && mCurrentType.equalsIgnoreCase(getActivity().getString(R.string.edition))) {
            setEditionsAdapter();
            mEditionsPresenter.getEdition(searchStr, mPage);
        }
    }


    private void setEditionsAdapter() {
        space.setVisibility(View.GONE);
        mTvEditionContent.setVisibility(View.VISIBLE);
        search.setInputType(InputType.TYPE_NULL);

        mEditionsAdapter = new EditionsAdapter(editions, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mEditionsAdapter.setCallback(new EditionsAdapter.EditionOnclickCallback() {
            @Override
            public void onClickItem(int position, String id) {

            }

            @Override
            public void onClickMarkInner(int position, String id, boolean isFollow) {
                if (isFollow) {
                    mEditionsPresenter.followEdition(position, id, isFollow);
                } else {
                    mEditionsPresenter.unFollowEdition(position, id, isFollow);
                }
            }

            @Override
            public void onClickMark(int position, String id, boolean isFollow) {
                if (isFollow) {
                    mEditionsPresenter.followEdition(position, id, isFollow);
                } else {
                    mEditionsPresenter.unFollowEdition(position, id, isFollow);
                }
            }
        });
        mRecyclerView.setAdapter(mEditionsAdapter);
        mRecyclerView.clearOnScrollListeners();

        mNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                Log.i(TAG, "Scroll DOWN");

                if (!TextUtils.isEmpty(mPage) && !isLoading) {
                    if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                        int visibleItemCount = mRecyclerView.getLayoutManager().getChildCount();
//                        int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
//                        int pastVisibleItems = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//                        Log.i(TAG, "=====================");
//                        Log.i(TAG, "visibleItemCount = " + visibleItemCount);
//                        Log.i(TAG, "totalItemCount = " + totalItemCount);
//                        Log.i(TAG, "pastVisibleItems = " + pastVisibleItems);

                        View lastChild = mNestedScrollView.getChildAt(mNestedScrollView.getChildCount() - 1);

                        if (lastChild != null) {
                            if ((scrollY >= (lastChild.getMeasuredHeight() - mNestedScrollView.getMeasuredHeight())) && scrollY > oldScrollY) {
                                Log.i(TAG, "ACtual API CALLED");
                                mEditionsPresenter.getEdition(searchStr, mPage);
                            }
                        }

//                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount
//                                && pastVisibleItems >= 0
//                                && totalItemCount >= 15) {
//                            Log.i(TAG, "API CALLED");
//                            mEditionsPresenter.getEdition(searchStr, mPage);
//                        }
//                        Log.i(TAG, "=====================");
                    }
                }
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Utils.hideKeyboard(getActivity(), mRecyclerView);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        search.setText("");
        searchStr = "";

        showOrHideSuggested(false);
        showFollowedTitle("");
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void success(ResponseEdition responseEdition) {
        isLoading = false;
        if (responseEdition != null && responseEdition.getEditions().size() > 0) {
            editions.addAll(responseEdition.getEditions());
            if (mEditionsAdapter != null)
                mEditionsAdapter.notifyDataSetChanged();
            if (responseEdition.getMeta() != null) {
                mPage = responseEdition.getMeta().getNext();
            }
            if (TextUtils.isEmpty(mPage)) {
                isLastPage = true;
            }
        }
    }

    @Override
    public void successFollowed(ResponseEdition responseEdition) {

    }

    @Override
    public void onUpdateSuccess() {

    }

    @Override
    public void successFollowUnfollow(int position, String id, boolean isFollow) {
        if (position < editions.size()) {
            editions.get(position).setSelected(isFollow);
            mEditionsAdapter.notifyDataSetChanged();
        }
    }

    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("HOMELIFE", "onAttach");
        if (context instanceof ProfileFragment.OnFragmentInteractionListener) {
            listener = (ProfileFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnHomeFragmentInteractionListener");
        }
    }


    public interface OnFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void onItemClicked(TYPE type, String id, String name, boolean favorite);
    }
}
