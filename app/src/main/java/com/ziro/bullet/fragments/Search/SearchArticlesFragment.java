package com.ziro.bullet.fragments.Search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.articledetail.ArticleDetailNew;
import com.ziro.bullet.adapters.discover_new.DiscoverArticlesAdapter;
import com.ziro.bullet.adapters.feed.FeedAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.AuthorListResponse;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.fragments.DividerItemDecorator;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.CommunityCallback;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.SearchTabsInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.presenter.SearchTabPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.PlayerSelector;

public class SearchArticlesFragment extends Fragment implements SearchTabsInterface {

    private static final String TAG = SearchArticlesFragment.class.getSimpleName();
    private static String BUNDLE_QUERY = "";
    private static GoHome goHomeTempHome;
    private static GoHome goHome1;
    private static GoHome goHomeMainActivity;
    private OnAllFragmentInteractionListener listener;
    private RecyclerView mRecyclerView;
    private LinearLayout search_skeleton;
    private ProgressBar progress;
    private EditText etSearchArticles;
    private ImageView ivClearText;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private ImageView back_img;
    private LinearLayoutManager manager;
    private SearchTabPresenter mPresenter;
    private String mSearchChar = "";
    private String currentPage = "";
    private LinearLayout ll_no_results = null;
    private boolean isReload = false;
    private FeedAdapter mHomeAdapter;
    private DiscoverArticlesAdapter discoverArticlesAdapter;
    private PictureLoadingDialog mLoadingDialog;
    private ArrayList<Article> contentArrayList = new ArrayList<>();
    private SpeedyLinearLayoutManager cardLinearLayoutManager;
    private PlayerSelector selector = PlayerSelector.DEFAULT;
    private boolean adFailedStatus;
    private PrefConfig prefConfig;

    public static SearchArticlesFragment getInstance(Bundle bundle2, GoHome goHomeTempHomeListener) {

        SearchArticlesFragment searchArticlesFragment = new SearchArticlesFragment();
        searchArticlesFragment.setArguments(bundle2);
        goHome1 = goHomeTempHomeListener;
        return searchArticlesFragment;
    }

    public static SearchArticlesFragment newInstance(String query, GoHome goHome1) {
        goHomeTempHome = goHome1;
        goHomeMainActivity = goHome1;
        SearchArticlesFragment allFragment = new SearchArticlesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_QUERY, query);
        bundle.putString(BUNDLE_QUERY, query);
        allFragment.setArguments(bundle);
//        return allFragment; check and add this
        return new SearchArticlesFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (goHome1 != null) {
            goHome1.scrollUp();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(BUNDLE_QUERY)) {
            mSearchChar = getArguments().getString(BUNDLE_QUERY);
        }

        if (getArguments() != null && getArguments().containsKey("query")) {
            mSearchChar = getArguments().getString("query");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_articles_fragment, container, false);

        bindView(view);
        init();
        searchQuery(mSearchChar);
        setPagination();

