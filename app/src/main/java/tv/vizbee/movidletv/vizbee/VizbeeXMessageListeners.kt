package tv.vizbee.movidletv.vizbee

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

object VizbeeXMessageListeners {
    private const val LOG_TAG = "VizbeeXMessageListeners"

    // region Event update listeners with Live Data
    private val startActivityEvent = MutableLiveData<Pair<String, JSONObject>>()
    private val deviceChangeEvent = MutableLiveData<VizbeeDevice>()

    fun getStartActivityEvent(): LiveData<Pair<String, JSONObject>> {
        return startActivityEvent
    }

    fun triggerStartActivity(navigationFor: String, payload: JSONObject) {
        Log.i(LOG_TAG, "Triggering startActivity event")
        startActivityEvent.postValue(Pair(navigationFor, payload))
    }

    fun getDeviceChangeEvent(): LiveData<VizbeeDevice> {
        return deviceChangeEvent
    }

    fun triggerDeviceChange(device: VizbeeDevice) {
        Log.i(LOG_TAG, "Triggering device change event")
        deviceChangeEvent.postValue(device)
    }
    // endregion
}