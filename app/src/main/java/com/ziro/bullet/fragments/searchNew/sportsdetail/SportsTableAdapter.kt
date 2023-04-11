package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.model.discoverNew.tabletest.Team


class SportsTableAdapter(
    private var context: Activity, private var teamList1: ArrayList<Team>
) : RecyclerView.Adapter<SportsTableAdapter.ViewHolder>() {

    private lateinit var team1: Team
    private var holder: SportsTableAdapter.ViewHolder? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SportsTableAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sport_table_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SportsTableAdapter.ViewHolder, position: Int) {
        this.holder = holder
        team1 = teamList1[position]

        holder.team_name?.text = "${team1.Tnm} "
        holder.Tv_m?.text = "${team1.pld} "
        holder.Tv_w?.text = "${team1.win} "
        holder.tv_L?.text = "${team1.lst} "
        holder.tv_T?.text = "${team1.td} "
        holder.tv_NR?.text = "${team1.nr} "
        holder.tv_PTS?.text = "${team1.pts} "
        holder.tv_NRR?.text = "${team1.nrr} "
    }

    override fun getItemCount(): Int {
        return teamList1.size
    }


    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var team_name: TextView? = null
        var Tv_m: TextView? = null
        var Tv_w: TextView? = null
        var tv_L: TextView? = null
        var tv_T: TextView? = null
        var tv_NR: TextView? = null
        var tv_PTS: TextView? = null
        var tv_NRR: TextView? = null

        init {
            team_name = itemView.findViewById(R.id.team_name)
            Tv_m = itemView.findViewById(R.id.Tv_m)
            Tv_w = itemView.findViewById(R.id.Tv_w)
            tv_L = itemView.findViewById(R.id.tv_L)
            tv_T = itemView.findViewById(R.id.tv_T)
            tv_NR = itemView.findViewById(R.id.tv_NR)
            tv_PTS = itemView.findViewById(R.id.tv_PTS)
            tv_NRR = itemView.findViewById(R.id.tv_NRR)
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