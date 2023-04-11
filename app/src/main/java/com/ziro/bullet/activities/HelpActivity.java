package com.ziro.bullet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ziro.bullet.R;
import com.ziro.bullet.utills.Utils;

public class HelpActivity extends BaseActivity {
    private ImageView header_back,helpImg;
    private RelativeLayout signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_help);

        header_back = findViewById(R.id.leftArrow);
        helpImg = findViewById(R.id.helpImg);
        signin = findViewById(R.id.signin);

//        Glide.with(this)
//                .load(R.raw.help)
//                .into(helpImg);

        // HEADER BACK CLICK
        header_back.setOnClickListener(view -> {
            onBackPressed();
        });
        signin.setOnClickListener(view -> {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
