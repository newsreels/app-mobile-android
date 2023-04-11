package com.ziro.bullet.mediapicker.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.TranscoderListener;
import com.otaliastudios.transcoder.TranscoderOptions;
import com.otaliastudios.transcoder.sink.DataSink;
import com.otaliastudios.transcoder.sink.DefaultDataSink;
import com.otaliastudios.transcoder.source.ClipDataSource;
import com.otaliastudios.transcoder.source.FilePathDataSource;
import com.otaliastudios.transcoder.source.UriDataSource;
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy;
import com.video.trimmer.interfaces.OnTrimVideoListener;
import com.video.trimmer.interfaces.OnVideoListener;
import com.ziro.bullet.R;
import com.ziro.bullet.background.UploadInfoKt;
import com.ziro.bullet.background.VideoInfo;
import com.ziro.bullet.mediapicker.config.PictureConfig;
import com.ziro.bullet.mediapicker.config.PictureMimeType;
import com.ziro.bullet.mediapicker.config.PictureSelectionConfig;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.gallery.process.InstagramLoadingDialog;
import com.ziro.bullet.mediapicker.gallery.process.InstagramMediaProcessActivity;
import com.ziro.bullet.mediapicker.gallery.process.MyExactResizer;
import com.ziro.bullet.mediapicker.gallery.process.VideoTrimmer;
import com.ziro.bullet.mediapicker.gallery.process.VideoTrimmerReel;
import com.ziro.bullet.mediapicker.utils.DateUtils;
import com.ziro.bullet.mediapicker.utils.SdkVersionUtils;
import com.ziro.bullet.mediapicker.utils.ToastUtils;
import com.ziro.bullet.utills.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class VideoEditorActivity extends AppCompatActivity implements OnTrimVideoListener, OnVideoListener {
    private static final String TAG = "VideoEditorActivity";

    private LocalMedia mMedia;

    private InstagramLoadingDialog mLoadingDialog;
    private TextView tvAdd, tvCancel;
    private Future<Void> mTranscodeFuture;

    private PictureSelectionConfig mConfig;


    public static void launchActivity(Activity activity, PictureSelectionConfig config, List<LocalMedia> images, Bundle extras, int requestCode) {
        Intent intent = new Intent(activity.getApplicationContext(), VideoEditorActivity.class);
        intent.putExtra(PictureConfig.EXTRA_CONFIG, config);
        intent.putParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST,
                (ArrayList<? extends Parcelable>) images);
        if (extras != null) {
            intent.putExtras(extras);
        }
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_editor);
        Utils.setStatusBarColor(this);
        if (mMedia == null && getIntent() != null) {
            List<LocalMedia> mSelectMedia = getIntent().getParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST);
            if (mSelectMedia != null) {
                mMedia = mSelectMedia.get(0);
            }
        }

        if (mConfig == null) {
            mConfig = getIntent() != null ? getIntent().getParcelableExtra(PictureConfig.EXTRA_CONFIG) : mConfig;
        }

        if (mMedia == null)
            finish();

        String path = mMedia.getRealPath();
        Log.d("TAGss", "onCreate: " + path);

        tvCancel = findViewById(R.id.tvCancel);
        tvAdd = findViewById(R.id.tvAdd);

        if (mConfig.isReel) {
            VideoTrimmerReel videoTrimmer = new VideoTrimmerReel(this);
            RelativeLayout container = findViewById(R.id.container);
            container.addView(videoTrimmer);
            videoTrimmer
                    .setOnTrimVideoListener(this)
                    .setOnVideoListener(this)
                    .setVideoURI(Uri.parse(path), mMedia.getWidth(), mMedia.getHeight())
                    .setVideoInformationVisibility(true)
//                    .setMaxDuration((int) mMedia.getDuration())
                    .setMaxDuration(120 * 1000)
                    .setMinDuration(2000);

            tvAdd.setOnClickListener(v -> {
                videoTrimmer.onSaveClicked();
            });

            tvCancel.setOnClickListener(v -> {
                videoTrimmer.onCancelClicked();
            });
        } else {
            VideoTrimmer videoTrimmer = new VideoTrimmer(this);
            RelativeLayout container = findViewById(R.id.container);
            container.addView(videoTrimmer);

            videoTrimmer
                    .setOnTrimVideoListener(this)
                    .setOnVideoListener(this)
                    .setVideoURI(Uri.parse(path), mMedia.getWidth(), mMedia.getHeight())
                    .setVideoInformationVisibility(true)
                    .setMaxDuration((int) mMedia.getDuration())
                    .setMinDuration(2000);

            tvAdd.setOnClickListener(v -> {
                videoTrimmer.onSaveClicked();
            });

            tvCancel.setOnClickListener(v -> {
                videoTrimmer.onCancelClicked();
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTranscodeFuture != null) {
            mTranscodeFuture.cancel(true);
            mTranscodeFuture = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(InstagramMediaProcessActivity.RESULT_MEDIA_PROCESS_CANCELED);
    }

    @Override
    public void onTrimStarted(float mStartPosition, float mEndPosition, String mAspectRatio) {
        cropVideo(mStartPosition, mEndPosition, mAspectRatio);
        Log.d(TAG, "onTrimStarted() called with: mStartPosition = [" + mStartPosition + "], mEndPosition = [" + mEndPosition + "], mAspectRatio = [" + mAspectRatio + "]");
    }


    @Override
    public void getResult(@NotNull Uri uri) {
        Log.d(TAG, "getResult() called with: uri = [" + uri + "]");
    }

    @Override
    public void cancelAction() {
        onBackPressed();
    }

    @Override
    public void onError(@NotNull String message) {
        Log.d(TAG, "onError() called with: message = [" + message + "]");
    }

    @Override
    public void onVideoPrepared(int videoWidth, int videoHeight) {
        Log.d(TAG, "onVideoPrepared: ");
        mMedia.setWidth(videoWidth);
        mMedia.setHeight(videoHeight);
    }


    private void cropVideo(float mStartPosition, float mEndPosition, String mAspectRatio) {
        showLoadingView(true);

        Log.d("time", "cropVideo:  = duration " + mMedia.getDuration());

        Log.d("time", "cropVideo: mStartPosition = " + mStartPosition);
        Log.d("time", "cropVideo: mEndPosition = " + mEndPosition);

        if (mMedia.getDuration() < mEndPosition) {
            mEndPosition = mMedia.getDuration();
        }

        if (mStartPosition < 0) {
            mStartPosition = 0;
        }

        long startTime = (long) mStartPosition;
        long endTime = (long) mEndPosition;

        if (endTime - startTime < 1000) {
            showLoadingView(false);
            Utils.showSnacky(getWindow().getDecorView().getRootView(), getString(R.string.video_duration_limit));
            return;
        }

        Log.d("time", "cropVideo: startTime = " + startTime);
        Log.d("time", "cropVideo: endTime = " + endTime);

        long startTimeUS = (long) (mStartPosition * 1000);
        long endTimeUS = (long) (mEndPosition * 1000);

        Log.d("time", "cropVideo: startTimeUS = " + startTimeUS);
        Log.d("time", "cropVideo: endTimeUS = " + endTimeUS);

//        File transcodeOutputFile;
//        try {
//            File outputDir = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "TrimVideos");
//            //noinspection ResultOfMethodCallIgnored
//            outputDir.mkdir();
//            transcodeOutputFile = File.createTempFile(DateUtils.getCreateFileName("trim_"), ".mp4", outputDir);
//        } catch (IOException e) {
//            ToastUtils.s(this, "Failed to create temporary file.");
//            return;
//        }


        int width, height;


        int orgWidth = mMedia.getWidth();
        int orgHeight = mMedia.getHeight();
        float videoProportion = (float) orgWidth / orgHeight;


        if (!mConfig.isReel) {
            //0.5625

//            x/480 = 0.5625

            if (mAspectRatio.equals("169")) {
                if (orgWidth >= 1280 && orgHeight >= 720) {
                    height = 720;
                    width = 1280;
                } else if (orgWidth >= 960 && orgHeight >= 540) {
                    height = 540;
                    width = 960;
                } else if (orgWidth >= 640 && orgHeight >= 360) {
                    height = 360;
                    width = 640;
                } else if (orgWidth >= 480 && orgHeight >= 270) {
                    height = 270;
                    width = 480;
                } else if (orgWidth >= 240 && orgHeight >= 135) {
                    height = 180;
                    width = 320;
                } else {
                    height = 72;
                    width = 128;
                }
            } else if (mAspectRatio.equals("11")) {
                if (orgWidth >= 720 && orgHeight >= 720) {
                    height = 720;
                    width = 720;
                } else if (orgWidth > orgHeight) {
                    height = orgHeight;
                    width = orgHeight;
                } else {
                    height = orgWidth;
                    width = orgWidth;
                }
            } else {
                if (orgWidth >= 768 && orgHeight >= 1024) {
                    height = 1024;
                    width = 768;
                } else if (orgWidth >= 540 && orgHeight >= 720) {
                    height = 720;
                    width = 540;
                } else if (orgWidth >= 360 && orgHeight >= 480) {
                    height = 480;
                    width = 360;
                } else if (orgWidth >= 180 && orgHeight >= 240) {
                    height = 240;
                    width = 180;
                } else {
                    height = 144;
                    width = 108;
                }
            }
        } else {
            if (mAspectRatio.equals("scaled")) {
                if (orgHeight >= 1280 && orgWidth >= 720) {
                    height = 1280;
                    width = 720;
                } else if (orgHeight >= 960 && orgWidth >= 540) {
                    height = 960;
                    width = 540;
                } else {
                    height = 640;
                    width = 360;
                }
            } else {
//                if (orgHeight < 1000 && orgWidth < 100) {
                height = orgHeight;
                width = orgWidth;
//                } else {
//                    height = orgHeight / 2;
//                    width = orgWidth / 2;
//                }
            }
        }

        Log.d("resolution", "cropVideo: orgWidth = " + orgWidth);
        Log.d("resolution", "cropVideo: orgHeight = " + orgHeight);

        Log.d("resolution", "cropVideo: width = " + width);
        Log.d("resolution", "cropVideo: height = " + height);

        String videoRatio;

        if (mConfig.isReel) {
            if (mAspectRatio.equals("scaled")) {
                videoRatio = UploadInfoKt.VIDEO_RATIO_SCALED;
            } else {
                videoRatio = UploadInfoKt.VIDEO_RATIO_DEFAULT;
            }
        } else {
            if (mAspectRatio.equals("169")) {
                videoRatio = UploadInfoKt.VIDEO_RATIO_169;
            } else if (mAspectRatio.equals("11")) {
                videoRatio = UploadInfoKt.VIDEO_RATIO_11;
            } else {
                videoRatio = UploadInfoKt.VIDEO_RATIO_34;
            }
        }

        VideoInfo videoInfo = new VideoInfo(
                mMedia.getRealPath(),
                width,
                height,
                startTimeUS,
                endTimeUS,
                videoRatio
        );

        setResult(Activity.RESULT_OK, new Intent().putExtra("video_info", videoInfo));
        finish();

//        DefaultVideoStrategy videoStrategy = new DefaultVideoStrategy.Builder()
//                .addResizer(new MyExactResizer(width, height)) //16: 9
////                .addResizer(new MyExactResizer(720,720)) //1: 1
////                .addResizer(new FractionResizer(1F))
//                .build();

//        File transcodeOutputFile;
//        try {
//            File outputDir = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "TrimVideos");
//            //noinspection ResultOfMethodCallIgnored
//            outputDir.mkdir();
//            transcodeOutputFile = File.createTempFile(DateUtils.getCreateFileName("trim_"), ".mp4", outputDir);
//        } catch (IOException e) {
//            ToastUtils.s(this, "Failed to create temporary file.");
//            return;
//        }
//
//        DataSink sink = new DefaultDataSink(transcodeOutputFile.getAbsolutePath());
//        TranscoderOptions.Builder builder = Transcoder.into(sink);
//        if (PictureMimeType.isContent(mMedia.getPath())) {
//            builder.addDataSource(new ClipDataSource(new UriDataSource(this, Uri.parse(mMedia.getPath())), startTimeUS, endTimeUS));
//        } else {
//            builder.addDataSource(new ClipDataSource(new FilePathDataSource(mMedia.getPath()), startTimeUS, endTimeUS));
//        }
//        mTranscodeFuture = builder.setListener(new TranscoderListenerImpl(this, startTime, endTime, transcodeOutputFile))
//                .setVideoTrackStrategy(videoStrategy)
//                .transcode();
    }

    private void showLoadingView(boolean isShow) {
        if (isFinishing()) {
            return;
        }
        if (isShow) {
            if (mLoadingDialog == null) {
                mLoadingDialog = new InstagramLoadingDialog(this);
                mLoadingDialog.setOnCancelListener(dialog -> {
                    if (mTranscodeFuture != null) {
                        mTranscodeFuture.cancel(true);
                    }
                });
            }
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
            mLoadingDialog.updateProgress(0);
            mLoadingDialog.show();
        } else {
            if (mLoadingDialog != null
                    && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }

    private static class TranscoderListenerImpl implements TranscoderListener {
        private WeakReference<VideoEditorActivity> mActivityWeakReference;
        private long mStartTime;
        private long mEndTime;
        private File mTranscodeOutputFile;

        public TranscoderListenerImpl(VideoEditorActivity activity, long startTime, long endTime, File transcodeOutputFile) {
            mActivityWeakReference = new WeakReference<>(activity);
            mStartTime = startTime;
            mEndTime = endTime;
            mTranscodeOutputFile = transcodeOutputFile;
        }

        @Override
        public void onTranscodeProgress(double progress) {
            VideoEditorActivity activity = mActivityWeakReference.get();
            if (activity == null) {
                return;
            }
            if (activity.mLoadingDialog != null
                    && activity.mLoadingDialog.isShowing()) {
                activity.mLoadingDialog.updateProgress(progress);
            }
        }

        @Override
        public void onTranscodeCompleted(int successCode) {
            Log.d(TAG, "onTranscodeCompleted() called with: successCode = [" + successCode + "]");
            VideoEditorActivity activity = mActivityWeakReference.get();

            Log.d(TAG, "onTranscodeCompleted: "+Utils.isFileLessThanMB(100, mTranscodeOutputFile));

            if (!Utils.isFileLessThanMB(100, mTranscodeOutputFile)) {
                if (activity != null)
                    Utils.showSnacky(activity.getWindow().getDecorView().getRootView(), activity.getString(R.string.file_upload_size_error));
                return;
            }

            String s = SdkVersionUtils.checkedAndroid_Q() ? mTranscodeOutputFile.getAbsolutePath() : activity.mMedia.getAndroidQToPath();
            Log.d(TAG, "onTranscodeCompleted: " + s);
            if (activity == null) {
                return;
            }
            if (successCode == Transcoder.SUCCESS_TRANSCODED) {
//                activity.mMedia.setSize(mTranscodeOutputFile.length());
//                activity.mMedia.setDuration(mEndTime - mStartTime);
//                activity.mMedia.setPath(mTranscodeOutputFile.getAbsolutePath());
//                activity.mMedia.setAndroidQToPath(SdkVersionUtils.checkedAndroid_Q() ? mTranscodeOutputFile.getAbsolutePath() : activity.mMedia.getAndroidQToPath());
//                List<LocalMedia> list = new ArrayList<>();
//                list.add(activity.mMedia);
//                activity.setResult(Activity.RESULT_OK, new Intent().putParcelableArrayListExtra(PictureConfig.EXTRA_SELECT_LIST, (ArrayList<? extends Parcelable>) list));
//                activity.finish();
            } else if (successCode == Transcoder.SUCCESS_NOT_NEEDED) {

            }
            activity.showLoadingView(false);
        }

        @Override
        public void onTranscodeCanceled() {
            VideoEditorActivity activity = mActivityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.showLoadingView(false);
        }

        @Override
        public void onTranscodeFailed(@NonNull Throwable exception) {
            VideoEditorActivity activity = mActivityWeakReference.get();
            if (activity == null) {
                return;
            }
            exception.printStackTrace();
            Utils.showSnacky(activity.getWindow().getDecorView().getRootView(), activity.getString(R.string.video_clip_failed));
            activity.showLoadingView(false);
        }
    }
}
