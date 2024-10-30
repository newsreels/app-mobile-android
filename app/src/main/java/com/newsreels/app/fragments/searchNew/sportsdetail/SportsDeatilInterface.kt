package com.newsreels.app.fragments.searchNew.sportsdetail

import com.newsreels.app.model.discoverNew.scorecard.ScorecardResponse
import com.newsreels.app.model.discoverNew.sportstable.SportsTable
import com.newsreels.app.model.discoverNew.spotsinfo.SportsInfoResponse
import com.newsreels.app.model.discoverNew.sportsteam.SportTeam
import com.newsreels.app.model.discoverNew.tabletest.SportTablenew

interface SportsDeatilInterface {
    fun loadingData(isLoading: Boolean)

    fun error(error: String, topic: String)

    fun getSportInfo(sportsInfoResponse: SportsInfoResponse?)
    fun getSportsTeam(sportsTeam: SportTeam?)
    fun getSportsTable(sportsTable: SportTablenew?)
    fun getScorecard(scorecardResponse: ScorecardResponse?)

}