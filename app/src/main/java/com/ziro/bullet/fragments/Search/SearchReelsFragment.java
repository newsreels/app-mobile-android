package com.ziro.bullet.fragments.Search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.R;
import com.ziro.bullet.adapters.discover.SearchReelsAdapter;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.fragments.test.ReelInnerActivity;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.SearchTabsInterface;
import com.ziro.bullet.interfaces.VideoInterface;
import com.ziro.bullet.mediapicker.gallery.SpacingItemDecoration;
import com.ziro.bullet.mediapicker.utils.ScreenUtils;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.ReelPresenter;
import com.ziro.bullet.presenter.SearchTabPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchReelsFragment extends Fragment implements SearchTabsInterface {

    private static final String TAG = SearchReelsFragment.class.getSimpleName();
    private static String BUNDLE_QUERY = "";
    private ArrayList<ReelsItem> mReelResponseArrayList = new ArrayList<>();
    private SearchReelsAdapter mAdapter;
    private OnAllFragmentInteractionListener listener;
    private RecyclerView mRecyclerView;
    private ProgressBar progress;
    private LinearLayout search_skeleton;
    private static GoHome goHome;
    private SearchTabPresenter mPresenter;
    private ReelPresenter reelPresenter;
    private String mSearchChar = "";
    private Boolean reelapi = false;
    private String mPage = "";
    private LinearLayout ll_no_results = null;
    //    private TextView tv_search2;
    private ImageView back_img;
    private boolean isReload = false;
    private boolean isLoading = false;
    private VideoInterface videoInterface;
    private EditText etSearchReels;
    private ImageView ivClearText;

    public static SearchReelsFragment newInstance(String query) {
        SearchReelsFragment allFragment = new SearchReelsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_QUERY, query);
        allFragment.setArguments(bundle);
        return new SearchReelsFragment();
    }

    public static SearchReelsFragment getInstance(Bundle bundle2, GoHome goHomeTempHomeListener) {

        SearchReelsFragment searchReelsFragment = new SearchReelsFragment();
        searchReelsFragment.setArguments(bundle2);
        goHome = goHomeTempHomeListener;
        return searchReelsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_QUERY)) {
            mSearchChar = getArguments().getString(BUNDLE_QUERY);
        }

        if (getArguments() != null && getArguments().containsKey("query")) {
            mSearchChar = getArguments().getString("query");
            reelapi = getArguments().getBoolean("reelsapi");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (goHome != null) {
            goHome.scrollUp();
        } else {
//            Log.e(TAG, "onViewCreated:nulll gohome ");
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
        mPage = "";
        ll_no_results.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mSearchChar) && reelPresenter != null)
            reelPresenter.searchReels(Constants.REELS_FOR_YOU, "", mSearchChar, mPage, false, true, false, "");
        else {
            reelPresenter.getReelsHome(Constants.REELS_FOR_YOU, "", "", false, true, true, "");
            mReelResponseArrayList.clear();
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
    }

    private void init() {

        if (!TextUtils.isEmpty(mSearchChar)) {
            etSearchReels.setText(mSearchChar);
        }

        etSearchReels.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    ivClearText.setVisibility(View.VISIBLE);
                } else {
                    ivClearText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etSearchReels.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String searchQuery = etSearchReels.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchQuery)) {
                        mPage = "";
                        mSearchChar = searchQuery;
                        reelPresenter.searchReels(Constants.REELS_FOR_YOU, "", mSearchChar, "", false, true, false, "");
                        Utils.hideKeyboard(requireActivity(), etSearchReels);
                    }
                    return true;
                }
                return false;
            }
        });

        ivClearText.setOnClickListener(v -> {
            etSearchReels.setText("");
            Utils.hideKeyboard(requireActivity(), etSearchReels);
        });

        videoInterface = new VideoInterface() {
            @Override
            public void loaderShow(boolean flag) {
                search_skeleton.setVisibility(flag ? View.VISIBLE : View.GONE);
                mRecyclerView.setVisibility(flag ? View.GONE : View.VISIBLE);
            }

            @Override
            public void error(String error) {
                search_skeleton.setVisibility(View.GONE);
                ll_no_results.setVisibility(View.GONE);
            }

            @Override
            public void error404(String error) {
                search_skeleton.setVisibility(View.GONE);
                ll_no_results.setVisibility(View.GONE);
            }

            @Override
            public void success(ReelResponse reelResponse, boolean reload) {
                search_skeleton.setVisibility(View.GONE);
                ll_no_results.setVisibility(View.GONE);
                if (TextUtils.isEmpty(mPage)) {
                    mReelResponseArrayList.clear();
                }

                if (reelResponse.getMeta() != null) {
                    mPage = reelResponse.getMeta().getNext();
                }
                if (reelResponse.getReels() != null && reelResponse.getReels().size() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mReelResponseArrayList.addAll(reelResponse.getReels());
                }
                if (mAdapter != null) {
                    mAdapter.setNextPageParam(mPage);
                    mAdapter.updateRecords(mReelResponseArrayList);
                    mAdapter.notifyDataSetChanged();
                }
                if (mReelResponseArrayList.size() > 0) {
                    ll_no_results.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    ll_no_results.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }

                if (reelResponse.getReels() != null && reelResponse.getReels().size() < 12 && !TextUtils.isEmpty(mPage)) {
                    if (TextUtils.isEmpty(mSearchChar)) {
                        reelPresenter.getReelsHome(Constants.REELS_FOR_YOU, "", mPage, false, false, true, "");
                    } else {
                        reelPresenter.searchReels(Constants.REELS_FOR_YOU, "", mSearchChar, mPage, false, false, false, "");
                    }
                }
            }

            @Override
            public void nextVideo(int position) {

            }
        };

        mPresenter = new SearchTabPresenter(getActivity(), this);
        reelPresenter = new ReelPresenter(getActivity(), videoInterface);
        mAdapter = new SearchReelsAdapter(mReelResponseArrayList, getContext(), "userContext");
        if (mSearchChar != null) {
            mAdapter.updateQuery(mSearchChar, reelapi, this);
        }


        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new SpacingItemDecoration(2, ScreenUtils.dip2px(getContext(), 10), false));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Utils.hideKeyboard(getActivity(), mRecyclerView);
                if (dy > 0) { // only when scrolling up

                    final int visibleThreshold = 3;

                    GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                    int lastItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();
                    if (!isLoading && !TextUtils.isEmpty(mPage) && currentTotalCount <= lastItem + visibleThreshold) {
                        //show your loading view
                        // load content in background
                        if (!TextUtils.isEmpty(mSearchChar)) {
//                            mPresenter.searchReel(mSearchChar, mPage);
                            reelPresenter.searchReels(Constants.REELS_FOR_YOU, "", mSearchChar, mPage, false, false, false, "");
                        } else {
                            reelPresenter.getReelsHome(Constants.REELS_FOR_YOU, "", mPage, false, false, false, "");
                        }
                    }
                }
            }
        });
        isReload = false;
    }

    private boolean isLast() {
        return mPage.equalsIgnoreCase("");
    }

    private void bindView(View view) {
        mRecyclerView = view.findViewById(R.id.rvList);
//        tv_search2 = view.findViewById(R.id.tv_search2);
        ll_no_results = view.findViewById(R.id.ll_no_results);
        progress = view.findViewById(R.id.progress);
        back_img = view.findViewById(R.id.back_img);
        search_skeleton = view.findViewById(R.id.search_skeleton);
        etSearchReels = view.findViewById(R.id.et_searchtext);
        ivClearText = view.findViewById(R.id.iv_clear_text);
    }

    @Override
    public void loaderShow(boolean flag) {
        search_skeleton.setVisibility(flag ? View.VISIBLE : View.GONE);
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

    }

    @Override
    public void onReelSuccess(ReelResponse response) {
        if (response != null) {
            search_skeleton.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(mPage)) {
            mReelResponseArrayList.clear();
        }

        if (response.getMeta() != null) {
            mPage = response.getMeta().getNext();
        }
        if (response.getReels() != null && response.getReels().size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mReelResponseArrayList.addAll(response.getReels());
        }
        if (mAdapter != null) {
            mAdapter.setNextPageParam(mPage);
            mAdapter.notifyDataSetChanged();
        }
        if (mReelResponseArrayList.size() > 0) {
            ll_no_results.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            search_skeleton.setVisibility(View.GONE);
        } else {
            ll_no_results.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        if (response.getReels() != null && response.getReels().size() < 12) {
            reelPresenter.getReelsHome(Constants.REELS_FOR_YOU, "", mPage, false, false, true, "");
        }
    }

    @Override
    public void searchChildSecondOnClick(ReelsItem reelsItem, List<ReelsItem> reelsList, int position, String page) {
        Intent intent = new Intent(getContext(), ReelInnerActivity.class);
        if (reelsItem.getAuthor() != null && reelsItem.getAuthor().size() > 0) {
            intent.putExtra(ReelInnerActivity.REEL_F_AUTHOR_ID, reelsItem.getAuthor().get(0).getId());
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, "userContext");
        }
        if (reelsItem.getSource() != null) {
            intent.putExtra(ReelInnerActivity.REEL_F_SOURCE_ID, reelsItem.getSource().getId());
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, "userContext");
        }
        intent.putExtra(ReelInnerActivity.REEL_F_MODE, "public");
        intent.putExtra(ReelInnerActivity.REEL_F_TITLE, "Reels");
        intent.putExtra(ReelInnerActivity.REEL_F_PAGE, page);//page
        intent.putExtra(ReelInnerActivity.REEL_POSITION, 0);
//        intent.putExtra(ReelInnerFragment.API_CALL, reelapi);

        if (reelapi) {
            //search page
            intent.putExtra(ReelInnerActivity.REEL_F_TITLE, mSearchChar);
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, reelsItem.getContext());
        } else {
            //discover page
            intent.putExtra(ReelInnerActivity.REEL_F_TITLE, "Trending Reels");
            intent.putExtra(ReelInnerActivity.REEL_F_CONTEXT, "userContext");
        }


        intent.putParcelableArrayListExtra(ReelInnerActivity.REEL_F_DATALIST, (ArrayList<? extends Parcelable>) reelsList);
        getContext().startActivity(intent);
    }

    @Override
    public void onRelevantArticlesSuccess(ArticleResponse response) {

    }

    public interface OnAllFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void onItemClicked(TYPE type, String context, String id, String name, boolean favorite);
    }
}