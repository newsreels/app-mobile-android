package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ziro.bullet.R
import com.ziro.bullet.model.discoverNew.liveScore.SportEvent
import com.ziro.bullet.model.discoverNew.scorecard.Prns
import com.ziro.bullet.model.discoverNew.scorecard.ScorecardResponse
import com.ziro.bullet.model.discoverNew.sportsteam.SportTeam
import com.ziro.bullet.model.discoverNew.spotsinfo.SportsInfoResponse
import com.ziro.bullet.model.discoverNew.tabletest.SportTablenew

class SportsDetailActivity : AppCompatActivity(), SportsDeatilInterface {

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var viewPagerAdapter: VPAdapter? = null
    var tvTeam1Name: TextView? = null
    var tvTeam2Name: TextView? = null
    var tvTeam1Overs: TextView? = null
    var tvTeam2Overs: TextView? = null
    var tvTeam1Score: TextView? = null
    var tvTeam2Score: TextView? = null
    var tvEventStatus: TextView? = null
    var tvSchedule: TextView? = null
    var tvEventInfo: TextView? = null
    val sportsInfoFragment1 = SportsInfoFragment1()
    val scorecardFragment2 = ScorecardFragment2()
    val summaryFragment3 = SummaryFragment3()
    val teamsFragment4 = TeamsFragment4()
    val tableFragment5 = TableFragment5()


    private lateinit var sportsPresenter: SportsPresenter

    private var sportsEventSelected: SportEvent? = null
    private var sportsInfo: SportsInfoResponse? = null
    private var sportsTeam: SportTeam? = null
    private var sportsTable: SportTablenew? = null
    private var scorecard: ScorecardResponse? = null
    var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sports_detail)
        bindView()
        init()
    }

    fun bindView() {
        tvEventInfo = findViewById(R.id.tv_event_info)

        tvTeam1Name = findViewById(R.id.tv_team1_name)
        tvTeam2Name = findViewById(R.id.tv_team2_name)

        tvTeam1Overs = findViewById(R.id.tv_team1_overs)
        tvTeam2Overs = findViewById(R.id.tv_team2_overs)

        tvTeam1Score = findViewById(R.id.tv_team1_score)
        tvTeam2Score = findViewById(R.id.tv_team2_score)

        tvEventStatus = findViewById(R.id.tv_event_status)
        tvSchedule = findViewById(R.id.tv_schedule)

    }

    fun init() {
        sportsPresenter = SportsPresenter(this, this)

        sportsEventSelected = intent.getParcelableExtra("sportsDeatil")

        apiCalls()
//        sportsEventSelected?.Eid?.let { sportsPresenter.getSportInfo(it) }

        bindInput()

        viewPager = findViewById(R.id.sportsViewPager);
        tabLayout = findViewById(R.id.tabLayout2);
        with(tabLayout) { this?.setupWithViewPager(viewPager) }
        viewPagerAdapter = VPAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )

//        bundle.putString("DATE", "2017-10-19")
//
//        bundle.putParcelable("scoreInfo", sportsInfo)
//

//
//        scorecardFragment2.arguments = bundle
//        summaryFragment3.arguments = bundle
//        teamsFragment4.arguments = bundle
//        tableFragment5.arguments = bundle
//
//
//
//        viewPagerAdapter?.addFrag(scorecardFragment2, "scorecard")
//        viewPagerAdapter?.addFrag(summaryFragment3, "summmary")
//        viewPagerAdapter?.addFrag(teamsFragment4, "teams")
//        viewPagerAdapter?.addFrag(tableFragment5, "table")
//
//        with(viewPager) { this?.setAdapter(viewPagerAdapter) }
        with(viewPager) { this?.setAdapter(viewPagerAdapter) }
