package com.ziro.bullet.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ziro.bullet.R
import com.ziro.bullet.activities.onboarding.OnboardingDetailsActivity
import com.ziro.bullet.activities.onboarding.OnboardingDetailsActivity.Companion.startActivity
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.activity_post_settings.*
import kotlinx.android.synthetic.main.hashtag.*

class PostSettingsActivity : BaseActivity(), View.OnClickListener {

    private var mPrefConfig: PrefConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBarColor(this)
        setContentView(R.layout.activity_post_settings)
        init()
        setClickListeners()
        setData()
    }

    override fun onResume() {
        super.onResume()
        when (mPrefConfig?.textSize?.id) {
            0 -> hash.text = getString(R.string.small)
            1 -> hash.text = getString(R.string.defaultt)
            2 -> hash.text = getString(R.string.medium)
            3 -> hash.text = getString(R.string.large)
            else -> hash.text = getString(R.string.defaultt)
        }
    }

    private fun init() {
        mPrefConfig = PrefConfig(this)
    }

    private fun setData() {
        haptic.isChecked = mPrefConfig?.isHaptic ?: false
        bullet_auto_play.isChecked = mPrefConfig?.isBulletsAutoPlay ?: false
        video_auto_play.isChecked = mPrefConfig?.isVideoAutoPlay ?: false
        reels_auto_play.isChecked = mPrefConfig?.isReelsAutoPlay ?: false
        reader_mode.isChecked = mPrefConfig?.isReaderMode ?: false
    }

    private fun setClickListeners() {
        back.setOnClickListener(this)
        post_language.setOnClickListener(this)
        audio_settings.setOnClickListener(this)
        article_text_size.setOnClickListener(this)
        haptic.setOnCheckedChangeListener { _, flag -> mPrefConfig?.isHaptic = flag }
        bullet_auto_play.setOnCheckedChangeListener { _, flag ->
            mPrefConfig?.isBulletsAutoPlay = flag
        }
        video_auto_play.setOnCheckedChangeListener { _, flag ->
            mPrefConfig?.isVideoAutoPlay = flag
        }
        reels_auto_play.setOnCheckedChangeListener { _, flag ->
            mPrefConfig?.isReelsAutoPlay = flag
        }
        reader_mode.setOnCheckedChangeListener { _, flag -> mPrefConfig?.isReaderMode = flag }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.back -> {
                onBackPressed()
            }
            R.id.post_language -> {
                startActivity(this, OnboardingDetailsActivity.UPDATE_CONTENT_LANGUAGE)
            }
            R.id.audio_settings -> {
                startActivity(Intent(this, AudioSettingsActivity::class.java))
            }
            R.id.article_text_size -> {
                startActivity(Intent(this, FontSizeActivity::class.java))
            }
        }
    }
}