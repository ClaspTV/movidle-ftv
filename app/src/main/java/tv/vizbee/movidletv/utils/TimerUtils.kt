package tv.vizbee.movidletv.utils

import android.os.Handler
import android.os.Looper

object TimerUtils {
    /**
     * Executes a callback after a specified delay.
     *
     * @param delayMillis The delay in milliseconds before the callback is invoked.
     * @param callback The function to be executed after the delay.
     */
    fun executeAfterDelay(delayMillis: Long, callback: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            callback.invoke()
        }, delayMillis)
    }

    /**
     * Starts a countdown timer and updates the UI every second.
     *
     * @param totalDurationMillis The total duration of the countdown in milliseconds.
     * @param onTick The function to be called every second with the remaining time.
     * @param onFinish The function to be called when the countdown finishes.
     */
    fun startCountdown(totalDurationMillis: Long, onTick: (Long) -> Unit, onFinish: () -> Unit) {
        fun updateCountdown(remainingMillis: Long) {
            onTick(remainingMillis)
            if (remainingMillis > 1000) {
                executeAfterDelay(1000) {
                    updateCountdown(remainingMillis - 1000)
                }
            } else {
                onFinish()
            }
        }
        updateCountdown(totalDurationMillis)
    }
}