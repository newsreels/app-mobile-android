package com.ziro.bullet.bottomSheet;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.ChannelPostActivity;
import com.ziro.bullet.adapters.ArticlesCategoryAdapterNew;
import com.ziro.bullet.adapters.CategoryAdapter;
import com.ziro.bullet.adapters.TopicsAdapter;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.author.AuthorSearchResponse;
import com.ziro.bullet.data.models.home.HomeModel;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.interfaces.BottomSheetInterface;
import com.ziro.bullet.interfaces.CategoryCallback;
import com.ziro.bullet.interfaces.FollowingInterface;
import com.ziro.bullet.interfaces.HomeCallback;
import com.ziro.bullet.interfaces.SearchInterface;
import com.ziro.bullet.interfaces.TopicInterface;
import com.ziro.bullet.model.FollowResponse;
import com.ziro.bullet.model.Menu.Category;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.NewDiscoverPage.NewDiscoverResponse;
import com.ziro.bullet.model.Tabs.DataItem;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.FollowingPresenter;
import com.ziro.bullet.presenter.HomePresenter;
import com.ziro.bullet.presenter.TopicPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import retrofit2.Response;

public class ForYouReelSheet extends BottomSheetDialogFragment implements SearchInterface, FollowingInterface, HomeCallback, TopicsAdapter.ItemClickListener, TopicInterface {

    //Const
    private static final String KEY_MODE = "mode";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SELECTED_CATEGORY = "selected_category";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_FOR_YOU = "for_you";
    private static final String KEY_FOLLOWING = "following";
    //Objects
    private static HomeModel mHomeModel;
    //Listeners
    private static BottomSheetInterface bottomSheetInterface;
    private static View.OnClickListener listener;
    //Views
    private LinearLayout ll_reels_category;
    private ImageView ivBack;
    private ProgressBar progress;
    private FrameLayout bottomSheet;
    private RecyclerView rvCategories;
    private ImageView ivSearchCat;
    private EditText etSearchCat;
    private LinearLayout llSearchTopic;
    private BottomSheetBehavior behavior;
    //Data Objects
    private ArrayList<Category> searchedTopicsList = new ArrayList<>();
    private ArrayList<DataItem> mCategoriesList = new ArrayList<>();
    private boolean isChannelsEmpty = false, isTopicsEmpty = false, isAuthorsEmpty = false, isPlacesEmpty = false;
    private String selected_category;
    private String mode, type;
    private boolean isLastPage;
    private String nextPage, searchQuery;
    private CategoryAdapter adapter;
    private ArticlesCategoryAdapterNew articlesCategoryAdapterNew;
    private TopicsAdapter topicsAdapter;
    private FollowingPresenter followingPresenter;
    private HomePresenter mHomePresenter;
    private TopicPresenter topicPresenter;

    public static ForYouReelSheet getInstance(String mode, String type, String selected_category, HomeModel model1, View.OnClickListener listener1, BottomSheetInterface bottomSheetInterface1) {
        listener = listener1;
        bottomSheetInterface = bottomSheetInterface1;
        ForYouReelSheet reelViewMoreSheet = new ForYouReelSheet();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_MODE, mode);
        bundle.putString(KEY_TYPE, type);
        bundle.putString(KEY_SELECTED_CATEGORY, selected_category);
        bundle.putString(KEY_CATEGORIES, new Gson().toJson(model1));
        reelViewMoreSheet.setArguments(bundle);
        return reelViewMoreSheet;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog d = (BottomSheetDialog) getDialog();
        if (d != null) {
            setupFullHeight(d);
            bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null && getContext() != null) {
                behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setDraggable(false);
        behavior.setSkipCollapsed(true);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("FOR_YOU_TAG", "onCreateView: ");
        View view = inflater.inflate(R.layout.for_you_reel, container, false);
        if (getArguments() != null && getArguments().containsKey(KEY_SELECTED_CATEGORY)) {
            selected_category = getArguments().getString(KEY_SELECTED_CATEGORY);
        }
        if (getArguments() != null && getArguments().containsKey(KEY_MODE)) {
            mode = getArguments().getString(KEY_MODE);
        }
        if (getArguments() != null && getArguments().containsKey(KEY_TYPE))
            type = getArguments().getString(KEY_TYPE);
        if (getArguments() != null && getArguments().containsKey(KEY_CATEGORIES)) {
            mHomeModel = new Gson().fromJson(getArguments().getString(KEY_CATEGORIES), HomeModel.class);
        }
        initView(view);
        init();
        setListener();
        return view;
    }

