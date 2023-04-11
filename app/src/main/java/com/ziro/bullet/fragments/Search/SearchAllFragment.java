package com.ziro.bullet.fragments.Search;

import static androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE;
import static com.ziro.bullet.adapters.MainActivityAdapter.TYPE_SEARCH;

import android.content.Intent;
import android.graphics.Rect;
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

import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BulletDetailActivity;
import com.ziro.bullet.activities.CommentsActivity;
import com.ziro.bullet.adapters.relevant.SearchRelevantMainAdapter;
import com.ziro.bullet.adapters.relevant.callbacks.SourceFollowingCallback;
import com.ziro.bullet.adapters.relevant.callbacks.TopicsFollowingCallback;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.NewFeed.HomeResponse;
import com.ziro.bullet.data.models.location.LocationModel;
import com.ziro.bullet.data.models.relevant.RelevantItem;
import com.ziro.bullet.data.models.relevant.RelevantResponse;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.fragments.FollowingDetailActivity;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.SearchTabsInterface;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ViewItemClickListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.model.News.Category;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.presenter.SearchTabPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.SpeedyLinearLayoutManager;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchAllFragment extends Fragment implements SearchTabsInterface {

    private static final String TAG = SearchAllFragment.class.getSimpleName();
    private static String BUNDLE_QUERY = "";
    private OnAllFragmentInteractionListener listener;
    private RecyclerView mRvArticles;
    private ProgressBar progress;
    private SearchTabPresenter mPresenter;
    private String mSearchChar = "";
    private String mPage = "";
    private ArrayList<Article> mContentArrayList = new ArrayList<>();
    private SearchRelevantMainAdapter mSearchRelevantMainAdapter;
    private SpeedyLinearLayoutManager speedyLinearLayoutManager = new SpeedyLinearLayoutManager(getContext());
    private final ViewItemClickListener viewItemClickListener = (view, type, data) -> {
        switch (type) {
            case ViewItemClickListener.TYPE_RELEVANT_TOPIC_SEE_ALL:
                FollowingDetailActivity.launchActivity(getActivity(), "topics", mSearchChar);
                break;
            case ViewItemClickListener.TYPE_RELEVANT_CHANNEL_SEE_ALL:
                FollowingDetailActivity.launchActivity(getActivity(), "channels", mSearchChar);
                break;
            case ViewItemClickListener.TYPE_RELEVANT_AUTHOR_SEE_ALL:
                FollowingDetailActivity.launchActivity(getActivity(), "authors", mSearchChar);
                break;
            case ViewItemClickListener.TYPE_RELEVANT_PLACE_SEE_ALL:
                FollowingDetailActivity.launchActivity(getActivity(), "places", mSearchChar);
                break;
        }
    };
    private static GoHome goHomeMainActivity;
    private int mArticlePosition = 0;
    private boolean isLoading = false;
    private String mArticleContext = null;
    private LinearLayout ll_no_results = null;

    public static SearchAllFragment newInstance(String query, GoHome goHome1) {
        goHomeMainActivity = goHome1;
        SearchAllFragment searchAllFragment = new SearchAllFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_QUERY, query);
        searchAllFragment.setArguments(bundle);
        return new SearchAllFragment();
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
        View view = inflater.inflate(R.layout.search_all_fragment, container, false);
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
        if (!TextUtils.isEmpty(mSearchChar) && mPresenter != null) {
            mPresenter.getRelevant(mSearchChar);
        } else {
            mContentArrayList.clear();
            if (mSearchRelevantMainAdapter != null) {
                mSearchRelevantMainAdapter.setRelevantItemArrayList(new ArrayList<RelevantItem>());
                mSearchRelevantMainAdapter.notifyDataSetChanged();
            }
        }
    }

    private void init() {
        mPresenter = new SearchTabPresenter(getActivity(), this);
        mSearchRelevantMainAdapter = new SearchRelevantMainAdapter(new CommentClick() {
            @Override
            public void commentClick(int position, String id) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("article_id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);
            }

            @Override
            public void onDetailClick(int position, Article article) {
                Intent intent = new Intent(getContext(), BulletDetailActivity.class);
                intent.putExtra("article", new Gson().toJson(article));
                intent.putExtra("position", position);
                startActivityForResult(intent, Constants.CommentsRequestCode);

            }

            @Override
            public void fullscreen(int position, Article article, long duration, String mode, boolean isManual) {

            }
        }, goHomeMainActivity, new ShareToMainInterface() {
            @Override
            public void removeItem(String id, int position) {

            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
                if (listener != null) {
                    listener.onItemClicked(type, "", id, name, favorite);
                }
            }

            @Override
            public void unarchived() {

            }
        }, null, new NewsCallback() {
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
                if (listener != null) {
                    listener.onItemClicked(type, "", id, name, favorite);
                }
            }

            @Override
            public void onItemClicked(TYPE type, String id, String name, boolean favorite, Article article, int position) {

            }

            @Override
            public void onArticleSelected(Article article) {
                Constants.mute_disable = article.isMute();
                if (Constants.mute_disable) {
                    if (goHomeMainActivity != null)
                        goHomeMainActivity.sendAudioEvent("mute");
                } else {
                    if (!Constants.muted) {
                        if (goHomeMainActivity != null)
                            goHomeMainActivity.sendAudioEvent("unmute");
                    } else {
                        if (goHomeMainActivity != null)
                            goHomeMainActivity.sendAudioEvent("mute");
                    }
                }
            }
        }, getActivity(), mContentArrayList, true, TYPE_SEARCH,
                new TopicsFollowingCallback() {
                    @Override
                    public void onItemFollowed(Topics topic) {
                        if (listener != null)
                            listener.dataChanged(TYPE.TOPIC, "");
                    }

                    @Override
                    public void onItemUnfollowed(Topics topic) {
                        if (listener != null)
                            listener.dataChanged(TYPE.TOPIC, "");
                    }

                    @Override
                    public void onItemClicked(Topics topic) {
                        if (listener != null)
                            listener.onItemClicked(TYPE.TOPIC, "", topic.getId(), topic.getName(), topic.isFavorite());
                    }
                },
                new SourceFollowingCallback() {
                    @Override
                    public void onItemFollowed(Source source) {
                        if (listener != null)
                            listener.dataChanged(TYPE.SOURCE, "");
                    }

                    @Override
                    public void onItemUnfollowed(Source source) {
                        if (listener != null)
                            listener.dataChanged(TYPE.SOURCE, "");
                    }

                    @Override
                    public void onItemClicked(Source source) {
                        if (listener != null)
                            listener.onItemClicked(TYPE.SOURCE, "", source.getId(), source.getName(), source.isFavorite());

                    }
                },
                new SearchRelevantMainAdapter.FollowingCallback() {

                    @Override
                    public void onItemFollowed(com.ziro.bullet.data.models.relevant.Location location) {
                        if (listener != null)
                            listener.dataChanged(TYPE.LOCATION, "");
                    }

                    @Override
                    public void onItemUnfollowed(com.ziro.bullet.data.models.relevant.Location location) {
                        if (listener != null)
                            listener.dataChanged(TYPE.LOCATION, "");
                    }

                    @Override
                    public void onItemClicked(com.ziro.bullet.data.models.relevant.Location location) {
                        if (listener != null)
                            listener.onItemClicked(TYPE.LOCATION, location.getContext(), location.getId(), location.getName(), location.isFavorite());
                    }
                }, viewItemClickListener,
                new DetailsActivityInterface() {
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
                },
                show -> loaderShow(show),
                (view, type, data) -> {
                    if (data instanceof Author) {
                        Author author = (Author) data;
                        if (getContext() != null && author != null) {
                            Utils.openAuthor(getContext(), author);
                        }
                    }
                },
                () -> {

                }
        );

        mRvArticles.setLayoutManager(speedyLinearLayoutManager);
        mRvArticles.setAdapter(mSearchRelevantMainAdapter);
        mRvArticles.clearOnScrollListeners();


