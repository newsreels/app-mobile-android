package com.ziro.bullet.fragments.discoverdetail.weather

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.activities.BaseActivity
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.fragments.discoverdetail.crypto.CryptoPresenter
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import com.ziro.bullet.utills.Utils
import java.text.SimpleDateFormat
import java.util.*


class WeatherActivity : BaseActivity() {
    private lateinit var cryptoPresenter: CryptoPresenter
    var recyclerView1: RecyclerView? = null
    var recyclerView2: RecyclerView? = null
    var cardview: CardView? = null
    private var ll_no_results: LinearLayout? = null
    private lateinit var back_img: ImageView
    var search_skeleton: LinearLayout? = null
    private var weatherForecastResponse: WeatherForecastResponse? = null
    var tv_search2: TextView? = null
    var tv_location: TextView? = null
    var tv_tempcel: TextView? = null
    var tv_temp_unit: TextView? = null
    var ts_temp_switch: SwitchCompat? = null
    var swOnOff: SwitchCompat? = null
    private var prefConfig: PrefConfig? = null
    var tv_status: TextView? = null
    var tv_highlow: TextView? = null
    private lateinit var weatherAdapterOne: WeatherAdapterOne
    private lateinit var weatherAdaptertwo: WeatherAdaptertwo
//
//    companion object {
//        private var forexSeeAllFragment: WeatherDetsilFragment? = null
//
//        lateinit var searchResultItem: SearchresultdataBase
//        private var goHome: GoHome? = null
//        private var isLoad: Boolean = false
//        private var isLast: Boolean = false
//
//        fun getInstance(bundle1: Bundle, goHome: GoHome): WeatherDetsilFragment {
//            if (forexSeeAllFragment == null)
//                forexSeeAllFragment = WeatherDetsilFragment()
//            forexSeeAllFragment!!.arguments = bundle1
//            this.goHome = goHome
//            isLoad = false
//            isLast = false
//            return forexSeeAllFragment!!
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.checkAppModeColor(this, false)
        setContentView(R.layout.fragment_weathernew)
        back_img = findViewById(R.id.back_img)
        val bar: ActionBar? = actionBar
        prefConfig = PrefConfig(this)

//        val actionBarTitleId: Int =
//            Resources.getSystem().getIdentifier("action_bar_title", "id", "android")
//        if (actionBarTitleId > 0) {
//            val title = findViewById<View>(actionBarTitleId) as TextView
//            title?.setTextColor(Color.RED)
//        }

//        bar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.active_dot)))

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        val decorView = window.decorView //set status background black
        decorView.systemUiVisibility =
            decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //set status text  light

