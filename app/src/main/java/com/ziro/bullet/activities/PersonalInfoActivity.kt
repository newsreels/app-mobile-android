package com.ziro.bullet.activities

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.auth.OAuthAPIClient
import com.ziro.bullet.background.VideoProcessorService
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.models.userInfo.User
import com.ziro.bullet.interfaces.ApiCallbacks
import com.ziro.bullet.interfaces.ProfileApiCallback
import com.ziro.bullet.onboarding.ui.WelcomeActivity
import com.ziro.bullet.presenter.ProfilePresenter
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.Utils
import com.ziro.bullet.widget.CollectionWidget
import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.activity_post_settings.back
import kotlinx.android.synthetic.main.del_acc_confirm_dialog.view.*
import kotlinx.android.synthetic.main.progress.*
import java.io.File
import java.io.IOException


class PersonalInfoActivity : BaseActivity(), View.OnClickListener, ProfileApiCallback {

    private var profileImageFile: File? = null
    private lateinit var validUsername: String
    private var profilePresenter: ProfilePresenter? = null
    private var isUsernameValid: Boolean = false
    private var user: User? = null
    private var mPrefConfig: PrefConfig? = null
    val PROFILE_IMAGE_REQUEST = 10
    val PERMISSION_REQUEST = 745
    private var cacheManager: DbHandler? = null
    private val TAG = "ProfileImageActivity"
    private val PERMISSIONS = mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_IMAGES)
//            add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    //dialog
    private var delAccConfirmationDialog: AlertDialog? = null
    private var isDelAcc = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBarColor(this)
        setContentView(R.layout.activity_personal_info)
        init()
        setClickListeners()
        setData()
        initDialog()
    }

    private fun initDialog() {
        val builder = AlertDialog.Builder(this)
        val delAccDialogView =
            LayoutInflater.from(this).inflate(R.layout.del_acc_confirm_dialog, null, false)
        builder.setView(delAccDialogView)
        builder.setCancelable(true)
        delAccConfirmationDialog = builder.create()
        delAccConfirmationDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        delAccConfirmationDialog?.setCanceledOnTouchOutside(true)
        delAccDialogView.tv_cancel_del_acc.setOnClickListener {
            delAccConfirmationDialog?.dismiss()
        }

        delAccDialogView.tv_del_acc_confirm.setOnClickListener {
            progress.visibility = VISIBLE
            delAccConfirmationDialog?.dismiss()
            profilePresenter?.deleteAccount("Bearer ${mPrefConfig?.accessToken}",
                object : ApiCallbacks {
                    override fun loaderShow(flag: Boolean) {
                        progress.visibility = if (flag) VISIBLE else GONE
                    }

                    override fun error(error: String?) {
                        progress.visibility = GONE
                        Utils.showSnacky(window.decorView.rootView, error)
                    }

                    override fun error404(error: String?) {
                        progress.visibility = GONE
                        Utils.showSnacky(window.decorView.rootView, error)
                    }

                    override fun success(response: Any?) {
                        logout()
                    }
                })
        }
    }

    private fun init() {
        mPrefConfig = PrefConfig(this)
        cacheManager = DbHandler(this)
        profilePresenter = ProfilePresenter(this@PersonalInfoActivity, this)
    }


    fun setData() {
        continue_.isEnabled = false
        btn_color.setBackgroundColor(
            (ContextCompat.getColor(
                this, R.color.disable_btn
            ))
        )
        btn_text.setTextColor(
            (ContextCompat.getColor(
                this, R.color.edittextHint
            ))
        )
        if (mPrefConfig != null) {
            user = mPrefConfig!!.isUserObject
            if (user != null) {
                if (!TextUtils.isEmpty(user!!.name)) {
                    edittextName.setText(String.format("%s", user!!.name))
                }
                if (!TextUtils.isEmpty(user!!.username)) {
                    edittextUsername.setText(String.format("%s", user!!.username))
                }
                if (!TextUtils.isEmpty(user!!.profile_image)) {
                    Picasso.get().load(user!!.profile_image)
                        .placeholder(R.drawable.ic_placeholder_user).into(roundedImageView)
                } else {
                    roundedImageView.setImageResource(R.drawable.ic_placeholder_user)
                }
            }
        }

        edittextName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                continue_.isEnabled = true
                btn_color.setBackgroundColor(
                    (ContextCompat.getColor(
                        this@PersonalInfoActivity, R.color.theme_color_1
                    ))
                )
                btn_text.setTextColor(
                    (ContextCompat.getColor(
                        this@PersonalInfoActivity, R.color.white
                    ))
                )
            }
        })

