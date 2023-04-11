package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ziro.bullet.R
import com.ziro.bullet.model.discoverNew.liveScore.SportEvent
import com.ziro.bullet.model.discoverNew.spotsinfo.SportsInfoResponse
import kotlinx.android.synthetic.main.fragment_sports_info.*

class SportsInfoFragment1() : Fragment() {
    private var date: String? = null
    private var sportsInfo: SportsInfoResponse? = null
    private var sportsEventSelected: SportEvent? = null
    var tvEventInfo: TextView? = null
    var llevent: LinearLayout? = null
    private var tvThismatchInfo: TextView? = null
    private var tvTossInfo: TextView? = null
    private var tvVenueInfo: TextView? = null
    private var tvCountryInfo: TextView? = null
    private var tvCityInfo: TextView? = null
    private var tvUmp1Info: TextView? = null
    private var tvUmp2Info: TextView? = null
    private var tvTvUmpInfo: TextView? = null
    private var tvRefInfo: TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            date = it.getString("DATE")
            sportsInfo = it.getParcelable("scoreInfo")
            sportsEventSelected = it.getParcelable("sportsEventSelected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sports_info, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        bindData()
    }


    fun init() {
        tvEventInfo = view?.findViewById(R.id.tv_event_info)
        llevent = view?.findViewById(R.id.llevent)
        tvThismatchInfo = view?.findViewById(R.id.tv_thismatch_info)
        tvTossInfo = view?.findViewById(R.id.tv_toss_info)
        tvCountryInfo = view?.findViewById(R.id.tv_country_info)
        tvCityInfo = view?.findViewById(R.id.tv_city_info)
        tvVenueInfo = view?.findViewById(R.id.tv_venue_info)
        tvUmp1Info = view?.findViewById(R.id.tv_ump1_info)
        tvUmp2Info = view?.findViewById(R.id.tv_ump2_info)
        tvTvUmpInfo = view?.findViewById(R.id.tv_tvump_info)
        tvRefInfo = view?.findViewById(R.id.tv_ref_info)
    }

    private fun bindData() {
        if (sportsInfo != null) {

            for (ref in sportsInfo?.refs!!) {
                when (ref.kn) {
                    6 -> {
                        if(ref.nm != null){
                            ll_ump1.visibility = View.VISIBLE
                            v_venue.visibility = View.VISIBLE
                            tvUmp1Info?.text = ref.nm
                        }
                    }
                    7 ->{
                        if(ref.nm != null){
                            ll_ump2.visibility = View.VISIBLE
                            v_ump1.visibility = View.VISIBLE
                            tvUmp2Info?.text = ref.nm
                        }
                    }
                    5 -> {
                        if (ref.nm != null) {
                            ll_tvump.visibility = View.VISIBLE
                            v_ump2.visibility = View.VISIBLE
                            tvTvUmpInfo?.text = ref.nm
                        }
                    }
                    0 ->{
                        if (ref.nm != null) {
                            ll_ref.visibility = View.VISIBLE
                            v_tvump.visibility = View.VISIBLE
                            tvRefInfo?.text = ref.nm
                        }
                    }
                }
            }

            if (sportsEventSelected?.Stg?.Snm != null && sportsEventSelected?.Stg?.Cnm != null) {
                llevent?.visibility = View.VISIBLE
                v_matchinfo?.visibility = View.VISIBLE
                tvEventInfo?.text =
                    "${sportsEventSelected?.Stg?.Snm} ${sportsEventSelected?.Stg?.Cnm}"
            }
            if (sportsEventSelected?.ErnInf != null) {
                llmatch.visibility = View.VISIBLE
                v_event.visibility = View.VISIBLE
                tvThismatchInfo?.text = sportsEventSelected?.ErnInf.toString()
            }
            if(sportsInfo?.vCnm != null){
                ll_country.visibility = View.VISIBLE
                v_toss.visibility = View.VISIBLE
                tvCountryInfo?.text = sportsInfo?.vCnm.toString()
            }
            if(sportsInfo?.vcy != null){
                ll_city.visibility = View.VISIBLE
                v_country.visibility = View.VISIBLE
                tvCityInfo?.text = sportsInfo?.vcy.toString()
            }


            if(sportsInfo?.vnm != null){
                ll_venue.visibility = View.VISIBLE
                v_city.visibility = View.VISIBLE
                tvVenueInfo?.text = sportsInfo?.vnm.toString()
            }

        }

    }
}

