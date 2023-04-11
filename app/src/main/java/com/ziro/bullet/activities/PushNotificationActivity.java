package com.ziro.bullet.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.app.NotificationManagerCompat;

import com.ziro.bullet.R;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.push.Push;
import com.ziro.bullet.interfaces.PushNotificationInterface;
import com.ziro.bullet.presenter.PushNotificationPresenter;
import com.ziro.bullet.utills.Utils;

import java.util.Arrays;
import java.util.List;

public class PushNotificationActivity extends BaseActivity implements PushNotificationInterface {

    private static final String FREQUENCY_30M = "30m";
    private static final String FREQUENCY_1H = "1h";
    private static final String FREQUENCY_3H = "3h";
    private static final String FREQUENCY_6H = "6h";
    private static final String FREQUENCY_12H = "12h";
    private static final String FREQUENCY_24H = "24h";
    private final String START_TIME = "start_time";
    private final String END_TIME = "end_time";

    private LinearLayout off1;
    private LinearLayout off2;
    private LinearLayout on1;
    private LinearLayout on2;
    private Switch switch_breakingnews;
    private Switch switch_personalized;
    private LinearLayout llStartTime, llEndTime;
    private TextView tv30m, tv1h, tv3h, tv6h, tv12h, tv24h;
    private LinearLayout ll30m, ll1h, ll3h, ll6h, ll12h, ll24h;
    private PushNotificationPresenter presenter;
    private boolean breaking = true;
    private boolean personalized = false;
    private String mFreq = FREQUENCY_1H;
    private boolean isLoading = false;
    private LinearLayout content;
    private ImageView back_img;
    private RelativeLayout progress;

    private PrefConfig mPrefconfig;
    private RelativeLayout overlay;

    private AlertDialog selectNotificationTimeDialog;
    private View selectTimeView;
    private TimePicker timePicker;
    private TextView tvStartHour, tvStartMin, tvEndHour, tvEndMin, tvSelectTime;
    private String click, startHour, startMin, endHour, endMin, startTime, endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_push_notification);
        mPrefconfig = new PrefConfig(this);
        presenter = new PushNotificationPresenter(this, this);
        bindView();
        initDialog();
        setlistener();

        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            overlay.setVisibility(View.GONE);
            loadFromDb();
            if (presenter != null) {
                presenter.pushConfig();
            }
        } else {
            overlay.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
            switch_breakingnews.setChecked(false);
            switch_personalized.setChecked(false);
        }


        switch_breakingnews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", "" + isChecked);
                switch_breakingnews.setChecked(isChecked);
                if (presenter != null) {
                    breaking = isChecked;
                    AnalyticsEvents.INSTANCE.logEvent(PushNotificationActivity.this, Events.NOTIFICATION_SETTINGS_BREAKINGON);
                    presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
                }
            }

        });

        switch_personalized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", "" + isChecked);

                switch_personalized.setChecked(isChecked);
                if (presenter != null) {
                    personalized = isChecked;
                    AnalyticsEvents.INSTANCE.logEvent(PushNotificationActivity.this, Events.NOTIFICATION_SETTINGS_BREAKINGON);
                    presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
                }
