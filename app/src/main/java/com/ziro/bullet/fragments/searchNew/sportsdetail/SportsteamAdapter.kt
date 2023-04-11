package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.model.discoverNew.sportsteam.P

class SportsteamAdapter(
    private var context: Activity, private var teamList1: List<P?>, private var teamList2: List<P?>
) : RecyclerView.Adapter<SportsteamAdapter.ViewHolder>() {
    private lateinit var team1: P
    private lateinit var team2: P
    private var holder: SportsteamAdapter.ViewHolder? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SportsteamAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sport_team_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SportsteamAdapter.ViewHolder, position: Int) {
        this.holder = holder
        team1 = teamList1[position]!!
        team2 = teamList2[position]!!
        holder.player_name1?.text = "${team1.Snm} "
        holder.player_name2?.text = "${team2.Snm}"
    }

    override fun getItemCount(): Int {
        return teamList1.size
    }


    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var player_name1: TextView? = null
        var player_name2: TextView? = null

        init {
            player_name1 = itemView?.findViewById(R.id.player_name1)
            player_name2 = itemView?.findViewById(R.id.player_name2)
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