//        edittextUsername.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                errorUsername.text = ""
//                errorUsername.visibility = VISIBLE
//                username_icon.visibility = INVISIBLE
//                frame_username.background = getDrawable(R.drawable.normal_edittext_theme)
//                edittextUsername.setTextColor(
//                    (ContextCompat.getColor(
//                        this@PersonalInfoActivity, R.color.black
//                    ))
//                )
//
//                continue_.isEnabled = false
//                btn_color.setBackgroundColor(
//                    (ContextCompat.getColor(
//                        this@PersonalInfoActivity, R.color.disable_btn
//                    ))
//                )
//                btn_text.setTextColor(
//                    (ContextCompat.getColor(
//                        this@PersonalInfoActivity, R.color.edittextHint
//                    ))
//                )
//                OAuthAPIClient.cancelAll()
//                isUsernameValid = false
//                val usernameCopy = s.toString()
//                if (!s!!.isNullOrEmpty()) {
//                    profilePresenter!!.checkUsername(usernameCopy, object : ApiCallbacks {
//                        override fun loaderShow(flag: Boolean) {
//                            if (flag) {
//                                setUsernameLoading()
//                            }
//                        }
//
//                        override fun error(error: String?) {
//                            error(
//                                errorUsername,
//                                frame_username,
//                                edittextUsername,
//                                btn_color,
//                                btn_text,
//                                error
//                            )
//                        }
//
//                        override fun error404(error: String?) {
//                            error(
//                                errorUsername,
//                                frame_username,
//                                edittextUsername,
//                                btn_color,
//                                btn_text,
//                                error
//                            )
//                        }
//
//                        override fun success(response: Any?) {
//                            if (response is Boolean) {
//                                if (response) {
//                                    validUsername = usernameCopy
//                                    isUsernameValid = true
//                                    continue_.isEnabled = true
//                                    setUsernameValidIcon()
//                                } else {
//                                    isUsernameValid = false
//                                    setUsernameInvalidIcon()
//                                }
//                            } else {
//                                isUsernameValid = false
//                                setUsernameInvalidIcon()
//                            }
//                        }
//                    })
//                }
//            }
//        })
    }

    private fun setUsernameValidIcon() {
        continue_.isEnabled = true
        username_progress.visibility = INVISIBLE
        username_icon.setImageResource(R.drawable.ic_username_check)
        username_icon.visibility = VISIBLE

        btn_color.setBackgroundColor(
            (ContextCompat.getColor(
                this, R.color.theme_color_1
            ))
        )
        btn_text.setTextColor(
            (ContextCompat.getColor(
                this, R.color.white
            ))
        )
        btn_text.text = getString(R.string.continuee)
    }

    private fun setUsernameInvalidIcon() {
        username_progress.visibility = INVISIBLE
        username_icon.setImageResource(R.drawable.ic_username_cross)
        username_icon.visibility = VISIBLE

        continue_.isEnabled = false
        btn_color.setBackgroundColor(
            (ContextCompat.getColor(
                this, R.color.disable_btn
            ))
        )
        btn_text.setTextColor(
            (ContextCompat.getColor(
                this, R.color.edittextHint
            ))
        )

        error(
            errorUsername,
            frame_username,
            edittextUsername,
            btn_color,
            btn_text,
            getString(R.string.already_taken)
        )

    }

    private fun setUsernameLoading() {
        username_progress.visibility = VISIBLE
        username_icon.setImageResource(R.drawable.ic_username_cross)
        username_icon.visibility = GONE
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
        frame.background =
            ResourcesCompat.getDrawable(this.resources, R.drawable.error_edittext_theme, null)
        edittext.setTextColor((ContextCompat.getColor(this@PersonalInfoActivity, R.color.red)))
        buttonColor.setBackgroundColor(
            (ContextCompat.getColor(
                this@PersonalInfoActivity, R.color.disable_btn
            ))
        )
        buttonTextColor.setTextColor(
            (ContextCompat.getColor(
                this@PersonalInfoActivity, R.color.edittextHint
            ))
        )
    }

    private fun setClickListeners() {
        back.setOnClickListener(this)
        set_image.setOnClickListener(this)
        continue_.setOnClickListener(this)
        tv_del_acc.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.back -> {
                onBackPressed()
            }
            R.id.set_image -> {
                fromGallery(ProfileImageActivity.PROFILE_IMAGE_REQUEST)
            }
            R.id.continue_ -> {
                update()
            }
            R.id.tv_del_acc -> {
                delAccConfirmationDialog?.show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == ProfileImageActivity.PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fromGallery(ProfileImageActivity.PROFILE_IMAGE_REQUEST)
            }
        }
    }

    private fun fromGallery(type: Int) {
        if (!checkPermissions(*PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                this, PERMISSIONS, ProfileImageActivity.PERMISSION_REQUEST
            )
            error(getString(R.string.permision), null)
            return
        }

        val mimeTypes = arrayOf("image/jpeg", "image/jpg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            startActivityForResult(intent, type)
        } else {
            val pickPhoto = Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).setType("image/jpg").putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(pickPhoto, type)
        }
    }

