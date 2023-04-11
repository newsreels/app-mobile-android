package com.ziro.bullet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelDetailsActivity;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.adapters.SearchModiSourceAdapter;
import com.ziro.bullet.adapters.SearchTopicsAdapter;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.interfaces.SearchInterface;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.presenter.SearchPresenter;
import com.ziro.bullet.utills.SpacesItemDecoration;

import java.util.ArrayList;

import retrofit2.Response;

public class ProfileFollowingFragment extends Fragment implements SearchInterface {

    private static final String ARG_PARAM1 = "type";

    private RelativeLayout rlBack;
    private TextView topicTitle;
    private TextView channelTitle;
    private RecyclerView mTopicList;
    private RecyclerView mChannelList;
    private ArrayList<Topics> mFollowedTopics = new ArrayList<>();
    private ArrayList<Source> mFollowedChannels = new ArrayList<>();
    private SearchTopicsAdapter mSearchTopicsAdapter;
    private SearchModiSourceAdapter mSearchChannelsAdapter;

    private String mPage = "";
    private boolean isLastPage = false;
    private SearchPresenter mPresenter;

    public ProfileFollowingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SearchPresenter(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_following, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getFollowingTopics(mPage);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topicTitle = view.findViewById(R.id.topicTitle);
        channelTitle = view.findViewById(R.id.ChannelTitle);
        rlBack = view.findViewById(R.id.ivBack);

        rlBack.setOnClickListener(v -> getActivity().onBackPressed());
        setTopics(view);
        setChannels(view);
    }

    private void setChannels(View view) {
        mChannelList = view.findViewById(R.id.channelList);
        mChannelList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mChannelList.addItemDecoration(new SpacesItemDecoration(20));

        mSearchChannelsAdapter = new SearchModiSourceAdapter(getActivity(), mFollowedChannels, true);
        mChannelList.setAdapter(mSearchChannelsAdapter);
        mSearchChannelsAdapter.setCallback(new SearchModiSourceAdapter.FollowingCallback() {
            @Override
            public void onItemFollowed(Source source) {

            }

            @Override
            public void onItemUnfollowed(Source source) {

            }

            @Override
            public void onItemClicked(Source source) {
                Intent intent = new Intent(getContext(), ChannelDetailsActivity.class);
                intent.putExtra("type", TYPE.SOURCE);
                intent.putExtra("id", source.getId());
                intent.putExtra("name", source.getName());
                intent.putExtra("favorite", source.isFavorite());
                startActivity(intent);
            }
        });
    }

    private void setTopics(@NonNull View view) {
        mTopicList = view.findViewById(R.id.topicList);
        mTopicList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mTopicList.addItemDecoration(new SpacesItemDecoration(20));

        mSearchTopicsAdapter = new SearchTopicsAdapter(getActivity(), mFollowedTopics, true);
        mTopicList.setAdapter(mSearchTopicsAdapter);
        mSearchTopicsAdapter.setCallback(new SearchTopicsAdapter.FollowingCallback() {
            @Override
            public void onItemFollowed(Topics topic) {

            }

            @Override
            public void onItemUnfollowed(Topics topic) {

            }

            @Override
            public void onItemClicked(Topics topic) {
                Intent intent = new Intent(getContext(), ChannelPostActivity.class);
                intent.putExtra("type", TYPE.TOPIC);
                intent.putExtra("id", topic.getId());
                intent.putExtra("name", topic.getName());
                intent.putExtra("favorite", topic.isFavorite());
                startActivity(intent);
            }
        });
    }

    @Override
    public void loaderShow(boolean flag) {

    }

    @Override
    public void error(String error, int load) {

    }

    @Override
    public void onFollowedTopicsSuccess(TopicsModel response) {
        if (response != null) {
            mFollowedTopics.clear();
            if (response.getTopics() != null && response.getTopics().size() > 0) {
                topicTitle.setVisibility(View.VISIBLE);
                mFollowedTopics.addAll(response.getTopics());
            } else {
                topicTitle.setVisibility(View.GONE);
            }
        }
        mSearchTopicsAdapter.notifyDataSetChanged();
        mPresenter.getFollowingChannels(mPage);
    }

    @Override
    public void onFollowedChannelsSuccess(SourceModel response) {
        mFollowedChannels.clear();
        if (response != null && response.getSources() != null && response.getSources().size() > 0) {
            channelTitle.setVisibility(View.VISIBLE);
            mFollowedChannels.addAll(response.getSources());
        } else {
            channelTitle.setVisibility(View.GONE);
        }
        mSearchChannelsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFollowedLocationSuccess(LocationModel response) {

    }

    @Override
    public void onSuggestedTopicsSuccess(TopicsModel response) {

    }

    @Override
    public void onSuggestedChannelsSuccess(SourceModel response) {

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