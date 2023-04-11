package com.ziro.bullet.activities.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.activities.BaseActivity
import com.ziro.bullet.activities.onboarding.adapter.OnboardingDetailLanguageAdapter
import com.ziro.bullet.activities.onboarding.adapter.OnboardingDetailRegionAdapter
import com.ziro.bullet.adapters.OnboardingTopicsAdapter
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.data.dataclass.*
import com.ziro.bullet.data.models.topics.Topics
import com.ziro.bullet.data.models.topics.TopicsModel
import com.ziro.bullet.presenter.OnboardingPresenter
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.activity_onboarding_details.*

class OnboardingDetailsActivity : BaseActivity(), ApiCallback {

    private var mType: String = ""

    private var languageList = ArrayList<ContentLanguage>()
    private var regionList = ArrayList<Region>()
    private var topicList = ArrayList<Topics>()

    private var selectedLanguageList = ArrayList<ContentLanguage>()
    private var unselectedLanguageList = ArrayList<ContentLanguage>()

    private var selectedRegionList = ArrayList<Region>()
    private var selectedTopicList = ArrayList<Topics>()

//    private var selectedlanguageList = ArrayList<ContentLanguage>()
//    private var selectedRegionList = ArrayList<Region>()
//    private var selectedTopicList = ArrayList<Topics>()

    private lateinit var languageAdapter: OnboardingDetailLanguageAdapter
    private lateinit var regionAdapter: OnboardingDetailRegionAdapter
    private lateinit var topicAdapter: OnboardingTopicsAdapter

    private lateinit var cacheManager: DbHandler
    private lateinit var mPrefConfig: PrefConfig

    private var isLoading = false


    private lateinit var presenter: OnboardingPresenter

    private var mPage = ""
    private var mQuery = ""

    private val handler = Handler()
    private val input_finish_checker = Runnable {
        reset()
        load()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBarColor(this)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white);
        setContentView(R.layout.activity_onboarding_details)

        mType = intent.extras?.getString(TYPE).toString()
        presenter = OnboardingPresenter(this, this)
        cacheManager = DbHandler(this)
        mPrefConfig = PrefConfig(this)

        when (mType) {
            OnBoardingActivity.CONTENT_LANGUAGE -> {
                if (intent.extras?.containsKey(INIT_DATA) == true)
                    selectedLanguageList =
                        intent.extras?.getParcelableArrayList<ContentLanguage>(INIT_DATA) as ArrayList<ContentLanguage>
                setupLanguage()
            }
            OnBoardingActivity.REGION -> {
                if (intent.extras?.containsKey(INIT_DATA) == true)
                    selectedRegionList =
                        intent.extras?.getParcelableArrayList<ContentLanguage>(INIT_DATA) as ArrayList<Region>
                setupRegion()
            }
            OnBoardingActivity.TOPIC -> {
                if (intent.extras?.containsKey(INIT_DATA) == true)
                    selectedTopicList =
                        intent.extras?.getParcelableArrayList<ContentLanguage>(INIT_DATA) as ArrayList<Topics>
                setupTopics()
            }
            UPDATE_CONTENT_LANGUAGE -> {
                setupLanguage()
            }
        }

