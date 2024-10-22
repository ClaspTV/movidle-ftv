package tv.vizbee.movidletv.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.databinding.ActivityGameStatusBinding
import tv.vizbee.movidletv.model.VideoStorage
import tv.vizbee.movidletv.vizbee.VizbeeXMessageParameter
import tv.vizbee.movidletv.vizbee.VizbeeXMessageType
import tv.vizbee.movidletv.vizbee.VizbeeXWrapper

class GameStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityGameStatusBinding
    private var contentPosition: Int = 0
    private var clipPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentPosition = getContentPosition()
        clipPosition = getClipPosition()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()

        if (clipPosition == 0) {
            playVideo()
        } else {
            val clipSize = VideoStorage.getMovie(contentPosition)?.clips?.size ?: 0
            if (clipSize != 0) {
                sendGameStatus("clip_ended", clipPosition - 1)
                binding.gameStatusTitleText.text = "Movie 1 - Clip $clipPosition Ended"
                binding.gameStatusDescriptionText.text = "Guess the movie name on your mobile"
                Handler(Looper.getMainLooper()).postDelayed({
                    if (clipPosition == clipSize) {
                        sendGameStatus("movie_completed", clipPosition - 1)
                        navigateToGameScoreActivity()
                    } else {
                        playVideo()
                    }
                }, 30000)
            }
        }
    }

    private fun navigateToGameScoreActivity() {
        Intent(this, GameScoreActivity::class.java).apply {
            putExtra("contentPosition", contentPosition)
            putExtra("clipPosition", clipPosition)
        }.also {
            contentPosition++
            clipPosition = 0
            startActivity(it)
        }
        if (VideoStorage.getMovie(contentPosition) == null) {
            finish()
        }
    }

    private fun sendGameStatus(status: String, clipPosition: Int = this.clipPosition) {
        val currentMovie = VideoStorage.getMovieClip(contentPosition, clipPosition)
        VizbeeXWrapper.sendMessageWithBiCast(JSONObject().apply {
            put(VizbeeXMessageParameter.MESSAGE_TYPE.value, VizbeeXMessageType.GAME_STATUS.value)
            put(VizbeeXMessageParameter.STATUS.value, status)
            put(VizbeeXMessageParameter.MOVIE_NAME.value, VideoStorage.getMovie(contentPosition)?.name ?: "")
            if (status != "movie_completed") {
                put(VizbeeXMessageParameter.CLIP_ID.value, currentMovie?.id ?: "")
                put(VizbeeXMessageParameter.CLIP_SCORE.value, currentMovie?.score ?: "0")
                put(VizbeeXMessageParameter.CLIP_NUMBER.value, "$clipPosition")
                put(VizbeeXMessageParameter.TOTAL_CLIPS.value, VideoStorage.getMovie(contentPosition)?.clips?.size ?: 0)
            }
        })
    }

    private fun playVideo() {
        VideoStorage.getMovieClip(contentPosition, clipPosition)?.url?.let { videoUrl ->
            sendGameStatus("clip_started")

            clipPosition++
            Intent(this, PlayerActivity::class.java).apply {
                putExtra("videoUrl", videoUrl)
            }.also {
                startActivity(it)
            }
//        } ?: kotlin.run {
//            contentPosition++
//            navigate(this, GameScoreActivity::class.java)
//            VideoStorage.getMovie(contentPosition)?.let {
//                playVideo()
//            } ?: kotlin.run {
//                // No Videos or clips to play
//
//            }
        }
    }

    private fun getContentPosition(): Int {
        return intent.getIntExtra("contentPosition", 0)
    }

    private fun getClipPosition(): Int {
        return intent.getIntExtra("clipPosition", 0)
    }
}