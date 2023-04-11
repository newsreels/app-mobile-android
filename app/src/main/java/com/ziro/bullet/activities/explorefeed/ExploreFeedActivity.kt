package com.ziro.bullet.activities.explorefeed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ziro.bullet.R
import com.ziro.bullet.activities.changereels.ReelTopicsAdapter
import com.ziro.bullet.adapters.NewFeed.SuggestedChannelAdapter
import com.ziro.bullet.adapters.relevant.AuthorItemsAdapter
import com.ziro.bullet.data.models.home.HomeModel
import com.ziro.bullet.fragments.FollowingDetailActivity
import com.ziro.bullet.fragments.TempHomeFragment
import com.ziro.bullet.model.articles.Author
import com.ziro.bullet.utills.Constants
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.activity_explore_feed.*
import kotlinx.android.synthetic.main.card_item_home.view.*

class ExploreFeedActivity : AppCompatActivity() {
    companion object {

        val HOME_MODEL = "homemodel"
        const val SELECTED = "selected"

        @JvmStatic
        fun startActivity(
                fragment: Fragment,
                mHomeModel: HomeModel,
                selectedId: String
        ) {
            val intent = Intent(fragment.context, ExploreFeedActivity::class.java)
            intent.putExtra(HOME_MODEL, mHomeModel)
            intent.putExtra(SELECTED, selectedId)
            fragment.startActivityForResult(intent, TempHomeFragment.INTENT_EXPLORE_REQUEST)
        }
    }

    private lateinit var homeModel: HomeModel

    override fun onBackPressed() {
        if (Constants.homeDataUpdate) {
            setResult(RESULT_OK)
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setBlackStatusBarColor(this)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black);
        setContentView(R.layout.activity_explore_feed)

        ivBack.setOnClickListener { onBackPressed() }

        if (intent.extras?.containsKey(HOME_MODEL) == true)
            homeModel = intent.extras?.getParcelable(HOME_MODEL)!!

        var selected = ""
        if (intent.extras?.containsKey(SELECTED) == true) {
            selected = intent.extras?.getString(SELECTED).toString()
        }

        addCategories(selected)

        if (homeModel.topicsList != null && homeModel.topicsList.size > 0) {
            if (homeModel.topicsList.size > 3) {
                rvTopics.layoutManager = GridLayoutManager(this,  2, GridLayoutManager.HORIZONTAL, false)
            } else {
                rvTopics.layoutManager = GridLayoutManager(this,  1, GridLayoutManager.HORIZONTAL, false)
            }
            rvTopics.adapter = ReelTopicsAdapter(this, homeModel.topicsList, false)
        } else {
            topicsHeader.visibility = View.GONE
        }

        if (homeModel.sourceList != null && homeModel.sourceList.size > 0) {
            rvChannels.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvChannels.adapter = SuggestedChannelAdapter(this, homeModel.sourceList, false)
        } else {
            channelsHeader.visibility = View.GONE
        }

        if (homeModel.authorList != null && homeModel.authorList.size > 0) {
            rvAuthors.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvAuthors.adapter =
                    AuthorItemsAdapter(homeModel.authorList, this) { view: View, i: Int, any: Any ->
                        run {
                            if (any is Author) {
                                val author = any as Author?
                                if (author != null) {
                                    Utils.openAuthor(this, author)
                                }
                            }
                        }
                    }
        } else {
            authorsHeader.visibility = View.GONE
        }

        topicsHeader.setOnClickListener {
            FollowingDetailActivity.launchActivity(
                    this,
                    "topics",
                    ""
            )
        }

        channelsHeader.setOnClickListener {
            FollowingDetailActivity.launchActivity(
                    this,
                    "channels",
                    ""
            )
        }

        authorsHeader.setOnClickListener {
            FollowingDetailActivity.launchActivity(
                    this,
                    "authors",
                    ""
            )
        }
    }

    private fun addCategories(selected: String) {
        // flow_layout.isRtl = !Utils.isRTL()
        flow_layout.removeAllViews()
        for (item in homeModel.data) {
            val view = layoutInflater.inflate(R.layout.card_item_home, null)
//            if (item.id == selected)
//                view.card.strokeColor = ContextCompat.getColor(
//                        this,
//                        R.color.red
//                )
//            else
//                view.card.strokeColor = ContextCompat.getColor(
//                        this,
//                        R.color.crop_icon_color
//                )

            view.card.setOnClickListener {
                val intent = Intent()
                intent.putExtra(SELECTED, item.id)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            view.tvLabel.text = item.title
            flow_layout.addView(view)
        }
    }
}