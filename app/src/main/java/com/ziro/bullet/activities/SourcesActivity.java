package com.ziro.bullet.activities;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.Sources2Adapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.CategoryModel;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.interfaces.SourceInterface;
import com.ziro.bullet.interfaces.SourcesHorizontalInterface;
import com.ziro.bullet.presenter.SourcePresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.SpacesItemDecoration;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SourcesActivity extends BaseActivity implements SourcesHorizontalInterface, SourceInterface {
    private static final String TAG = SourcesActivity.class.getSimpleName();

    private TextView continue_text;
    private RelativeLayout done;
    private PrefConfig prefConfig;
    private LinearLayout ll_no_results;
    private RelativeLayout mProgressContainer;
    private SourcePresenter presenter;
    private ArrayList<CategoryModel> mSourcesCategory = new ArrayList<>();
    private ArrayList<Source> internationalList = new ArrayList<>();
    private RecyclerView international_recycler;
    private Sources2Adapter international_adapter;

    //search
    private EditText search;
    private long delay = 500; // 1 seconds after user stops typing
    private long last_text_edit = 0;
    private String searchStr = "";
    private Handler handler = new Handler();
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            presenter.searchSources(searchStr, PAGE_START);
        }
    };
    private String check = "";
    private String add = "";
    private String nextInternationalPage = PAGE_START;
    private boolean isLastInternationalPage = false;
    private boolean isLoadingInternational = false;
    private ArrayList<Source> favList = new ArrayList<>();
    private RelativeLayout back;
    private RelativeLayout gradient;
    private RelativeLayout cancel;
    private int minimum = 3;
    private boolean isDone = false;

    private ArrayList<String> followList = new ArrayList<>();
    private ArrayList<String> unfollowList = new ArrayList<>();
    private SpacesItemDecoration spaces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefConfig = new PrefConfig(this);
        if (prefConfig.getRegistration() != null && prefConfig.getRegistration().getTopic() != null) {
            minimum = prefConfig.getRegistration().getTopic().getMinimum();
            isDone = prefConfig.getRegistration().getTopic().isDone();
        }
        checkBundle();
        if (!TextUtils.isEmpty(add)) {
            Utils.checkAppModeColor(this, false);
        } else {
            new Components().settingStatusBarColors(this, "black");
            BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            Utils.saveSystemThemeAsDefault(this, prefConfig);
        }
        setContentView(R.layout.activity_sources);

        bindView();
        listener();
        init();

        presenter.AllSources(PAGE_START, true);
    }

    private void checkBundle() {
        if (getIntent() != null) {
            check = getIntent().getStringExtra("check");
            add = getIntent().getStringExtra("add");
        }
    }

    private void init() {
        spaces = new SpacesItemDecoration(18);
        presenter = new SourcePresenter(this, this);

        done.setBackground(getResources().getDrawable(R.drawable.shape));
        done.setEnabled(true);
        continue_text.setText(getResources().getString(R.string.continuee));

    }

    private void setPaginationInternational(RecyclerView mRvRecyclerView, GridLayoutManager manager) {
        mRvRecyclerView.addOnScrollListener(new PaginationListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoadingInternational = true;
                if (TextUtils.isEmpty(searchStr)) {
                    presenter.sourcesInternational(nextInternationalPage);
                } else {
                    presenter.searchSources(searchStr, nextInternationalPage);
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastInternationalPage;
            }

            @Override
            public boolean isLoading() {
                return isLoadingInternational;
            }

            @Override
            public void onScroll(int position) {
                Utils.hideKeyboard(SourcesActivity.this, mRvRecyclerView);
            }

            @Override
            public void onScrolling(@NonNull @NotNull RecyclerView recyclerView, int newState) {

            }
        });
        mRvRecyclerView.setLayoutManager(manager);
    }

    private void listener() {
        findViewById(R.id.help).setOnClickListener(v -> Utils.openHelpView(this, BuildConfig.HelpUrl, "Help"));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });
        done.setOnClickListener(v -> {
            done.setEnabled(false);
            back.setEnabled(false);
            Set<String> followSet = new HashSet<>(followList);
            Set<String> unfollowSet = new HashSet<>(unfollowList);
            followList.clear();
            followList.addAll(followSet);

            unfollowList.clear();
            unfollowList.addAll(unfollowSet);

            presenter.updateSource(followList, unfollowList);
        });
        back.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(check) && check.equalsIgnoreCase("signup")) {
                finishAfterTransition();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            } else {
                Intent intent = new Intent();
                intent.putExtra("recall", "recall");
                setResult(122, intent);
                finish();
            }
        });
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

                    international_recycler.setVisibility(View.GONE);

                    mSourcesCategory.clear();
                    internationalList.clear();
                    nextInternationalPage = PAGE_START;
                    isLastInternationalPage = false;
                    isLoadingInternational = false;
                    presenter.AllSources(PAGE_START, false);
                }
            }
        });
    }

    private void checkData() {
        if (international_adapter != null) {
            if (favList != null) {
                if (favList.size() > 0) {
                    for (int i = 0; i < favList.size(); i++) {
                        for (int j = 0; j < internationalList.size(); j++) {
                            if (internationalList.get(j).getId().equalsIgnoreCase(favList.get(i).getId())) {
                                internationalList.get(j).setFavorite(favList.get(i).isFavorite());
                            }
                        }
                    }
                    for (int i = 0; i < favList.size(); i++) {
                        if (!favList.get(i).isFavorite()) {
                            favList.remove(i);
                        }
                    }
                    international_adapter.notifyDataSetChanged();
//                    updateButton();
                }
//                else {
//                    if (!TextUtils.isEmpty(add)) {
//                        done.setBackground(getResources().getDrawable(R.drawable.shape));
//                        done.setEnabled(true);
//                        continue_text.setText(getResources().getString(R.string.continuee));
//                    } else {
//                        done.setBackground(getResources().getDrawable(R.drawable.shape_grey));
//                        done.setEnabled(false);
//                        continue_text.setText(getResources().getString(R.string.follow_3_to_continue));
//                    }
//                }
            }
        }
    }

    private void bindView() {
        cancel = findViewById(R.id.cancel);
        gradient = findViewById(R.id.gradient);
        international_recycler = findViewById(R.id.list);

        back = findViewById(R.id.back);
        search = findViewById(R.id.search);
        ll_no_results = findViewById(R.id.ll_no_results);
        mProgressContainer = findViewById(R.id.progress);
        continue_text = findViewById(R.id.continue_text);
        done = findViewById(R.id.done);

//        ImageView loader_gif = findViewById(R.id.loader);
//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(prefConfig.getAppTheme()))
//                .into(loader_gif);
//        done.setEnabled(false);

        international_recycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Utils.hideKeyboard(SourcesActivity.this, international_recycler);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        Utils.hideKeyboard(this, search);
    }

    @Override
    public void Place(String name, double lat, double lng) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.hideKeyboard(this, search);
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            ll_no_results.setVisibility(View.GONE);
            mProgressContainer.setVisibility(View.VISIBLE);
        } else {
            mProgressContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        if (!TextUtils.isEmpty(error) && error.toLowerCase().contains("cancel")) {
            return;
        }
        done.setEnabled(true);
        back.setEnabled(true);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void error404(String error) {
        done.setEnabled(true);
        back.setEnabled(true);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(SourceModel response, boolean setAdapter) {
        Log.d("TAG", "onResponse: " + new Gson().toJson(response));
        international_recycler.setVisibility(View.GONE);

        if (response != null) {
            if (response.getMeta() != null) {
                nextInternationalPage = response.getMeta().getNext();
            }
            if (TextUtils.isEmpty(nextInternationalPage)) {
                isLastInternationalPage = true;
            }
            if (response.getSources() != null && response.getSources().size() > 0) {
                internationalList.addAll(response.getSources());
                international_recycler.setVisibility(View.VISIBLE);
                ll_no_results.setVisibility(View.GONE);
            } else {
                ll_no_results.setVisibility(View.VISIBLE);
            }

            if (setAdapter) {
                international_recycler.removeItemDecoration(spaces);
                GridLayoutManager internationalManager = new GridLayoutManager(this, 2);
                international_recycler.addItemDecoration(spaces);
                international_recycler.setLayoutManager(internationalManager);
                international_adapter = new Sources2Adapter(SourcesActivity.this, internationalList);
                international_adapter.setClickListener(new Sources2Adapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, boolean isFavorite) {
                        if (internationalList != null && internationalList.size() > 0) {
                            if (internationalList.get(position).isFavorite()) {
                                for (int i = 0; i < favList.size(); i++) {
                                    if (internationalList.get(position).getId().equalsIgnoreCase(favList.get(i).getId())) {
                                        favList.get(i).setFavorite(false);
                                        break;
                                    }
                                }
                            } else {
                                if (!favList.contains(internationalList.get(position))) {
                                    favList.add(internationalList.get(position));
                                }
                            }

                            if (isFavorite) {
                                if (unfollowList.contains(internationalList.get(position).getId()))
                                    unfollowList.remove(internationalList.get(position).getId());
                                else
                                    followList.add(internationalList.get(position).getId());
                            } else {
                                if (followList.contains(internationalList.get(position).getId()))
                                    followList.remove(internationalList.get(position).getId());
                                else
                                    unfollowList.add(internationalList.get(position).getId());
                            }
                            internationalList.get(position).setFavorite(isFavorite);
                        }
                        checkData();
                    }

                    @Override
                    public void isFavorite(View view, int position) {
                        if (internationalList.size() > position) {
                            internationalList.get(position).setFavorite(true);
                            if (!favList.contains(internationalList.get(position))) {
                                favList.add(internationalList.get(position));
                            }
                            updateButton();
                        }
                    }

                    @Override
                    public void isLastItem(boolean flag) {
                        if (flag) {
                            gradient.setVisibility(View.GONE);
                        } else {
                            gradient.setVisibility(View.VISIBLE);
                        }
                    }
                });
                international_recycler.setAdapter(international_adapter);
                setPaginationInternational(international_recycler, internationalManager);
            }
            if (international_adapter != null) {
                international_adapter.notifyDataSetChanged();
            }
        }
        checkData();
    }

    @Override
    public void successLocalPaginationResult(CategoryModel response) {
//        isLoadingLocal = false;
//        if (response != null) {
//            if (!TextUtils.isEmpty(response.getName())) {
//                if (response.getName().equalsIgnoreCase("Local")) {
//                    nextLocalPage = response.getMeta().getNextPage();
//                    localList.addAll(response.getSources());
//                    local_adapter.notifyDataSetChanged();
//                    if (currentLocalPage == nextLocalPage) {
//                        isLastLocalPage = true;
//                    }
//                }
//            }
//        }
    }

    private void updateButton() {
//        if (!TextUtils.isEmpty(add)) {
//            done.setBackground(getResources().getDrawable(R.drawable.shape));
//            done.setEnabled(true);
//            continue_text.setText(getResources().getString(R.string.continuee));
//        } else {
//            if (favList.size() >= minimum) {
//                done.setBackground(getResources().getDrawable(R.drawable.shape));
//                done.setEnabled(true);
//                continue_text.setText(getResources().getString(R.string.continuee));
//            } else {
//                done.setBackground(getResources().getDrawable(R.drawable.shape_grey));
//                done.setEnabled(false);
//                continue_text.setText(getResources().getString(R.string.follow_3_to_continue));
//            }
//        }
    }

    @Override
    public void successInternationalPaginationResult(SourceModel response) {
        isLoadingInternational = false;
        if (response != null) {
            if (response.getMeta() != null) {
                nextInternationalPage = response.getMeta().getNext();
            }
            if (TextUtils.isEmpty(nextInternationalPage)) {
                isLastInternationalPage = true;
            }
            internationalList.addAll(response.getSources());
            international_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addSuccess(int position) {

//        if (!TextUtils.isEmpty(add)) {
//            done.setEnabled(true);
//            Constants.isSourceDataChange = true;
//            Utils.showSnacky(done, ""+getString(R.string.added_successfully));
////            Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
//            finish();
//        } else {
        updateWidget();
//        Intent intent = new Intent(SourcesActivity.this, DarkLightModeActivity.class);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivityForResult(intent, 101);
//        overridePendingTransition(R.anim.enter, R.anim.exit);


        Intent intent = new Intent(SourcesActivity.this, MainActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();


//            Intent intent = new Intent(SourcesActivity.this, MainActivityNew.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            done.setEnabled(true);
            back.setEnabled(true);
        }
    }

    @Override
    public void deleteSuccess(int position) {

    }

    @Override
    public void searchSuccess(SourceModel response) {
        internationalList.clear();
        international_recycler.setVisibility(View.GONE);
        if (response != null) {
            if (response.getMeta() != null) {
                nextInternationalPage = response.getMeta().getNext();
            }
            if (TextUtils.isEmpty(nextInternationalPage)) {
                isLastInternationalPage = true;
            }

            if (response.getSources() != null && response.getSources().size() > 0) {
                internationalList.addAll(response.getSources());
                international_recycler.setVisibility(View.VISIBLE);
                ll_no_results.setVisibility(View.GONE);
            } else {
                ll_no_results.setVisibility(View.VISIBLE);
            }

//            if (response.getCategoryModel() != null && response.getCategoryModel().size() > 0) {
//                mTvProgress.setVisibility(View.GONE);
//                for (int i = 0; i < response.getCategoryModel().size(); i++) {
//                    CategoryModel model = response.getCategoryModel().get(i);
//                    if (model != null) {
//                        if (!TextUtils.isEmpty(model.getName())) {
//                            if (model.getName().equalsIgnoreCase("international")) {
//                                internationalList.addAll(model.getSources());
//                                international.setVisibility(View.VISIBLE);
//                                international_recycler.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    }
//                }
//            }
        }
        if (international_adapter != null)
            international_adapter.notifyDataSetChanged();
        checkData();
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
}