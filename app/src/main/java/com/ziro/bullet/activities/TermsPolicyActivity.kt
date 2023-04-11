package com.ziro.bullet.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ziro.bullet.R
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.activity_post_settings.back
import kotlinx.android.synthetic.main.activity_terms_policy.*

class TermsPolicyActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBarColor(this)
        setContentView(R.layout.activity_terms_policy)
        setClickListeners()
    }

    private fun setClickListeners() {
        back.setOnClickListener(this)
        terms.setOnClickListener(this)
        policy.setOnClickListener(this)
        community.setOnClickListener(this)
        about.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.back -> {
                onBackPressed()
            }
            R.id.terms -> {
                Utils.openWebView(
                    this,
                    "https://www.newsinbullets.app/terms?header=false",
                    getString(R.string.terms_conditions)
                )
            }
            R.id.policy -> {
                Utils.openWebView(
                    this,
                    "https://www.newsinbullets.app/privacy?header=false",
                    getString(R.string.privacy_policy)
                )
            }
            R.id.community -> {
                Utils.openWebView(
                    this,
                    "https://www.newsinbullets.app/community-guidelines?header=false",
                    getString(R.string.community_guideline_)
                )
            }
            R.id.about -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
            }
        }
    }
}