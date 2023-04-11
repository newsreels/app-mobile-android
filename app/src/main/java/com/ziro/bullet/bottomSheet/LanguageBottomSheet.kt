package com.ziro.bullet.bottomSheet

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.ziro.bullet.R
import com.ziro.bullet.adapters.LanguageAdapter
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.model.language.LanguageResponse
import com.ziro.bullet.model.language.LanguagesItem
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.lang_bottom_sheet.view.*


class LanguageBottomSheet(
    val activity: Activity,
) {
    private var mCode: String? = null
    private var preference: PrefConfig? = null
    private var dialog: BottomSheetDialog =
        BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
    private val languages = ArrayList<LanguagesItem>()
    private var adapter: LanguageAdapter? = null
    private var listener: ShareSheet? = null

    fun setListener(listener: ShareSheet) {
        this.listener = listener
    }

    fun show() {
        preference = PrefConfig(activity)
        adapter = LanguageAdapter(languages, activity, null, false)
        val manager = LinearLayoutManager(activity)
        val dialogView: View = activity.layoutInflater.inflate(R.layout.lang_bottom_sheet, null)
        dialogView.lang_list.layoutManager = manager
        dialogView.lang_list.adapter = adapter
        adapter!!.setCallback(object : LanguageAdapter.LanguageClickCallback {
            override fun onClick(position: Int, id: String?, code: String?) {
                var itemPos = position
                for (i in languages.indices) {
                    languages[i].isSelected = false
                    if (languages[i].id.equals(id, ignoreCase = true)) {
                        itemPos = i
                    }
                }
                if (itemPos < languages.size) {
                    languages[itemPos].isSelected = true
                    preference!!.setLanguageForServer(id)
                    preference!!.defaultLanguage = languages[itemPos]
                    listener!!.onLanguageChange(preference!!.defaultLanguage.name)
                    adapter!!.notifyDataSetChanged()
                }
                mCode = code
            }

            override fun isLastItem(flag: Boolean) {

            }

        })

        dialogView.closeBtn.setOnClickListener {
            hide()
        }
        dialogView.saveBtn.setOnClickListener {
            hide()
        }
        loadLanguages()

        dialog.setContentView(dialogView)
        dialog.dismissWithAnimation = true
        dialog.setOnDismissListener(null)
        dialog.setOnShowListener {
            val d = dialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_HALF_EXPANDED
                BottomSheetBehavior.from(bottomSheet).addBottomSheetCallback(bottomSheetCallback)
            }
        }
        dialog.show()
    }

    private val bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
//            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
    }

    fun loadLanguages() {
        val json = Utils.getJsonFromAssets(activity, "languages")
        val languageResponse = Gson().fromJson(json, LanguageResponse::class.java)

        for (i in languageResponse.languages.indices) {
            val languagesItem: LanguagesItem = languageResponse.getLanguages().get(i)
            if (preference?.defaultLanguage?.id == languagesItem.id) {
                languagesItem.isSelected = true
                languages.add(0, languagesItem)
                mCode = languagesItem.code
            } else {
                languagesItem.isSelected = false
                languages.add(languagesItem)
            }
        }
        adapter?.notifyDataSetChanged()
    }

    fun hide() {
        dialog.dismiss()
    }

    interface ShareSheet {
        fun onLanguageChange(lang: String)
    }
}