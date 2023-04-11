package com.ziro.bullet.activities.changereels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.BaseActivity;
import com.ziro.bullet.adapters.NewFeed.SuggestedChannelAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.TopicsModel;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.model.Edition.EditionsItem;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeReelsActivity extends BaseActivity {
    private PrefConfig mPrefConfig;
    private RecyclerView rvChannelsSuggested, rvTopicsSuggested;
    private TextView tvSuggestedChannels, tvSuggestedTopics;
    private RadioButton rb_for_you,
            rb_following,
            rb_community;

//    private boolean hasFollowers = false;
    private PictureLoadingDialog loadingDialog;

//    private ReelTopicsAdapter topicsAdapter;
//    private GridLayoutManager topicsLayoutManager;
//    private ArrayList<Topics> mTopicsList = new ArrayList<>();
//    private boolean isTopicsLoading = false;
//    private String mNextPage = "";
    private SuggestedChannelAdapter mChannelsSuggestedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_change_reels);

        mPrefConfig = new PrefConfig(this);
        bindViews();

//        getFollowingTopics();
        getSuggestedTopics(true);
        getSuggestedChannels(true);
//        getFollowersWithReels();
    }

    private void bindViews() {
        tvSuggestedChannels = findViewById(R.id.tvSuggestedChannelsLabel);
        tvSuggestedTopics = findViewById(R.id.tvSuggestedTopicsLabel);

        RadioGroup rgReels = findViewById(R.id.radio_reels);
        rb_for_you = findViewById(R.id.rb_for_you);
        rb_following = findViewById(R.id.rb_following);
        rb_community = findViewById(R.id.rb_community);

        rvChannelsSuggested = findViewById(R.id.rvChannelsSuggested);
        rvTopicsSuggested = findViewById(R.id.rvTopicsSuggested);

        loadReelType();


        rgReels.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);

            if (checkedRadioButton == rb_for_you) {
                selectForYou();
                onBackPressed();
            } else if (checkedRadioButton == rb_following) {
                selectFollowing();
//                if (!hasFollowers) {
//                    FollowingActivity.launchActivity(this, true);
//                } else {
                    onBackPressed();
//                }
            } else {
                selectCommunity();
                onBackPressed();
            }
        });

        findViewById(R.id.ivBack).setOnClickListener(v -> onBackPressed());


//        topicsLayoutManager = new GridLayoutManager(ChangeReelsActivity.this, 2, GridLayoutManager.HORIZONTAL, false);

//        rvChannelsSuggested.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int visibleItemCount = topicsLayoutManager.getChildCount();
//                int totalItemCount = topicsLayoutManager.getItemCount();
//                int firstVisibleItemPosition = 0;
//
//                firstVisibleItemPosition = topicsLayoutManager.findFirstVisibleItemPosition();
//
//                if (!isTopicsLoading && !TextUtils.isEmpty(mNextPage)) {
//                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
//                            && firstVisibleItemPosition >= 0
//                    ) {
//                        getFollowingTopics();
//                    }
//                }
//            }
//        });

