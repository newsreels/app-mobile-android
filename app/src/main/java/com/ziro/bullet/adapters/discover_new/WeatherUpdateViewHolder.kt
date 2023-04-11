package com.ziro.bullet.adapters.discover_new

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.fragments.DiscoverChildInterface
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import java.text.SimpleDateFormat
import java.util.*

class WeatherUpdateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var swOnOff: SwitchCompat
    private var prefConfig: PrefConfig? = null
    var isfernite: Boolean = false
    var weatherForecastResponseNew: WeatherForecastResponse? = null
    val futureForecastAdapter = WeatherFutureForecastAdapter()

    fun onBind(
        context: Context,
        weatherForecastResponse: WeatherForecastResponse?,
        discoverChildInterface: DiscoverChildInterface
    ) {

        this.weatherForecastResponseNew = weatherForecastResponse
        prefConfig = PrefConfig(context)
        // "\u2109" for Fahrenheit Symbol

        swOnOff = itemView.findViewById<SwitchCompat>(R.id.swOnOffw)
        swOnOff.textOn = context.getString(R.string.c)
        swOnOff.textOff = context.getString(R.string.f)
        swOnOff.isChecked = weatherForecastResponseNew?.isfernite != true

        if (weatherForecastResponse != null) {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH)
            val calender = Calendar.getInstance()
            calender.time =
                simpleDateFormat.parse(weatherForecastResponse.current?.last_updated ?: "") as Date

            itemView.findViewById<ConstraintLayout>(R.id.cl_forecast).setOnClickListener {
                if (discoverChildInterface != null) {
                    discoverChildInterface.searchChildWeatherSecondOnClick(weatherForecastResponse)
                }
            }

            itemView.findViewById<TextView>(R.id.tv_more).setOnClickListener {
                if (discoverChildInterface != null) {
                    discoverChildInterface.searchChildWeatherSecondOnClick(weatherForecastResponse)
                }
            }

            itemView.findViewById<LinearLayout>(R.id.ll_other_forecast).setOnClickListener {
                if (discoverChildInterface != null) {
                    discoverChildInterface.searchChildWeatherSecondOnClick(weatherForecastResponse)
                }
            }

            itemView.findViewById<TextView>(R.id.tv_location).text =
                "${weatherForecastResponse.location?.name}, ${weatherForecastResponse.location?.country}"

            itemView.findViewById<TextView>(R.id.tv_wind_speed).text =
                "${itemView.context.resources.getString(R.string.wind)} ${weatherForecastResponse.current?.wind_kph}/Kmh"

//            if (weatherForecastResponse.isfernite) {
//                swOnOff.textOn = context.getString(R.string.c)
//                swOnOff.textOff = context.getString(R.string.f)
//            } else {
//                swOnOff.textOn = context.getString(R.string.c)
//                swOnOff.textOff = context.getString(R.string.f)
//            }

//        swOnOff.setTextColor(Color.WHITE);
//        swOnOff.setSwitchTextAppearance(this, Color.WHITE);
            bindData()
            swOnOff.setTextColor(context.resources.getColor(R.color.white))
            swOnOff.setTextColor(context.resources.getColor(R.color.white))
            swOnOff.setSwitchTextAppearance(context, context.resources.getColor(R.color.white))
            swOnOff.setOnClickListener {
                //change if true to false viceversa
                weatherForecastResponseNew?.isfernite =
                    weatherForecastResponseNew?.isfernite == false
                //save the changes in pref
                prefConfig?.weaTemp = weatherForecastResponseNew?.isfernite == true

                bindData()
                futureForecastAdapter.notifyDataSetChanged()
            }
            if (!(weatherForecastResponseNew?.isfernite)!!) {
                itemView.findViewById<TextView>(R.id.tv_temperature).text =
                    weatherForecastResponse.current.temp_c.toInt().toString()
            } else {
                itemView.findViewById<TextView>(R.id.tv_temperature).text =
                    weatherForecastResponse.current.temp_f.toInt().toString()
            }


            itemView.findViewById<TextView>(R.id.tv_humidity).text =
                "${itemView.context.resources.getString(R.string.humidity)} ${weatherForecastResponse.current?.humidity}%"

            itemView.findViewById<TextView>(R.id.tv_rain).text =
                "${itemView.context.resources.getString(R.string.rain)} ${
                    weatherForecastResponse.forecast?.forecastday?.get(
                        0
                    )?.day?.daily_chance_of_rain
                }%"

//            Glide.with(itemView.context)
//                .load("https:${weatherForecastResponse.current.condition.icon}")
//                .into(itemView.findViewById(R.id.iv_weather_icon))

            val date = SimpleDateFormat("dd MMM, yy", Locale.ENGLISH).format(calender.time)
            val time = SimpleDateFormat("EEE | hh:mm a", Locale.ENGLISH).format(calender.time)

            itemView.findViewById<TextView>(R.id.tv_date).text = date
            itemView.findViewById<TextView>(R.id.tv_day_time).text = time



            itemView.findViewById<RecyclerView>(R.id.rv_future_forecast).apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = futureForecastAdapter
                weatherForecastResponse.forecast.forecastday.let {
                    futureForecastAdapter.updateForecast(
                        it
                    )
                }

                futureForecastAdapter.updateweatherForecastResponse(weatherForecastResponse)
                futureForecastAdapter.addChildListenerDiscover(discoverChildInterface)
                itemView.findViewById<ConstraintLayout>(R.id.cl_forecast).visibility = View.VISIBLE
                itemView.findViewById<ShimmerFrameLayout>(R.id.forecast_shimmer).visibility =
                    View.GONE
            }

        }

    }

    fun bindData() {
        if (!weatherForecastResponseNew?.isfernite!!) {
            itemView.findViewById<TextView>(R.id.tv_temperature).text =
                weatherForecastResponseNew?.current?.temp_c?.toInt().toString()
            itemView.findViewById<TextView>(R.id.tv_temp_unit).text = "℃"
        } else {
            itemView.findViewById<TextView>(R.id.tv_temperature).text =
                weatherForecastResponseNew?.current?.temp_f?.toInt().toString()

            itemView.findViewById<TextView>(R.id.tv_temp_unit).text = "\u2109"
        }
    }

}
