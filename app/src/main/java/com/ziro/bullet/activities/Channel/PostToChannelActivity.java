package com.ziro.bullet.activities.Channel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AddTagActivity;
import com.ziro.bullet.adapters.ChannelsAdapter;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.interfaces.UserChannelCallback;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.model.CategorizedChannelsData;
import com.ziro.bullet.model.SelectedChannel;
import com.ziro.bullet.presenter.UserChannelPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class PostToChannelActivity extends AppCompatActivity implements UserChannelCallback {

    private RelativeLayout back;
    private ConstraintLayout my_profile;
    private LinearLayout container;
    private ImageView loader;
    private RelativeLayout progress;
    private RoundedImageView roundedImageView;
    private UserChannelPresenter presenter;
    private PrefConfig mPrefConfig;
    private User user = null;
    private String articleId = null;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_post_to_channel);
        bindView();
        init();
        setListener();
        getUserData();
    }

    private void getUserData() {
        if (mPrefConfig != null) {
            user = mPrefConfig.isUserObject();
            if (user != null && !TextUtils.isEmpty(user.getProfile_image())) {
                Picasso.get().load(user.getProfile_image()).into(roundedImageView);
            }
        }
        if (presenter != null)
            presenter.getCategorizedChannels();

        if (getIntent() != null) {
            articleId = getIntent().getStringExtra(AddTagActivity.ARTICLE_ID_KEY);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setListener() {
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        my_profile.setOnClickListener(v -> {
            if (mPrefConfig != null && mPrefConfig.isUserObject() != null && !TextUtils.isEmpty(mPrefConfig.isUserObject().getId())) {
                selectedChannel("", getResources().getString(R.string.my_profile), mPrefConfig.isUserObject().getProfile_image());
            }
        });
    }

    private void init() {
        mPrefConfig = new PrefConfig(this);
        presenter = new UserChannelPresenter(this, this);
//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme()))
//                .into(loader);
    }

    private void bindView() {
        my_profile = findViewById(R.id.my_profile);
        container = findViewById(R.id.container);
        roundedImageView = findViewById(R.id.roundedImageView);
        back = findViewById(R.id.back);
        progress = findViewById(R.id.progress);
        loader = findViewById(R.id.loader);
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
        Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(ArrayList<Source> channels) {
        Utils.showSnacky(container, getString(R.string.updated_successfully));
    }

    @Override
    public void successData(ArrayList<CategorizedChannelsData> data) {
        if (data != null && data.size() > 0) {
            container.removeAllViews();
            for (CategorizedChannelsData mData : data) {
                if (mData.getChannels() != null && mData.getChannels().size() > 0) {
                    View view = getLayoutInflater().inflate(R.layout.channel_header_view, null);
                    TextView label = view.findViewById(R.id.label);
                    RecyclerView channelList = view.findViewById(R.id.channelList);
                    label.setText(mData.getTitle());
                    channelList.setLayoutManager(new LinearLayoutManager(this));
                    ChannelsAdapter channelAdapter = new ChannelsAdapter(this, mData.getChannels(), Constants.POST_TO_CHANNELS);
                    channelList.setAdapter(channelAdapter);
                    channelAdapter.setCallback(new ChannelsAdapter.ChannelCallback() {
                        @Override
                        public void onItemClicked(Source source) {
                            if (source != null && !TextUtils.isEmpty(source.getId())) {
                                selectedChannel(source.getId(), source.getName(), source.getIcon());
                            }
                        }

                        @Override
                        public void onCreateChannel() {

                        }

                        @Override
                        public void onContactUs() {

                        }
                    });
                    container.addView(view);
                }
            }
        }
    }

    @Override
    public void channelSelected() {
        finish();
    }

    private void selectedChannel(String id, String name, String image) {
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("image", image);
        if (mPrefConfig != null) {
            mPrefConfig.setChannel(new SelectedChannel(id, image, name));
        }
        setResult(RESULT_OK, intent);
        if (presenter != null && !TextUtils.isEmpty(articleId)) {
            presenter.updateChannel(articleId, id);
        } else {
            finish();
        }
    }
}