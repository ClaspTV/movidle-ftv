package tv.vizbee.movidletv.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.databinding.ActivityWelcomeBinding
import tv.vizbee.movidletv.utils.TimerUtils
import tv.vizbee.screen.api.Vizbee

class WelcomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityWelcomeBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Vizbee.getInstance().wasAppLaunchedByVizbee()) {
            navigateToStartGameActivity()
        }

        binding.root.setOnClickListener {
            navigateToStartGameActivity()
        }
    }

    private fun navigateToStartGameActivity() {
        navigate(this, StartGameActivity::class.java)
    }
}