//        tabLayout?.size
//        tabLayout?.newTab()?.let { tabLayout?.addTab(it,0) }


    }


    @SuppressLint("SetTextI18n")
    private fun bindInput() {

        tvEventInfo?.text = sportsEventSelected?.EpsL

        tvTeam1Name?.text = sportsEventSelected?.team1?.get(0)?.Nm
        tvTeam2Name?.text = sportsEventSelected?.team2?.get(0)?.Nm

        if (sportsEventSelected?.Tr1C2 != null)
            tvTeam1Score?.text = "${sportsEventSelected?.Tr1C2}/${sportsEventSelected?.Tr1CW2} & " +
                    "${sportsEventSelected?.Tr1C1}/${sportsEventSelected?.Tr1CW1}"
        else
            tvTeam1Score?.text = "${sportsEventSelected?.Tr1C1}/${sportsEventSelected?.Tr1CW1}"

        if (sportsEventSelected?.Tr2C2 != null)
            tvTeam2Score?.text = "${sportsEventSelected?.Tr2C2}/${sportsEventSelected?.Tr2CW2} & " +
                    "${sportsEventSelected?.Tr2C1}/${sportsEventSelected?.Tr2CW1}"
        else
            tvTeam2Score?.text = "${sportsEventSelected?.Tr2C1}/${sportsEventSelected?.Tr2CW1}"

        if (sportsEventSelected?.Tr1CO2 != null)
            tvTeam1Overs?.text = "(${sportsEventSelected?.Tr1CO2} & ${sportsEventSelected?.Tr1CO1})"
        else
            tvTeam1Overs?.text = "(${sportsEventSelected?.Tr1CO1})"

        if (sportsEventSelected?.Tr2CO2 != null)
            tvTeam2Overs?.text = "(${sportsEventSelected?.Tr2CO2} & ${sportsEventSelected?.Tr2CO1})"
        else
            tvTeam2Overs?.text = "(${sportsEventSelected?.Tr2CO1})"

        if (sportsEventSelected?.ErnInf != null) {
            tvEventStatus?.text = "${sportsEventSelected?.ErnInf} - ${sportsEventSelected?.ECo}"
        } else {
            tvEventStatus?.text = sportsEventSelected?.ECo
        }
    }

    override fun loadingData(isLoading: Boolean) {

    }

    override fun error(error: String, topic: String) {
    }

    override fun getSportInfo(sportsInfoResponse: SportsInfoResponse?) {
        if (sportsInfoResponse != null) {
            sportsInfo = sportsInfoResponse
            bundle.putParcelable("scoreInfo", sportsInfo)
            bundle.putParcelable("sportsEventSelected", sportsEventSelected)
            sportsInfoFragment1.arguments = bundle
            viewPagerAdapter?.addFrag(sportsInfoFragment1, "info")
//            tabLayout?.newTab()?.let { tabLayout?.addTab(it,0) }
            viewPagerAdapter?.notifyDataSetChanged()
        }
    }

    override fun getSportsTeam(sportsTeamResponse: SportTeam?) {
        if (sportsTeamResponse != null) {
            sportsTeam = sportsTeamResponse
            bundle.putParcelable("sportsTeam", sportsTeam)
            teamsFragment4.arguments = bundle
            viewPagerAdapter?.addFrag(teamsFragment4, "teams")

//            tabLayout?.newTab()?.let { tabLayout?.addTab(it)}

            viewPagerAdapter?.notifyDataSetChanged()
//            with(viewPager) { this?.setAdapter(viewPagerAdapter) }
        }
    }

    override fun getSportsTable(sportsTableResponse: SportTablenew?) {

        if (sportsTableResponse != null) {
            sportsTable = sportsTableResponse
            bundle.putParcelable("sportsTable", sportsTable)
            tableFragment5.arguments = bundle
            viewPagerAdapter?.addFrag(tableFragment5, "table")

//            tabLayout?.newTab()?.let { tabLayout?.addTab(it)}

            viewPagerAdapter?.notifyDataSetChanged()
//            with(viewPager) { this?.setAdapter(viewPagerAdapter) }
        }
    }
    fun playerFullNameNew(pid: Int, prns: List<Prns>): String {

        var name: String = ""
        for (player in prns) {
            if (pid == player.pid) {
                return player.snm
            }
        }
//        when(pid){
//            prns.pid -> return prns.snm
//        }

        return name
    }

    fun playerLastName(pid: Int, prns: List<Prns>): String {

        var name: String = ""
        for (player in prns) {
            if (pid == player.pid) {
                return player.ln
            }
        }

        return name
    }
    override fun getScorecard(scorecardResponse: ScorecardResponse?) {
        if (scorecardResponse != null) {
            scorecard = scorecardResponse

            bundle.putParcelable("scorecard", scorecard)

            scorecardFragment2.arguments = bundle
            viewPagerAdapter?.addFrag(scorecardFragment2, "score")

//            tabLayout?.newTab()?.let { tabLayout?.addTab(it)}

            viewPagerAdapter?.notifyDataSetChanged()
//            with(viewPager) { this?.setAdapter(viewPagerAdapter) }
        }
    }

    fun apiCalls() {
        //Api call for Info tab
        sportsEventSelected?.Eid?.let { sportsPresenter.getSportInfo(it) }
        sportsEventSelected?.Eid?.let { sportsPresenter.getScorecard(it) }
        sportsEventSelected?.Eid?.let { sportsPresenter.getSportTeams(it) }
        sportsEventSelected?.Eid?.let { sportsPresenter.getSportTable(it) }

    }

}
