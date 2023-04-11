package com.ziro.bullet.fragments.discoverdetail.forex

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.fragments.discoverdetail.crypto.CryptoSeeAllInterface
import com.ziro.bullet.model.discoverNew.trading.Ticker

class ForexAdapter(
    private var context: Activity,
    private var cryptoSeeAllInterface: CryptoSeeAllInterface?
) : RecyclerView.Adapter<ForexAdapter.NewViewHolder?>() {

    private var holder: ForexAdapter.NewViewHolder? = null
    private var tickerList = mutableListOf<Ticker>()
    private lateinit var ticker: Ticker


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForexAdapter.NewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.forex_items_layout, parent, false)
        return NewViewHolder(view)
    }


    override fun onBindViewHolder(holder: ForexAdapter.NewViewHolder, position: Int) {
        this.holder = holder
        ticker = tickerList[position]

        if (!ticker.iconFrom.isNullOrEmpty()) {
            holder.iv_symbol_from?.let {
                if (!context.isDestroyed)
                    Glide.with(context)
                        .load(ticker.iconFrom)
                        .into(it)
            }
        }
        if (!ticker.iconTo.isNullOrEmpty()) {
            holder.iv_symbol_to?.let {
                if (!context.isDestroyed)
                    Glide.with(context)
                        .load(ticker.iconTo)
                        .into(it)
            }
        }

        holder.tvTitle?.text = ticker.ticker

        if (ticker.lastQuote == null) {
            holder.tv_buy_price?.text = "${ticker.min?.closingPrice}"
            holder.tv_sell_price?.text = "${ticker.min?.openingPrice}"
        } else {
            holder.tv_buy_price?.text = "${ticker.lastQuote?.bidPrice}"
            holder.tv_sell_price?.text = "${ticker.lastQuote?.askPrice}"
        }
        if (!ticker.fromName.isNullOrEmpty() && !ticker.toName.isNullOrEmpty()) {
            holder.tvSubtitle?.text = "${ticker.fromName} / ${ticker.toName}"
        }

        holder.tv_percentage?.text = "${String.format("%, .3f", ticker.todaysChangePerc)}"


    }

    override fun getItemCount(): Int {
        return tickerList.size
    }


    inner class NewViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var iv_symbol_to: ImageView? = null
        var iv_symbol_from: ImageView? = null
        var tvTitle: TextView? = null
        var tvSubtitle: TextView? = null
        var tv_buy_price: TextView? = null
        var tv_sell_price: TextView? = null
        var tv_percentage: TextView? = null


        init {
            iv_symbol_to = itemView?.findViewById(R.id.iv_symbol_to)
            iv_symbol_from = itemView?.findViewById(R.id.iv_symbol_from)

            tvTitle = itemView?.findViewById(R.id.tv_item_title)
            tvSubtitle = itemView?.findViewById(R.id.tv_subtitle)

            tv_sell_price = itemView?.findViewById(R.id.tv_sell_price)
            tv_buy_price = itemView?.findViewById(R.id.tv_buy_price)
            tv_percentage = itemView?.findViewById(R.id.tv_percentage)
        }

    }

    fun updateForexData(tickerList: ArrayList<Ticker>) {
        this.tickerList = tickerList
        notifyDataSetChanged()
    }
}


