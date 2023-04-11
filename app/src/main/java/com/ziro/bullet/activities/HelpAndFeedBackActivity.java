package com.ziro.bullet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.ziro.bullet.R;
import com.ziro.bullet.utills.Utils;

public class HelpAndFeedBackActivity extends BaseActivity {

    private ConstraintLayout feedbackBtn;
    private ConstraintLayout helpBtn;
    private RelativeLayout ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_feed_back);

        bindViews();
        listeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkAppModeColor(this, false);
    }

    private void bindViews() {
        feedbackBtn = findViewById(R.id.feedback_btn);
        helpBtn = findViewById(R.id.help_btn);
        ivBack = findViewById(R.id.ivBack);
    }

    private void listeners() {
        feedbackBtn.setOnClickListener(v -> startActivity(new Intent(HelpAndFeedBackActivity.this, SuggestionActivity.class)));

        helpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HelpAndFeedBackActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        ivBack.setOnClickListener(v -> finish());
    }


}