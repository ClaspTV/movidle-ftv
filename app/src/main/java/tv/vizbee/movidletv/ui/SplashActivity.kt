package tv.vizbee.movidletv.ui

import android.os.Bundle
import android.view.animation.AnimationUtils
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        TimerUtils.executeAfterDelay(3000) {
//            navigate(this, WelcomeActivity::class.java)
//        }

        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        binding.appLogo.startAnimation(bounceAnimation)

        binding.appLogo.postDelayed({
            navigate(this, WelcomeActivity::class.java)
        }, 4000)  // Delay of 2 seconds for the splash screen
    }
}