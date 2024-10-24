package tv.vizbee.movidletv.vizbee

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

object VizbeeXMessageListeners {
    private const val LOG_TAG = "VizbeeXMessageListeners"

    // region Event update listeners with Live Data
    private val startActivityEvent = MutableLiveData<Pair<String, JSONObject>?>()
    private val deviceChangeEvent = MutableLiveData<VizbeeDevice?>()
    private val resetUIEvent = MutableLiveData<String?>()
    private val scoreUpdateEvent = MutableLiveData<JSONObject?>()

    fun getStartActivityEvent(): LiveData<Pair<String, JSONObject>?> {
        return startActivityEvent
    }

    fun triggerStartActivity(navigationFor: String, payload: JSONObject) {
        Log.i(LOG_TAG, "Triggering startActivity event")
        startActivityEvent.postValue(Pair(navigationFor, payload))
    }

    fun getDeviceChangeEvent(): LiveData<VizbeeDevice?> {
        return deviceChangeEvent
    }

    fun triggerDeviceChange(device: VizbeeDevice?) {
        Log.i(LOG_TAG, "Triggering device change event")
        deviceChangeEvent.postValue(device)
    }

    fun getResetUIEvent(): LiveData<String?> {
        return resetUIEvent
    }

    fun triggerResetUIEvent(messageType: String) {
        Log.i(LOG_TAG, "Triggering reset UI event")
        resetUIEvent.postValue(messageType)
    }

    fun getScoreUpdateEvent(): LiveData<JSONObject?> {
        return scoreUpdateEvent
    }

    fun triggerScoreUpdateEvent(payload: JSONObject) {
        Log.i(LOG_TAG, "Triggering score update event")
        scoreUpdateEvent.postValue(payload)
    }

    fun resetStartAction() {
        startActivityEvent.value = null
    }

    fun resetDeviceAction() {
        deviceChangeEvent.value = null
    }

    fun resetResetUIAction() {
        resetUIEvent.value = null
    }

    fun resetScoreUpdateAction() {
        scoreUpdateEvent.value = null
    }
    // endregion
}