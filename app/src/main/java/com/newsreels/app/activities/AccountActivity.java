package com.newsreels.app.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.newsreels.app.R;
import com.newsreels.app.analytics.AnalyticsEvents;
import com.newsreels.app.analytics.Events;
import com.newsreels.app.data.PrefConfig;
import com.newsreels.app.utills.Constants;
import com.newsreels.app.utills.Utils;

public class AccountActivity extends BaseActivity implements View.OnClickListener {
    // private static final String TAG = AccountActivity.class.getSimpleName();
    private static final String TYPE_EMAIL = "change_email";
    private static final String TYPE_PASSWORD = "change_pwd";

    private RelativeLayout mIvBack;
    private ConstraintLayout mRlChangeEmail;
    private ConstraintLayout mRlChangePwd;
    private TextView mTvUsername;
    private View divider;
    private PrefConfig mPrefConfig;
    private ConstraintLayout blockedListBtn;
    private ConstraintLayout savedArticleBtn;

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkAppModeColor(this, false);

        try {
            mTvUsername.setText(mPrefConfig.getUserEmail());
        } catch (Exception ignore) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        bindViews();
        init();
        AnalyticsEvents.INSTANCE.logEvent(this,
                Events.ACCOUNT_PAGE_CLICK);
    }


    private void init() {
        mPrefConfig = new PrefConfig(this);

        mIvBack.setOnClickListener(this);
        mRlChangeEmail.setOnClickListener(this);
        mRlChangePwd.setOnClickListener(this);
        blockedListBtn.setOnClickListener(this);
        savedArticleBtn.setOnClickListener(this);

        if (!mPrefConfig.getHasPassword()){
            mRlChangeEmail.setVisibility(View.GONE);
            mRlChangePwd.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
    }

    private void bindViews() {
        mIvBack = findViewById(R.id.ivBack);
        mRlChangeEmail = findViewById(R.id.rl_change_email);
        mRlChangePwd = findViewById(R.id.rl_change_pwd);
        mTvUsername = findViewById(R.id.tvSubLabel);
        blockedListBtn = findViewById(R.id.blocked_list_btn);
        savedArticleBtn = findViewById(R.id.saved_article_btn);
        divider = findViewById(R.id.divider);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onClick(View view) {
        if (view == mIvBack) {
            finish();
//            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (view == mRlChangeEmail) {
//            new LocaleManager().setLocale(this, "en");
            AnalyticsEvents.INSTANCE.logEvent(this,
                    Events.CHANGE_EMAIL);
            Intent emailIntent = new Intent(this, ChangeAccountInfoActivity.class);
            emailIntent.putExtra("type", TYPE_EMAIL);
            startActivity(emailIntent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (view == mRlChangePwd) {
//            new LocaleManager().setLocale(this, "hi");
            AnalyticsEvents.INSTANCE.logEvent(this,
                    Events.CHANGE_PASSWORD);
            Intent emailIntent = new Intent(this, ChangeAccountInfoActivity.class);
            emailIntent.putExtra("type", TYPE_PASSWORD);
            startActivity(emailIntent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if(view == blockedListBtn){
            startActivity(new Intent(AccountActivity.this, BlockActivity.class));
        } else if(view == savedArticleBtn){
            startActivity(new Intent(AccountActivity.this, SavedPostActivity.class));
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Asdasdasdasdsa", "onConfigurationChanged: ");
    }
}
