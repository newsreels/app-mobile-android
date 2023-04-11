package com.ziro.bullet.mediapicker.gallery

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.VideoView

class FixedSizeVideoView : VideoView {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    private var mVideoWidth = 0
    private var mVideoHeight = 0

    fun setVideoSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
    }

    // rather than shrink down to fit, stay at the size requested by layout params. Let the scaling mode
    // of the media player shine through. If the scaling mode on the media player is set to the one
    // with cropping, you can make a player similar to AVLayerVideoGravityResizeAspectFill on iOS
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        try {
            var width = getDefaultSize(mVideoWidth, widthMeasureSpec)
            var height = getDefaultSize(mVideoHeight, heightMeasureSpec)
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (mVideoWidth * height > width * mVideoHeight) {
                    // Log.i("@@@", "image too tall, correcting");
                    height = width * (mVideoHeight / mVideoWidth)
                    width = height * (mVideoWidth / mVideoHeight)
                } else if (mVideoWidth * height < width * mVideoHeight) {
                    // Log.i("@@@", "image too wide, correcting");
                    height = width * (mVideoHeight / mVideoWidth)
                    width = height * (mVideoWidth / mVideoHeight)
                } else {
                    // Log.i("@@@", "aspect ratio is correct: " +
                    // width+"/"+height+"="+
                    // mVideoWidth+"/"+mVideoHeight);
                }
            }

            val width1 = View.getDefaultSize(width, widthMeasureSpec)
            val height1 = View.getDefaultSize(height, heightMeasureSpec)
            setMeasuredDimension(width1, height1)
        }
        catch (ex: Exception) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}