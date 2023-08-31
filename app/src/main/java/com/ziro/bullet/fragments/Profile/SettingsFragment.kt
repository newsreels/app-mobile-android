package com.ziro.bullet.fragments.Profile

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.onesignal.OneSignal
import com.squareup.picasso.Picasso
import com.ziro.bullet.BuildConfig
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.activities.AboutUsActivity
import com.ziro.bullet.activities.AccSettingActivity
import com.ziro.bullet.activities.BlockActivity
import com.ziro.bullet.activities.FontSizeActivity
import com.ziro.bullet.activities.HelpFeedbackActivity
import com.ziro.bullet.activities.LanguageActivity
import com.ziro.bullet.activities.LoginPopupActivity
import com.ziro.bullet.activities.PersonalInfoActivity
import com.ziro.bullet.activities.PushNotificationActivity
import com.ziro.bullet.activities.SavedPostActivity
import com.ziro.bullet.activities.WebViewActivity
import com.ziro.bullet.activities.logs.DebugLogsActivity
import com.ziro.bullet.analytics.AnalyticsEvents.logEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.auth.OAuthAPIClient
import com.ziro.bullet.background.VideoProcessorService
import com.ziro.bullet.bottomSheet.ForYouReelSheet
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.userInfo.User
import com.ziro.bullet.onboarding.ui.WelcomeActivity
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.InternetCheckHelper
import com.ziro.bullet.utills.Utils
import com.ziro.bullet.widget.CollectionWidget
import kotlinx.android.synthetic.main.fragment_settings.app_version
import kotlinx.android.synthetic.main.fragment_settings.cl_change_email
import kotlinx.android.synthetic.main.fragment_settings.cl_change_primary_lang
import kotlinx.android.synthetic.main.fragment_settings.cl_change_region
import kotlinx.android.synthetic.main.fragment_settings.cl_change_second_lang
import kotlinx.android.synthetic.main.fragment_settings.cl_font_size
import kotlinx.android.synthetic.main.fragment_settings.cl_logout
import kotlinx.android.synthetic.main.fragment_settings.cl_notifications_setting
import kotlinx.android.synthetic.main.fragment_settings.cl_show_about
import kotlinx.android.synthetic.main.fragment_settings.cl_show_block_list
import kotlinx.android.synthetic.main.fragment_settings.cl_show_debug_logs
import kotlinx.android.synthetic.main.fragment_settings.cl_show_guidelines
import kotlinx.android.synthetic.main.fragment_settings.cl_show_help
import kotlinx.android.synthetic.main.fragment_settings.cl_show_policy
import kotlinx.android.synthetic.main.fragment_settings.cl_show_saved
import kotlinx.android.synthetic.main.fragment_settings.cl_show_terms
import kotlinx.android.synthetic.main.fragment_settings.cl_view_profile
import kotlinx.android.synthetic.main.fragment_settings.facebook_link
import kotlinx.android.synthetic.main.fragment_settings.frag_setting_sw_play
import kotlinx.android.synthetic.main.fragment_settings.instagram_link
import kotlinx.android.synthetic.main.fragment_settings.iv_select_primary_lang
import kotlinx.android.synthetic.main.fragment_settings.iv_select_second_lang
import kotlinx.android.synthetic.main.fragment_settings.roundedImageView
import kotlinx.android.synthetic.main.fragment_settings.switch_reader_mode
import kotlinx.android.synthetic.main.fragment_settings.tv_view_profile
import kotlinx.android.synthetic.main.fragment_settings.twitter_link
import kotlinx.android.synthetic.main.fragment_settings.username
import kotlinx.android.synthetic.main.fragment_settings.view_profile_text
import kotlinx.android.synthetic.main.fragment_settings.youtube_link
import kotlinx.android.synthetic.main.progress.progress
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment : Fragment(), View.OnClickListener {

    private var forYouReelSheet: ForYouReelSheet? = null
    private var user: User? = null
    private var mPrefConfig: PrefConfig? = null
    private var cacheManager: DbHandler? = null

    companion object {
        @JvmStatic
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPrefConfig = PrefConfig(activity)
        cacheManager = DbHandler(context)
        app_version?.text = "App version: ${BuildConfig.VERSION_NAME}"
        if (BuildConfig.DEBUG) {
            cl_show_debug_logs?.visibility = View.VISIBLE
        } else {
            cl_show_debug_logs?.visibility = View.GONE
        }
        setListener()
    }

    override fun onResume() {
        super.onResume()
        setData()
    }

    private fun getUserDataFromShredPref() {
        if (mPrefConfig != null) {
            user = mPrefConfig!!.isUserObject
            if (user != null) showVerifiedIcon(username, user!!.isVerified)
        }
    }

    private fun showVerifiedIcon(view: TextView?, tag: Boolean) {
        if (view == null) return
        if (tag) {
            if (Utils.isRTL()) {
                view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified, 0, 0, 0)
            } else {
                view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified, 0)
            }
        }
    }

    private fun setData() {
        if (mPrefConfig != null) {

            if (!mPrefConfig!!.hasPassword) {
                cl_change_email.visibility = View.GONE
            }

            iv_select_primary_lang.text = mPrefConfig?.prefPrimaryLang
            iv_select_second_lang.text = mPrefConfig?.prefSecondaryLang
            if (mPrefConfig!!.isGuestUser) {
                tv_view_profile.setText(R.string.sign_up_login)
                cl_logout.visibility = View.GONE
            } else {
                tv_view_profile.setText(R.string.view_and_edit_your_profile)
                cl_logout.visibility = View.VISIBLE
            }

            user = mPrefConfig!!.isUserObject
            val isAutoPlay = mPrefConfig!!.isVideoAutoPlay
            frag_setting_sw_play.isChecked = isAutoPlay
            val readerMode = mPrefConfig?.isReaderMode
            switch_reader_mode.isChecked = readerMode ?: false
            if (user != null) {
                if (!TextUtils.isEmpty(user!!.name)) {
                    username.text = String.format("%s", user!!.name)
                } else {
                    username.setText(R.string.create_your_profile)
                }

//                if (!TextUtils.isEmpty(user!!.username)) {
//                    view_profile_text.visibility = View.GONE
////                    view_profile_text.visibility = View.VISIBLE
////                    view_profile_text.text = String.format("%s", user!!.username)
//                }
                if (!TextUtils.isEmpty(user!!.profile_image)) {
                    Picasso.get().load(user!!.profile_image)
                        .placeholder(R.drawable.ic_placeholder_user)
                        .into(roundedImageView)
                } else {
                    roundedImageView.setImageResource(R.drawable.ic_placeholder_user)
                }
            } else {
                username.setText(R.string.create_your_profile)
                roundedImageView.setImageResource(R.drawable.ic_placeholder_user)
            }
        }
        getUserDataFromShredPref()
    }

    private fun setListener() {
        cl_view_profile.setOnClickListener(this)
        cl_change_primary_lang.setOnClickListener(this)
        cl_change_second_lang.setOnClickListener(this)
        cl_change_region.setOnClickListener(this)
        cl_notifications_setting.setOnClickListener(this)
        cl_font_size.setOnClickListener(this)
        cl_change_email.setOnClickListener(this)
        cl_show_saved.setOnClickListener(this)
        cl_show_block_list.setOnClickListener(this)
        cl_show_terms.setOnClickListener(this)
        cl_logout.setOnClickListener(this)
        cl_show_policy.setOnClickListener(this)
        cl_show_guidelines.setOnClickListener(this)
        cl_show_about.setOnClickListener(this)
        cl_show_help.setOnClickListener(this)
//        tiktok_link.setOnClickListener(this)
        twitter_link.setOnClickListener(this)
        facebook_link.setOnClickListener(this)
        youtube_link.setOnClickListener(this)
        instagram_link.setOnClickListener(this)
        cl_show_debug_logs.setOnClickListener(this)

        frag_setting_sw_play.setOnCheckedChangeListener { _, flag ->
            mPrefConfig?.isVideoAutoPlay = flag
        }

        switch_reader_mode.setOnCheckedChangeListener { _, flag ->
            mPrefConfig?.isReaderMode = flag
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cl_change_primary_lang -> {
                var intent = Intent(requireActivity(), LanguageActivity::class.java)
                intent.putExtra("flow", "setting")
                intent.putExtra("type", Constants.PRIMARY_LANGUAGE)
                startActivity(intent)
            }

            R.id.cl_change_second_lang -> {
                var intent = Intent(requireActivity(), LanguageActivity::class.java)
                intent.putExtra("flow", "setting")
                intent.putExtra("type", Constants.SECONDARY_LANGUAGE)
                startActivity(intent)
            }

            R.id.cl_change_region -> {
                var intent = Intent(requireActivity(), LanguageActivity::class.java)
                intent.putExtra("flow", "setting")
                intent.putExtra("type", Constants.REGION)
                startActivity(intent)
            }

            R.id.cl_font_size -> {
                if (activity != null) {
                    startActivity(Intent(requireActivity(), FontSizeActivity::class.java))
                }
            }

            R.id.cl_change_email -> {
                if (mPrefConfig!!.isGuestUser) {
                    LoginPopupActivity.start(activity)
                } else {
                    logEvent(
                        context,
                        Events.CREATE_PROFILE_CLICK
                    )
                    if (activity != null) {
                        requireActivity().startActivity(
                            Intent(
                                activity,
                                AccSettingActivity::class.java
                            )
                        )
                    }
                }
            }

            R.id.cl_show_saved -> {
                if (activity != null) {
                    requireActivity().startActivity(Intent(activity, SavedPostActivity::class.java))
                }
            }

            R.id.cl_notifications_setting -> {
                if (activity != null) {
                    requireActivity().startActivity(
                        Intent(
                            activity,
                            PushNotificationActivity::class.java
                        )
                    )
                }
            }

            R.id.cl_show_block_list -> {
                if (activity != null) {
                    requireActivity().startActivity(Intent(activity, BlockActivity::class.java))
                }
            }

            R.id.cl_view_profile -> {
                if (mPrefConfig!!.isGuestUser) {
                    LoginPopupActivity.start(activity)
                } else {
                    logEvent(
                        context,
                        Events.CREATE_PROFILE_CLICK
                    )
                    if (activity != null) {
                        requireActivity().startActivity(
                            Intent(
                                activity,
                                PersonalInfoActivity::class.java
                            )
                        )
                    }
                }
            }

            R.id.cl_show_help -> {
                if (activity != null) {
                    requireActivity().startActivity(
                        Intent(
                            activity,
                            HelpFeedbackActivity::class.java
                        )
                    )
                }
            }

            R.id.cl_show_terms -> {
                Utils.openWebView(
                    requireContext(),
                    "https://www.newsreels.app/terms-conditions.html",
                    getString(R.string.terms_conditions)
                )
            }

            R.id.cl_show_policy -> {
                Utils.openWebView(
                    requireContext(),
                    "https://www.newsreels.app/privacy-policy.html",
                    getString(R.string.privacy_policy)
                )
            }

            R.id.cl_show_guidelines -> {
                Utils.openWebView(
                    requireContext(),
                    "https://www.newsreels.app/community-guidelines.html",
                    getString(R.string.community_guideline_)
                )
            }

            R.id.cl_show_about -> {
                startActivity(Intent(requireActivity(), AboutUsActivity::class.java))
            }

            R.id.cl_logout -> {
                logoutAPI()
            }

//            R.id.tiktok_link -> {
//                logEvent(
//                    context,
//                    Events.MENU_TIKTOK_OPEN
//                )
//                openSocialLink(Constants.SOCIAL_LINKS.TIKTOK)
//            }

            R.id.twitter_link -> {
                logEvent(
                    context,
                    Events.MENU_TW_OPEN
                )
                openSocialLink(Constants.SOCIAL_LINKS.TWITTER)
            }

            R.id.facebook_link -> {
                logEvent(
                    context,
                    Events.MENU_FB_OPEN
                )
                val fbUrl = Constants.SOCIAL_LINKS.FB
                val fbAppUrl = Constants.SOCIAL_LINKS.FB_PAGE
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl))
                    startActivity(intent)
                } catch (e: Exception) {
                    openSocialLink(fbUrl)
                }
            }

            R.id.youtube_link -> {
                logEvent(
                    context,
                    Events.MENU_YT_OPEN
                )
                openSocialLink(Constants.SOCIAL_LINKS.YOUTUBE)
            }

            R.id.instagram_link -> {
                logEvent(
                    context,
                    Events.MENU_IG_OPEN
                )
                openSocialLink(Constants.SOCIAL_LINKS.IG)
            }

            R.id.cl_show_debug_logs -> {
                if (activity != null) {
                    requireActivity().startActivity(Intent(activity, DebugLogsActivity::class.java))
                }
            }
        }
    }

    fun openSocialLink(link: String?) {
        if (activity != null) {
            val customTabsPackages = Utils.getCustomTabsPackages(
                activity
            )
            if (customTabsPackages.size > 0) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder
                    .setShowTitle(true)
                    .build()
                customTabsIntent.launchUrl(requireActivity(), Uri.parse(link))
            } else {
                val intent = Intent(activity, WebViewActivity::class.java)
                intent.putExtra("title", "Social Media Account")
                intent.putExtra("url", link)
                requireActivity().startActivity(intent)
            }
        }
    }

    fun forYouBottomSheet(mode: String?) {
        forYouReelSheet = ForYouReelSheet.getInstance(mode, "", null, null, null, null)
        forYouReelSheet?.show(childFragmentManager, "id")
        forYouReelSheet?.viewLifecycleOwnerLiveData?.observe(this,
            Observer { lifecycleOwner: LifecycleOwner? ->
                //when lifecycleOwner is null fragment is destroyed
                if (lifecycleOwner == null) {
                }
            })
    }

    private fun logoutAPI() {

        logEvent(context, Events.LOGOUT_CLICK)
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(context, "" + this.getString(R.string.network_error), Toast.LENGTH_SHORT)
                .show()
            return
        }

        progress.visibility = View.VISIBLE
        val call = OAuthAPIClient
            .getInstance()
            .api
            .logout("Bearer " + mPrefConfig!!.accessToken, mPrefConfig!!.refreshToken)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                logout()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                logout()
            }
        })
    }

    private fun logout() {

        val savedLangName = mPrefConfig!!.prefPrimaryLang
        val savedRegion = mPrefConfig!!.selectedRegion

        Constants.saveLanguageId = mPrefConfig!!.isLanguagePushedToServer
        mPrefConfig!!.clear()
        mPrefConfig!!.setLanguageForServer(Constants.saveLanguageId)
        mPrefConfig!!.prefPrimaryLang = savedLangName
        mPrefConfig!!.selectedRegion = savedRegion
        mPrefConfig!!.firstTimeLaunch = false
        OneSignal.removeExternalUserId()
        try {
            if (activity != null) {
                if (Utils.isMyServiceRunning(activity, VideoProcessorService::class.java)) {
                    requireActivity().stopService(
                        Intent(
                            activity,
                            VideoProcessorService::class.java
                        )
                    )
                }
            }
            if (cacheManager != null) {
                cacheManager!!.clearDb()
            }
            if (activity != null) {
                val notificationManager =
                    requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        progress.visibility = View.GONE
        Constants.onResumeReels = true
        updateWidget()
        val intent = Intent(context, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        if (activity != null) {
            requireActivity().finish()
        }
    }

    private fun updateWidget() {
        try {
            if (context != null) if (AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(
                        requireContext(),
                        CollectionWidget::class.java
                    )
                ).isNotEmpty()
            ) {
                val updateWidget = Intent(context, CollectionWidget::class.java)
                updateWidget.action = "update_widget"
                val pending = PendingIntent.getBroadcast(
                    context,
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

}