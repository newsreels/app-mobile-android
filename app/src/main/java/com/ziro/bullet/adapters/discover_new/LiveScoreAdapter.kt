package com.ziro.bullet.adapters.discover_new

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.liveScore.SportEvent

class LiveScoreAdapter() : RecyclerView.Adapter<LiveScoreAdapter.LiveScoreViewHolder>() {
    private var liveEventsList = listOf<SportEvent>()
    private lateinit var discoverChildInterface: DiscoverChildInterface
    lateinit var sportCategory: String
    lateinit var context: Context

    inner class LiveScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(liveEvent: SportEvent) {
            Log.e("sportCategory", "onBind: $sportCategory")
            if (sportCategory == "cricket") {
                if (liveEvent.EpsL != null) {
                    itemView.findViewById<TextView>(R.id.tv_event_info).text = liveEvent.EpsL
                }
                if (liveEvent.team1[0].Nm != null) {
                    itemView.findViewById<TextView>(R.id.tv_team1_name).text = liveEvent.team1[0].Nm
                }
                if (liveEvent.team2[0].Nm != null) {
                    itemView.findViewById<TextView>(R.id.tv_team2_name).text = liveEvent.team2[0].Nm
                }

                itemView.findViewById<TextView>(R.id.tv_team1_score).apply {
                    text = if (liveEvent.Tr1C2 != null)
                        "${liveEvent.Tr1C2}/${liveEvent.Tr1CW2} & ${liveEvent.Tr1C1}/${liveEvent.Tr1CW1}"
                    else
                        "${liveEvent.Tr1C1}/${liveEvent.Tr1CW1}"
                }

                itemView.findViewById<TextView>(R.id.tv_team2_score).apply {
                    text = if (liveEvent.Tr2C2 != null)
                        "${liveEvent.Tr2C2}/${liveEvent.Tr2CW2} & ${liveEvent.Tr2C1}/${liveEvent.Tr2CW1}"
                    else
                        "${liveEvent.Tr2C1}/${liveEvent.Tr2CW1}"
                }

                itemView.findViewById<TextView>(R.id.tv_team1_overs).apply {
                    text = if (liveEvent.Tr1CO2 != null)
                        "(${liveEvent.Tr1CO2} & ${liveEvent.Tr1CO1})"
                    else
                        "(${liveEvent.Tr1CO1})"
                }

                itemView.findViewById<TextView>(R.id.tv_team2_overs).apply {
                    text = if (liveEvent.Tr2CO2 != null)
                        "(${liveEvent.Tr2CO2} & ${liveEvent.Tr2CO1})"
                    else
                        "(${liveEvent.Tr2CO1})"
                }

                itemView.findViewById<TextView>(R.id.tv_event_status).text =
                    if (liveEvent.ErnInf != null) {
                        "${liveEvent.ErnInf} - ${liveEvent.ECo}"
                    } else {
                        liveEvent.ECo
                    }
            } else if (sportCategory == "soccer") {

                if (liveEvent.team1[0].Nm != null) {
                    itemView.findViewById<TextView>(R.id.tv_team1_name).text = liveEvent.team1[0].Nm
                }
                if (liveEvent.team2[0].Nm != null) {
                    itemView.findViewById<TextView>(R.id.tv_team2_name).text = liveEvent.team2[0].Nm
                }
                if (liveEvent.Tr1OR != null) {
                    itemView.findViewById<TextView>(R.id.tv_team1_score).text = liveEvent.Tr1OR

                }
                if (liveEvent.Tr2OR != null) {
                    itemView.findViewById<TextView>(R.id.tv_team2_score).text = liveEvent.Tr2OR
                }
                Log.e("team name", "onBind: ${liveEvent.team1[0].CoNm}")
                if (liveEvent.team1[0].CoNm != null) {
                    itemView.findViewById<TextView>(R.id.tv_event_info).text =
                        "${liveEvent.team1[0].CoNm} vs ${liveEvent.team2[0].CoNm}"
                }

                var img1 = liveEvent.team1[0].Img.toString()
                var image1 = "https://lsm-static-prod.livescore.com/medium/$img1"
                var img2 = liveEvent.team2[0].Img.toString()
                var image2 = "https://lsm-static-prod.livescore.com/medium/$img2"
                Log.e("image1", "onBind: $image1 $image2")
                if (image1 != null) {

                    Glide.with(context).load(image1)
                        .placeholder(R.drawable.img_place_holder)
                        .into(itemView.findViewById<ImageView>(R.id.iv_team1_flag))
                } else {
                    itemView.findViewById<ImageView>(R.id.iv_team1_flag)
                        .setImageResource(R.drawable.trending_item)
                }

                if (image2 != null) {
                    Glide.with(context).load(image2)
                        .placeholder(R.drawable.img_place_holder)
                        .into(itemView.findViewById<ImageView>(R.id.iv_team2_flag))
                } else {
                    itemView.findViewById<ImageView>(R.id.iv_team1_flag)
                        .setImageResource(R.drawable.trending_item)
                }

            }


            itemView.findViewById<ConstraintLayout>(R.id.sportsLayout).setOnClickListener {
                liveEvent.let {
            //                    discoverChildInterface.moveSportsDetailPage(sportCategory,
            //                        liveEvent
            //                    )
                }
            }
        }
    }

    fun addChildListenerDiscover(
        discoverChildInterface: DiscoverChildInterface, sportCategory: String, context: Context
    ) {
        this.context = context
        this.sportCategory = sportCategory
        this.discoverChildInterface = discoverChildInterface
    }

    fun updateLiveScore(liveEventsList: List<SportEvent>) {
        this.liveEventsList = liveEventsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveScoreViewHolder {
        return LiveScoreViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sport_event_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LiveScoreViewHolder, position: Int) {
        holder.onBind(liveEventsList[position])
    }

    override fun getItemCount(): Int {
        return liveEventsList.size
    }
}