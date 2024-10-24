package tv.vizbee.movidletv.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.json.JSONObject
import tv.vizbee.movidletv.vizbee.VizbeeXMessageListeners
import tv.vizbee.movidletv.vizbee.VizbeeXMessageType
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

abstract class BaseActivity : AppCompatActivity() {
    private val LOG_TAG = "BaseActivity"

    val startObserver = Observer<Pair<String, JSONObject>?> { pair ->
        pair?.let {
            VizbeeXMessageListeners.resetStartAction()

            Log.i(LOG_TAG, "Received startActivity event. class = ${this::class.java.simpleName}")
            onStartActivityAction(pair.first, pair.second)
        }
    }

    val deviceChangeObserver = Observer<VizbeeDevice?> { device ->
        device?.let {
            VizbeeXMessageListeners.resetDeviceAction()

            Log.i(LOG_TAG, "Received device change event. class = ${this::class.java.simpleName}")
            onDeviceChangeAction(device)
        }
    }

    val resetUIObserver = Observer<String?> { messageType ->
        messageType?.let {
            VizbeeXMessageListeners.resetResetUIAction()

            Log.i(LOG_TAG, "Received reset UI event.  class = ${this::class.java.simpleName}")
            onResetUIAction(messageType)
        }
    }

    val scoreUpdateObserver = Observer<JSONObject?> { payload ->
        payload?.let {
            VizbeeXMessageListeners.resetScoreUpdateAction()

            Log.i(LOG_TAG, "Received score update event. class = ${this::class.java.simpleName}")
            onScoreUpdate(payload)
        }
    }

    override fun onStart() {
        super.onStart()

        addObservers()
    }

    override fun onStop() {
        super.onStop()

        removeObservers()
    }

    private fun removeObservers() {
        VizbeeXMessageListeners.getStartActivityEvent().removeObservers(this)
    }

    private fun addObservers() {
        VizbeeXMessageListeners.getStartActivityEvent().observe(this, startObserver)
        VizbeeXMessageListeners.getDeviceChangeEvent().observe(this, deviceChangeObserver)
        VizbeeXMessageListeners.getResetUIEvent().observe(this, resetUIObserver)
        VizbeeXMessageListeners.getScoreUpdateEvent().observe(this, scoreUpdateObserver)
    }

    open fun onDeviceChangeAction(device: VizbeeDevice?) {
        // Child class will implement this if needed
    }

    open fun onStartActivityAction(messageType: String, payload: JSONObject) {
        // Child class will implement this if needed
    }

    private fun onResetUIAction(messageType: String) {
        if (messageType == VizbeeXMessageType.JOIN_GAME.value) {
            Intent(this, WaitingForPlayersActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.also {
                startActivity(it)
            }
        }
    }

    open fun onScoreUpdate(payload: JSONObject) {
        // Child class will implement this if needed
    }

    protected fun navigate(context: Context, activityTo: Class<*>, shouldFinish: Boolean = true) {
        val intent = Intent(context, activityTo)
        startActivity(intent)
        if (shouldFinish) {
            finish()
        }
    }
}