        return view;
    }

    private void setPagination() {
//        manager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);

        mRecyclerView.addOnScrollListener(new PaginationListener(cardLinearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                mPresenter.searchArticles(mSearchChar, currentPage, true);
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
//                Utils.hideKeyboard(TopicsActivity.this, recyclerView);
            }

            @Override
            public void onScrolling(@NonNull @NotNull RecyclerView recyclerView, int newState) {

            }
        });

    }

    public void searchQuery(String mSearchChar) {
        currentPage = "";
        ll_no_results.setVisibility(View.GONE);
        mPresenter.searchArticles(mSearchChar, currentPage, false);

    }

    private void init() {

        prefConfig = new PrefConfig(getContext());
        mPresenter = new SearchTabPresenter(getActivity(), this);
        etSearchArticles.setText(mSearchChar);

        etSearchArticles.addTextChangedListener(new TextWatcher() {
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

        etSearchArticles.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String searchQuery = etSearchArticles.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchQuery)) {
                        mSearchChar = searchQuery;
                        mPresenter.searchArticles(mSearchChar, "", false);
                        Utils.hideKeyboard(requireActivity(), etSearchArticles);
                    }
                    return true;
                }
                return false;
            }
        });

        ivClearText.setOnClickListener(v -> {
            etSearchArticles.setText("");
            Utils.hideKeyboard(requireActivity(), etSearchArticles);
        });

        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(requireActivity(), view);
                try {
                    requireActivity().onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mHomeAdapter = new FeedAdapter(new CommentClick() {
            @Override
            public void commentClick(int position, String id) {

            }

            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void onNewDetailClick(int position, Article article, List<Article> articlelist) {
                Intent intent = new Intent(getContext(), ArticleDetailNew.class);
                ArrayList<Article> itemsa = new ArrayList<>();
                itemsa = (ArrayList<Article>) articlelist;
                intent.putExtra("myArrayList", new Gson().toJson(itemsa));
                intent.putExtra("type", "type");
                intent.putExtra("articleID", article.getId());
                intent.putExtra("position", position);
                intent.putExtra("mContextId", article.getId());
                intent.putExtra("NextPageApi", "");
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {

            }
        }, false, (AppCompatActivity) getActivity(), contentArrayList, "AuthorArticles", true, new DetailsActivityInterface() {
            @Override
            public void playAudio(AudioCallback audioCallback, String fragTag, AudioObject audio) {
                if (goHomeMainActivity != null) {
                    goHomeMainActivity.sendAudioToTempHome(audioCallback, fragTag, "", audio);
                }
            }

            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }
        }, goHomeMainActivity, new ShareToMainInterface() {
            @Override
            public void removeItem(String id, int position) {

            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

            }

            @Override
            public void unarchived() {

            }
        }, null, new CommunityCallback() {
            @Override
            public void authors(AuthorListResponse response) {

            }

            @Override
            public void reels(ReelResponse response) {

            }

            @Override
            public void loaderShow(boolean flag) {

            }

            @Override
            public void error(String error) {

            }

            @Override
            public void error404(String error) {

            }

            @Override
            public void success(ArticleResponse response, boolean offlineData) {

            }

            @Override
            public void successArticle(Article article) {

            }

            @Override
            public void homeSuccess(HomeResponse homeResponse, String currentPage) {

            }

            @Override
            public void nextPosition(int position) {

            }

            @Override
            public void nextPositionNoScroll(int position, boolean shouldNotify) {

            }

            @Override
            public void onItemHeightMeasured(int height) {

            }
        }, new OnGotoChannelListener() {
            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {

            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position) {

            }

            @Override
            public void onArticleSelected(Article article) {

            }
        },
                new ShowOptionsLoaderCallback() {
                    @Override
                    public void showLoader(boolean show) {

                    }
                },
                new AdFailedListener() {
                    @Override
                    public void onAdFailed() {
                        removeAdItem();
                        adFailedStatus = true;
                    }
                }, getLifecycle()
        );

        discoverArticlesAdapter = new DiscoverArticlesAdapter(contentArrayList, false, new CommentClick() {
            @Override
            public void commentClick(int position, String id) {

            }

            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void onNewDetailClick(int position, Article article, List<Article> articlelist) {
                Intent intent = new Intent(getContext(), ArticleDetailNew.class);
                ArrayList<Article> itemsa = new ArrayList<>(); // your ArrayList of Article objects

                itemsa = (ArrayList<Article>) articlelist;
                intent.putExtra("myArrayList", new Gson().toJson(itemsa));
                intent.putExtra("type", "type");
                intent.putExtra("articleID", article.getId());
                intent.putExtra("position", position);
                intent.putExtra("mContextId", article.getId());
                intent.putExtra("NextPageApi", "");
                startActivityForResult(intent, Constants.CommentsRequestCode);

            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {

            }
        });

        cardLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(cardLinearLayoutManager);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        if (!TextUtils.isEmpty(mSearchChar)) {
            mRecyclerView.setAdapter(mHomeAdapter);
        } else {
            mRecyclerView.setAdapter(discoverArticlesAdapter);
        }
//        mRecyclerView.setCacheManager(mArticlesAdapter);
//        mRecyclerView.setPlayerSelector(selector);

    }

    /**
     * loading dialog
     */
    protected void showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(getContext());
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissProgressDialog() {
        try {
            if (mLoadingDialog != null
                    && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            mLoadingDialog = null;
            e.printStackTrace();
        }
    }

    private void removeAdItem() {
        if (contentArrayList != null && contentArrayList.size() > 0) {
            for (int i = 0; i < contentArrayList.size(); i++) {
                if (contentArrayList.get(i) != null && !TextUtils.isEmpty(contentArrayList.get(i).getType())) {
                    if (contentArrayList.get(i).getType().equals("FB_Ad") || contentArrayList.get(i).getType().equals("G_Ad")) {
                        contentArrayList.remove(i);
                        if (mHomeAdapter != null)
                            mHomeAdapter.notifyItemRemoved(i);
                        removeAdItem();
                        return;
                    }
                }
            }
        }
    }

    private boolean isLast() {
        return currentPage.equalsIgnoreCase("");
    }

    private void bindView(View view) {
        mRecyclerView = view.findViewById(R.id.rvList);
        search_skeleton = view.findViewById(R.id.search_skeleton);
        ll_no_results = view.findViewById(R.id.ll_no_results);
        progress = view.findViewById(R.id.progress);
//        tv_search2 = view.findViewById(R.id.tv_search2);
        back_img = view.findViewById(R.id.back_img);
        etSearchArticles = view.findViewById(R.id.et_searchtext);
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
        isLoading = false;
        if (response != null) {
            mRecyclerView.setVisibility(View.VISIBLE);
            search_skeleton.setVisibility(View.GONE);

            if (response.getMeta() != null)
                currentPage = response.getMeta().getNext();

            if (TextUtils.isEmpty(currentPage)) {
                isLastPage = true;
//                    contentArrayList.clear();
            }
            if (!isPagination) {
                contentArrayList.clear();
            }

            if (response.getArticles() != null && response.getArticles().size() > 0) {
                for (int position = 0; position < response.getArticles().size(); position++) {
                    Article article = response.getArticles().get(position);
                    Glide.with(requireActivity())
                            .load(article.getImage())
                            .diskCacheStrategy(DiskCacheStrategy.ALL).preload();
//                    if (prefConfig != null && prefConfig.getAds() != null && prefConfig.getAds().isEnabled()) {
//                        int interval = 10;
//                        if (prefConfig.getAds().getInterval() != 0) {
//                            interval = prefConfig.getAds().getInterval();
//                        }
//                        int newCount = contentArrayList.size();
//                        if (newCount != 0 && newCount % interval == 0 && !adFailedStatus) {
//                            Log.e("ADS", "AD Added");
//                            Article adArticle1 = new Article();
//                            if (!TextUtils.isEmpty(prefConfig.getAds().getType()) && prefConfig.getAds().getType().equalsIgnoreCase("facebook")) {
//                                adArticle1.setType("FB_Ad");
//                            } else {
//                                adArticle1.setType("G_Ad");
//                            }
//                            contentArrayList.add(adArticle1);
//                        }
//                    }
                    contentArrayList.add(article);
                }

//                if (fragmentVisible) {
//                    selectCardPosition(mArticlePosition);
//                    if (mCardAdapter != null) {
//                        mCardAdapter.notifyDataSetChanged();
//                    }
//                }
                if (!TextUtils.isEmpty(mSearchChar) && !(mRecyclerView.getAdapter() instanceof FeedAdapter)) {
                    mRecyclerView.setAdapter(mHomeAdapter);
                }
                if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter() instanceof FeedAdapter) {
                    mHomeAdapter.notifyDataSetChanged();
                } else if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter() instanceof DiscoverArticlesAdapter) {
                    discoverArticlesAdapter.notifyDataSetChanged();
                }
            }
            if (contentArrayList.size() <= 0) {
                mRecyclerView.setVisibility(View.GONE);
                ll_no_results.setVisibility(View.VISIBLE);
                search_skeleton.setVisibility(View.GONE);
            } else {
                ll_no_results.setVisibility(View.GONE);
            }

            if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter() instanceof FeedAdapter) {
                mHomeAdapter.notifyDataSetChanged();
            } else if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter() instanceof DiscoverArticlesAdapter) {
                discoverArticlesAdapter.notifyDataSetChanged();
            }

        }
    }

    private void selectCardPosition(int position) {
        if (contentArrayList.size() > 0 && position > -1 && position < contentArrayList.size()) {
            for (int i = 0; i < contentArrayList.size(); i++) {
                contentArrayList.get(i).setSelected(false);
            }
            contentArrayList.get(position).setSelected(true);
        }

        if (mHomeAdapter != null)
            mHomeAdapter.notifyDataSetChanged();
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