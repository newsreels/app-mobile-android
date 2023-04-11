package com.ziro.bullet.data;

import static com.ziro.bullet.utills.Constants.MENU_EXTENDED_VIEW;
import static com.ziro.bullet.utills.Constants.REELS_FOR_YOU;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ziro.bullet.R;
import com.ziro.bullet.data.models.config.Ads;
import com.ziro.bullet.data.models.config.Narration;
import com.ziro.bullet.data.models.config.Static;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.data.models.location.Location;
import com.ziro.bullet.data.models.push.Push;
import com.ziro.bullet.data.models.search.Registration;
import com.ziro.bullet.data.models.sources.Source;
import com.ziro.bullet.data.models.userInfo.User;
import com.ziro.bullet.model.Edition.EditionsItem;
import com.ziro.bullet.model.SelectedChannel;
import com.ziro.bullet.model.TextSizeModel;
import com.ziro.bullet.model.language.LanguagesItem;
import com.ziro.bullet.utills.Constants;

import java.util.ArrayList;

/**
 * Created by shine_joseph on 27/05/20.
 */
public class PrefConfig {
    private static final String PREF_USER_CONFIG = "pref_user_config";
    private static final String PREF_FIREBASE_TOKEN = "pref_firebase_token";
    private static final String PREF_ACCESS_TOKEN = "pref_access_token_new";
    private static final String PREF_REFRESH_TOKEN = "pref_refresh_token";
    private static final String PREF_USERNAME = "pref_username";
    private static final String PREF_USERNAME_NEW = "pref_username_new";
    private static final String PREF_hasPassword = "pref_hasPassword";
    private static final String PREF_PROFILE = "pref_profile";
    private static final String PREF_EMAIL = "pref_email";
    private static final String PREF_LOGIN_STATE = "pref_state";
    private static final String PREF_APP_THEME = "pref_theme";
    private static final String PREF_WIDGET_DATA = "pref_widget_data";
    private static final String PREF_WIDGET_INDEX = "pref_widget_index";
    private static final String PREF_MENU_VIEW_MODE = "pref_menu_view_mode";
    private static final String PREF_MENU_NARRATION_DATA = "pref_menu_narration_data";
    private static final String PREF_HOME_IMAGES = "pref_home_images";
    private static final String PREF_FIRST_TIME = "pref_first_time";
    private static final String PREF_REGISTRATION = "pref_registration";
    private static final String PREF_EDITIONS = "pref_editions";
    private static final String PREF_LOGIN_TIME = "pref_login_time";
    private static final String PREF_REGION = "pref_region";
    private static final String PREF_PRIMARY_LANG = "pref_primary_lang";
    private static final String PREF_SECONDARY_LANG = "pref_secondary_lang";
    private static final String PREF_LOCALE = "pref_locale";
    private static final String PREF_DEFAULT_LANGUAGE = "pref_default_language";
    private static final String PREF_LANGUAGE_API_UPDATE = "pref_language_api_update";
    private static final String PREF_SOURCE_LANGUAGE = "pref_SOURCE_language";
    private static final String PREF_SOURCE_LOCATION = "pref_SOURCE_location";
    private static final String PREF_SOURCE_FAV = "pref_SOURCE_Fav";
    private static final String PREF_WEAT_TEMP = "pref_WEAT_TEMP";
    private static final String PREF_TAP_INTRO = "pref_tap_intro";
    private static final String PREF_AD_ENABLED = "pref_ad_enabled";
    private static final String PREF_ADS = "pref_ads";
    private static final String PREF_BACKGROUND_TIME = "pref_background_time";
    private static final String PREF_REEL_TIME = "pref_reel_time";
    private static final String PREF_RATING_INTERVAL = "pref_rating_interval";
    private static final String PREF_NEXT_RATING_INTERVAL = "pref_next_rating_interval";
    private static final String PREF_RATING_POPUPSHOWN = "pref_rating_popupshown";
    private static final String PREF_LIST_HEIGHT = "pref_list_height";
    private static final String PREF_READER_MODE = "pref_reader_mode";
    private static final String PREF_TEXT_SIZE = "pref_text_size";
    private static final String PREF_HOME_TAB = "pref_home_tab";
    private static final String PREF_NOTIFICATION_SETTINGS = "pref_notification_settings";
    private static final String PREF_GUIDELINES = "pref_guidelines";
    private static final String PREF_CHANNELS = "pref_channels";
    private static final String PREF_ACCESS_TOKEN_OLD = "pref_access_token_old";
    private static final String PREF_ACCESS_TOKEN_SWAPPED = "pref_access_token_swapped";
    private static final String PREF_PROFILE_CHANGE = "pref_profile_change";
    private static final String PREF_GUEST_USER_STATUS = "pref_guest_user_status";
    private static final String PREF_SELECTED_CHANNEL = "pref_selected_channel";
    private static final String PREF_WALLET_URL = "pref_wallet_url";
    private static final String PREF_NEW_LOGIN_FLAG = "pref_new_login_flag";
    private static final String PREF_REEL_SELECTED = "pref_reel_selected";
    private static final String PREF_REEL_TYPE = "pref_reel_type";
    private static final String PREF_BULLET_AUDIO_MUTE = "pref_bullet_audio_mute";
    private static final String PREF_REELS_AUDIO_MUTE = "pref_reels_audio_mute";
    private static final String PREF_REGIONS = "pref_regions";

