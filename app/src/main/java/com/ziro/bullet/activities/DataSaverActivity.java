package com.ziro.bullet.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.sources.Info;
import com.ziro.bullet.data.models.sources.SourceModel;
import com.ziro.bullet.data.models.topics.Topics;
import com.ziro.bullet.interfaces.MainInterface;
import com.ziro.bullet.model.articles.ArticleResponse;
import com.ziro.bullet.model.Menu.CategoryResponse;
import com.ziro.bullet.presenter.MainPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.util.ArrayList;

public class DataSaverActivity extends BaseActivity implements MainInterface {
    private static final String TAG = "";

    private RelativeLayout mRlBack;
    private LinearLayout off1;
    private LinearLayout off2;
    private LinearLayout on1;
    private LinearLayout on2;
    private TextView on1_text;
    private TextView on2_text;
    private TextView off1_text;
    private TextView off2_text;

    private MainPresenter presenter;
    private PrefConfig prefConfig;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_data_saver);

        bindViews();
        setListeners();

        presenter = new MainPresenter(this, this);
        prefConfig = new PrefConfig(this);


        autoPlayEnabled(prefConfig.isBulletsAutoPlay());
        readerModeEnabled(prefConfig.isReaderMode());
    }

    private void autoPlayEnabled(boolean enabled) {
        if (enabled)
            on1();
        else off1();
    }

    private void readerModeEnabled(boolean enabled) {
        if (enabled)
            on2();
        else off2();
    }

    private void bindViews() {
        mRlBack = findViewById(R.id.ivBack);

        off1 = findViewById(R.id.off1);
        off2 = findViewById(R.id.off2);
        on1 = findViewById(R.id.on1);
        on2 = findViewById(R.id.on2);
        on1_text = findViewById(R.id.on1_text);
        on2_text = findViewById(R.id.on2_text);
        off1_text = findViewById(R.id.off1_text);
        off2_text = findViewById(R.id.off2_text);
    }

    private void setListeners() {
        mRlBack.setOnClickListener(v -> onBackPressed());

        off1.setOnClickListener(v -> {
            if (isLoading)
                return;
            autoPlayEnabled(false);
            if (presenter != null) {
                presenter.updateConfig(prefConfig.getMenuViewMode(), prefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, false, prefConfig.isReaderMode(), prefConfig.isVideoAutoPlay(), prefConfig.isReelsAutoPlay(),
                        flag -> {
                            prefConfig.setBulletsAutoPlay(!prefConfig.isBulletsAutoPlay());
                        });
            }
        });
        on1.setOnClickListener(v -> {
            if (isLoading)
                return;
            autoPlayEnabled(true);
            if (presenter != null) {
                presenter.updateConfig(prefConfig.getMenuViewMode(), prefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, true, prefConfig.isReaderMode(), prefConfig.isVideoAutoPlay(), prefConfig.isReelsAutoPlay(),
                        flag -> {
                            prefConfig.setBulletsAutoPlay(!prefConfig.isBulletsAutoPlay());
                        });
            }
        });
        off2.setOnClickListener(v -> {
            if (isLoading)
                return;
            readerModeEnabled(false);
            if (presenter != null) {
                presenter.updateConfig(prefConfig.getMenuViewMode(), prefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, prefConfig.isBulletsAutoPlay(), false, prefConfig.isVideoAutoPlay(), prefConfig.isReelsAutoPlay(),
                        flag -> {
                            prefConfig.setReaderMode(!prefConfig.isReaderMode());
                        });
            }
        });
        on2.setOnClickListener(v -> {
            if (isLoading)
                return;
            readerModeEnabled(true);
            if (presenter != null) {
                presenter.updateConfig(prefConfig.getMenuViewMode(), prefConfig.getMenuNarration().isMuted(), Constants.HEADLINES_ONLY, Constants.reading_speed, false, prefConfig.isBulletsAutoPlay(), true, prefConfig.isVideoAutoPlay(), prefConfig.isReelsAutoPlay(),
                        flag -> {
                            prefConfig.setReaderMode(!prefConfig.isReaderMode());
                        });
            }
        });
    }

    private void on1() {
        on1.setBackground(getResources().getDrawable(R.drawable.shape));
        on1_text.setTextColor(getResources().getColor(R.color.tab_selected));
        off1.setBackground(null);
        off1_text.setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    private void off1() {
        off1.setBackground(getResources().getDrawable(R.drawable.shape));
        off1_text.setTextColor(getResources().getColor(R.color.tab_selected));
        on1.setBackground(null);
        on1_text.setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    private void on2() {
        on2.setBackground(getResources().getDrawable(R.drawable.shape));
        on2_text.setTextColor(getResources().getColor(R.color.tab_selected));
        off2.setBackground(null);
        off2_text.setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    private void off2() {
        off2.setBackground(getResources().getDrawable(R.drawable.shape));
        off2_text.setTextColor(getResources().getColor(R.color.tab_selected));
        on2.setBackground(null);
        on2_text.setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
    }

    @Override
    public void error(String error, int load) {
        Utils.showSnacky(getWindow().getDecorView().getRootView(), "" + error);
    }

    @Override
    public void error404(String error) {
        Utils.showSnacky(getWindow().getDecorView().getRootView(), "" + error);
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