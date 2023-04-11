package com.ziro.bullet.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ziro.bullet.R
import com.ziro.bullet.bottomSheet.RegisterLoginHelper
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.utills.Utils

class RegistrationActivity : BaseActivity() {
    private lateinit var helper: RegisterLoginHelper
    private var preference: PrefConfig? = null
    private var view: View? = null
    private var isPopup: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_login_bottom_sheet)
        view = window.decorView.rootView
        isPopup = intent.getBooleanExtra("popup", false)
        preference = PrefConfig(this)
        helper = RegisterLoginHelper(this, preference!!, view!!, isPopup)
        helper.init()
        helper.enterEmailFlow(true)
    }

    fun loaderReset() {
        helper.loaderShow(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        helper.loaderShow(false)
        if (data != null) {
            helper.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        Utils.hideKeyboard(this, view)
        super.onBackPressed()
    }
}