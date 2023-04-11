package com.ziro.bullet.activities;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.model.TextSizeModel;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.viewHolder.FontSizeViewHolder;

public class FontSizeActivity extends BaseActivity {

    private FontSizeViewHolder viewHolder;
    private PrefConfig prefConfig;
    private TextView defaultText;
    private TextView smallText;
    private TextView mediumText;
    private TextView largeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_size);
        viewHolder = new FontSizeViewHolder(this);
        prefConfig = new PrefConfig(this);
        init();
        listener();
        setData();

        Utils.setStatusBarColor(this);
    }

    private void setData() {
        if (prefConfig.getTextSize() != null)
            viewHolder.getSeekbar().setProgress(prefConfig.getTextSize().getId());
    }

    private void listener() {
        findViewById(R.id.header_back).setOnClickListener(v -> finish());
        viewHolder.getSeekbar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBars, int progress, boolean fromUser) {
                Log.d("Font_TAG", "onProgressChanged: ");
                switch (progress) {
                    case 0:
                        _small();
                        Constants.isFontSizeUpdated = true;
                        break;
                    case 1:
                        _default();
                        Constants.isFontSizeUpdated = true;
                        break;
                    case 2:
                        _medium();
                        Constants.isFontSizeUpdated = true;
                        break;
                    case 3:
                        _large();
                        Constants.isFontSizeUpdated = true;
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void init() {
        defaultText = findViewById(R.id.defaultText);
        smallText = findViewById(R.id.smallText);
        mediumText = findViewById(R.id.mediumText);
        largeText = findViewById(R.id.largeText);
        viewHolder.getSeekbar().setMaxFloat(3);
        viewHolder.getSeekbar().setMinFloat(0);
        viewHolder.getSeekbar().setValue(1);
        viewHolder.getSeekbar().setSeekValueFloat(1);
    }

    private void _default() {
        defaultText.setTextColor(this.getResources().getColor(R.color.theme_color_1));
        smallText.setTextColor(this.getResources().getColor(R.color.black));
        mediumText.setTextColor(this.getResources().getColor(R.color.black));
        largeText.setTextColor(this.getResources().getColor(R.color.black));
        viewHolder.getLabel().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._10sdp));
        viewHolder.getDesc().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._14sdp));
        viewHolder.getBullet1_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));
//        viewHolder.getBullet2_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._11sdp));
        viewHolder.getBullet3_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));
//        viewHolder.getBullet4_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._11sdp));
//        viewHolder.getBullet5_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._11sdp));
//        viewHolder.getBullet6_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._11sdp));
        prefConfig.setTextSize(new Gson().toJson(new TextSizeModel(viewHolder.getDesc().getTextSize(), viewHolder.getBullet1_text().getTextSize(), 1)));
    }

    private void _small() {
        smallText.setTextColor(this.getResources().getColor(R.color.theme_color_1));
        defaultText.setTextColor(this.getResources().getColor(R.color.black));
        mediumText.setTextColor(this.getResources().getColor(R.color.black));
        largeText.setTextColor(this.getResources().getColor(R.color.black));
        viewHolder.getLabel().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._8sdp));
        viewHolder.getDesc().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._12sdp));
        viewHolder.getBullet1_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._9sdp));
//        viewHolder.getBullet2_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._9sdp));
        viewHolder.getBullet3_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._9sdp));
//        viewHolder.getBullet4_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._9sdp));
//        viewHolder.getBullet5_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._9sdp));
//        viewHolder.getBullet6_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._9sdp));
        prefConfig.setTextSize(new Gson().toJson(new TextSizeModel(viewHolder.getDesc().getTextSize(), viewHolder.getBullet1_text().getTextSize(), 0)));
    }

    private void _medium() {
        mediumText.setTextColor(this.getResources().getColor(R.color.theme_color_1));
        smallText.setTextColor(this.getResources().getColor(R.color.black));
        defaultText.setTextColor(this.getResources().getColor(R.color.black));
        largeText.setTextColor(this.getResources().getColor(R.color.black));
        viewHolder.getLabel().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._12sdp));
        viewHolder.getDesc().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._16sdp));
        viewHolder.getBullet1_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._15sdp));
//        viewHolder.getBullet2_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));
        viewHolder.getBullet3_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._15sdp));
//        viewHolder.getBullet4_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));
//        viewHolder.getBullet5_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));
//        viewHolder.getBullet6_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._13sdp));
        prefConfig.setTextSize(new Gson().toJson(new TextSizeModel(viewHolder.getDesc().getTextSize(), viewHolder.getBullet1_text().getTextSize(), 2)));
    }

    private void _large() {
        mediumText.setTextColor(this.getResources().getColor(R.color.black));
        smallText.setTextColor(this.getResources().getColor(R.color.black));
        defaultText.setTextColor(this.getResources().getColor(R.color.black));
        largeText.setTextColor(this.getResources().getColor(R.color.theme_color_1));
        viewHolder.getLabel().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._14sdp));
        viewHolder.getDesc().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._18sdp));
        viewHolder.getBullet1_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._17sdp));
//        viewHolder.getBullet2_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._15sdp));
        viewHolder.getBullet3_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._17sdp));
//        viewHolder.getBullet4_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._15sdp));
//        viewHolder.getBullet5_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._15sdp));
//        viewHolder.getBullet6_text().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen._15sdp));
        prefConfig.setTextSize(new Gson().toJson(new TextSizeModel(viewHolder.getDesc().getTextSize(), viewHolder.getBullet1_text().getTextSize(), 3)));
    }

}