package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziro.bullet.R
import com.ziro.bullet.fragments.DividerItemDecorator
import com.ziro.bullet.model.discoverNew.scorecard.ScorecardResponse

class ScorecardFragment2 : Fragment(),ScorecardHeadInterface {

    var bundle = Bundle()
    private var date: String? = null
    private var pos: Int = 0
    private var scorecard: ScorecardResponse? = null
    private var tv_data: TextView? = null
    private var rv_scorecard: RecyclerView? = null
    private var rv_scorecard2: RecyclerView? = null
    private var rv_head: RecyclerView? = null
    lateinit var sportsScorecardAdapter: SportsScorecardAdapter
    lateinit var scorecardHeadAdapter: ScorecardHeadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scorecard = it.getParcelable("scorecard")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        bundle = this.arguments!!;
//        date = bundle.getString("DATE");
        return inflater.inflate(R.layout.fragment_scorecard, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        for(player in scorecard?.prns!!){
//            sportsScorecardAdapter.updateprns()
//           playerFullName( player.pid,player)
//        }


        init()
    }
    private fun rvScoreBat() {

        if (!::sportsScorecardAdapter.isInitialized) {
            if (scorecard?.sDInn?.get(0)?.bat != null) {
                sportsScorecardAdapter = SportsScorecardAdapter(
                    requireActivity(),
                )
            }
            //else add no data screen
        }

        rv_scorecard?.setHasFixedSize(true)
        rv_scorecard?.isNestedScrollingEnabled = false

        if (rv_scorecard?.adapter == null) {
            rv_scorecard?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecorator = DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider_light
                )
            })


            rv_scorecard?.addItemDecoration(itemDecorator)
            rv_scorecard?.adapter = sportsScorecardAdapter
            rv_scorecard?.layoutManager!!.scrollToPosition(0)




            //updating the data in adapter
            sportsScorecardAdapter.updatedate(  scorecard?.sDInn?.get(pos)?.bat!!, scorecard!!.prns)

        }
    }
    private fun rvScoreBat2() {

        if (!::sportsScorecardAdapter.isInitialized) {
            if (scorecard?.sDInn?.get(0)?.bat != null) {
                sportsScorecardAdapter = SportsScorecardAdapter(
                    requireActivity(),
                )
            }
            //else add no data screen
        }
        rv_scorecard2?.setHasFixedSize(true)
        rv_scorecard2?.isNestedScrollingEnabled = false

        if (rv_scorecard2?.adapter == null) {
            rv_scorecard2?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecorator = DividerItemDecorator(context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.divider_light
                )
            })

            rv_scorecard2?.addItemDecoration(itemDecorator)
            rv_scorecard2?.adapter = sportsScorecardAdapter
            rv_scorecard2?.layoutManager!!.scrollToPosition(0)
            //updating the data in adapter



            sportsScorecardAdapter.updatedate(  scorecard?.sDInn?.get(pos)?.bat!!, scorecard!!.prns)

        }
    }

    override fun getScorecard(position: Int) {
        Log.e("testtab", "getScorecard:$position ")

        pos = position
        sportsScorecardAdapter.updatedate(scorecard?.sDInn?.get(position)?.bat!!, scorecard!!.prns)
        sportsScorecardAdapter.notifyDataSetChanged()
    }

    private fun rvHead() {

        if (!::scorecardHeadAdapter.isInitialized) {
            if (scorecard?.sDInn?.get(0)?.bat != null) {
                scorecardHeadAdapter = ScorecardHeadAdapter(requireActivity(), scorecard?.sDInn!!,this)
            }
            //else add no data screen
        }

        if (rv_head?.adapter == null) {
            rv_head?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rv_head?.adapter = scorecardHeadAdapter
            rv_head?.layoutManager!!.scrollToPosition(0)
        }

//        scorecardHeadAdapter.updateInterface(this)
    }

    fun init() {
        rv_scorecard = view?.findViewById(R.id.rv_scorecard)
        rv_scorecard2 = view?.findViewById(R.id.rv_scorecard2)
        rv_head = view?.findViewById(R.id.rv_head)
        rvHead()
        rvScoreBat()
        rvScoreBat2()
    }


}