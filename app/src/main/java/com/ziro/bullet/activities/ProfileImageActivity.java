package com.ziro.bullet.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.activities.onboarding.OnBoardingActivity;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.data.models.config.UserConfigModel;
import com.ziro.bullet.interfaces.AccountLinkCallback;
import com.ziro.bullet.interfaces.ProfileApiCallback;
import com.ziro.bullet.interfaces.UserConfigCallback;
import com.ziro.bullet.presenter.FCMPresenter;
import com.ziro.bullet.presenter.PasswordPresenter;
import com.ziro.bullet.presenter.ProfilePresenter;
import com.ziro.bullet.presenter.UserConfigPresenter;
import com.ziro.bullet.utills.Components;
import com.ziro.bullet.utills.SearchLayout;
import com.ziro.bullet.utills.Utils;
import com.ziro.bullet.widget.CollectionWidget;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageActivity extends BaseActivity implements ProfileApiCallback, UserConfigCallback {

    public static final int PROFILE_IMAGE_REQUEST = 3857;
    public static final int PERMISSION_REQUEST = 745;
    private static final String TAG = "ProfileImageActivity";
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private CircleImageView profile_image;
    private UserConfigPresenter configPresenter;
    private PasswordPresenter presenter;
    private FCMPresenter fcmPresenter;
    private RelativeLayout back;
    private String username;
    private String firstName;
    private String lastName;
    private TextView button_text;
    private CardView button;
    private File profileImageFile = null;
    private ProfilePresenter profilePresenter;
    private PrefConfig mPrefConfig;
    private RelativeLayout progress;
    private ImageView edit_profile_image;
    private boolean isPopUpLogin;

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

        }

        @Override
        public void error404(String error) {

        }

        @Override
        public void onLinkSuccess(boolean status) {
            if (status) {
                if (configPresenter != null) {
                    configPresenter.getUserConfig(false);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        SearchLayout.setSearchActivity(this);
        Utils.hideKeyboard(this, back);
    }

    @Override
    public void onBackPressed() {
        Utils.showKeyboard(this);
        finishAfterTransition();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Components().blackStatusBar(this, "black");
        BulletApp.setDarkLightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        setContentView(R.layout.activity_profile_image);

        if (getIntent() != null && getIntent().hasExtra("isPopUpLogin")) {
            isPopUpLogin = getIntent().getBooleanExtra("isPopUpLogin", false);
        }

        bindView();
        init();
        setData();
        setListener();

    }


    private void passDataToPreviousActivity() {
        if (isPopUpLogin) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", true);
            setResult(Activity.RESULT_OK, returnIntent);
            onBackPressed();
        }
    }

    private void init() {
        mPrefConfig = new PrefConfig(this);
        presenter = new PasswordPresenter(this, null);
        fcmPresenter = new FCMPresenter(this);
        profilePresenter = new ProfilePresenter(this, this);
        configPresenter = new UserConfigPresenter(this, this);
    }

    private void setListener() {
        back.setOnClickListener(v -> onBackPressed());
        profile_image.setOnClickListener(v -> {
            fromGallery(PROFILE_IMAGE_REQUEST);
        });
        edit_profile_image.setOnClickListener(v -> {
            fromGallery(PROFILE_IMAGE_REQUEST);
        });
        button.setOnClickListener(v -> {
            profilePresenter.updateProfile(firstName, profileImageFile, null, true, username);
        });
    }

    private void bindView() {
        edit_profile_image = findViewById(R.id.edit_profile_image);
        back = findViewById(R.id.back);
        progress = findViewById(R.id.progress);
        profile_image = findViewById(R.id.profile_image);
        button = findViewById(R.id.button);
        button_text = findViewById(R.id.button_text);
        button_text.setText(getString(R.string.skip));
//        button.setBackground(getResources().getDrawable(R.drawable.shape_with_stroke));
    }

    private void setData() {
        if (getIntent() != null) {
            username = getIntent().getStringExtra("username");
            firstName = getIntent().getStringExtra("first_name");
            lastName = getIntent().getStringExtra("last_name");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fromGallery(PROFILE_IMAGE_REQUEST);
            }
        }
    }

    public void fromGallery(int type) {
        if (!checkPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST);
            error(getString(R.string.permision), null);
            return;
        }

        String[] mimeTypes = {"image/jpeg", "image/jpg"};
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                .setType("image/jpg")
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(pickPhoto, type);
    }

    public boolean checkPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PROFILE_IMAGE_REQUEST) && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();

                    String selectedImagePath = getRealPathFromURI(imageUri);
                    File file = new File(selectedImagePath);
                    loadSelectedImage(requestCode, file, imageUri);
                }
            } else if (data.getData() != null) {
                String selectedImagePath = getRealPathFromURI(data.getData());
                File file = new File(selectedImagePath);
                loadSelectedImage(requestCode, file, data.getData());
            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return "";
    }

    private void loadSelectedImage(int type, File file, Uri uri) {
        button_text.setText(getString(R.string.next));
        File modifiedFile;
        try {
            modifiedFile = Utils.getFileFromBitmap(this, "imageProfile", Utils.modifyOrientation(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
            modifiedFile = file;
        }
        if (type == PROFILE_IMAGE_REQUEST) {
            profile_image.setImageURI(uri);
            profileImageFile = modifiedFile;
        }
    }


    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
        button.setEnabled(!flag);
        back.setEnabled(!flag);
    }

    @Override
    public void error(String error) {
        Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void error404(String error) {
        Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserConfigSuccess(UserConfigModel userConfigModel) {
        try {

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(mPrefConfig.isLanguagePushedToServer())) {
            presenter.selectRegion(mPrefConfig.getSelectedRegion(), mPrefConfig);
            presenter.selectLanguage(mPrefConfig.isLanguagePushedToServer(), mPrefConfig);
        }

        Intent intent = null;
        if (userConfigModel.isOnboarded()) {
            intent = new Intent(ProfileImageActivity.this, MainActivityNew.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            updateWidget();
        } else {
            intent = new Intent(ProfileImageActivity.this, OnBoardingActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("main", "main");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (isPopUpLogin) {
            passDataToPreviousActivity();
        } else {
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
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
    public void error(String error, String img) {
        Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success() {
//        if(isPopUpLogin){
//            if (configPresenter != null) {
//                configPresenter.linkAccount(accountLinkCallback);
//            }
//        }else {
        if (configPresenter != null) {
            configPresenter.getUserConfig(false);
        }
//        }
//        Intent intent = new Intent(ProfileImageActivity.this, MainActivityNew.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        startActivity(intent);
//        overridePendingTransition(R.anim.enter, R.anim.exit);
    }
}