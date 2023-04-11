package com.ziro.bullet.bottomSheet

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.onesignal.OneSignal
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.BuildConfig
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.activities.BaseActivity
import com.ziro.bullet.activities.LoginPopupActivity
import com.ziro.bullet.activities.MainActivityNew
import com.ziro.bullet.activities.ProfileNameActivity
import com.ziro.bullet.analytics.AnalyticsEvents.logEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.auth.OAuthAPIClient
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.config.UserConfigModel
import com.ziro.bullet.interfaces.*
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.presenter.*
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.InternetCheckHelper
import com.ziro.bullet.utills.Utils
import com.ziro.bullet.widget.CollectionWidget
import kotlinx.android.synthetic.main.add_password_flow.view.*
import kotlinx.android.synthetic.main.create_new_password.view.*
import kotlinx.android.synthetic.main.enter_email_flow.view.*
import kotlinx.android.synthetic.main.enter_username_flow.view.*
import kotlinx.android.synthetic.main.fill_btn_wid_progress.view.*
import kotlinx.android.synthetic.main.newsreels_edittext.view.*
import kotlinx.android.synthetic.main.newsreels_edittext_password.view.*
import kotlinx.android.synthetic.main.newsreels_edittext_password.view.eye
import kotlinx.android.synthetic.main.newsreels_edittext_password1.view.*
import kotlinx.android.synthetic.main.newsreels_edittext_username.view.*
import kotlinx.android.synthetic.main.outline_facebook_btn_wid_progress.view.*
import kotlinx.android.synthetic.main.outline_google_btn_wid_progress.view.*
import kotlinx.android.synthetic.main.password_btn_save.view.*
import kotlinx.android.synthetic.main.password_btn_save1.view.*
import kotlinx.android.synthetic.main.reg_btn_conitnue.view.*
import kotlinx.android.synthetic.main.register_login_bottom_sheet.view.*
import kotlinx.android.synthetic.main.registration_complete_flow.view.*
import kotlinx.android.synthetic.main.reset_email_edittext.view.*
import kotlinx.android.synthetic.main.reset_password_flow.view.*
import kotlinx.android.synthetic.main.reset_password_flow.view.errorResetEmail
import kotlinx.android.synthetic.main.send_link_btn.view.*
import kotlinx.android.synthetic.main.terms_flow.view.*
import kotlinx.android.synthetic.main.username_btn_conitnue.view.*
import kotlinx.android.synthetic.main.welcome_back_flow.view.*
import kotlinx.android.synthetic.main.welcome_continuee.view.*
import kotlinx.android.synthetic.main.welcome_edittext_password.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern


