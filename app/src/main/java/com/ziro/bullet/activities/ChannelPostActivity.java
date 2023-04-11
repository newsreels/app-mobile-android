package com.ziro.bullet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.ziro.bullet.R;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.fragments.DetailFragment;
import com.ziro.bullet.interfaces.AudioCallback;
import com.ziro.bullet.interfaces.MainInterface;
import com.ziro.bullet.interfaces.GoHome;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.AudioObject;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.presenter.MainPresenter;
import com.ziro.bullet.texttospeech.TextToAudioPlayerHelper;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ChannelPostActivity extends BaseActivity implements GoHome, DetailFragment.OnHomeFragmentInteractionListener {

    private TYPE type;
    private String id;
    private String context;
    private String name;
    private String subtitle;
    private boolean favorite;
    private AudioCallback audioCallback;
    private TextToAudioPlayerHelper textToAudio;
    private MainPresenter mainPresenter;

    private LinearLayout main;
    private LinearLayout ll_no_results;
    private RelativeLayout close;
    private TextView titleTextView;
    private TextView subtitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        findViewById(android.R.id.content).setTransitionName("shared_element_container");
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());

        MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
        materialContainerTransform.addTarget(android.R.id.content);
        materialContainerTransform.setDuration(550L);

        getWindow().setSharedElementEnterTransition(materialContainerTransform);

        MaterialContainerTransform materialContainerTransform1 = new MaterialContainerTransform();
        materialContainerTransform1.addTarget(android.R.id.content);
        materialContainerTransform1.setDuration(550L);

        getWindow().setSharedElementReturnTransition(materialContainerTransform1);

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_channel);
        new Components().statusBarColor(this, "white");

        main = findViewById(R.id.main);
        ll_no_results = findViewById(R.id.ll_no_results);
        close = findViewById(R.id.close);
        titleTextView = findViewById(R.id.title);
        subtitleTextView = findViewById(R.id.subtitle);

        textToAudio = new TextToAudioPlayerHelper(this);
        mainPresenter = new MainPresenter(this, new MainInterface() {
            @Override
            public void loaderShow(boolean flag) {

            }

            @Override
            public void error(String error, int load) {
                ll_no_results.setVisibility(View.VISIBLE);
            }

            @Override
            public void error404(String error) {
                ll_no_results.setVisibility(View.VISIBLE);
// error screen

            }

            @Override
            public void success(ArticleResponse response, int load, int category, String topic) {

            }

            @Override
            public void success(CategoryResponse response) {

            }

            @Override
            public void UserTopicSuccess(ArrayList<Topics> response) {

            }

            @Override
            public void UserInfoSuccess(Info info) {

            }

            @Override
            public void UserSourceSuccess(SourceModel response) {

            }
        });

        if(getIntent() != null){
            type = (TYPE) getIntent().getSerializableExtra("type");
            id = getIntent().getStringExtra("id");
            context = getIntent().getStringExtra("context");
            name = getIntent().getStringExtra("name");
            subtitle = getIntent().getStringExtra("subtitle");
            favorite = getIntent().getBooleanExtra("favorite", false);
            id = getIntent().getStringExtra("id");

            if(type == TYPE.TRENDING){
                main.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
                titleTextView.setText(name);
                subtitleTextView.setText(subtitle);

                close.setOnClickListener(v -> finishAfterTransition());
            }
        }

        Constants.canAudioPlay = true;
        DetailFragment detailFragment = DetailFragment.newInstance(type, id, context, name, favorite);
        Constants.visiblePageHomeDetails = 0;
        Constants.visiblePage = Constants.visiblePageHomeDetails;

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.frameLayout, detailFragment, "6")
                .show(detailFragment)
                .addToBackStack("6")
                .commitAllowingStateLoss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.TYPE_COUNT_API_CALL) {
            if(mainPresenter != null) {
                mainPresenter.articleViewCountUpdate(event.getStringData());
            }
        }
