package tv.vizbee.movidletv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.data.model.AppState
import tv.vizbee.movidletv.databinding.FragmentPlayerConnectedBinding

class PlayerConnectedFragment : BaseFragment<FragmentPlayerConnectedBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerConnectedBinding {
        return FragmentPlayerConnectedBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
//                findNavController().navigate(R.id.action_to_exit)
                // In the above line we are trying to close the application using action
                // alternatively we can use requireActivity().finish(), which will close the current activity and finish
                // the graph that the activity contains
            }
        })

        appViewModel.appState.observe(viewLifecycleOwner) { state ->
            if (state is AppState.NotConnected) {
                // Navigate back to welcome screen
                findNavController().navigate(R.id.welcomeFragment)
            } else if (state is AppState.WaitingForPlayers) {
                val action =
                    PlayerConnectedFragmentDirections.actionPlayerConnectedFragmentToWaitingForPlayersFragment(state.channelId)
                findNavController().navigate(action)
            }
        }
    }
}