package com.ziro.bullet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.NewFeed.SuggestedChannelAdapter;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.interfaces.SearchInterface;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.presenter.SearchPresenter;
import com.ziro.bullet.utills.PaginationListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Response;

public class ChannelFragment extends Fragment implements SearchInterface {

    private static final String BUNDLE_CONTEXT_ID = "bundle_context_id";
    private RecyclerView rvChannels;
    private RelativeLayout progress;
    private SearchPresenter mPresenter;
    private ArrayList<Source> mFollowedChannels = new ArrayList<>();
    private SuggestedChannelAdapter mChannelsAdapter;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    public static ChannelFragment newInstance(String sourceId) {
        ChannelFragment authorArticleFragment = new ChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CONTEXT_ID, sourceId);
        authorArticleFragment.setArguments(bundle);
        return authorArticleFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channels, container, false);
        bindView(view);
        init();
        return view;
    }

    private void init() {
        mPresenter = new SearchPresenter(getActivity(), this);
        mChannelsAdapter = new SuggestedChannelAdapter(getActivity(), mFollowedChannels, false);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvChannels.setLayoutManager(layoutManager);
        rvChannels.setAdapter(mChannelsAdapter);

        rvChannels.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                mPresenter.getSuggestedChannels(false);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public void onScroll(int position) {
//                Utils.hideKeyboard(EditionActivity.this, mRvRecyclerView);
            }

            @Override
            public void onScrolling(@NonNull @NotNull RecyclerView recyclerView, int newState) {

            }
        });

        mPresenter.getSuggestedChannels(false);
    }

    private void bindView(View view) {
        rvChannels = view.findViewById(R.id.rvChannels);
        progress = view.findViewById(R.id.progress);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {

        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    @Override
    public void loaderShow(boolean flag) {
        progress.setVisibility(flag? View.VISIBLE: View.GONE);
    }

    @Override
    public void error(String error, int load) {

    }

    @Override
    public void onFollowedTopicsSuccess(TopicsModel response) {

    }

    @Override
    public void onFollowedChannelsSuccess(SourceModel response) {

    }

    @Override
    public void onFollowedLocationSuccess(LocationModel response) {

    }

    @Override
    public void onSuggestedTopicsSuccess(TopicsModel response) {

    }

    @Override
    public void onSuggestedChannelsSuccess(SourceModel response) {
        if (response != null) {
            if (response.getSources().size() > 0) {
                mFollowedChannels.clear();
                mFollowedChannels.addAll(response.getSources());
                mChannelsAdapter.notifyDataSetChanged();
            } else {

            }
        }
    }

    @Override
    public void onSuggestedLocationSuccess(LocationModel response) {

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

    }

    @Override
    public void onSuggestedAuthorsSuccess(AuthorSearchResponse response) {

    }
}