package com.ziro.bullet.mediapicker.gallery.process;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.ziro.bullet.R;
import com.ziro.bullet.mediapicker.config.PictureMimeType;
import com.ziro.bullet.mediapicker.config.PictureSelectionConfig;
import com.ziro.bullet.mediapicker.entity.LocalMedia;
import com.ziro.bullet.mediapicker.gallery.AnimatorListenerImpl;
import com.ziro.bullet.mediapicker.gallery.InstagramPreviewContainer;
import com.ziro.bullet.mediapicker.gallery.InstagramViewPager;
import com.ziro.bullet.mediapicker.gallery.OnPageChangeListener;
import com.ziro.bullet.mediapicker.gallery.Page;
import com.ziro.bullet.mediapicker.utils.SdkVersionUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.ziro.bullet.mediapicker.gallery.InstagramSelectionConfig.THEME_STYLE_DARK;

/**
 * ================================================
 * Created by JessYan on 2020/6/1 15:27
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class InstagramMediaSingleVideoContainer extends FrameLayout implements ProcessStateCallBack, LifecycleCallBack {
    private FrameLayout mTopContainer;
    private VideoView mVideoView;
    private boolean isAspectRatio;
    private ImageView mThumbView;
    private ImageView mPlayButton;
    private InstagramViewPager mInstagramViewPager;
    private MediaPlayer mMediaPlayer;
    private PictureSelectionConfig mConfig;
    private LocalMedia mMedia;
    private boolean isStart;
    private ObjectAnimator mPlayAnimator;
    private boolean isFrist;
    private boolean isVolumeOff;
    private List<Page> mList;
    private int mCoverPlayPosition;
    private boolean isPlay;
    private boolean needPause;
    private boolean needSeekCover;


    public InstagramMediaSingleVideoContainer(@NonNull Context context, PictureSelectionConfig config, LocalMedia media, boolean isAspectRatio) {
        super(context);
        mConfig = config;
        mMedia = media;
        this.isAspectRatio = isAspectRatio;

        mTopContainer = new FrameLayout(context);
        mTopContainer.setBackgroundColor(ContextCompat.getColor(context,R.color.home_bg));

        addView(mTopContainer);

        mVideoView = new VideoView(context);
        mTopContainer.addView(mVideoView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        mVideoView.setVisibility(View.GONE);

        if (SdkVersionUtils.checkedAndroid_Q() && PictureMimeType.isContent(media.getPath())) {
            mVideoView.setVideoURI(Uri.parse(media.getPath()));
        } else {
            mVideoView.setVideoPath(media.getPath());
        }

        mVideoView.setOnClickListener((v -> startVideo(!isStart)));

        mVideoView.setOnPreparedListener(mp -> {
            mMediaPlayer = mp;
            mp.setLooping(true);
            changeVideoSize(mp, isAspectRatio);
            mp.setOnInfoListener((mp1, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // video started
                    isPlay = true;
                    if (needSeekCover && mCoverPlayPosition >= 0) {
                        mVideoView.seekTo(mCoverPlayPosition);
                        mCoverPlayPosition = -1;
                        needSeekCover = false;
                    }
                    if (needPause) {
                        mVideoView.pause();
                        needPause = false;
                    }
                    if (mThumbView.getVisibility() == VISIBLE) {
                        ObjectAnimator.ofFloat(mThumbView, "alpha", 1.0f, 0).setDuration(400).start();
                    }
                    return true;
                }
                return false;
            });
        });

        mThumbView = new ImageView(context);
        mTopContainer.addView(mThumbView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mThumbView.setOnClickListener(v -> {
            if (mInstagramViewPager.getSelectedPosition() == 0) {
                startVideo(!isStart);
            }
        });

        mPlayButton = new ImageView(context);
        mPlayButton.setImageResource(R.drawable.icon_video_play);
        mPlayButton.setOnClickListener(v -> startVideo(!isStart));
        mTopContainer.addView(mPlayButton, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        mList = new ArrayList<>();
        mList.add(new PageTrim(config, media, mVideoView, isAspectRatio, new TrimContainer.VideoPauseListener() {
            @Override
            public void onChange() {
                if (!isFrist) {
                    startVideo(true);
                }
            }

            @Override
            public void onVideoPause() {
                if (isPlay) {
                    startVideo(false);
                }
            }
        }));

//        if (config.instagramSelectionConfig.haveCover()) {
//            mList.add(new PageCover(config, media));
//            ((PageCover) mList.get(1)).setOnSeekListener(new CoverContainer.onSeekListener() {
//                @Override
//                public void onSeek(float percent) {
//                    if (!isFrist) {
//                        startVideo(true);
//                    }
//                    mVideoView.seekTo((int) (mMedia.getDuration() * percent));
//                }
//
//                @Override
//                public void onSeekEnd() {
//                    needPause = true;
//                    if (isStart && isPlay) {
//                        startVideo(false);
//                    }
//                    mPlayButton.setVisibility(GONE);
//                }
//            });
//        }

        mInstagramViewPager = new InstagramViewPager(getContext(), mList, config);
        mInstagramViewPager.displayTabLayout(false);
        mInstagramViewPager.setScrollEnable(false);
        addView(mInstagramViewPager);
        mInstagramViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    if (isStart) {
                        startVideo(false);
                    }
                    mPlayButton.setVisibility(GONE);
                    if (mCoverPlayPosition >= 0) {
                        mVideoView.seekTo(mCoverPlayPosition);
                        mCoverPlayPosition = -1;
                    }
                } else if (position == 0) {
                    mCoverPlayPosition = mVideoView.getCurrentPosition();
                    ((PageTrim) mList.get(0)).resetStartLine();
                    mVideoView.seekTo((int) ((PageTrim) mList.get(0)).getStartTime());
                    if (!isStart) {
                        mPlayButton.setVisibility(VISIBLE);
                    }
                }
            }
        });

        Glide.with(context).load(media.getPath()).into(mThumbView);

//        new getFrameBitmapTask(context, media, isAspectRatio, -1, new OnCompleteListenerImpl(mThumbView)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void startVideo(boolean start) {
        if (isStart == start) {
            return;
        }

        if (isFrist && start && mInstagramViewPager.getSelectedPosition() == 1) {
            return;
        }

        isStart = start;

        if (!isFrist) {
            isFrist = true;
            mVideoView.setVisibility(View.VISIBLE);
        }

        if (mInstagramViewPager.getSelectedPosition() == 0 || mInstagramViewPager.getSelectedPosition() == 1 && !start) {
            ((PageTrim) mList.get(0)).playVideo(start, mVideoView);
        }

        if (mPlayAnimator != null && mPlayAnimator.isRunning()) {
            mPlayAnimator.cancel();
        }
        if (!start) {
            mPlayButton.setVisibility(VISIBLE);
            mPlayAnimator = ObjectAnimator.ofFloat(mPlayButton, "alpha", 0, 1.0f).setDuration(200);
            mVideoView.pause();
        } else {
            mPlayAnimator = ObjectAnimator.ofFloat(mPlayButton, "alpha", 1.0f, 0).setDuration(200);
            mPlayAnimator.addListener(new AnimatorListenerImpl() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPlayButton.setVisibility(GONE);
                }
            });
            mVideoView.start();
        }
        mPlayAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mTopContainer.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY));
        mInstagramViewPager.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height - width, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int viewTop = 0;
        int viewLeft = 0;
        mTopContainer.layout(viewLeft, viewTop, viewLeft + mTopContainer.getMeasuredWidth(), viewTop + mTopContainer.getMeasuredHeight());

        viewTop = mTopContainer.getMeasuredHeight();
        mInstagramViewPager.layout(viewLeft, viewTop, viewLeft + mInstagramViewPager.getMeasuredWidth(), viewTop + mInstagramViewPager.getMeasuredHeight());
    }

    @Override
    public void onBack(InstagramMediaProcessActivity activity) {
        activity.setResult(InstagramMediaProcessActivity.RESULT_MEDIA_PROCESS_CANCELED);
        activity.finish();
    }

    @Override
    public void onCenterFeature(InstagramMediaProcessActivity activity, ImageView view) {
    }

    @Override
    public void onProcess(InstagramMediaProcessActivity activity) {
        int c = 1;
        if (mConfig.instagramSelectionConfig.haveCover()) {
            c++;
        }
        CountDownLatch count = new CountDownLatch(c);
        ((PageTrim) mList.get(0)).trimVideo(activity, count);
//        if (mConfig.instagramSelectionConfig.haveCover()) {
//            ((PageCover) mList.get(1)).cropCover(count);
//        }
    }

    @Override
    public void onActivityResult(InstagramMediaProcessActivity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onStart(InstagramMediaProcessActivity activity) {

    }

    @Override
    public void onResume(InstagramMediaProcessActivity activity) {
        if (mInstagramViewPager.getSelectedPosition() == 0) {
            mThumbView.setVisibility(VISIBLE);
            mThumbView.setAlpha(1f);
            mPlayButton.setVisibility(VISIBLE);
            mPlayButton.setAlpha(1f);
        } else if (mInstagramViewPager.getSelectedPosition() == 1) {
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            }
            needPause = true;
            needSeekCover = true;
        }
        isStart = false;
        if (mInstagramViewPager != null) {
            mInstagramViewPager.onResume();
        }
    }

    @Override
    public void onPause(InstagramMediaProcessActivity activity) {
        if (mInstagramViewPager.getSelectedPosition() == 1) {
            mCoverPlayPosition = mVideoView.getCurrentPosition();
        }
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
        if (mInstagramViewPager != null) {
            mInstagramViewPager.onPause();
        }
        isPlay = false;
        needPause = false;
    }

    @Override
    public void onDestroy(InstagramMediaProcessActivity activity) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mInstagramViewPager != null) {
            mInstagramViewPager.onDestroy();
            mInstagramViewPager = null;
        }
        mVideoView = null;
    }

    public void changeVideoSize(MediaPlayer mediaPlayer, boolean isAspectRatio) {
        if (mediaPlayer == null || mVideoView == null) {
            return;
        }
        try {
            mediaPlayer.getVideoWidth();
        } catch (Exception e) {
            return;
        }
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        int parentWidth = getMeasuredWidth();
        int parentHeight = getMeasuredWidth();

        float instagramAspectRatio = InstagramPreviewContainer.getInstagramAspectRatio(videoWidth, videoHeight);
        float targetAspectRatio = videoWidth * 1.0f / videoHeight;

        int height = (int) (parentWidth / targetAspectRatio);

        int adjustWidth;
        int adjustHeight;
        if (isAspectRatio) {
            if (height > parentHeight) {
                adjustWidth = (int) (parentWidth * (instagramAspectRatio > 0 ? instagramAspectRatio : targetAspectRatio));
                adjustHeight = height;
            } else {
                if (instagramAspectRatio > 0) {
                    adjustWidth = (int) (parentHeight * targetAspectRatio);
                    adjustHeight = (int) (parentHeight / instagramAspectRatio);
                } else {
                    adjustWidth = parentWidth;
                    adjustHeight = height;
                }
            }
        } else {
            if (height < parentHeight) {
                adjustWidth = (int) (parentHeight * targetAspectRatio);
                adjustHeight = parentHeight;
            } else {
                adjustWidth = parentWidth;
                adjustHeight = height;
            }
        }

        LayoutParams layoutParams = (LayoutParams) mVideoView.getLayoutParams();
        layoutParams.width = adjustWidth;
        layoutParams.height = adjustHeight;
        mVideoView.setLayoutParams(layoutParams);
    }

    public static class OnCompleteListenerImpl implements getFrameBitmapTask.OnCompleteListener {
        private WeakReference<ImageView> mImageViewWeakReference;

        public OnCompleteListenerImpl(ImageView imageView) {
            mImageViewWeakReference = new WeakReference<>(imageView);
        }

        @Override
        public void onGetBitmapComplete(Bitmap bitmap) {
            ImageView imageView = mImageViewWeakReference.get();
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
