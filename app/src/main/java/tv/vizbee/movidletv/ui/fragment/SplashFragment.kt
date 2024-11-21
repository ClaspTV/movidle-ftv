package tv.vizbee.movidletv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.databinding.FragmentSplashBinding
import tv.vizbee.movidletv.utils.TimerUtils

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start animating the logo
        val bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
        binding.appLogo.startAnimation(bounceAnimation)

        // Navigate to the welcome screen after the specified splash time
        TimerUtils.executeAfterDelay(SPLASH_TIME) {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        }
    }

    companion object {
        private const val SPLASH_TIME = 4000L
    }
} 