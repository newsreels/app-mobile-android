package com.ziro.bullet.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.ziro.bullet.BulletApp;
import com.ziro.bullet.R;
import com.ziro.bullet.data.MODE;
import com.ziro.bullet.interfaces.PostArticleCallback;
import com.ziro.bullet.mediapicker.GalleryPicker;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.engine.GlideCacheEngine;
import com.ziro.bullet.mediapicker.engine.GlideEngine;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.permissions.PermissionChecker;
import com.ziro.bullet.mediapicker.utils.PictureSelector;
import com.ziro.bullet.model.articles.Article;
import com.ziro.bullet.model.articles.Bullet;
import com.ziro.bullet.model.Reel.ReelsItem;
import com.ziro.bullet.presenter.PostArticlePresenter;
import com.ziro.bullet.utills.BlurTransformation;
import com.ziro.bullet.utills.Constants;
import com.ziro.bullet.utills.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddBulletActivity extends BaseActivity implements PostArticleCallback {

    public static final int PERMISSION_REQUEST = 745;
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TextView title;
    private TextView counter;
    private TextView save;
    private RelativeLayout back;
    private RelativeLayout menu;
    private LinearLayout delete_image;
    private LinearLayout replace_image;
    private LinearLayout delete_bullet;
    private ImageView imageBack;
    private ImageView image;
    private CardView card;
    private EditText bullet;
    private Bullet bulletObj = new Bullet();
    private String position = null;
    private int size = 0;
    private ConstraintLayout save_btn;
    private RelativeLayout progressLayout;
    private PostArticlePresenter presenter;
    private MODE mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this);
        setContentView(R.layout.activity_add_bullet);
        presenter = new PostArticlePresenter(this, this);
        bindView();
        listener();
        bundles();
    }

    private void bindView() {
        save_btn = findViewById(R.id.save_btn);
        save = findViewById(R.id.save);
        menu = findViewById(R.id.menu);
        delete_bullet = findViewById(R.id.delete_bullet);
        replace_image = findViewById(R.id.replace_image);
        delete_image = findViewById(R.id.delete_image);
        counter = findViewById(R.id.counter);
        progressLayout = findViewById(R.id.progressLayout);
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        card = findViewById(R.id.card);
        imageBack = findViewById(R.id.imageBack);
        image = findViewById(R.id.image);
        bullet = findViewById(R.id.bullet);
        save_btn.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_post_article_button));
        save.setTextColor(getResources().getColor(R.color.white));
    }

    private void bundles() {
        if (getIntent() != null) {
            size = getIntent().getIntExtra("size", 0);
            position = getIntent().getStringExtra("position");
            mode = (MODE) getIntent().getSerializableExtra("MODE");
            int pos = Integer.parseInt(position);
            pos++;
            title.setText(getString(R.string.add_bullet_) + " " + pos);
            if (size > 2) {
                delete_bullet.setVisibility(View.VISIBLE);
            }
            bulletObj = new Gson().fromJson(getIntent().getStringExtra("bullet"), Bullet.class);
            if (bulletObj != null) {
                if (!TextUtils.isEmpty(bulletObj.getData()))
                    bullet.setText(bulletObj.getData());
                loadSelectedImage(bulletObj.getImage(), false);
            }
        }
    }

    private void listener() {
        back.setOnClickListener(v -> finish());
        save_btn.setOnClickListener(v -> save());
        delete_image.setOnClickListener(v -> {
            imageBack.setImageDrawable(null);
            image.setImageDrawable(null);
            if (bulletObj != null) {
                bulletObj.setImage("");
            }
            menu.setVisibility(View.GONE);
        });
        replace_image.setOnClickListener(v -> {
            gotoGallery();
        });
        card.setOnClickListener(v -> {
            gotoGallery();
        });
        bullet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    counter.setText(s.length() + "/250");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        delete_bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("bullet", new Gson().toJson(bulletObj));
                intent.putExtra("position", position);
                intent.putExtra("delete", "1");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void save() {
        if (progressLayout.getVisibility() != View.VISIBLE) {

            if (TextUtils.isEmpty(bullet.getText().toString().trim())) {
                error(getString(R.string.enter_bullet));
                return;
            }

            if (bulletObj != null) {
                bulletObj.setData("" + bullet.getText().toString().trim());
            }
            Intent intent = new Intent();
            intent.putExtra("bullet", new Gson().toJson(bulletObj));
            intent.putExtra("position", position);
            intent.putExtra("delete", "0");
            setResult(RESULT_OK, intent);
            finish();
        } else {
            error("Uploading...");
        }
    }

    private void gotoGallery() {
        if (PermissionChecker
                .checkSelfPermission(AddBulletActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                PermissionChecker
                        .checkSelfPermission(AddBulletActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            GalleryPicker.openGalleryOnlyPictures(AddBulletActivity.this, GlideEngine.createGlideEngine(), GlideCacheEngine.createCacheEngine(), new ArrayList<>(), PERMISSION_REQUEST);
        } else {
            PermissionChecker.requestPermissions(AddBulletActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
//        String[] mimeTypes = {"image/jpeg", "image/jpg"};
//        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                .setType("image/jpeg")
//                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        startActivityForResult(pickPhoto, PERMISSION_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST) {
//            if (data != null) {
//                if (data.getClipData() != null) {
//                    int count = data.getClipData().getItemCount();
//                    for (int i = 0; i < count; i++) {
//                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
//
//                        String selectedImagePath = getRealPathFromURI(imageUri);
//                        File file = new File(selectedImagePath);
//                        loadSelectedImage(requestCode, file, imageUri);
//                    }
//                } else if (data.getData() != null) {
//                    String selectedImagePath = getRealPathFromURI(data.getData());
//                    File file = new File(selectedImagePath);
//                    loadSelectedImage(requestCode, file, data.getData());
//                }
//            }
            if (resultCode == Activity.RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList != null && selectList.size() > 0 && selectList.get(0) != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        loadSelectedImage(selectList.get(0).getAndroidQToPath(), true);
                    } else {
                        loadSelectedImage(selectList.get(0).getPath(), true);
                    }
                }
            }
        }
    }

    private void loadSelectedImage(String path, boolean isUpload) {
        if (!TextUtils.isEmpty(path)) {
            menu.setVisibility(View.VISIBLE);
            Glide.with(BulletApp.getInstance())
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                    .override(Constants.targetWidth, Constants.targetHeight)
                    .into(imageBack);

            Glide.with(BulletApp.getInstance())
                    .load(path)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            image.setImageResource(R.drawable.img_place_holder);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }

                    })
                    .error(R.drawable.img_place_holder)
                    .placeholder(R.drawable.img_place_holder)
                    .override(Constants.targetWidth, Constants.targetHeight)
                    .into(image);

        } else {
//            image.setImageResource(R.drawable.img_place_holder);
        }
        if (isUpload) {
            if (presenter != null) {
                presenter.uploadImageVideo(new File(path), "images");
            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return "";
    }

    public boolean checkPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void loaderShow(boolean flag) {
        if (flag) {
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            progressLayout.setVisibility(View.GONE);
        }
    }

    public void error(String msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success(Object responseBody) {

    }

    @Override
    public void successDelete() {

    }

    @Override
    public void createSuccess(Article responseBody, String type) {

    }

    @Override
    public void createSuccess(ReelsItem responseBody, String type) {

    }

    @Override
    public void uploadSuccess(String url, String type) {
        progressLayout.setVisibility(View.GONE);
        if (bulletObj != null)
            bulletObj.setImage(url);
    }

    @Override
    public void proceedToUpload() {

    }

    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoGallery();
            }
        }
    }

}