@SuppressLint("UseCompatLoadingForDrawables")
class RegisterLoginHelper(
    val activity: Activity,
    val preference: PrefConfig,
    val view: View,
    val isPopup: Boolean,
) : SignInInterface, UserConfigCallback, ProfileApiCallback, PasswordInterface {
    private var validEmail: String = ""
    private var validPassword: String = ""
    private var validUsername: String = ""
    private var isUsernameValid: Boolean = false
    private val TAG: String = "REGISTER"
    private val FACEBOOK_PERMISSIONS = Arrays.asList("public_profile", "email")
    private val RC_SIGN_IN = 33421
    val FINISH_TO_LOGIN = 2317
    private var fbCallbackManager: CallbackManager? = null
    private var mSocialLoginPresenter: SocialLoginPresenter? = null
    private lateinit var presenter: SignInPresenter
    private var configPresenter: UserConfigPresenter? = null
    private var fcmPresenter: FCMPresenter? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var profilePresenter: ProfilePresenter? = null
    private var step: String = "1"
    private var passwordPresenter: PasswordPresenter? = null

    fun init() {
        passwordPresenter = PasswordPresenter(activity, this)
        presenter = SignInPresenter(activity, this)
        mSocialLoginPresenter = SocialLoginPresenter(activity, this)
        configPresenter = UserConfigPresenter(activity, this)
        fcmPresenter = FCMPresenter(activity)
        profilePresenter = ProfilePresenter(activity, this)
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.G_SERVER_CLIENT_ID)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        view.back.setOnClickListener {
            backward()
        }
        view.closeBtn.setOnClickListener {
            activity.setResult(RESULT_OK)
            Utils.hideKeyboard(activity, view)
            activity.finish()
        }
    }

    fun enterEmailFlow(isFirstTime: Boolean) {
        step = "1"
        view.nestedScroll.visibility = View.VISIBLE
        view.email_flow.visibility = View.VISIBLE
        view.reset_password_flow.visibility = View.GONE
        view.welcome_back_flow.visibility = View.GONE
        view.username_flow.visibility = View.GONE
        view.password_flow.visibility = View.GONE
        view.terms_flow.visibility = View.GONE
        view.reg_complete_flow.visibility = View.GONE
        view.back.visibility = View.GONE
        view.closeBtn.visibility = View.VISIBLE
        view.edittextEmail.requestFocus()
        if (isFirstTime) {
            emailFlowFunc()
        }
    }

    private fun emailFlowFunc() {
        view.continue_.isEnabled = false
        view.edittext.background = activity.getDrawable(R.drawable.normal_edittext_theme)
        view.edittextEmail.setTextColor((ContextCompat.getColor(activity, R.color.black)))
        view.btn_color.setBackgroundColor(
            (ContextCompat.getColor(
                activity,
                R.color.disable_btn
            ))
        )
        view.btn_next_text.setTextColor(
            (ContextCompat.getColor(
                activity,
                R.color.edittextHint
            ))
        )
        view.btn_next_text.text = activity.getString(R.string.continuee)

        view.edittextEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                view.errorEmail.text = ""
                view.errorEmail.visibility = View.VISIBLE
                view.edittext.background = activity.getDrawable(R.drawable.normal_edittext_theme)
                view.edittextEmail.setTextColor((ContextCompat.getColor(activity, R.color.black)))

                view.btn_color.setBackgroundColor(
                    (ContextCompat.getColor(
                        activity,
                        R.color.theme_color_1
                    ))
                )
                view.btn_next_text.setTextColor((ContextCompat.getColor(activity, R.color.white)))
                view.btn_next_text.text = activity.getString(R.string.continuee)

                if (s!!.isEmpty()) {
                    view.continue_.isEnabled = false
                    view.btn_color.setBackgroundColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.disable_btn
                        ))
                    )
                    view.btn_next_text.setTextColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.edittextHint
                        ))
                    )

                } else {
                    if (!TextUtils.isEmpty(view.edittextEmail.text) && Utils.isValidEmail(
                            view.edittextEmail.text.toString()
                        )
                    ) {
                        view.continue_.isEnabled = true
                    } else {
                        view.continue_.isEnabled = false
                        view.btn_color.setBackgroundColor(
                            (ContextCompat.getColor(
                                activity,
                                R.color.disable_btn
                            ))
                        )
                        view.btn_next_text.setTextColor(
                            (ContextCompat.getColor(
                                activity,
                                R.color.edittextHint
                            ))
                        )
                    }
                }
            }
        })
        view.continue_.setOnClickListener {
            if (!InternetCheckHelper.isConnected()) {
                Utils.showPopupMessageWithCloseButton(
                    activity,
                    2000,
                    activity.getString(R.string.internet_error),
                    true
                )
                return@setOnClickListener
            }
            view.btn_next_progress.visibility = View.VISIBLE
            view.btn_next_text.visibility = View.GONE
            presenter.checkEmail(view.edittextEmail.text.toString())
        }
        view.google.setOnClickListener {
            view.google.requestFocus()
            Utils.hideKeyboard(activity, view.google)
            logEvent(
                activity,
                Events.SIGNUP_GOOGLE
            )
            if (!InternetCheckHelper.isConnected()) {
                Utils.showPopupMessageWithCloseButton(
                    activity,
                    2000,
                    activity.getString(R.string.internet_error),
                    true
                )
                return@setOnClickListener
            }
            view.btn_google_progress.visibility = View.VISIBLE
            view.btn_google_text.visibility = View.GONE

            loaderShow(true)
            googleSignIn()
        }
        view.facebook.setOnClickListener {
            view.facebook.requestFocus()
            Utils.hideKeyboard(activity, view.facebook)
            step = "7"
            logEvent(
                activity,
                Events.SIGNUP_FACEBOOK
            )
            if (!InternetCheckHelper.isConnected()) {
                Utils.showPopupMessageWithCloseButton(
                    activity,
                    2000,
                    activity.getString(R.string.internet_error),
                    true
                )
                return@setOnClickListener
            }

            view.btn_fb_progress.visibility = View.VISIBLE
            view.btn_fb_text.visibility = View.GONE

            loaderShow(true)

            fbCallbackManager = CallbackManager.Factory.create()

            if (LoginManager.getInstance() != null) {
                LoginManager.getInstance().logOut()
            }

            LoginManager.getInstance()
                .logInWithReadPermissions(activity, FACEBOOK_PERMISSIONS)

            LoginManager.getInstance()
                .registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        //1. Logged in to Facebook
                        val accessToken = result.accessToken
                        mSocialLoginPresenter!!.facebookLogin(accessToken)
                    }

                    override fun onCancel() {
                        Log.i(TAG, "Facebook sign-in cancelled")
                    }

                    override fun onError(error: FacebookException) {
                        Log.e(TAG, "Error " + error.message)
                    }
                })
        }
    }

    private fun enterUserNameFlow(isFirstTime: Boolean) {
        step = "2"
        view.nestedScroll.visibility = View.VISIBLE
        view.email_flow.visibility = View.GONE
        view.reset_password_flow.visibility = View.GONE
        view.welcome_back_flow.visibility = View.GONE
        view.username_flow.visibility = View.VISIBLE
        view.back.visibility = View.VISIBLE
        view.password_flow.visibility = View.GONE
        view.terms_flow.visibility = View.GONE
        view.reg_complete_flow.visibility = View.GONE
        view.closeBtn.visibility = View.VISIBLE
        view.edittextUsername.requestFocus()
        if (isFirstTime) {
            userNameFlowFunc()
        }
    }

    private fun userNameFlowFunc() {
        view.continueUsername.isEnabled = false
        view.frame_username.background = activity.getDrawable(R.drawable.normal_edittext_theme)
        view.edittextUsername.setTextColor((ContextCompat.getColor(activity, R.color.black)))
        view.btn_username_color.setBackgroundColor(
            (ContextCompat.getColor(
                activity,
                R.color.disable_btn
            ))
        )
        view.btn_username_text.setTextColor(
            (ContextCompat.getColor(
                activity,
                R.color.edittextHint
            ))
        )
        view.btn_username_text.text = activity.getString(R.string.continuee)

        view.edittextUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.errorUsername.text = ""
                view.errorUsername.visibility = View.VISIBLE
                view.username_icon.visibility = View.INVISIBLE
                view.frame_username.background =
                    activity.getDrawable(R.drawable.normal_edittext_theme)
                view.edittextUsername.setTextColor(
                    (ContextCompat.getColor(
                        activity,
                        R.color.black
                    ))
                )

                view.continueUsername.isEnabled = false
                view.btn_username_color.setBackgroundColor(
                    (ContextCompat.getColor(
                        activity,
                        R.color.disable_btn
                    ))
                )
                view.btn_username_text.setTextColor(
                    (ContextCompat.getColor(
                        activity,
                        R.color.edittextHint
                    ))
                )
                OAuthAPIClient.cancelAll()
                isUsernameValid = false
                val usernameCopy = s.toString()
                if (!s!!.isNullOrEmpty()) {
                    profilePresenter!!.checkUsername(usernameCopy, object : ApiCallbacks {
                        override fun loaderShow(flag: Boolean) {
                            if (flag) {
                                setUsernameLoading()
                            }
                        }

                        override fun error(error: String?) {
                            error(
                                view.errorUsername,
                                view.frame_username,
                                view.edittextUsername,
                                view.btn_username_color,
                                view.btn_username_text,
                                error
                            )
                        }

                        override fun error404(error: String?) {
                            error(
                                view.errorUsername,
                                view.frame_username,
                                view.edittextUsername,
                                view.btn_username_color,
                                view.btn_username_text,
                                error
                            )
                        }

                        override fun success(response: Any?) {
                            if (response is Boolean) {
                                if (response) {
                                    validUsername = usernameCopy
                                    isUsernameValid = true
                                    view.continueUsername.isEnabled = true
                                    setUsernameValidIcon()
                                } else {
                                    isUsernameValid = false
                                    setUsernameInvalidIcon()
                                }
                            } else {
                                isUsernameValid = false
                                setUsernameInvalidIcon()
                            }
                        }
                    })
                }
            }
        })

        view.continueUsername.setOnClickListener {
            enterPasswordFlow(true)
        }
    }

    private fun enterPasswordFlow(isFirstTime: Boolean) {
        step = "3"
        view.nestedScroll.visibility = View.VISIBLE
        view.email_flow.visibility = View.GONE
        view.reset_password_flow.visibility = View.GONE
        view.welcome_back_flow.visibility = View.GONE
        view.username_flow.visibility = View.GONE
        view.back.visibility = View.VISIBLE
        view.password_flow.visibility = View.VISIBLE
        view.terms_flow.visibility = View.GONE
        view.reg_complete_flow.visibility = View.GONE
        view.closeBtn.visibility = View.VISIBLE
        view.edittextPassword.requestFocus()
        if (isFirstTime) {
            passwordFlowFunc()
        }
    }

    private fun createPasswordCheck() {
        view.savepas.isEnabled = false
        view.frame_password1.background = activity.getDrawable(R.drawable.normal_edittext_theme)
        view.edittextPassword1.setTextColor((ContextCompat.getColor(activity, R.color.black)))
        view.btn_save_color1.setBackgroundColor(
            (ContextCompat.getColor(
                activity,
                R.color.disable_btn
            ))
        )
        view.btn_save_text1.setTextColor((ContextCompat.getColor(activity, R.color.edittextHint)))
        view.btn_save_text1.text = activity.getString(R.string.save_password)

        var flag: Boolean = false
        view.eye1.setOnClickListener {
            if (flag) {
                view.edittextPassword1.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                view.eye1.setImageResource(R.drawable.eyes_off)
                flag = false
            } else {
                view.edittextPassword1.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                flag = true
                view.eye1.setImageResource(R.drawable.eyes_on)
            }
        }
        view.edittextPassword1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.error1.text = ""
                view.error1.visibility = View.VISIBLE
                view.frame_password1.background =
                    activity.getDrawable(R.drawable.normal_edittext_theme)
                view.edittextPassword1.setTextColor(
                    (ContextCompat.getColor(
                        activity,
                        R.color.black
                    ))
                )
                validPassword = ""
                if (s!!.isEmpty()) {
                    condition(view.cond1_text1, view.cond1_icon1, false)
                    condition(view.cond2_text1, view.cond2_icon1, false)
                    condition(view.cond3_text1, view.cond3_icon1, false)

                    view.savepas.isEnabled = false
                    view.btn_save_color1.setBackgroundColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.disable_btn
                        ))
                    )
                    view.btn_save_text1.setTextColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.edittextHint
                        ))
                    )

                } else {

                    if (is8Char(view.edittextPassword1.text.toString())) {
                        condition(view.cond1_text1, view.cond1_icon1, true)
                    } else {
                        condition(view.cond1_text1, view.cond1_icon1, false)
                    }
                    if (isOneDigit(view.edittextPassword1.text.toString())) {
                        condition(view.cond2_text1, view.cond2_icon1, true)
                    } else {
                        condition(view.cond2_text1, view.cond2_icon1, false)
                    }
                    if (isOneUpperCase(view.edittextPassword1.text.toString())) {
                        condition(view.cond3_text1, view.cond3_icon1, true)
                    } else {
                        condition(view.cond3_text1, view.cond3_icon1, false)
                    }

                    if (isValidPassword(view.edittextPassword1.text.toString())) {
                        if (isOneUpperCase(view.edittextPassword1.text.toString()) &&
                            isOneDigit(view.edittextPassword1.text.toString()) &&
                            is8Char(view.edittextPassword1.text.toString())
                        ) {
                            view.savepas.isEnabled = true
                            validPassword = view.edittextPassword1.text.toString()
                            view.btn_save_color1.setBackgroundColor(
                                (ContextCompat.getColor(
                                    activity,
                                    R.color.theme_color_1
                                ))
                            )
                            view.btn_save_text1.setTextColor(
                                (ContextCompat.getColor(
                                    activity,
                                    R.color.white
                                ))
                            )
                            view.btn_save_text1.text = activity.getString(R.string.save_password)


                        }

                    } else {
                        view.savepas.isEnabled = false
                        view.btn_save_color1.setBackgroundColor(
                            (ContextCompat.getColor(
                                activity,
                                R.color.disable_btn
                            ))
                        )
                        view.btn_save_text1.setTextColor(
                            (ContextCompat.getColor(
                                activity,
                                R.color.edittextHint
                            ))
                        )
                    }
                }
            }
        })

        view.savepas.setOnClickListener {
            passwordPresenter!!.register(
                validEmail,
                view.edittextPassword1.text.toString(),
                "784398",
                true,
                true
            )

//            if (!InternetCheckHelper.isConnected()) {
//                Utils.showPopupMessageWithCloseButton(
//                    activity,
//                    2000,
//                    activity.getString(R.string.internet_error),
//                    true
//                )
//                return@setOnClickListener
//            }
//            view.btn_save_progress1.visibility = View.VISIBLE
//            view.btn_save_text1.visibility = View.GONE

//            logEvent(
//                activity,
//                Events.SIGNIN_CLICK
//            )

            //register the new password and do normal login
//            passwordPresenter!!.normalLogin(
//                validEmail,
//                view.edittextPassword1.text.toString()
//            )


            //going to next page by enabling true

//            enterTermsFlow(true)
        }

    }

    private fun passwordFlowFunc() {

        view.continuePassword.isEnabled = false
        view.frame_password.background = activity.getDrawable(R.drawable.normal_edittext_theme)
        view.edittextPassword.setTextColor((ContextCompat.getColor(activity, R.color.black)))
        view.btn_save_color.setBackgroundColor(
            (ContextCompat.getColor(
                activity,
                R.color.disable_btn
            ))
        )
        view.btn_save_text.setTextColor((ContextCompat.getColor(activity, R.color.edittextHint)))
        view.btn_save_text.text = activity.getString(R.string.save_password)

        var flag: Boolean = false
        view.eye.setOnClickListener {
            if (flag) {
                view.edittextPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                view.eye.setImageResource(R.drawable.eyes_off)
                flag = false
            } else {
                view.edittextPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                flag = true
                view.eye.setImageResource(R.drawable.eyes_on)
            }
        }
        view.edittextPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.error.text = ""
                view.error.visibility = View.VISIBLE
                view.frame_password.background =
                    activity.getDrawable(R.drawable.normal_edittext_theme)
                view.edittextPassword.setTextColor(
                    (ContextCompat.getColor(
                        activity,
                        R.color.black
                    ))
                )
                validPassword = ""
                if (s!!.isEmpty()) {
                    condition(view.cond1_text, view.cond1_icon, false)
                    condition(view.cond2_text, view.cond2_icon, false)
                    condition(view.cond3_text, view.cond3_icon, false)

                    view.continuePassword.isEnabled = false
                    view.btn_save_color.setBackgroundColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.disable_btn
                        ))
                    )
                    view.btn_save_text.setTextColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.edittextHint
                        ))
                    )

                } else {

                    if (is8Char(view.edittextPassword.text.toString())) {
                        condition(view.cond1_text, view.cond1_icon, true)
                    } else {
                        condition(view.cond1_text, view.cond1_icon, false)
                    }
                    if (isOneDigit(view.edittextPassword.text.toString())) {
                        condition(view.cond2_text, view.cond2_icon, true)
                    } else {
                        condition(view.cond2_text, view.cond2_icon, false)
                    }
                    if (isOneUpperCase(view.edittextPassword.text.toString())) {
                        condition(view.cond3_text, view.cond3_icon, true)
                    } else {
                        condition(view.cond3_text, view.cond3_icon, false)
                    }

                    if (isValidPassword(view.edittextPassword.text.toString())) {

                        if (isOneUpperCase(view.edittextPassword.text.toString()) &&
                            isOneDigit(view.edittextPassword.text.toString()) &&
                            is8Char(view.edittextPassword.text.toString())
                        ) {
                            view.continuePassword.isEnabled = true
                            validPassword = view.edittextPassword.text.toString()
                            view.btn_save_color.setBackgroundColor(
                                (ContextCompat.getColor(
                                    activity,
                                    R.color.theme_color_1
                                ))
                            )
                            view.btn_save_text.setTextColor(
                                (ContextCompat.getColor(
                                    activity,
                                    R.color.white
                                ))
                            )
                            view.btn_save_text.text = activity.getString(R.string.save_password)
                        }

                    } else {
                        view.continuePassword.isEnabled = false
                        view.btn_save_color.setBackgroundColor(
                            (ContextCompat.getColor(
                                activity,
                                R.color.disable_btn
                            ))
                        )
                        view.btn_save_text.setTextColor(
                            (ContextCompat.getColor(
                                activity,
                                R.color.edittextHint
                            ))
                        )
                    }
                }
            }
        })

        view.continuePassword.setOnClickListener {
            validPassword = view.edittextPassword.text.toString()
            enterTermsFlow(true)
        }

    }

    private fun enterTermsFlow(isFirstTime: Boolean) {
        step = "4"
        Utils.hideKeyboard(activity, view.terms_flow)
        view.nestedScroll.visibility = View.GONE
        view.email_flow.visibility = View.GONE
        view.reset_password_flow.visibility = View.GONE
        view.username_flow.visibility = View.GONE
        view.welcome_back_flow.visibility = View.GONE
        view.back.visibility = View.GONE
        view.password_flow.visibility = View.GONE
        view.terms_flow.visibility = View.VISIBLE
        view.reg_complete_flow.visibility = View.GONE
        view.closeBtn.visibility = View.GONE
        if (isFirstTime) {

            val webSettings: WebSettings = view.terms_webview.settings
            view.terms_webview.webViewClient = WebViewClient()
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            view.terms_webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
//            webSettings.setAppCacheEnabled(false)
            view.terms_webview.loadUrl("https://www.newsinbullets.app/terms?header=false")
            //WEBVIEW CALLBACKS
            view.terms_webview.setWebChromeClient(object : WebChromeClient() {
                override fun onProgressChanged(view1: WebView, newProgress: Int) {
                    super.onProgressChanged(view1, newProgress)
                    Log.e(
                        TAG,
                        "onProgressChanged() called with: view1 = $view1, newProgress = $newProgress"
                    )
//                    view.linearPb.setProgressCompat(newProgress, true)
                    if (newProgress == 10) {
                        loaderShow(true)
                    } else if (newProgress == 100) {
                        loaderShow(false)
                    }

                }
            })
            view.disagree.setOnClickListener {
                activity.setResult(RESULT_OK)
                activity.finish()
            }
            view.agree.setOnClickListener {

                Log.e(TAG, "=========================")
                Log.e(TAG, "email : " + validEmail)
                Log.e(TAG, "password : " + validPassword)
                Log.e(TAG, "username : " + validUsername)

                if (!InternetCheckHelper.isConnected()) {
                    Utils.showPopupMessageWithCloseButton(
                        activity,
                        2000,
                        activity.getString(R.string.internet_error),
                        true
                    )
                    return@setOnClickListener
                }
                logEvent(
                    activity,
                    Events.SIGNIN_CLICK
                )

                passwordPresenter!!.register(
                    validEmail,
                    view.edittextPassword.text.toString(),
                    true
                )

            }
        }
    }

    fun enterRegCompleteFlow(isFirstTime: Boolean) {
        step = "5"
        view.nestedScroll.visibility = View.VISIBLE
        view.email_flow.visibility = View.GONE
        view.reset_password_flow.visibility = View.GONE
        view.username_flow.visibility = View.GONE
        view.welcome_back_flow.visibility = View.GONE
        view.back.visibility = View.GONE
        view.password_flow.visibility = View.GONE
        view.terms_flow.visibility = View.GONE
        view.reg_complete_flow.visibility = View.VISIBLE
        view.closeBtn.visibility = View.VISIBLE
        if (isFirstTime) {
            regCompleteFunc()
        }
    }

    private fun regCompleteFunc() {
        view.continueRegComplete.setOnClickListener {
            step = "5"
//            var intent: Intent? = null
//            intent = Intent(activity, OnBoardingActivity::class.java)
//            view.tint.visibility = View.GONE
//            activity.startActivity(intent)
            view.btn_reg_text.visibility = View.GONE
            view.btn_reg_progress.visibility = View.VISIBLE
            presenter.checkEmail(view.edittextEmail.text.toString())
        }
    }

    private fun enterWelcomeBackFlow(isFirstTime: Boolean) {
        step = "6"
        view.nestedScroll.visibility = View.VISIBLE
        view.email_flow.visibility = View.GONE
        view.username_flow.visibility = View.GONE
        view.reset_password_flow.visibility = View.GONE
        view.welcome_back_flow.visibility = View.VISIBLE
        view.back.visibility = View.VISIBLE
        view.password_flow.visibility = View.GONE
        view.terms_flow.visibility = View.GONE
        view.reg_complete_flow.visibility = View.GONE
        view.closeBtn.visibility = View.VISIBLE
        view.welcomePassword.requestFocus()
        if (isFirstTime) {
            welcomeBackFunc()
        }
    }

    private fun welcomeBackFunc() {

        view.continueWelcomePassword.isEnabled = false
        view.frame_welcome_password.background =
            activity.getDrawable(R.drawable.normal_edittext_theme)
        view.welcomePassword.setTextColor((ContextCompat.getColor(activity, R.color.black)))
        view.welcome_btn_color.setBackgroundColor(
            (ContextCompat.getColor(
                activity,
                R.color.disable_btn
            ))
        )
        view.welcome_btn_text.setTextColor((ContextCompat.getColor(activity, R.color.edittextHint)))
        view.welcome_btn_text.text = activity.getString(R.string.continuee)
        view.welcomePassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                view.errorWelcome.text = ""
                view.errorWelcome.visibility = View.VISIBLE
                view.frame_welcome_password.background =
                    activity.getDrawable(R.drawable.normal_edittext_theme)
                view.welcomePassword.setTextColor(
                    (ContextCompat.getColor(
                        activity,
                        R.color.black
                    ))
                )
                validPassword = ""

                if (s!!.isEmpty() || s.length < 8) {
                    view.continueWelcomePassword.isEnabled = false
                    view.welcome_btn_color.setBackgroundColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.disable_btn
                        ))
                    )
                    view.welcome_btn_text.setTextColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.edittextHint
                        ))
                    )

                } else {

                    validPassword = view.welcomePassword.text.toString()
                    view.continueWelcomePassword.isEnabled = true
                    view.welcome_btn_color.setBackgroundColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.theme_color_1
                        ))
                    )
                    view.welcome_btn_text.setTextColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.white
                        ))
                    )
                    view.welcome_btn_text.text = activity.getString(R.string.continuee)
                }
            }
        })

        view.continueWelcomePassword.setOnClickListener {

            if (!InternetCheckHelper.isConnected()) {
                Utils.showPopupMessageWithCloseButton(
                    activity,
                    2000,
                    activity.getString(R.string.internet_error),
                    true
                )
                return@setOnClickListener
            }
            view.welcome_btn_progress.visibility = View.VISIBLE
            view.welcome_btn_text.visibility = View.GONE

            logEvent(
                activity,
                Events.SIGNIN_CLICK
            )

            Log.e("CALLXSXS", "EMAIL : " + view.edittextEmail.text.toString())
            Log.e("CALLXSXS", "password : " + view.welcomePassword.text.toString())

            passwordPresenter!!.normalLogin(
                view.edittextEmail.text.toString(),
                view.welcomePassword.text.toString()
            )
        }
