package tv.vizbee.movidletv.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.databinding.ActivityGameStatusBinding
import tv.vizbee.movidletv.model.VideoStorage

class GameStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityGameStatusBinding
    private var contentPosition: Int = 0
    private var clipPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            if (clipPosition < 5) {
                binding.gameStatusTitleText.text = "Movie 1 - Clip $clipPosition Ended"
                binding.gameStatusDescriptionText.text = "Guess the movie name on your mobile"
                Handler(Looper.getMainLooper()).postDelayed({
                    playVideo()
                }, 3000)
            } else {
                navigate(this, GameScoreActivity::class.java)
            }
        }
    }

    private fun playVideo() {
        VideoStorage.getMovie(contentPosition)?.get(clipPosition)?.let { videoUrl ->
            clipPosition++
            Intent(this, PlayerActivity::class.java).apply {
                putExtra("videoUrl", videoUrl)
            }.also { startActivity(it) }
        } ?: kotlin.run {

        }
    }

    private fun getContentPosition(): Int {
        return intent.getIntExtra("contentPosition", 0)
    }

    private fun getClipPosition(): Int {
        return intent.getIntExtra("clipPosition", 0)
    }
}