        back_img.setOnClickListener(View.OnClickListener { onBackPressed() })
        bindView()
        init()
        setLayout()
    }

    override fun onResume() {
        super.onResume()
//        if (WeatherAct.goHome != null) WeatherAct.goHome!!.scrollUp()
        setLayout()
    }

    override fun onBackPressed() {
        var tt = weatherForecastResponse?.isfernite
        Log.e("TAG", "onBackPressed: $tt")
        super.onBackPressed()
        //        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    fun bindView() {
        recyclerView1 = findViewById(R.id.recycleview1)
        tv_location = findViewById(R.id.tv_location)
        tv_highlow = findViewById(R.id.tv_highlow)
        tv_status = findViewById(R.id.tv_status)
        tv_tempcel = findViewById(R.id.tv_tempcel)
        tv_temp_unit = findViewById(R.id.tv_unit)
        recyclerView2 = findViewById(R.id.recycleview2)
        cardview = findViewById(R.id.cardview)
        tv_search2 = findViewById(R.id.tv_search2)
        ll_no_results = findViewById(R.id.ll_no_results)
        back_img = findViewById(R.id.back_img)
        search_skeleton = findViewById(R.id.search_skeleton)
        ts_temp_switch = findViewById(R.id.ts_temp_switch)
        swOnOff = findViewById(R.id.swOnOff)
    }

    fun init() {
        //hide bottom nav
//        if (WeatherAct.goHome != null) WeatherAct.goHome!!.scrollUp()
        cryptoPresenter = CryptoPresenter(this)
        weatherForecastResponse = intent.getParcelableExtra("weatherInfo")

        back_img!!.setOnClickListener { this!!.onBackPressed() }
//Adapter 1
        if (!::weatherAdapterOne.isInitialized) {
            weatherAdapterOne = WeatherAdapterOne(this)
            cryptoPresenter = CryptoPresenter(this)
        }
        if (recyclerView1?.adapter == null) {
            recyclerView1?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView1?.adapter = weatherAdapterOne
            recyclerView1?.layoutManager!!.scrollToPosition(0)
        }
        //Adapter 2
        if (!::weatherAdaptertwo.isInitialized) {
            weatherAdaptertwo = WeatherAdaptertwo(this)
            cryptoPresenter = CryptoPresenter(this)
        }
        if (recyclerView2?.adapter == null) {
            recyclerView2?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerView2?.adapter = weatherAdaptertwo
            recyclerView2?.layoutManager!!.scrollToPosition(0)
        }

    }

    fun setLayout() {
        weatherForecastResponse?.let { weatherAdapterOne.setIsFer(it) }
        weatherForecastResponse?.let { weatherAdaptertwo.setIsFer(it) }

        weatherForecastResponse?.forecast?.forecastday?.get(0)?.hour?.let {
            val time: String = SimpleDateFormat("hh a", Locale.getDefault()).format(
                Calendar.getInstance().time
            )
            var matchtimeindex: Int = 0

            if (it != null) {
                for ((index, value) in it.withIndex()) {
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
                    val calender = Calendar.getInstance()
                    calender.time = simpleDateFormat.parse(value.time)!!
                    var apitime = SimpleDateFormat("hh a", Locale.ENGLISH).format(calender.time)
                    if (apitime.equals(time)) {
                        matchtimeindex = index
                    }

                }
            }
            val subList = it.subList(matchtimeindex, it.size)
            if (subList != null) {
                weatherAdapterOne.updateForecast(
                    subList
                )
            }
        }
        weatherForecastResponse?.forecast?.forecastday?.let { weatherAdaptertwo.updateForecast(it) }

        tv_location?.text = weatherForecastResponse?.location?.name
        tv_status?.text = weatherForecastResponse?.current?.condition?.text.toString()

        if (weatherForecastResponse?.isfernite == true) {
            swOnOff?.textOff = getString(R.string.f)
        } else {
            swOnOff?.textOn = getString(R.string.c)
        }
        swOnOff?.textOn = getString(R.string.c)
        swOnOff?.textOff = getString(R.string.f)
        swOnOff?.isChecked = weatherForecastResponse?.isfernite != true

        swOnOff?.setTextColor(this.resources.getColor(R.color.white))
        swOnOff?.setSwitchTextAppearance(this, resources.getColor(R.color.white))
        swOnOff?.setOnClickListener {
//            swOnOff?.isChecked = weatherForecastResponse?.isfernite != true
            //negate
            weatherForecastResponse?.isfernite = weatherForecastResponse?.isfernite == false

            prefConfig!!.weaTemp = weatherForecastResponse?.isfernite == true
            Log.e("prefConfig", "setLayout: ${prefConfig!!.weaTemp}")
//           if(swOnOff?.textOn?.equals(getString(R.string.f)) == true){
//               swOnOff?.textOn?.equals(getString(R.string.c))
//           }else if(swOnOff?.textOn?.equals(getString(R.string.c)) == true){
//               swOnOff?.textOn?.equals(getString(com.ziro.bullet.R.string.f))
//           }

            //change if true to false viceversa
            //save the changes in pref

            bindSwitch()
        }
        bindSwitch()
    }

    private fun bindSwitch() {
//        weatherAdaptertwo.setIsFer(weatherForecastResponse?.isfernite)
//        weatherAdapterOne.setIsFer(weatherForecastResponse.isfernite)
        if (!weatherForecastResponse?.isfernite!!) {
            val higc = weatherForecastResponse?.forecast?.forecastday?.get(0)?.day?.maxtemp_c
            val lowc = weatherForecastResponse?.forecast?.forecastday?.get(0)?.day?.mintemp_c
            tv_highlow?.text = "H:$higc°  L:$lowc°"
            val temc = weatherForecastResponse?.current?.temp_c?.toInt().toString()
            tv_tempcel?.text = "$temc"
            tv_temp_unit?.text = "\u2103"
        } else {
            val higf = weatherForecastResponse?.forecast?.forecastday?.get(0)?.day?.maxtemp_f
            val lowf = weatherForecastResponse?.forecast?.forecastday?.get(0)?.day?.mintemp_f
            tv_highlow?.text = "H:$higf°  L:$lowf°"
            val temf = weatherForecastResponse?.current?.temp_f?.toInt().toString()
            tv_tempcel?.text = "$temf"
            tv_temp_unit?.text = "\u2109"
        }
        weatherAdaptertwo.notifyDataSetChanged()
        weatherAdapterOne.notifyDataSetChanged()

    }

}
