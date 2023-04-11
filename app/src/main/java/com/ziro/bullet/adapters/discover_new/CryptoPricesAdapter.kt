package com.ziro.bullet.adapters.discover_new

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.model.discoverNew.trading.Ticker

class CryptoPricesAdapter : RecyclerView.Adapter<CryptoPricesAdapter.CryptoPricesViewHolder>() {

    private var tickerList = listOf<Ticker>()

    inner class CryptoPricesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(ticker: Ticker) {
            itemView.findViewById<TextView>(R.id.tv_item_title).text = ticker.ticker

            itemView.findViewById<TextView>(R.id.tv_item_price).text =
                "$${ticker.min?.closingPrice}"

            if (!ticker.icon.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(ticker.icon)
//                    .placeholder(R.drawable.img_place_holder)
                    .into(itemView.findViewById(R.id.iv_symbol))
            }

            itemView.findViewById<TextView>(R.id.tv_percentage).text =
//                "${ticker.todaysChangePerc}"
                "${String.format("%, .2f", ticker.todaysChangePerc)}%"
            if (ticker.todaysChangePerc < 0) {
                itemView.findViewById<ImageView>(R.id.iv_stock_down).visibility = View.VISIBLE
                itemView.findViewById<ImageView>(R.id.iv_stock_up).visibility = View.INVISIBLE
                itemView.findViewById<TextView>(R.id.tv_percentage)
                    .setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            } else {
                itemView.findViewById<ImageView>(R.id.iv_stock_down).visibility = View.INVISIBLE
                itemView.findViewById<ImageView>(R.id.iv_stock_up).visibility = View.VISIBLE
                itemView.findViewById<TextView>(R.id.tv_percentage)
                    .setTextColor(ContextCompat.getColor(itemView.context, R.color.green_))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoPricesViewHolder {
        return CryptoPricesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.crypto_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CryptoPricesViewHolder, position: Int) {
        holder.onBind(tickerList[position])
    }

    override fun getItemCount(): Int {

        return if (tickerList.size <= 20) {
            tickerList.size
        } else {
            20
        }
    }

    fun updateCryptoPrices(tickerList: List<Ticker>) {
        this.tickerList = tickerList
        notifyDataSetChanged()
    }
}