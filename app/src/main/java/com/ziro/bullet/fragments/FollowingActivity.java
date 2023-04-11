package com.ziro.bullet.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.BaseActivity;
import com.ziro.bullet.adapters.NewFeed.SuggestedChannelAdapter;
import com.ziro.bullet.adapters.following.FollowingAuthorsAdapter;
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
import com.ziro.bullet.presenter.SearchPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

import retrofit2.Response;

public class FollowingActivity extends BaseActivity implements SearchInterface {
    public static final String TAG = "FollowingFragment";
    private static final int INTENT_REELS_VERSION = 3424;

//    private RecyclerView rvChannels, rvAuthors;
    private TagFlowLayout rvTopics, rvPlaces;
    //    private TextView tvEmpty, tvManageChannels, tvManageTopics, tvManageAuthors, tvManagePlaces;
//    private TextView tvHeaderChannels, tvHeaderTopics, tvHeaderAuthors, tvHeaderPlaces;
    //    private RelativeLayout cardChannels, cardTopics, cardAuthors, cardPlaces;
    private RelativeLayout ivBack;

    private ArrayList<Topics> mFollowedTopics = new ArrayList<>();
    private ArrayList<Source> mFollowedChannels = new ArrayList<>();
    private ArrayList<Author> mFollowedAuthors = new ArrayList<>();
    private ArrayList<Location> mFollowedLocations = new ArrayList<>();

    private SuggestedChannelAdapter mChannelsAdapter;
    private TagAdapter<Topics> mTopicsAdapter;
    private FollowingAuthorsAdapter mAuthorsAdapter;
    private TagAdapter<Location> mPlacesAdapter;

    private SearchPresenter mPresenter;
    private boolean isChannelsEmpty = false, isTopicsEmpty = false, isAuthorssEmpty = false, isPlacesEmpty = false;
    private boolean hasReels = false;
    private ProgressBar progress;
    private PrefConfig mPrefConfig;

    public static void launchActivity(Context context) {
        context.startActivity(new Intent(context, FollowingActivity.class));
    }

