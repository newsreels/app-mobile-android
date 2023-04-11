package com.ziro.bullet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.followers.FollowersAdapter;
import com.ziro.bullet.interfaces.ApiCallbacks;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.followers.FollowersListResponse;
import com.ziro.bullet.presenter.FollowersPresenter;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowersActivity extends BaseActivity {

    public static final String EXTRA_SOURCE_ID = "EXTRA_SOURCE_ID";
    public static final String EXTRA_USER_ID = "EXTRA_USER_ID";
    public static final String EXTRA_IS_PROFILE = "EXTRA_IS_PROFILE";
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private RelativeLayout ivBack;
    private RecyclerView followersList;
    private LinearLayout progressLayout;
    private LinearLayout emptyLayout;
    private TextView emptyText;
    private SearchView svSearch;
    private FollowersPresenter followersPresenter;
    private String page = "";
    private ArrayList<Author> followersUserArrayList;
    private FollowersAdapter followersAdapter;
    private LinearLayoutManager mLayoutManager;
    private String sourceId = "";
    private String query = "";
    private boolean loading = true;
    private final ApiCallbacks apiCallbacks = new ApiCallbacks() {

        @Override
        public void loaderShow(boolean flag) {
            loading = flag;
            progressLayout.setVisibility(loading && page.equals("") ? View.VISIBLE : View.GONE);
        }

        @Override
        public void error(String error) {

        }

        @Override
        public void error404(String error) {

        }

        @Override
        public void success(Object response) {
            if (response instanceof FollowersListResponse) {
                FollowersListResponse followersListResponse = (FollowersListResponse) response;
                followersUserArrayList.addAll(followersListResponse.getUsers());
                followersAdapter.setSearchResult(!TextUtils.isEmpty(query));
                followersAdapter.notifyDataSetChanged();

                if (followersUserArrayList.size() <= 0) {
                    if (!TextUtils.isEmpty(query)) {
                        emptyText.setText(getString(R.string.no_results_found));
                    } else {
                        emptyText.setText(getString(R.string.you_don_t_have_any_followers));
                    }
                    emptyLayout.setVisibility(View.VISIBLE);
                    followersList.setVisibility(View.GONE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    followersList.setVisibility(View.VISIBLE);
                }

                if (TextUtils.isEmpty(page)) {
                    followersList.scrollToPosition(0);
                }

                if (followersListResponse.getMeta() != null)
                    page = followersListResponse.getMeta().getNext();
            }
        }
    };

    public static void start(Context context, String sourceId) {
        Intent intent = new Intent(context, FollowersActivity.class);
        intent.putExtra(EXTRA_SOURCE_ID, sourceId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_followers);

        if (getIntent() != null) {
            if (getIntent().hasExtra(EXTRA_SOURCE_ID)) {
                sourceId = getIntent().getStringExtra(EXTRA_SOURCE_ID);
            }
        }

        bindViews();
        initData();
        listeners();
    }

    private void bindViews() {
        ivBack = findViewById(R.id.ivBack);
        followersList = findViewById(R.id.followers_list);
        svSearch = findViewById(R.id.svSearch);
        progressLayout = findViewById(R.id.progress_layout);
        emptyLayout = findViewById(R.id.empty_layout);
        emptyText = findViewById(R.id.empty_text);
    }

    private void initData() {
        followersUserArrayList = new ArrayList<>();
        followersPresenter = new FollowersPresenter(this, apiCallbacks);

        mLayoutManager = new LinearLayoutManager(this);
        followersAdapter = new FollowersAdapter(followersUserArrayList, this);

        followersList.setLayoutManager(mLayoutManager);
        followersList.setAdapter(followersAdapter);

        page = "";
        followersPresenter.getFollowers(sourceId, query, page);
    }

    private void listeners() {
        ivBack.setOnClickListener(v -> finish());
        followersList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (!loading && !TextUtils.isEmpty(page)) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount - 5) {
                            followersPresenter.getFollowers(sourceId, query, page);
                        }
                    }
                }
            }
        });
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                ApiClient.cancelAll();
                page = "";
                followersPresenter.getFollowers(sourceId, query, page);
                return false;
            }
        });
    }

}