//        else if (event.getType() == MessageEvent.TYPE_SHOW_DETAIL_VIEW) {
//
//            Intent intent = new Intent(ChannelPostActivity.this, BulletDetailActivity.class);
//            intent.putExtra("article", (Article) event.getObjectData());
//            startActivity(intent);
//            // finish();
//        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        EventBus.getDefault().unregister(this);
        finishAfterTransition();
    }

    @Override
    public void home() {

    }

    @Override
    public void sendAudioToTempHome(AudioCallback audioCallback, String fragTag, String status, AudioObject audio) {
        this.audioCallback = audioCallback;
        if (audio != null) {
            Log.e("sendAudioToTemp", "=================HOME===================");
            Log.e("sendAudioToTemp", "fragTag : " + fragTag);
            Log.e("sendAudioToTemp", "speech : " + audio.getText());
            Log.e("sendAudioToTemp", "speech : " + audio.getId());
            Log.e("sendAudioToTemp", "bullet_position : " + audio.getIndex());
            if (Constants.canAudioPlay) {
                if (!Constants.muted) {
                    if (textToAudio != null) {
                        textToAudio.stop();
//                    textToAudio.play(articleId, bullet_position, speech);
                        textToAudio.isPlaying(audio, audioCallback);
                    }
                }
            }
        }
    }

    @Override
    public void scrollUp() {

    }

    @Override
    public void scrollDown() {

    }

    @Override
    public void sendAudioEvent(String event) {
        Log.e("ACTION-", "ACTION : " + event);
        if (textToAudio != null && !TextUtils.isEmpty(event)) {
            switch (event) {
                case "pause":
                    Log.d("audiotest", "sendAudioEvent: pause");
                    textToAudio.pause();
                    break;
                case "resume":
                    Log.d("audiotest", "sendAudioEvent: resume");
                    if (Constants.canAudioPlay) {
                        if (!Constants.muted) {
                            textToAudio.resume();
                        }
                    }
                    break;
                case "stop_destroy":
                    Log.d("audiotest", "sendAudioEvent: stop_destroy");
                    textToAudio.stop();
                    textToAudio.destroy();
                    break;
                case "stop":
                    Log.d("audiotest", "sendAudioEvent: stop");
                    textToAudio.stop();
                    break;
                case "destroy":
                    Log.d("audiotest", "sendAudioEvent: destroy");
                    textToAudio.destroy();
                    break;
                case "mute":
                    Log.d("audiotest", "sendAudioEvent: mute");
                    textToAudio.mute();
                    textToAudio.stop();
                    textToAudio.destroy();
                    break;
                case "unmute":
                    textToAudio.unmute();
                    break;
                case "isSpeaking":
                    Log.d("audiotest", "sendAudioEvent: isSpeaking");
                    if (!textToAudio.isSpeaking()) {
                        if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(Constants.speech)) {
                            sendAudioToTempHome(audioCallback, "isSpeaking", "", new AudioObject(Constants.articleId, Constants.speech, Constants.url, Constants.bullet_position, Constants.bullet_duration));
                        }
                    }
                    break;
                case "play":
                    Log.d("audiotest", "sendAudioEvent: play");
                    textToAudio.stop();
                    if (!TextUtils.isEmpty(Constants.articleId) && Constants.bullet_position != -1 && !TextUtils.isEmpty(Constants.speech)) {
                        sendAudioToTempHome(audioCallback, "play", "", new AudioObject(Constants.articleId, Constants.speech, Constants.url, Constants.bullet_position, Constants.bullet_duration));
                    }
                    break;
            }
        }
    }

    @Override
    public void onItemClicked(TYPE type, String id, String name, boolean favorite) {
        Log.e("@@@", "ITEM CLICKED");

        //Resetting the audio data to avoid old article speech while loading new data
        Constants.articleId = "";
        Constants.speech = "";
        Constants.url = "";
        Log.d("audiotest", " onitemclick : stop_destroy");
        sendAudioEvent("stop_destroy");
        // Utils.hideKeyboard(BulletDetailActivity.this, mRoot);

        if (type != null && type.equals(TYPE.MANAGE)) {
            //selectSearch(name);
        } else if (type != null && id != null && name != null) {
//            setTitle(name, name.equalsIgnoreCase(getString(R.string.profile)) || type == TYPE.TOPIC || type == TYPE.SOURCE || type == TYPE.SOURCE_PUSH || type == TYPE.ARCHIVE || type == TYPE.LOCATION);
            Constants.canAudioPlay = true;

            Intent intent = null;
            if (type.equals(TYPE.SOURCE)) {
                intent = new Intent(ChannelPostActivity.this, ChannelDetailsActivity.class);
            } else {
                intent = new Intent(ChannelPostActivity.this, ChannelPostActivity.class);
            }
            intent.putExtra("type", type);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("favorite", favorite);
            startActivity(intent);
        }
    }
}