package com.ziro.bullet.activities;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.onboarding.OnBoardingActivity;
import com.ziro.bullet.adapters.EditionsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.interfaces.EditionInterface;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.model.Edition.EditionsItem;
import com.ziro.bullet.model.Edition.ResponseEdition;
import com.ziro.bullet.presenter.EditionPresenter;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EditionActivity extends BaseActivity implements EditionInterface {

    private boolean isReg = false;
    private EditText search;
    private TextView headerContent;
    private TextView help;
    //    private TextView headerText;
    private long delay = 500; // 1 seconds after user stops typing
    private long last_text_edit = 0;
    private String searchStr = "";
    private Handler handler = new Handler();
    private RelativeLayout done;
    private RelativeLayout ivBack;
    private RelativeLayout progress;
    private RecyclerView language_list;
    private EditionPresenter presenter;
    private EditionsAdapter adapter;
    private ArrayList<EditionsItem> editions = new ArrayList<>();
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            ApiClient.cancelAll();
            editions.clear();
            adapter.notifyDataSetChanged();
            presenter.getEdition(searchStr, PAGE_START);
        }
    };
    private boolean isLoading = false;
    private String nextPage = PAGE_START;
    private boolean isLastPage;
    private String check, flow;

    private ArrayList<String> followList = new ArrayList<>();
    private ArrayList<String> unfollowList = new ArrayList<>();

    private UserConfigPresenter mUserConfigPresenter;
    private RelativeLayout cancel;
    private LinearLayout mLlNoRecord;
    private boolean force = false;
    private DbHandler cacheManager;
    private PrefConfig prefConfig;

    @Override
    protected void onResume() {
        super.onResume();
        Utils.hideKeyboard(this, help);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefConfig = new PrefConfig(this);
        cacheManager = new DbHandler(this);
        if (getIntent() != null) {
            flow = getIntent().getStringExtra("flow");
            if (TextUtils.isEmpty(flow)) {
                // new Components().settingStatusBarColors(this, "black");
                BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                Utils.saveSystemThemeAsDefault(this, prefConfig);
            } else {
                Utils.checkAppModeColor(this, false);
            }
        } else {
            // new Components().settingStatusBarColors(this, "black");
            BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            Utils.saveSystemThemeAsDefault(this, prefConfig);
        }
        setContentView(R.layout.activity_edition);
        bindViews();
        setListener();
        init();
        setbundle();

        Utils.setStatusBarColor(this);
    }

    private void setbundle() {
        if (getIntent() != null) {
            String flow = getIntent().getStringExtra("flow");
            check = getIntent().getStringExtra("check");
            if (TextUtils.isEmpty(flow)) {
                isReg = true;
                help.setVisibility(View.VISIBLE);
                done.setVisibility(View.VISIBLE);
//                headerText.setVisibility(View.INVISIBLE);
//                headerContent.setVisibility(View.VISIBLE);
            } else {
                isReg = false;
                done.setVisibility(View.VISIBLE);
                help.setVisibility(View.GONE);
//                headerText.setVisibility(View.VISIBLE);
//                headerContent.setVisibility(View.GONE);
            }
        }
    }

    private void goBack() {
        if (isReg) {
            Intent intent = new Intent(EditionActivity.this, FirstActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void init() {
        presenter = new EditionPresenter(this, this);
        adapter = new EditionsAdapter(editions, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        language_list.setLayoutManager(manager);
        adapter.setCallback(new EditionsAdapter.EditionOnclickCallback() {
            @Override
            public void onClickItem(int position, String id) {
//                Log.e("onClickItem", "-----------------------------------");
//                Log.e("onClickItem", "--> " + position);
//                presenter.getEdition(searchStr, nextPage, id, position);
            }

            @Override
            public void onClickMarkInner(int position, String id, boolean isFollow) {
                if (isFollow) {
                    if (unfollowList.contains(id))
                        unfollowList.remove(id);
                    else
                        followList.add(id);
                } else {
                    if (followList.contains(id))
                        followList.remove(id);
                    else
                        unfollowList.add(id);
                }
            }

            @Override
            public void onClickMark(int position, String id, boolean isFollow) {
                if (isFollow) {
                    if (unfollowList.contains(editions.get(position).getId()))
                        unfollowList.remove(editions.get(position).getId());
                    else
                        followList.add(editions.get(position).getId());
                } else {
                    if (followList.contains(editions.get(position).getId()))
                        followList.remove(editions.get(position).getId());
                    else
                        unfollowList.add(editions.get(position).getId());
                }
                editions.get(position).setSelected(isFollow);
                adapter.notifyDataSetChanged();
            }
        });
        language_list.setAdapter(adapter);
        presenter.getEdition(searchStr, nextPage);

        setPagination(language_list, manager);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
                if (cancel != null && s != null) {
                    if (s.length() == 0) {
                        cancel.setVisibility(View.GONE);
                    } else {
                        cancel.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("TECT", "--> " + s.toString());
                handler.removeCallbacks(input_finish_checker);
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    searchStr = s.toString();
                    handler.postDelayed(input_finish_checker, delay);
                } else if (s.length() == 0) {
                    ApiClient.cancelAll();
                    editions.clear();
                    adapter.notifyDataSetChanged();
                    searchStr = "";
                    nextPage = PAGE_START;
                    isLastPage = false;
                    isLoading = false;
                    presenter.getEdition(searchStr, nextPage);
                }
            }
        });

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
                if (isReg) {

                    Intent intent = null;
                    if (userConfigModel.isOnboarded()) {
                        intent = new Intent(EditionActivity.this, MainActivityNew.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        updateWidget();
                    } else {
                        intent = new Intent(EditionActivity.this, OnBoardingActivity.class);

                    }

                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
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

    private void setPagination(RecyclerView mRvRecyclerView, LinearLayoutManager manager) {
        mRvRecyclerView.addOnScrollListener(new PaginationListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                presenter.getEdition(searchStr, nextPage);
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
        mRvRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Utils.hideKeyboard(EditionActivity.this, mRvRecyclerView);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        mRvRecyclerView.setLayoutManager(manager);
    }

    private void setListener() {
        ivBack.setOnClickListener(v -> goBack());
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });
        help.setOnClickListener(v -> Utils.openHelpView(this, BuildConfig.HelpUrl, "Help"));
        done.setOnClickListener(v -> {
            if (isReg) {
                force = true;
            }
            done.setEnabled(false);
            ivBack.setEnabled(false);

            Set<String> followSet = new HashSet<>(followList);
            Set<String> unfollowSet = new HashSet<>(unfollowList);
            followList.clear();
            followList.addAll(followSet);

            unfollowList.clear();
            unfollowList.addAll(unfollowSet);

            Log.e("updateEditions", "===================================================");
            Log.e("updateEditions", "follow : " + new Gson().toJson(followList.toArray()));
            Log.e("updateEditions", "unfollow : " + new Gson().toJson(unfollowList.toArray()));

            presenter.updateEditions(followList, unfollowList, force);
        });
    }

    private void bindViews() {
        cancel = findViewById(R.id.cancel);
        help = findViewById(R.id.help);
//        headerText = findViewById(R.id.headerText);
        headerContent = findViewById(R.id.headerContent);
        done = findViewById(R.id.done);
        search = findViewById(R.id.search);
        progress = findViewById(R.id.progress);
        ivBack = findViewById(R.id.ivBack);
        language_list = findViewById(R.id.language_list);
        mLlNoRecord = findViewById(R.id.ll_no_results);

//        ImageView gif = findViewById(R.id.loader);
        ImageView gif2 = findViewById(R.id.gif);
//        Glide.with(this).load(Utils.getLoaderForTheme(prefConfig.getAppTheme())).into(gif);
        Glide.with(this).load(R.raw.binoculars).into(gif2);
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        if (!TextUtils.isEmpty(error) && error.toLowerCase().contains("cancel")) {
            return;
        }
        done.setEnabled(true);
        ivBack.setEnabled(true);
    }

    private void filterAlreadyFollowed(ResponseEdition responseEdition) {
        if (followList != null && followList.size() > 0) {
            if (responseEdition.getEditions() != null && responseEdition.getEditions().size() > 0) {
                for (EditionsItem item : responseEdition.getEditions()) {
                    for (String followed : followList) {
                        if (followed.equalsIgnoreCase(item.getId())) {
                            item.setSelected(true);
                        }
                    }
                    editions.add(item);
                }
            }
        }
    }

    private void filterAlreadyUnfollowed(ResponseEdition responseEdition) {
        if (unfollowList != null && unfollowList.size() > 0) {
            if (responseEdition.getEditions() != null && responseEdition.getEditions().size() > 0) {
                for (EditionsItem item : responseEdition.getEditions()) {
                    for (String followed : unfollowList) {
                        if (followed.equalsIgnoreCase(item.getId())) {
                            item.setSelected(false);
                        }
                    }
                    editions.add(item);
                }
            }
        }
    }

    @Override
    public void success(ResponseEdition responseEdition) {
        isLoading = false;
        if (responseEdition != null) {
            if ((followList != null && followList.size() > 0) || (unfollowList != null && unfollowList.size() > 0)) {
                filterAlreadyFollowed(responseEdition);
                filterAlreadyUnfollowed(responseEdition);
            } else {
                editions.addAll(responseEdition.getEditions());
            }
            adapter.notifyDataSetChanged();
            if (responseEdition.getMeta() != null) {
                nextPage = responseEdition.getMeta().getNext();
            }
            if (TextUtils.isEmpty(nextPage)) {
                isLastPage = true;
            }

            if (editions.size() == 0) {
                done.setVisibility(View.GONE);
                mLlNoRecord.setVisibility(View.VISIBLE);
            } else {
                done.setVisibility(View.VISIBLE);
                mLlNoRecord.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void successFollowed(ResponseEdition responseEdition) {
        if (cacheManager != null) {
            Constants.homeDataUpdate = true;
            Constants.menuDataUpdate = true;
            Constants.reelDataUpdate = true;
            if (prefConfig != null)
                prefConfig.setAppStateHomeTabs("");
            cacheManager.clearDb();
        }
        prefConfig.setEditions(responseEdition.getEditions());
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void successFollowUnfollow(int position, String id, boolean isFollow) {
        if (position < editions.size()) {
            editions.get(position).setSelected(!isFollow);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onUpdateSuccess() {
        done.setEnabled(true);
        ivBack.setEnabled(true);
        if (isReg) {
            mUserConfigPresenter.getUserConfig(false);
//            Intent intent = new Intent(EditionActivity.this, TopicsActivity.class);
//            intent.putExtra("check", check);
//            startActivity(intent);
//            overridePendingTransition(R.anim.enter, R.anim.exit);


//            Intent intent = new Intent(EditionActivity.this, MainActivityNew.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//            finish();

        } else {
//            mUserConfigPresenter.getUserConfig();
            presenter.getSelectedEditions();
//            if (!TextUtils.isEmpty(flow) && flow.equalsIgnoreCase("setting")) {
//                setResult(Activity.RESULT_OK);
//                finish();
//            } else {
//                setResult(Activity.RESULT_OK);
//                finish();
//            }
        }

    }
}