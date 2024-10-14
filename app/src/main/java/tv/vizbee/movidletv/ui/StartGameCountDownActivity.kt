package tv.vizbee.movidletv.ui

import android.os.Bundle
import tv.vizbee.movidletv.databinding.ActivityStartGameCountDownBinding
import tv.vizbee.movidletv.utils.TimerUtils
import java.util.Locale
import java.util.concurrent.TimeUnit

class StartGameCountDownActivity : BaseActivity() {

    private lateinit var binding: ActivityStartGameCountDownBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartGameCountDownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCountdown()
    }

    private fun startCountdown() {
        val totalDuration = 1 * 20 * 1000L // 5 minutes in milliseconds

        TimerUtils.startCountdown(
            totalDurationMillis = totalDuration,
            onTick = { remainingMillis ->
                updateTimerText(remainingMillis)
            },
            onFinish = {
                binding.timerText.text = "00:00:00"
                // Add logic to start the game or navigate to the game activity
            }
        )
    }

    private fun updateTimerText(remainingMillis: Long) {
        val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60

        val timerText = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        binding.timerText.text = timerText
    }
}