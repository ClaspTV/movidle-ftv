package tv.vizbee.movidletv.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.data.model.AppState
import tv.vizbee.movidletv.data.model.VideoStorage
import tv.vizbee.movidletv.databinding.FragmentScoresBinding
import tv.vizbee.movidletv.ui.adapter.ScoresRecyclerAdapter
import tv.vizbee.movidletv.ui.custom.CustomItemDecoration
import tv.vizbee.movidletv.vizbee.PlayerManager
import tv.vizbee.movidletv.vizbee.VizbeeWrapper

class ScoresFragment : BaseFragment<FragmentScoresBinding>() {
    private val args: ScoresFragmentArgs by navArgs()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentScoresBinding {
        return FragmentScoresBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup
        if (args.forMovie) {
            binding.gameScoreTitle.text = "Movie ${args.contentPosition + 1} Completed"
        } else {
            binding.gameScoreTitle.text = "Movie ${args.contentPosition + 1} - Clip ${args.clipPosition} Completed"
        }
        binding.scoresRecyclerView.addItemDecoration(CustomItemDecoration())

        // Listeners
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.i("ScoresFrament", "onBackPressed")
                VizbeeWrapper.clearVizbeeX()

                findNavController().navigateUp()
            }
        })

        Log.i(LOG_TAG, "Current Players with score: ${PlayerManager._players}")
        binding.scoresRecyclerView.apply {
            val finalPlayers = ArrayList(PlayerManager._players.values)
            finalPlayers.sortByDescending { it.score.toInt() }
            adapter = ScoresRecyclerAdapter(finalPlayers)
        }

        lifecycleScope.launch {
            if (args.forMovie) {
                delay(30000)
            } else {
                delay(10000)
            }

            if (VideoStorage.getMovie(args.contentPosition + 1) != null ||
                VideoStorage.getMovieClip(args.contentPosition, args.clipPosition) != null
            ) {
                Log.i(LOG_TAG, "trying to go to game play controller after 30 sec")
//                findNavController().navigateUp()
                findNavController().navigate(
                    ScoresFragmentDirections.actionScoreFragmentToGamePlayControllerFragment(
                        args.contentPosition,
                        args.clipPosition, true
                    )
                )
            } else {
                Log.i(LOG_TAG, "Game completed")
                binding.gameScoreTitle.text = "Game Completed"
            }
        }

        appViewModel.appState.observe(viewLifecycleOwner) { state ->
            if (state == AppState.WaitingForPlayers) {
                findNavController().navigate(R.id.waitingForPlayersFragment)
            }
        }

        scoreViewModel.scores.observe(viewLifecycleOwner) { scores ->
            Log.i("ScoresFragment", "Updated players with score: ${PlayerManager._players}")
            binding.scoresRecyclerView.apply {
                try {
                    val finalPlayers = ArrayList(PlayerManager._players.values)
                    finalPlayers.sortByDescending { it.score.toInt() }
                    adapter = ScoresRecyclerAdapter(finalPlayers)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

//    override fun onStartActivityAction(messageType: String, payload: JSONObject) {
//        super.onStartActivityAction(messageType, payload)
//
//        if (messageType == "join_game") {
//            val action = WaitingForPlayersFragmentDirections.actionWaitingForPlayersFragmentToGamePlayControllerFragment(0, 0)
//            findNavController().navigate(action)
//
//            findNavController().popBackStack()
////            findNavController().navigateUp()
//        }
//    }

    companion object {
        private const val LOG_TAG = "ScoresFragment"
    }
}