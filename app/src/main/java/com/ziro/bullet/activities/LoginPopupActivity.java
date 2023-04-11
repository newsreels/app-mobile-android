package com.ziro.bullet.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
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
import com.ziro.bullet.BuildConfig;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.onboarding.OnBoardingActivity;
import com.ziro.bullet.analytics.AnalyticsEvents;
import com.ziro.bullet.analytics.Events;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.interfaces.AccountLinkCallback;
import com.ziro.bullet.interfaces.PasswordInterface;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.presenter.FCMPresenter;
import com.ziro.bullet.presenter.SocialLoginPresenter;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.InternetCheckHelper;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import java.util.Arrays;
import java.util.List;

public class LoginPopupActivity extends BaseActivity implements View.OnClickListener {

    public static final int LOGIN_WITH_EMAIL = 45;
    private static final int RC_SIGN_IN = 33421;
    private static final List<String> FACEBOOK_PERMISSIONS = Arrays.asList("public_profile", "email");
    private static final String TAG = LoginPopupActivity.class.getSimpleName();
    private RelativeLayout ivBack;
    private TextView agreeTextView;
    private CardView signInGoogle;
    private CardView signInFacebook;
    private CardView signInEmail;
    private RelativeLayout progress;

    private PrefConfig mPrefConfig;
    private FCMPresenter fcmPresenter;

    private UserConfigPresenter configPresenter;
    private SocialLoginPresenter mSocialLoginPresenter;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager fbCallbackManager;

    private boolean linkingApiCalled;

