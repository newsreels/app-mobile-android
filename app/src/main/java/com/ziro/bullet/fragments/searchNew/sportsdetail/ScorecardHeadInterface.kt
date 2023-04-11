package com.ziro.bullet.fragments.searchNew.sportsdetail

import com.ziro.bullet.model.discoverNew.scorecard.ScorecardResponse
import com.ziro.bullet.model.discoverNew.sportstable.SportsTable
import com.ziro.bullet.model.discoverNew.spotsinfo.SportsInfoResponse
import com.ziro.bullet.model.discoverNew.sportsteam.SportTeam
import com.ziro.bullet.model.discoverNew.tabletest.SportTablenew

interface ScorecardHeadInterface {
    fun getScorecard(position: Int)
}