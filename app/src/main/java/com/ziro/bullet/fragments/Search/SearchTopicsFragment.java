package com.ziro.bullet.fragments.Search;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.following.SuggestedTopicsAdapter;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.interfaces.SearchTabsInterface;
import com.ziro.bullet.model.Menu.Category;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.SearchTabPresenter;
import com.ziro.bullet.utills.PaginationListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchTopicsFragment extends Fragment implements SearchTabsInterface {

    private static final String TAG = SearchTopicsFragment.class.getSimpleName();
    private static String BUNDLE_QUERY = "";
    private OnAllFragmentInteractionListener listener;
    private RecyclerView mRecyclerView;
    private ProgressBar progress;
    private SearchTabPresenter mPresenter;
    private String mSearchChar = "";
    private String mPage = "";
    private LinearLayout ll_no_results = null;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private ArrayList<Category> mContentArrayList = new ArrayList<>();
    private SuggestedTopicsAdapter mChannelsAdapter;
    private LinearLayoutManager layoutManager;

    public static SearchTopicsFragment newInstance(String query) {
        SearchTopicsFragment allFragment = new SearchTopicsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_QUERY, query);
        allFragment.setArguments(bundle);
        return new SearchTopicsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_QUERY)) {
            mSearchChar = getArguments().getString(BUNDLE_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_reels_fragment, container, false);
        bindView(view);
        init();
        searchQuery(mSearchChar);
        return view;
    }

    public void searchQuery(String mSearchChar) {
        Log.d(TAG, "searchQuery() called with: mSearchChar = [" + mSearchChar + "]");
        Log.d(TAG, "searchQuery() called with: mPresenter = [" + mPresenter + "]");
        ll_no_results.setVisibility(View.GONE);
        mPage = "";
        if (!TextUtils.isEmpty(mSearchChar) && mPresenter != null)
            mPresenter.searchTopics(mSearchChar, mPage);
        else {
            mContentArrayList.clear();
            if (mChannelsAdapter != null)
                mChannelsAdapter.notifyDataSetChanged();
        }
    }

    private void init() {
        mPresenter = new SearchTabPresenter(getActivity(), this);
        mChannelsAdapter = new SuggestedTopicsAdapter(getActivity(), mContentArrayList);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mChannelsAdapter);
        mRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                mPresenter.searchChannels(mSearchChar, mPage);
            }

            @Override
            public boolean isLastPage() {
                return isLast();
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
    }

    private boolean isLast() {
        return mPage.equalsIgnoreCase("");
    }

    private void bindView(View view) {
        mRecyclerView = view.findViewById(R.id.rvList);
        ll_no_results = view.findViewById(R.id.ll_no_results);
        progress = view.findViewById(R.id.progress);
    }

    @Override
    public void loaderShow(boolean flag) {
        progress.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void error(String error, int load) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchArticleSuccess(ArticleResponse response, Boolean isPagination) {

    }


    @Override
    public void onRelevantSuccess(RelevantResponse response) {

    }

    @Override
    public void onSearchChannelsSuccess(SourceModel response) {

    }

    @Override
    public void onSearchPlacesSuccess(LocationModel response) {

    }

    @Override
    public void onSearchTopicsSuccess(CategoryResponse response) {
        if (TextUtils.isEmpty(mPage)) {
            mContentArrayList.clear();
        }

        if (response != null) {
            if (response.getMeta() != null)
                mPage = response.getMeta().getNext();
            if (response.getCategory().size() > 0) {
                ll_no_results.setVisibility(View.GONE);
                mContentArrayList.clear();
                mContentArrayList.addAll(response.getCategory());
                if (mChannelsAdapter != null)
                    mChannelsAdapter.notifyDataSetChanged();
            } else {
                ll_no_results.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onReelSuccess(ReelResponse response) {

    }

    @Override
    public void searchChildSecondOnClick(ReelsItem response, List<ReelsItem> reelsList, int position, String page) {

    }

    @Override
    public void onRelevantArticlesSuccess(ArticleResponse response) {

    }

    public interface OnAllFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void onItemClicked(TYPE type, String context, String id, String name, boolean favorite);

        void onItemClicked(TYPE type, String id, String name, boolean favorite);
    }
}