package com.ziro.bullet.activities.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
import com.ziro.bullet.R
import com.ziro.bullet.activities.MainActivityNew
import com.ziro.bullet.activities.SearchActivity
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.dataclass.*
import com.ziro.bullet.data.models.topics.Topics
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.flowlayout.FlowLayout
import com.ziro.bullet.flowlayout.TagAdapter
import com.ziro.bullet.flowlayout.TagFlowLayout.OnTagClickListener
import com.ziro.bullet.mediapicker.dialog.PictureLoadingDialog
import com.ziro.bullet.presenter.OnboardingPresenter
import com.ziro.bullet.utills.Components
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.activity_on_boarding.*
import kotlinx.android.synthetic.main.new_topic_item.view.*


class OnBoardingActivity : AppCompatActivity(), ApiCallback {

    private lateinit var regionAdapter: TagAdapter<Region>
    private lateinit var regionSelectedAdapter: TagAdapter<Region>
    private lateinit var topicAdapter: TagAdapter<Topics>
    private var languageList = ArrayList<ContentLanguage>()
    private var regionList = ArrayList<Region>()
    private var topicList = ArrayList<Topics>()
    private lateinit var presenter: OnboardingPresenter
    private lateinit var prefConfig: PrefConfig
    private var loadingDialog: PictureLoadingDialog? = null
    private val isMinimumTopicsSelected
    get() = topicList.filter { it.isFavorite }.count() >= 3
    private var isTopic: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Components().settingStatusBarColors(this, "white")
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white);
        setContentView(R.layout.activity_on_boarding)

        presenter = OnboardingPresenter(this, this)
        prefConfig = PrefConfig(this)

        if (intent.hasExtra("main")) {
            ivBack.visibility = View.INVISIBLE
        }
        ivBack.setOnClickListener { onBackPressed() }
        regionMain.visibility = View.GONE
        topicsMain.visibility = View.VISIBLE
        save.setOnClickListener {
            if (isTopic) { // Topics Case
                if (isMinimumTopicsSelected) {
                    regionMain.visibility = View.VISIBLE
                    topicsMain.visibility = View.GONE
                    isTopic = false
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.follow_at_least_3_topics),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {  //Location case
                languageList.clear()
                if (prefConfig != null && prefConfig.defaultLanguage != null) {
                    languageList.add(
                        ContentLanguage(
                            prefConfig.defaultLanguage.id,
                            prefConfig.defaultLanguage.name,
                            "", "", true
                        )
                    )
                }
                presenter.saveOnboarding(
                    SaveOnBoardingModel(
                        languageList.filter {
                            it.favorite
                        }.map {
                            it.id
                        },
                        regionList.filter {
                            it.favorite
                        }.map {
                            it.id
                        },
                        topicList.filter {
                            it.isFavorite
                        }.map {
                            it.id
                        }
                    )
                )
            }
        }

//        rlSearchLanguage.setOnClickListener {
//            OnboardingDetailsActivity.startActivity(
//                    getSelected(CONTENT_LANGUAGE),
//                    this,
//                    CONTENT_LANGUAGE,
//                    INTENT_ONBOARDING_REQUEST
//            )
//        }

        rlSearchRegion.setOnClickListener {
            OnboardingDetailsActivity.startActivity(
                getSelected(REGION), this, REGION, INTENT_ONBOARDING_REQUEST
            )
        }

        rlSearchTopics.setOnClickListener {
            OnboardingDetailsActivity.startActivity(
                getSelected(TOPIC),
                this, TOPIC, INTENT_ONBOARDING_REQUEST
            )
        }

        find_more.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            if (isTopic) {
                //Topic case
                intent.putExtra("case", "topics")
            } else {
                //Location case
                intent.putExtra("case", "places")
            }
            startActivity(intent)
        }

        setupRecyclerviews()

        presenter.getOnboardingCollection()
    }


    override fun onResume() {
        super.onResume()
        buttonStatusChange()
    }

    fun buttonStatusChange() {
        if (isMinimumTopicsSelected) {
//            ViewCompat.setBackgroundTintList(
//                    btnContinue,
//                    ContextCompat.getColorStateList(this@OnBoardingActivity, R.color.red)
//            )
            save.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.theme_color_1
                )
            )
        } else {
//            ViewCompat.setBackgroundTintList(
//                    btnContinue,
//                    ContextCompat.getColorStateList(this@OnBoardingActivity, R.color.inactive_btn_bg)
//            )
            save.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.page_bg
                )
            )
        }
    }

    fun getSelected(type: String): List<Any> {
        when (type) {
//            CONTENT_LANGUAGE -> {
//                return languageList.filter {
//                    it.favorite
//                }
//            }
            REGION -> {
                return regionList.filter {
                    it.favorite
                }
            }
            TOPIC -> {
                return topicList.filter {
                    it.isFavorite
                }
            }
        }
        return emptyList()
    }

    private fun setupRecyclerviews() {

        val staggeredGridLayoutManager1 =
            StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL)
