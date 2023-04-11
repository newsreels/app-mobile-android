package com.ziro.bullet.adapters.discover_new

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.liveScore.SportEvent
import com.ziro.bullet.model.discoverNew.liveScore.Stage

class SportsScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val liveScoreAdapter = LiveScoreAdapter()
    lateinit var context: Context
    lateinit var sportCategory: String

    fun onBind(
        category: String,
        context: Context,
        sportsStagesList: List<Stage>?,
        discoverChildInterface: DiscoverChildInterface
    ) {
        this.context = context
        sportCategory = category

        if (sportsStagesList != null) {
            itemView.findViewById<TextView>(R.id.tv_title).apply {
                text = context.getString(R.string.sports)
            }

            setBackground(sportCategory)

            itemView.findViewById<TextView>(R.id.tv_cricket).setOnClickListener {
//                sportCategory = "cricket"
                setBackground("cricket")

                liveScoreAdapter.addChildListenerDiscover(
                    discoverChildInterface,
                    "cricket",
                    context
                )
                discoverChildInterface.updateSports("cricket")
            }

            itemView.findViewById<TextView>(R.id.tv_football).setOnClickListener {
                setBackground("soccer")
//                sportCategory = "soccer"
                liveScoreAdapter.addChildListenerDiscover(discoverChildInterface, "soccer", context)
                discoverChildInterface.updateSports("soccer")
            }

            val sportsEventsTab = itemView.findViewById<TabLayout>(R.id.sports_events_tab)


            itemView.findViewById<RecyclerView>(R.id.pager_sports_item).apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = liveScoreAdapter
            }

            itemView.findViewById<ImageView>(R.id.imgRefresh).setOnClickListener {
                discoverChildInterface.updateSports(sportCategory)
                it.startAnimation(
                    AnimationUtils.loadAnimation(
                        it.context,
                        R.anim.rotate_anti_clockwise
                    )
                )
            }

            liveScoreAdapter.addChildListenerDiscover(
                discoverChildInterface,
                sportCategory,
                context
            )

            if (sportsEventsTab.tabCount > 0) {
                sportsEventsTab.removeAllTabs()
            }

            var eventsList = mutableListOf<SportEvent>()

            if (!sportsStagesList.isNullOrEmpty()) {

                var totalEvents = 0
                sportsStagesList.forEach {
                    eventsList.addAll(it.Events)
                    sportsEventsTab.addTab(
                        sportsEventsTab.newTab().setText("${it.Snm} (${it.Events.size})")
                    )
                    totalEvents += it.Events.size
                }
                sportsEventsTab.addTab(
                    sportsEventsTab.newTab()
                        .setText("${itemView.context.getString(R.string.all_match)} ($totalEvents)"),
                    0, true
                )
                sportsEventsTab.scrollX = 0
                liveScoreAdapter.updateLiveScore(eventsList)
                itemView.findViewById<ConstraintLayout>(R.id.cl_sports_detail).visibility =
                    View.VISIBLE
                itemView.findViewById<ShimmerFrameLayout>(R.id.sports_shimmer).visibility =
                    View.GONE
            }

            sportsEventsTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val tabPosition = tab!!.position
                    if (tabPosition == 0) {
                        liveScoreAdapter.updateLiveScore(eventsList)
                    } else if (tabPosition <= sportsStagesList.size) {
                        liveScoreAdapter.updateLiveScore(sportsStagesList[tabPosition - 1].Events)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

        }
    }

    private fun setBackground(category: String) {
        if (category == "cricket") {
            itemView.findViewById<TextView>(R.id.tv_cricket).setTextColor(Color.WHITE)
            itemView.findViewById<TextView>(R.id.tv_cricket)
                .setBackgroundResource(R.drawable.bg_black)

            itemView.findViewById<TextView>(R.id.tv_football)
                .setBackgroundResource(R.drawable.bg_rectangle_black_border)
            itemView.findViewById<TextView>(R.id.tv_football).setTextColor(Color.BLACK)
        } else if (category == "soccer") {
            itemView.findViewById<TextView>(R.id.tv_football).setTextColor(Color.WHITE)
            itemView.findViewById<TextView>(R.id.tv_football)
                .setBackgroundResource(R.drawable.bg_black)

            itemView.findViewById<TextView>(R.id.tv_cricket)
                .setBackgroundResource(R.drawable.bg_rectangle_black_border)
            itemView.findViewById<TextView>(R.id.tv_cricket).setTextColor(Color.BLACK)

        }
    }
}