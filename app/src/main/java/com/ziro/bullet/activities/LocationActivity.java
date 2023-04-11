package com.ziro.bullet.activities;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziro.bullet.adapters.AddEditLocationAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.interfaces.LocationCallback;
import com.ziro.bullet.presenter.LocationPresenter;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.PaginationListener;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.location.LocationModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ziro.bullet.utills.PaginationListener.PAGE_START;

public class LocationActivity extends BaseActivity implements LocationCallback {
    private static final String TAG = LocationActivity.class.getSimpleName();

    private TextView continue_text;
    private RelativeLayout done;
    private PrefConfig prefConfig;
    private ProgressBar mProgressBar;
    private TextView mTvProgress;
    private LocationPresenter presenter;

    private ArrayList<Location> locationArrayList = new ArrayList<>();
    private TextView international;
    private RecyclerView international_recycler;
    private AddEditLocationAdapter addEditLocationAdapter;

    //search
    private EditText search;
    private long delay = 500; // 1 seconds after user stops typing
    private long last_text_edit = 0;
    private String searchStr = "";
    private Handler handler = new Handler();
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            presenter.getAllLocations(searchStr, PAGE_START);
        }
    };
    private String check = "";
    private String add = "";
    private String nextInternationalPage = PAGE_START;
    private boolean isLastInternationalPage = false;
    private boolean isLoadingInternational = false;
    private ArrayList<Location> favList = new ArrayList<>();
    private RelativeLayout back;
    private RelativeLayout gradient;
    private int minimum = 3;
    private boolean isDone = false;

    private ArrayList<String> followList = new ArrayList<>();
    private ArrayList<String> unfollowList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefConfig = new PrefConfig(this);

        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_location);

        bindView();
        listener();
        init();

        presenter.getAllLocations("", PAGE_START);
    }


    private void init() {
        presenter = new LocationPresenter(this, this);
        prefConfig = new PrefConfig(this);

        done.setBackground(getResources().getDrawable(R.drawable.shape));
        done.setEnabled(true);
        continue_text.setText(getResources().getString(R.string.continuee));

        LinearLayoutManager internationalManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        international_recycler.setLayoutManager(internationalManager);
//        international_recycler.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        international_recycler.setItemAnimator(new DefaultItemAnimator());
        addEditLocationAdapter = new AddEditLocationAdapter(LocationActivity.this, locationArrayList);
        addEditLocationAdapter.setClickListener(new AddEditLocationAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, boolean isFavorite) {
                if (locationArrayList != null && locationArrayList.size() > 0) {
                    if (locationArrayList.get(position).isFavorite()) {
                        for (int i = 0; i < favList.size(); i++) {
                            if (locationArrayList.get(position).getId().equalsIgnoreCase(favList.get(i).getId())) {
                                favList.get(i).setFavorite(false);
                                break;
                            }
                        }
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.LOCATION_ID,locationArrayList.get(position).getId());
                        AnalyticsEvents.INSTANCE.logEvent(LocationActivity.this,
                                params,
                                Events.UNFOLLOWED_LOCATION);
                    } else {
                        Map<String,String> params = new HashMap<>();
                        params.put(Events.KEYS.LOCATION_ID,locationArrayList.get(position).getId());
                        AnalyticsEvents.INSTANCE.logEvent(LocationActivity.this,
                                params,
                                Events.FOLLOW_LOCATION);
                        if (!favList.contains(locationArrayList.get(position))) {
                            favList.add(locationArrayList.get(position));
                        }
                    }

                    if (isFavorite) {
                        if (unfollowList.contains(locationArrayList.get(position).getId()))
                            unfollowList.remove(locationArrayList.get(position).getId());
                        else
                            followList.add(locationArrayList.get(position).getId());
                    } else {
                        if (followList.contains(locationArrayList.get(position).getId()))
                            followList.remove(locationArrayList.get(position).getId());
                        else
                            unfollowList.add(locationArrayList.get(position).getId());
                    }
                    locationArrayList.get(position).setFavorite(isFavorite);
                }
                checkData();
            }

            @Override
            public void isFavorite(View view, int position) {
                if (locationArrayList.size() > position) {
                    locationArrayList.get(position).setFavorite(true);
                    if (!favList.contains(locationArrayList.get(position))) {
                        favList.add(locationArrayList.get(position));
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
        international_recycler.setAdapter(addEditLocationAdapter);

        international_recycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                Utils.hideKeyboard(LocationActivity.this, international_recycler);
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        setPaginationInternational(international_recycler, internationalManager);
    }

    private void setPaginationInternational(RecyclerView mRvRecyclerView, LinearLayoutManager manager) {
        mRvRecyclerView.addOnScrollListener(new PaginationListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoadingInternational = true;
                mTvProgress.setText(getString(R.string.loading));

                presenter.getAllLocations(searchStr, nextInternationalPage);
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
//                Utils.hideKeyboard(LocationActivity.this, mRvRecyclerView);
            }

            @Override
            public void onScrolling(@NonNull @NotNull RecyclerView recyclerView, int newState) {

            }
        });
        mRvRecyclerView.setLayoutManager(manager);
    }

    private void listener() {
        done.setOnClickListener(v -> {
            done.setEnabled(false);
            ArrayList<String> sources = new ArrayList<>();
            if ((followList != null && followList.size() > 0)
                    || (unfollowList != null && unfollowList.size() > 0)) {

                Set<String> followSet = new HashSet<>(followList);
                Set<String> unfollowSet = new HashSet<>(unfollowList);
                followList.clear();
                followList.addAll(followSet);

                unfollowList.clear();
                unfollowList.addAll(unfollowSet);

                for (int i = 0; i < followList.size(); i++) {
                    Log.d(TAG, "listener: follow = = " + followList.get(i));
                }

                for (int i = 0; i < unfollowList.size(); i++) {
                    Log.d(TAG, "listener: unfollow = = " + unfollowList.get(i));
                }
                presenter.updateLocation(followList, unfollowList);
            } else {
                finish();
            }
        });
        back.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("recall", "recall");
            setResult(RESULT_OK, intent);
            finish();
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("TECT", "--> " + s.toString());
                handler.removeCallbacks(input_finish_checker);
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    searchStr = s.toString();
                    locationArrayList.clear();
                    handler.postDelayed(input_finish_checker, delay);
                } else if (s.length() == 0) {

                    international.setVisibility(View.GONE);
                    international_recycler.setVisibility(View.GONE);

                    locationArrayList.clear();
                    nextInternationalPage = PAGE_START;
                    isLastInternationalPage = false;
                    isLoadingInternational = false;
                    presenter.getAllLocations("", PAGE_START);
                }
            }
        });
    }

    private void checkData() {
        if (addEditLocationAdapter != null) {
            if (favList != null) {
                if (favList.size() > 0) {
                    for (int i = 0; i < favList.size(); i++) {
                        for (int j = 0; j < locationArrayList.size(); j++) {
                            if (locationArrayList.get(j).getId().equalsIgnoreCase(favList.get(i).getId())) {
                                locationArrayList.get(j).setFavorite(favList.get(i).isFavorite());
                            }
                        }
                    }
                    for (int i = 0; i < favList.size(); i++) {
                        if (!favList.get(i).isFavorite()) {
                            favList.remove(i);
                        }
                    }
                    addEditLocationAdapter.notifyDataSetChanged();
                    updateButton();
                } else {

                    done.setBackground(getResources().getDrawable(R.drawable.shape));
                    done.setEnabled(true);
                    continue_text.setText(getResources().getString(R.string.continuee));

                }
            }
        }
    }

    private void bindView() {
        gradient = findViewById(R.id.gradient);
        international = findViewById(R.id.international);
        international_recycler = findViewById(R.id.list);

        back = findViewById(R.id.back);
        search = findViewById(R.id.search);
        mProgressBar = findViewById(R.id.progress_circular);
        mTvProgress = findViewById(R.id.tvProgress);
        continue_text = findViewById(R.id.continue_text);
        done = findViewById(R.id.done);
        Utils.hideKeyboard(this, search);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.hideKeyboard(this, search);
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            mTvProgress.setText(getString(R.string.loading));
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mTvProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        mProgressBar.setVisibility(View.GONE);
        mTvProgress.setVisibility(View.VISIBLE);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(LocationModel response) {
        Log.d("TAG", "onResponse: " + new Gson().toJson(response));
        international.setVisibility(View.GONE);
        international_recycler.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        if (response != null) {
            if (response.getMeta() != null) {
                nextInternationalPage = response.getMeta().getNext();
            }
            if (TextUtils.isEmpty(nextInternationalPage)) {
                isLastInternationalPage = true;
            }
            if (response.getLocations() != null && response.getLocations().size() > 0) {
                locationArrayList.addAll(response.getLocations());
                international.setVisibility(View.VISIBLE);
                international_recycler.setVisibility(View.VISIBLE);
            } else {
                mTvProgress.setText(getString(R.string.no_record));
                mTvProgress.setVisibility(View.VISIBLE);
            }

            addEditLocationAdapter.notifyDataSetChanged();
        } else {
            mTvProgress.setText(getString(R.string.no_record));
            mTvProgress.setVisibility(View.VISIBLE);
        }
        checkData();
    }

    private void updateButton() {
        done.setBackground(getResources().getDrawable(R.drawable.shape));
        done.setEnabled(true);
        continue_text.setText(getResources().getString(R.string.continuee));
    }


    @Override
    public void addSuccess(int position) {
        done.setEnabled(true);
        Constants.isSourceDataChange = false;
        Toast.makeText(this, ""+getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("change", true);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void searchSuccess(LocationModel response) {
        locationArrayList.clear();
        international.setVisibility(View.GONE);
        international_recycler.setVisibility(View.GONE);
        if (response != null) {
            if (response.getMeta() != null) {
                nextInternationalPage = response.getMeta().getNext();
            }
            if (TextUtils.isEmpty(nextInternationalPage)) {
                isLastInternationalPage = true;
            }

            if (response.getLocations() != null && response.getLocations().size() > 0) {
                locationArrayList.addAll(response.getLocations());
                international.setVisibility(View.VISIBLE);
                international_recycler.setVisibility(View.VISIBLE);
            } else {
                mTvProgress.setText(getString(R.string.no_record));
                mTvProgress.setVisibility(View.VISIBLE);
            }
        }
        if (addEditLocationAdapter != null)
            addEditLocationAdapter.notifyDataSetChanged();
        checkData();
    }
}