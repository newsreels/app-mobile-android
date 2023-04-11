package com.ziro.bullet.activities.Channel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.CreateChannelCallback;
import com.ziro.bullet.presenter.CreateChannelPresenter;
import com.ziro.bullet.utills.ChatEditText;
import com.ziro.bullet.utills.SearchLayout;
import com.ziro.bullet.utills.Utils;

public class ChannelNameActivity extends AppCompatActivity implements CreateChannelCallback {

    private RelativeLayout back;
    private TextView next_btn_text;
    private ConstraintLayout next_btn;
    private ChatEditText nameTv;
    private TextView countTv;
    private TextView errorTv;
    private CreateChannelPresenter presenter;
    private Handler handler = new Handler();
    private Runnable input_finish_checker = this::search;
//    private ImageView loader;
    private RelativeLayout progress;
    private String name;
    private String description;
    private String url;
    private PrefConfig mPrefConfig;
    private boolean isNext = false;

    @Override
    protected void onResume() {
        super.onResume();
        SearchLayout.setSearchActivity(this);
        new Handler().postDelayed(() -> {
            Utils.showKeyboard(ChannelNameActivity.this);
            if (nameTv != null)
                nameTv.requestFocus();
        }, 200);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isNext) {
            Utils.hideKeyboard(this, next_btn);
        } else {
            isNext = false;
        }
    }

    @Override
    protected void onDestroy() {
        Utils.hideKeyboard(this, next_btn);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_channel_name);
        bindView();
        setListeners();
        init();
        setData();
        isNextEnable(false);
    }

    private void init() {
        mPrefConfig = new PrefConfig(this);
        presenter = new CreateChannelPresenter(this, this);
    }

    private void setListeners() {
        back.setOnClickListener(v -> {
            Utils.hideKeyboard(this, next_btn);
            finish();
        });
        next_btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(nameTv.getText().toString().trim())) {
                Toast.makeText(ChannelNameActivity.this, getString(R.string.enter_channel_name), Toast.LENGTH_SHORT).show();
                return;
            }
            isNext = true;
            Intent intent = new Intent(ChannelNameActivity.this, ChannelDescriptionActivity.class);
            intent.putExtra("name", nameTv.getText().toString().trim());
            intent.putExtra("description", description);
            intent.putExtra("url", url);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, 101);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        nameTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
                if (s != null) {
                    error("");
                    countTv.setText(s.length() + "/50");
                    isNextEnable(false);
                    if (nameTv.getText().toString().trim().length() > 1)
                        handler.postDelayed(input_finish_checker, 500);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void search() {
        ApiClient.cancelAll();
        if (presenter != null) {
            presenter.checkValidName(nameTv.getText().toString());
        }
    }

    private void setData() {
        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            description = getIntent().getStringExtra("description");
            url = getIntent().getStringExtra("url");
            if (!TextUtils.isEmpty(name)) {
                nameTv.setText(name);
            }
        }
//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme()))
//                .into(loader);
        nameTv.requestFocus();
    }

    private void isNextEnable(boolean isEnable) {
        Log.d("isNextEnable", "isNextEnable() called with: isEnable = [" + isEnable + "]");
        next_btn.setEnabled(isEnable);
        if (isEnable) {
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_post_article_button));
            next_btn_text.setTextColor(getResources().getColor(R.color.white));
        } else {
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_post_article_button_disable));
            next_btn_text.setTextColor(getResources().getColor(R.color.disable_next_btn_text));
        }
    }

    private void bindView() {
        back = findViewById(R.id.back);
        errorTv = findViewById(R.id.error);
        next_btn = findViewById(R.id.next_btn);
        next_btn_text = findViewById(R.id.next_btn_text);
        nameTv = findViewById(R.id.name);
        countTv = findViewById(R.id.count);
        progress = findViewById(R.id.progress);
//        loader = findViewById(R.id.loader);
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
        errorTv.setTextColor(getResources().getColor(R.color.red));
        errorTv.setText("" + error);
    }

    @Override
    public void mediaUploaded(String url, String type, int request) {

    }

    @Override
    public void validName(boolean isValid) {
        isNextEnable(isValid);
        if (!isValid) {
            errorTv.setText(nameTv.getText() + " " + getResources().getString(R.string.is_already_in_use));
            errorTv.setTextColor(getResources().getColor(R.color.red));
        } else {
            errorTv.setText(nameTv.getText() + " " + getResources().getString(R.string.is_available));
            errorTv.setTextColor(getResources().getColor(R.color.green));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 101) {
            if (data != null) {
                name = data.getStringExtra("name");
                description = data.getStringExtra("description");
                url = data.getStringExtra("url");
                if (data.getBooleanExtra("finish", false)) {
                    Intent intent = new Intent();
                    intent.putExtra("finish", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
    }
}