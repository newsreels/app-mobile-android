package com.ziro.bullet.fragments.searchNew

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.adapters.LanguageAdapter
import com.ziro.bullet.adapters.RegionAdapter
import com.ziro.bullet.adapters.RegionDialogAdapter
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.fragments.RestartPopup
import com.ziro.bullet.interfaces.LanguageInterface
import com.ziro.bullet.model.language.LanguageResponse
import com.ziro.bullet.model.language.LanguagesItem
import com.ziro.bullet.model.language.region.Region
import com.ziro.bullet.model.language.region.RegionApiResponse
import com.ziro.bullet.presenter.LanguagePresenter
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.DividerItemDecorator
import com.ziro.bullet.utills.LocaleManager
import kotlinx.android.synthetic.main.fragment_language_select.*
import kotlinx.android.synthetic.main.progress.*
import java.util.*

class LanguageSelectionFragment : Fragment(), LanguageInterface,
    RegionAdapter.FavoriteChangeListener, LanguageAdapter.LanguageClickCallback {

    //Objects
    private lateinit var languagePresenter: LanguagePresenter
    private lateinit var languageAdapter: LanguageAdapter
    private lateinit var regionAdapter: RegionDialogAdapter
    private var selectedRegion: Region? = null
    private var selectedLanguage: LanguagesItem? = null

    //Data
    private var languageList = arrayListOf<LanguagesItem>()
    private var regionList = listOf<Region>()
    private var selectRegionDialog: AlertDialog? = null
    private var mPrefConfig: PrefConfig? = null

    companion object {
        var languageSelectionCallBack: LanguageSelectionCallBack? = null

        fun instance(languageSelectionCallBack: LanguageSelectionCallBack): LanguageSelectionFragment {
            this.languageSelectionCallBack = languageSelectionCallBack
            return LanguageSelectionFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_language_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObj()
        initDialog()
        initListeners()

    }


    private fun initObj() {
        languagePresenter = LanguagePresenter(requireActivity(), this)
        languageAdapter = LanguageAdapter(languageList, requireContext(), this, false)
        regionAdapter = RegionDialogAdapter(this)
        mPrefConfig = PrefConfig(requireContext())

        if (selectedRegion == null) {
            showLoadingView(true)
            languagePresenter.getPublicRegions()
        } else {
            tv_select_region.text = selectedRegion?.name
        }

        rv_languages.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(requireContext(), R.drawable.gray_divider)
                )
            )

            adapter = languageAdapter
        }
    }

    private fun initDialog() {
        val regionDialogBuilder = AlertDialog.Builder(requireContext())
        val regionDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.region_dialog_view, null, false)
        regionDialogBuilder.setView(regionDialogView)
        regionDialogBuilder.setCancelable(true)
        selectRegionDialog = regionDialogBuilder.create()
        selectRegionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        selectRegionDialog?.setCanceledOnTouchOutside(true)
        regionDialogView.findViewById<RecyclerView>(R.id.rv_regions).apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

