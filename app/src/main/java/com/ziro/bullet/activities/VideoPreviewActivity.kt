package com.ziro.bullet.activities

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.ziro.bullet.R
import com.ziro.bullet.background.VideoInfo
import com.ziro.bullet.mediapicker.gallery.VideoPreview
import com.ziro.bullet.mediapicker.gallery.process.VideoTrimmer
import com.ziro.bullet.mediapicker.utils.PictureFileUtils
import com.ziro.bullet.mediapicker.utils.SdkVersionUtils
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.activity_video_preview.*
import kotlinx.android.synthetic.main.view_trimmer.view.*

class VideoPreviewActivity : BaseActivity() {

    private lateinit var videoInfo: VideoInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Utils.setStatusBarColor(this)
        setContentView(R.layout.activity_video_preview)

        ivBack.setOnClickListener { onBackPressed() }

        val containsKey = intent.extras?.containsKey("video_info")
        if (containsKey == true) {
            videoInfo = intent.extras?.getParcelable("video_info")!!

            Log.d("TAGss", "onCreate: " + videoInfo.path)
            // 如原图路径不存在或者路径存在但文件不存在
            val newPath = if (SdkVersionUtils.checkedAndroid_Q()) PictureFileUtils.getPath(
                this,
                Uri.parse(videoInfo.path)
            ) else videoInfo.path

            val videoTrimmer = VideoPreview(this)
            val container = findViewById<RelativeLayout>(R.id.container)
            container.addView(videoTrimmer)

            videoTrimmer
                .setVideoRatio(videoInfo.videoRatio)
                .setVideoURI(Uri.parse(videoInfo.path))
                .setMaxDuration(40000 as Int)
                .setMinDuration(2000)
        }
    }
}