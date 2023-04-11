package com.ziro.bullet.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ziro.bullet.R
import com.ziro.bullet.utills.Utils
import kotlinx.android.synthetic.main.activity_help_feeedback.*
import kotlinx.android.synthetic.main.activity_post_settings.back

class HelpFeedbackActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBarColor(this)
        setContentView(R.layout.activity_help_feeedback)
        setClickListeners()
    }

    private fun setClickListeners() {
        back.setOnClickListener(this)
        help_center.setOnClickListener(this)
        feedback_suggestion.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.back -> {
                onBackPressed()
            }
            R.id.help_center -> {
                startActivity(Intent(this, HelpActivity::class.java))
            }
            R.id.feedback_suggestion -> {
                startActivity(Intent(this, SuggestionActivity::class.java))
            }
        }
    }
}