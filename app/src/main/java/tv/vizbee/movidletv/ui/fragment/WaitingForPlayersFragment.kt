package tv.vizbee.movidletv.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import tv.vizbee.movidletv.data.model.AppState
import tv.vizbee.movidletv.databinding.FragmentWaitingForPlayersBinding
import tv.vizbee.movidletv.ui.adapter.WaitingForPlayersRecyclerAdapter
import tv.vizbee.movidletv.vizbee.PlayerManager

class WaitingForPlayersFragment : BaseFragment<FragmentWaitingForPlayersBinding>() {
    private val playersAdapter by lazy { WaitingForPlayersRecyclerAdapter() }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWaitingForPlayersBinding {
        return FragmentWaitingForPlayersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        appViewModel.appState.observe(viewLifecycleOwner) { state ->
            if (state is AppState.GameStarted) {
                navigateToTheGameControllerScreen()
            }
        }

        playerViewModel.playerList.observe(viewLifecycleOwner) { players ->
            playersAdapter.updateAll(players)
        }
    }

    private fun setupRecyclerView() {
        binding.waitingForPlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WaitingForPlayersFragment.playersAdapter
        }

        // Add existing players to the adapter
        PlayerManager._players.values.forEach {
            playersAdapter.addPlayer(it)
        }
    }

    private fun navigateToTheGameControllerScreen() {
        Log.i(LOG_TAG, "Starting the game")
        val action = WaitingForPlayersFragmentDirections.actionWaitingForPlayersFragmentToGamePlayControllerFragment(0, 0)
        findNavController().navigate(action)
    }

//    override fun onDeviceChangeAction(device: VizbeeDevice?) {
//        super.onDeviceChangeAction(device)
//
//        Log.i("WaitingForPlayersActivity", "Device change event: Current Players ${PlayerManager.players.values}")
//        PlayerManager.players.values.find { it.userId == device?.deviceId }?.let { player ->
//            playersAdapter.addPlayer(player)
//        } ?: kotlin.run {
//            playersAdapter.remove(device?.deviceId)
//        }
//
//        val adapterPlayers = playersAdapter.getAll()
//        PlayerManager.players.values.forEach { player ->
//            adapterPlayers.find { it.userId == player.userId }?.let { player ->
//                // Do Nothing
//            } ?: kotlin.run {
//                playersAdapter.addPlayer(player)
//            }
//        }
//    }

    companion object {
        private const val LOG_TAG = "WaitingForPlayersActivity"
    }
}