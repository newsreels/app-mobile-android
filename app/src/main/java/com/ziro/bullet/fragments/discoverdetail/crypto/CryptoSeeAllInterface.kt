package com.ziro.bullet.fragments.discoverdetail.crypto

import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse
import com.ziro.bullet.model.discoverNew.trading.Ticker
import com.ziro.bullet.model.discoverNew.trading.icons.TradingIconsResponse

interface CryptoSeeAllInterface {
    fun loadingData(isLoading: Boolean)

    fun error(error: String, topic: String)

    fun onItemClick(position: Int, item: Ticker?)
    fun onItemClickFollow(position: Int, item: Ticker?)

    fun getCryptoPrices(cryptoForexApiResponse: CryptoForexApiResponse?)
    fun getForexPrices(cryptoForexApiResponse: CryptoForexApiResponse?)
    fun getTradingItemsList(tradingIconsResponse: TradingIconsResponse?)

    //channel
    fun onItemChannelFollowed2(channel: Ticker?, position: Int)
    fun onItemChannleUnfollowed2(channel: Ticker?, position: Int)

}