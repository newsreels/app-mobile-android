package com.ziro.bullet.fragments.searchNew.sportsdetail

import com.ziro.bullet.model.discoverNew.scorecard.ScorecardResponse
import com.ziro.bullet.model.discoverNew.sportstable.SportsTable
import com.ziro.bullet.model.discoverNew.spotsinfo.SportsInfoResponse
import com.ziro.bullet.model.discoverNew.sportsteam.SportTeam
import com.ziro.bullet.model.discoverNew.tabletest.SportTablenew

interface SportsDeatilInterface {
    fun loadingData(isLoading: Boolean)

    fun error(error: String, topic: String)

    fun getSportInfo(sportsInfoResponse: SportsInfoResponse?)
    fun getSportsTeam(sportsTeam: SportTeam?)
    fun getSportsTable(sportsTable: SportTablenew?)
    fun getScorecard(scorecardResponse: ScorecardResponse?)

}