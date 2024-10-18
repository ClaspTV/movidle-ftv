package tv.vizbee.movidletv.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.adapter.ScoresRecyclerAdapter
import tv.vizbee.movidletv.databinding.ActivityGameScoreBinding

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

        binding.scoresRecyclerView.apply {
            adapter = ScoresRecyclerAdapter(getScores())
        }
    }

    private fun getScores(): ArrayList<String> {
        return arrayListOf("250", "240", "230", "220", "210", "200", "190")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigate(this, StartGameActivity::class.java)
        finish()
    }
}