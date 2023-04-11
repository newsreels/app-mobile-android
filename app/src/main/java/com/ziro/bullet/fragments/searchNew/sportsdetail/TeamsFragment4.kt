package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.model.discoverNew.sportsteam.SportTeam

class TeamsFragment4() : Fragment() {
    private var sportsTeam: SportTeam? = null
    lateinit var sportsTeamAdapter: SportsteamAdapter
    var revteam1: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sportsTeam = it.getParcelable("sportsTeam")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        revteam1 = view.findViewById(R.id.rv_team1)
        init()
    }

    fun init() {

        if (!::sportsTeamAdapter.isInitialized) {
            if(sportsTeam?.Lu?.get(0)?.Ps != null && sportsTeam?.Lu?.get(1)?.Ps != null ){
                sportsTeamAdapter = SportsteamAdapter(requireActivity(),sportsTeam?.Lu?.get(0)?.Ps!! ,sportsTeam?.Lu?.get(1)?.Ps!!)
            }
        }

        if (revteam1?.adapter == null) {
            revteam1?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecorator = DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider_light
                )
            })

            revteam1?.addItemDecoration(itemDecorator)
            revteam1?.adapter = sportsTeamAdapter
            revteam1?.layoutManager!!.scrollToPosition(0)
        }
    }
}


