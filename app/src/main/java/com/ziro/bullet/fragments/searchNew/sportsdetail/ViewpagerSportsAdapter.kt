package com.ziro.bullet.fragments.searchNew.sportsdetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.facebook.login.LoginFragment


class ViewpagerSportsAdapter(
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = SportsInfoFragment1()
            1 -> fragment = ScorecardFragment2()
            2 -> fragment = SummaryFragment3()
            3 -> fragment = TeamsFragment4()
            4 -> fragment = TableFragment5()

        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 5
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        when (position) {
            0 -> title = "Info"
            1 -> title = "Scorecard"
            2 -> title = "Summary"
            3 -> title = "Teams"
            4 -> title = "Table"
        }
        return title
    }
}
