package com.ziro.bullet.activities.logs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ziro.bullet.R
import com.ziro.bullet.data.PrefConfig
import com.ziro.bullet.interfaces.VideoInterface
import com.ziro.bullet.model.Reel.ReelResponse
import com.ziro.bullet.presenter.ReelPresenter
import kotlinx.android.synthetic.main.activity_debug_logs.*

class DebugLogsActivity : AppCompatActivity(), VideoInterface {

    private var reelPresenter: ReelPresenter? = null
    private var prefConfig: PrefConfig? = null
    private val stringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug_logs)

        ivBack.setOnClickListener(View.OnClickListener { onBackPressed() })
        reelPresenter = ReelPresenter(this, this)
        prefConfig = PrefConfig(this)

        tv_token.text = "Token>> ${prefConfig?.firebaseToken}"
        tv_user_id.text = "User Id>> ${prefConfig?.userConfig?.user?.id}"

        reelPresenter?.getReelsHome(prefConfig?.reelsType, "", "", false, true, false, "")

    }

    override fun loaderShow(flag: Boolean) {

    }

    override fun error(error: String?) {

    }

    override fun error404(error: String?) {

    }

    override fun success(reelResponse: ReelResponse?, reload: Boolean) {
        val reelsList = reelResponse?.reels
        reelsList?.forEach {
//            stringBuilder.append("${it.source.name} \n")
            stringBuilder.append("${it.debug} \n")
            stringBuilder.append("\n================================ \n\n")
        }

        tv_logs.text = stringBuilder.toString()

//        while (!reelResponse?.meta?.next.isNullOrEmpty()){
        if (!this.isFinishing && reelResponse?.meta?.next!!.isNotEmpty())
            reelPresenter?.getReelsHome(
                prefConfig?.reelsType,
                "",
                reelResponse.meta?.next,
                false,
                true,
                true,
                ""
            )
//        }

    }

    override fun nextVideo(position: Int) {

    }

}