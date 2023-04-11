package com.ziro.bullet.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.adapters.SettingsMenuAdapter;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.auth.OAuthAPIClient;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.TYPE;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ziro.bullet.activities.MainActivityNew.RESULT_INTENT_CHANGE_EDITION;

public class AccountSettingsActivity extends BaseActivity {

    private static final String TAG = AccountSettingsActivity.class.getSimpleName();

    //account
    private String MY_ACCOUNT = null;
//    private String POST_ARTICLE = null;
    private String BLOCKLIST = null;
    private String LANGUAGE = null;
    private String EDITION = null;

    //notifications
    private String PUSH_NOTIFICATIONS = null;
    private String EMAIL = null;

    //personalization
    private String THEME = null;
    private String AUTO = null;
    private String DARK = null;
    private String LIGHT = null;
    private String HAPTIC = null;
    private String AUDIO_SETTINGS = null;

    //others
    private String FEEDBACK = null;
    private String HELP = null;
    private String ABOUT = null;
    private String LEGAL = null;
    private String SIGN_OUT = null;

    private RecyclerView mRvSettings;
    private SettingsMenuAdapter mAdapter;

    private RelativeLayout ivBack;
    private CardView mBtnLogout;
    private TextView btn_text;
    private TextView build;
    private TextView headerText;
    //    private TextView button_text;
    private PrefConfig mPrefConfig;
    private RelativeLayout main;
    private String defaultSelectedTheme = "";
    private boolean changes = false;
    private UserConfigPresenter userConfigPresenter;

