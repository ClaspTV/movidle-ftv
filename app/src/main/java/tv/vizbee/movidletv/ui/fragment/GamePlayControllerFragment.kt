package tv.vizbee.movidletv.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import tv.vizbee.movidletv.databinding.FragmentGamePlayControllerBinding
import tv.vizbee.movidletv.data.model.VideoStorage
import tv.vizbee.movidletv.vizbee.VizbeeXMessageParameter
import tv.vizbee.movidletv.vizbee.VizbeeXMessageType
import tv.vizbee.movidletv.vizbee.VizbeeXWrapper

class GamePlayControllerFragment : BaseFragment<FragmentGamePlayControllerBinding>() {
    private var contentPosition: Int = 0
    private var clipPosition: Int = 0
    private var areScoresShown: Boolean= false
    private val args: GamePlayControllerFragmentArgs by navArgs()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentGamePlayControllerBinding {
        return FragmentGamePlayControllerBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOG_TAG, "OnCreate invoked")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(LOG_TAG, "OnCreateView invoked")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(LOG_TAG, "onAttach invoked")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOG_TAG, "OnViewCreated invoked")

        contentPosition = args.contentPosition
        clipPosition = args.clipPosition
        areScoresShown = args.areScoresShown
        if (areScoresShown) {
            if (VideoStorage.getMovieClip(contentPosition, clipPosition) == null) {
                contentPosition++
                clipPosition = 0
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(LOG_TAG, "OnResume invoked")

        if (clipPosition == 0) {
            playVideo()
        } else if (areScoresShown) {
            playVideo()
        } else {
            val clipSize = VideoStorage.getMovie(contentPosition)?.clips?.size ?: 0
            if (clipSize != 0) {
                sendGameStatus("clip_ended", clipPosition - 1)
                binding.gameStatusTitleText.text = "Movie ${contentPosition + 1} - Clip $clipPosition Ended"
                binding.gameStatusDescriptionText.text = "Guess the movie name on your mobile"
                lifecycleScope.launch {
                    delay(30000)
                    if (clipPosition == clipSize) {
                        sendGameStatus("movie_completed", clipPosition - 1)
                        navigateToGameScoreActivity()
                    } else {
                        navigateToGameScoreActivity(forMovie = false)
                    }
                }
            }
        }
    }

    private fun navigateToGameScoreActivity(forMovie: Boolean = true) {
        val action = GamePlayControllerFragmentDirections.actionGamePlayControllerFragmentToScoresFragnent(
            contentPosition,
            clipPosition,
            forMovie
        )
        contentPosition++
        clipPosition = 0
//        if (VideoStorage.getMovie(contentPosition) == null) {
//            findNavController().popBackStack()
//        }
        findNavController().navigate(action)
    }

    private fun sendGameStatus(status: String, clipPosition: Int = this.clipPosition) {
        val currentMovie = VideoStorage.getMovieClip(contentPosition, clipPosition)
        VizbeeXWrapper.sendMessageWithBiCast(JSONObject().apply {
            put(VizbeeXMessageParameter.MESSAGE_TYPE.value, VizbeeXMessageType.GAME_STATUS.value)
            put(VizbeeXMessageParameter.STATUS.value, status)
            put(VizbeeXMessageParameter.MOVIE_NAME.value, VideoStorage.getMovie(contentPosition)?.name ?: "")
            put(VizbeeXMessageParameter.MOVIE_NUMBER.value, contentPosition + 1)
            put(VizbeeXMessageParameter.TOTAL_MOVIES.value, VideoStorage.movieList.size)
            if (status != "movie_completed") {
                put(VizbeeXMessageParameter.CLIP_ID.value, currentMovie?.id ?: "")
                put(VizbeeXMessageParameter.CLIP_SCORE.value, currentMovie?.score ?: "0")
                put(VizbeeXMessageParameter.CLIP_NUMBER.value, clipPosition + 1)
                put(
                    VizbeeXMessageParameter.TOTAL_CLIPS.value,
                    VideoStorage.getMovie(contentPosition)?.clips?.size ?: 0
                )
            }
        })
    }

    private fun playVideo() {
        VideoStorage.getMovieClip(contentPosition, clipPosition)?.url?.let { videoUrl ->
            areScoresShown = false
            sendGameStatus("clip_started")

            clipPosition++
            val action = GamePlayControllerFragmentDirections.actionGamePlayControllerFragmentToPlayerActivity(videoUrl)
            findNavController().navigate(action)

//        } ?: kotlin.run {
//            contentPosition++
//            navigate(this, GameScoreActivity::class.java)
//            VideoStorage.getMovie(contentPosition)?.let {
//                playVideo()
//            } ?: kotlin.run {
//                // No Videos or clips to play
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, "onDestroy invoked")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(LOG_TAG, "onDetach invoked")
    }

    override fun onPause() {
        super.onPause()
        Log.i(LOG_TAG, "onPause invoked")
    }

    override fun onStop() {
        super.onStop()
        Log.i(LOG_TAG, "onStop invoked")
    }

    companion object {
        private const val LOG_TAG = "GamePlayControllerFragment"
    }
}