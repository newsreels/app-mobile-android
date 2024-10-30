package com.newsreels.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.newsreels.app.R;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.data.models.sources.Source;
import com.newsreels.app.interfaces.ChannelApiCallback;
import com.newsreels.app.presenter.ChannelEditPresenter;
import com.newsreels.app.utills.ChatEditText;
import com.newsreels.app.utills.SearchLayout;
import com.newsreels.app.utills.Utils;

public class ChannelDescriptionEditActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_CHANNEL = "EXTRA_CHANNEL";

    private RelativeLayout ivBack;
    private ChatEditText channelDescription;
    private ConstraintLayout saveDescription;
    private ImageView loader;
    private TextView countTv;

    private PrefConfig mPrefConfig;

    private Source channelObj;
    private String channelId;


    @Override
    public void onBackPressed() {
        Utils.showKeyboard(this);
    }

    private ChannelApiCallback channelApiCallback = new ChannelApiCallback() {
        @Override
        public void loaderShow(boolean flag) {
            loader.setVisibility(flag ? View.VISIBLE : View.GONE);
        }

        @Override
        public void error(String error, String img) {
            Toast.makeText(ChannelDescriptionEditActivity.this, error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void success(Object data) {
            Intent intent = new Intent();
            intent.putExtra("description", channelDescription.getText().toString());
            setResult(RESULT_OK, intent);
            Utils.hideKeyboard(ChannelDescriptionEditActivity.this, channelDescription);
            finish();
        }
    };
    private ChannelEditPresenter mChannelEditPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_channel_description_edit);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_CHANNEL)) {
            channelObj = (Source) getIntent().getParcelableExtra(EXTRA_CHANNEL);
        }
        if (channelObj != null) {
            channelId = channelObj.getId();
        }

        mPrefConfig = new PrefConfig(this);
        mChannelEditPresenter = new ChannelEditPresenter(this, channelApiCallback);


        bindViews();
        loadData();
        listeners();
    }

    private void loadData() {
        if (channelObj != null) {
            channelDescription.setText(channelObj.getDescription());
            countTv.setText(channelDescription.getText().length() + "/500");
        }
//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme()))
//                .into(loader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        channelDescription.requestFocus();
        SearchLayout.setSearchActivity(this);
        Utils.showKeyboard(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.hideKeyboard(this, channelDescription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.hideKeyboard(this, channelDescription);
    }

    private void bindViews() {
        ivBack = findViewById(R.id.back);
        channelDescription = findViewById(R.id.channel_description);
        saveDescription = findViewById(R.id.save_description);
        loader = findViewById(R.id.loader);
        countTv = findViewById(R.id.count);
    }

    private void listeners() {
        ivBack.setOnClickListener(this);
        saveDescription.setOnClickListener(this);

        channelDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    countTv.setText(s.length() + "/500");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            Utils.hideKeyboard(this, ivBack);
            finish();
        } else if (v.getId() == R.id.save_description) {
//            if(!TextUtils.isEmpty(channelDescription.getText())) {
            mChannelEditPresenter.updateChannelProfile(channelId, channelDescription.getText().toString(), null, null, null, true);
//            }
        }
    }
}