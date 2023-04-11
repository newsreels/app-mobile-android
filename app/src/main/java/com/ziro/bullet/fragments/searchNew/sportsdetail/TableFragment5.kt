package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.model.discoverNew.tabletest.SportTablenew

class TableFragment5 : Fragment() {
    private var sportsTable: SportTablenew? = null
    lateinit var sportsTableAdapter: SportsTableAdapter

    private var rv_table: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sportsTable = it.getParcelable("sportsTable")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_table = view.findViewById(R.id.rv_table)
        init()
    }
    fun init() {

        if (!::sportsTableAdapter.isInitialized) {
            if(sportsTable?.LeagueTable?.L?.get(0)?.Tables?.get(0)?.team != null ){
                sportsTableAdapter = SportsTableAdapter(requireActivity(),
                    sportsTable?.LeagueTable?.L?.get(0)?.Tables?.get(0)?.team!!
                )
            }
        }

        if (rv_table?.adapter == null) {
            rv_table?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecorator = DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider_light
                )
            })

            rv_table?.addItemDecoration(itemDecorator)
            rv_table?.adapter = sportsTableAdapter
            rv_table?.layoutManager!!.scrollToPosition(0)
        }
    }
}