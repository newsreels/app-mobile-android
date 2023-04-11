package com.ziro.bullet.bottomSheet

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.NonNull
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig

class RegisterLoginBottomSheet(
    val activity: Activity,
) {
    private var preference: PrefConfig? = null
    private var helper: RegisterLoginHelper? = null
    private var dialog: BottomSheetDialog =
        BottomSheetDialog(activity, R.style.BottomSheetDialogThemeFloating)

    fun show() {
        preference = PrefConfig(activity)
        val dialogView: View =
            activity.layoutInflater.inflate(R.layout.register_login_bottom_sheet, null)
        dialog.setContentView(dialogView)
//        helper = RegisterLoginHelper(activity, preference!!, dialogView, dialog)
//        helper!!.init()
//        helper!!.enterEmailFlow(true)

        dialog.dismissWithAnimation = true
        dialog.setOnDismissListener(null)
        dialog.setOnShowListener {
            val d = dialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state =
                    BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(bottomSheetCallback)
            }
        }
        dialog.show()
    }

    fun loaderReset() {
        helper!!.loaderShow(false)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        helper!!.onActivityResult(requestCode,resultCode,data)
    }

    private val bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {}

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
        }

}