//        mRvArticles.setOnFlingListener(new RecyclerView.OnFlingListener() {
//
//            @Override
//            public boolean onFling(int velocityX, int velocityY) {
//                if (abs(velocityY) > 15000) {
//                    velocityY = 5500 * (int) Math.signum((double) velocityY);
//                    mRvArticles.fling(velocityX, velocityY);
//                    return true;
//                } else if (abs(velocityY) > 10000 && abs(velocityY) != 6500) {
//                    velocityY = 4500 * (int) Math.signum((double) velocityY);
//                    mRvArticles.fling(velocityX, velocityY);
//                    return true;
//                } else if (abs(velocityY) > 5000 && abs(velocityY) != 5500 && abs(velocityY) != 4500) {
//                    velocityY = 3000 * (int) Math.signum((double) velocityY);
//                    mRvArticles.fling(velocityX, velocityY);
//                    return true;
//                }
//                return false;
//            }
//        });

        mRvArticles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == SCROLL_STATE_IDLE) {
                    Constants.auto_scroll = true;


                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mRvArticles.getLayoutManager());

                    final int firstPosition = layoutManager.findFirstVisibleItemPosition();

                    if (firstPosition != -1) {

                        Rect rvRect = new Rect();
                        mRvArticles.getGlobalVisibleRect(rvRect);

                        Rect rowRect = new Rect();
                        layoutManager.findViewByPosition(firstPosition).getGlobalVisibleRect(rowRect);

//                        int percentFirst;
//                        if (rowRect.bottom >= rvRect.bottom) {
//                            int visibleHeightFirst = rvRect.bottom - rowRect.top;
//                            percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
//                        } else {
//                            int visibleHeightFirst = rowRect.bottom - rvRect.top;
//                            percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(firstPosition).getHeight();
//                        }
//
//                        if (percentFirst > 100)
//                            percentFirst = 100;
//
//                        if (percentFirst >= 90) {
                        mArticlePosition = firstPosition;
//                        } else {
//                            mArticlePosition++;
//                        }

                        mArticlePosition = mArticlePosition - (mSearchRelevantMainAdapter != null ? mSearchRelevantMainAdapter.getExtraCount() : 0);

                        Log.e("###", "FIRST " + mArticlePosition);
                        if (mArticlePosition >= 0 && mContentArrayList.size() > mArticlePosition &&
                                (mContentArrayList.get(mArticlePosition).getType() == null ||
                                        !mContentArrayList.get(mArticlePosition).getType().equalsIgnoreCase("YOUTUBE"))) {
                            //if(!mContentArrayList.get(mArticlePosition).getType().equalsIgnoreCase("YOUTUBE")) {
                            selectCardPosition(mArticlePosition);
                            //}
                        } else {
                            for (int i = 0; i < mContentArrayList.size(); i++) {
                                mContentArrayList.get(i).setSelected(false);
                            }
                            if (goHomeMainActivity != null)
                                goHomeMainActivity.sendAudioEvent("stop_destroy");
                        }

                        if (mSearchRelevantMainAdapter != null)
                            mSearchRelevantMainAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mArticlePosition = speedyLinearLayoutManager.findFirstVisibleItemPosition();
                if (mContentArrayList.size() - 3 <= speedyLinearLayoutManager.findLastVisibleItemPosition() && !isLast()) {
                    if (!isLoading) {
                        isLoading = true;
                        mPresenter.getRelevantArticles(mArticleContext, mPage);
                    }
                }
            }
        });
    }

    private void selectCardPosition(int position) {
        if (mContentArrayList.size() > 0 && position > -1 && position < mContentArrayList.size()) {
            for (int i = 0; i < mContentArrayList.size(); i++) {
                mContentArrayList.get(i).setSelected(false);
            }
            mContentArrayList.get(position).setSelected(true);
        }

        if (mSearchRelevantMainAdapter != null)
            mSearchRelevantMainAdapter.setCurrentArticlePosition(mArticlePosition);
    }

    private boolean isLast() {
        return mPage.equalsIgnoreCase("");
    }

    private void bindView(View view) {
        mRvArticles = view.findViewById(R.id.rvList);
        progress = view.findViewById(R.id.progress);
        ll_no_results = view.findViewById(R.id.ll_no_results);
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
    public void onSearchArticleSuccess(ArticleResponse response,Boolean isPagination) {

    }

    @Override
    public void onRelevantSuccess(RelevantResponse response) {
        if(TextUtils.isEmpty(mPage)) {
            mContentArrayList.clear();
        }
        if (response != null) {
            ArrayList<RelevantItem> relevantItemArrayList = new ArrayList<>();
            Category category1 = new Category();
            if (response.getArticles() != null && response.getArticles().size() > 0) {
                category1.setContents((ArrayList<Article>) response.getArticles());
            } else {
                category1.setContents(new ArrayList<>());
            }

            if (response.getArticlePage() != null) {
                mPage = response.getArticlePage().getNext();
            }

            mContentArrayList.addAll(category1.getContents());

            for (int i = 0; i < mContentArrayList.size(); i++) {
                mContentArrayList.get(i).setFragTag("id_0");
            }

            if (mSearchRelevantMainAdapter != null) {
                relevantItemArrayList = orderingItems(response, response.getArticles() != null && response.getArticles().size() > 0);
                mSearchRelevantMainAdapter.setRelevantItemArrayList(relevantItemArrayList);
                mSearchRelevantMainAdapter.notifyDataSetChanged();
                if (relevantItemArrayList.size() <= 0) {
                    ll_no_results.setVisibility(View.VISIBLE);
                } else {
                    ll_no_results.setVisibility(View.GONE);
                }
            }
        }
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


    private ArrayList<RelevantItem> orderingItems(RelevantResponse response, boolean showArticleTitle) {
        ArrayList<RelevantItem> relevantItemArrayList = new ArrayList<>();
        if (response != null) {
            if (response.getExactMatch() == null || response.getExactMatch().equals("")) {
                if (response.getTopics() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.topics), getString(R.string.topics))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TOPICS, getString(R.string.topics), response.getTopics())
                    );
                }
                if (response.getLocations() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.location), getString(R.string.location))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_LOCATION, getString(R.string.location), response.getLocations())
                    );
                }
                if (response.getSources() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.channels), getString(R.string.channels))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_CHANNELS, getString(R.string.channels), response.getSources())
                    );
                }
                if (response.getAuthors() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.authors), getString(R.string.authors))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_AUTHOR, getString(R.string.authors), response.getAuthors())
                    );
                }
                if (response.getReels() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.newsreels), getString(R.string.newsreels))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_REELS, getString(R.string.newsreels), response.getReels())
                    );
                }
            }

            if (response.getExactMatch() != null && response.getExactMatch().equals("topics")) {
                if (response.getTopics() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.topics), getString(R.string.topics))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_FIRST_ITEM, getString(R.string.topics), response.getTopics())
                    );
                }
                if (response.getLocations() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.location), getString(R.string.location))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_LOCATION, getString(R.string.location), response.getLocations())
                    );
                }
                if (response.getSources() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.channels), getString(R.string.channels))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_CHANNELS, getString(R.string.channels), response.getSources())
                    );
                }
                if (response.getAuthors() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.authors), getString(R.string.authors))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_AUTHOR, getString(R.string.authors), response.getAuthors())
                    );
                }
            }

            if (response.getExactMatch() != null && response.getExactMatch().equals("sources")) {
                if (response.getSources() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.channels), getString(R.string.channels))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_FIRST_ITEM, getString(R.string.channels), response.getSources())
                    );
                }
                if (response.getLocations() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.location), getString(R.string.location))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_LOCATION, getString(R.string.location), response.getLocations())
                    );
                }
                if (response.getTopics() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.topics), getString(R.string.topics))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TOPICS, getString(R.string.topics), response.getTopics())
                    );
                }
                if (response.getAuthors() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.authors), getString(R.string.authors))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_AUTHOR, getString(R.string.authors), response.getAuthors())
                    );
                }
            }

            if (response.getExactMatch() != null && response.getExactMatch().equals("locations")) {
                if (response.getLocations() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.location), getString(R.string.location))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_FIRST_ITEM, getString(R.string.location), response.getLocations())
                    );
                }
                if (response.getSources() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.channels), getString(R.string.channels))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_CHANNELS, getString(R.string.channels), response.getSources())
                    );
                }
                if (response.getTopics() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.topics), getString(R.string.topics))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TOPICS, getString(R.string.topics), response.getTopics())
                    );
                }
                if (response.getAuthors() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.authors), getString(R.string.authors))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_AUTHOR, getString(R.string.authors), response.getAuthors())
                    );
                }
            }

            if (response.getExactMatch() != null && response.getExactMatch().equals("authors")) {
                if (response.getAuthors() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.authors), getString(R.string.authors))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_FIRST_ITEM, getString(R.string.authors), response.getAuthors())
                    );
                }
                if (response.getLocations() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.location), getString(R.string.location))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_LOCATION, getString(R.string.location), response.getLocations())
                    );
                }
                if (response.getSources() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.channels), getString(R.string.channels))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_CHANNELS, getString(R.string.channels), response.getSources())
                    );
                }
                if (response.getTopics() != null) {
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.topics), getString(R.string.topics))
                    );
                    relevantItemArrayList.add(
                            new RelevantItem(SearchRelevantMainAdapter.TYPE_TOPICS, getString(R.string.topics), response.getTopics())
                    );
                }
            }

            if (showArticleTitle) {
                relevantItemArrayList.add(
                        new RelevantItem(SearchRelevantMainAdapter.TYPE_TITLE, getString(R.string.articles), getString(R.string.articles))
                );
            }
        }
        return relevantItemArrayList;
    }

    @Override
    public void onRelevantArticlesSuccess(ArticleResponse response) {

    }

    public interface OnAllFragmentInteractionListener {
        void dataChanged(TYPE type, String id);

        void onItemClicked(TYPE type, String context, String id, String name, boolean favorite);
    }
}