//forgot password flow
        view.forgot_password.setOnClickListener {
            enterResetPasswordFlow(true)
        }

    }

    private fun enterResetPasswordFlow(isFirstTime: Boolean) {
        step = "7"
        view.nestedScroll.visibility = View.VISIBLE
        view.email_flow.visibility = View.GONE
        view.username_flow.visibility = View.GONE
        view.reset_password_flow.visibility = View.VISIBLE
        view.welcome_back_flow.visibility = View.GONE
        view.back.visibility = View.GONE
        view.password_flow.visibility = View.GONE
        view.terms_flow.visibility = View.GONE
        view.reg_complete_flow.visibility = View.GONE
        view.closeBtn.visibility = View.VISIBLE
        if (isFirstTime) {
            resetEmailFlowFunc()
        }
// sprint 2 commented shifa
//        view.sendLink.isEnabled = true
//        //commenting for deeplink
//        view.sendLink.setOnClickListener {
//            enterCreateNewPasswordFlow(true)
//        }
    }

    private fun enterCreateNewPasswordFlow(isFirstTime: Boolean) {
        step = "8"
        view.nestedScroll.visibility = View.VISIBLE
        view.email_flow.visibility = View.GONE
        view.username_flow.visibility = View.GONE
        view.reset_password_flow.visibility = View.GONE
        view.create_new_password.visibility = View.VISIBLE
        view.welcome_back_flow.visibility = View.GONE
        view.back.visibility = View.GONE
        view.password_flow.visibility = View.GONE
        view.terms_flow.visibility = View.GONE
        view.reg_complete_flow.visibility = View.GONE
        view.closeBtn.visibility = View.VISIBLE
        if (isFirstTime) {
            createPasswordCheck()
        }
    }

    private fun resetEmailFlowFunc() {
        view.sendLink.isEnabled = false
        view.frame_reset.background = activity.getDrawable(R.drawable.normal_edittext_theme)
        view.resetEmail.setTextColor((ContextCompat.getColor(activity, R.color.black)))
        view.link_btn_color.setBackgroundColor(
            (ContextCompat.getColor(
                activity,
                R.color.disable_btn
            ))
        )
        view.link_btn_text.setTextColor(
            (ContextCompat.getColor(
                activity,
                R.color.edittextHint
            ))
        )
        view.link_btn_text.text = activity.getString(R.string.send_reset_link)


        view.resetEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                view.errorResetEmail.text = ""
                view.errorResetEmail.visibility = View.VISIBLE
                view.frame_reset.background = activity.getDrawable(R.drawable.normal_edittext_theme)
                view.resetEmail.setTextColor((ContextCompat.getColor(activity, R.color.black)))

                view.link_btn_color.setBackgroundColor(
                    (ContextCompat.getColor(
                        activity,
                        R.color.theme_color_1
                    ))
                )
                view.link_btn_text.setTextColor((ContextCompat.getColor(activity, R.color.white)))
                view.link_btn_text.text = activity.getString(R.string.send_reset_link)

                if (s!!.isEmpty()) {
                    view.sendLink.isEnabled = false
                    view.link_btn_color.setBackgroundColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.disable_btn
                        ))
                    )
                    view.link_btn_text.setTextColor(
                        (ContextCompat.getColor(
                            activity,
                            R.color.edittextHint
                        ))
                    )

                } else {
                    if (!TextUtils.isEmpty(view.resetEmail.text) && Utils.isValidEmail(
                            view.resetEmail.text.toString()
                        )
                    ) {
                        view.sendLink.isEnabled = true
                    } else {
                        view.sendLink.isEnabled = false
                        view.link_btn_color.setBackgroundColor(
                            (ContextCompat.getColor(
                                activity,
                                R.color.disable_btn
                            ))
                        )
                        view.link_btn_text.setTextColor(
                            (ContextCompat.getColor(
                                activity,
                                R.color.edittextHint
                            ))
                        )
                    }
                }
            }
        })
        //reset password link
        view.sendLink.setOnClickListener {
            if (!InternetCheckHelper.isConnected()) {
                Utils.showPopupMessageWithCloseButton(
                    activity,
                    2000,
                    activity.getString(R.string.internet_error),
                    true
                )
                return@setOnClickListener
            }
            view.link_btn_progress.visibility = View.VISIBLE
            view.link_btn_text.visibility = View.GONE

            passwordPresenter!!.forgotPassword(validEmail)

        }
    }

    fun isValidPassword(password: String): Boolean {
        val passwordREGEX = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    ".{8,}" +               //at least 8 characters
                    "$"
        );
        return passwordREGEX.matcher(password).matches()
    }

    private fun backward() {
        when (step) {
            "1" -> print("step == 1")
            "2" -> enterEmailFlow(false)
            "3" -> enterEmailFlow(false)
            "4" -> enterPasswordFlow(false)
            "5" -> print("step == 5")
            "6" -> enterEmailFlow(false)
            else -> { // Note the block
                print("step is neither 1 nor 5")
            }
        }
    }

    private fun setUsernameValidIcon() {
        view.continueUsername.isEnabled = true
        view.username_progress.visibility = View.INVISIBLE
        view.username_icon.setImageResource(R.drawable.ic_username_check)
        view.username_icon.visibility = View.VISIBLE

        view.btn_username_color.setBackgroundColor(
            (ContextCompat.getColor(
                activity,
                R.color.theme_color_1
            ))
        )
        view.btn_username_text.setTextColor(
            (ContextCompat.getColor(
                activity,
                R.color.white
            ))
        )
        view.btn_username_text.text = activity.getString(R.string.continuee)
    }

    private fun setUsernameInvalidIcon() {
        view.username_progress.visibility = View.INVISIBLE
        view.username_icon.setImageResource(R.drawable.ic_username_cross)
        view.username_icon.visibility = View.VISIBLE

        view.continueUsername.isEnabled = false
        view.btn_username_color.setBackgroundColor(
            (ContextCompat.getColor(
                activity,
                R.color.disable_btn
            ))
        )
        view.btn_username_text.setTextColor(
            (ContextCompat.getColor(
                activity,
                R.color.edittextHint
            ))
        )

        error(
            view.errorUsername,
            view.frame_username,
            view.edittextUsername,
            view.btn_username_color,
            view.btn_username_text,
            activity.getString(R.string.already_taken)
        )

    }

    private fun setUsernameLoading() {
        view.username_progress.visibility = View.VISIBLE
        view.username_icon.setImageResource(R.drawable.ic_username_cross)
        view.username_icon.visibility = View.GONE
    }

    fun condition(text: TextView, icon: ImageView, flag: Boolean) {
        if (flag) {
            text.setTextColor(
                (ContextCompat.getColor(
                    activity,
                    R.color.green2
                ))
            )
            icon.setColorFilter(
                ContextCompat.getColor(activity, R.color.green2),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
        } else {
            text.setTextColor(
                (ContextCompat.getColor(
                    activity,
                    R.color.icon
                ))
            )
            icon.setColorFilter(
                ContextCompat.getColor(activity, R.color.icon),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
        }
    }

    fun is8Char(password: String?): Boolean {
        val passwordREGEX = Pattern.compile(
            "^" +
                    ".{8,}" +               //at least 8 characters
                    "$"
        )
        return passwordREGEX.matcher(password).matches()
    }

    fun isOneDigit(password: String?): Boolean {
        //at least 1 digit
        return Pattern.compile("[0-9]").matcher(password).find()
    }

    fun isOneUpperCase(password: String?): Boolean {
        //at least 1 upper case letter
        return Pattern.compile("[A-Z]").matcher(password).find()
    }

    private fun googleSignIn() {
        step = "7"
        if (GoogleSignIn.getLastSignedInAccount(activity) == null) {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            activity.startActivityForResult(signInIntent, RC_SIGN_IN)
        } else {
            mGoogleSignInClient!!.signOut()
            googleSignIn()
        }
    }

    override fun loaderShow(flag: Boolean) {
        if (flag) {
            view.tint.visibility = View.VISIBLE
            if (step == "4" || step == "7")
                view.progress.visibility = View.VISIBLE
        } else {
            view.tint.visibility = View.GONE
            view.progress.visibility = View.GONE
            view.btn_google_progress.visibility = View.GONE
            view.btn_fb_progress.visibility = View.GONE
            view.btn_next_progress.visibility = View.GONE
            view.btn_reg_progress.visibility = View.GONE
            view.welcome_btn_progress.visibility = View.GONE
            view.btn_next_text.visibility = View.VISIBLE
            view.btn_google_text.visibility = View.VISIBLE
            view.btn_fb_text.visibility = View.VISIBLE
            view.btn_reg_text.visibility = View.VISIBLE
            view.welcome_btn_text.visibility = View.VISIBLE
        }
    }

    override fun error(error: String?, img: String?) {

    }

    override fun success() {

    }

    override fun error(error: String) {
        when (step) {
            "1" -> {
                error(
                    view.errorEmail,
                    view.edittext,
                    view.edittextEmail,
                    view.btn_color,
                    view.btn_next_text,
                    error
                )
            }
            "3" -> {
                error(
                    view.errorWelcome,
                    view.frame_welcome_password,
                    view.welcomePassword,
                    view.welcome_btn_color,
                    view.welcome_btn_text,
                    error
                )
            }
            "6" -> {
                error(
                    view.errorWelcome,
                    view.frame_welcome_password,
                    view.welcomePassword,
                    view.welcome_btn_color,
                    view.welcome_btn_text,
                    error
                )
            }
            //commenting for sprint 2 shifa
//            "8" -> {
//                error(
//                    view.error1,
//                    view.frame_password1,
//                    view.edittextPassword1,
//                    view.btn_save_color1,
//                    view.btn_save_text1,
//                    error
//                )
//            }
            else -> {
                Utils.showPopupMessageWithCloseButton(
                    activity,
                    3000,
                    error,
                    true
                )
            }
        }

    }

    override fun error404(error: String?) {
        when (step) {
            "1" -> {
                error(
                    view.errorEmail,
                    view.edittext,
                    view.edittextEmail,
                    view.btn_color,
                    view.btn_next_text,
                    error
                )
            }
            "3" -> {
                error(
                    view.errorWelcome,
                    view.frame_welcome_password,
                    view.welcomePassword,
                    view.welcome_btn_color,
                    view.welcome_btn_text,
                    error
                )
            }
            "6" -> {
                error(
                    view.errorWelcome,
                    view.frame_welcome_password,
                    view.welcomePassword,
                    view.welcome_btn_color,
                    view.welcome_btn_text,
                    error
                )
            }
            else -> {
                Utils.showPopupMessageWithCloseButton(
                    activity,
                    3000,
                    error,
                    true
                )
            }
        }
    }

    private fun error(
        errorTextView: TextView,
        frame: View,
        edittext: EditText,
        buttonColor: RelativeLayout,
        buttonTextColor: TextView,
        error: String?
    ) {
        errorTextView.text = error
        frame.background = activity.getDrawable(R.drawable.error_edittext_theme)
        edittext.setTextColor((ContextCompat.getColor(activity, R.color.theme_color_red)))
        buttonColor.setBackgroundColor((ContextCompat.getColor(activity, R.color.disable_btn)))
        buttonTextColor.setTextColor((ContextCompat.getColor(activity, R.color.edittextHint)))
    }

    override fun onUserConfigSuccess(userConfigModel: UserConfigModel?) {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d(TAG, "onComplete: fcm  == $token")
                preference.firebaseToken = token
                OneSignal.setExternalUserId(token)
                fcmPresenter!!.sentTokenToServer(preference)
            })
            if (!TextUtils.isEmpty(preference.isLanguagePushedToServer)) {
                mSocialLoginPresenter?.selectRegion(preference.selectedRegion, preference)
                mSocialLoginPresenter?.selectLanguage(preference.isLanguagePushedToServer)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (userConfigModel!!.isOnboarded) {
            loadReels()
        } else {
            when (step) {
                "6" -> {
                    var intent: Intent? = null
                    intent = Intent(activity, ProfileNameActivity::class.java)
                    intent.putExtra("onboard", userConfigModel.isOnboarded)
                    intent.putExtra("isPopUpLogin", isPopup)
                    view.tint.visibility = View.GONE
                    activity.startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL)
                }
                "4" -> enterRegCompleteFlow(true)
                "7" -> {
                    var intent: Intent? = null
                    intent = Intent(activity, ProfileNameActivity::class.java)
                    intent.putExtra("onboard", userConfigModel.isOnboarded)
                    intent.putExtra("isPopUpLogin", isPopup)
                    view.tint.visibility = View.GONE
                    activity.startActivityForResult(intent, LoginPopupActivity.LOGIN_WITH_EMAIL)
                }
            }
        }
    }

    private fun loadReels() {
        loaderShow(true)
        val call = ApiClient.getInstance(activity)
            .api
            .newsReel(
                "Bearer " + preference.accessToken,
                "",
                "",
                "",
                Constants.REELS_FOR_YOU,
                BuildConfig.DEBUG
            )

        call.enqueue(object : Callback<ReelResponse?> {
            override fun onResponse(call: Call<ReelResponse?>, response: Response<ReelResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val dbHandler = DbHandler(activity)
                    dbHandler.insertReelList("ReelsList", Gson().toJson(response.body()))
                }
                loaderShow(false)
                gotoHome()
            }

            override fun onFailure(call: Call<ReelResponse?>, t: Throwable) {
                loaderShow(false)
                gotoHome()
            }
        })
    }

    private fun gotoHome() {
        view.tint.visibility = View.GONE
        val intent = Intent(activity, MainActivityNew::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        updateWidget()
    }

    private fun updateWidget() {
        try {
            if (AppWidgetManager.getInstance(activity)
                    .getAppWidgetIds(ComponentName(activity, CollectionWidget::class.java))
                    .isNotEmpty()
            ) {
                val updateWidget = Intent(activity, CollectionWidget::class.java)
                updateWidget.action = "update_widget"
                val pending = PendingIntent.getBroadcast(
                    activity,
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

    override fun success(flag: Boolean) {
        when (step) {
            "1" -> {
                preference.newUserFlag = !flag
                validEmail = view.edittextEmail.text.toString()
                if (flag) {
                    enterWelcomeBackFlow(true)
                } else {
                    view.edittextUsername.text = null
                    enterPasswordFlow(true)
                    view.username_icon.visibility = View.INVISIBLE
                }
            }
            "3" -> {
                if (flag) {
                    configPresenter!!.getUserConfig(false)
                }
            }
            "4" -> {
                if (flag) {
                    enterRegCompleteFlow(true)
                }
            }
            "5" -> {
                if (flag) {
                    validPassword = view.edittextPassword.text.toString()
                    step = "6" // email is verified
                    passwordPresenter!!.normalLogin(validEmail, validPassword)
                } else {
                    Utils.showPopupMessageWithCloseButton(
                        activity,
                        3000,
                        activity.getString(R.string.email_not_verified),
                        true
                    )
                }
            }
            "6" -> {
                if (flag) {
                    configPresenter!!.getUserConfig(false)
                }
            }
            "7" -> {
                if (flag) {
                    configPresenter!!.getUserConfig(false)
                }
            }
            //commenting sprint 2 shifa
//            "8" -> {
//                if (flag) {
//                    configPresenter!!.getUserConfig(false)
//                }
//            }
        }


    }

    override fun reset() {

    }

    override fun resetSuccess(str: String?) {
        view.link_btn_progress.visibility = View.GONE
        view.link_btn_text.visibility = View.VISIBLE

        Utils.showPopupMessageWithCloseButton(activity, 3000, str, false)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.e(
            TAG,
            "onActivityResult() called with: requestCode = [$requestCode], resultCode = [$resultCode], data = [$data]"
        )
        fbCallbackManager?.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else if (requestCode == LoginPopupActivity.LOGIN_WITH_EMAIL) {
            if (resultCode == RESULT_OK) {
                val result = data.getBooleanExtra("result", false)
                if (result) {
                    Log.e("####", true.toString())
                    passDataToPreviousActivity()
                }
            }
            if (resultCode == BaseActivity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    private fun passDataToPreviousActivity() {
        if (preference != null) {
            preference.isGuestUser = false
            preference.isProfileChange = true
        }
        val returnIntent = Intent()
        returnIntent.putExtra("result", true)
        activity.setResult(RESULT_OK, returnIntent)
        activity.finish()
        activity.overridePendingTransition(R.anim.no_animation, R.anim.slide_out_up)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            mSocialLoginPresenter!!.googleLogin(account.idToken)
        } catch (e: ApiException) {
            Utils.showPopupMessageWithCloseButton(
                activity,
                2000,
                activity.getString(R.string.server_error),
                true
            )
            Log.d(TAG, "handleSignInResult: ${e.localizedMessage ?: ""}")
            e.printStackTrace()
        }
    }

}