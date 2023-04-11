package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.model.discoverNew.scorecard.SDInn


class ScorecardHeadAdapter(
    private var context: Activity, private var SDInnList: List<SDInn>, private var scorecardHeadInterface: ScorecardHeadInterface?
) : RecyclerView.Adapter<ScorecardHeadAdapter.ViewHolder>() {
//    private var scorecardHeadInterface: ScorecardHeadInterface? = null
    private lateinit var SDInn: SDInn

    private var holder: ScorecardHeadAdapter.ViewHolder? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScorecardHeadAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.head_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScorecardHeadAdapter.ViewHolder, position: Int) {
        this.holder = holder
        SDInn = SDInnList[position]
        holder.tv_headname?.text = "${SDInn.ti}"
        holder.cd_head?.setOnClickListener {
            Log.e("testtab", "onBindViewHolder: ", )
            scorecardHeadInterface?.getScorecard(position)
        }

    }

    fun updateInterface(scorecardHeadInterface: ScorecardHeadInterface?) {
        this.scorecardHeadInterface = scorecardHeadInterface
    }

    override fun getItemCount(): Int {
        return SDInnList.size
    }


    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var tv_headname: TextView? = null
        var cd_head: CardView? = null


        init {
            tv_headname = itemView.findViewById(R.id.tv_headname)
            cd_head = itemView.findViewById(R.id.cd_head)
        }
    }


//    fun updateSportsTeam1(teamList1: ArrayList<P>) {
//        this.teamList1 = teamList1
//        notifyDataSetChanged()
//    }
//
//    fun updateSportsTeam2(teamList2: ArrayList<P>) {
//        this.teamList2 = teamList2
//        notifyDataSetChanged()
//    }
}