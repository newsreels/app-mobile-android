package com.ziro.bullet.activities.intro

import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.push.Push
import com.ziro.bullet.interfaces.PushNotificationInterface
import com.ziro.bullet.presenter.PushNotificationPresenter
import com.ziro.bullet.utills.Utils


class EnableNotificationActivity : AppCompatActivity(), PushNotificationInterface {

    companion object {
        private val FREQUENCY_30M = "30m"
        private val FREQUENCY_1H = "1h"
        private val FREQUENCY_3H = "3h"
        private val FREQUENCY_6H = "6h"
        private val FREQUENCY_12H = "12h"
        private val FREQUENCY_24H = "24h"
    }

    private lateinit var closeBtn: ImageView
    private lateinit var presenter: PushNotificationPresenter
    var btn_color: RelativeLayout? = null
    var breaking: Boolean = false
    var personalized: Boolean = false
    private val mFreq = FREQUENCY_1H
    var btn_skip_color: RelativeLayout? = null
    private var mPrefconfig: PrefConfig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setBlackStatusBarColor(this)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.enable_notification)

        bindview()
        init()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun bindview() {
        btn_color = findViewById(R.id.btn_color)
        btn_skip_color = findViewById(R.id.btn_skip_color)
        closeBtn = findViewById(R.id.closeBtn)
    }

    fun init() {
        mPrefconfig = PrefConfig(this)

        presenter = PushNotificationPresenter(this, this)
        closeBtn?.setOnClickListener { this!!.onBackPressed() }

        btn_color?.setOnClickListener {
            breaking = true
            personalized = false
            presenter.pushConfig(breaking, personalized, mFreq, "", "")
        }
        btn_skip_color?.setOnClickListener {
            breaking = false
            personalized = false
            presenter.pushConfig(breaking, personalized, mFreq, "", "")
        }
    }

    override fun loaderShow(flag: Boolean) {
        TODO("Not yet implemented")
    }

    override fun error(error: String?) {
        Utils.showPopupMessageWithCloseButton(this, 2000, error, true)
    }

    override fun error404(error: String?) {
        Utils.showPopupMessageWithCloseButton(this, 2000, error, true)
    }

    override fun success(model: Push) {
        TODO("Not yet implemented")
    }

    override fun SuccessFirst(flag: Boolean) {


    }
}