package tv.vizbee.movidletv.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import tv.vizbee.movidletv.vizbee.VizbeeWrapper
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VizbeeWrapper.getStartActivityEvent().observe(this) { pair ->
            onStartActivityAction(pair.first, pair.second)
        }

        VizbeeWrapper.getDeviceChangeEvent().observe(this) { device ->
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