package com.ziro.bullet.activities;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.onboarding.OnBoardingActivity;
import com.ziro.bullet.adapters.TopicsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.fragments.DividerItemDecorator;
import com.ziro.bullet.interfaces.TopicInterface;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.model.FollowResponse;
import com.ziro.bullet.model.Menu.Category;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.presenter.TopicPresenter;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class TopicsActivity extends BaseActivity implements TopicInterface {

    private TextView continue_text;
    private TextView tv_search2;
    private RelativeLayout done;
    private RecyclerView recyclerView;
    private CardView cardview;
    private TopicPresenter presenter;
    private ProgressBar progressnew;
    private EditText etSearchTopics;
    private ImageView ivClearText;

    private RelativeLayout mProgressContainer;

    private ArrayList<Category> data = new ArrayList<>();
    private ArrayList<Category> favList = new ArrayList<>();
    private TopicsAdapter adapter;

    //searchf
    private long delay = 500; // 1 seconds after user stops typing
    private long last_text_edit = 0;
    private String searchStr = "";
    private String check = "";
    private String home = "";
    private String currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private GridLayoutManager manager;
    private String add = "";
    private PrefConfig prefConfig;
    private int minimum = 3;
    private boolean isDone = false;

    private ArrayList<String> followList = new ArrayList<>();
    private ArrayList<String> unfollowList = new ArrayList<>();
    private RelativeLayout cancel;
    private LinearLayout ll_no_results;
    private LinearLayout search_skeleton;
    private ImageView backImage;

    private UserConfigPresenter mUserConfigPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefConfig = new PrefConfig(this);
        if (prefConfig.getRegistration() != null && prefConfig.getRegistration().getTopic() != null) {
            minimum = prefConfig.getRegistration().getTopic().getMinimum();
            isDone = prefConfig.getRegistration().getTopic().isDone();
        }
        checkBundle();
        setContentView(R.layout.activity_topics);

        presenter = new TopicPresenter(this, this);
        bindView();
        listener();
//        presenter.searchTopics(searchStr, currentPage, false);

        if (TextUtils.isEmpty(searchStr)) {
            presenter.topics(currentPage, false);
        } else {
            presenter.searchTopics(searchStr, currentPage, false);
        }
        setPagination();

        mUserConfigPresenter = new UserConfigPresenter(this, new UserConfigCallback() {
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
            public void onUserConfigSuccess(UserConfigModel userConfigModel) {

                Intent intent = null;
                if (userConfigModel.isOnboarded()) {
                    intent = new Intent(TopicsActivity.this, MainActivityNew.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    updateWidget();
                } else {
                    intent = new Intent(TopicsActivity.this, OnBoardingActivity.class);

                    intent.putExtra("flow", "");
                    intent.putExtra("check", "signup");
                }

                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        if (getIntent() != null) {
            searchStr = getIntent().getStringExtra("query");
        }

    }

    private void updateWidget() {
        try {
            if (AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CollectionWidget.class)).length > 0) {
                Intent updateWidget = new Intent(this, CollectionWidget.class);
                updateWidget.setAction("update_widget");
                PendingIntent pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                pending.send();
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.hideKeyboard(this, etSearchTopics);
        if (Constants.topicsStatusChanged != null) {

            if (data != null && !data.isEmpty()) {
                int position = -1;
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getId().equals(Constants.topicsStatusChanged)) {
                        position = i;
                        break;
                    }
                }

                if (position >= 0) {
                    boolean isFavTopics = data.get(position).isFavorite();
                    if (Constants.isTopicDataChange) {
                        data.get(position).setFavorite(!isFavTopics);
                    }
                    adapter.notifyItemChanged(position);
//                    Constants.isTopicDataChange = false;
//                    Constants.topicsStatusChanged = null;
//                    Constants.followStatus = null;
                }
            }
        }
    }

    private void setPagination() {
//        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        manager = new GridLayoutManager(this, 1);
//        recyclerView.addItemDecoration(new SpacesItemDecoration(2));
        recyclerView.addOnScrollListener(new PaginationListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                if (TextUtils.isEmpty(searchStr)) {
                    presenter.topics(currentPage, true);
                } else {
                    presenter.searchTopics(searchStr, currentPage, true);
                }
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

            }

            @Override
            public void onScrolling(@NonNull @NotNull RecyclerView recyclerView, int newState) {

            }
        });
        recyclerView.setLayoutManager(manager);
        adapter = new TopicsAdapter(this, data);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
//        val itemDecorator =  DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider));
//        rvTrendingArticle.addItemDecoration(itemDecorator)
    }

    private void checkBundle() {
        if (getIntent() != null) {
            check = getIntent().getStringExtra("check");
            searchStr = getIntent().getStringExtra("query");
            home = getIntent().getStringExtra("home");
            add = getIntent().getStringExtra("add");
        }
    }

    private void listener() {

    }

    private void goBack() {
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        finish();
    }

    private void bindView() {
        cancel = findViewById(R.id.cancel);
        recyclerView = findViewById(R.id.topics_list);
        cardview = findViewById(R.id.cardview);
        tv_search2 = findViewById(R.id.tv_search2);
        ll_no_results = findViewById(R.id.ll_no_results);
        search_skeleton = findViewById(R.id.search_skeleton);
        progressnew = findViewById(R.id.progressnew);
        backImage = findViewById(R.id.back_img);
        etSearchTopics = findViewById(R.id.et_searchtext);
        ivClearText = findViewById(R.id.iv_clear_text);

        etSearchTopics.setText(searchStr);

        etSearchTopics.addTextChangedListener(new TextWatcher() {
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

        etSearchTopics.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                String searchQuery = etSearchTopics.getText().toString().trim();
                if (!TextUtils.isEmpty(searchQuery)) {
                    searchStr = searchQuery;
                    currentPage = "";
                    presenter.searchTopics(searchStr, currentPage, false);
                    Utils.hideKeyboard(TopicsActivity.this, etSearchTopics);
                }
                return true;
            }
            return false;
        });

        ivClearText.setOnClickListener(v -> {
            etSearchTopics.setText("");
            Utils.hideKeyboard(this, etSearchTopics);
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Utils.hideKeyboard(TopicsActivity.this, recyclerView);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        Utils.hideKeyboard(this, etSearchTopics);
    }

    private void checkData() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            if (favList != null) {
                if (favList.size() > 0) {

                    for (int i = 0; i < favList.size(); i++) {
                        for (int j = 0; j < data.size(); j++) {
                            if (data.get(j).getId().equalsIgnoreCase(favList.get(i).getId())) {
                                data.get(j).setFavorite(favList.get(i).isFavorite());
                            }
                        }
                    }
                    for (int i = 0; i < favList.size(); i++) {
                        if (!favList.get(i).isFavorite()) {
                            favList.remove(i);
                        }
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
//        finishAfterTransition();
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        goBack();
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            ll_no_results.setVisibility(View.GONE);
            progressnew.setVisibility(View.VISIBLE);
        } else {
            progressnew.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        progressnew.setVisibility(View.GONE);
    }

    @Override
    public void error404(String error) {
        progressnew.setVisibility(View.GONE);
    }

    @Override
    public void success(CategoryResponse response, boolean isPagination) {
        isLoading = false;
//        back.setEnabled(true);
        if (response != null) {
            cardview.setVisibility(View.VISIBLE);
            search_skeleton.setVisibility(View.GONE);

            if (response.getMeta() != null) {
                currentPage = response.getMeta().getNext();
            }
            if (TextUtils.isEmpty(currentPage)) {
                isLastPage = true;
            }
            data.addAll(response.getCategory());
            if (data.size() <= 0) {
                search_skeleton.setVisibility(View.GONE);
                cardview.setVisibility(View.GONE);
                ll_no_results.setVisibility(View.VISIBLE);
            } else {
                ll_no_results.setVisibility(View.GONE);
            }

            if (!isPagination) {
                adapter = new TopicsAdapter(this, data);
                adapter.setClickListener(new TopicsAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, boolean isFavorite) {
                        if (data != null && data.size() > 0) {
                            if (data.get(position).isFavorite()) {

                                for (int i = 0; i < favList.size(); i++) {
                                    if (data.get(position).getId().equalsIgnoreCase(favList.get(i).getId())) {
                                        favList.get(i).setFavorite(false);
                                        break;
                                    }
                                }
                            } else {
                                data.get(position).setFavorite(true);
                                favList.add(data.get(position));
                            }

                            if (isFavorite) {
                                if (unfollowList.contains(data.get(position).getId()))
                                    unfollowList.remove(data.get(position).getId());
                                else
                                    followList.add(data.get(position).getId());
                            } else {
                                if (followList.contains(data.get(position).getId()))
                                    followList.remove(data.get(position).getId());
                                else
                                    unfollowList.add(data.get(position).getId());
                            }
                            data.get(position).setFavorite(isFavorite);
                        }
                        checkData();
                    }

                    @Override
                    public void isLastItem(boolean flag) {
//                        if (flag) {
//                            gradient.setVisibility(View.GONE);
//                        } else {
//                            gradient.setVisibility(View.VISIBLE);
//                        }
                    }

                    @Override
                    public void onItemTopicClick(int position, Category item) {
                        Intent intent = new Intent(TopicsActivity.this, ChannelPostActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("data", new Gson().toJson(adapter.getSelected()));
                        intent.putExtra("check", check);
                        intent.putExtra("type", TYPE.TOPIC);
                        intent.putExtra("id", item.getId());
                        intent.putExtra("context", item.getContext());
                        intent.putExtra("name", item.getName());
                        intent.putExtra("favorite", item.isFavorite());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

                    @Override
                    public void onItemTopicFollowed2(int position, Category topic) {
                        presenter.getTopicFollowPresenter(topic.getId(), position, topic);
                    }

                    @Override
                    public void onItemTopicUnfollowed2(int position, Category topic) {
                        presenter.getTopicUnfollowPresenter(topic.getId(), position, topic);
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        }
        checkData();
    }

    @Override
    public void searchSuccess(CategoryResponse response, boolean isPagination) {
//        back.setEnabled(true);
        isLoading = false;
        if (response != null) {
            cardview.setVisibility(View.VISIBLE);
            search_skeleton.setVisibility(View.GONE);
            if (response.getMeta() != null) {
                currentPage = response.getMeta().getNext();
            }
            if (TextUtils.isEmpty(currentPage)) {
                isLastPage = true;
            }
            if (!isPagination) {
                data.clear();
            }
            data.addAll(response.getCategory());
            adapter.notifyDataSetChanged();

            if (data.size() <= 0) {
                search_skeleton.setVisibility(View.GONE);
                cardview.setVisibility(View.GONE);
                ll_no_results.setVisibility(View.VISIBLE);
            } else {
                ll_no_results.setVisibility(View.GONE);
            }

            if (!isPagination) {
                adapter = new TopicsAdapter(this, data);
                adapter.setClickListener(new TopicsAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, boolean isFavorite) {
                        if (data != null && data.size() > 0) {
                            if (data.get(position).isFavorite()) {

                                for (int i = 0; i < favList.size(); i++) {
                                    if (data.get(position).getId().equalsIgnoreCase(favList.get(i).getId())) {
                                        favList.get(i).setFavorite(false);
                                        break;
                                    }
                                }
                            } else {
                                data.get(position).setFavorite(true);
                                favList.add(data.get(position));
                            }

                            if (isFavorite) {
                                if (unfollowList.contains(data.get(position).getId()))
                                    unfollowList.remove(data.get(position).getId());
                                else
                                    followList.add(data.get(position).getId());
                            } else {
                                if (followList.contains(data.get(position).getId()))
                                    followList.remove(data.get(position).getId());
                                else
                                    unfollowList.add(data.get(position).getId());
                            }
//                            data.get(position).setFavorite(isFavorite);
                        }
                        checkData();
                    }

                    @Override
                    public void isLastItem(boolean flag) {
                        if (flag) {
//                            gradient.setVisibility(View.GONE);
                        } else {
//                            gradient.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onItemTopicClick(int position, Category item) {
                        Intent intent = new Intent(TopicsActivity.this, ChannelPostActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("data", new Gson().toJson(adapter.getSelected()));
                        intent.putExtra("check", check);
                        intent.putExtra("type", TYPE.TOPIC);
                        intent.putExtra("id", item.getId());
                        intent.putExtra("context", item.getContext());
                        intent.putExtra("name", item.getName());
                        intent.putExtra("favorite", item.isFavorite());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }

                    @Override
                    public void onItemTopicFollowed2(int position, Category topic) {
                        presenter.getTopicFollowPresenter(topic.getId(), position, topic);
                    }

                    @Override
                    public void onItemTopicUnfollowed2(int position, Category topic) {
                        presenter.getTopicUnfollowPresenter(topic.getId(), position, topic);
//call unfollow api
                    }
                });
                recyclerView.setAdapter(adapter);
            }


        }
        checkData();
    }

    @Override
    public void addSuccess(int position) {
        mUserConfigPresenter.getUserConfig(false);
    }

    @Override
    public void deleteSuccess(int position) {
//        back.setEnabled(true);
        if (data != null && data.size() > 0) {
            data.get(position).setFavorite(false);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        checkData();
    }

    @Override
    public void getTopicsFollow(FollowResponse response, int position, Category topic) {
        if (response.getMessage().toLowerCase(Locale.ROOT).equals("success")) {
            adapter.notifyDataSetChanged();
            Constants.topicsStatusChanged = topic.getId();
            Constants.followStatus = String.valueOf(topic.isFavorite());
            Constants.itemPosition = position;
            Constants.isTopicDataChange = true;
        }
    }
}