        btnContinue.setOnClickListener {
            if (mType == UPDATE_CONTENT_LANGUAGE) {
                presenter.updateContentLanguages(
                    selectedLanguageList.filter {
                        it.favorite
                    }.map {
                        it.id
                    }
                )
            } else {
                val intent = Intent().apply {
                    putExtra(TYPE, mType)
                    when (mType) {
                        OnBoardingActivity.CONTENT_LANGUAGE -> {
                            putParcelableArrayListExtra(
                                RESULT_DATA,
                                getSelected() as ArrayList<ContentLanguage>
                            )
                        }
                        OnBoardingActivity.REGION -> {
                            Log.d("TAG", "onCreate: " + getSelected() as ArrayList<Region>)
                            putParcelableArrayListExtra(
                                RESULT_DATA,
                                getSelected() as ArrayList<Region>
                            )
                        }
                        OnBoardingActivity.TOPIC -> {
                            putParcelableArrayListExtra(
                                RESULT_DATA,
                                getSelected() as ArrayList<Topics>
                            )
                        }
                    }
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        epoxyRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (recyclerView.layoutManager is LinearLayoutManager) {
                    val visibleItemCount: Int =
                        (recyclerView.layoutManager as LinearLayoutManager).getChildCount()
                    val totalItemCount: Int =
                        (recyclerView.layoutManager as LinearLayoutManager).getItemCount()
                    val firstVisibleItemPosition: Int =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()


                    // onScrolling(recyclerView, 0);
                    if (!isLoading && !mPage.isEmpty()) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            when (mType) {
                                OnBoardingActivity.CONTENT_LANGUAGE -> {
                                    presenter.getContentLanguages(mQuery, mPage)
                                }
                                UPDATE_CONTENT_LANGUAGE -> {
                                    presenter.getContentLanguages(mQuery, mPage)
                                }
                                OnBoardingActivity.REGION -> {
                                    presenter.getRegions(mQuery, mPage)
                                }
                            }
                        }
                    }
                } else {
                    if (dy > 0) { // only when scrolling up
                        val visibleThreshold = 3
                        val layoutManager = recyclerView.layoutManager as GridLayoutManager
                        val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                        val currentTotalCount = layoutManager.itemCount
                        if (!isLoading && !mPage.isEmpty() && currentTotalCount <= lastItem + visibleThreshold) {
                            presenter.getTopics(mQuery, mPage)
                        }
                    }
                }
            }
        })

        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handler.removeCallbacks(input_finish_checker)
                if (newText!!.trim().length > 1) {
                    if (mQuery != newText.trim()) {
                        mQuery = newText.trim()
                        handler.postDelayed(input_finish_checker, 500)
                    }
                } else if (newText.isEmpty()) {
                    mQuery = ""
                    reset()
                    load()
                }
                return false
            }
        })

        svSearch.setOnQueryTextFocusChangeListener { v: View?, hasFocus: Boolean ->
            if (hasFocus) {
                svSearch.isFocusable = true
                svSearch.requestFocus()
                svSearch.requestFocusFromTouch()
            }
        }

        svSearch.setOnCloseListener {
            reset()
            load()
            true
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun reset() {
        mPage = ""
//        languageList.clear()
//        regionList.clear()
//        topicList.clear()
    }

    fun load() {
        when (mType) {
            UPDATE_CONTENT_LANGUAGE -> {
                presenter.getContentLanguages(mQuery, mPage)
            }
            OnBoardingActivity.CONTENT_LANGUAGE -> {
                presenter.getContentLanguages(mQuery, mPage)
            }
            OnBoardingActivity.REGION -> {
                presenter.getRegions(mQuery, mPage)
            }
            OnBoardingActivity.TOPIC -> {
                presenter.getTopics(mQuery, mPage)
            }
        }
    }

    fun getSelected(): List<Any> {
        when (mType) {
            OnBoardingActivity.CONTENT_LANGUAGE -> {
                return selectedLanguageList.filter {
                    it.favorite
                } as ArrayList<Any>
            }
            OnBoardingActivity.REGION -> {
                return selectedRegionList.filter {
                    it.favorite
                } as ArrayList<Any>
            }
            OnBoardingActivity.TOPIC -> {
                return selectedTopicList.filter {
                    it.isFavorite
                } as ArrayList<Any>
            }
        }
        return emptyList()
    }


    private fun setupLanguage() {
        tvTitle.text = getString(R.string.content_language)

        languageAdapter = OnboardingDetailLanguageAdapter(this, languageList)

        languageAdapter.onItemClick = { it ->
            var pos = -1
            selectedLanguageList.forEachIndexed { index, lang ->
                if (lang.id == it.id) {
                    pos = index
                }
            }

            if (pos != -1) {
                selectedLanguageList.removeAt(pos)
            } else {
                selectedLanguageList.add(it)
            }

            pos = -1
            unselectedLanguageList.forEachIndexed { index, lang ->
                if (lang.id == it.id) {
                    pos = index
                }
            }

            if (pos != -1) {
                unselectedLanguageList.removeAt(pos)
            } else {
                unselectedLanguageList.add(it)
            }
        }

        epoxyRecyclerView.layoutManager = LinearLayoutManager(this)
        epoxyRecyclerView.adapter = languageAdapter

        presenter.getContentLanguages(mQuery, mPage)
    }

    private fun setupRegion() {
        tvTitle.setText(getString(R.string.region))
        regionAdapter = OnboardingDetailRegionAdapter(this, regionList)

        epoxyRecyclerView.layoutManager = LinearLayoutManager(this)
        epoxyRecyclerView.adapter = regionAdapter

        regionAdapter.onItemClick = { it ->
            var containsItem: Region? = null

            selectedRegionList.forEach { region ->
                if (region.id == it.id) {
                    containsItem = region
                }
            }

            if (containsItem != null) {
                selectedRegionList.remove(containsItem)
            } else {
                selectedRegionList.add(it)
            }
        }

        presenter.getRegions(mQuery, mPage)
    }

    private fun setupTopics() {
        tvTitle.text = getString(R.string.topics)
        topicAdapter = OnboardingTopicsAdapter(this, topicList)

        epoxyRecyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        epoxyRecyclerView.adapter = topicAdapter

        topicAdapter.onItemClick = { it ->
            var containsItem: Topics? = null

            selectedTopicList.forEach { topic ->
                if (topic.id == it.id) {
                    containsItem = topic
                }
            }

            if (containsItem != null) {
                selectedTopicList.remove(containsItem)
            } else {
                selectedTopicList.add(it)
            }
        }

        presenter.getTopics(mQuery, mPage)
    }


    override fun onLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        if (mPage == "") {
            showLoadingView(isLoading)
        }
    }

    override fun onError(error: String) {
        Utils.showSnacky(window.decorView.rootView, error)
    }

    override fun onSuccess(response: OnBoardingModel) {
    }

    override fun onSuccess(response: ContentLanguageResponse) {
        if (mPage.isEmpty())
            languageList.clear()
        mPage = response.meta.next
        val responseList = response.languageList

        // add selected items in to the list
        responseList.forEach { item ->
            if (item.favorite) {
                var contains = false
                unselectedLanguageList.forEach { selectedItem ->
                    run {
                        if (item.id == selectedItem.id)
                            contains = true
                    }
                }

                if (contains) {
                    item.favorite = false
                    return@forEach
                }

                contains = false
                selectedLanguageList.forEach { selectedItem ->
                    run {
                        if (item.id == selectedItem.id)
                            contains = true
                    }
                }

                if (!contains)
                    selectedLanguageList.add(item.deepCopy())
            }
        }

        //handle selected list locally if coming from Onboarding
        if (mType != UPDATE_CONTENT_LANGUAGE) {
            if (responseList != null && responseList.size > 0)
                selectedLanguageList.forEach { responseItem ->
                    val isContainItemList = ArrayList<ContentLanguage>()
                    responseList.forEach { tempItem ->
                        if (tempItem.id == responseItem.id) {
                            isContainItemList.add(tempItem)
                            tempItem.favorite = responseItem.favorite
//                    responseList.remove(tempItem)
//                    responseList.add(0, tempItem)
                        }
                    }

                    if (mQuery.isEmpty())
                        if (isContainItemList.isNotEmpty()) {
                            responseList.removeAll(isContainItemList)
                            responseList.addAll(0, isContainItemList)
                        } else
                            responseList.add(0, responseItem)
                }
        } else {
            responseList.forEach { item ->
                item.favorite = false
                selectedLanguageList.forEach { selectedItem ->
                    run {
                        if (item.id == selectedItem.id) {
                            item.favorite = selectedItem.favorite
                        }
                    }
                }
            }
        }

        if (responseList != null && responseList.size > 0)
            languageList.addAll(responseList)
        languageAdapter.notifyDataSetChanged()


        if (!isFinishing) {
            if (languageList.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = getString(R.string.no_results_found)
            } else {
                tvEmpty.visibility = View.GONE
            }
        }
    }

    override fun onSuccess(response: RegionResponse) {
        if (mPage.isEmpty())
            regionList.clear()
        mPage = response.meta.next

        val responseList = response.regionList
        if (responseList != null && responseList.size > 0)
            selectedRegionList.forEach { responseItem ->
                val isContainItemList = ArrayList<Region>()
                responseList.forEach { tempItem ->
                    if (tempItem.id == responseItem.id) {
                        isContainItemList.add(tempItem)
                        tempItem.favorite = responseItem.favorite
                    }
                }

                if (mQuery.isEmpty())
                    if (isContainItemList.isNotEmpty()) {
                        responseList.removeAll(isContainItemList)
                        responseList.addAll(0, isContainItemList)
                    } else
                        responseList.add(0, responseItem)
            }

        if (responseList != null && responseList.size > 0)
            regionList.addAll(responseList)
        regionAdapter.notifyDataSetChanged()

        if (!isFinishing) {
            if (regionList.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = getString(R.string.no_results_found)
            } else {
                tvEmpty.visibility = View.GONE
            }
        }
    }

    override fun onSuccess(response: TopicsModel) {
        if (mPage.isEmpty())
            topicList.clear()
        mPage = response.meta.next

        val responseList = response.topics

        if (responseList != null && responseList.size > 0)
            selectedTopicList.forEach { responseItem ->
                val isContainItemList = ArrayList<Topics>()
                responseList.forEach { tempItem ->
                    if (tempItem.id == responseItem.id) {
                        isContainItemList.add(tempItem)
                        tempItem.isFavorite = responseItem.isFavorite
//                    responseList.remove(tempItem)
//                    responseList.add(0, tempItem)
                    }
                }

                if (mQuery.isEmpty())
                    if (isContainItemList.isNotEmpty()) {
                        responseList.removeAll(isContainItemList)
                        responseList.addAll(0, isContainItemList)
                    } else
                        responseList.add(0, responseItem)
            }

        if (responseList != null && responseList.size > 0)
            topicList.addAll(responseList)
        topicAdapter.notifyDataSetChanged()

        if (!isFinishing) {
            if (topicList.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = getString(R.string.no_results_found)
            } else {
                tvEmpty.visibility = View.GONE
            }
        }
    }

    private fun clear() {
        if (cacheManager != null) {
            Constants.homeDataUpdate = true
            Constants.menuDataUpdate = true
            Constants.reelDataUpdate = true
            if (mPrefConfig != null) mPrefConfig.setAppStateHomeTabs("")
            cacheManager.clearDb()
        }
    }


    override fun onSuccess() {
        clear()
        finish()
    }

    private fun showLoadingView(isShow: Boolean) {
        if (isFinishing) {
            return
        }

        if (isShow) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    companion object {
        const val TYPE = "TYPE"
        const val UPDATE_CONTENT_LANGUAGE = "UPDATE_CONTENT_LANGUAGE"
        const val RESULT_DATA = "RESULT_DATA"
        const val INIT_DATA = "INIT_DATA"

        @JvmStatic
        fun startActivity(list: List<Any>, context: Activity, type: String, requestCode: Int) {
            val intent = Intent(context, OnboardingDetailsActivity::class.java)
            intent.apply {
                putExtra(TYPE, type)
                if (list.isNotEmpty())
                    when (type) {
                        OnBoardingActivity.CONTENT_LANGUAGE -> {
                            putParcelableArrayListExtra(
                                INIT_DATA,
                                list as ArrayList<ContentLanguage>
                            )
                        }
                        OnBoardingActivity.REGION -> {
                            putParcelableArrayListExtra(INIT_DATA, list as ArrayList<Region>)
                        }
                        OnBoardingActivity.TOPIC -> {
                            putParcelableArrayListExtra(INIT_DATA, list as ArrayList<Topics>)
                        }
                    }
            }

            context.startActivityForResult(intent, requestCode)
        }

        @JvmStatic
        fun startActivity(context: Activity, type: String) {
            val intent = Intent(context, OnboardingDetailsActivity::class.java)
            intent.putExtra(TYPE, type)
            context.startActivity(intent)
        }
    }

}