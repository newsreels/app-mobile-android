package com.newsreels.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.newsreels.app.R
import com.newsreels.app.data.PrefConfig
import com.newsreels.app.utills.Utils
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_post_settings.back
import kotlinx.android.synthetic.main.hashtag.*

class AccSettingActivity : BaseActivity(), View.OnClickListener {

    private var mPrefConfig: PrefConfig? = null
    private val TYPE_EMAIL = "change_email"
    private val TYPE_PASSWORD = "change_pwd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBarColor(this)
        setContentView(R.layout.activity_account_setting)
        init()
        setClickListeners()
        setData()
    }

    private fun init() {
        mPrefConfig = PrefConfig(this)
    }

    private fun setData() {
//        hash.text = mPrefConfig?.defaultLanguage?.name
    }

    private fun setClickListeners() {
        back.setOnClickListener(this)
        change_email.setOnClickListener(this)
        change_password.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.back -> {
                onBackPressed()
            }
            R.id.change_email -> {
                var intent = Intent(this, ChangeAccountInfoActivity::class.java)
                intent.putExtra("type", TYPE_EMAIL)
                startActivity(intent)
            }
            R.id.change_password -> {
                var intent = Intent(this, ChangeAccountInfoActivity::class.java)
                intent.putExtra("type", TYPE_PASSWORD)
                startActivity(intent)
            }
        }
    }
}