//        staggeredGridLayoutManager1.gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        val staggeredGridLayoutManager2 =
            StaggeredGridLayoutManager(5, LinearLayoutManager.HORIZONTAL)
        staggeredGridLayoutManager2.gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        var staggeredGridLayoutManager3 =
            StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
//        staggeredGridLayoutManager3.gapStrategy = GAP_HANDLING_NONE
        if (topicList.size > 12) {
            staggeredGridLayoutManager3 =
                StaggeredGridLayoutManager(4, LinearLayoutManager.HORIZONTAL)
        }

//        rvLanguage.layoutManager = staggeredGridLayoutManager1
//        rvRegions.layoutManager = staggeredGridLayoutManager2
//        rvTopics.layoutManager = staggeredGridLayoutManager3

//        languageAdapter = ContentLanguageAdapter(this, languageList)
//        regionAdapter = RegionAdapter(this, )
//        regionAdapter.favoriteChangeListener = object : FavoriteChangeListener,
//            RegionAdapter.FavoriteChangeListener {
//            override fun onChange() {
//                regionAdapter.notifyDataSetChanged()
//            }
//        }
//        rvLanguage.adapter = languageAdapter
//        rvRegions.adapter = regionAdapter


        //REGION SELECTED FLOW
        rvRegionsSelected.adapter = object : TagAdapter<Region>(regionList) {
            val mInflater = LayoutInflater.from(this@OnBoardingActivity)
            override fun getView(parent: FlowLayout?, position: Int, region: Region): View {
                val tv = mInflater.inflate(
                    R.layout.new_topic_item,
                    rvRegionsSelected, false
                )
                tv.name.text = region.name

                if (region.favorite) {
                    tv.item_color.setBackgroundColor(
                        ContextCompat.getColor(
                            this@OnBoardingActivity,
                            R.color.white
                        )
                    )
                    tv.name.setTextColor(
                        ContextCompat.getColor(
                            this@OnBoardingActivity,
                            R.color.black
                        )
                    )
                    tv.cross.visibility = View.VISIBLE
                } else {
                    tv.visibility = View.GONE
//                    tv.item_color.setBackgroundColor(
//                        ContextCompat.getColor(
//                         this@OnBoardingActivity,
//                            R.color.theme_color_1
//                        )
//                    )
//                    tv.name.setTextColor(
//                        ContextCompat.getColor(
//                         this@OnBoardingActivity,
//                            R.color.white
//                        )
//                    )
//                    tv.cross.visibility = View.GONE
                }

                return tv
            }
        }.also { regionSelectedAdapter = it }

        rvRegionsSelected.setOnTagClickListener(OnTagClickListener { view, position, parent ->
            regionList[position].favorite = !regionList[position].favorite
            regionAdapter.notifyDataChanged()
            regionSelectedAdapter.notifyDataChanged()
            true
        })

        //REGION FLOW
        rvRegions.adapter = object : TagAdapter<Region>(regionList) {
            val mInflater = LayoutInflater.from(this@OnBoardingActivity)
            override fun getView(parent: FlowLayout?, position: Int, region: Region): View {
                val tv = mInflater.inflate(
                    R.layout.new_topic_item,
                    rvRegions, false
                )
                tv.name.text = region.name

                if (region.favorite) {
                    tv.visibility = View.GONE
//                    tv.item_color.setBackgroundColor(
//                        ContextCompat.getColor(
//                            this@OnBoardingActivity,
//                            R.color.white
//                        )
//                    )
//                    tv.name.setTextColor(
//                        ContextCompat.getColor(
//                         this@OnBoardingActivity,
//                            R.color.black
//                        )
//                    )
//                    tv.cross.visibility = View.VISIBLE
                } else {
                    tv.item_color.setBackgroundColor(
                        ContextCompat.getColor(
                            this@OnBoardingActivity,
                            R.color.theme_color_1
                        )
                    )
                    tv.name.setTextColor(
                        ContextCompat.getColor(
                            this@OnBoardingActivity,
                            R.color.white
                        )
                    )
                    tv.cross.visibility = View.GONE
                }

                return tv
            }
        }.also { regionAdapter = it }

        rvRegions.setOnTagClickListener(OnTagClickListener { view, position, parent ->
            regionList[position].favorite = !regionList[position].favorite
            regionAdapter.notifyDataChanged()
            regionSelectedAdapter.notifyDataChanged()
            true
        })


        //TOPICS FLOW
        rvTopics.adapter = object : TagAdapter<Topics>(topicList) {
            val mInflater = LayoutInflater.from(this@OnBoardingActivity)
            override fun getView(parent: FlowLayout?, position: Int, topic: Topics): View {
                val tv = mInflater.inflate(
                    R.layout.new_topic_item,
                    rvTopics, false
                )
                tv.name.text = topic.name

                if (topic.isFavorite) {
                    tv.item_color.setBackgroundColor(
                        ContextCompat.getColor(
                            this@OnBoardingActivity,
                            R.color.white
                        )
                    )
                    tv.name.setTextColor(
                        ContextCompat.getColor(
                            this@OnBoardingActivity,
                            R.color.black
                        )
                    )
                    tv.cross.visibility = View.VISIBLE
                } else {
                    tv.item_color.setBackgroundColor(
                        ContextCompat.getColor(
                            this@OnBoardingActivity,
                            R.color.theme_color_1
                        )
                    )
                    tv.name.setTextColor(
                        ContextCompat.getColor(
                            this@OnBoardingActivity,
                            R.color.white
                        )
                    )
                    tv.cross.visibility = View.GONE
                }

                return tv
            }
        }.also { topicAdapter = it }

        rvTopics.setOnTagClickListener(OnTagClickListener { view, position, parent ->
            topicList[position].isFavorite = !topicList[position].isFavorite
            topicAdapter.notifyDataChanged()
            buttonStatusChange()
            true
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == INTENT_ONBOARDING_REQUEST) {

            if (data != null && data.extras != null && data.extras?.containsKey(
                    OnboardingDetailsActivity.TYPE
                ) == true
            ) {
                val type = data.extras?.getString(OnboardingDetailsActivity.TYPE)
                if (type != null) {
                    when (type) {
//                        CONTENT_LANGUAGE -> {
//                            val parcelableArrayList =
//                                    data.extras?.getParcelableArrayList<ContentLanguage>(
//                                            OnboardingDetailsActivity.RESULT_DATA
//                                    )
//
//                            languageList.forEach { tempItem ->
//                                tempItem.favorite = false
//                            }
//
//                            if (parcelableArrayList != null) {
//                                parcelableArrayList.forEach { responseItem ->
//                                    var contains = false
//                                    languageList.forEach { tempItem ->
//                                        if (tempItem.id == responseItem.id) {
//                                            tempItem.favorite = responseItem.favorite
//                                            contains = true
//                                        }
//                                    }
//                                    if (!contains)
//                                        languageList.add(0, responseItem)
//                                }
//
//                                languageAdapter.notifyDataSetChanged()
//                            }
//                        }

                        REGION -> {
                            val parcelableArrayList =
                                data.extras?.getParcelableArrayList<Region>(
                                    OnboardingDetailsActivity.RESULT_DATA
                                )

                            regionList.forEach { tempItem ->
                                tempItem.favorite = false
                            }

                            var listTobeMoved = ArrayList<Region>()

                            if (parcelableArrayList != null) {
                                parcelableArrayList.forEach { responseItem ->
                                    var contains = false
                                    regionList.forEach { tempItem ->
                                        if (tempItem.id == responseItem.id) {
                                            tempItem.favorite = responseItem.favorite
                                            contains = true
                                            listTobeMoved.add(tempItem)
                                        }
                                    }
                                    if (!contains)
                                        regionList.add(0, responseItem)
                                }

                                if (listTobeMoved.size > 0) {
                                    regionList.removeAll(listTobeMoved)
                                    regionList.addAll(0, listTobeMoved)
                                }

                                regionAdapter.notifyDataChanged()
                                regionSelectedAdapter.notifyDataChanged()
                            }
                        }

                        TOPIC -> {
                            val parcelableArrayList =
                                data?.extras?.getParcelableArrayList<Topics>(
                                    OnboardingDetailsActivity.RESULT_DATA
                                )
                            topicList.forEach { tempItem ->
                                tempItem.isFavorite = false
                            }
                            var listTobeMoved = ArrayList<Topics>()

                            if (parcelableArrayList != null) {
                                parcelableArrayList.forEach { responseItem ->
                                    var contains = false
                                    topicList.forEach { tempItem ->
                                        if (tempItem.id == responseItem.id) {
                                            tempItem.isFavorite = responseItem.isFavorite
                                            contains = true
                                            listTobeMoved.add(tempItem)
                                        }
                                    }
                                    if (!contains)
                                        topicList.add(0, responseItem)
                                }

                                if (listTobeMoved.size > 0) {
                                    topicList.removeAll(listTobeMoved)
                                    topicList.addAll(0, listTobeMoved)
                                }

//                                topicAdapter.notifyDataSetChanged()
                                topicAdapter.notifyDataChanged()
                            }
                        }
                    }

                }
            }
        }
    }

    companion object {
        val INTENT_ONBOARDING_REQUEST = 2324

        const val CONTENT_LANGUAGE = "CONTENT_LANGUAGE"
        const val REGION = "REGION"
        const val TOPIC = "TOPIC"
    }

    override fun onLoading(isLoading: Boolean) {
        showLoadingView(isLoading)
    }

    override fun onError(error: String) {
        Utils.showSnacky(window.decorView.rootView, error)
    }

    override fun onSuccess(response: OnBoardingModel) {
        nestedScrollView.visibility = View.VISIBLE

//        if (!response.languageList.isNullOrEmpty()) {
//            languageList.addAll(response.languageList)
//        }
        if (!response.topics.isNullOrEmpty()) {
            topicList.addAll(response.topics)
        }
        if (!response.regions.isNullOrEmpty()) {
            regionList.addAll(response.regions)
        }

//        languageAdapter.notifyDataSetChanged()
        regionAdapter.notifyDataChanged()
        regionSelectedAdapter.notifyDataChanged()
        topicAdapter.notifyDataChanged()
        buttonStatusChange()
    }

    override fun onSuccess() {
        val intent = Intent(this, MainActivityNew::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("prefs", "save")
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finishAfterTransition()
    }

    override fun onSuccess(response: ContentLanguageResponse) {
    }

    override fun onSuccess(response: RegionResponse) {
    }

    override fun onSuccess(response: TopicsModel) {
    }

    private fun showLoadingView(isShow: Boolean) {
        if (isFinishing) {
            return
        }
        if (isShow) {
            if (loadingDialog == null) {
                loadingDialog = PictureLoadingDialog(this)
            }
            loadingDialog!!.show()
        } else {
            if (loadingDialog != null
                && loadingDialog!!.isShowing()
            ) {
                loadingDialog!!.dismiss()
            }
        }
    }
}

interface ApiCallback {
    fun onLoading(isLoading: Boolean)

    fun onError(error: String)

    fun onSuccess()

    fun onSuccess(response: OnBoardingModel)

    fun onSuccess(response: ContentLanguageResponse)

    fun onSuccess(response: RegionResponse)

    fun onSuccess(response: TopicsModel)
}
