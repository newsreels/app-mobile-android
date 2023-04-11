package com.ziro.bullet.activities.intro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ziro.bullet.R
import com.ziro.bullet.utills.Utils

/**
 *   Create by waxif
 *   Email: waxif.1@gmail.com
 *   02,February,2022
 */


class IntroScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setBlackStatusBarColor(this)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black);
        setContentView(R.layout.intro_slide_1)


    }
}