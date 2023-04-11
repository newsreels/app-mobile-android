package com.ziro.bullet.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.ziro.bullet.APIResources.ApiClient;
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.CacheData.DbHandler;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.onboarding.OnBoardingActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.interfaces.PasswordInterface;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.model.Reel.ReelResponse;
import com.ziro.bullet.presenter.FCMPresenter;
import com.ziro.bullet.presenter.SocialLoginPresenter;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirstActivity extends BaseActivity implements PasswordInterface, UserConfigCallback {
    private static final String TAG = FirstActivity.class.getSimpleName();

    private static final List<String> FACEBOOK_PERMISSIONS = Arrays.asList("public_profile", "email");
    private static final int RC_SIGN_IN = 33421;
    public static final int FINISH_TO_LOGIN = 2317;
    private CallbackManager fbCallbackManager;
    private PrefConfig mPrefConfig;
    private SocialLoginPresenter mSocialLoginPresenter;
    private UserConfigPresenter configPresenter;
    private FCMPresenter fcmPresenter;
    private RelativeLayout progress;
    private CardView rlSkipLogin;
    private TextView terms;
    private TextView privacy;
    private RelativeLayout tint;
    private LinearLayout language;
    private TextView languageText;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Components().blackStatusBar(this, "black");
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_first);

        progress = findViewById(R.id.progress);
        rlSkipLogin = findViewById(R.id.rlSkipLogin);
        tint = findViewById(R.id.tint);
        language = findViewById(R.id.language);
        languageText = findViewById(R.id.languageText);
        mPrefConfig = new PrefConfig(this);
        Utils.saveSystemThemeAsDefault(this, mPrefConfig);


        mPrefConfig.setFirstTimeLaunch(false);

        mSocialLoginPresenter = new SocialLoginPresenter(this, this);
        configPresenter = new UserConfigPresenter(this, this);
        fcmPresenter = new FCMPresenter(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.G_SERVER_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Constants.HomeSelectedFragment = Constants.BOTTOM_TAB_HOME;

        CardView ivFb = findViewById(R.id.btnFacebook);
        ivFb.setOnClickListener(v -> {
            AnalyticsEvents.INSTANCE.logEvent(this,
                    Events.SIGNUP_FACEBOOK);
            if (!InternetCheckHelper.isConnected()) {
                Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                return;
            }

            ivFb.setEnabled(false);
            fbCallbackManager = CallbackManager.Factory.create();

            if (LoginManager.getInstance() != null) {
                LoginManager.getInstance().logOut();
            }

            LoginManager.getInstance().logInWithReadPermissions(this, FACEBOOK_PERMISSIONS);

            LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult result) {
                    //1. Logged in to Facebook
                    AccessToken accessToken = result.getAccessToken();
                    mSocialLoginPresenter.facebookLogin(accessToken);
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "Facebook sign-in cancelled");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e(TAG, "Error " + error.getMessage());
                }
            });
            new Handler().postDelayed(() -> {
                ivFb.setEnabled(true);
            }, 2000);
        });

        CardView btnGoogle = findViewById(R.id.btn_google);
        btnGoogle.setOnClickListener(v -> {
            AnalyticsEvents.INSTANCE.logEvent(this,
                    Events.SIGNUP_GOOGLE);
            if (!InternetCheckHelper.isConnected()) {
                Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                return;
            }
            btnGoogle.setEnabled(false);
            googleSignin();
//            mSocialLoginPresenter.googleLogin();
            new Handler().postDelayed(() -> {
                btnGoogle.setEnabled(true);
            }, 2000);
        });

        findViewById(R.id.signin).setOnClickListener(v -> {
            Intent intent = new Intent(FirstActivity.this, SignInActivity.class);
            intent.putExtra("check", "signin");
            intent.putExtra("finishToLogin", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        findViewById(R.id.signup).setOnClickListener(v -> {
            Intent intent = new Intent(FirstActivity.this, SignInActivity.class);
            intent.putExtra("check", "signup");
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        rlSkipLogin.setOnClickListener(v -> {
            if (!InternetCheckHelper.isConnected()) {
                Toast.makeText(FirstActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                return;
            }
            AnalyticsEvents.INSTANCE.logEvent(FirstActivity.this,
                    Events.SIGNIN_GUEST);
            mSocialLoginPresenter.skipLogin(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        });

        if (mPrefConfig != null && mPrefConfig.getDefaultLanguage() != null && !TextUtils.isEmpty(mPrefConfig.getDefaultLanguage().getName())) {
            languageText.setText(mPrefConfig.getDefaultLanguage().getName());
            language.setOnClickListener(v -> {
                Intent intent = new Intent(FirstActivity.this, LanguageActivity.class);
                startActivity(intent);
            });
        }
        setTextColor();
    }

    private void setTextColor() {

        privacy = findViewById(R.id.privacy);
        terms = findViewById(R.id.terms);
        String bodyData = getString(R.string.by_clicking_continue_you_agree_with_our_terms);
        String bodyData2 = getString(R.string.in_our_privacy_policy);
        try {

            String locale = getResources().getConfiguration().locale.getLanguage();

            if (TextUtils.isEmpty(locale) || locale.toLowerCase().contains("en")) {
                String termsTxt = getString(R.string.terms);

                int termsLength = termsTxt.length();
                int termsStart = ((bodyData.length() - 1) - termsLength);
                int termsEnd = bodyData.length() - 1;

                SpannableStringBuilder spannableString = new SpannableStringBuilder(bodyData);
                spannableString.setSpan(new UnderlineSpan(), termsStart, termsEnd, 0);
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        Utils.openWebView(FirstActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions));
                    }
                }, termsStart, termsEnd, 0);
                spannableString.setSpan(
                        new ForegroundColorSpan(getResources().getColor(R.color.guest_login_btn_border)),
                        termsStart, termsEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                terms.setText(spannableString);
                terms.setMovementMethod(LinkMovementMethod.getInstance());

                String privacyPolicyTxt = getString(R.string.privacy_policy);
                int privacyLength = privacyPolicyTxt.length();
                int privacyStart = ((bodyData2.length() - 1) - privacyLength);
                int privacyEnd = bodyData2.length() - 1;

                SpannableStringBuilder spannableString2 = new SpannableStringBuilder(bodyData2);
                spannableString2.setSpan(new UnderlineSpan(), privacyStart, privacyEnd, 0);
                spannableString2.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        Utils.openWebView(FirstActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy));
                    }
                }, privacyStart, privacyEnd, 0);
                spannableString2.setSpan(
                        new ForegroundColorSpan(getResources().getColor(R.color.guest_login_btn_border)),
                        privacyStart, privacyEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                privacy.setText(spannableString2);
                privacy.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                terms.setText(bodyData);
                privacy.setText(bodyData2);
                terms.setOnClickListener(v -> Utils.openWebView(FirstActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions)));
                privacy.setOnClickListener(v -> Utils.openWebView(FirstActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy)));

            }

        } catch (Exception e) {
            e.printStackTrace();
            terms.setText(bodyData);
            privacy.setText(bodyData2);
            terms.setOnClickListener(v -> Utils.openWebView(FirstActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions)));
            privacy.setOnClickListener(v -> Utils.openWebView(FirstActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy)));
        }
    }

    private void googleSignin() {


        if (GoogleSignIn.getLastSignedInAccount(this) == null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            mGoogleSignInClient.signOut();
            googleSignin();
//            mSocialLoginPresenter.googleLogin(account.getIdToken());
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            if (fbCallbackManager != null)
                fbCallbackManager.onActivityResult(requestCode, resultCode, data);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.

            mSocialLoginPresenter.googleLogin(account.getIdToken());
//            updateUI(account);
        } catch (ApiException e) {
            Toast.makeText(FirstActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();

//            updateUI(null);
        }
    }


    @Override
    public void loaderShow(boolean flag) {
        try{
        runOnUiThread(() -> {
            if (flag) {
                progress.setVisibility(View.VISIBLE);
                tint.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.GONE);
            }
        });}catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void error(String error) {
        try {
            runOnUiThread(() -> {
                if (TextUtils.isEmpty(error)) {
                    Toast.makeText(FirstActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FirstActivity.this, error, Toast.LENGTH_SHORT).show();
                }
                tint.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void error404(String error) {
        try{
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FirstActivity.this, error, Toast.LENGTH_SHORT).show();
                tint.setVisibility(View.GONE);
            }
        });}catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUserConfigSuccess(UserConfigModel userConfigModel) {
        try {
//            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
//                if (mPrefConfig != null) {
//                    mPrefConfig.setFirebaseToken(Objects.requireNonNull(task.getResult()).getToken());
//                    fcmPresenter.sentTokenToServer(mPrefConfig);
//                }
//            });

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    Log.d(TAG, "onComplete: fcm  == " + token);

                    mPrefConfig.setFirebaseToken(token);
                    fcmPresenter.sentTokenToServer(mPrefConfig);
                }
            });

            if (!TextUtils.isEmpty(mPrefConfig.isLanguagePushedToServer())) {
                mSocialLoginPresenter.selectRegion(mPrefConfig.getSelectedRegion(), mPrefConfig);
                mSocialLoginPresenter.selectLanguage(mPrefConfig.isLanguagePushedToServer());
            }
//            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(task -> {
//                if (mPrefConfig != null) {
//                    mPrefConfig.setFirebaseToken(Objects.requireNonNull(task.getResult()).getToken());
//                    fcmPresenter.sentTokenToServer(mPrefConfig);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (userConfigModel.isOnboarded()) {
//            mPrefConfig.setLoginTime();
            loadReels();
        } else {
            Intent intent = null;
            intent = new Intent(FirstActivity.this, OnBoardingActivity.class);
            tint.setVisibility(View.GONE);

            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }

    }

    private void gotoHome(){
        tint.setVisibility(View.GONE);
        Intent intent = new Intent(FirstActivity.this, MainActivityNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        updateWidget();
    }

    private void loadReels() {
        loaderShow(true);
        Call<ReelResponse> call = ApiClient.getInstance(this)
                .getApi()
                .newsReel(
                        "Bearer " + mPrefConfig.getAccessToken(), "", "", "", Constants.REELS_FOR_YOU,BuildConfig.DEBUG
                );

        call.enqueue(new Callback<ReelResponse>() {
            @Override
            public void onResponse(Call<ReelResponse> call, Response<ReelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DbHandler dbHandler = new DbHandler(FirstActivity.this);
                    dbHandler.insertReelList("ReelsList", new Gson().toJson(response.body()));
                }
                loaderShow(false);
                gotoHome();
            }

            @Override
            public void onFailure(Call<ReelResponse> call, Throwable t) {
                loaderShow(false);
                gotoHome();
            }
        });
    }

    private void updateWidget() {
        try {
            if (AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, CollectionWidget.class)).length > 0) {
                Intent updateWidget = new Intent(this, CollectionWidget.class);
                updateWidget.setAction("update_widget");
                PendingIntent pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                pending.send();
            }
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void success(boolean flag) {
        if (flag) {
            configPresenter.getUserConfig(false);
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public void resetSuccess(String str) {

    }
}