    private UserConfigCallback userConfigCallback = new UserConfigCallback() {
        @Override
        public void loaderShow(boolean flag) {
            try{
            runOnUiThread(() -> {
                if (flag) {
                    progress.setVisibility(View.VISIBLE);
                } else {
                    progress.setVisibility(View.GONE);
                }
            });}catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void error(String error) {
            try{
            runOnUiThread(() -> {
                if (TextUtils.isEmpty(error)) {
                    Toast.makeText(LoginPopupActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginPopupActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });}catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void error404(String error) {
            try{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginPopupActivity.this, error, Toast.LENGTH_SHORT).show();
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


            if (!userConfigModel.getUser().isSetup() && !linkingApiCalled) {
                linkingApiCalled = true;
                configPresenter.linkAccount(accountLinkCallback);
                return;
            }


//            Intent intent = null;
//            if (userConfigModel.isOnboarded()) {
////            mPrefConfig.setLoginTime();
//                intent = new Intent(LoginPopupActivity.this, MainActivityNew.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                updateWidget();
//            } else {
//                intent = new Intent(LoginPopupActivity.this, OnBoardingActivity.class);
//
//            }

            passDataToPreviousActivity();


//            startActivity(intent);
//            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    };
    private AccountLinkCallback accountLinkCallback = new AccountLinkCallback() {
        @Override
        public void loaderShow(boolean flag) {
            if (flag) {
                progress.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.GONE);
            }
        }

        @Override
        public void error(String error) {
            try{
            runOnUiThread(() -> {
                if (TextUtils.isEmpty(error)) {
                    Toast.makeText(LoginPopupActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginPopupActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });}catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void error404(String error) {
            try{
            runOnUiThread(() -> {
                if (TextUtils.isEmpty(error)) {
                    Toast.makeText(LoginPopupActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginPopupActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });}catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLinkSuccess(boolean status) {
            if (status) {
                configPresenter.getUserConfig(false);
            }
        }
    };
    private PasswordInterface passwordInterface = new PasswordInterface() {
        @Override
        public void loaderShow(boolean flag) {
            try{
            runOnUiThread(() -> {
                if (flag) {
                    progress.setVisibility(View.VISIBLE);
                } else {
                    progress.setVisibility(View.GONE);
                }
            });}catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void error(String error) {
            try{
            runOnUiThread(() -> {
                if (TextUtils.isEmpty(error)) {
                    Toast.makeText(LoginPopupActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginPopupActivity.this, error, Toast.LENGTH_SHORT).show();
                }
                progress.setVisibility(View.GONE);
            });}catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void error404(String error) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginPopupActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void success(boolean flag) {
            if (flag) {

                configPresenter.getUserConfig(true);

//                linkingApiCalled = true;
//                configPresenter.linkAccount(accountLinkCallback);
            }
        }

        @Override
        public void reset() {

        }

        @Override
        public void resetSuccess(String str) {

        }
    };

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, RegistrationActivity.class);
        intent.putExtra("popup", true);
        activity.startActivityForResult(intent, LOGIN_WITH_EMAIL);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.no_animation);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_popup);

        mSocialLoginPresenter = new SocialLoginPresenter(this, passwordInterface);
        configPresenter = new UserConfigPresenter(this, userConfigCallback);
        mPrefConfig = new PrefConfig(this);
        fcmPresenter = new FCMPresenter(this);

        mPrefConfig.setOldAccessToken(mPrefConfig.getAccessToken());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.G_SERVER_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        bindView();
        initViews();
        setTextColor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkAppModeColor(this, false);
    }

    private void bindView() {
        ivBack = findViewById(R.id.ivBack);
        agreeTextView = findViewById(R.id.agree_text_view);
        signInGoogle = findViewById(R.id.sign_in_google);
        signInFacebook = findViewById(R.id.sign_in_facebook);
        signInEmail = findViewById(R.id.sign_in_email);
        progress = findViewById(R.id.progress);
    }

    private void initViews() {
        ivBack.setOnClickListener(this);
        signInGoogle.setOnClickListener(this);
        signInFacebook.setOnClickListener(this);
        signInEmail.setOnClickListener(this);
    }

    private void setTextColor() {

        String bodyData = getString(R.string.login_popup_agree);
        try {
            String terms = getString(R.string.terms);
            String privacyPolicy = getString(R.string.privacy_policy);

            int termsStart = bodyData.indexOf(terms);
            int termsEnd = termsStart + terms.length();

            int privacyPolicyStart = bodyData.indexOf(privacyPolicy);
            int privacyPolicyEnd = privacyPolicyStart + privacyPolicy.length();

            SpannableStringBuilder spannableString = new SpannableStringBuilder(bodyData);
            spannableString.setSpan(new UnderlineSpan(), termsStart, termsEnd, 0);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View v) {
                    Utils.openWebView(LoginPopupActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions));
                }
            }, termsStart, termsEnd, 0);
            spannableString.setSpan(
                    new ForegroundColorSpan(getResources().getColor(R.color.guest_login_btn_border)),
                    termsStart, termsEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            spannableString.setSpan(new UnderlineSpan(), privacyPolicyStart, privacyPolicyEnd, 0);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View v) {
                    Utils.openWebView(LoginPopupActivity.this, "https://www.newsinbullets.app/privacy?header=false", getString(R.string.privacy_policy));
                }
            }, privacyPolicyStart, privacyPolicyEnd, 0);
            spannableString.setSpan(
                    new ForegroundColorSpan(getResources().getColor(R.color.guest_login_btn_border)),
                    privacyPolicyStart, privacyPolicyEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            agreeTextView.setText(spannableString);
            agreeTextView.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception exception) {
            agreeTextView.setText(bodyData);
            agreeTextView.setOnClickListener(v -> {
                Utils.openWebView(LoginPopupActivity.this, "https://www.newsinbullets.app/terms?header=false", getString(R.string.terms_conditions));
            });
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out_up);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {
            onBackPressed();
        } else if (v.getId() == R.id.sign_in_google) {
            AnalyticsEvents.INSTANCE.logEvent(this,
                    Events.GUEST_SIGNUP_GOOGLE);
            if (!InternetCheckHelper.isConnected()) {
                Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                return;
            }
            signInGoogle.setEnabled(false);
            googleSignin();
//            mSocialLoginPresenter.googleLogin();
            new Handler().postDelayed(() -> {
                signInGoogle.setEnabled(true);
            }, 2000);
        } else if (v.getId() == R.id.sign_in_facebook) {
            AnalyticsEvents.INSTANCE.logEvent(this,
                    Events.GUEST_SIGNUP_FACEBOOK);
            if (!InternetCheckHelper.isConnected()) {
                Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                return;
            }

            signInFacebook.setEnabled(false);
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
                signInFacebook.setEnabled(true);
            }, 2000);
        } else if (v.getId() == R.id.sign_in_email) {
            AnalyticsEvents.INSTANCE.logEvent(this,
                    Events.GUEST_SIGNUP_EMAIL);
            Intent intent = new Intent(this, SignInActivity.class);
            intent.putExtra("check", "signin");
            intent.putExtra("isPopUpLogin", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, LOGIN_WITH_EMAIL);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (requestCode == LOGIN_WITH_EMAIL) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("result", false);
                if (result) {
                    Log.e("####", String.valueOf(true));
                    passDataToPreviousActivity();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        } else {
            if (fbCallbackManager != null)
                fbCallbackManager.onActivityResult(requestCode, resultCode, data);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void passDataToPreviousActivity() {
        if (mPrefConfig != null) {
            mPrefConfig.setGuestUser(false);
            mPrefConfig.setProfileChange(true);
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", true);
        setResult(Activity.RESULT_OK, returnIntent);

        onBackPressed();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.

            mSocialLoginPresenter.googleLogin(account.getIdToken());
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            e.printStackTrace();
            Toast.makeText(LoginPopupActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
//            updateUI(null);
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
}