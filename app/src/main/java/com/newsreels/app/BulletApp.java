package com.newsreels.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.work.WorkManager;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.facebook.FacebookSdk;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.onesignal.OneSignal;
import com.newsreels.app.data.PrefConfig;
//import com.newsreels.app.services.OneSignalNotificationOpenHandler;
import com.newsreels.app.utills.CacheUtils;
import com.newsreels.app.utills.Constants;
import com.newsreels.app.utills.InternetCheckHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class BulletApp extends Application implements androidx.work.Configuration.Provider {

    public static SimpleCache simpleCache = null;
    private static BulletApp singleton = null;
    private LocalizationApplicationDelegate localizationApplicationDelegate = new LocalizationApplicationDelegate();
    private long START_TIME = 0L;
    private long END_TIME = 0L;
    private String CONTENT_ID = "";
    private HttpProxyCacheServer proxy;
    private PrefConfig prefConfig;

    public synchronized static BulletApp getInstance() {
        if (singleton == null) {
            singleton = new BulletApp();
        }
        return singleton;
    }

    public static void setDarkLightTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
    }

    private  String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(this.getAssets().open("service-account.json"))
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }
    public static HttpProxyCacheServer getProxy(Context context) {
        BulletApp app = (BulletApp) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    public void setTracker(String id, long start) {
        CONTENT_ID = id;
        START_TIME = start;
    }

    public Pair<String, Long> getTracker() {
        long end = System.currentTimeMillis() - START_TIME;
        return new Pair<>(CONTENT_ID, end);
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this).cacheDirectory(CacheUtils.getVideoCacheDir(this)).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        WorkManager.initialize(this, new androidx.work.Configuration.Builder().build());
        MultiDex.install(this);
        prefConfig = new PrefConfig(this);
        FirebaseApp.initializeApp(this);
        Glide.get(getApplicationContext()).clearMemory();
        FacebookSdk.sdkInitialize(getApplicationContext());

        MobileAds.initialize(getApplicationContext());
//        checkAAID();
        AudienceNetworkAds.initialize(this);
//        checkAppModeColor(false);
        if (prefConfig.isFirst()) {
            Constants.ReelMute = false;
            prefConfig.setMute(false);
            prefConfig.setFirst(false);
        }

        Constants.muted = prefConfig.getBulletAudioMute();

//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//        OneSignal.initWithContext(getApplicationContext());
//        OneSignal.setAppId(BuildConfig.One_Signal);
//        OneSignal.setNotificationOpenedHandler(new OneSignalNotificationOpenHandler(getApplicationContext()));

//        if (prefConfig.getFirebaseToken() != null && !prefConfig.getFirebaseToken().isEmpty())
//            OneSignal.setExternalUserId(prefConfig.getFirebaseToken());

        PRDownloaderConfig.newBuilder().setDatabaseEnabled(true);
        PRDownloader.initialize(getApplicationContext());
        InternetCheckHelper.getInstance().startObservingConnection(getApplicationContext());



//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                try{
//                    Log.d("SHAHZAIB", "ACCESS TOKEN -> "+getAccessToken());
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                    Log.d("SHAHZAIB", "ACCESS TOKEN -> FAILED");
//                }
//            }
//        }.start();

    }
//
//    @Override
//    protected void attachBaseContext(Context base) {
//        Locale currentLanguage = Locale.getDefault();
//        if (currentLanguage.getLanguage().equals("hi")) {
//            super.attachBaseContext(LocaleManager.onAttach(base, "hi", Locale.getDefault().getCountry()));
//        } else {
//            super.attachBaseContext(LocaleManager.onAttach(base, Locale.ENGLISH.getLanguage(), Locale.getDefault().getCountry()));
//        }
//    }


    @Override
    protected void attachBaseContext(Context base) {
        Locale deviceLocale = null;
        if (prefConfig == null) {
            prefConfig = new PrefConfig(base);
        }
        String appLocale = prefConfig.getPrefLocale();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            deviceLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            deviceLocale = Resources.getSystem().getConfiguration().locale;
        }
        if (deviceLocale.getLanguage().equals(appLocale)) {
            localizationApplicationDelegate.setDefaultLanguage(base, Locale.forLanguageTag(appLocale));
        } else {
            if (deviceLocale.getLanguage().equals("hi")) {
                prefConfig.setPrefLocale("hi");
                localizationApplicationDelegate.setDefaultLanguage(base, Locale.forLanguageTag("hi"));
            } else {
                prefConfig.setPrefLocale(Locale.ENGLISH.getLanguage());
                localizationApplicationDelegate.setDefaultLanguage(base, Locale.ENGLISH);
            }
        }
        super.attachBaseContext(localizationApplicationDelegate.attachBaseContext(base));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        localizationApplicationDelegate.onConfigurationChanged(this);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Context getApplicationContext() {
        return localizationApplicationDelegate.getApplicationContext(super.getApplicationContext());
    }

    @Override
    public Resources getResources() {
        return localizationApplicationDelegate.getResources(getBaseContext(), super.getResources());
    }

    public void checkAppModeColor(boolean isDarkModeOnly) {
//        if (isDarkModeOnly) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            PrefConfig prefConfig = new PrefConfig(this);
//            if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.AUTO)) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//            } else if (prefConfig.getAppTheme().equalsIgnoreCase(Constants.DARK)) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            }
//        }
    }

    @NonNull
    @Override
    public androidx.work.Configuration getWorkManagerConfiguration() {
        return new androidx.work.Configuration.Builder().build();
    }

//    @NonNull
//    @Override
//    public Locale getDefaultLanguage(@NonNull Context context) {
//        Locale currentLanguage = Locale.getDefault();
//        Log.d("DEBUG_TAG", "attachBaseContext: " + currentLanguage.getLanguage());
//        if (currentLanguage.getLanguage().equals("hi")) {
//            return Locale.forLanguageTag("hi");
//        } else {
//            return Locale.forLanguageTag("en");
//        }
//    }
}
