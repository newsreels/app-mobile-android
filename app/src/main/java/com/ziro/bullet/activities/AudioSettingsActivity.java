package com.ziro.bullet.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.Narration;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.interfaces.MainInterface;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.presenter.MainPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.FloatSeekBar;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;


public class AudioSettingsActivity extends BaseActivity implements MainInterface {

    private PrefConfig prefConfig;
    private MainPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_audio_settings);
        init();
    }

    private void init() {
        prefConfig = new PrefConfig(this);
        presenter = new MainPresenter(this, this);

        RadioGroup rgNarratorMode = findViewById(R.id.radio_group_narrator_mode);
        FloatSeekBar seekBar = findViewById(R.id.speed);
        TextView speeder = findViewById(R.id.speeder);
        RadioButton rb_headelines = findViewById(R.id.rb_headelines);
        RadioButton rb_headelines_and_bullets = findViewById(R.id.rb_headelines_and_bullets);

        seekBar.setMaxFloat(4);
        seekBar.setMinFloat(0);
        seekBar.setValue(Constants.reading_speed);
        seekBar.setSeekValueFloat(Constants.reading_speed);

        if (Constants.reading_speed == 0.5f) {
            seekBar.setProgress(0);
        } else if (Constants.reading_speed == 0.75f) {
            seekBar.setProgress(1);
        } else if (Constants.reading_speed == 1.0f) {
            seekBar.setProgress(2);
        } else if (Constants.reading_speed == 1.5f) {
            seekBar.setProgress(3);
        } else {
            seekBar.setProgress(4);
        }


        speeder.setText(Constants.reading_speed + "x");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBars, int progress, boolean fromUser) {
                Log.e("SEEKBAR", "seek val : " + progress);
                double val = 0;
                switch (progress) {
                    case 0:
                        val = 0.5f;
                        AnalyticsEvents.INSTANCE.logEvent(AudioSettingsActivity.this,
                                Events.READING_SPEED_5);
                        break;
                    case 1:
                        val = 0.75f;
                        AnalyticsEvents.INSTANCE.logEvent(AudioSettingsActivity.this,
                                Events.READING_SPEED_75);
                        break;
                    case 2:
                        val = 1.0f;
                        AnalyticsEvents.INSTANCE.logEvent(AudioSettingsActivity.this,
                                Events.READING_SPEED_1);
                        break;
                    case 3:
                        val = 1.5f;
                        AnalyticsEvents.INSTANCE.logEvent(AudioSettingsActivity.this,
                                Events.READING_SPEED_15);
                        break;
                    case 4:
                        val = 2.0f;
                        AnalyticsEvents.INSTANCE.logEvent(AudioSettingsActivity.this,
                                Events.READING_SPEED_2);
                        break;
                }
                speeder.setText(val + "x");
                Constants.reading_speed = (float) val;

                presenter.updateConfig(prefConfig.getMenuViewMode(), prefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, prefConfig.isBulletsAutoPlay(), prefConfig.isReaderMode(), prefConfig.isVideoAutoPlay(), prefConfig.isReelsAutoPlay(),
                        flag -> {
                        });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (prefConfig.getMenuNarration() != null && !TextUtils.isEmpty(prefConfig.getMenuNarration().getMode())) {
            if (prefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_AND_BULLETS)) {
                rb_headelines.setChecked(false);
                rb_headelines_and_bullets.setChecked(true);
            } else if (prefConfig.getMenuNarration().getMode().equalsIgnoreCase(Constants.HEADLINES_ONLY)) {
                rb_headelines.setChecked(true);
                rb_headelines_and_bullets.setChecked(false);
            }
        }

        rgNarratorMode.setOnCheckedChangeListener((RadioGroup.OnCheckedChangeListener) (group, checkedId) -> {
            RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);

            if (checkedRadioButton == rb_headelines) {
                onNarratorMode(0);
            } else {
                onNarratorMode(1);
            }
        });

        findViewById(R.id.ivBack).setOnClickListener(v -> onBackPressed());
    }


    private void onNarratorMode(int mode) {
//        Utils.broadcastIntent(this, "stop_destroy", INTENT_ACTION_AUDIO);
        switch (mode) {
            case 0: //HEADLINES ONLY
                AnalyticsEvents.INSTANCE.logEvent(AudioSettingsActivity.this,
                        Events.HEADLINES_ONLY);
                presenter.updateConfig(prefConfig.getMenuViewMode(), prefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, prefConfig.isBulletsAutoPlay(), prefConfig.isReaderMode(), prefConfig.isVideoAutoPlay(), prefConfig.isReelsAutoPlay(),
                        flag -> {
                            if (flag) {
                                Narration narration = prefConfig.getMenuNarration();
                                narration.setMode(Constants.HEADLINES_ONLY);
                                prefConfig.setMenuNarration(narration);
//                                if (categoriesAdapter != null) {
//                                    categoriesAdapter.isHeadlineOnly(true);
//                                }
                            }
                        });
                break;
            case 1: //HEADLINE WITH BULLETS
                AnalyticsEvents.INSTANCE.logEvent(AudioSettingsActivity.this,
                        Events.HEADLINES_BULLETS);
                presenter.updateConfig(prefConfig.getMenuViewMode(), prefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_AND_BULLETS, Constants.reading_speed, false, prefConfig.isBulletsAutoPlay(), prefConfig.isReaderMode(), prefConfig.isVideoAutoPlay(), prefConfig.isReelsAutoPlay(),
                        flag -> {
                            if (flag) {
                                Narration narration = prefConfig.getMenuNarration();
                                narration.setMode(Constants.HEADLINES_AND_BULLETS);
                                prefConfig.setMenuNarration(narration);
//                                if (categoriesAdapter != null) {
//                                    categoriesAdapter.isHeadlineOnly(false);
//                                }
                            }
                        });
                break;
        }
    }

    @Override
    public void loaderShow(boolean flag) {

    }

    @Override
    public void error(String error, int load) {

    }

    @Override
    public void error404(String error) {

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
}