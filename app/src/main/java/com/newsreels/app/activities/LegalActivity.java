package com.newsreels.app.activities;

import android.os.Bundle;
import android.view.View;

import com.newsreels.app.R;
import com.newsreels.app.utills.Utils;

public class LegalActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_legal);
        setListener();
    }

    private void setListener() {
        findViewById(R.id.ivBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openWebView(LegalActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_and_condition));
            }
        });
        findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openWebView(LegalActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy));
            }
        });
    }
}