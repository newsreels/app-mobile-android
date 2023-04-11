package com.ziro.bullet.activities.Channel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ziro.bullet.R;
import com.ziro.bullet.activities.BaseActivity;
import com.ziro.bullet.utills.ChatEditText;
import com.ziro.bullet.utills.SearchLayout;
import com.ziro.bullet.utills.Utils;

public class ChannelDescriptionActivity extends BaseActivity {

    private RelativeLayout back;
    private TextView next_btn_text;
    private ConstraintLayout next_btn;
    private ChatEditText descriptionTv;
    private TextView countTv;
    private String name;
    private String description;
    private String url;

    @Override
    protected void onResume() {
        super.onResume();
        SearchLayout.setSearchActivity(this);
        Utils.showKeyboard(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.hideKeyboard(this, next_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.hideKeyboard(this, next_btn);
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_channel_description);
        bindView();
        setListeners();
        setData();
    }

    private void bindView() {
        back = findViewById(R.id.back);
        next_btn = findViewById(R.id.next_btn);
        next_btn_text = findViewById(R.id.next_btn_text);
        descriptionTv = findViewById(R.id.description);
        countTv = findViewById(R.id.count);
    }

    private void setData() {
        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            description = getIntent().getStringExtra("description");
            url = getIntent().getStringExtra("url");
            if (!TextUtils.isEmpty(description)) {
                descriptionTv.setText(description);
            }
        }
        descriptionTv.requestFocus();
    }

    private void setListeners() {
        back.setOnClickListener(v -> {
            Utils.showKeyboard(this);
            Intent intent = new Intent();
            intent.putExtra("name", name);
            intent.putExtra("url", url);
            intent.putExtra("description", descriptionTv.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });
        next_btn.setOnClickListener(v -> {
            Utils.hideKeyboard(this, next_btn);
            Intent intent = new Intent(ChannelDescriptionActivity.this, ChannelImageActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("url", url);
            intent.putExtra("description", descriptionTv.getText().toString().trim());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, 101);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        descriptionTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    countTv.setText(s.length() + "/500");
                    if (s.length() == 0) {
                        next_btn_text.setText(getResources().getString(R.string.skip));
                    } else {
                        next_btn_text.setText(getResources().getString(R.string.next));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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