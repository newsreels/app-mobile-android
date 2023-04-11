package com.ziro.bullet.fragments.discoverdetail.weather

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.weather.Forecastday
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdaptertwo(
    private var context: Activity
) : RecyclerView.Adapter<WeatherAdaptertwo.NewViewHolder?>() {
    private var holder: WeatherAdaptertwo.NewViewHolder? = null
    private var futureForecastList = listOf<Forecastday>()
    private lateinit var forecastday: Forecastday
    private lateinit var discoverChildInterface: DiscoverChildInterface
    private var weatherForecastResponse: WeatherForecastResponse? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherAdaptertwo.NewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.weather_items_layout, parent, false)
        return NewViewHolder(view)
    }

fun setIsFer(weatherForecastResponse: WeatherForecastResponse){
    this.weatherForecastResponse = weatherForecastResponse
}
    override fun onBindViewHolder(holder: WeatherAdaptertwo.NewViewHolder, position: Int) {
        this.holder = holder
        forecastday = futureForecastList[position]

        if (!forecastday.day.condition.icon.isNullOrEmpty()) {
            holder.iv_future_icon?.let {
                Glide.with(context)
                    .load("https:${forecastday.day.condition.icon}")
                    .into(it)
            }
        }
        if (!weatherForecastResponse?.isfernite!!) {
            holder.tv_temp_low?.text = "${forecastday.day.mintemp_c.toInt().toString()}°"
            holder.tv_temp_high?.text = "${forecastday.day.maxtemp_c.toInt().toString()}°"
        } else {
            holder.tv_temp_low?.text = "${forecastday.day.mintemp_f.toInt().toString()}°"
            holder.tv_temp_high?.text = "${forecastday.day.maxtemp_f.toInt().toString()}°"
        }

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val calender = Calendar.getInstance()
        calender.time = simpleDateFormat.parse(forecastday.date)
        holder.tv_future_day?.text =
            SimpleDateFormat("EEE", Locale.ENGLISH).format(calender.time)


    }

    override fun getItemCount(): Int {
        return futureForecastList.size
    }

    fun updateForecast(futureForecastList: List<Forecastday>) {
        this.futureForecastList = futureForecastList
        notifyDataSetChanged()
    }


    inner class NewViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var iv_future_icon: ImageView?
        var tv_future_day: TextView? = null
        var tv_temp_high: TextView? = null
        var tv_temp_low: TextView? = null


        init {
            iv_future_icon = itemView?.findViewById(R.id.iv_future_icon)
            tv_future_day = itemView?.findViewById(R.id.tv_future_day)

            tv_temp_low = itemView?.findViewById(R.id.tv_temp_low)
            tv_temp_high = itemView?.findViewById(R.id.tv_temp_high)
        }
    }

}



