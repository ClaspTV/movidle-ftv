package tv.vizbee.movidletv.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.adapter.ScoresRecyclerAdapter
import tv.vizbee.movidletv.databinding.ActivityGameScoreBinding
import tv.vizbee.movidletv.model.VideoStorage
import tv.vizbee.movidletv.vizbee.PlayerManager
import tv.vizbee.movidletv.vizbee.VizbeeWrapper
import tv.vizbee.movidletv.vizbee.VizbeeXWrapper
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

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

        Log.i("GameScoreActivity", "Current Players with score: ${PlayerManager.players}")
        binding.scoresRecyclerView.apply {
            val finalPlayers = ArrayList(PlayerManager.players.values)
            finalPlayers.sortByDescending { it.score.toInt() }
            adapter = ScoresRecyclerAdapter(finalPlayers)
        }

        lifecycleScope.launch {
            delay(30000)

            if (VideoStorage.getMovie(intent.getIntExtra("contentPosition", 0) + 1) != null) {
                finish()
            } else {
                binding.gameScoreTitle.text = "Game Completed"
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        VizbeeWrapper.clearVizbeeX()

        navigate(this, StartGameActivity::class.java)
        finish()
    }

    override fun onStartActivityAction(messageType: String, payload: JSONObject) {
        super.onStartActivityAction(messageType, payload)

        if (messageType == "join_game") {
            navigate(this, WaitingForPlayersActivity::class.java)
            finish()
        }
    }

    override fun onScoreUpdate(payload: JSONObject) {

        Log.i("GameScoreActivity", "Updated players with score: ${PlayerManager.players}")
        binding.scoresRecyclerView.apply {
            try {
                val finalPlayers = ArrayList(PlayerManager.players.values)
                finalPlayers.sortByDescending { it.score.toInt() }
                adapter = ScoresRecyclerAdapter(finalPlayers)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}