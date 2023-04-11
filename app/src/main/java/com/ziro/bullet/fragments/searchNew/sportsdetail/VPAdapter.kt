package com.ziro.bullet.fragments.searchNew.sportsdetail

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class VPAdapter(
    fm: FragmentManager,
    behaviorResumeOnlyCurrentFragment: Int
) : FragmentPagerAdapter(fm) {

    private val mFragmentList: ArrayList<Fragment> = ArrayList()
    private val mFragmentListnew: ArrayList<Fragment> = ArrayList()
    private val mFragmentTitleList: ArrayList<String> = ArrayList()

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        Log.e("mFragmentList", "getItem: $mFragmentList")
        return mFragmentList[position]
    }

    fun addFrag(fragment: Fragment, title: String?) {
        if (fragment != null) {
            mFragmentList.add(fragment)
        }
        Log.e("mFragmentList1", "getItem: $mFragmentList")
        if (title != null) {
            mFragmentTitleList.add(title)
        }
//
//        for (F in mFragmentList) {
//            when (F) {
//                is SportsInfoFragment1 ->{
//                    mFragmentList.add(0, F)
//                }
////                ScorecardFragment2() -> mFragmentListnew.add(1, F)
////                SummaryFragment3() -> mFragmentListnew.add(2, F)
//                is TeamsFragment4 -> {
//                    mFragmentList.add(1, F)
//                }
////                TableFragment5() -> mFragmentListnew.add(4, F)
//            }
//        }


//        for ((i, F) in mFragmentList.withIndex()) {
//
//            when (F) {
//                is SportsInfoFragment1 -> {
//                    mFragmentList.add((mFragmentList.size - (i+mFragmentList.size)), F)
//                }
////                ScorecardFragment2() -> mFragmentListnew.add(1, F)
////                SummaryFragment3() -> mFragmentListnew.add(2, F)
//                is TeamsFragment4 -> {
//                    mFragmentList.add(1, F)
//                }
////                TableFragment5() -> mFragmentListnew.add(4, F)
//            }
//        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
}