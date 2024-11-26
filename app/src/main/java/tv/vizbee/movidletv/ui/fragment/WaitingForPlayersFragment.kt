package tv.vizbee.movidletv.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import tv.vizbee.movidletv.data.model.AppState
import tv.vizbee.movidletv.databinding.FragmentWaitingForPlayersBinding
import tv.vizbee.movidletv.ui.adapter.WaitingForPlayersRecyclerAdapter
import tv.vizbee.movidletv.ui.custom.CustomItemDecoration
import tv.vizbee.movidletv.vizbee.PlayerManager

class WaitingForPlayersFragment : BaseFragment<FragmentWaitingForPlayersBinding>() {
    private val playersAdapter by lazy { WaitingForPlayersRecyclerAdapter() }
    private val args: WaitingForPlayersFragmentArgs by navArgs()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWaitingForPlayersBinding {
        return FragmentWaitingForPlayersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        binding.titleText.text = args.channelId.split("-")[1]

        appViewModel.appState.observe(viewLifecycleOwner) { state ->
            if (state is AppState.GameStarted) {
                navigateToTheGameControllerScreen()
            }
        }

        playerViewModel.playerList.observe(viewLifecycleOwner) { players ->
            playersAdapter.updateAll(players)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (playersAdapter.getAll().isEmpty()) {
                    findNavController().navigateUp()
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.waitingForPlayersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.waitingForPlayersRecyclerView.addItemDecoration(CustomItemDecoration())
        binding.waitingForPlayersRecyclerView.adapter = this.playersAdapter

        // Add existing players to the adapter
        PlayerManager._players.values.forEach {
            playersAdapter.addPlayer(it)
        }
    }

    private fun navigateToTheGameControllerScreen() {
        Log.i(LOG_TAG, "Starting the game")
        val action =
            WaitingForPlayersFragmentDirections.actionWaitingForPlayersFragmentToGamePlayControllerFragment(0, 0, false)
        findNavController().navigate(action)
    }

    companion object {
        private const val LOG_TAG = "WaitingForPlayersActivity"
    }
}