    public static void launchActivity(Fragment context, int requestCode) {
        Intent intent = new Intent(context.getContext(), FollowingActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void launchActivity(Context context, boolean isReel) {
        Intent intent = new Intent(context, FollowingActivity.class);
        intent.putExtra("extra", isReel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.fragment_following_profile);

        mPresenter = new SearchPresenter(this, this);
        mPrefConfig = new PrefConfig(this);
        if (getIntent() != null && getIntent().hasExtra("extra")) {
            hasReels = getIntent().getBooleanExtra("extra", false);
        }

        progress = findViewById(R.id.progress);
//        rvChannels = findViewById(R.id.rvChannels);
        rvTopics = findViewById(R.id.rvTopics);
//        rvAuthors = findViewById(R.id.rvAuthors);
        rvPlaces = findViewById(R.id.rvPlaces);
//        tvEmpty = findViewById(R.id.tvEmpty);
//        tvManageChannels = findViewById(R.id.tvManageChannels);
//        tvManageTopics = findViewById(R.id.tvManageTopics);
//        tvManageAuthors = findViewById(R.id.tvManageAuthors);
//        tvManagePlaces = findViewById(R.id.tvManagePlaces);
//        cardChannels = findViewById(R.id.cardChannels);
//        cardTopics = findViewById(R.id.cardTopics);
//        cardAuthors = findViewById(R.id.cardAuthors);
//        cardPlaces = findViewById(R.id.cardPlaces);
//        tvHeaderChannels = findViewById(R.id.tvHeaderChannels);
//        tvHeaderTopics = findViewById(R.id.tvHeaderTopics);
//        tvHeaderAuthors = findViewById(R.id.tvHeaderAuthors);
//        tvHeaderPlaces = findViewById(R.id.tvHeaderPlaces);
        ivBack = findViewById(R.id.ivBack);

//        tvManageChannels.setOnClickListener(v -> openDetail("channels"));
//        tvManageTopics.setOnClickListener(v -> openDetail("topics"));
//        tvManageAuthors.setOnClickListener(v -> openDetail("authors"));
//        tvManagePlaces.setOnClickListener(v -> openDetail("places"));
        ivBack.setOnClickListener(v -> onBackPressed());

//        rvChannels.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
//        rvAuthors.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        mChannelsAdapter = new SuggestedChannelAdapter(this, mFollowedChannels, false);
//        rvChannels.setAdapter(mChannelsAdapter);

//        mTopicsAdapter = new FollowingTopicsAdapter(this, mFollowedTopics);
//        rvTopics.setAdapter(mTopicsAdapter);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mTopicsAdapter = new TagAdapter<Topics>(mFollowedTopics) {
            @Override
            public View getView(FlowLayout parent, int position, Topics s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.new_topic_item, rvTopics, false);
                tv.setText(s.getName());
                return tv;
            }
        };
        rvTopics.setAdapter(mTopicsAdapter);


        mAuthorsAdapter = new FollowingAuthorsAdapter(this, mFollowedAuthors, false);
//        rvAuthors.setAdapter(mAuthorsAdapter);

//        mPlacesAdapter = new FollowingPlacesAdapter(this, mFollowedLocations, false);
//        rvPlaces.setAdapter(mPlacesAdapter);

        mPlacesAdapter = new TagAdapter<Location>(mFollowedLocations) {
            @Override
            public View getView(FlowLayout parent, int position, Location s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.new_topic_item, rvPlaces, false);
                tv.setText(s.getName());
                return tv;
            }
        };
        rvPlaces.setAdapter(mPlacesAdapter);
    }

    @Override
    public void onBackPressed() {
        if (Constants.reelDataUpdate) {
            if (mPrefConfig != null)
                mPrefConfig.setReelsType(Constants.REELS_FOLLOWING);
        }
//        if (hasReels) {
        setResult(RESULT_OK);
//        }
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        if (!hasReels) {
            mPresenter.getFollowingChannels("");
            mPresenter.getFollowingTopics("");
            mPresenter.getFollowingAuthors("");
//            mPresenter.getFollowingLocations("");
        } else {
            mPresenter.getSuggestedAuthors(hasReels);
            mPresenter.getSuggestedChannels(hasReels);
        }
    }

    private void openDetail(String type) {
        FollowingDetailActivity.launchActivity(this, type, "");
//        FollowingDetailFragment nextFrag = FollowingDetailFragment.launchActivity(type);
////            ProfileFollowingFragment nextFrag = new ProfileFollowingFragment();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.frameLayout, nextFrag, type)
//                .addToBackStack("following")
//                .commitAllowingStateLoss();
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error, int load) {

    }

    @Override
    public void onFollowedTopicsSuccess(TopicsModel response) {
        if (isFinishing())
            return;
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getTopics().size() > 0) {
                mFollowedTopics.clear();
                mFollowedTopics.addAll(response.getTopics());
                mTopicsAdapter.notifyDataChanged();
//                if (TextUtils.isEmpty(mPage) && !isSuggestedLoaded) {
//                    mPresenter.getSuggestedTopics();
//                }
                isTopicsEmpty = false;
//                cardTopics.setVisibility(View.VISIBLE);
//                tvManageTopics.setVisibility(View.VISIBLE);
            } else {
                if (mFollowedTopics.size() == 0) {
                    isTopicsEmpty = true;
//                    tvManageTopics.setVisibility(View.VISIBLE);
//                    showFollowedTitle("");
                    mPresenter.getSuggestedTopics();
//                    isSuggestedLoaded = true;
                }
            }

            isListEmpty();
        }
    }

    @Override
    public void onFollowedChannelsSuccess(SourceModel response) {
        if (isFinishing())
            return;
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getSources().size() > 0) {
                mFollowedChannels.clear();
                mFollowedChannels.addAll(response.getSources());
                mChannelsAdapter.notifyDataSetChanged();
//                if (TextUtils.isEmpty(mPage) && !isSuggestedLoaded) {
//                    mPresenter.getSuggestedChannels();
//                }
                isChannelsEmpty = false;
//                cardChannels.setVisibility(View.VISIBLE);
//                tvManageChannels.setVisibility(View.VISIBLE);
            } else {
                if (mFollowedChannels.size() == 0) {
                    mPresenter.getSuggestedChannels(false);
                    isChannelsEmpty = true;
//                    tvManageChannels.setVisibility(View.VISIBLE);
                }
            }

        }

        isListEmpty();
    }

    @Override
    public void onFollowedLocationSuccess(LocationModel response) {
        if (isFinishing())
            return;
        if (response != null) {
            if (response.getLocations().size() > 0) {
                mFollowedLocations.clear();
                mFollowedLocations.addAll(response.getLocations());
                mPlacesAdapter.notifyDataChanged();
                isPlacesEmpty = false;
//                cardPlaces.setVisibility(View.VISIBLE);
//                tvManagePlaces.setVisibility(View.VISIBLE);
            } else {
                if (mFollowedLocations.size() == 0) {
                    mPresenter.getSuggestedLocations();
                    isPlacesEmpty = true;
//                    tvManagePlaces.setVisibility(View.VISIBLE);
                }
            }
        }

        isListEmpty();
    }

    @Override
    public void onSuggestedTopicsSuccess(TopicsModel response) {
        if (isFinishing())
            return;
        if (response != null) {
            if (response.getTopics().size() > 0) {
                mFollowedTopics.addAll(response.getTopics());
                mTopicsAdapter.notifyDataChanged();
//                cardTopics.setVisibility(View.VISIBLE);
//                tvHeaderTopics.setText(getString(R.string.suggested_topics));
            } else {

            }
        }
    }

    @Override
    public void onSuggestedChannelsSuccess(SourceModel response) {
        if (isFinishing())
            return;
        if (response != null) {
            if (response.getSources().size() > 0) {
                mFollowedChannels.clear();
                mFollowedChannels.addAll(response.getSources());
                mChannelsAdapter.notifyDataSetChanged();
//                cardChannels.setVisibility(View.VISIBLE);
//                tvHeaderChannels.setText(getString(R.string.suggested_channels));
            } else {
            }

        }
    }

    @Override
    public void onSuggestedLocationSuccess(LocationModel response) {
        if (isFinishing())
            return;
        if (response != null) {
            if (response.getLocations().size() > 0) {
                mFollowedLocations.clear();
                mFollowedLocations.addAll(response.getLocations());
                mPlacesAdapter.notifyDataChanged();
//                cardPlaces.setVisibility(View.VISIBLE);
//                tvHeaderPlaces.setText(getString(R.string.suggested_places));
            } else {
            }
        }
    }

    @Override
    public void onSearchTopicsSuccess(TopicsModel response) {

    }

    @Override
    public void onSearchChannelsSuccess(SourceModel response) {

    }

    @Override
    public void onSearchLocationSuccess(LocationModel response) {

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

    }

    @Override
    public void onFollowedAuthorsSuccess(AuthorSearchResponse response) {
        if (isFinishing())
            return;
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getAuthors() != null && response.getAuthors().size() > 0) {
                mFollowedAuthors.clear();
                mFollowedAuthors.addAll(response.getAuthors());
                mAuthorsAdapter.notifyDataSetChanged();
                isAuthorssEmpty = false;
//                cardAuthors.setVisibility(View.VISIBLE);
//                tvManageAuthors.setVisibility(View.VISIBLE);
            } else {
                mPresenter.getSuggestedAuthors(false);
                isAuthorssEmpty = true;
//                tvManageAuthors.setVisibility(View.VISIBLE);
            }
        }

        isListEmpty();

    }

    @Override
    public void onSuggestedAuthorsSuccess(AuthorSearchResponse response) {
        if (isFinishing())
            return;
        if (response != null) {
//            if (response.meta != null) {
//                mPage = response.meta.getNext();
//            }
            if (response.getAuthors() != null && response.getAuthors().size() > 0) {
                mFollowedAuthors.clear();
                mFollowedAuthors.addAll(response.getAuthors());
                mAuthorsAdapter.notifyDataSetChanged();
//                cardAuthors.setVisibility(View.VISIBLE);
//                tvHeaderAuthors.setText(getString(R.string.suggested_authors));
            } else {
            }
        }

    }

    private void isListEmpty() {
        if (isChannelsEmpty &&
                isTopicsEmpty &&
                isAuthorssEmpty &&
                isPlacesEmpty) {
//            tvEmpty.setVisibility(View.VISIBLE);
        } else {
//            tvEmpty.setVisibility(View.GONE);
        }
    }

}
