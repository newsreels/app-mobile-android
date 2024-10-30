package com.newsreels.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsreels.app.R;
import com.newsreels.app.activities.Channel.ChannelNameActivity;
import com.newsreels.app.adapters.MyChannelAdapter;
import com.newsreels.app.analytics.AnalyticsEvents;
import com.newsreels.app.analytics.Events;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.interfaces.UserChannelCallback;
import com.newsreels.app.data.models.sources.Source;
import com.newsreels.app.model.CategorizedChannelsData;
import com.newsreels.app.presenter.UserChannelPresenter;
import com.newsreels.app.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyChannelsActivity extends BaseActivity implements UserChannelCallback {
    private static final String TAG = "MyChannelsFragment";
    private static final int REQUEST_CREATE_CHANNEL = 101;

    private UserChannelPresenter userChannelPresenter;

    private RelativeLayout rlBack;
    private ProgressBar progress;
    private LinearLayout llContactUs;
    private RelativeLayout rlCreateChannel;
    private RecyclerView mRecyclerView;
    private MyChannelAdapter mAdapter;

    private ArrayList<Source> channelList = new ArrayList<>();

    private PrefConfig mPrefConfig;
    private boolean isLoading= false;
    private String mPage = "";

    public static void launchActivity(Activity activity) {
        Intent intent = new Intent(activity, MyChannelsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setBlackStatusBarColor(this);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.fragment_my_channels);

        mPrefConfig = new PrefConfig(this);

        rlBack = findViewById(R.id.ivBack);
        progress = findViewById(R.id.progress);
        mRecyclerView = findViewById(R.id.rvChannels);
        llContactUs = findViewById(R.id.contactUs);
        rlCreateChannel = findViewById(R.id.rlCreateChannel);

        mAdapter = new MyChannelAdapter(this, channelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        userChannelPresenter = new UserChannelPresenter(this, this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !TextUtils.isEmpty(mPage)) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount - 5) {
                            userChannelPresenter.getUserChannels(mPage);
                        }
                    }
                }
            }
        });

        rlCreateChannel.setOnClickListener(v -> {
            if (mPrefConfig != null) {
                if (mPrefConfig.isGuestUser()) {
                    LoginPopupActivity.start(this);
                } else {
                    AnalyticsEvents.INSTANCE.logEvent(MyChannelsActivity.this,
                            Events.CREATE_CHANNEL_CLICK);
                    startActivityForResult(new Intent(this, ChannelNameActivity.class), REQUEST_CREATE_CHANNEL);
                }
            }
        });

        llContactUs.setOnClickListener(v -> {
            if (mPrefConfig != null) {
                if (mPrefConfig.isGuestUser()) {
                    LoginPopupActivity.start(this);
                } else {
                    Intent intent = new Intent(this, ContactUsActivity.class);
                    intent.putExtra("msg", getString(R.string.contact_us_prefilled_create_channel));
                    startActivity(intent);
                }
            }
        });

        rlBack.setOnClickListener(v -> onBackPressed());

        if (userChannelPresenter != null) {
            userChannelPresenter.getUserChannels(mPage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CREATE_CHANNEL
                && resultCode == Activity.RESULT_OK) {
            if (userChannelPresenter != null) {
                userChannelPresenter.getUserChannels(mPage);
            }
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag && !isFinishing()) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        if (!isFinishing()) {
            Utils.showSnacky(progress, getString(R.string.server_error));
        }
    }

    @Override
    public void success(ArrayList<Source> channels) {
        if (isFinishing()) {
            return;
        }
        channelList.clear();
        if (channels != null) {
            channelList.addAll(channels);
            mAdapter.notifyDataSetChanged();

            rlCreateChannel.setVisibility(channels.size() < 5 ? View.VISIBLE : View.GONE);
            llContactUs.setVisibility(channels.size() >= 5 ? View.VISIBLE : View.GONE);

            if (channels.size() > 0) {
                progress.setVisibility(View.GONE);
            } else {
                Utils.showSnacky(progress, getString(R.string.you_don_t_have_any_channels_yet));
                progress.setVisibility(View.VISIBLE);
            }
        } else {
            rlCreateChannel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void successData(ArrayList<CategorizedChannelsData> channels) {

    }

    @Override
    public void channelSelected() {

    }
}
