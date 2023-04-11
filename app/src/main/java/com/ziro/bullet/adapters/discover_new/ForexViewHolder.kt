package com.ziro.bullet.adapters.discover_new

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.trading.CryptoForexApiResponse

class ForexViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun onBind(
        title: String,
        cryptoForexApiResponse: CryptoForexApiResponse?,
        discoverChildInterface: DiscoverChildInterface
    ) {
        if (cryptoForexApiResponse == null) {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.GONE
        } else {
            itemView.findViewById<TextView>(R.id.tv_more).visibility = View.VISIBLE
        }

        itemView.findViewById<TextView>(R.id.tv_title).apply {
            text = title
        }

        itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
            if (cryptoForexApiResponse != null) {
                discoverChildInterface.onForexSeeAllClick(cryptoForexApiResponse)
            }
        }
        if (cryptoForexApiResponse != null) {
            itemView.findViewById<LinearLayout>(R.id.ll_topics_shimmer).visibility = View.GONE
            val cryptoAdapter = ForexPricesAdapter()
            itemView.findViewById<RecyclerView>(R.id.rv_trending_topics).apply {
                layoutManager =
                    GridLayoutManager(itemView.context, 2, GridLayoutManager.HORIZONTAL, false)
                adapter = cryptoAdapter
                visibility = View.VISIBLE
            }
            cryptoAdapter.updateCryptoPrices(cryptoForexApiResponse.tickers!!)
        }
    }
}