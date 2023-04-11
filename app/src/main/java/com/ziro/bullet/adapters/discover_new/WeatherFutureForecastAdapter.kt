package com.ziro.bullet.adapters.discover_new

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.weather.Forecastday
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import java.text.SimpleDateFormat
import java.util.*

class WeatherFutureForecastAdapter :
    RecyclerView.Adapter<WeatherFutureForecastAdapter.FutureForecastViewHolder>() {

    private var futureForecastList = listOf<Forecastday>()
    private lateinit var discoverChildInterface: DiscoverChildInterface
    private var weatherForecastResponse: WeatherForecastResponse? = null


    inner class FutureForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(forecast: Forecastday) {

            if(!weatherForecastResponse?.isfernite!!){
                itemView.findViewById<TextView>(R.id.tv_future_temp).text =
                    "${forecast.day.avgtemp_c.toInt()} \u2103"
            }else{
                itemView.findViewById<TextView>(R.id.tv_future_temp).text =
                    "${forecast.day.avgtemp_f.toInt()} ℉"
            }

//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            Glide.with(itemView.context)
                .load("https:${forecast.day.condition.icon}")
                .into(itemView.findViewById(R.id.iv_future_icon))

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val calender = Calendar.getInstance()
            calender.time = simpleDateFormat.parse(forecast.date)!!
            itemView.findViewById<TextView>(R.id.tv_future_day).text =
                SimpleDateFormat("EEE", Locale.ENGLISH).format(calender.time)

            itemView.findViewById<LinearLayout>(R.id.ll1).setOnClickListener {
                weatherForecastResponse?.let { it1 ->
                    discoverChildInterface.searchChildWeatherSecondOnClick(
                        it1
                    )
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureForecastViewHolder {
        return FutureForecastViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.future_forcast_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FutureForecastViewHolder, position: Int) {
//        if ((position + 1) < futureForecastList.size) {
//            holder.onBind(futureForecastList[position + 1])
//        }
        if ((position) < futureForecastList.size) {
            holder.onBind(futureForecastList[position])
        }
    }
//    fun setIsFer(ferh:Boolean?){
//        this.isFernite = ferh
//    }
    override fun getItemCount(): Int {
//        return futureForecastList.size - 1
        return futureForecastList.size
    }

    fun updateForecast(futureForecastList: List<Forecastday>) {
        this.futureForecastList = futureForecastList
        notifyDataSetChanged()
    }

    fun updateweatherForecastResponse(weatherForecastResponse: WeatherForecastResponse) {
        this.weatherForecastResponse = weatherForecastResponse
        notifyDataSetChanged()
    }
    fun addChildListenerDiscover(
        discoverChildInterface: DiscoverChildInterface
    ) {
        this.discoverChildInterface = discoverChildInterface
    }
}