//                mPrefConfig?.isVideoAutoPlay = flag
//                breaking = pushSaved.getBreaking();
            }

        });
        overlay.setOnClickListener(v -> Utils.showDialog(PushNotificationActivity.this, getString(R.string.turn_on_notifications), getString(R.string.open_settings), flag -> {
            finish();
            if (flag) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent settingsIntent = null;
                    settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(settingsIntent);
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        }));
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        selectTimeView = LayoutInflater.from(this).inflate(R.layout.notification_time_picker, null, false);
        timePicker = selectTimeView.findViewById(R.id.time_picker);
        tvSelectTime = selectTimeView.findViewById(R.id.tv_select_cur_time);
        builder.setCancelable(true);
        builder.setView(selectTimeView);
        selectNotificationTimeDialog = builder.create();
        selectNotificationTimeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectNotificationTimeDialog.setCanceledOnTouchOutside(true);
        timePicker.setIs24HourView(true);
    }

    private void loadFromDb() {
        Push pushSaved = mPrefconfig.getNotificationPref();
        if (pushSaved != null) {
            breaking = pushSaved.getBreaking();
            personalized = pushSaved.getPersonalized();
            mFreq = pushSaved.getFrequency();
            startTime = pushSaved.getStartTime();
            startTime = pushSaved.getEndTime();
            if ((startTime != null && !startTime.isEmpty()) && (endTime != null && !endTime.isEmpty())) {
                String[] startTimeVals = startTime.split(":");
                String[] endTimeVals = endTime.split(":");
                if ((startTimeVals != null && startTimeVals.length > 1) && (endTimeVals != null && endTimeVals.length > 1)) {
                    startHour = startTimeVals[0];
                    startMin = startTimeVals[1];
                    endHour = endTimeVals[0];
                    endMin = endTimeVals[1];
                    tvStartHour.setText(startHour);
                    tvStartMin.setText(startMin);
                    tvEndHour.setText(endHour);
                    tvEndMin.setText(endMin);
                } else {
                    startHour = "00";
                    startMin = "00";
                    endHour = "23";
                    endMin = "59";
                    tvStartHour.setText(startHour);
                    tvStartMin.setText(startMin);
                    tvEndHour.setText(endHour);
                    tvEndMin.setText(endMin);
                    startTime = startHour + ":" + startMin;
                    endTime = endHour + ":" + endMin;
                }
            } else {
                startHour = "00";
                startMin = "00";
                endHour = "23";
                endMin = "59";
                tvStartHour.setText(startHour);
                tvStartMin.setText(startMin);
                tvEndHour.setText(endHour);
                tvEndMin.setText(endMin);
                startTime = startHour + ":" + startMin;
                endTime = endHour + ":" + endMin;
            }

            setNotificationFrequency(mFreq);

            if (breaking) {
                switch_breakingnews.setChecked(true);
            } else {
                switch_breakingnews.setChecked(false);
            }

            if (personalized) {
                switch_personalized.setChecked(true);
            } else {
                switch_personalized.setChecked(false);
            }
        }
    }

    private void bindView() {
        content = findViewById(R.id.content);
        back_img = findViewById(R.id.leftArrow);
        overlay = findViewById(R.id.overlay);
        progress = findViewById(R.id.progress);
        off1 = findViewById(R.id.off1);
        off2 = findViewById(R.id.off2);
        on1 = findViewById(R.id.on1);
        on2 = findViewById(R.id.on2);
        switch_breakingnews = findViewById(R.id.sw_play);
        switch_personalized = findViewById(R.id.switch_personalized);
        llStartTime = findViewById(R.id.ll_start_time);
        llEndTime = findViewById(R.id.ll_end_time);
        tvStartHour = findViewById(R.id.tv_start_hours);
        tvStartMin = findViewById(R.id.tv_start_mins);
        tvEndHour = findViewById(R.id.tv_end_hours);
        tvEndMin = findViewById(R.id.tv_end_mins);

        tv30m = findViewById(R.id.tv_30m);
        tv1h = findViewById(R.id.tv_1h);
        tv3h = findViewById(R.id.tv_3h);
        tv6h = findViewById(R.id.tv_6h);
        tv12h = findViewById(R.id.tv_12h);
        tv24h = findViewById(R.id.tv_24h);

        ll30m = findViewById(R.id.ll_30m);
        ll1h = findViewById(R.id.ll_1h);
        ll3h = findViewById(R.id.ll_3h);
        ll6h = findViewById(R.id.ll_6h);
        ll12h = findViewById(R.id.ll_12h);
        ll24h = findViewById(R.id.ll_24h);
//        ImageView gif = findViewById(R.id.loader);
//        Glide.with(this).load(Utils.getLoaderForTheme(mPrefconfig.getAppTheme())).into(gif);
    }

    private void setlistener() {
        back_img.setOnClickListener(v -> onBackPressed());

        llStartTime.setOnClickListener(v -> {
            click = START_TIME;
            tvSelectTime.setText(this.getResources().getString(R.string.confirm_start_time));
            try {
                if (startHour != null && !startHour.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.setHour(Integer.parseInt(startHour));
                    } else {
                        timePicker.setCurrentHour(Integer.valueOf(startHour));
                    }
                }
                if (startMin != null && !startMin.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.setMinute(Integer.parseInt(startMin));
                    } else {
                        timePicker.setCurrentMinute(Integer.valueOf(startMin));
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setHour(0);
                } else {
                    timePicker.setCurrentHour(0);
                }
            }
            selectNotificationTimeDialog.show();
        });

        llEndTime.setOnClickListener(v -> {
            click = END_TIME;
            tvSelectTime.setText(this.getResources().getString(R.string.confirm_end_time));
            try {
                if (endHour != null && !endHour.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.setHour(Integer.parseInt(endHour));
                    } else {
                        timePicker.setCurrentHour(Integer.valueOf(endHour));
                    }
                }
                if (endMin != null && !endMin.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.setMinute(Integer.parseInt(endMin));
                    } else {
                        timePicker.setCurrentMinute(Integer.valueOf(endMin));
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setHour(0);
                } else {
                    timePicker.setCurrentHour(0);
                }
            }
            selectNotificationTimeDialog.show();
        });

        tvSelectTime.setOnClickListener(v -> {
            if (click.equals(START_TIME)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int hour = timePicker.getHour();
                    int min = timePicker.getMinute();
                    if (hour < 10)
                        startHour = "0" + timePicker.getHour();
                    else
                        startHour = String.valueOf(timePicker.getHour());
                    if (min < 10)
                        startMin = "0" + timePicker.getMinute();
                    else
                        startMin = String.valueOf(timePicker.getMinute());
                } else {
                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();
                    if (hour < 10)
                        startHour = "0" + timePicker.getCurrentHour();
                    else
                        startHour = String.valueOf(timePicker.getCurrentHour());
                    if (min < 10)
                        startMin = "0" + timePicker.getCurrentMinute();
                    else
                        startMin = String.valueOf(timePicker.getCurrentMinute());
                }
                tvStartHour.setText(startHour);
                tvStartMin.setText(startMin);
                startTime = startHour + ":" + startMin;
            } else if (click.equals(END_TIME)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int hour = timePicker.getHour();
                    int min = timePicker.getMinute();
                    if (hour < 10)
                        endHour = "0" + timePicker.getHour();
                    else
                        endHour = String.valueOf(timePicker.getHour());
                    if (min < 10)
                        endMin = "0" + timePicker.getMinute();
                    else
                        endMin = String.valueOf(timePicker.getMinute());
                } else {
                    int hour = timePicker.getCurrentHour();
                    int min = timePicker.getCurrentMinute();
                    if (hour < 10)
                        endHour = "0" + timePicker.getCurrentHour();
                    else
                        endHour = String.valueOf(timePicker.getCurrentHour());
                    if (min < 10)
                        endMin = "0" + timePicker.getCurrentMinute();
                    else
                        endMin = String.valueOf(timePicker.getCurrentMinute());
                }
                tvEndHour.setText(endHour);
                tvEndMin.setText(endMin);
                endTime = endHour + ":" + endMin;
            }
            selectNotificationTimeDialog.dismiss();
            presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
        });

        ll30m.setOnClickListener(v -> {
            if (isLoading) return;
            mFreq = FREQUENCY_30M;
            AnalyticsEvents.INSTANCE.logEvent(PushNotificationActivity.this, Events.NOTIFICATION_SETTINGS_30M);
            resetBackground(Arrays.asList(ll30m, ll1h, ll12h, ll3h, ll6h, ll12h, ll24h), Arrays.asList(tv30m, tv1h, tv12h, tv3h, tv6h, tv12h, tv24h));
            presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
            setONState(ll30m, tv30m);
        });
        ll1h.setOnClickListener(v -> {
            if (isLoading) return;
            mFreq = FREQUENCY_1H;
            AnalyticsEvents.INSTANCE.logEvent(PushNotificationActivity.this, Events.NOTIFICATION_SETTINGS_1H);
            resetBackground(Arrays.asList(ll30m, ll1h, ll12h, ll3h, ll6h, ll12h, ll24h), Arrays.asList(tv30m, tv1h, tv12h, tv3h, tv6h, tv12h, tv24h));
            presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
            setONState(ll1h, tv1h);
        });
        ll3h.setOnClickListener(v -> {
            if (isLoading) return;
            mFreq = FREQUENCY_3H;
            AnalyticsEvents.INSTANCE.logEvent(PushNotificationActivity.this, Events.NOTIFICATION_SETTINGS_3H);
            resetBackground(Arrays.asList(ll30m, ll1h, ll12h, ll3h, ll6h, ll12h, ll24h), Arrays.asList(tv30m, tv1h, tv12h, tv3h, tv6h, tv12h, tv24h));
            presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
            setONState(ll3h, tv3h);
        });
        ll6h.setOnClickListener(v -> {
            if (isLoading) return;
            mFreq = FREQUENCY_6H;
            AnalyticsEvents.INSTANCE.logEvent(PushNotificationActivity.this, Events.NOTIFICATION_SETTINGS_6H);
            resetBackground(Arrays.asList(ll30m, ll1h, ll12h, ll3h, ll6h, ll12h, ll24h), Arrays.asList(tv30m, tv1h, tv12h, tv3h, tv6h, tv12h, tv24h));
            presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
            setONState(ll6h, tv6h);
        });
        ll12h.setOnClickListener(v -> {
            if (isLoading) return;
            mFreq = FREQUENCY_12H;
            AnalyticsEvents.INSTANCE.logEvent(PushNotificationActivity.this, Events.NOTIFICATION_SETTINGS_12H);
            resetBackground(Arrays.asList(ll30m, ll1h, ll12h, ll3h, ll6h, ll12h, ll24h), Arrays.asList(tv30m, tv1h, tv12h, tv3h, tv6h, tv12h, tv24h));
            presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
            setONState(ll12h, tv12h);
        });
        ll24h.setOnClickListener(v -> {
            if (isLoading) return;
            mFreq = FREQUENCY_24H;
            AnalyticsEvents.INSTANCE.logEvent(PushNotificationActivity.this, Events.NOTIFICATION_SETTINGS_24H);
            resetBackground(Arrays.asList(ll30m, ll1h, ll12h, ll3h, ll6h, ll12h, ll24h), Arrays.asList(tv30m, tv1h, tv12h, tv3h, tv6h, tv12h, tv24h));
            presenter.pushConfig(breaking, personalized, mFreq, startTime, endTime);
            setONState(ll24h, tv24h);
        });
    }


    private void setONState(LinearLayout layout, TextView textView) {
//        layout.setBackground(getResources().getDrawable(R.drawable.shape));
        textView.setTextColor(getResources().getColor(R.color.theme_color_1));
//        off1.setBackground(null);
//        off1_text.setTextColor(getResources().getColor(R.color.tab_unselected));
    }

    private void resetBackground(List<LinearLayout> linearLayouts, List<TextView> textViews) {
        for (LinearLayout layout : linearLayouts) {
            layout.setBackground(null);
        }

        for (TextView textView : textViews) {
            textView.setTextColor(getResources().getColor(R.color.tab_unselected));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void loaderShow(boolean flag) {
        isLoading = flag;
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void error(String error) {
        Utils.showPopupMessageWithCloseButton(this, 2000, error, true);
    }

    @Override
    public void error404(String error) {
        Utils.showPopupMessageWithCloseButton(this, 2000, error, true);
    }

    @Override
    public void success(Push model) {
        this.breaking = model.getBreaking();
        this.personalized = model.getPersonalized();
        this.mFreq = model.getFrequency();
        this.startTime = model.getStartTime();
        this.endTime = model.getEndTime();
        if ((startTime != null && !startTime.isEmpty()) && (endTime != null && !endTime.isEmpty())) {
            String[] startTimeVals = startTime.split(":");
            String[] endTimeVals = endTime.split(":");
            if ((startTimeVals != null && startTimeVals.length > 1) && (endTimeVals != null && endTimeVals.length > 1)) {
                startHour = startTimeVals[0];
                startMin = startTimeVals[1];
                endHour = endTimeVals[0];
                endMin = endTimeVals[1];
                tvStartHour.setText(startHour);
                tvStartMin.setText(startMin);
                tvEndHour.setText(endHour);
                tvEndMin.setText(endMin);
            } else {
                startHour = "00";
                startMin = "00";
                endHour = "23";
                endMin = "59";
                tvStartHour.setText(startHour);
                tvStartMin.setText(startMin);
                tvEndHour.setText(endHour);
                tvEndMin.setText(endMin);
                startTime = startHour + ":" + startMin;
                endTime = endHour + ":" + endMin;
            }
        } else {
            startHour = "00";
            startMin = "00";
            endHour = "23";
            endMin = "59";
            tvStartHour.setText(startHour);
            tvStartMin.setText(startMin);
            tvEndHour.setText(endHour);
            tvEndMin.setText(endMin);
            startTime = startHour + ":" + startMin;
            endTime = endHour + ":" + endMin;
        }
        setNotificationFrequency(model.getFrequency());

        if (breaking) {
            switch_breakingnews.setChecked(true);
//            on1();
        } else {
            switch_breakingnews.setChecked(false);
//            off1();
        }

        if (personalized) {
//            on2();
            switch_personalized.setChecked(true);
        } else {
            switch_personalized.setChecked(false);
//            off2();
        }
    }

    @Override
    public void SuccessFirst(boolean flag) {

    }


    private void setNotificationFrequency(String tme) {
        resetBackground(Arrays.asList(ll30m, ll1h, ll12h, ll3h, ll6h, ll12h, ll24h), Arrays.asList(tv30m, tv1h, tv12h, tv3h, tv6h, tv12h, tv24h));
        switch (tme) {
            case "30m":
                setONState(ll30m, tv30m);
                break;
            case "1h":
                setONState(ll1h, tv1h);
                break;
            case "3h":
                setONState(ll3h, tv3h);
                break;
            case "6h":
                setONState(ll6h, tv6h);
                break;
            case "12h":
                setONState(ll12h, tv12h);
                break;
            case "24h":
                setONState(ll24h, tv24h);
                break;
        }
    }
}