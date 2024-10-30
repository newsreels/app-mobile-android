package com.newsreels.app.fragments.searchNew.sportsdetail

import com.newsreels.app.model.discoverNew.scorecard.ScorecardResponse
import com.newsreels.app.model.discoverNew.sportstable.SportsTable
import com.newsreels.app.model.discoverNew.spotsinfo.SportsInfoResponse
import com.newsreels.app.model.discoverNew.sportsteam.SportTeam
import com.newsreels.app.model.discoverNew.tabletest.SportTablenew

interface ScorecardHeadInterface {
    fun getScorecard(position: Int)
}