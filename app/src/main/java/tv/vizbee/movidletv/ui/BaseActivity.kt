package tv.vizbee.movidletv.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import tv.vizbee.movidletv.vizbee.VizbeeXMessageListeners
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

abstract class BaseActivity : AppCompatActivity() {
    private val LOG_TAG = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VizbeeXMessageListeners.getStartActivityEvent().observe(this) { pair ->
            Log.i(LOG_TAG, "Received startActivity event")
            onStartActivityAction(pair.first, pair.second)
        }

        VizbeeXMessageListeners.getDeviceChangeEvent().observe(this) { device ->
            Log.i(LOG_TAG, "Received device change event")
            onDeviceChangeAction(device)
        }
    }

    open fun onDeviceChangeAction(device: VizbeeDevice?) {
        // Child class will implement this if needed
    }

    open fun onStartActivityAction(messageType: String, payload: JSONObject) {
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