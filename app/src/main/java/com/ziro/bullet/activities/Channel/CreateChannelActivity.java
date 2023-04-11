package com.ziro.bullet.activities.Channel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.CreateChannelCallback;
import com.ziro.bullet.presenter.CreateChannelPresenter;
import com.ziro.bullet.utills.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateChannelActivity extends AppCompatActivity implements CreateChannelCallback {

    private TextView channelName;
    private TextView channelName2;
    private TextView channelDesc;
    private TextView button_text;
    private CircleImageView image;
    private CardView button;
    private ImageView loader;
    private RelativeLayout progress;
    private RelativeLayout back;
    private String name;
    private String description;
    private String url;
    private PrefConfig mPrefConfig;
    private CreateChannelPresenter presenter;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("description", description);
        intent.putExtra("url", url);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_create_channel);
        bindView();
        init();
        setData();
        setListeners();
    }

    private void setListeners() {
        back.setOnClickListener(v -> onBackPressed());
        button.setOnClickListener(v -> {
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(CreateChannelActivity.this, getString(R.string.enter_channel_name), Toast.LENGTH_SHORT).show();
                return;
            }
//            if (TextUtils.isEmpty(url)) {
//                Toast.makeText(CreateChannelActivity.this, getString(R.string.upload_media), Toast.LENGTH_SHORT).show();
//                return;
//            }
            if (presenter != null) {
                presenter.createChannel(name, description, url);
            }
        });
    }


    private void init() {
        mPrefConfig = new PrefConfig(this);
        presenter = new CreateChannelPresenter(this, this);
    }

    private void setData() {
        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            description = getIntent().getStringExtra("description");
            url = getIntent().getStringExtra("url");
            if (!TextUtils.isEmpty(name)) {
                channelName.setText(name);
                channelName2.setText(name);
            }
            if (!TextUtils.isEmpty(description)) {
                channelDesc.setText(description);
            }
            if (!TextUtils.isEmpty(url)) {
                Picasso.get()
                        .load(url)
                        .into(image);
            }
        }
//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme()))
//                .into(loader);
        button_text.setText(getResources().getString(R.string.create_channel_btn));
        button.setBackground(getResources().getDrawable(R.drawable.shape));
    }


    private void bindView() {
        channelName = findViewById(R.id.channelName);
        back = findViewById(R.id.back);
        channelName2 = findViewById(R.id.channelName2);
        channelDesc = findViewById(R.id.channelDesc);
        image = findViewById(R.id.image);
        button_text = findViewById(R.id.button_text);
        button = findViewById(R.id.button);
        progress = findViewById(R.id.progress);
        loader = findViewById(R.id.loader);
    }

    @Override
    public void loaderShow(boolean flag) {
        button.setEnabled(!flag);
        back.setEnabled(!flag);
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mediaUploaded(String url, String type, int request) {

    }

    @Override
    public void validName(boolean isValid) {
        if (isValid) {
            Intent intent = new Intent();
            intent.putExtra("finish", true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}