    private boolean isThemeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        mPrefConfig = new PrefConfig(this);
        bindViews();
        init();
        setTheme();
    }

    private void setTheme() {
        if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.AUTO)) {
            systemMode();
            defaultSelectedTheme = "auto";
        } else if (mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
            darkMode();
            defaultSelectedTheme = "dark";
        } else {
            lightMode();
            defaultSelectedTheme = "light";
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        Log.d(TAG, "onConfigurationChanged: " + nightModeFlags);

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
            applyDayNight("light");
        } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            applyDayNight("night");
        }
    }

    private void applyDayNight(String state) {
        Log.d(TAG, "applyDayNight: " + state);
        findViewById(R.id.main).setBackgroundColor(ContextCompat.getColor(this, R.color.base));
        if (state.equals("light")) {
            //apply day colors for your views
            setLightModePills();
        } else {
            //apply night colors for your views
            setDarkModePills();
        }
    }

    private void init() {

        HAPTIC = getString(R.string.haptics);
        MY_ACCOUNT = getString(R.string.ny_account);
        ABOUT = getString(R.string.about);
        LANGUAGE = getString(R.string.language);
        PUSH_NOTIFICATIONS = getString(R.string.push_notifications);
        LEGAL = getString(R.string.legal);
        HELP = getString(R.string.help_contact_us);
        THEME = getString(R.string.reader_theme);
        BLOCKLIST = getString(R.string.block_list);
        AUTO = getString(R.string.auto2);
        DARK = getString(R.string.dark2);
        LIGHT = getString(R.string.light2);
        FEEDBACK = getString(R.string.feedback);
//        POST_ARTICLE = getString(R.string.post_article);
        SIGN_OUT = getString(R.string.sign_out);
        AUDIO_SETTINGS = getString(R.string.audio_settings);
        EDITION = getString(R.string.edition_region);


        mAdapter = new SettingsMenuAdapter(getSettingsItems(), this);
        mRvSettings.setLayoutManager(new LinearLayoutManager(this));
        mRvSettings.setAdapter(mAdapter);
        mRvSettings.setNestedScrollingEnabled(false);
        mAdapter.setCallback(new SettingsMenuAdapter.SettingsCallback() {
            @Override
            public void onClick(String item) {

                if (item.equalsIgnoreCase(MY_ACCOUNT)) {
                    startActivity(new Intent(AccountSettingsActivity.this, AccountActivity.class));
                } else if (item.equalsIgnoreCase(HELP)) {
                    Intent intentHelp = new Intent(AccountSettingsActivity.this, ContactUsActivity.class);
                    intentHelp.putExtra("title", HELP);
                    startActivity(intentHelp);
                } else if (item.equalsIgnoreCase(ABOUT)) {
                    AnalyticsEvents.INSTANCE.logEvent(AccountSettingsActivity.this,
                            Events.ABOUT_CLICK);
                    Intent intentAbout = new Intent(AccountSettingsActivity.this, AboutUsActivity.class);
                    intentAbout.putExtra("title", ABOUT);
                    startActivity(intentAbout);
                } else if (item.equalsIgnoreCase(AUTO)) {
                    isThemeChanged = true;
                    AnalyticsEvents.INSTANCE.logEvent(AccountSettingsActivity.this,
                            Events.AUTO_MODE);
                    if (defaultSelectedTheme.equalsIgnoreCase("auto")) {
                        changes = false;
                    } else {
                        changes = true;
                    }
                    systemMode();

                } else if (item.equalsIgnoreCase(LIGHT)) {
                    isThemeChanged = true;
                    AnalyticsEvents.INSTANCE.logEvent(AccountSettingsActivity.this,
                            Events.LIGHT_MODE);
                    if (defaultSelectedTheme.equalsIgnoreCase("light")) {
                        changes = false;
                    } else {
                        changes = true;
                    }
                    lightMode();

                } else if (item.equalsIgnoreCase(DARK)) {
                    isThemeChanged = true;
                    AnalyticsEvents.INSTANCE.logEvent(AccountSettingsActivity.this,
                            Events.DARK_MODE);
                    if (defaultSelectedTheme.equalsIgnoreCase("dark")) {
                        changes = false;
                    } else {
                        changes = true;
                    }
                    darkMode();

                } else if (item.equalsIgnoreCase(PUSH_NOTIFICATIONS)) {
                    startActivity(new Intent(AccountSettingsActivity.this, PushNotificationActivity.class));
                } else if (item.equalsIgnoreCase(BLOCKLIST)) {
                    startActivity(new Intent(AccountSettingsActivity.this, BlockActivity.class));
                } else if (item.equalsIgnoreCase(LEGAL)) {
                    startActivity(new Intent(AccountSettingsActivity.this, LegalActivity.class));
                }
//                else if (item.equalsIgnoreCase(ARCHIVE)) {
//                    if (listener != null)
//                        listener.onItemClicked(TYPE.ARCHIVE, "", getString(R.string.saved_articles), false);
//                }
                else if (item.equalsIgnoreCase(FEEDBACK)) {
                    startActivity(new Intent(AccountSettingsActivity.this, SuggestionActivity.class));
                }
//                else if (item.equalsIgnoreCase(POST_ARTICLE)) {
//                    startActivity(new Intent(AccountSettingsActivity.this, PostArticleActivity.class));
//                }
                else if (item.equalsIgnoreCase(SIGN_OUT)) {
                    logoutAPI();
                } else if (item.equalsIgnoreCase(EDITION)) {
                    Intent intent = new Intent(AccountSettingsActivity.this, EditionActivity.class);
                    intent.putExtra("flow", "setting");
                    startActivityForResult(intent, RESULT_INTENT_CHANGE_EDITION);
                } else if (item.equalsIgnoreCase(LANGUAGE)) {
                    Intent intent = new Intent(AccountSettingsActivity.this, LanguageActivity.class);
                    intent.putExtra("flow", "setting");
                    startActivityForResult(intent, MainActivityNew.RESULT_INTENT_CHANGE_LANGUAGE);
                } else if (item.equalsIgnoreCase(AUDIO_SETTINGS)) {
                    Intent intent = new Intent(AccountSettingsActivity.this, AudioSettingsActivity.class);
                    startActivity(intent);
                }
            }
        });

        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
        build.setText(String.format(Locale.getDefault(), "%s %s(%d)", getString(R.string.nib_version), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_INTENT_CHANGE_EDITION){
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isThemeChanged) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else super.onBackPressed();
    }

    private void lightMode() {
        new Components().settingStatusBarColors(this, "white");
        setLightModePills();
        Utils.checkAppModeColor(this, false);
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void setLightModePills() {
        headerText.setTextColor(getResources().getColor(R.color.lightText));
        build.setTextColor(getResources().getColor(R.color.textSmallGrey));
//        button_text.setTextColor(getResources().getColor(R.color.black));
        main.setBackgroundColor(getResources().getColor(R.color.white));
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void darkMode() {
        new Components().settingStatusBarColors(this, "black");
        setDarkModePills();
        Utils.checkAppModeColor(this, false);
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_YES);
    }

    private void setDarkModePills() {
        headerText.setTextColor(getResources().getColor(R.color.darkText));
        build.setTextColor(getResources().getColor(R.color.textSmallGrey));
//        button_text.setTextColor(getResources().getColor(R.color.white));
        main.setBackgroundColor(getResources().getColor(R.color.black));
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void systemMode() {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                new Components().settingStatusBarColors(this, "black");
                setDarkModePills();
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                new Components().settingStatusBarColors(this, "white");
                setLightModePills();
                break;
        }

        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    private void bindViews() {

//        button_text = view.findViewById(R.id.button_text);
        main = findViewById(R.id.main);
        headerText = findViewById(R.id.headerText);
        build = findViewById(R.id.build);
        mRvSettings = findViewById(R.id.rvSettings);
        mBtnLogout = findViewById(R.id.button);
        ivBack = findViewById(R.id.ivBack);
//        btn_text = view.findViewById(R.id.button_text);
//
//        btn_text.setText(this.getString(R.string.sign_out));
//        mBtnLogout.setBackground(getResources().getDrawable(R.drawable.shape_with_stroke));
//
//        mBtnLogout.setOnClickListener(v -> {
//            logoutAPI();
//        });
    }


    public void moveToTop() {
        if (mRvSettings != null) {
            mRvSettings.scrollTo(0, 0);
        }
    }

    public void updateList() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<String> getSettingsItems() {
        ArrayList<String> items = new ArrayList<>();
        if (mPrefConfig.getHasPassword()) {
            items.add(MY_ACCOUNT);
        }
//        items.add(POST_ARTICLE);
        items.add(BLOCKLIST);
        items.add(LANGUAGE);
        items.add(EDITION);
        items.add(PUSH_NOTIFICATIONS);
        items.add(THEME);
        items.add(HAPTIC);
        items.add(AUDIO_SETTINGS);
        items.add(FEEDBACK);
        items.add(HELP);
        items.add(ABOUT);
        items.add(LEGAL);
        items.add(SIGN_OUT);
        return items;
    }

    private void logoutAPI() {
        AnalyticsEvents.INSTANCE.logEvent(AccountSettingsActivity.this,
                Events.LOGOUT_CLICK);
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(this, "" + this.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Call<ResponseBody> call = OAuthAPIClient
                .getInstance()
                .getApi()
                .logout("Bearer " + mPrefConfig.getAccessToken(), mPrefConfig.getRefreshToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: ");
                logout();
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.d(TAG, "onResponse: ");
                logout();
            }
        });
    }

    private void logout() {
        if (!mPrefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK))
            darkMode();

        mPrefConfig.clear();
        mPrefConfig.setFirstTimeLaunch(false);
        try {

//            Thread thread = new Thread(() -> {
//                try {
//                    FirebaseInstallations.getInstance().delete();
//                    FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(task -> {
//                        if (mPrefConfig != null)
//                            mPrefConfig.setFirebaseToken(Objects.requireNonNull(task.getResult()).getToken());
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//            thread.start();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateWidget();
        Intent intent = new Intent(this, FirstActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateWidget() {
        try {
            if (AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CollectionWidget.class)).length > 0) {
                Intent updateWidget = new Intent(this, CollectionWidget.class);
                updateWidget.setAction("update_widget");
                PendingIntent pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);;
                pending.send();
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public interface OnAccountFragmentInteractionListener {
        void onItemClicked(TYPE type, String id, String name, boolean favorite);
    }
}