    private void init() {
        mHomePresenter = new HomePresenter(getActivity(), this);
        topicPresenter = new TopicPresenter(getActivity(), this);
        followingPresenter = new FollowingPresenter(getActivity(), this);

        mCategoriesList = mHomeModel.getData();
        if (!TextUtils.isEmpty(mode)) {
            if (mode.equalsIgnoreCase("menu")) {
                rvCategories.setVisibility(View.GONE);
//                ll_reels_category.setVisibility(View.VISIBLE);
            } else {

                ItemTouchHelper.Callback touchHelperCallback = new ItemTouchHelper.Callback() {
                    @Override
                    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                        int fromPosition = viewHolder.getAbsoluteAdapterPosition();

                        if ((mCategoriesList.get(fromPosition).isLocked() || mCategoriesList.get(fromPosition).isFollowed())) {
                            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
                        }
                        return 0;
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        int fromPosition = viewHolder.getAdapterPosition();
                        int toPosition = target.getAdapterPosition();
//                        List<String> catIdsList = new ArrayList<>();
                        Log.d("ForYouReel_TAG", "onMove: Item Moving");
                        if ((mCategoriesList.get(fromPosition).isLocked() || mCategoriesList.get(fromPosition).isFollowed()) &&
                                (mCategoriesList.get(toPosition).isLocked() || mCategoriesList.get(toPosition).isFollowed())) {
                            Collections.swap(mCategoriesList, fromPosition, toPosition);
                            articlesCategoryAdapterNew.notifyItemMoved(fromPosition, toPosition);
//                            for (DataItem dataItem : mCategoriesList) {
//                                if (dataItem.isLocked() || dataItem.isFollowed()) {
//                                    catIdsList.add(dataItem.getRawId());
//                                }
//                            }
//                            mHomePresenter.updateArticleCatSequence(catIdsList);
//                            bottomSheetInterface.onSequenceUpdated(mCategoriesList, fromPosition, toPosition);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                        switch (actionState) {
                            case ItemTouchHelper.ACTION_STATE_IDLE:
                                bottomSheetInterface.onSequenceUpdated(mCategoriesList);
                                break;
                            case ItemTouchHelper.ACTION_STATE_DRAG:
                                Log.d("ForYouReel_TAG", "onSelectedChanged: Item Drag");
                            default:
                                Log.d("ForYouReel_TAG", "onSelectedChanged:");
                        }
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                    }
                };

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
                if (mHomeModel != null && mHomeModel.getData() != null) {
                    rvCategories.setVisibility(View.VISIBLE);
                    ll_reels_category.setVisibility(View.GONE);
//                    adapter = new CategoryAdapter(mCategoriesList, new CategoryCallback() {
//                        @Override
//                        public void onTabClick(DataItem tab) {
//                            if (bottomSheetInterface != null && tab != null)
//                                bottomSheetInterface.onHomeTab(tab);
//                        }
//
//                        @Override
//                        public void onItemFollowClick(DataItem dataItem, int position) {
//
//                        }
//                    }, getContext(), selected_category);

                    articlesCategoryAdapterNew = new ArticlesCategoryAdapterNew(mCategoriesList, new CategoryCallback() {
                        @Override
                        public void onTabClick(DataItem tab) {
                            if (bottomSheetInterface != null && tab != null)
                                bottomSheetInterface.onHomeTab(tab);
                        }

                        @Override
                        public void onItemFollowClick(DataItem dataItem, int position) {
                            if (dataItem.isFollowed()) {
                                followingPresenter.followTopic(dataItem.getRawId(), dataItem, position);
                            } else {
                                followingPresenter.unFollowTopic(dataItem.getRawId(), dataItem, position);
                            }
                        }
                    }, getContext(), selected_category);

//                    if (TextUtils.equals(type, Constants.CAT_TYPE_REELS)) {
//                        rvCategories.setBackground(null);
//                        ivSearchCat.setVisibility(View.GONE);
//                        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
//                        rvCategories.setAdapter(adapter);
//                    } else {
                    ivSearchCat.setVisibility(View.VISIBLE);
                    itemTouchHelper.attachToRecyclerView(rvCategories);
                    rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    rvCategories.setAdapter(articlesCategoryAdapterNew);
//                    }
                } else {
                    rvCategories.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setListener() {

        Objects.requireNonNull(getDialog()).setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                if (llSearchTopic.getVisibility() == View.GONE) {
                    if (listener != null) listener.onClick(ivBack);
                    dismiss();
                } else {
                    isLastPage = false;
                    nextPage = "";
                    llSearchTopic.setVisibility(View.GONE);
                    ivSearchCat.setVisibility(View.VISIBLE);
                    Utils.hideKeyboard(requireActivity(), etSearchCat);
                    if (!(rvCategories.getAdapter() instanceof ArticlesCategoryAdapterNew)) {
                        rvCategories.setAdapter(articlesCategoryAdapterNew);
                        articlesCategoryAdapterNew.notifyDataSetChanged();
                    }
                }
                return true;
            }
            return false;
        });

        ivBack.setOnClickListener(view -> {
            if (llSearchTopic.getVisibility() == View.GONE) {
                if (listener != null) listener.onClick(ivBack);
                dismiss();
            } else {
                isLastPage = false;
                nextPage = "";
                llSearchTopic.setVisibility(View.GONE);
                ivSearchCat.setVisibility(View.VISIBLE);
                Utils.hideKeyboard(requireActivity(), etSearchCat);
                if (!(rvCategories.getAdapter() instanceof ArticlesCategoryAdapterNew)) {
                    rvCategories.setAdapter(articlesCategoryAdapterNew);
                    articlesCategoryAdapterNew.notifyDataSetChanged();
                }
            }
        });

        ivSearchCat.setOnClickListener(v -> {
            if (llSearchTopic.getVisibility() == View.GONE) {
                llSearchTopic.setVisibility(View.VISIBLE);
                ivSearchCat.setVisibility(View.GONE);
            }
        });

        etSearchCat.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                searchQuery = etSearchCat.getText().toString().trim();
                if (!TextUtils.isEmpty(searchQuery)) {
                    isLastPage = false;
                    nextPage = "";
                    mHomePresenter.searchTopics(searchQuery, nextPage, isLastPage);
                    Utils.hideKeyboard(requireActivity(), etSearchCat);
                }
                return true;
            }
            return false;
        });

        rvCategories.addOnScrollListener(new PaginationListener((LinearLayoutManager) rvCategories.getLayoutManager()) {
            @Override
            protected void loadMoreItems() {
                if (!isLastPage && !TextUtils.isEmpty(nextPage)) {
                    mHomePresenter.searchTopics(searchQuery, nextPage, isLastPage);
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return false;
            }

            @Override
            public void onScroll(int position) {

            }

            @Override
            public void onScrolling(@NonNull RecyclerView recyclerView, int newState) {

            }
        });

    }

    public void updateDataList(int position, ArrayList<DataItem> updatedTabList) {
        mHomeModel.setData(updatedTabList);
        mCategoriesList = updatedTabList;
        articlesCategoryAdapterNew.updateList(position, updatedTabList);
        bottomSheetInterface.updateTabs(mCategoriesList);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        bottomSheetInterface.dialogDismissListener();
        super.onDismiss(dialog);
    }

    private void initView(View dialogView) {
        ivSearchCat = dialogView.findViewById(R.id.iv_search);
        etSearchCat = dialogView.findViewById(R.id.et_search_cat);
        llSearchTopic = dialogView.findViewById(R.id.ll_search_topic);
        ll_reels_category = dialogView.findViewById(R.id.ll_reels_category);
        rvCategories = dialogView.findViewById(R.id.categories);
        progress = dialogView.findViewById(R.id.progress);
        ivBack = dialogView.findViewById(R.id.iv_back);
    }

    public void loaderShow(boolean flag) {
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {

    }

    @Override
    public void error404(String error) {

    }

    @Override
    public void success(CategoryResponse response, boolean isPagination) {

    }

    @Override
    public void success(HomeModel response) {
        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
            mCategoriesList = response.getData();
            articlesCategoryAdapterNew.updateList(mCategoriesList);
            bottomSheetInterface.updateTabs(mCategoriesList);
        }
    }

    @Override
    public void error(String error, int load) {

    }

    @Override
    public void onTopicFollowSuccess(DataItem dataItem, int position) {
        if (dataItem != null) {
            mHomePresenter.getHome(type);
        }
    }

    @Override
    public void searchSuccess(CategoryResponse body, boolean isPagination) {
        if (topicsAdapter == null) {
            topicsAdapter = new TopicsAdapter(requireContext(), searchedTopicsList);
            topicsAdapter.setClickListener(this);
        }
        if (body != null && body.getCategory() != null) {
            if (TextUtils.isEmpty(nextPage)) {
                searchedTopicsList.clear();
            }
            searchedTopicsList.addAll(body.getCategory());
            if (body.meta.getNext().isEmpty()) {
                isLastPage = true;
            } else {
                nextPage = body.getMeta().getNext();
            }
        }

        if (rvCategories.getAdapter() == topicsAdapter) {
            topicsAdapter.notifyDataSetChanged();
        } else {
            rvCategories.setAdapter(topicsAdapter);
        }
    }

    @Override
    public void addSuccess(int position) {

    }

    @Override
    public void deleteSuccess(int position) {

    }

    @Override
    public void getTopicsFollow(FollowResponse response, int position, Category topic) {
        if (topic != null) {
            searchedTopicsList.remove(position);
            searchedTopicsList.add(position, topic);
            topicsAdapter.notifyItemChanged(position);
            mHomePresenter.getHome(type);
        }
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
    public void onSearchArticleSuccess(ArticleResponse response, Boolean isPagination) {

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

    @Override
    public void onItemClick(View view, int position, boolean isFav) {

    }

    @Override
    public void isLastItem(boolean flag) {

    }

    @Override
    public void onItemTopicClick(int position, Category item) {
        Constants.itemPosition = position;
        Intent intent = new Intent(getContext(), ChannelPostActivity.class);
        intent.putExtra("type", TYPE.TOPIC);
        intent.putExtra("id", item.getId());
        intent.putExtra("context", item.getContext());
        intent.putExtra("name", item.getName());
        intent.putExtra("favorite", item.isFavorite());
        requireContext().startActivity(intent);
    }

    @Override
    public void onItemTopicFollowed2(int position, Category item) {
        topicPresenter.getTopicFollowPresenter(item.getId(), position, item);
    }

    @Override
    public void onItemTopicUnfollowed2(int position, Category item) {
        topicPresenter.getTopicUnfollowPresenter(item.getId(), position, item);
    }

    public void updateSearchResults() {
        try {
            if (rvCategories.getAdapter() instanceof TopicsAdapter) {
                if (Constants.itemPosition >= 0 && Constants.topicsStatusChanged != null) {
                    searchedTopicsList.get(Constants.itemPosition).setFavorite(Boolean.parseBoolean(Constants.followStatus));
                    topicsAdapter.notifyItemChanged(Constants.itemPosition);
                    Constants.itemPosition = -1;
                    Constants.topicsStatusChanged = null;
                    Constants.followStatus = null;
                    Constants.isTopicDataChange = false;
                    mHomePresenter.getHome(type);
                }
            } else if (rvCategories.getAdapter() instanceof ArticlesCategoryAdapterNew) {
                if (Constants.articleTabItem != null) {
                    mHomePresenter.getHome(type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
