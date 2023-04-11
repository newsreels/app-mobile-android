package com.ziro.bullet.fragments.test

import android.app.Activity
import android.util.Log
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.analytics.AnalyticsEvents.logEvent
import com.ziro.bullet.analytics.Events
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.fragments.searchNew.sportsdetail.SportsDeatilInterface
import com.ziro.bullet.interfaces.LikeInterface
import com.ziro.bullet.model.discoverNew.spotsinfo.SportsInfoResponse
import com.ziro.bullet.utills.InternetCheckHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class ReelsNewPresenter constructor(
    private val activity: Activity? = null,
    private val reelFraInterface: ReelFraInterface? = null
) {

    companion object {
        const val SPORTS = "Sports"
    }

    private val headerPrefix = "Bearer "
    private val mPrefs: PrefConfig = PrefConfig(activity)
    private val cacheManager: DbHandler = DbHandler(activity)


    open fun like(reelid: String, likeInterface: LikeInterface?, like: Boolean) {
        try {
            if (!InternetCheckHelper.isConnected()) {
                likeInterface?.failure()
                return
            } else {
                val params: MutableMap<String, String> = HashMap()
                params[Events.KEYS.ARTICLE_ID] = reelid
                logEvent(
                    activity,
                    params,
                    Events.FEED_LIKE
                )
                val call = ApiClient
                    .getInstance(activity)
                    .api
                    .likeUnlikeArticle("Bearer " + mPrefs.accessToken, reelid, like)

                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.code() == 200) {
                            likeInterface?.success(like)
                        } else {
                            likeInterface?.failure()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("likeArticle", "RES: " + t.message)
                        likeInterface?.failure()
                    }
                })
            }
        } catch (t: Exception) {
            Log.e("likeArticle", "RES: " + t.message)
            likeInterface?.failure()
        }
    }

}

