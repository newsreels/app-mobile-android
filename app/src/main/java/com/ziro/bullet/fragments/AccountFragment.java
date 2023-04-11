package com.ziro.bullet.fragments;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.AboutUsActivity;
import com.ziro.bullet.activities.AccountActivity;
import com.ziro.bullet.activities.AudioSettingsActivity;
import com.ziro.bullet.activities.BlockActivity;
import com.ziro.bullet.activities.ContactUsActivity;
import com.ziro.bullet.activities.EditionActivity;
import com.ziro.bullet.activities.LanguageActivity;
import com.ziro.bullet.activities.LegalActivity;
import com.ziro.bullet.activities.MainActivityNew;
import com.ziro.bullet.activities.PushNotificationActivity;
import com.ziro.bullet.activities.SplashActivity;
import com.ziro.bullet.activities.SuggestionActivity;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    public static final String TAG = AccountFragment.class.getSimpleName();
    private static final int RESULT_INTENT_SETTING_CHANGES = 3211;

    //account
    private String MY_ACCOUNT = null;
//    private String POST_ARTICLE = null;
    private String ARCHIVE = null;
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
    private OnAccountFragmentInteractionListener listener;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Utils.checkAppModeColor(getActivity(), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mPrefConfig = new PrefConfig(getContext());
        bindViews(view);
        init();
        setTheme();
        return view;
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

    private void init() {

        HAPTIC = getString(R.string.haptics);
        MY_ACCOUNT = getString(R.string.ny_account);
        ABOUT = getString(R.string.about);
        LANGUAGE = getString(R.string.language);
        EDITION = getString(R.string.edition_region);
        PUSH_NOTIFICATIONS = getString(R.string.push_notifications);
        LEGAL = getString(R.string.legal);
        HELP = getString(R.string.help_contact_us);
        THEME = getString(R.string.reader_theme);
        BLOCKLIST = getString(R.string.block_list);
        ARCHIVE = getString(R.string.saved_articles);
        AUTO = getString(R.string.auto);
        DARK = getString(R.string.dark);
        LIGHT = getString(R.string.light);
        FEEDBACK = getString(R.string.feedback);
//        POST_ARTICLE = getString(R.string.post_article);
        SIGN_OUT = getString(R.string.sign_out);
        AUDIO_SETTINGS = getString(R.string.audio_settings);

        mAdapter = new SettingsMenuAdapter(getSettingsItems(), getContext());
        mRvSettings.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvSettings.setAdapter(mAdapter);
        mRvSettings.setNestedScrollingEnabled(false);
        mAdapter.setCallback(new SettingsMenuAdapter.SettingsCallback() {
            @Override
            public void onClick(String item) {

                if (item.equalsIgnoreCase(MY_ACCOUNT)) {
                    AccountFragment.this.startActivity(new Intent(AccountFragment.this.getActivity(), AccountActivity.class));
                } else if (item.equalsIgnoreCase(HELP)) {
                    Intent intentHelp = new Intent(AccountFragment.this.getActivity(), ContactUsActivity.class);
                    intentHelp.putExtra("title", HELP);
                    AccountFragment.this.startActivity(intentHelp);
                } else if (item.equalsIgnoreCase(ABOUT)) {
                    AnalyticsEvents.INSTANCE.logEvent(getContext(),
                            Events.ABOUT_CLICK);
                    Intent intentAbout = new Intent(AccountFragment.this.getActivity(), AboutUsActivity.class);
                    intentAbout.putExtra("title", ABOUT);
                    AccountFragment.this.startActivity(intentAbout);
                } else if (item.equalsIgnoreCase(AUTO)) {
                    if (defaultSelectedTheme.equalsIgnoreCase("auto")) {
                        changes = false;
                    } else {
                        changes = true;
                    }
                    AccountFragment.this.systemMode();

                } else if (item.equalsIgnoreCase(LIGHT)) {
                    if (defaultSelectedTheme.equalsIgnoreCase("light")) {
                        changes = false;
                    } else {
                        changes = true;
                    }
                    AccountFragment.this.lightMode();

                } else if (item.equalsIgnoreCase(DARK)) {
                    if (defaultSelectedTheme.equalsIgnoreCase("dark")) {
                        changes = false;
                    } else {
                        changes = true;
                    }
                    AccountFragment.this.darkMode();

                } else if (item.equalsIgnoreCase(PUSH_NOTIFICATIONS)) {
                    AccountFragment.this.startActivity(new Intent(AccountFragment.this.getActivity(), PushNotificationActivity.class));
                } else if (item.equalsIgnoreCase(BLOCKLIST)) {
                    AccountFragment.this.startActivity(new Intent(AccountFragment.this.getActivity(), BlockActivity.class));
                } else if (item.equalsIgnoreCase(LEGAL)) {
                    AccountFragment.this.startActivity(new Intent(AccountFragment.this.getActivity(), LegalActivity.class));
                } else if (item.equalsIgnoreCase(ARCHIVE)) {
                    if (listener != null)
                        listener.onItemClicked(TYPE.ARCHIVE, "", getString(R.string.saved_articles), false);
                } else if (item.equalsIgnoreCase(FEEDBACK)) {
                    AccountFragment.this.startActivity(new Intent(AccountFragment.this.getActivity(), SuggestionActivity.class));
                }
//                else if (item.equalsIgnoreCase(POST_ARTICLE)) {
//                    AccountFragment.this.startActivity(new Intent(AccountFragment.this.getActivity(), PostArticleActivity.class));
//                }
                else if (item.equalsIgnoreCase(SIGN_OUT)) {
                    AccountFragment.this.logoutAPI();
                } else if (item.equalsIgnoreCase(LANGUAGE)) {
                    Intent intent = new Intent(AccountFragment.this.getActivity(), LanguageActivity.class);
                    intent.putExtra("flow", "setting");
                    getActivity().startActivityForResult(intent, MainActivityNew.RESULT_INTENT_CHANGE_LANGUAGE);
                } else if (item.equalsIgnoreCase(EDITION)) {
                    Intent intent = new Intent(AccountFragment.this.getActivity(), EditionActivity.class);
                    intent.putExtra("flow", "setting");
                    getActivity().startActivityForResult(intent, MainActivityNew.RESULT_INTENT_CHANGE_EDITION);
                } else if (item.equalsIgnoreCase(AUDIO_SETTINGS)) {
                    Intent intent = new Intent(AccountFragment.this.getActivity(), AudioSettingsActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
        build.setText(String.format(Locale.getDefault(), "%s %s(%d)", getActivity().getString(R.string.nib_version), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));

    }

    private void lightMode() {
        new Components().settingStatusBarColors(getActivity(), "white");
        setLightModePills();
        Utils.checkAppModeColor(getActivity(), false);
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
        new Components().settingStatusBarColors(getActivity(), "black");
        setDarkModePills();
        Utils.checkAppModeColor(getActivity(), false);
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
                new Components().settingStatusBarColors(getActivity(), "black");
                setDarkModePills();
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                new Components().settingStatusBarColors(getActivity(), "white");
                setLightModePills();
                break;
        }

        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    private void bindViews(View view) {

//        button_text = view.findViewById(R.id.button_text);
        main = view.findViewById(R.id.main);
        headerText = view.findViewById(R.id.headerText);
        build = view.findViewById(R.id.build);
        mRvSettings = view.findViewById(R.id.rvSettings);
        mBtnLogout = view.findViewById(R.id.button);
//        btn_text = view.findViewById(R.id.button_text);
//
//        btn_text.setText(getContext().getString(R.string.sign_out));
//        mBtnLogout.setBackground(getResources().getDrawable(R.drawable.shape_with_stroke));
//
//        mBtnLogout.setOnClickListener(v -> {
//            logoutAPI();
//        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAccountFragmentInteractionListener) {
            listener = (OnAccountFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAccountFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        listener = null;
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
        items.add(ARCHIVE);
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
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(getActivity(), "" + getContext().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateWidget();
        Intent intent = new Intent(getContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().finish();
        startActivity(intent);
    }

    private void updateWidget() {
        try {
            if (AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), CollectionWidget.class)).length > 0) {
                Intent updateWidget = new Intent(getContext(), CollectionWidget.class);
                updateWidget.setAction("update_widget");
                PendingIntent pending =PendingIntent.getBroadcast(getContext(), 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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