package com.ziro.bullet.adapters.relevant;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.MainActivityAdapter;
import com.ziro.bullet.adapters.relevant.callbacks.SourceFollowingCallback;
import com.ziro.bullet.adapters.relevant.callbacks.TopicsFollowingCallback;
import com.ziro.bullet.data.PrefConfig;

import com.ziro.bullet.data.models.relevant.Location;
import com.ziro.bullet.data.models.relevant.RelevantItem;

import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.interfaces.AdFailedListener;
import com.ziro.bullet.interfaces.CommentClick;
import com.ziro.bullet.interfaces.DetailsActivityInterface;
import com.ziro.bullet.interfaces.NewsCallback;
import com.ziro.bullet.interfaces.OnGotoChannelListener;
import com.ziro.bullet.interfaces.ShareToMainInterface;
import com.ziro.bullet.interfaces.ShowOptionsLoaderCallback;
import com.ziro.bullet.interfaces.TempCategorySwipeListener;
import com.ziro.bullet.interfaces.ViewItemClickListener;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Author;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.model.ShareBottomSheetResponse;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRelevantMainAdapter extends MainActivityAdapter {

    public static final int TYPE_TITLE = 2;
    public static final int TYPE_LOCATION = 3;
    public static final int TYPE_TOPICS = 4;
    public static final int TYPE_CHANNELS = 5;
    public static final int TYPE_AUTHOR = 6;
    public static final int TYPE_FIRST_ITEM = 7;
    public static final int TYPE_REELS = 8;

    private final Activity mContext;
    private ArrayList<RelevantItem> relevantItemArrayList = new ArrayList<>();

    private int extraCount = 0;

    private final PrefConfig mPrefConfig;

    private final TopicsFollowingCallback followingCallback;
    private final SourceFollowingCallback followingCallback1;
    private final SearchRelevantMainAdapter.FollowingCallback followingCallback2;
    private final ViewItemClickListener viewItemClickListener;
    private final ShowOptionsLoaderCallback showOptionsLoaderCallback;
    private final ViewItemClickListener viewItemClickListenerAuthorItem;

    private ArrayList<Topics> topicArrayList = null;
    private ArrayList<Location> locationArrayList = null;
    private ArrayList<Source> sourceArrayList = null;
    private ArrayList<Author> authorSearchArrayList = null;
    private CommentClick mCommentClick;

    public SearchRelevantMainAdapter(
            CommentClick mCommentClick,
            GoHome goHomeMainActivity,
            ShareToMainInterface shareToMainInterface,
            TempCategorySwipeListener swipeListener,
            NewsCallback categoryCallback,
            OnGotoChannelListener gotoChannelListener,
            Activity context,
            List<Article> arrayList,
            boolean isGotoFollowShow,
            String type,
            TopicsFollowingCallback followingCallback,
            SourceFollowingCallback followingCallback1,
            SearchRelevantMainAdapter.FollowingCallback followingCallback2,
            ViewItemClickListener viewItemClickListener,
            DetailsActivityInterface detailsActivityInterface,
            ShowOptionsLoaderCallback showOptionsLoaderCallback,
            ViewItemClickListener viewItemClickListenerAuthorItem,
            AdFailedListener adFailedListener
    ) {
        super(mCommentClick, goHomeMainActivity, shareToMainInterface, swipeListener, categoryCallback, gotoChannelListener,
                context, arrayList, isGotoFollowShow, type, detailsActivityInterface, showOptionsLoaderCallback, adFailedListener);
        this.mContext = context;
        this.mCommentClick = mCommentClick;
        this.followingCallback = followingCallback;
        this.followingCallback1 = followingCallback1;
        this.followingCallback2 = followingCallback2;
        this.viewItemClickListener = viewItemClickListener;
        this.showOptionsLoaderCallback = showOptionsLoaderCallback;
        this.viewItemClickListenerAuthorItem = viewItemClickListenerAuthorItem;

        mPrefConfig = new PrefConfig(mContext);
    }

    public void setRelevantItemArrayList(ArrayList<RelevantItem> relevantItemArrayList) {
        this.relevantItemArrayList = relevantItemArrayList;
        this.extraCount = relevantItemArrayList.size();
        notifyDataSetChanged();
    }

    public int getExtraCount() {
        return extraCount;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case TYPE_TITLE:
                view = LayoutInflater.from(mContext).inflate(R.layout.relevant_item_title, parent, false);
                return new TitleViewHolder(view);
            case TYPE_TOPICS:
                view = LayoutInflater.from(mContext).inflate(R.layout.community_suggested_item, parent, false);
                return new CircleTopicsViewHolder(view,mContext,false);
            case TYPE_CHANNELS:
                view = LayoutInflater.from(mContext).inflate(R.layout.community_suggested_item, parent, false);
                return new ChannelsViewHolder(view, false);
            case TYPE_LOCATION:
                view = LayoutInflater.from(mContext).inflate(R.layout.relevant_item_location, parent, false);
                return new PlaceViewHolder(view);
            case TYPE_AUTHOR:
                view = LayoutInflater.from(mContext).inflate(R.layout.relevant_item_author, parent, false);
                return new AuthorViewHolder(view);
            case TYPE_REELS:
                view = LayoutInflater.from(mContext).inflate(R.layout.relevant_item_reels, parent, false);
                return new ReelsViewHolder(view);
            case TYPE_FIRST_ITEM:
                view = LayoutInflater.from(mContext).inflate(R.layout.relevant_item_first, parent, false);
                return new FirstViewHolder(view);
            default:
                return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder1, int position) {
        if (holder1 instanceof TitleViewHolder || holder1 instanceof CircleTopicsViewHolder ||
                holder1 instanceof ChannelsViewHolder || holder1 instanceof PlaceViewHolder
                || holder1 instanceof FirstViewHolder || holder1 instanceof AuthorViewHolder || holder1 instanceof ReelsViewHolder) {

            if (holder1 instanceof TitleViewHolder) {
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder1;
                titleViewHolder.bind(relevantItemArrayList.get(position).getTitle());
            } else if (holder1 instanceof CircleTopicsViewHolder) {
                RelevantItem relevantItem = relevantItemArrayList.get(position);
                ArrayList<Topics> topicsArrayList = relevantItem.getObject() != null ? (ArrayList<Topics>) relevantItem.getObject() : new ArrayList<>();
                CircleTopicsViewHolder topicsViewHolder = (CircleTopicsViewHolder) holder1;
                ((CircleTopicsViewHolder) topicsViewHolder).bind(relevantItemArrayList.get(position).getTitle(), topicsArrayList, new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        int firstVisiblePosition = ((CircleTopicsViewHolder) holder1).layoutManager.findFirstVisibleItemPosition();
                        View firstItemView = ((CircleTopicsViewHolder) holder1).layoutManager.findViewByPosition(firstVisiblePosition);
                        if (firstItemView != null) {
                            float Offset = firstItemView.getLeft();

                            if (relevantItemArrayList.size() > position) {
                                relevantItemArrayList.get(position).setPos(firstVisiblePosition);
                                relevantItemArrayList.get(position).setOffset(Offset);
                            }
                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                }, relevantItemArrayList.get(position).getPos(), relevantItemArrayList.get(position).getOffset());

            } else if (holder1 instanceof ChannelsViewHolder) {
                RelevantItem relevantItem = relevantItemArrayList.get(position);
                ArrayList<Source> sourceArrayList = relevantItem.getObject() != null ? (ArrayList<Source>) relevantItem.getObject() : new ArrayList<>();

                ChannelsViewHolder channelsViewHolder = (ChannelsViewHolder) holder1;
                channelsViewHolder.bind("", (Activity) mContext, sourceArrayList,
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                int firstVisiblePosition = ((ChannelsViewHolder) holder1).layoutManager.findFirstVisibleItemPosition();
                                View firstItemView = ((ChannelsViewHolder) holder1).layoutManager.findViewByPosition(firstVisiblePosition);
                                if (firstItemView != null) {
                                    float Offset = firstItemView.getLeft();

                                    if (relevantItemArrayList.size() > position) {
                                        relevantItemArrayList.get(position).setPos(firstVisiblePosition);
                                        relevantItemArrayList.get(position).setOffset(Offset);
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, relevantItemArrayList.get(position).getPos(), relevantItemArrayList.get(position).getOffset()
                );
            } else if (holder1 instanceof PlaceViewHolder) {
                RelevantItem relevantItem = relevantItemArrayList.get(position);
                ArrayList<com.ziro.bullet.data.models.relevant.Location> locationArrayList = relevantItem.getObject() != null ? (ArrayList<com.ziro.bullet.data.models.relevant.Location>) relevantItem.getObject() : new ArrayList<>();

                PlaceViewHolder placeViewHolder = (PlaceViewHolder) holder1;
                placeViewHolder.bind(mContext, locationArrayList, followingCallback2,
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                int firstVisiblePosition = ((PlaceViewHolder) holder1).layoutManager.findFirstVisibleItemPosition();
                                View firstItemView = ((PlaceViewHolder) holder1).layoutManager.findViewByPosition(firstVisiblePosition);
                                if (firstItemView != null) {
                                    float Offset = firstItemView.getLeft();

                                    if (relevantItemArrayList.size() > position) {
                                        relevantItemArrayList.get(position).setPos(firstVisiblePosition);
                                        relevantItemArrayList.get(position).setOffset(Offset);
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, relevantItemArrayList.get(position).getPos(), relevantItemArrayList.get(position).getOffset(),
                        viewItemClickListener
                );

            } else if (holder1 instanceof AuthorViewHolder) {
                RelevantItem relevantItem = relevantItemArrayList.get(position);
                ArrayList<Author> authorSearchArrayList = relevantItem.getObject() != null ? (ArrayList<Author>) relevantItem.getObject() : new ArrayList<>();

                AuthorViewHolder authorViewHolder = (AuthorViewHolder) holder1;
                authorViewHolder.bind(mContext, authorSearchArrayList, viewItemClickListenerAuthorItem,
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                int firstVisiblePosition = ((AuthorViewHolder) holder1).layoutManager.findFirstVisibleItemPosition();
                                View firstItemView = ((AuthorViewHolder) holder1).layoutManager.findViewByPosition(firstVisiblePosition);
                                if (firstItemView != null) {
                                    float Offset = firstItemView.getLeft();

                                    if (relevantItemArrayList.size() > position) {
                                        relevantItemArrayList.get(position).setPos(firstVisiblePosition);
                                        relevantItemArrayList.get(position).setOffset(Offset);
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, relevantItemArrayList.get(position).getPos(), relevantItemArrayList.get(position).getOffset(),
                        viewItemClickListener
                );

            } else if (holder1 instanceof ReelsViewHolder) {
                RelevantItem relevantItem = relevantItemArrayList.get(position);
                ArrayList<ReelsItem> reelSearchArrayList = relevantItem.getObject() != null ? (ArrayList<ReelsItem>) relevantItem.getObject() : new ArrayList<>() ;

                ReelsViewHolder reelsViewHolder = (ReelsViewHolder) holder1;
                reelsViewHolder.bind(mContext, reelSearchArrayList, viewItemClickListenerAuthorItem,
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);

                                int firstVisiblePosition = ((ReelsViewHolder) holder1).layoutManager.findFirstVisibleItemPosition();
                                View firstItemView = ((ReelsViewHolder) holder1).layoutManager.findViewByPosition(firstVisiblePosition);
                                if (firstItemView != null) {
                                    float Offset = firstItemView.getLeft();

                                    if (relevantItemArrayList.size() > position) {
                                        relevantItemArrayList.get(position).setPos(firstVisiblePosition);
                                        relevantItemArrayList.get(position).setOffset(Offset);
                                    }
                                }
                            }

                            @Override
                            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        }, relevantItemArrayList.get(position).getPos(), relevantItemArrayList.get(position).getOffset(),
                        viewItemClickListener
                );
            } else {
                RelevantItem relevantItem = relevantItemArrayList.get(position);

                if (relevantItem.getTitle().equals(mContext.getString(R.string.topics))) {
                    topicArrayList = (ArrayList<Topics>) relevantItem.getObject();
                }
                if (relevantItem.getTitle().equals(mContext.getString(R.string.location))) {
                    locationArrayList = (ArrayList<Location>) relevantItem.getObject();
                }
                if (relevantItem.getTitle().equals(mContext.getString(R.string.channels))) {
                    sourceArrayList = (ArrayList<Source>) relevantItem.getObject();
                }
                if (relevantItem.getTitle().equals(mContext.getString(R.string.authors))) {
                    authorSearchArrayList = (ArrayList<Author>) relevantItem.getObject();
                }

                FirstViewHolder firstViewHolder = (FirstViewHolder) holder1;
//                ArrayList<Topics> finalTopicArrayList = topicArrayList;
//                ArrayList<com.ziro.bullet.data.models.sources.Source> finalSourceArrayList = sourceArrayList;
                firstViewHolder.bind(topicArrayList,
                        locationArrayList, sourceArrayList, authorSearchArrayList, followingCallback, followingCallback1, followingCallback2,
                        v -> {
                            if (v.getVisibility() == View.VISIBLE) {
                                if (topicArrayList.size() > 0) {
//                                    topicArrayList.get(0).setFavorite(false);
                                    unfollowTopic(topicArrayList.get(0), firstViewHolder);
                                }
                            } else {
                                if (topicArrayList.size() > 0) {
//                                    topicArrayList.get(0).setFavorite(true);
                                    followTopic(topicArrayList.get(0), firstViewHolder);
                                }
                            }
                        },
                        v -> {
                            if (v.getVisibility() == View.VISIBLE) {
                                if (sourceArrayList.size() > 0) {
                                    sourceArrayList.get(0).setFavorite(false);
                                    unfollowSource(sourceArrayList.get(0), firstViewHolder);
                                }
                            } else {
                                if (sourceArrayList.size() > 0) {
                                    sourceArrayList.get(0).setFavorite(true);
                                    followSource(sourceArrayList.get(0), firstViewHolder);
                                }
                            }
                        },
                        v -> {
                            if (v.getVisibility() == View.VISIBLE) {
                                if (locationArrayList.size() > 0) {
                                    locationArrayList.get(0).setFavorite(false);
                                    unfollowLocation(locationArrayList.get(0), firstViewHolder);
                                }
                            } else {
                                if (locationArrayList.size() > 0) {
                                    locationArrayList.get(0).setFavorite(true);
                                    followLocation(locationArrayList.get(0), firstViewHolder);
                                }
                            }
                        },
                        v -> {
                            if (v.getVisibility() == View.VISIBLE) {
                                if (authorSearchArrayList.size() > 0) {
                                    authorSearchArrayList.get(0).setFollow(false);
                                    unfollowAuthor(authorSearchArrayList.get(0), firstViewHolder);
                                }
                            } else {
                                if (authorSearchArrayList.size() > 0) {
                                    authorSearchArrayList.get(0).setFollow(true);
                                    followAuthor(authorSearchArrayList.get(0), firstViewHolder);
                                }
                            }
                        }, new PrefConfig(mContext)
                );
            }
        } else {
            super.onBindViewHolder(holder1, position - extraCount);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (relevantItemArrayList != null && relevantItemArrayList.size() > 0 && relevantItemArrayList.size() > position) {
            return relevantItemArrayList.get(position).getType();
        } else {
            return super.getItemViewType(position - extraCount);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + extraCount;
    }

    private void unfollowSource(Source source, FirstViewHolder holder) {

        Call<ShareBottomSheetResponse> call = ApiClient
                .getInstance((Activity) mContext)
                .getApi()
                .unfollowSource("Bearer " + mPrefConfig.getAccessToken(), source.getId());
        call.enqueue(new Callback<ShareBottomSheetResponse>() {
            @Override
            public void onResponse(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Response<ShareBottomSheetResponse> response) {

                if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    source.setFavorite(true);

                    holder.unfollow.setVisibility(View.GONE);
                    holder.follow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ShareBottomSheetResponse> call, @NotNull Throwable t) {

                source.setFavorite(true);

                holder.unfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void followSource(Source source, FirstViewHolder holder) {

        Call<ResponseBody> call = ApiClient
                .getInstance((Activity) mContext)
                .getApi()
                .followSource("Bearer " + mPrefConfig.getAccessToken(), source.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {

                if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    source.setFavorite(false);
                    holder.unfollow.setVisibility(View.VISIBLE);
                    holder.follow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                source.setFavorite(false);
                holder.unfollow.setVisibility(View.VISIBLE);
                holder.follow.setVisibility(View.GONE);
            }
        });
    }


    private void unfollowTopic(Topics topic, FirstViewHolder holder) {

        Call<ResponseBody> call = ApiClient
                .getInstance((Activity) mContext)
                .getApi()
                .unfollowTopic("Bearer " + mPrefConfig.getAccessToken(), topic.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
//                    topic.setFavorite(true);
                    holder.unfollow.setVisibility(View.GONE);
                    holder.follow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                // topic.setFavorite(true);
                holder.unfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.VISIBLE);
            }
        });
    }

    private void followTopic(Topics topic, FirstViewHolder holder) {

        Call<ResponseBody> call = ApiClient
                .getInstance((Activity) mContext)
                .getApi()
                .addTopic("Bearer " + mPrefConfig.getAccessToken(), topic.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
//                    topic.setFavorite(false);
                    holder.unfollow.setVisibility(View.VISIBLE);
                    holder.follow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                topic.setFavorite(false);
                holder.unfollow.setVisibility(View.VISIBLE);
                holder.follow.setVisibility(View.GONE);
            }
        });
    }


    private void unfollowLocation(Location location, FirstViewHolder holder) {

        Call<ResponseBody> call = ApiClient
                .getInstance((Activity) mContext)
                .getApi()
                .unfollowLocation("Bearer " + mPrefConfig.getAccessToken(), location.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    location.setFavorite(true);
                    holder.unfollow.setVisibility(View.GONE);
                    holder.follow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                location.setFavorite(true);
                holder.unfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.VISIBLE);
            }
        });
    }

    private void followLocation(Location location, FirstViewHolder holder) {

        Call<ResponseBody> call = ApiClient
                .getInstance((Activity) mContext)
                .getApi()
                .followLocation("Bearer " + mPrefConfig.getAccessToken(), location.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    location.setFavorite(false);
                    holder.unfollow.setVisibility(View.VISIBLE);
                    holder.follow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                location.setFavorite(false);
                holder.unfollow.setVisibility(View.VISIBLE);
                holder.follow.setVisibility(View.GONE);
            }
        });
    }

    private void unfollowAuthor(Author authorSearch, FirstViewHolder holder) {

        Call<ResponseBody> call = ApiClient
                .getInstance((Activity) mContext)
                .getApi()
                .unFollowAuthor("Bearer " + mPrefConfig.getAccessToken(), authorSearch.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    authorSearch.setFollow(true);
                    holder.unfollow.setVisibility(View.GONE);
                    holder.follow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                authorSearch.setFollow(true);
                holder.unfollow.setVisibility(View.GONE);
                holder.follow.setVisibility(View.VISIBLE);
            }
        });
    }

    private void followAuthor(Author authorSearch, FirstViewHolder holder) {

        Call<ResponseBody> call = ApiClient
                .getInstance((Activity) mContext)
                .getApi()
                .followAuthor("Bearer " + mPrefConfig.getAccessToken(), authorSearch.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.code() == 400) {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = jsonObject.getString("message");
                            Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    authorSearch.setFollow(false);
                    holder.unfollow.setVisibility(View.VISIBLE);
                    holder.follow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Toast.makeText(mContext, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                authorSearch.setFollow(false);
                holder.unfollow.setVisibility(View.VISIBLE);
                holder.follow.setVisibility(View.GONE);
            }
        });
    }

    public interface FollowingCallback {

        void onItemFollowed(Location location);

        void onItemUnfollowed(Location location);

        void onItemClicked(Location location);
    }
}
