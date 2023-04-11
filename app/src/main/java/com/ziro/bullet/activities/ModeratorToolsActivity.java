package com.ziro.bullet.activities;

import static com.ziro.bullet.activities.ChannelPhotoEditActivity.EXTRA_CHANNEL;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.utills.MessageEvent;
import com.ziro.bullet.utills.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ModeratorToolsActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mIvBack;
    private ConstraintLayout scheduledPostBtn;
    private ConstraintLayout draftsBtn;
    private ConstraintLayout channelAndCoverBtn;
    private ConstraintLayout descriptionBtn;

    private Source channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_moderator_tools);

        if (getIntent().hasExtra("SOURCE")) {
            String sourceString = getIntent().getStringExtra("SOURCE");
            channel = new Gson().fromJson(sourceString, Source.class);
        }

        bindViews();
        init();
    }

    private void bindViews() {
        mIvBack = findViewById(R.id.ivBack);
        scheduledPostBtn = findViewById(R.id.scheduled_post_btn);
        draftsBtn = findViewById(R.id.drafts_btn);
        channelAndCoverBtn = findViewById(R.id.channel_and_cover_btn);
        descriptionBtn = findViewById(R.id.description_btn);
    }

    private void init() {
        mIvBack.setOnClickListener(this);
        scheduledPostBtn.setOnClickListener(this);
        draftsBtn.setOnClickListener(this);
        channelAndCoverBtn.setOnClickListener(this);
        descriptionBtn.setOnClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.TYPE_CHANNEL_PROFILE_CHANGED) {
            channel = (Source) event.getObjectData();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {
            finish();
        } else if (v.getId() == R.id.scheduled_post_btn) {
            Intent intent = new Intent(this, ScheduleListingActivity.class);
            if (channel != null && !TextUtils.isEmpty(channel.getId()))
                intent.putExtra(ScheduleListingActivity.SOURCE_ID, channel.getId());
            startActivity(intent);
        } else if (v.getId() == R.id.drafts_btn) {
            Intent intent = new Intent(this, DraftsListingActivity.class);
            if (channel != null && !TextUtils.isEmpty(channel.getId()))
                intent.putExtra(DraftsListingActivity.SOURCE_ID, channel.getId());
            startActivity(intent);
        } else if (v.getId() == R.id.channel_and_cover_btn) {
            Intent intent = new Intent(this, ChannelPhotoEditActivity.class);
            if (channel != null)
                intent.putExtra(EXTRA_CHANNEL, channel);
            startActivityForResult(intent, 122);
        } else if (v.getId() == R.id.description_btn) {
            Intent intent = new Intent(this, ChannelDescriptionEditActivity.class);
            if (channel != null)
                intent.putExtra(ChannelDescriptionEditActivity.EXTRA_CHANNEL, channel);
            startActivityForResult(intent, 121);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 121) {
                if (channel != null && data != null && !TextUtils.isEmpty(data.getStringExtra("description"))) {
                    channel.setDescription(data.getStringExtra("description"));
                }
            } else if (requestCode == 122) {
                if (channel != null && data != null) {
                    if (data.hasExtra(EXTRA_CHANNEL)) {
                        Source channelDummy = (Source) data.getSerializableExtra(EXTRA_CHANNEL);
                        if (channelDummy != null)
                            channel = channelDummy;
                    }
                }
            }
        }
    }
}