//            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

            adapter = regionAdapter

        }
    }

    private fun initListeners() {
        languageAdapter.setCallback(this)

        tv_select_region.setOnClickListener {
            if (selectRegionDialog != null) {
                selectRegionDialog?.show()
            }
        }

        done.setOnClickListener {
            val currentLang = Locale.getDefault().displayLanguage
            if (currentLang.equals(selectedLanguage?.code, true) || currentLang.equals(
                    selectedLanguage?.name,
                    true
                )
            ) {
                mPrefConfig?.selectedRegion = selectedRegion?.id
                mPrefConfig?.prefPrimaryLang = selectedLanguage?.name
                mPrefConfig?.setLanguageForServer(selectedLanguage?.id)
                languageSelectionCallBack?.onLanguageChange(
                    selectedRegion!!,
                    selectedLanguage!!,
                    false
                )
//                requireActivity().onBackPressed()
                return@setOnClickListener
            }
            if (selectedLanguage?.id != mPrefConfig?.isLanguagePushedToServer) {
                val restartPopup = RestartPopup(
                    requireContext()
                ) { flag, msg ->
                    if (flag) {
                        LocaleManager.setLocale(
                            requireContext(),
                            getLanguageCode(selectedLanguage!!.code),
                            getCountryCode(selectedLanguage!!.code)
                        )
                        if (mPrefConfig != null)
                            mPrefConfig = PrefConfig(requireContext())
                        mPrefConfig?.selectedRegion = selectedRegion?.id
                        mPrefConfig?.prefPrimaryLang = selectedLanguage?.name
                        mPrefConfig?.setLanguageForServer(selectedLanguage?.id)
                        languageSelectionCallBack?.onLanguageChange(
                            selectedRegion!!,
                            selectedLanguage!!,
                            true
                        )
                    }
                }
                restartPopup.showDialog()
            } else {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun languageResult(size: Int) {

    }

    override fun loaderShow(flag: Boolean) {

    }

    override fun error(error: String?) {

    }

    override fun success(response: Any?) {
        showLoadingView(false)
        if (response is RegionApiResponse) {
            if (!response.regions.isNullOrEmpty()) {
                regionList = response.regions
                var position = -1
                regionList.forEachIndexed { index, region ->
                    if (region.id == mPrefConfig?.selectedRegion) {
                        selectedRegion = region
                        position = index
                    }
                }
                if (position >= 0) {
                    regionList[position].favorite = true
                } else {
                    regionList[0].favorite = true
                    selectedRegion = regionList[0]
                }
                showLoadingView(true)
                languagePresenter.getPublicLanguages(selectedRegion?.id)
                tv_select_region?.text = selectedRegion?.name
                regionAdapter.updateRegion(regionList)
            }
        } else if (response is LanguageResponse) {
            if (response != null && !response.languages.isNullOrEmpty()) {
                languageList = response.languages as ArrayList<LanguagesItem>
                var position = -1
                languageList.forEachIndexed { index, languagesItem ->
                    if (languagesItem.name.equals(
                            mPrefConfig?.prefPrimaryLang,
                            true
                        ) || languagesItem.id.equals(mPrefConfig?.isLanguagePushedToServer)
                    ) {
                        selectedLanguage = languagesItem
                        position = index
                    }
                }
                if (position >= 0) {
                    languageList.removeAt(position)
                    selectedLanguage?.tag = Constants.PRIMARY_LANGUAGE
                    languageList.add(0, selectedLanguage!!)
                } else {
                    languageList[0].tag = Constants.PRIMARY_LANGUAGE
                    selectedLanguage = languageList[0]
                }
                languageAdapter.updateLanguageList(languageList)
            }
        }
    }

    override fun onRegionSelection(region: Region, position: Int) {
        if (view == null) {
            return
        }
//        enableSaveButton(false)
        selectRegionDialog?.dismiss()
        if (selectedRegion != null) {
            if (selectedRegion == region)
                return
        }
        selectedRegion = region
        for (i in regionList.indices) {
            regionList[i].favorite = false
        }
        regionList[position].favorite = true
        regionAdapter.updateRegion(regionList)
        tv_select_region.text = selectedRegion?.name
        showLoadingView(true)
        selectedLanguage = null
        languagePresenter.getPublicLanguages(selectedRegion?.id)
    }

    private fun showLoadingView(isShow: Boolean) {
        if (view == null) {
            return
        }
        if (isShow) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.GONE
        }
    }

    private fun enableSaveButton(isEnable: Boolean) {
        if (view == null) {
            return
        }
        if (isEnable) {
            done.isEnabled = true
            DrawableCompat.setTint(
                done.background,
                ContextCompat.getColor(requireContext(), R.color.theme_color_1)
            )
        } else {
            done.isEnabled = false
            DrawableCompat.setTint(
                done.background,
                ContextCompat.getColor(requireContext(), R.color.disabled_setting_btn)
            )
        }
    }

    override fun onClick(position: Int, id: String?, code: String?) {
        if (selectedLanguage != null) {
            if (selectedLanguage?.id == id)
                return
        }
        for (i in languageList.indices) {
            languageList[i].tag = ""
        }
        languageList[position].tag = Constants.PRIMARY_LANGUAGE
        selectedLanguage = languageList[position]
//        enableSaveButton(true)
        languageAdapter.updateLanguageList(languageList)
    }

    override fun isLastItem(flag: Boolean) {

    }

    interface LanguageSelectionCallBack {
        fun onLanguageChange(region: Region, languageItem: LanguagesItem, restart: Boolean)
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