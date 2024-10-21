package tv.vizbee.movidletv.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.databinding.ActivityStartGameBinding

class StartGameActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityStartGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameStartText)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        TimerUtils.executeAfterDelay(5000) {
//            navigate(this, StartGameCountDownActivity::class.java)
//        }

        binding.root.setOnClickListener {
            navigate(this, WaitingForPlayersActivity::class.java)
        }
    }

    override fun onStartActivityAction(messageType: String, payload: JSONObject) {
        super.onStartActivityAction(messageType, payload)

        navigate(this, WaitingForPlayersActivity::class.java)
    }
}