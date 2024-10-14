package tv.vizbee.movidletv.ui

import android.os.Bundle
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.utils.TimerUtils

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        TimerUtils.executeAfterDelay(3000) {
            navigate(this, WelcomeActivity::class.java)
        }
    }
}