    private static final String without_header_collapse_size = "without_header_collapse_size";
    private static final String without_header_expand_size = "without_header_expand_size";
    private static final String without_header_size_set = "without_header_size_set";
    private static final String with_header_collapse_size = "with_header_collapse_size";
    private static final String with_header_expand_size = "with_header_expand_size";
    private static final String with_header_size_set = "with_header_size_set";
    private static final String haptic = "haptic";
    private static final String appStateHomeTabs = "appStateHomeTabs";
    private static final String appStateMainTabs = "appStateMainTabs";
    private static final String hasAskedNotificationPermission = "hasAskedNotificationPermission";

    private static final String PREF_DATA_SAVER = "pref_data_saver"; //bullets auto play
    private static final String video_auto_play = "video_auto_play";
    private static final String is_mute = "is_mute";
    private static final String is_first = "is_first";
    private static final String reels_auto_play = "reels_auto_play";
    private static final String reels_caption = "reels_caption";

    private Context context;
    private SharedPreferences sharedPreferences;
    private boolean firstTimeLaunch;

    public PrefConfig(Context context) {
        this.context = context;
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_file), Context.MODE_PRIVATE);
        }
    }

    public void setHasAskedNotificationPermission(boolean hasAskedNotificationPermission) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(this.hasAskedNotificationPermission, hasAskedNotificationPermission);
        editor.apply();
    }

    public boolean hasAskedNotificationPermission() {
        return sharedPreferences.getBoolean(hasAskedNotificationPermission, false);
    }

    public String getSelectedRegion() {
        return sharedPreferences.getString(PREF_REGION, "");
    }

    public void setSelectedRegion(String selectedRegion) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_REGION, selectedRegion);
        editor.apply();
    }

    public String getPrefPrimaryLang() {
        return sharedPreferences.getString(PREF_PRIMARY_LANG, "");
    }

    public void setPrefPrimaryLang(String langName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_PRIMARY_LANG, langName);
        editor.apply();
    }

    public String getPrefSecondaryLang() {
        return sharedPreferences.getString(PREF_SECONDARY_LANG, "");
    }

    public void setPrefSecondaryLang(String langName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SECONDARY_LANG, langName);
        editor.apply();
    }

    public String getPrefLocale() {
        return sharedPreferences.getString(PREF_LOCALE, "");
    }

    public void setPrefLocale(String localeNam) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_LOCALE, localeNam);
        editor.apply();
    }

    public String getAppStateHomeTabs() {
        return sharedPreferences.getString(appStateHomeTabs, null);
    }

    public void setAppStateHomeTabs(String lang) {
        Log.e("setAppState", "setAppState : " + lang);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(appStateHomeTabs, lang);
        editor.apply();
    }

    public UserConfigModel getUserConfig() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_USER_CONFIG, null), UserConfigModel.class);
    }

    public void setUserConfig(UserConfigModel model) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USER_CONFIG, new Gson().toJson(model));
        editor.apply();
    }

    public String getReelState() {
        return sharedPreferences.getString(PREF_REEL_SELECTED, "verified");
    }

    public void setReelState(String lang) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_REEL_SELECTED, lang);
        editor.apply();
    }

    public String getAppStateMainTabs() {
        return sharedPreferences.getString(appStateMainTabs, null);
    }

    public void setAppStateMainTabs(String lang) {
        Log.e("setAppState", "setAppState : " + lang);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(appStateMainTabs, lang);
        editor.apply();
    }

    public boolean getGuidelines() {
        return sharedPreferences.getBoolean(PREF_GUIDELINES, false);
    }

    public void setGuidelines(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_GUIDELINES, flag);
        editor.apply();
    }

    public String getSrcLang() {
        return sharedPreferences.getString(PREF_SOURCE_LANGUAGE, null);
    }

    public void setSrcLang(String lang) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SOURCE_LANGUAGE, lang);
        editor.apply();
    }

    public String getSrcLoc() {
        return sharedPreferences.getString(PREF_SOURCE_LOCATION, null);
    }

    public void setSrcLoc(String lang) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SOURCE_LOCATION, lang);
        editor.apply();
    }

    public boolean getSrcFav() {
        return sharedPreferences.getBoolean(PREF_SOURCE_FAV, false);
    }

    public void setSrcFav(boolean lang) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_SOURCE_FAV, lang);
        editor.apply();
    }

    public boolean getWeaTemp() {
        return sharedPreferences.getBoolean(PREF_WEAT_TEMP, false);
    }

    public void setWeaTemp(boolean isfer) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_WEAT_TEMP, isfer);
        editor.apply();
    }

    public boolean isHaptic() {
        return sharedPreferences.getBoolean(haptic, true);
    }

    public void setHaptic(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(haptic, flag);
        editor.apply();
    }

    public boolean isWithHeaderSizeSet() {
        return sharedPreferences.getBoolean(with_header_size_set, false);
    }

    public void setWithHeaderSizeSet(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(with_header_size_set, flag);
        editor.apply();
    }

    public int getWithHeaderCollapseSize() {
        return sharedPreferences.getInt(with_header_collapse_size, 0);
    }

    public void setWithHeaderCollapseSize(int size) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(with_header_collapse_size, size);
        editor.apply();
    }

    public int getWithHeaderExpandSize() {
        return sharedPreferences.getInt(with_header_expand_size, 0);
    }

    public void setWithHeaderExpandSize(int size) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(with_header_expand_size, size);
        editor.apply();
    }

    public boolean isWithoutHeaderSizeSet() {
        return sharedPreferences.getBoolean(without_header_size_set, false);
    }

    public void setWithoutHeaderSizeSet(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(without_header_size_set, flag);
        editor.apply();
    }

    public int getWithoutHeaderExpandSize() {
        return sharedPreferences.getInt(without_header_expand_size, 0);
    }

    public void setWithoutHeaderExpandSize(int size) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(without_header_expand_size, size);
        editor.apply();
    }

    public int getWithoutHeaderCollapseSize() {
        return sharedPreferences.getInt(without_header_collapse_size, 0);
    }

    public void setWithoutHeaderCollapseSize(int size) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(without_header_collapse_size, size);
        editor.apply();
    }

    public String getFirebaseToken() {
        return sharedPreferences.getString(PREF_FIREBASE_TOKEN, null);
    }

    public void setFirebaseToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_FIREBASE_TOKEN, accessToken);
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(PREF_ACCESS_TOKEN, null);
    }

    public void setAccessToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public String getOldAccessToken() {
        return sharedPreferences.getString(PREF_ACCESS_TOKEN_OLD, null);
    }

    public void setOldAccessToken(String accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_ACCESS_TOKEN_OLD, accessToken);
        editor.apply();
    }

    public boolean getTokenSwapped() {
        return sharedPreferences.getBoolean(PREF_ACCESS_TOKEN_SWAPPED, false);
    }

    public void setTokenSwapped(boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_ACCESS_TOKEN_SWAPPED, status);
        editor.apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(PREF_REFRESH_TOKEN, "");
    }

    public void setRefreshToken(String refreshToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(PREF_USERNAME, "");
    }

    public void setUserEmail(String uname) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USERNAME, uname);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(PREF_USERNAME_NEW, "");
    }

    public void setUsername(String uname) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USERNAME_NEW, uname);
        editor.apply();
    }

    public boolean getHasPassword() {
        return sharedPreferences.getBoolean(PREF_hasPassword, false);
    }

    public void setHasPassword(boolean check) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_hasPassword, check);
        editor.apply();
    }

    public User isUserObject() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_PROFILE, null), User.class);
    }

    public void setUserProfile(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_PROFILE, new Gson().toJson(user));
        editor.apply();
    }

    public void saveNotificationPref(Push push) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_NOTIFICATION_SETTINGS, new Gson().toJson(push));
        editor.apply();
    }

    public Push getNotificationPref() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_NOTIFICATION_SETTINGS, null), Push.class);
    }

    public int getLoginState() {
        return sharedPreferences.getInt(PREF_LOGIN_STATE, 0);
    }

    public void setPrefLoginState(int state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_LOGIN_STATE, state);
        editor.apply();
    }

    public String getAppTheme() {
        return sharedPreferences.getString(PREF_APP_THEME, Constants.LIGHT);
    }

    public void setAppTheme(String state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_APP_THEME, state);
        editor.apply();
    }

    public String getWidgetArticles() {
        return sharedPreferences.getString(PREF_WIDGET_DATA, "");
    }

    public void setWidgetArticles(String data) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_WIDGET_DATA, data);
        editor.apply();
    }

    public int getWidgetCurrentIndex() {
        return sharedPreferences.getInt(PREF_WIDGET_INDEX, 0);
    }

    public void setWidgetCurrentIndex(int index) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_WIDGET_INDEX, index);
        editor.apply();
    }

    public void clear() {
        LanguagesItem defaultLanguage = getDefaultLanguage();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        //keep lang settings even after logout
        setDefaultLanguage(defaultLanguage);
    }

    public String getMenuViewMode() {
        return sharedPreferences.getString(PREF_MENU_VIEW_MODE, MENU_EXTENDED_VIEW);
    }

    public void setMenuViewMode(String mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_MENU_VIEW_MODE, mode);
        editor.apply();
    }

    public Narration getMenuNarration() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_MENU_NARRATION_DATA, ""), Narration.class);
    }

    public void setMenuNarration(Narration menuNarration) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_MENU_NARRATION_DATA, new Gson().toJson(menuNarration));
        editor.apply();
    }

    public Static getHomeImage() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_HOME_IMAGES, ""), Static.class);
    }

    public void setHomeImage(Static mStatic) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_HOME_IMAGES, new Gson().toJson(mStatic));
        editor.apply();
    }

    public boolean getFirstTimeLaunch() {
        return sharedPreferences.getBoolean(PREF_FIRST_TIME, true);
    }

    public void setFirstTimeLaunch(boolean check) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_FIRST_TIME, check);
        editor.apply();
    }

    public String isLanguagePushedToServer() {
        return sharedPreferences.getString(PREF_LANGUAGE_API_UPDATE, "");
    }


    public void setLanguageForServer(String language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_LANGUAGE_API_UPDATE, language);
        editor.apply();
    }

    public Registration getRegistration() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_REGISTRATION, ""), Registration.class);
    }

    public void setRegistration(Registration registration) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_REGISTRATION, new Gson().toJson(registration));
        editor.apply();
    }

    public ArrayList<EditionsItem> getEditions() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_EDITIONS, ""), new TypeToken<ArrayList<EditionsItem>>() {
        }.getType());
    }

    public void setEditions(ArrayList<EditionsItem> itemArrayList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_EDITIONS, new Gson().toJson(itemArrayList));
        editor.apply();
    }

    public LanguagesItem getDefaultLanguage() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_DEFAULT_LANGUAGE, " {\n" +
                "      \"id\": \"ee4add73-b717-4e32-bffb-fecbf82ee6d9\",\n" +
                "      \"name\": \"English (US)\",\n" +
                "      \"sample\": \"Hi!\",\n" +
                "      \"image\": \"https://cdn.newsinbullets.app/flags/us.svg\",\n" +
                "      \"code\": \"en\",\n" +
                "      \"selected\": true\n" +
                "    }"), LanguagesItem.class);
    }

    public void setDefaultLanguage(LanguagesItem registration) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_DEFAULT_LANGUAGE, new Gson().toJson(registration));
        editor.apply();
    }

