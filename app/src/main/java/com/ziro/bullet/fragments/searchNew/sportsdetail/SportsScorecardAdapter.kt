package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.model.discoverNew.scorecard.Bat
import com.ziro.bullet.model.discoverNew.scorecard.Prns


class SportsScorecardAdapter(
    private var context: Activity
) : RecyclerView.Adapter<SportsScorecardAdapter.ViewHolder>() {

    private lateinit var team1: Bat
    private lateinit var prns1: Prns
    private lateinit var teamList1: List<Bat>
    private lateinit var prns: List<Prns>
//    var c: String =""
//    var b: String =""
    private var holder: SportsScorecardAdapter.ViewHolder? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SportsScorecardAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sport_bat_score, parent, false)
        return ViewHolder(view)
    }

    fun updatedate(teamList1: List<Bat>, prns: List<Prns>) {
        this.teamList1 = teamList1
        this.prns = prns
    }
    fun updateInterface(){

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SportsScorecardAdapter.ViewHolder, position: Int) {
        this.holder = holder
        team1 = teamList1[position]
        prns1 = prns[position]

        holder.Tv_r?.text = "${team1.r} "
        holder.Tv_b?.text = "${team1.b} "
        holder.tv_four?.text = "${team1.score4} "
        holder.tv_six?.text = "${team1.score6} "
        holder.tv_SR?.text = "${team1.sr} "

        var sp = SportsDetailActivity()
        var name: String = sp.playerFullNameNew(team1.pid, prns)
        holder.player_name?.text = name
//        Log.e("testtab", "onBindViewHolder:R ${team1.fid}" )
//        if( team1.fid != 0){
//            c = (sp.playerLastName(team1.fid, prns))
//        }
//
//        if(team1.bid != 0){
//            b  = (sp.playerLastName(team1.bid, prns))
//        }


        var c: String = (sp.playerLastName(team1.fid, prns))
        var b: String = (sp.playerLastName(team1.bid, prns))
        Log.e("testtab", "c B: $c $b", )
        if ((c !="" && b != "")) {
            holder.tv_cb?.text = " c [$c] b [$b]"
        } else if (!c.isNullOrEmpty()) {
            holder.tv_cb?.text = " c [$c]"
        } else if (!b.isNullOrEmpty()) {
            holder.tv_cb?.text = "b [$b]"
        }else{
            holder.tv_cb?.text = team1.lpTx
        }


    }

    override fun getItemCount(): Int {
        return teamList1.size
    }


    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var player_name: TextView? = null
        var Tv_r: TextView? = null
        var Tv_b: TextView? = null
        var tv_four: TextView? = null
        var tv_six: TextView? = null
        var tv_SR: TextView? = null
        var tv_PTS: TextView? = null
        var tv_cb: TextView? = null

        init {
            player_name = itemView.findViewById(R.id.player_name)
            Tv_r = itemView.findViewById(R.id.Tv_r)
            Tv_b = itemView.findViewById(R.id.Tv_b)
            tv_four = itemView.findViewById(R.id.tv_four)
            tv_six = itemView.findViewById(R.id.tv_six)
            tv_SR = itemView.findViewById(R.id.tv_SR)
            tv_PTS = itemView.findViewById(R.id.tv_PTS)
            tv_cb = itemView.findViewById(R.id.tv_cb)
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