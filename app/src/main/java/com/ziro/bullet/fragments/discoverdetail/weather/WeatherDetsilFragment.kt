package com.ziro.bullet.fragments.discoverdetail.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.fragments.discoverdetail.crypto.CryptoPresenter
import com.ziro.bullet.interfaces.GoHome
import com.ziro.bullet.model.discoverNew.weather.WeatherForecastResponse
import com.ziro.bullet.model.searchresultnew.SearchresultdataBase
import java.text.SimpleDateFormat
import java.util.*


class WeatherDetsilFragment : Fragment() {
    private lateinit var cryptoPresenter: CryptoPresenter
    var recyclerView1: RecyclerView? = null
    var recyclerView2: RecyclerView? = null
    var cardview: CardView? = null
    private var ll_no_results: LinearLayout? = null
    private var back_img: ImageView? = null
    var search_skeleton: LinearLayout? = null
    private var weatherForecastResponse: WeatherForecastResponse? = null
    var tv_search2: TextView? = null
    var tv_location: TextView? = null
    var tv_tempcel: TextView? = null
    var ts_temp_switch: SwitchCompat? = null
    var tv_status: TextView? = null
    var tv_highlow: TextView? = null
    lateinit var WeatherAdapterOne: WeatherAdapterOne
    lateinit var WeatherAdaptertwo: WeatherAdaptertwo
    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (goHome != null) goHome!!.scrollUp()
            if (childFragmentManager.backStackEntryCount > 0) {
                childFragmentManager.popBackStackImmediate()
                return
            }
            isEnabled = false

            requireActivity().onBackPressed()
        }
    }

    companion object {
        private var forexSeeAllFragment: WeatherDetsilFragment? = null

        lateinit var searchResultItem: SearchresultdataBase
        private var goHome: GoHome? = null
        private var isLoad: Boolean = false
        private var isLast: Boolean = false

        fun getInstance(bundle1: Bundle, goHome: GoHome): WeatherDetsilFragment {
            if (forexSeeAllFragment == null)
                forexSeeAllFragment = WeatherDetsilFragment()
            forexSeeAllFragment!!.arguments = bundle1
            this.goHome = goHome
            isLoad = false
            isLast = false
            return forexSeeAllFragment!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_weathernew, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView1 = view.findViewById(R.id.recycleview1)
        tv_location = view.findViewById(R.id.tv_location)
        tv_highlow = view.findViewById(R.id.tv_highlow)
        tv_status = view.findViewById(R.id.tv_status)
        tv_tempcel = view.findViewById(R.id.tv_tempcel)
        recyclerView2 = view.findViewById(R.id.recycleview2)
        cardview = view.findViewById(R.id.cardview)
        tv_search2 = view.findViewById(R.id.tv_search2)
        ll_no_results = view.findViewById(R.id.ll_no_results)
        back_img = view.findViewById(R.id.back_img)
        search_skeleton = view.findViewById(R.id.search_skeleton)
        ts_temp_switch = view.findViewById(R.id.ts_temp_switch)



        init()
    }


    override fun onResume() {
        super.onResume()
        if (goHome != null) goHome!!.scrollUp()

    }

    fun init() {
        //hide bottom nav
        if (goHome != null) goHome!!.scrollUp()

        cryptoPresenter = CryptoPresenter(requireActivity())

        var bundle1 = arguments
        weatherForecastResponse = bundle1?.getParcelable("weatherInfo")

        back_img!!.setOnClickListener { activity!!.onBackPressed() }

        if (!::WeatherAdapterOne.isInitialized) {
            WeatherAdapterOne = WeatherAdapterOne(requireActivity())
            cryptoPresenter = CryptoPresenter(requireActivity())
        }
        if (recyclerView1?.adapter == null) {
            recyclerView1?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            val itemDecorator = DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider_lightopacity
                )
            });
            recyclerView1?.addItemDecoration(itemDecorator)
            recyclerView1?.adapter = WeatherAdapterOne
            recyclerView1?.layoutManager!!.scrollToPosition(0)
        }


        if (!::WeatherAdaptertwo.isInitialized) {
            WeatherAdaptertwo = WeatherAdaptertwo(requireActivity())
            cryptoPresenter = CryptoPresenter(requireActivity())
        }
        if (recyclerView2?.adapter == null) {
            recyclerView2?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecorator = DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider_lightopacity
                )
            });
            recyclerView1?.addItemDecoration(itemDecorator)
            recyclerView2?.adapter = WeatherAdaptertwo
            recyclerView2?.layoutManager!!.scrollToPosition(0)
        }
        weatherForecastResponse?.forecast?.forecastday?.get(0)?.hour?.let {
            val time: String = SimpleDateFormat("hh a", Locale.getDefault()).format(
                Calendar.getInstance().time)
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
                WeatherAdapterOne.updateForecast(
                    subList
                )
            }
        }
        weatherForecastResponse?.forecast?.forecastday?.let { WeatherAdaptertwo.updateForecast(it) }

        tv_location?.text = weatherForecastResponse?.location?.name
        tv_status?.text = weatherForecastResponse?.current?.condition?.text.toString()
        ts_temp_switch?.setOnClickListener {
            weatherForecastResponse?.isfernite = weatherForecastResponse?.isfernite == false
            bindSwitch()
        }

        bindSwitch()
    }

    private fun bindSwitch() {
        if (!weatherForecastResponse?.isfernite!!) {
            val higc = weatherForecastResponse?.forecast?.forecastday?.get(0)?.day?.maxtemp_c
            val lowc = weatherForecastResponse?.forecast?.forecastday?.get(0)?.day?.mintemp_c
            tv_highlow?.text = "H:$higc°  L:$lowc°"
            val temc = weatherForecastResponse?.current?.temp_c?.toInt().toString()
            tv_tempcel?.text = "$temc\u2103"
        } else {
            val higf = weatherForecastResponse?.forecast?.forecastday?.get(0)?.day?.maxtemp_f
            val lowf = weatherForecastResponse?.forecast?.forecastday?.get(0)?.day?.mintemp_f
            tv_highlow?.text = "H:$higf°  L:$lowf°"
            val temf = weatherForecastResponse?.current?.temp_f?.toInt().toString()
            tv_tempcel?.text = "$temf\u2109"
        }

    }


}
