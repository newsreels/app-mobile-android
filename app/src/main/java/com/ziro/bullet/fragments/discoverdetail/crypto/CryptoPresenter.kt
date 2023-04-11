package com.ziro.bullet.fragments.discoverdetail.crypto

import android.app.Activity
import android.util.Log
import com.ziro.bullet.APIResources.ApiClient
import com.ziro.bullet.CacheData.DbHandler
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse
import com.ziro.bullet.presenter.NewDiscoverPresenter
import com.ziro.bullet.utills.InternetCheckHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class CryptoPresenter constructor(
    private val activity: Activity? = null,
    private val cryptoSeeAllInterface: CryptoSeeAllInterface? = null
) {

    companion object {
        const val CRYPTO = "Crypto"
    }

//    var isfernite: Boolean = false
    private val headerPrefix = "Bearer "
    private val mPrefs: PrefConfig = PrefConfig(activity)
    private val cacheManager: DbHandler = DbHandler(activity)

    fun getCryptoPrices(url: String, header: String) {
        if (!InternetCheckHelper.isConnected()) {
            cryptoSeeAllInterface?.error(
                activity!!.getString(R.string.internet_error),
                CRYPTO
            )
        } else {
            val call = ApiClient.getInstance().api.getCryptoPrices(url, header)
            call.enqueue(object : Callback<CryptoForexApiResponse?> {
                override fun onResponse(
                    call: Call<CryptoForexApiResponse?>,
                    response: Response<CryptoForexApiResponse?>
                ) {
                    if (response.isSuccessful) {
                        cryptoSeeAllInterface!!.getCryptoPrices(response.body())
                    } else {
                        cryptoSeeAllInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            CryptoPresenter.CRYPTO
                        )
                    }
                }

                override fun onFailure(call: Call<CryptoForexApiResponse?>, t: Throwable) {

                    Log.e("ERROR_TAG", "onFailure: ${t.localizedMessage}")
                    cryptoSeeAllInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        CryptoPresenter.CRYPTO
                    )
                }
            })
        }
    }

    fun getTradingItemsList() {
        if (!InternetCheckHelper.isConnected()) {
            cryptoSeeAllInterface!!.error(
                activity!!.getString(R.string.internet_error),
                NewDiscoverPresenter.TRADING_ITEMS_LIST
            )
        } else {
            val call = ApiClient.getInstance().api.getTradingItemsList(mPrefs.accessToken)
            call.enqueue(object : Callback<TradingIconsResponse?> {
                override fun onResponse(
                    call: Call<TradingIconsResponse?>,
                    response: Response<TradingIconsResponse?>
                ) {
                    if (response.isSuccessful) {
                        cryptoSeeAllInterface!!.getTradingItemsList(response.body())
                    } else {
                        cryptoSeeAllInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            NewDiscoverPresenter.TRADING_ITEMS_LIST
                        )
                    }
                }

                override fun onFailure(call: Call<TradingIconsResponse?>, t: Throwable) {

                    Log.e("ERROR_TAG", "onFailure: ${t.localizedMessage}")
                    cryptoSeeAllInterface!!.error(
                        activity!!.getString(R.string.server_error),
                        NewDiscoverPresenter.TRADING_ITEMS_LIST
                    )
                }
            })
        }
    }

    fun getForexPrices(url: String, header: String) {
        if (!InternetCheckHelper.isConnected()) {
            cryptoSeeAllInterface!!.error(
                activity!!.getString(R.string.internet_error),
                NewDiscoverPresenter.FOREX
            )
        } else {
            ApiClient.getInstance().api.getForexPrices(url, header).apply {
                enqueue(object : Callback<CryptoForexApiResponse?> {
                    override fun onResponse(
                        call: Call<CryptoForexApiResponse?>,
                        response: Response<CryptoForexApiResponse?>
                    ) {
                        if (response.isSuccessful) {
                            cryptoSeeAllInterface!!.getForexPrices(response.body())
                        } else {
                            cryptoSeeAllInterface!!.error(
                                activity!!.getString(R.string.server_error),
                                NewDiscoverPresenter.FOREX
                            )
                        }
                    }

                    override fun onFailure(call: Call<CryptoForexApiResponse?>, t: Throwable) {
                        cryptoSeeAllInterface!!.error(
                            activity!!.getString(R.string.server_error),
                            NewDiscoverPresenter.FOREX
                        )
                    }
                })
            }
        }
    }
}

