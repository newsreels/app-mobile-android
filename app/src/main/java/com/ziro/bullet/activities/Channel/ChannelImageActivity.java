package com.ziro.bullet.activities.Channel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.ziro.bullet.R;
import com.ziro.bullet.data.PrefConfig;
import com.ziro.bullet.interfaces.CreateChannelCallback;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.thread.PictureThreadUtils;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.presenter.CreateChannelPresenter;
import com.ziro.bullet.utills.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelImageActivity extends AppCompatActivity implements CreateChannelCallback {

    public static final int PROFILE_IMAGE_REQUEST = 10;
    public static final int PROFILE_COVER_REQUEST = 11;
    public static final int PERMISSION_REQUEST = 745;
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private ConstraintLayout imageMain;
    private CircleImageView image;
    private RelativeLayout back;
    private TextView channelName;
    private TextView button_text;
    private CardView button;
    private String name;
    private String description;
    private ImageView loader;
    private ImageView cover_image;
    private RelativeLayout progress;
    private String url;
    private String cover_url;
    private PrefConfig mPrefConfig;
    private CreateChannelPresenter presenter;
    private PictureLoadingDialog mLoadingDialog;

    @Override
    public void onBackPressed() {
        Utils.showKeyboard(this);
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.putExtra("description", description);
        intent.putExtra("url", url);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_channel_image);
        bindView();
        setListeners();
        init();
        setData();
    }

    private void init() {
        mPrefConfig = new PrefConfig(this);
        presenter = new CreateChannelPresenter(this, this);
    }

    private void bindView() {
        cover_image = findViewById(R.id.cover_image);
        imageMain = findViewById(R.id.imageMain);
        image = findViewById(R.id.image);
        back = findViewById(R.id.back);
        button_text = findViewById(R.id.button_text);
        channelName = findViewById(R.id.channelName);
        button = findViewById(R.id.button);
        progress = findViewById(R.id.progress);
        loader = findViewById(R.id.loader);
    }

    private void setData() {
        if (getIntent() != null) {
            name = getIntent().getStringExtra("name");
            description = getIntent().getStringExtra("description");
            url = getIntent().getStringExtra("url");
            if (!TextUtils.isEmpty(url)) {
                Picasso.get()
                        .load(url)
                        .into(image);
                button_text.setText(getResources().getString(R.string.continuee));
            } else {
                button_text.setText(getResources().getString(R.string.skip));
            }
        }
//        Glide.with(this)
//                .load(Utils.getLoaderForTheme(mPrefConfig.getAppTheme()))
//                .into(loader);
        button.setBackground(getResources().getDrawable(R.drawable.shape));
    }

    private void setListeners() {
        back.setOnClickListener(v -> onBackPressed());
        button.setOnClickListener(v -> {
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(ChannelImageActivity.this, getString(R.string.enter_channel_name), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(ChannelImageActivity.this, CreateChannelActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("description", description);
            intent.putExtra("url", url);
            intent.putExtra("cover", cover_url);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityForResult(intent, 101);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        });
        imageMain.setOnClickListener(v -> fromGallery(PROFILE_IMAGE_REQUEST));
        cover_image.setOnClickListener(v -> fromGallery(PROFILE_COVER_REQUEST));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fromGallery(requestCode);
            }
        }
    }

    public void fromGallery(int type) {
        if (PermissionChecker
                .checkSelfPermission(ChannelImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(ChannelImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryOnlyPicturesForProfilePic(this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), type);
        } else {
            PermissionChecker.requestPermissions(ChannelImageActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, type);
        }
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
        if (resultCode == RESULT_OK) {
            if (requestCode == PROFILE_COVER_REQUEST || requestCode == PROFILE_IMAGE_REQUEST) {
                showProgressDialog();
                LocalMedia localMedia = PictureSelector.obtainMultipleResult(data).get(0);

                File file;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    file = new File(localMedia.getAndroidQToPath());
                } else {
                    file = new File(localMedia.getPath());
                }


                PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<List<File>>() {

                    @Override
                    public List<File> doInBackground() {
                        File modifiedFile;
                        try {
                            modifiedFile = Utils.getFileFromBitmap(ChannelImageActivity.this, "imageProfile", Utils.modifyOrientation(file.getAbsolutePath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            modifiedFile = file;
                        }

                        return Collections.singletonList(modifiedFile);
                    }

                    @Override
                    public void onSuccess(List<File> files) {
                        loadSelectedImage(requestCode, new File(files.get(0).getPath()));
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            loadSelectedImage(requestCode, new File(files.get(0).getPath()));
//                        } else {
//                            loadSelectedImage(requestCode, new File(files.get(0).getPath()));
//                        }
                    }
                });
            } else {
                if (requestCode == 101) {
                    if (data != null) {
                        name = data.getStringExtra("name");
                        description = data.getStringExtra("description");
                        url = data.getStringExtra("url");
                        if (data.getBooleanExtra("finish", false)) {
                            Intent intent = new Intent();
                            intent.putExtra("finish", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }
            }
        }

//        if ((requestCode == PROFILE_IMAGE_REQUEST) && resultCode == RESULT_OK && null != data) {
//            if (data.getClipData() != null) {
//                int count = data.getClipData().getItemCount();
//                for (int i = 0; i < count; i++) {
//                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
//
//                    String selectedImagePath = getRealPathFromURI(imageUri);
//                    File file = new File(selectedImagePath);
//                    loadSelectedImage(requestCode, file, imageUri);
//                }
//            } else if (data.getData() != null) {
//                String selectedImagePath = getRealPathFromURI(data.getData());
//                File file = new File(selectedImagePath);
//                loadSelectedImage(requestCode, file, data.getData());
//            }
//        }
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

    private void loadSelectedImage(int type, File file) {
        button_text.setText(getString(R.string.next));
        if (presenter != null) {
            presenter.uploadImageVideo(file, "images", type);
        }
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            button.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
        } else {
            button.setEnabled(true);
            progress.setVisibility(View.GONE);
            dismissProgressDialog();
        }
    }

    @Override
    public void error(String error) {
        Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void mediaUploaded(String url, String type, int requestCode) {
        if (!TextUtils.isEmpty(url)) {
            if (requestCode == PROFILE_IMAGE_REQUEST) {
                this.url = url;
                Picasso.get().load(url).into(image);
            }
            if (requestCode == PROFILE_COVER_REQUEST) {
                Picasso.get().load(url).into(cover_image);
                this.cover_url = url;
            }
        }
    }

    @Override
    public void validName(boolean isValid) {

    }

    /**
     * loading dialog
     */
    protected void showProgressDialog() {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = new PictureLoadingDialog(this);
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissProgressDialog() {
        try {
            if (mLoadingDialog != null
                    && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            mLoadingDialog = null;
            e.printStackTrace();
        }
    }

}