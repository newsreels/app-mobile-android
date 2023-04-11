package com.ziro.bullet.analytics

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.data.PrefConfig
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AnalyticsEvents {

    fun logEvent(mContext: Context?, event: String) {
        val prefConfig = PrefConfig(mContext)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(mContext!!)

        try {
            firebaseAnalytics.setUserProperty(Events.KEYS.USER, prefConfig.isUserObject.id)
        } catch (ignore: Exception) {
        }
        firebaseAnalytics.logEvent(event, Bundle())
    }

    fun logEvent(mContext: Context?, params: Map<String, String>, event: String) {
        val prefConfig = PrefConfig(mContext)

        val firebaseAnalytics = mContext?.let { FirebaseAnalytics.getInstance(it) }

        try {
            firebaseAnalytics?.setUserProperty(Events.KEYS.USER, prefConfig.isUserObject.id)
        } catch (ignore: Exception) {
        }
        firebaseAnalytics?.logEvent(event, params.toBundle())
    }

    fun reelViewEvent(mContext: Context?, reelId: String) {
        val prefConfig = PrefConfig(mContext)
        val call = ApiClient
            .getInstance()
            .api
            .reelViewEvent(
                "Bearer " + prefConfig.accessToken,
                reelId
            )
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {}
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
        })
    }

    fun reelDurationEvent(
        mContext: Context?,
        params: Map<String, String>,
        event: String,
        reelId: String
    ) {
        val prefConfig = PrefConfig(mContext)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(mContext!!)
        var duration = "0"
        if (params.containsKey(Events.KEYS.DURATION)) {
            duration = params[Events.KEYS.DURATION]!!
        }
        val reelduration: MutableMap<String, String> = HashMap()
        reelduration["duration"] = duration
        try {
            firebaseAnalytics.setUserProperty(Events.KEYS.USER, prefConfig.isUserObject.id)
        } catch (ignore: Exception) {
            ignore
        }
        firebaseAnalytics.logEvent(event, params.toBundle())

        val call = ApiClient
            .getInstance()
            .api
            .reelDurationEvent(
                "Bearer " + prefConfig.accessToken, reelId, reelduration)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {}
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
        })
    }

    fun articleViewEvent(mContext: Context?, articleId: String) {
        val prefConfig = PrefConfig(mContext)
        val call = ApiClient
            .getInstance()
            .api
            .articleViewCount(
                "Bearer " + prefConfig.accessToken,
                articleId
            )
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {}
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
        })
    }

    fun logEventWithAPI(mContext: Context?, params: Map<String, String>, event: String) {
//        if (BuildConfig.DEBUG) {
//            return;
//        }
        val prefConfig = PrefConfig(mContext)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(mContext!!)
        var duration = "0"
        if (params.containsKey(Events.KEYS.DURATION)) {
            duration = params[Events.KEYS.DURATION]!!
        }
        try {
            firebaseAnalytics.setUserProperty(Events.KEYS.USER, prefConfig.isUserObject.id)
        } catch (ignore: Exception) {
            ignore
        }
        firebaseAnalytics.logEvent(event, params.toBundle())
        val call = ApiClient
            .getInstance()
            .api
            .event(
                "Bearer " + prefConfig.accessToken,
                params[Events.KEYS.ARTICLE_ID],
                event,
                duration
            )
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {}
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {}
        })
    }

    fun Map<String, Any?>.toBundle(): Bundle = bundleOf(*this.toList().toTypedArray())
}