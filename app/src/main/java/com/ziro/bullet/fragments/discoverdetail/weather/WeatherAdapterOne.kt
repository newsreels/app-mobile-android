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
import com.ziro.bullet.model.discoverNew.weather.Hour
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapterOne(
    private var context: Activity
) : RecyclerView.Adapter<WeatherAdapterOne.NewViewHolder?>() {
    private var holder: WeatherAdapterOne.NewViewHolder? = null
    private var listHour = listOf<Hour>()
    private lateinit var hour: Hour

    private lateinit var discoverChildInterface: DiscoverChildInterface
    private var weatherForecastResponse: WeatherForecastResponse? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherAdapterOne.NewViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false)
        return NewViewHolder(view)
    }

    fun setIsFer(weatherForecastResponse: WeatherForecastResponse){
        this.weatherForecastResponse = weatherForecastResponse
    }

    override fun onBindViewHolder(holder: WeatherAdapterOne.NewViewHolder, position: Int) {
        this.holder = holder
        hour = listHour[position]

        if (!hour.condition.icon.isNullOrEmpty()) {
            holder.iv_future_icon?.let {
                Glide.with(context)
                    .load("https:${hour.condition.icon}")
                    .into(it)
            }
        }
        if (!weatherForecastResponse?.isfernite!!) {
            holder.tv_future_temp?.text = "${hour.temp_c.toInt()}\u00B0"
        } else {
            holder.tv_future_temp?.text = "${hour.temp_f.toInt()}\u00B0"
        }


        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
        val calender = Calendar.getInstance()
        calender.time = simpleDateFormat.parse(hour.time)!!
        if(position == 0){
            holder.tv_future_day?.text = "Now"
        }else{
            holder.tv_future_day?.text =
                SimpleDateFormat("hh a", Locale.ENGLISH).format(calender.time)
        }



    }

    override fun getItemCount(): Int {
        return listHour.size
//        return if (listHour.size <= 5) {
//            listHour.size
//        } else {
//            5
//        }
    }

    fun updateForecast(listHour: List<Hour>) {
        this.listHour = listHour
        notifyDataSetChanged()
    }

//    fun setIsFer(ferh:Boolean){
//        this.isFernite = ferh
//    }

    inner class NewViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var iv_future_icon: ImageView?
        var tv_future_day: TextView? = null
        var tv_future_temp: TextView? = null


        init {
            iv_future_icon = itemView?.findViewById(R.id.iv_future_icon)
            tv_future_day = itemView?.findViewById(R.id.tv_future_day)

            tv_future_temp = itemView?.findViewById(R.id.tv_future_temp)
        }
    }

}



