package com.ziro.bullet.onboarding.ui

import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
import com.ziro.bullet.R
import com.ziro.bullet.activities.BaseActivity
import com.ziro.bullet.activities.MainActivityNew
import com.ziro.bullet.activities.RegistrationActivity
import com.ziro.bullet.analytics.AnalyticsEvents.logEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.config.UserConfigModel
import com.ziro.bullet.fragments.searchNew.LanguageSelectionFragment
import com.ziro.bullet.interfaces.PasswordInterface
import com.ziro.bullet.interfaces.UserConfigCallback
import com.ziro.bullet.model.language.LanguagesItem
import com.ziro.bullet.model.language.region.Region
import com.ziro.bullet.onboarding.ui.categories.OnBoardingBottomSheet
import com.ziro.bullet.presenter.FCMPresenter
import com.ziro.bullet.presenter.SocialLoginPresenter
import com.ziro.bullet.presenter.UserConfigPresenter
import com.ziro.bullet.utills.Components
import com.ziro.bullet.utills.InternetCheckHelper
import com.ziro.bullet.widget.CollectionWidget
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.fill_btn_wid_progress.*
import kotlinx.android.synthetic.main.outline_btn_wid_progress.*
import java.util.*


class WelcomeActivity : BaseActivity(), PasswordInterface, UserConfigCallback,
    LanguageSelectionFragment.LanguageSelectionCallBack {

    private lateinit var prefConfig: PrefConfig

    private lateinit var mSocialLoginPresenter: SocialLoginPresenter
    private lateinit var configPresenter: UserConfigPresenter
    private lateinit var fcmPresenter: FCMPresenter

    private lateinit var onboardingDialog: OnBoardingBottomSheet
    private var languageSelectionFragment: LanguageSelectionFragment? = null

    private lateinit var selectedRegion: Region
    private lateinit var selectedLanguage: LanguagesItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TransformationCompat.onTransformationStartContainer(this);
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false

        Components().settingStatusBarColors(this, "white")
        setContentView(R.layout.activity_welcome)
        initDependencies()
        setupViews()

    }

    private fun initDependencies() {
        prefConfig = PrefConfig(this)
        mSocialLoginPresenter = SocialLoginPresenter(this, this)
        configPresenter = UserConfigPresenter(this, this)
        fcmPresenter = FCMPresenter(this)
        onboardingDialog = OnBoardingBottomSheet()
        onboardingDialog.updateFragManager(supportFragmentManager)
//        if (prefConfig.prefPrimaryLang.isEmpty()) {
//            languageBottomSheet()
//        }
    }

    private fun languageBottomSheet() {
        languageSelectionFragment = LanguageSelectionFragment.instance(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.welcome_frag_container, languageSelectionFragment!!).addToBackStack(null)
            .commit()
    }

    private fun registerLoginBottomSheet() {
        val intent = Intent(this@WelcomeActivity, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun setupViews() {
        btn_next_text.text = getString(R.string.register_or_login)
        btn_skip_text.text = getString(R.string.skip_login_)
        btn_color.setBackgroundColor(ContextCompat.getColor(this, R.color.theme_color_1))

        if (prefConfig.defaultLanguage != null && !TextUtils.isEmpty(
                prefConfig.defaultLanguage.name
            )
        ) {
            tv_selected_language.text =
                prefConfig.prefPrimaryLang.ifEmpty { prefConfig.defaultLanguage.name }
        }

        language.setOnClickListener {
            languageBottomSheet()
        }

        btn_skip.setOnClickListener {


            if (!InternetCheckHelper.isConnected()) {
                Toast.makeText(
                    this@WelcomeActivity, getString(R.string.internet_error), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            logEvent(
                this@WelcomeActivity, Events.SIGNIN_GUEST
            )
            btn_skip_text.visibility = View.GONE
            btn_skip_progress.visibility = View.VISIBLE
            prefConfig.isGuestUser = true
            mSocialLoginPresenter.skipLogin(
                Settings.Secure.getString(
                    contentResolver, Settings.Secure.ANDROID_ID
                )
            )
        }
        btn_next.setOnClickListener {
            registerLoginBottomSheet()
        }

    }

    override fun loaderShow(flag: Boolean) {
        try {
            runOnUiThread {
                tint?.visibility = if (flag) View.VISIBLE else View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun error(error: String) {

        try {
            runOnUiThread {
                tint?.visibility = View.GONE
                btn_skip_text.visibility = View.VISIBLE
                btn_next_text.visibility = View.VISIBLE
                btn_skip_progress.visibility = View.GONE
                btn_next_progress.visibility = View.GONE
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun error404(error: String) {
        btn_skip_text.visibility = View.VISIBLE
        btn_next_text.visibility = View.VISIBLE
        btn_skip_progress.visibility = View.GONE
        btn_next_progress.visibility = View.GONE
        tint!!.visibility = View.GONE
    }

    override fun onUserConfigSuccess(userConfigModel: UserConfigModel) {
        if (!this.isFinishing) try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d("TAG", "onComplete: fcm  == $token")
                prefConfig.firebaseToken = token
                fcmPresenter.sentTokenToServer(prefConfig)
                OneSignal.setExternalUserId(token)
            })
            if (!TextUtils.isEmpty(prefConfig.isLanguagePushedToServer)) {
                mSocialLoginPresenter.selectRegion(prefConfig.selectedRegion, prefConfig)
                mSocialLoginPresenter.selectLanguage(prefConfig.isLanguagePushedToServer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (userConfigModel.isOnboarded) {
            loadReels()
        } else {
//            val intent = Intent(this@WelcomeActivity, OnBoardingActivity::class.java)
            tint!!.visibility = View.GONE
            btn_next_progress.visibility = View.GONE
            btn_skip_progress.visibility = View.GONE
            btn_skip_text.visibility = View.VISIBLE
            btn_next_text.visibility = View.VISIBLE
//            startActivity(intent)
//            overridePendingTransition(R.anim.enter, R.anim.exit)
            if (!this.isFinishing) {
                onboardingDialog.show()
            }
        }
    }

    override fun success(flag: Boolean) {
        if (flag) {
            configPresenter.getUserConfig(false)
        }
    }

    override fun reset() {}

    override fun resetSuccess(str: String) {}

    private fun loadReels() {
        loaderShow(true)
        gotoHome()
    }

    private fun gotoHome() {
        tint!!.visibility = View.GONE
        val intent = Intent(this@WelcomeActivity, MainActivityNew::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        updateWidget()
    }

    private fun updateWidget() {
        try {
            if (AppWidgetManager.getInstance(this)
                    .getAppWidgetIds(ComponentName(this, CollectionWidget::class.java)).isNotEmpty()
            ) {
                val updateWidget = Intent(this, CollectionWidget::class.java)
                updateWidget.action = "update_widget"
                val pending = PendingIntent.getBroadcast(
                    this,
                    0,
                    updateWidget,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                pending.send()
            }
        } catch (e: CanceledException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (prefConfig.prefPrimaryLang.isNotEmpty() || prefConfig.isLanguagePushedToServer.isNotEmpty()) {
            if (supportFragmentManager.fragments.isEmpty()) {
                super.onBackPressed()
            } else {
                supportFragmentManager.popBackStackImmediate()
            }
        } else {
            finish()
        }
    }

    override fun onLanguageChange(region: Region, languageItem: LanguagesItem, reStart: Boolean) {
        this.selectedRegion = region
        this.selectedLanguage = languageItem
        tv_selected_language.text = selectedLanguage.name
        if (reStart) {
            setLanguage(Locale.forLanguageTag(languageItem.code))

            val intent = Intent(
                this,
                WelcomeActivity::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            onBackPressed()
        }
    }

    private fun getLanguageCode(code: String): String {
        val codesArray = code.split("-".toRegex()).toTypedArray()
        return codesArray[0]
    }

    private fun getCountryCode(code: String): String {
        val codesArray = code.split("-".toRegex()).toTypedArray()
        return if (codesArray.size > 1) codesArray[1] else ""
    }


}