//    public void setLoginTime() {
//        long time = System.currentTimeMillis();
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putLong(PREF_LOGIN_TIME, time);
//        editor.apply();
//    }
//
//    public long getLoginTime() {
//        return sharedPreferences.getLong(PREF_LOGIN_TIME, 0);
//    }

    public void setLoginCount() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_LOGIN_TIME, getLoginCount() + 1);
        editor.apply();
    }

    public int getLoginCount() {
        return sharedPreferences.getInt(PREF_LOGIN_TIME, 0);
    }

    public void resetTapIntro() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_TAP_INTRO, false);
        editor.apply();
    }

    public boolean isTapIntro() {
        return sharedPreferences.getBoolean(PREF_TAP_INTRO, false);
    }

    public void setTapIntro(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_TAP_INTRO, flag);
        editor.apply();
    }

    public Ads getAds() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_ADS, ""), Ads.class);
    }

    public void setAds(Ads ads) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_ADS, new Gson().toJson(ads));
        editor.apply();
    }


    public long getBgTime() {
        return sharedPreferences.getLong(PREF_BACKGROUND_TIME, 0);
    }

    public void setBgTime(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREF_BACKGROUND_TIME, time);
        editor.apply();
    }

    public long getReelTime() {
        return sharedPreferences.getLong(PREF_REEL_TIME, 0);
    }

    public void setReelTime(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREF_REEL_TIME, time);
        editor.apply();
    }

    public int getRatingInterval() {
        return sharedPreferences.getInt(PREF_RATING_INTERVAL, 100);
    }

    public void setRatingInterval(int interval) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_RATING_INTERVAL, interval);
        editor.apply();
    }

    public int getLongRatingInterval() {
        return sharedPreferences.getInt(PREF_NEXT_RATING_INTERVAL, 100);
    }

    public void setLongRatingInterval(int interval) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_NEXT_RATING_INTERVAL, interval);
        editor.apply();
    }

    public boolean isRatingPopupShown() {
        return sharedPreferences.getBoolean(PREF_RATING_POPUPSHOWN, false);
    }

    public void setRatingPopupShown(boolean isShown) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_RATING_POPUPSHOWN, isShown);
        editor.apply();
    }

    public int getListHeight() {
        return sharedPreferences.getInt(PREF_LIST_HEIGHT, 0);
    }

    public void setListHeight(int listHeight) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_LIST_HEIGHT, listHeight);
        editor.apply();
    }

    public TextSizeModel getTextSize() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_TEXT_SIZE, null), TextSizeModel.class);
    }

    public void setTextSize(String size) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_TEXT_SIZE, size);
        editor.apply();
    }

    public ArrayList<Source> userChannels() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_CHANNELS, null), new TypeToken<ArrayList<Source>>() {
        }.getType());
    }

    public void setUserChannels(ArrayList<Source> channels) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_CHANNELS, new Gson().toJson(channels));
        editor.apply();
    }

    public boolean isProfileChange() {
        return sharedPreferences.getBoolean(PREF_PROFILE_CHANGE, false);
    }

    public void setProfileChange(boolean changed) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_PROFILE_CHANGE, changed);
        editor.apply();
    }

    public boolean isGuestUser() {
        return sharedPreferences.getBoolean(PREF_GUEST_USER_STATUS, false);
    }

    public void setGuestUser(boolean changed) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_GUEST_USER_STATUS, changed);
        editor.apply();
    }

    public String getWalletUrl() {
        return sharedPreferences.getString(PREF_WALLET_URL, "");
    }

    public void setWalletUrl(String wallet) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_WALLET_URL, wallet);
        editor.apply();
    }

    public SelectedChannel selectedChannel() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_SELECTED_CHANNEL, null), SelectedChannel.class);
    }

    public void setChannel(SelectedChannel channel) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SELECTED_CHANNEL, new Gson().toJson(channel));
        editor.apply();
    }

    public boolean getNewUserFlag() {
        return sharedPreferences.getBoolean(PREF_NEW_LOGIN_FLAG, false);
    }

    public void setNewUserFlag(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_NEW_LOGIN_FLAG, flag);
        editor.apply();
    }

    public String getReelsType() {
        return sharedPreferences.getString(PREF_REEL_TYPE, REELS_FOR_YOU);
    }

    public void setReelsType(String type) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_REEL_TYPE, type);
        editor.apply();
    }

    public boolean getBulletAudioMute() {
        return sharedPreferences.getBoolean(PREF_BULLET_AUDIO_MUTE, false);
    }

    public void setBulletAudioMute(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_BULLET_AUDIO_MUTE, flag);
        editor.apply();
    }

    public boolean getReelsAudioMute() {
        return sharedPreferences.getBoolean(PREF_REELS_AUDIO_MUTE, false);
    }

    public void setReelsAudioMute(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_REELS_AUDIO_MUTE, flag);
        editor.apply();
    }

    public ArrayList<Location> getRegions() {
        return new Gson().fromJson(sharedPreferences.getString(PREF_REGIONS, ""), new TypeToken<ArrayList<Location>>() {
        }.getType());
    }

    public void setRegions(ArrayList<Location> itemArrayList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_REGIONS, new Gson().toJson(itemArrayList));
        editor.apply();
    }

    public boolean isBulletsAutoPlay() {
        return sharedPreferences.getBoolean(PREF_DATA_SAVER, false);
    }

    public void setBulletsAutoPlay(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_DATA_SAVER, enabled);
        editor.apply();
    }

    public boolean isReaderMode() {
        return sharedPreferences.getBoolean(PREF_READER_MODE, false);
    }

    public void setReaderMode(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_READER_MODE, enabled);
        editor.apply();
    }

    public boolean isVideoAutoPlay() {
        return sharedPreferences.getBoolean(video_auto_play, true);
    }

    public void setVideoAutoPlay(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(video_auto_play, flag);
        editor.apply();
    }

    public boolean isMute() {
        return sharedPreferences.getBoolean(is_mute, true);
    }


    public void setMute(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(is_mute, flag);
        editor.apply();
    }

    public boolean isFirst() {
//        if (sharedPreferences.getBoolean("firstRun", true)) {
//            //You can perform anything over here. This will call only first time
//            sharedPreferences.edit().putBoolean("firstRun", false).commit();
//        }
        return sharedPreferences.getBoolean(is_first, true);
    }

    public void setFirst(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(is_first, flag);
        editor.apply();
    }

    public boolean isReelsAutoPlay() {
        return sharedPreferences.getBoolean(reels_auto_play, true);
    }

    public void setReelsAutoPlay(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(reels_auto_play, flag);
        editor.apply();
    }

    public boolean isReelsCaption() {
        return sharedPreferences.getBoolean(reels_caption, true);
    }

    public void setReelsCaption(boolean flag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(reels_caption, flag);
        editor.apply();
    }
}
