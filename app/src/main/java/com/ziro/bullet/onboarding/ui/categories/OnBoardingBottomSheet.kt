package com.ziro.bullet.onboarding.ui.categories

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.activities.MainActivityNew
import com.ziro.bullet.common.base.BaseBottomSheetFragment
import com.ziro.bullet.common.base.UiState
import com.ziro.bullet.data.dataclass.OnBoardingModel
import com.ziro.bullet.data.models.push.Push
import com.ziro.bullet.interfaces.PushNotificationInterface
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.presenter.PushNotificationPresenter
import kotlinx.android.synthetic.main.bottom_sheet_onboarding.*
import kotlinx.android.synthetic.main.enable_notification.*
import kotlinx.android.synthetic.main.fill_btn_wid_progress.*
import kotlinx.android.synthetic.main.fill_btn_wid_progress.btn_next
import kotlinx.android.synthetic.main.fill_btn_wid_progress1.*
import kotlinx.android.synthetic.main.outline_btn_onboard.*


class OnBoardingBottomSheet :
    BaseBottomSheetFragment(R.layout.bottom_sheet_onboarding), PushNotificationInterface,
    OnBoardingPresenter.Listener, CategoriesSelectorController.ItemInteractionListener {

    lateinit var presenter: OnBoardingPresenter
    private lateinit var pushpresenter: PushNotificationPresenter
    lateinit var loadingDialog: PictureLoadingDialog
    private lateinit var fm: FragmentManager
    private val controller by lazy {
        CategoriesSelectorController(this)
    }

    fun updateFragManager(fragmentManager: FragmentManager) {
        this.fm = fragmentManager
    }

    override fun setupDependencies() {
        presenter = OnBoardingPresenter(this)
        pushpresenter = PushNotificationPresenter(requireActivity(), this)
        loadingDialog = PictureLoadingDialog(requireContext())
        isCancelable = false
    }

    override fun setupObservers() {
        presenter.initPresenter()
        presenter.getOnBoardingCollection()
    }

    override fun setupView() {
        epoxyRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        epoxyRecyclerView.setController(controller)
        btn_next_text.text = getText(R.string.save)
        btn_ok_text.text = getText(R.string.enable_notification)
        btn_skip_text.text = getText(R.string.no_do_it_later)

        btn_next.setOnClickListener {
            if (controller.selectedMoreThanMinimum) {
                presenter.saveCategories(controller.selectedCategories)
            } else Toast.makeText(
                requireContext(), getString(R.string.follow_at_least_3_topics), Toast.LENGTH_SHORT
            ).show()
        }
    }

    override val activity: Activity
        get() = requireActivity()

    override fun onBoardingCollections(state: UiState<OnBoardingModel>) {
        when (state) {
            is UiState.Loading -> showLoadingView(true)
            is UiState.Success -> controller.collections = state.data ?: return
            is UiState.Error -> state.error?.printStackTrace()
        }
    }

    override fun onCategoriesSaved(state: UiState<Unit>) {
        when (state) {
            is UiState.Loading -> showLoadingView(true)
            is UiState.Success -> {
                showLoadingView(false)

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    navigateNotification()
                } else {
                    presenter.loadReels()
                }
//                presenter.loadReels()
            }
            is UiState.Error -> {
                state.error?.printStackTrace()
                showLoadingView(false)
            }
        }
    }

    private fun navigateNotification() {
//        val intent = Intent(context, EnableNotificationActivity::class.java)
//        context!!.startActivity(intent)
        epoxyRecyclerView.visibility = View.GONE
        linearLayout6.visibility = View.GONE
        llnoti.visibility = View.VISIBLE

        closeBtn?.setOnClickListener { pushpresenter.onBoardingPushConfig(false, false, "1h") }

        btn_red1?.setOnClickListener {
            pushpresenter.onBoardingPushConfig(true, false, "1h")
        }
        btn_skip_color?.setOnClickListener {
            pushpresenter.onBoardingPushConfig(false, false, "1h")
        }
    }

    override fun onReelsResult(state: UiState<ReelResponse>) {
        when (state) {
            is UiState.Loading -> showLoadingView(true)
            is UiState.Success -> {
                val dbHandler = DbHandler(context)
                dbHandler.insertReelList("ReelsList", Gson().toJson(state.data))
                showLoadingView(false)
                startNewActivity()
            }
            is UiState.Error -> {
                state.error?.printStackTrace()
                showLoadingView(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        showLoadingView(false)
    }

    private fun showLoadingView(isShow: Boolean) {
        if (view == null || requireActivity().isFinishing) {
            return
        }
        if (isShow) loadingDialog.show()
        else loadingDialog.dismiss()
    }

    private fun startNewActivity() {
        try {
            val intent = Intent(requireContext(), MainActivityNew::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("prefs", "save")
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            requireActivity().finishAfterTransition()
        } catch (e: Exception) {
        }
    }

    fun show() {
        show(fm, OnBoardingBottomSheet::class.simpleName)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet ?: return)
        val layoutParams = bottomSheet.layoutParams

        val windowHeight: Int = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        bottomSheet.setBackgroundResource(R.drawable.bottom_sheet_corners)
        behavior.isDraggable = false
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.heightPixels - 100

    }

    override fun onItemSelection() {
        if (controller.selectedMoreThanMinimum) {
            btn_color.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.theme_color_1
                )
            )
        } else {
            btn_color.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.disabled_setting_btn
                )
            )
        }
    }

    override fun loaderShow(flag: Boolean) {

    }

    override fun error(error: String?) {

    }

    override fun error404(error: String?) {

    }

    override fun success(model: Push) {

    }

    override fun SuccessFirst(flag: Boolean) {
        presenter.loadReels()
    }

}