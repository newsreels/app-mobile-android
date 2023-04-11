package com.ziro.bullet.viewHolder;

import android.app.Activity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ziro.bullet.R;
import com.ziro.bullet.utills.FloatSeekBar;

public class FontSizeViewHolder {

    private TextView header_text;
    private TextView label;
    private TextView desc;
    private TextView bullet1_text;
//    private TextView bullet2_text;
    private TextView bullet3_text;
//    private TextView bullet4_text;
//    private TextView bullet5_text;
//    private TextView bullet6_text;
    private RelativeLayout smallMain;
    private TextView smallText;
    private RelativeLayout defaultMain;
    private TextView defaultText;
    private RelativeLayout mediumMain;
    private TextView mediumText;
    private RelativeLayout largeMain;
    private TextView largeText;
    private TextView footerText;
    private FloatSeekBar seekbar;


    public FontSizeViewHolder(Activity view) {
        header_text = view.findViewById(R.id.header_text);
        label = view.findViewById(R.id.label);
        desc = view.findViewById(R.id.desc);
        bullet1_text = view.findViewById(R.id.bullet1_text);
//        bullet2_text = view.findViewById(R.id.bullet2_text);
        bullet3_text = view.findViewById(R.id.bullet3_text);
//        bullet4_text = view.findViewById(R.id.bullet4_text);
//        bullet5_text = view.findViewById(R.id.bullet5_text);
//        bullet6_text = view.findViewById(R.id.bullet6_text);
        smallMain = view.findViewById(R.id.smallMain);
        smallText = view.findViewById(R.id.smallText);
        defaultMain = view.findViewById(R.id.defaultMain);
        defaultText = view.findViewById(R.id.defaultText);
        mediumMain = view.findViewById(R.id.mediumMain);
        mediumText = view.findViewById(R.id.mediumText);
        largeMain = view.findViewById(R.id.largeMain);
        largeText = view.findViewById(R.id.largeText);
        seekbar = view.findViewById(R.id.seekBarFont);
//        footerText = view.findViewById(R.id.footerText);
    }

    public TextView getHeader_text() {
        return header_text;
    }

    public TextView getLabel() {
        return label;
    }

    public TextView getDesc() {
        return desc;
    }

    public TextView getBullet1_text() {
        return bullet1_text;
    }

//    public TextView getBullet2_text() {
//        return bullet2_text;
//    }

    public TextView getBullet3_text() {
        return bullet3_text;
    }

//    public TextView getBullet4_text() {
//        return bullet4_text;
//    }
//
//    public TextView getBullet5_text() {
//        return bullet5_text;
//    }
//
//    public TextView getBullet6_text() {
//        return bullet6_text;
//    }

    public RelativeLayout getSmallMain() {
        return smallMain;
    }

    public TextView getSmallText() {
        return smallText;
    }

    public RelativeLayout getDefaultMain() {
        return defaultMain;
    }

    public TextView getDefaultText() {
        return defaultText;
    }

    public RelativeLayout getMediumMain() {
        return mediumMain;
    }

    public TextView getMediumText() {
        return mediumText;
    }

    public RelativeLayout getLargeMain() {
        return largeMain;
    }

    public TextView getLargeText() {
        return largeText;
    }

    public TextView getFooterText() {
        return footerText;
    }

    public FloatSeekBar getSeekbar() {
        return seekbar;
    }
}
