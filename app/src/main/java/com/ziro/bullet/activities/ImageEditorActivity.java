package com.ziro.bullet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.util.MimeType;
import com.yalantis.ucrop.util.ScreenUtils;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;
import com.ziro.bullet.R;
import com.ziro.bullet.mediapicker.ImageCrop;
import com.ziro.bullet.utills.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ImageEditorActivity extends BaseActivity {
    public static final int DEFAULT_COMPRESS_QUALITY = 80;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;
    private static final String TAG = "ImageEditorActivity";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected float frameAspectRatio = 1.77f;
    protected boolean hideControls = false;
    protected int mScreenWidth;

    protected RelativeLayout uCropPhotoBox;
    protected View mBlockingView;

    private boolean mShowLoader = true;
    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;

    private RelativeLayout rlAspect_169, rlAspect_916, rlAspect_11;
    private RelativeLayout rlRotate, rlFlipVertical, rlFlipHorizontal;
    private TextView tvNext;
    private RelativeLayout rlBack;
    private TextView title;
    private LinearLayout llAspectRatioContainer, llModifyContainer;

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
        }

        @Override
        public void onScale(float currentScale) {
        }

        @Override
        public void onBitmapLoadComplete(@NonNull Bitmap bitmap) {

        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            mBlockingView.setClickable(!isOnTouch());
            mShowLoader = false;
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            setResultError(e);
            onBackPressed();
        }

    };

    @Override
    public boolean isImmersive() {
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        Utils.checkAppModeColor(this, false);
        setContentView(R.layout.activity_image_editor);
        mScreenWidth = ScreenUtils.getScreenWidth(this);
        setupViews();
        setImageData(intent);
        setInitialState();
        addBlockingView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == com.yalantis.ucrop.R.id.menu_crop) {
            cropAndSaveImage();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    /**
     * This method extracts all data from the incoming intent and setups views properly.
     */
    protected void setImageData(@NonNull Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        Uri outputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
        frameAspectRatio = intent.getFloatExtra(ImageCrop.EXTRA_INPUT_FRAME_ASPECT_RATIO, 1.77f);
        hideControls = intent.getBooleanExtra(ImageCrop.EXTRA_INPUT_HIDE_CONTROLS, false);
        processOptions(intent);

        if (inputUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setRotateEnabled(true);
                mGestureCropImageView.setScaleEnabled(true);
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                onBackPressed();
            }
        } else {
            onBackPressed();
        }
    }

    /**
     * 是否可以触摸
     *
     * @return
     */
    private boolean isOnTouch() {
        Uri inputUri = getIntent().getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        if (inputUri == null) {
            return true;
        }
        return isOnTouch(inputUri);
    }

    /**
     * 是否可以触摸
     *
     * @param inputUri
     * @return
     */
    private boolean isOnTouch(Uri inputUri) {
        if (inputUri == null) {
            return true;
        }
        boolean isHttp = MimeType.isHttp(inputUri.toString());
        if (isHttp) {
            // 网络图片
            String lastImgType = MimeType.getLastImgType(inputUri.toString());
            return !MimeType.isGifForSuffix(lastImgType);
        } else {
            String mimeType = MimeType.getMimeTypeFromMediaContentUri(this, inputUri);
            if (mimeType.endsWith("image/*")) {
                String path = FileUtils.getPath(this, inputUri);
                mimeType = MimeType.getImageMimeType(path);
            }
            return !MimeType.isGif(mimeType);
        }
    }

    /**
     * This method extracts {@link com.yalantis.ucrop.UCrop.Options #optionsBundle} from incoming intent
     * and setups Activity, {@link OverlayView} and {@link CropImageView} properly.
     */
    @SuppressWarnings("deprecation")
    private void processOptions(@NonNull Intent intent) {

        mOverlayView.setDimmedStrokeWidth(1);


        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(CropImageView.DEFAULT_MAX_BITMAP_SIZE);
        mGestureCropImageView.setMaxScaleMultiplier(CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER);
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION);

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(false);
        mOverlayView.setDragFrame(true);
        mOverlayView.setDimmedColor(getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_dimmed));
        mOverlayView.setCircleDimmedLayer(OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER);

        mOverlayView.setShowCropFrame(OverlayView.DEFAULT_SHOW_CROP_FRAME);
        mOverlayView.setCropFrameColor(getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_crop_frame));
        mOverlayView.setCropFrameStrokeWidth(getResources().getDimensionPixelSize(com.yalantis.ucrop.R.dimen.ucrop_default_crop_frame_stoke_width));

        mOverlayView.setShowCropGrid(OverlayView.DEFAULT_SHOW_CROP_GRID);
        mOverlayView.setCropGridRowCount(OverlayView.DEFAULT_CROP_GRID_ROW_COUNT);
        mOverlayView.setCropGridColumnCount(OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT);
        mOverlayView.setCropGridColor(getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_crop_grid));
        mOverlayView.setCropGridStrokeWidth(getResources().getDimensionPixelSize(com.yalantis.ucrop.R.dimen.ucrop_default_crop_grid_stoke_width));

    }

    protected void setupViews() {
        mUCropView = findViewById(R.id.ucrop);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();
        tvNext = findViewById(R.id.tvNext);
        rlBack = findViewById(R.id.ivBack);
        title = findViewById(R.id.title);
        llAspectRatioContainer = findViewById(R.id.llAspectRatioContainer);
        llModifyContainer = findViewById(R.id.llModifyContainer);

        mGestureCropImageView.setTransformImageListener(mImageListener);
        rlAspect_11 = findViewById(R.id.rl_aspect_11);
        rlAspect_169 = findViewById(R.id.rl_aspect_169);
        rlAspect_916 = findViewById(R.id.rl_aspect_916);

        rlRotate = findViewById(R.id.rl_rotate);
        rlFlipVertical = findViewById(R.id.rl_flip_vertical);
        rlFlipHorizontal = findViewById(R.id.rl_flip_horizontal);

        rlAspect_11.setOnClickListener(v -> {
            mGestureCropImageView.setTargetAspectRatio(1f);
            mGestureCropImageView.setImageToWrapCropBounds();
        });
        rlAspect_169.setOnClickListener(v -> {
            mGestureCropImageView.setTargetAspectRatio(1.77f);
            mGestureCropImageView.setImageToWrapCropBounds();
        });
        rlAspect_916.setOnClickListener(v -> {
            mGestureCropImageView.setTargetAspectRatio(0.56f);
            mGestureCropImageView.setImageToWrapCropBounds();
        });
        rlRotate.setOnClickListener(v -> {
            mGestureCropImageView.postRotate(-90);
            mGestureCropImageView.setImageToWrapCropBounds();
        });
        rlFlipVertical.setOnClickListener(v -> {
            mGestureCropImageView.setScaleY(mGestureCropImageView.getScaleY() == -1 ? 1 : -1);
            mGestureCropImageView.setImageToWrapCropBounds();
        });
        rlFlipHorizontal.setOnClickListener(v -> {
            mGestureCropImageView.setScaleX(mGestureCropImageView.getScaleX() == -1 ? 1 : -1);
            mGestureCropImageView.setImageToWrapCropBounds();
        });
        tvNext.setOnClickListener(v -> cropAndSaveImage());
        rlBack.setOnClickListener(v -> onBackPressed());
    }

    protected void setInitialState() {
        mGestureCropImageView.setTargetAspectRatio(frameAspectRatio);
        mGestureCropImageView.setImageToWrapCropBounds();
        if (hideControls) {
            title.setVisibility(View.GONE);
            llAspectRatioContainer.setVisibility(View.GONE);
            llModifyContainer.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
            llAspectRatioContainer.setVisibility(View.VISIBLE);
            llModifyContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    protected void addBlockingView() {
        if (mBlockingView == null) {
            mBlockingView = new View(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            mBlockingView.setLayoutParams(lp);
            mBlockingView.setClickable(true);
        }

        ((RelativeLayout) findViewById(R.id.ucrop_photobox)).addView(mBlockingView);
    }

    protected void cropAndSaveImage() {
        mBlockingView.setClickable(true);
        mShowLoader = true;
        supportInvalidateOptionsMenu();

        mGestureCropImageView.cropAndSaveImage(DEFAULT_COMPRESS_FORMAT, DEFAULT_COMPRESS_QUALITY, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                Log.d(TAG, "onBitmapCropped() called with: resultUri = [" + resultUri + "], offsetX = [" + offsetX + "], offsetY = [" + offsetY + "], imageWidth = [" + imageWidth + "], imageHeight = [" + imageHeight + "]");
                setResultUri(resultUri, mGestureCropImageView.getTargetAspectRatio(), offsetX, offsetY, imageWidth, imageHeight);
                Activity currentActivity = getCurrentActivity();
//                if (currentActivity instanceof PictureMultiCuttingActivity) {
//                } else {
                onBackPressed();
//                }
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                Log.d(TAG, "onCropFailure() called with: t = [" + t + "]");
                setResultError(t);
                onBackPressed();
            }
        });
    }

    protected void setResultUri(Uri uri, float resultAspectRatio, int offsetX, int offsetY, int imageWidth, int imageHeight) {
        setResult(RESULT_OK, new Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_X, offsetX)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y, offsetY)
        );
    }

    protected void setResultError(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }

    protected Activity getCurrentActivity() {
        return this;
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    /**
     * exit activity
     */
    protected void closeActivity() {
        finish();
        exitAnimation();
    }

    protected void exitAnimation() {
        int exitAnimation = getIntent().getIntExtra(UCrop.Options.EXTRA_WINDOW_EXIT_ANIMATION, 0);
        overridePendingTransition(com.yalantis.ucrop.R.anim.ucrop_anim_fade_in, exitAnimation != 0 ? exitAnimation : com.yalantis.ucrop.R.anim.ucrop_close);
    }

    @IntDef({NONE, SCALE, ROTATE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureTypes {

    }
}