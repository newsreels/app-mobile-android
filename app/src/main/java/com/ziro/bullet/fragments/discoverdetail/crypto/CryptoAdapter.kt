package com.ziro.bullet.fragments.discoverdetail.crypto

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.model.discoverNew.trading.Ticker

class CryptoAdapter(
    private var context: Activity,
    private var cryptoSeeAllInterface: CryptoSeeAllInterface?
) : RecyclerView.Adapter<CryptoAdapter.NewViewHolder?>() {
    private var holder: CryptoAdapter.NewViewHolder? = null
    private var tickerList = mutableListOf<Ticker>()
    private lateinit var ticker: Ticker


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoAdapter.NewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.crypto_items_layout, parent, false)
        return NewViewHolder(view)
    }


    override fun onBindViewHolder(holder: CryptoAdapter.NewViewHolder, position: Int) {
        this.holder = holder
        ticker = tickerList[position]

        if (!ticker.icon.isNullOrEmpty()) {
            holder.imageView?.let {
                if (!context.isDestroyed)
                    Glide.with(it.context)
                        .load(ticker.icon)
                        .into(it.findViewById(R.id.iv_symbol))
            }

        }
        holder.tvTitle?.text = ticker.ticker
        if (!ticker.tickerName.isNullOrEmpty()) {
            holder.tvSubtitle?.text = ticker.tickerName
        }
        if (ticker.lastQuote == null) {
            holder.tv_item_price?.text = "$${ticker.min?.closingPrice}"
        } else {
            holder.tv_item_price?.text = "$${ticker.lastQuote?.bidPrice}"
        }

       holder.tv_percentage?.text =  "${String.format("%, .2f", ticker.todaysChangePerc)}%"

        if (ticker.todaysChangePerc < 0) {
//            holder.tv_percentage?.setCompoundDrawablesWithIntrinsicBounds(
//                R.drawable.ic_arrowdown, 0, 0, 0);
//                    fun String.toColor(): Int = Color.parseColor(this)
            holder.arrowImg?.setImageDrawable(context.getDrawable(R.drawable.ic_arrowdown))
            holder.consbg?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.crypto_item_peach_bg
                )
            );
//            holder.tv_percentage?.setBackgroundColor(ContextCompat.getColor(context, R.color.peach))
        } else {
//            holder.tv_percentage?.setCompoundDrawablesWithIntrinsicBounds(
//                R.drawable.ic_arrowup, 0, 0, 0);
            holder.arrowImg?.setImageDrawable(context.getDrawable(R.drawable.ic_arrowup))
            holder.consbg?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.crypto_item_bg
                )
            );
        }

    }

    override fun getItemCount(): Int {
        return tickerList.size
    }


    inner class NewViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var imageView: ImageView? = null
        var tvTitle: TextView? = null
        var tvSubtitle: TextView? = null
        var tv_item_price: TextView? = null
        var tv_percentage: TextView? = null

        var arrowImg: ImageView? = null
        var consbg: ConstraintLayout? = null


        init {
            imageView = itemView?.findViewById(R.id.iv_symbol)
            tvTitle = itemView?.findViewById(R.id.tv_item_title)
            tvSubtitle = itemView?.findViewById(R.id.tv_subtitle)
            tv_item_price = itemView?.findViewById(R.id.tv_item_price)
            tv_percentage = itemView?.findViewById(R.id.tv_percentage)

            consbg = itemView?.findViewById(R.id.consbg)
            arrowImg = itemView?.findViewById(R.id.ic_stock_updown)
        }

    }

    fun updateCryptoData(tickerList: ArrayList<Ticker>) {
        this.tickerList = tickerList
        notifyDataSetChanged()
    }
}


