package com.newsreels.app.model.discoverNew.trading.icons

data class TradingIconsResponse(
    val crypto: List<Crypto>,
    val currency: List<Currency>,
    val forex: List<Forex>
)