//        rvChannelsSuggested.setLayoutManager(topicsLayoutManager);
//
//        topicsAdapter = new ReelTopicsAdapter(ChangeReelsActivity.this, mTopicsList, true);
//        rvChannelsSuggested.setAdapter(topicsAdapter);
    }

    private void loadReelType() {
        switch (mPrefConfig.getReelsType()) {
            case Constants.REELS_FOR_YOU:
                rb_for_you.setChecked(true);
                break;
            case Constants.REELS_FOLLOWING:
                rb_following.setChecked(true);
                break;
            default:
                rb_community.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    private void selectForYou() {
        mPrefConfig.setReelsType(Constants.REELS_FOR_YOU);
    }

    private void selectFollowing() {
        mPrefConfig.setReelsType(Constants.REELS_FOLLOWING);
    }

    private void selectCommunity() {
        mPrefConfig.setReelsType(Constants.REELS_COMMUNITY);
    }


    private View createEditionItem(EditionsItem editionsItem) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = vi.inflate(R.layout.edition_menu_item, null);

        ImageView editionIcon = v.findViewById(R.id.edition_icon);
        TextView locationEd = v.findViewById(R.id.location_ed);
        TextView languageEd = v.findViewById(R.id.language_ed);

        Picasso.get().load(editionsItem.getImage()).into(editionIcon);
        locationEd.setText(editionsItem.getName());
        languageEd.setText(editionsItem.getLanguage());

        return v;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void getFollowersWithReels() {
//        showLoadingView(true);
//        Call<ResponseBody> call = ApiClient
//                .getInstance(this)
//                .getApi()
//                .getFollowersWithReels("Bearer " + mPrefConfig.getAccessToken());
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                showLoadingView(false);
//                if (response.isSuccessful()) {
//                    try {
//                        if (response.body() != null) {
//                            JSONObject jsonObject = new JSONObject(response.body().string());
//                            hasFollowers = jsonObject.getBoolean("has_following");
//                        }
//                    } catch (JSONException | IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                showLoadingView(false);
//            }
//        });
//    }

//    public void getFollowingTopics() {
//        isTopicsLoading = true;
//        Call<TopicsModel> call = ApiClient
//                .getInstance(this)
//                .getApi()
//                .getFollowingTopics("Bearer " + mPrefConfig.getAccessToken(), mNextPage);
//        call.enqueue(new Callback<TopicsModel>() {
//            @Override
//            public void onResponse(Call<TopicsModel> call, Response<TopicsModel> response) {
//                isTopicsLoading = false;
//                if (response.isSuccessful() && response.body() != null
//                        && response.body().getTopics() != null
//                        && response.body().getTopics().size() > 0) {
//                    tvSuggestedChannels.setVisibility(View.VISIBLE);
//                    mTopicsList.addAll(response.body().getTopics());
//
//                    if (mTopicsList.size() > 2) {
//                        topicsLayoutManager = new GridLayoutManager(ChangeReelsActivity.this, 2, GridLayoutManager.HORIZONTAL, false);
//                    } else {
//                        topicsLayoutManager = new GridLayoutManager(ChangeReelsActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
//                    }
//                    rvChannelsSuggested.setLayoutManager(topicsLayoutManager);
//
//                }
//                if (response.body() != null && response.body().meta != null) {
//                    mNextPage = response.body().meta.getNext();
//                } else
//                    mNextPage = "";
//            }
//
//            @Override
//            public void onFailure(Call<TopicsModel> call, Throwable t) {
//                isTopicsLoading = false;
//            }
//        });
//    }

    public void getSuggestedChannels(boolean hasReels) {
        Call<SourceModel> call = ApiClient
                .getInstance(this)
                .getApi()
                .getSuggestedChannels("Bearer " + mPrefConfig.getAccessToken(),hasReels);
        call.enqueue(new Callback<SourceModel>() {
            @Override
            public void onResponse(@NonNull Call<SourceModel> call, @NonNull Response<SourceModel> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getSources() != null
                        && response.body().getSources().size() > 0) {

                    tvSuggestedChannels.setVisibility(View.VISIBLE);

                    rvChannelsSuggested.setLayoutManager(new GridLayoutManager(ChangeReelsActivity.this, 1, RecyclerView.HORIZONTAL, false));
                    mChannelsSuggestedAdapter = new SuggestedChannelAdapter(ChangeReelsActivity.this, response.body().getSources(), false);
                    rvChannelsSuggested.setAdapter(mChannelsSuggestedAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SourceModel> call, @NonNull Throwable t) {
            }
        });
    }

    public void getSuggestedTopics(boolean hasReels) {
        Call<TopicsModel> call = ApiClient
                .getInstance(this)
                .getApi()
                .getSuggestedTopics("Bearer " + mPrefConfig.getAccessToken(), hasReels);
        call.enqueue(new Callback<TopicsModel>() {
            @Override
            public void onResponse(@NonNull Call<TopicsModel> call, @NonNull Response<TopicsModel> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getTopics() != null
                        && response.body().getTopics().size() > 0) {
                    tvSuggestedTopics.setVisibility(View.VISIBLE);
                    rvTopicsSuggested.setLayoutManager(new GridLayoutManager(ChangeReelsActivity.this, 2, GridLayoutManager.HORIZONTAL, false));
                    rvTopicsSuggested.setAdapter(new ReelTopicsAdapter(ChangeReelsActivity.this, response.body().getTopics(), true));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TopicsModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void showLoadingView(boolean isShow) {
        if (isFinishing()) {
            return;
        }
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = new PictureLoadingDialog(this);
            }
            loadingDialog.show();
        } else {
            if (loadingDialog != null
                    && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}