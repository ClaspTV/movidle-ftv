package tv.vizbee.movidletv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.data.model.AppState
import tv.vizbee.movidletv.databinding.FragmentWelcomeBinding
import tv.vizbee.screen.api.Vizbee

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWelcomeBinding {
        return FragmentWelcomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Vizbee.getInstance().wasAppLaunchedByVizbee()) {
            navigateToPlayerConnectedScreen()
        } else {
            appViewModel.appState.observe(viewLifecycleOwner) { state ->
                if (state == AppState.Connected) {
                    navigateToPlayerConnectedScreen()
                }
            }
        }
    }

    private fun navigateToPlayerConnectedScreen() {
        findNavController().navigate(R.id.action_welcomeFragment_to_playerConnectedFragemnt)
    }
}