//    private fun selectSingleImage() {
//
//        val resultContract = object : ActivityResultContract<Unit, Uri>() {
//            override fun createIntent(context: Context, input: Unit): Intent {
//
//            }
//
//            override fun parseResult(resultCode: Int, intent: Intent?): Uri {
//
//            }
//        }
//
////
////        // Registers a photo picker activity launcher in single-select mode.
////        val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
////            // Callback is invoked after the user selects a media item or closes the
////            // photo picker.
////            if (uri != null) {
////                Log.d("PhotoPicker", "Selected URI: $uri")
////            } else {
////                Log.d("PhotoPicker", "No media selected")
////            }
////        }
////
////// Include only one of the following calls to launch(), depending on the types
////// of media that you want to allow the user to choose from.
////
////// Launch the photo picker and allow the user to choose images and videos.
////        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
////
////// Launch the photo picker and allow the user to choose only images.
////        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
////
////// Launch the photo picker and allow the user to choose only videos.
////        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
////
////// Launch the photo picker and allow the user to choose only images/videos of a
////// specific MIME type, such as GIFs.
////        val mimeType = "image/gif"
////        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.SingleMimeType(mimeType)))
//    }

    private fun checkPermissions(vararg permissions: String?): Boolean {
        if (permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext, permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun update() {
        var username = edittextUsername.text.toString()
        if (TextUtils.isEmpty(username)) {
            return
        }

        var name = edittextName.text.toString()
        if (TextUtils.isEmpty(name)) {
            return
        }
        profilePresenter!!.updateProfile(name, profileImageFile, null, false, username)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ProfileImageActivity.PROFILE_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            try {
                if (data.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        val selectedImagePath = getRealPathFromURI(imageUri)
                        val file = File(selectedImagePath)
                        loadSelectedImage(requestCode, file, imageUri)
                    }
                } else if (data.data != null) {
                    val selectedImagePath = getRealPathFromURI(data.data)
                    val file = File(selectedImagePath)
                    loadSelectedImage(requestCode, file, data.data!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val path = cursor.getString(idx)
            cursor.close()
            return path
        }
        return ""
    }


    private fun loadSelectedImage(type: Int, file: File, uri: Uri) {

        continue_.isEnabled = true
        btn_color.setBackgroundColor(
            (ContextCompat.getColor(
                this@PersonalInfoActivity, R.color.theme_color_1
            ))
        )
        btn_text.setTextColor(
            (ContextCompat.getColor(
                this@PersonalInfoActivity, R.color.white
            ))
        )

        val modifiedFile: File = try {
            Utils.getFileFromBitmap(
                this, "imageProfile", Utils.modifyOrientation(file.absolutePath)
            )
        } catch (e: IOException) {
            e.printStackTrace()
            file
        }
        if (type == ProfileImageActivity.PROFILE_IMAGE_REQUEST) {
            roundedImageView.setImageURI(uri)
            profileImageFile = modifiedFile
        }
    }


    override fun loaderShow(flag: Boolean) {
        progress.visibility = if (flag) VISIBLE else GONE
    }

    override fun error(error: String?, img: String?) {

    }

    override fun success() {
        Handler().postDelayed(Runnable { finish() }, 2000)
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
        try {
            if (!isDestroyed) {
                if (Utils.isMyServiceRunning(this, VideoProcessorService::class.java)) {
                    stopService(Intent(this, VideoProcessorService::class.java))
                }
            }
            if (cacheManager != null) {
                cacheManager!!.clearDb()
            }
            if (!isDestroyed) {
                val notificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        progress.visibility = View.GONE
        Constants.onResumeReels = true
        updateWidget()
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        if (!isDestroyed) {
            finish()
        }
    }

    private fun updateWidget() {
        try {
            if (!isDestroyed) if (AppWidgetManager.getInstance(this).getAppWidgetIds(
                    ComponentName(
                        this!!, CollectionWidget::class.java
                    )
                ).isNotEmpty()
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
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }
    }
}