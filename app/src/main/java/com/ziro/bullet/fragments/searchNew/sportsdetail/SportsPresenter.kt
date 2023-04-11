package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.app.Activity
import android.util.Log
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.model.discoverNew.scorecard.ScorecardResponse
import com.ziro.bullet.model.discoverNew.spotsinfo.SportsInfoResponse
import com.ziro.bullet.model.discoverNew.sportsteam.SportTeam
import com.ziro.bullet.model.discoverNew.tabletest.SportTablenew
import com.ziro.bullet.utills.InternetCheckHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class SportsPresenter constructor(
    private val activity: Activity? = null,
    private val sportsDetailInterface: SportsDeatilInterface? = null
) {

    companion object {
        const val SPORTS = "Sports"
    }

    private val headerPrefix = "Bearer "
    private val mPrefs: PrefConfig = PrefConfig(activity)
    private val cacheManager: DbHandler = DbHandler(activity)


    fun getSportInfo(eid: String) {
        if (!InternetCheckHelper.isConnected()) {
            sportsDetailInterface!!.error(
                activity!!.getString(R.string.internet_error),
                "Info"
            )
        } else {
            val call = ApiClient.Instance.api.getSportsInfo(
                "https://livescore6.p.rapidapi.com/matches/v2/get-info",
                "6bf4026ecbmshc32c080f12c325dp1ca5f5jsn2ebfe99fd6b3",
                "livescore6.p.rapidapi.com",
                "cricket",
                "723812"
            )
            call.enqueue(object : Callback<SportsInfoResponse?> {

                override fun onResponse(
                    call: Call<SportsInfoResponse?>,
                    response: Response<SportsInfoResponse?>
                ) {
                    if (response.isSuccessful) {
                        sportsDetailInterface?.getSportInfo(response.body())
                    } else {
                        sportsDetailInterface?.error(
                            activity!!.getString(R.string.server_error),
                            "Info"
                        )
                    }
                }

                override fun onFailure(call: Call<SportsInfoResponse?>, t: Throwable) {
                    t.printStackTrace()
                    sportsDetailInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        "Info"
                    )
                }
            })
        }
    }
    fun getSportTeams(eid: String) {
        if (!InternetCheckHelper.isConnected()) {
            sportsDetailInterface!!.error(
                activity!!.getString(R.string.internet_error),
                "Info"
            )
        } else {
            val call = ApiClient.Instance.api.getSportTeamsApi(
                "https://livescore6.p.rapidapi.com/matches/v2/get-lineups",
                "6bf4026ecbmshc32c080f12c325dp1ca5f5jsn2ebfe99fd6b3",
                "livescore6.p.rapidapi.com",
                "cricket",
                "723812"
            )
            call.enqueue(object : Callback<SportTeam?> {
                override fun onResponse(
                    call: Call<SportTeam?>,
                    response: Response<SportTeam?>
                ) {
                    if (response.isSuccessful) {
                        sportsDetailInterface?.getSportsTeam(response.body())
                    } else {
                        sportsDetailInterface?.error(
                            activity!!.getString(R.string.server_error),
                            "Info"
                        )
                    }
                }

                override fun onFailure(call: Call<SportTeam?>, t: Throwable) {
                    t.printStackTrace()
                    sportsDetailInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        "Info"
                    )
                }
            })
        }
    }
    fun getScorecard(eid: String) {
        if (!InternetCheckHelper.isConnected()) {
            sportsDetailInterface!!.error(
                activity!!.getString(R.string.internet_error),
                "Info"
            )
        } else {
            val call = ApiClient.Instance.api.getSportScorecard(
                "https://livescore6.p.rapidapi.com/matches/v2/get-innings",
                "6bf4026ecbmshc32c080f12c325dp1ca5f5jsn2ebfe99fd6b3",
                "livescore6.p.rapidapi.com",
                "cricket",
                "723812"
            )
            call.enqueue(object : Callback<ScorecardResponse?> {
                override fun onResponse(
                    call: Call<ScorecardResponse?>,
                    response: Response<ScorecardResponse?>
                ) {
                    Log.e("getSportScorecrd", "onResponse: 1", )
                    if (response.isSuccessful) {
                        Log.e("getSportScorecrd", "onResponse: 1", )
                        sportsDetailInterface?.getScorecard(response.body())
                    } else {
                        Log.e("getSportScorecrd", "onResponse: 1", )
                        sportsDetailInterface?.error(
                            activity!!.getString(R.string.server_error),
                            "Info"
                        )
                    }
                }

                override fun onFailure(call: Call<ScorecardResponse?>, t: Throwable) {
                    Log.e("getSportScorecrd", "fail", )
                    t.printStackTrace()
                    sportsDetailInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        "Info"
                    )
                }
            })
        }
    }


    fun getSportTable(eid: String) {
        if (!InternetCheckHelper.isConnected()) {
            sportsDetailInterface!!.error(
                activity!!.getString(R.string.internet_error),
                "Info"
            )
        } else {
            val call = ApiClient.Instance.api.getSportTableApi(
                "https://livescore6.p.rapidapi.com/matches/v2/get-table",
                "6bf4026ecbmshc32c080f12c325dp1ca5f5jsn2ebfe99fd6b3",
                "livescore6.p.rapidapi.com",
                "cricket",
                "723812"
            )
            call.enqueue(object : Callback<SportTablenew?> {
                override fun onResponse(
                    call: Call<SportTablenew?>,
                    response: Response<SportTablenew?>
                ) {
                    Log.e("test1", "onResponse: ", )
                    if (response.isSuccessful) {
                        Log.e("test1", "onResponse: 2", )
                        sportsDetailInterface?.getSportsTable(response.body())
                    } else {
                        Log.e("test1", "onResponse: 3", )
                        sportsDetailInterface?.error(
                            activity!!.getString(R.string.server_error),
                            "Info"
                        )
                    }
                }

                override fun onFailure(call: Call<SportTablenew?>, t: Throwable) {
                    Log.e("test1", "fail: 2", )
                    t.printStackTrace()
                    sportsDetailInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        "Info"
                    )
                }
            })
        }
    }
}

