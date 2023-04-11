package com.ziro.bullet.fragments.Search

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.adapters.discover.SearchReelsAdapter
import com.ziro.bullet.data.TYPE
import com.ziro.bullet.data.models.location.LocationModel
import com.ziro.bullet.data.models.relevant.RelevantResponse
import com.ziro.bullet.data.models.sources.SourceModel
import com.ziro.bullet.interfaces.GoHome
import com.ziro.bullet.interfaces.SearchTabsInterface
import com.ziro.bullet.interfaces.VideoInterface
import com.ziro.bullet.mediapicker.gallery.SpacingItemDecoration
import com.ziro.bullet.mediapicker.utils.ScreenUtils
import com.ziro.bullet.model.Menu.CategoryResponse
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.model.Reel.ReelsItem
import com.ziro.bullet.model.articles.ArticleResponse
import com.ziro.bullet.presenter.ReelPresenter
import com.ziro.bullet.presenter.SearchTabPresenter
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.PaginationListener
import com.ziro.bullet.utills.Utils

class SearchReelsFragment1 : Fragment(), SearchTabsInterface {
    private val mReelResponseArrayList = ArrayList<ReelsItem>()
    private var mAdapter: SearchReelsAdapter? = null
    private val listener: OnAllFragmentInteractionListener? = null
    private var mRecyclerView: RecyclerView? = null
    private var progress: ProgressBar? = null
    private var search_skeleton: LinearLayout? = null
    private var mPresenter: SearchTabPresenter? = null
    private val reelPresenter: ReelPresenter? = null
    private var mSearchChar: String? = ""
    private var reelapi = false
    private var mPage = ""
    private var ll_no_results: LinearLayout? = null
    private var tv_search2: TextView? = null
    private var back_img: ImageView? = null
    private val isReload = false
    private var isLoad = false
    private val isLastPage = false
    private val videoInterface: VideoInterface? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && arguments!!.containsKey(BUNDLE_QUERY)) {
            mSearchChar = arguments!!.getString(BUNDLE_QUERY)
        }
        if (arguments != null && arguments!!.containsKey("query")) {
            mSearchChar = arguments!!.getString("query") //search query
            reelapi =
                arguments!!.getBoolean("reelsapi") // boolean to set title w.r.t search & dicover page
        }
        if (goHome != null) {
            goHome!!.scrollUp()
        } else {
//            Log.e(TAG, "onViewCreated:nulll gohome ");
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_reels_fragment, container, false)
        bindView(view)
        init()
        setData()
        searchQuery(mSearchChar)
        //        setPagination();
        return view
    }

    private fun bindView(view: View) {
        mRecyclerView = view.findViewById(R.id.rvList)
        tv_search2 = view.findViewById(R.id.tv_search2)
        ll_no_results = view.findViewById(R.id.ll_no_results)
        progress = view.findViewById(R.id.progress)
        back_img = view.findViewById(R.id.back_img)
        search_skeleton = view.findViewById(R.id.search_skeleton)
    }

    private fun setPagination() {
        Log.e(TAG, "setPagination: ")
        val gridLayoutManager = GridLayoutManager(
            context, 3
        )
        mRecyclerView!!.addOnScrollListener(object : PaginationListener(gridLayoutManager) {
            override fun loadMoreItems() {
                isLoad = true
                Utils.hideKeyboard(activity, mRecyclerView)
                val visibleThreshold = 3
                if (!TextUtils.isEmpty(mSearchChar)) {
                    mPresenter!!.getReelsHome(
                        Constants.REELS_FOR_YOU,
                        "",
                        mSearchChar,
                        mPage,
                        false,
                        false,
                        true,
                        ""
                    )
                } else {
                    mPresenter!!.getReelsHome(
                        Constants.REELS_FOR_YOU,
                        "",
                        "",
                        mPage,
                        false,
                        false,
                        true,
                        ""
                    )
                }
            }

            override fun isLastPage(): Boolean {
                return isLast
            }

            override fun isLoading(): Boolean {
                return isLoad
            }

            override fun onScroll(position: Int) {
//                Utils.hideKeyboard(EditionActivity.this, mRvRecyclerView);
            }

            override fun onScrolling(recyclerView: RecyclerView, newState: Int) {}
        })
        mRecyclerView!!.layoutManager = gridLayoutManager
        //        searchChannelAdapter.updateChannelsData(channel)
    }

    private fun init() {
        mPresenter = SearchTabPresenter(activity, this)
        mAdapter = SearchReelsAdapter(mReelResponseArrayList, context, "userContext")
        val gridLayoutManager = GridLayoutManager(
            context, 3
        )
        mRecyclerView!!.layoutManager = gridLayoutManager
        mRecyclerView!!.addItemDecoration(
            SpacingItemDecoration(
                3, ScreenUtils.dip2px(
                    context, 10f
                ), false
            )
        )
        mRecyclerView!!.adapter = mAdapter

//        isReload = false;
    }

    private fun setData() {
        if (mSearchChar != null) {
//            mAdapter!!.updateQuery(mSearchChar, reelapi)
        }
        back_img!!.setOnClickListener { activity!!.onBackPressed() }
    }

    fun searchQuery(mSearchChar: String?) {
        ll_no_results!!.visibility = View.GONE
        if (mPresenter != null) {

            mPresenter!!.getReelsHome(
                Constants.REELS_FOR_YOU,
                "",
                mSearchChar,
                mPage,
                false,
                false,
                true,
                ""
            )
        } else {
            mReelResponseArrayList.clear()
            if (mAdapter != null) mAdapter!!.notifyDataSetChanged()
        }
    }

    private val isLast: Boolean
        private get() = mPage.equals("", ignoreCase = true)

    override fun loaderShow(flag: Boolean) {
        search_skeleton!!.visibility = if (flag) View.VISIBLE else View.GONE
    }

    override fun error(error: String, load: Int) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    override fun onSearchArticleSuccess(response: ArticleResponse, isPagination: Boolean) {}
    override fun onRelevantSuccess(response: RelevantResponse) {}
    override fun onSearchChannelsSuccess(response: SourceModel) {}
    override fun onSearchPlacesSuccess(response: LocationModel) {}
    override fun onSearchTopicsSuccess(response: CategoryResponse) {}
    override fun onReelSuccess(response: ReelResponse) {
        if (response != null) {
            search_skeleton!!.visibility = View.GONE
            mRecyclerView!!.visibility = View.VISIBLE
        }
        if (TextUtils.isEmpty(mPage)) {
            mReelResponseArrayList.clear()
        }
        if (response.meta != null) {
            mPage = response.meta.next
        }
        if (response.reels.size > 0) {
//            mReelResponseArrayList.clear();
            mReelResponseArrayList.addAll(response.reels)
            ll_no_results!!.visibility = View.GONE
            mRecyclerView!!.visibility = View.VISIBLE
            search_skeleton!!.visibility = View.GONE
            if (mAdapter != null) mAdapter!!.notifyDataSetChanged()
        } else {
            ll_no_results!!.visibility = View.VISIBLE
            mRecyclerView!!.visibility = View.GONE
        }
        setPagination()
    }

    override fun searchChildSecondOnClick(
        response: ReelsItem?,
        reelsList: MutableList<ReelsItem>?,
        position: Int,
        page: String?
    ) {
        TODO("Not yet implemented")
    }

    override fun onRelevantArticlesSuccess(response: ArticleResponse) {}
    interface OnAllFragmentInteractionListener {
        fun dataChanged(type: TYPE?, id: String?)
        fun onItemClicked(
            type: TYPE?,
            context: String?,
            id: String?,
            name: String?,
            favorite: Boolean
        )
    }

    companion object {
        private val TAG = SearchReelsFragment::class.java.simpleName
        private const val BUNDLE_QUERY = ""
        private var goHome: GoHome? = null
        fun newInstance(query: String?): SearchReelsFragment {
            val allFragment = SearchReelsFragment()
            val bundle = Bundle()
            bundle.putString(BUNDLE_QUERY, query)
            allFragment.arguments = bundle
            return allFragment
        }

        fun getInstance(bundle2: Bundle?, goHomeTempHomeListener: GoHome?): SearchReelsFragment {
            val searchReelsFragment = SearchReelsFragment()
            searchReelsFragment.arguments = bundle2
            goHome = goHomeTempHomeListener
            return searchReelsFragment
        }
    }
}