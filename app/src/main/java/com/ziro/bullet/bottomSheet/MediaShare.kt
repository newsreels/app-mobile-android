package com.ziro.bullet.bottomSheet

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Telephony
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ziro.bullet.R
import com.ziro.bullet.adapters.DirectShare
import com.ziro.bullet.adapters.ReelShareAdapter
import com.ziro.bullet.analytics.AnalyticsEvents
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.data.models.ShareInfo
import com.ziro.bullet.interfaces.ReelBottomSheetCallback
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog
import com.ziro.bullet.mediapicker.permissions.PermissionChecker
import com.ziro.bullet.model.articles.Article
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.InternetCheckHelper
import com.ziro.bullet.utills.ProcessVideo
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.reel_bottom_sheet.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MediaShare(
    val activity: Activity,
    val fragment: Fragment?,
    val mShareInfo: ShareInfo?,
    val article: Article?,
    val callback: ReelBottomSheetCallback?,
    val onDismissListener: DialogInterface.OnDismissListener?,
    val isArticle: Boolean?,
    val id: String?
) {
    private val logoWatermark = "newsreelslogo1.png"
    private val endingVideo = "ending_video.mp4"
    private val endingVideoLandscape = "ending_video_landscape.mp4"

    private var dialog: BottomSheetDialog =
        BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
    private var whatsappLabel = "Whatsapp"
    private var whatsappStatusLabel = "Whatsapp Status"
    private var instagramLabel = "Instagram"
    private var instagramStatusLabel = "Stories"
    private var facebookLabel = "Facebook"
    private var facebookStatusLabel = "Stories"

    private val nameOfAppsToShareWith = arrayOf(
        "com.whatsapp",
        "com.facebook.katana",
        "com.facebook.orca",
        "com.facebook.mlite" //            , "com.instagram.android"
        ,
        "com.snapchat.android",
        "org.telegram.messenger",
        "com.twitter.android",
        "com.tencent.mm",
        "jp.naver.line.android",
        "com.skype.raider" //            , "org.thoughtcrime.securesms"
        ,
        "com.viber.voip",
        "kik.android" //            , "com.google.android.apps.messaging"
        ,
        "com.samsung.android.messaging"
    )
    private val nameOfAppsToDirectShareWith = arrayOf(
        "com.whatsapp",
        "com.facebook.katana",
        "com.instagram.android",
        "com.snapchat.android",
        "com.zhiliaoapp.musically",
        "org.telegram.messenger",
        "com.twitter.android",
        "com.tencent.mm",
        "jp.naver.line.android",
        "com.viber.voip",
        "kik.android"
    )
    private val whatsappPackage = "com.whatsapp"
    private val instagramPackage = "com.instagram.android"
    private val facebookPackage = "com.facebook.katana"
    private val tiktokPackage = "com.zhiliaoapp.musically"
    private val telegramPackage = "org.telegram.messenger"
    private val twitterPackage = "com.twitter.android"

    private var mLoadingDialog: PictureLoadingDialog? = null

    private constructor(builder: Builder) : this(
        builder.activity,
        builder.fragmentContext,
        builder.mShareInfo,
        builder.articleObj,
        builder.callback,
        builder.onDismissListener,
        builder.isArticleBool,
        builder.idString
    )

    companion object {
        const val PERMISSION_REQUEST_STORAGE = 31243
        inline fun build(activity: Activity, block: Builder.() -> Unit) =
            Builder(activity).apply(block).build()
    }

    class Builder(
        val activity: Activity
    ) {
        var mShareInfo: ShareInfo? = null
        var articleObj: Article? = null
        var isArticleBool: Boolean? = null
        var callback: ReelBottomSheetCallback? = null
        var onDismissListener: DialogInterface.OnDismissListener? = null
        var fragmentContext: Fragment? = null
        var idString: String? = null

        fun setShareInfo(mShareInfo: ShareInfo) = apply { this.mShareInfo = mShareInfo }

        fun setFragmentContextVal(mFragment: Fragment) = apply { this.fragmentContext = mFragment }

        fun setArticle(article: Article) = apply { this.articleObj = article }

        fun setReelBottomSheetCallback(callback: ReelBottomSheetCallback) =
            apply { this.callback = callback }

        fun setonDismissListener(onDismissListener: DialogInterface.OnDismissListener) =
            apply { this.onDismissListener = onDismissListener }

        fun isArticle(isArticle: Boolean) = apply { this.isArticleBool = isArticle }

        fun setId(id: String) = apply { this.idString = id }

        fun build() = MediaShare(this)
    }

    fun show() {
        val dialogView: View = activity.layoutInflater.inflate(R.layout.reel_bottom_sheet, null)

        if (isArticle == true) {
            dialogView.llOptions.visibility = View.GONE
            dialogView.tvShareHeader1.text = activity.getString(R.string.share_via_link)
            dialogView.tvShareHeader2.text = activity.getString(R.string.share_original_media)
        } else {
            dialogView.llOptions.visibility = View.VISIBLE
            dialogView.tvShareHeader1.text = activity.getString(R.string.share_via_link)
            dialogView.tvShareHeader2.text = activity.getString(R.string.share_original)
        }

        dialogView.btnCancel.setOnClickListener {
            hide()
        }

        dialogView.apps.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        dialogView.apps.adapter = getLinkShareAdapter()

        dialogView.appsDirect.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        dialogView.appsDirect.adapter = getDirectShareAdapter()
        if (mShareInfo != null) {
            if (mShareInfo.isArticle_archived) {
                dialogView.saveText.text = activity.resources.getString(R.string.remove_fav)
            } else {
                dialogView.saveText.text = activity.resources.getString(R.string.add_fav)
            }
        }

        dialogView.report.setOnClickListener {
//            if (callback == null) return@setOnClickListener
            callback?.onReport()
            hide()
        }

        dialogView.notInterested.setOnClickListener {
//            if (callback == null) return@setOnClickListener
            callback?.onNotInterested()
            hide()
        }

        dialogView.saveToDevice.setOnClickListener {
            if (PermissionChecker.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) &&
                PermissionChecker.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                addWaterMark(
                    false,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath,
                    null
                )
            } else {
                if (fragment != null) {
                    PermissionChecker.requestPermissions(
                        fragment, arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), PERMISSION_REQUEST_STORAGE
                    )
                } else {
                    PermissionChecker.requestPermissions(
                        activity, arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), PERMISSION_REQUEST_STORAGE
                    )
                }
            }
        }
        dialogView.save.setOnClickListener {
            callback?.onSave()
            hide()
        }

        dialog.setContentView(dialogView)
        dialog.dismissWithAnimation = true
        dialog.setOnDismissListener(onDismissListener)
        dialog.setOnShowListener {
            val d = dialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        dialog.show()
    }

    fun hide() {
        dialog.dismiss()
    }

    private fun getLinkShareAdapter(): ReelShareAdapter {
        val packageNames = ArrayList<String>()
        val template = Intent(Intent.ACTION_SEND)
        template.type = "text/plain"

        val shareList: MutableList<DirectShare?> =
            ArrayList()

        shareList.add(
            0, DirectShare(
                activity.getString(R.string.copy_link),
                ContextCompat.getDrawable(activity, R.drawable.ic_copy_link),
                null
            )
        )

        try {
            val defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(activity)

            shareList.add(
                1, DirectShare(
                    activity.getString(R.string.sms),
                    ContextCompat.getDrawable(activity, R.drawable.ic_sms),
                    defaultSmsPackage
                )
            )
        } catch (ignore: Exception) {
        }

        val candidates: List<ResolveInfo> =
            activity.packageManager.queryIntentActivities(template, 0)
        for (candidate in candidates) {
            val packageName = candidate.activityInfo.packageName
            if (!packageNames.contains(packageName)) {
                if (nameOfAppsToShareWith
                        .contains(packageName.lowercase(Locale.getDefault()))
                ) {
                    shareList.add(
                        DirectShare(
                            candidate.loadLabel(activity.packageManager).toString(),
                            candidate.activityInfo.loadIcon(activity.packageManager),
                            candidate.activityInfo.packageName
                        )
                    )
                    packageNames.add(packageName)
                }
            }
        }
        shareList.add(null)

        return ReelShareAdapter(activity, shareList, mShareInfo, object : ShareSheet {
            override fun onShare(info: DirectShare?) {
                AnalyticsEvents.logEvent(activity, Events.REEL_SHARE_SEND)

                if (info != null) {
                    if (!info.packageName.isNullOrEmpty()) {
                        val target = Intent(Intent.ACTION_SEND)
                        target.type = "text/plain"
                        if (mShareInfo != null) {
                            target.putExtra(Intent.EXTRA_TEXT, mShareInfo.share_message)
                        }
                        target.setPackage(info.packageName)
                        activity.startActivity(target)
                    } else {
                        if (mShareInfo != null) {
                            Utils.copyTextToClipboard(activity, mShareInfo.link)
                            Toast.makeText(activity, "Link copied.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    if (mShareInfo != null) {
                        sendIntent.putExtra(Intent.EXTRA_TEXT, mShareInfo.share_message)
                    }
                    sendIntent.type = "text/plain"
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    activity.startActivity(shareIntent)
                }
                callback?.onIgnore()
                dialog.dismiss()
            }
        })
    }

    private fun getDirectShareAdapter(): ReelShareAdapter {
        val packageNames = ArrayList<String>()
        val template = Intent(Intent.ACTION_SEND)
        if (isArticle == true) {
            if (article?.type?.lowercase()?.contains("video") == false)
                template.type = "image/*"
            else
                template.type = "video/mp4"
        } else
            template.type = "video/mp4"
        val candidates: List<ResolveInfo> =
            activity.packageManager.queryIntentActivities(template, 0)
        val shareList: MutableList<DirectShare?> =
            ArrayList()
        for (candidate in candidates) {
            val packageName = candidate.activityInfo.packageName
            if (!packageNames.contains(packageName)) {
                if (nameOfAppsToDirectShareWith
                        .contains(packageName.lowercase(Locale.getDefault()))
                ) {
                    when {
                        packageName.lowercase(Locale.getDefault()) == whatsappPackage -> {
                            shareList.add(
                                0, DirectShare(
                                    whatsappLabel,
                                    candidate.activityInfo.loadIcon(activity.packageManager),
                                    candidate.activityInfo.packageName
                                )
                            )
                            shareList.add(
                                1, DirectShare(
                                    whatsappStatusLabel,
                                    candidate.activityInfo.loadIcon(activity.packageManager),
                                    candidate.activityInfo.packageName
                                )
                            )
                        }
                        packageName.lowercase(Locale.getDefault()) == instagramPackage -> {
                            shareList.add(
                                DirectShare(
                                    instagramLabel,
                                    candidate.activityInfo.loadIcon(activity.packageManager),
                                    candidate.activityInfo.packageName
                                )
                            )
                            shareList.add(
                                DirectShare(
                                    instagramStatusLabel,
                                    candidate.activityInfo.loadIcon(activity.packageManager),
                                    candidate.activityInfo.packageName
                                )
                            )
                        }
                        packageName.lowercase(Locale.getDefault()) == facebookPackage -> {
                            shareList.add(
                                DirectShare(
                                    facebookLabel,
                                    candidate.activityInfo.loadIcon(activity.packageManager),
                                    candidate.activityInfo.packageName
                                )
                            )
                            shareList.add(
                                DirectShare(
                                    facebookStatusLabel,
                                    candidate.activityInfo.loadIcon(activity.packageManager),
                                    candidate.activityInfo.packageName
                                )
                            )
                        }
                        else -> {
                            shareList.add(
                                DirectShare(
                                    candidate.loadLabel(activity.packageManager).toString(),
                                    candidate.activityInfo.loadIcon(activity.packageManager),
                                    candidate.activityInfo.packageName
                                )
                            )
                        }
                    }
                    packageNames.add(packageName)
                }
            }
        }
        shareList.add(null)
        return ReelShareAdapter(activity, shareList, mShareInfo, object : ShareSheet {
            override fun onShare(info: DirectShare?) {


                if (info != null) {
                    if (isArticle == true) {
                        if (article?.type?.lowercase()?.contains("video") == false) {
                            generateArticleImage(info)
                        } else {
                            addWaterMark(
                                true,
                                activity.cacheDir.path,
                                info
                            )
                        }
                    } else {
                        AnalyticsEvents.logEvent(activity, Events.REEL_SHARE_SEND)
                        addWaterMark(
                            true,
                            activity.cacheDir.path,
                            info
                        )
                    }
                } else {
                    AnalyticsEvents.logEvent(activity, Events.REEL_SHARE_SEND)
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    if (mShareInfo != null) {
                        sendIntent.putExtra(Intent.EXTRA_TEXT, mShareInfo.share_message)
                    }
                    sendIntent.type = "text/plain"
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    activity.startActivity(shareIntent)
                }
                callback?.onIgnore()
                dialog.dismiss()
            }
        })
    }

    fun generateArticleImage(info: DirectShare) {
        if (mShareInfo != null) {
            Glide.with(activity)
                .asBitmap()
                .load(mShareInfo.media)
                .override(Constants.targetWidth, Constants.targetHeight)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    @SuppressLint("SetTextI18n")
                    override fun onResourceReady(
                        resourceCover: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        if (article != null) {
                            if (article.source != null && !TextUtils.isEmpty(
                                    article.source.name_image
                                )
                            ) {
                                if (article != null) {
                                    Glide.with(activity)
                                        .asBitmap()
                                        .load(article.source.name_image)
                                        .into(object : CustomTarget<Bitmap?>() {

                                            override fun onLoadCleared(placeholder: Drawable?) {}
                                            override fun onResourceReady(
                                                resource: Bitmap,
                                                transition: Transition<in Bitmap?>?
                                            ) {
                                                val view = LayoutInflater.from(activity)
                                                    .inflate(R.layout.image_article_share, null)
                                                val imageView =
                                                    view.findViewById<ImageView>(R.id.post_image)
                                                val ivSource =
                                                    view.findViewById<ImageView>(R.id.source)
                                                val tvSource =
                                                    view.findViewById<TextView>(R.id.banner_text)
                                                val tvHeadline =
                                                    view.findViewById<TextView>(R.id.article_title)
                                                imageView.setImageBitmap(resourceCover)
//                                                val calculateBrightness =
//                                                    Utils.calculateBrightness(resource, 100)
//                                                if (calculateBrightness < 100) {
//
//                                                } else {
//
//                                                }
                                                ivSource.setImageBitmap(resource)
                                                tvSource.visibility = View.GONE
                                                if (article.title.isNotEmpty()) {
                                                    if (
                                                        info.packageName.equals(tiktokPackage) ||
                                                        info.packageName.equals(instagramPackage) ||
                                                        info.packageName.equals(facebookPackage)
                                                    ) {
                                                        tvHeadline.visibility = View.VISIBLE
                                                        tvHeadline.text = article.title
                                                    }
                                                }
                                                shareArticleImage(view, info)
                                            }
                                        })
                                }
                            } else {
                                val view =
                                    LayoutInflater.from(activity)
                                        .inflate(R.layout.image_article_share, null)
                                val imageView = view.findViewById<ImageView>(R.id.post_image)
                                val ivSource = view.findViewById<ImageView>(R.id.source)
                                val tvSource = view.findViewById<TextView>(R.id.banner_text)
                                val tvHeadline =
                                    view.findViewById<TextView>(R.id.article_title)
                                imageView.setImageBitmap(resourceCover)
                                ivSource.visibility = View.GONE
                                tvSource.visibility = View.VISIBLE
                                if (article.sourceNameToDisplay.isNotEmpty()) {
                                    tvSource.text = article.sourceNameToDisplay + "\u0020"
                                }
                                if (article.title.isNotEmpty()) {
                                    if (
                                        info.packageName.equals(tiktokPackage) ||
                                        info.packageName.equals(instagramPackage) ||
                                        info.packageName.equals(facebookPackage)
                                    ) {
                                        tvHeadline.visibility = View.VISIBLE
                                        tvHeadline.text = article.title
                                    }
                                }
                                shareArticleImage(view, info)
                            }
                        }
                    }
                })
        }
    }

    private fun shareArticleImage(view: View, info: DirectShare) {
        val bitmapFromView = Utils.getBitmapFromView(activity, view)
        val file =
            File(activity.cacheDir, System.currentTimeMillis().toString() + "share.jpg")
        if (!file.exists()) {
            try {
                val outStream = FileOutputStream(file)
                bitmapFromView.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream.flush()
                outStream.close()
                Log.d("TAG", "show: " + file.path)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        val authority: String = activity.packageName + ".fileprovider"
        val uriForFile: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(activity, authority, file)
        else
            Uri.fromFile(file)
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.type = "image/*"
        if (mShareInfo != null) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, mShareInfo.share_message)
        }
        sendIntent.putExtra(Intent.EXTRA_STREAM, uriForFile)
        sendIntent.setPackage(info.packageName)
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)
        dialog.dismiss()
    }

    fun addToGallery(resultPath: String) {
        val file = File(resultPath)
        val values = ContentValues()
        values.put(MediaStore.Video.Media.TITLE, "Video")
        values.put(MediaStore.Video.Media.DESCRIPTION, "Edited")
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(
            MediaStore.Video.VideoColumns.BUCKET_ID,
            file.toString().toLowerCase(Locale.US).hashCode()
        )
        values.put(
            MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
            file.name.toLowerCase(Locale.US)
        )
        values.put("_data", resultPath)

        val cr: ContentResolver = activity.contentResolver
        cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun addWaterMark(
        isShare: Boolean,
        root: String,
        info: DirectShare?
    ) {
        if (!InternetCheckHelper.isConnected()) {
            Toast.makeText(
                activity,
                "" + activity.getString(R.string.network_error),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Log.d("TAG", "addWaterMark: ==== " + id)

        showLoadingView(true)

        val downloadedFile: File
        if (id.isNullOrEmpty()) {
            downloadedFile = File(root, "" + System.currentTimeMillis() + "compressed.mp4")
            try {
                downloadedFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            downloadedFile = File(root, "$id.mp4")

            if (downloadedFile.exists()) {
                val inputPath = downloadedFile.absolutePath
                val uriForFile: Uri
                val authority: String = activity.packageName + ".fileprovider"
                uriForFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(activity, authority, downloadedFile)
                } else Uri.fromFile(downloadedFile)
                showLoadingView(false)
                shareOrDownloadVideo(isShare, uriForFile, info, inputPath)
                return
            }
            try {
                downloadedFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val inputPath = downloadedFile.absolutePath
        val uriForFile: Uri

        val authority: String = activity.packageName + ".fileprovider"
        uriForFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(activity, authority, downloadedFile)
        } else Uri.fromFile(downloadedFile)

        val processVideo = ProcessVideo(activity)

        if (mShareInfo != null) {
            processVideo.download(
                mShareInfo.downloadLink,
                inputPath,
                object : ProcessVideo.DownloadProgress {
                    override fun onProgress(progress: Long, length: Long) {
//                        val l = progress.toDouble() / length * 100
                    }

                    override fun onError() {
                        showLoadingView(false)
                        if (activity != null && !activity.isFinishing) {
                            try {
                                activity.runOnUiThread {
                                    Toast.makeText(
                                        activity,
                                        "" + activity.getString(R.string.server_error),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }catch (e:Exception) {
                                e.printStackTrace()
                            }
                        }
                        Log.d("TAG", "onError: ")
                    }

                    override fun onComplete() {
//                        updateProgress(100)
                        showLoadingView(false)
                        shareOrDownloadVideo(isShare, uriForFile, info, inputPath)
                    }
                })
        }
    }

    private fun shareOrDownloadVideo(
        isShare: Boolean,
        uriForFile: Uri,
        info: DirectShare?,
        inputPath: String
    ) {
        if (isShare) {
            val target = Intent(Intent.ACTION_SEND)
            target.type = "video/*"
            target.putExtra(
                Intent.EXTRA_TEXT,
                mShareInfo?.share_message
            )
            target.putExtra(
                Intent.EXTRA_STREAM,
                uriForFile
            )
            if (info != null) {
                target.setPackage(info.packageName)
            }
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivity(
                Intent.createChooser(
                    target,
                    "Share Video"
                )
            )
        } else {
            addToGallery(inputPath)
            hide()
            try {
                activity.runOnUiThread {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.newsreel_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showLoadingView(isShow: Boolean) {
        if (activity.isFinishing) {
            return
        }
        if (isShow) {
            if (mLoadingDialog == null) {
                mLoadingDialog = PictureLoadingDialog(activity)
            } else {

            }
//            mLoadingDialog?.updateProgress(0);
            mLoadingDialog?.show()
        } else {
            if (mLoadingDialog != null
                && mLoadingDialog!!.isShowing
            ) {
                mLoadingDialog?.dismiss()
            }
        }
    }

    fun saveToDevice() {
        if (mShareInfo != null) addWaterMark(
            false,
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath,
            null
        )
    }

//    private fun updateProgress(i: Int) {
//        activity.runOnUiThread { mLoadingDialog?.updateProgress(i) }
//    }


    interface ShareSheet {
        fun onShare(info: DirectShare?)
    }
}