package tv.vizbee.movidletv.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.adapter.ScoresRecyclerAdapter
import tv.vizbee.movidletv.databinding.ActivityGameScoreBinding
import tv.vizbee.movidletv.model.VideoStorage
import tv.vizbee.movidletv.vizbee.PlayerManager

class GameScoreActivity : BaseActivity() {
    private lateinit var binding: ActivityGameScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val moviePosition = intent.getIntExtra("contentPosition", 0)
        binding.gameScoreTitle.text = "Movie ${moviePosition + 1} Completed"

        binding.scoresRecyclerView.apply {
            val finalPlayers = ArrayList(PlayerManager.players.values)
            finalPlayers.sortByDescending { it.score }
            adapter = ScoresRecyclerAdapter(finalPlayers)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (VideoStorage.getMovie(intent.getIntExtra("contentPosition", 0) + 1) != null) {
                finish()
            } else {
                binding.gameScoreTitle.text = "All Movies Completed"
            }
        }, 30000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigate(this, StartGameActivity::class.java)
        finish()
    }
}