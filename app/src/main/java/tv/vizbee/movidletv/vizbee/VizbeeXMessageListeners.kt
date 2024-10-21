package tv.vizbee.movidletv.vizbee

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

object VizbeeXMessageListeners {
    // region Event update listeners with Live Data
    private val startActivityEvent = MutableLiveData<Pair<String, JSONObject>>()
    private val deviceChangeEvent = MutableLiveData<VizbeeDevice>()

    fun getStartActivityEvent(): LiveData<Pair<String, JSONObject>> {
        return startActivityEvent
    }

    fun triggerStartActivity(navigationFor: String, payload: JSONObject) {
        startActivityEvent.value = Pair(navigationFor, payload)
    }

    fun getDeviceChangeEvent(): LiveData<VizbeeDevice> {
        return deviceChangeEvent
    }

    fun triggerDeviceChange(device: VizbeeDevice) {
        deviceChangeEvent.value = device
    }
    // endregion
}