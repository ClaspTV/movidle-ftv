package tv.vizbee.movidletv.vizbee

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import tv.vizbee.movidletv.model.movie.MovieItem
import tv.vizbee.screen.api.Vizbee
import tv.vizbee.screen.api.adapter.VizbeeAppAdapter
import tv.vizbee.screen.api.session.model.device.VizbeeDevice
import tv.vizbee.screen.x.api.VizbeeX
import tv.vizbee.screen.x.api.model.event.VizbeeXConnectionEvent
import tv.vizbee.screen.x.api.model.message.VizbeeXMessage
import tv.vizbee.screen.x.api.model.options.VizbeeXConnectionType
import tv.vizbee.utils.ICommandCallback
import tv.vizbee.utils.VizbeeError


/**
 * #VizbeeGuide Do not modify this file.
 *
 * This class is an entry point to the Vizbee integration. It has a utility method to initialise the
 * Vizbee SDKs: the Continuity SDK and the HomeSSO SDK. It has methods to know if Vizbee is enabled
 * by the app and some easy to access adapter objects via extension methods.
 */
object VizbeeWrapper {
    private const val LOG_TAG = "VizbeeTVWrapper"
    private var isVizbeeEnabled: Boolean = false
    val vizbeeX by lazy { VizbeeX() }
    val channelVizbeeX by lazy { VizbeeX() }
    val devices by lazy { arrayListOf<VizbeeDevice>() }
    val movies by lazy { arrayListOf<MovieItem>() }

    // region class methods
    //------
    // Initialisation
    //------
    /**
     * Initializes Vizbee SDK with the app ID assigned for your app.
     * @param app Application
     */
    fun initialize(app: Application) {

        isVizbeeEnabled = isVizbeeEnabled(app)
        if (!isVizbeeEnabled) {
            Log.i(
                "VizbeeWrapper",
                "Vizbee is not enabled. Not initialising. Define your_app_vizbee_app_id for app to enable Vizbee."
            )
            return
        }

        // Initialise Vizbee Continuity SDK
        Vizbee.getInstance().enableVerboseLogging()
        Vizbee.getInstance().initialize(app, getVizbeeAppId(app), VizbeeAppAdapter())

        connectVizbeeX()
        joinChannel("Test Channel")
    }

    //------
    // Helpers
    //------

    /**
     * This method returns true if Vizbee is enabled by the app. Returning false will disable all the Vizbee integration.
     *
     * @return true if Vizbee should be enabled in the app integration.
     */
    private fun isVizbeeEnabled(context: Context): Boolean {
        val vizbeeAppId = getVizbeeAppId(context)
        return vizbeeAppId.isNotEmpty()
    }

    /**
     * @return returns the vizbee_app_id defined the resources.
     */
    private fun getVizbeeAppId(context: Context): String {
        val identifier = context.resources.getIdentifier(
            "vizbee_app_id",
            "string",
            context.packageName
        )
        if (identifier == 0) {
            return ""
        }
        val appId = context.getString(identifier)
        Log.i(LOG_TAG, appId)
        return appId
    }

    // endregion

    // region Vizbee X
    private fun connectVizbeeX() {
        Log.i(LOG_TAG, "Connecting VizbeeX with namespace = tv.vizbee.movidletv")

        // Initialise X SDK
        vizbeeX.connect(VizbeeXConnectionType.BICAST, "tv.vizbee.movidletv") { event, eventInfo ->
            when (event) {
                VizbeeXConnectionEvent.READY -> {
                    Log.i(LOG_TAG, "VizbeeX is ready")
                    receiveMessage()
                }

                VizbeeXConnectionEvent.NOT_READY -> {
                    Log.i(LOG_TAG, "VizbeeX is not ready")
                }

                VizbeeXConnectionEvent.ERROR -> {
                    // Handle error state
                    val error = eventInfo.error
                    Log.i(LOG_TAG, "Error occurred = ${error?.message}")
                }

                VizbeeXConnectionEvent.DEVICE_CONNECTED -> {
                    // Handle device connected
                    // Save the device and update the UI with device joining
                    val device = eventInfo.device
                    Log.i(LOG_TAG, "Device connected: ${device?.friendlyName}")

                    saveDevice(device)
                }

                VizbeeXConnectionEvent.DEVICE_DISCONNECTED -> {
                    // Handle device disconnected
                    // Remove the device and update the UI with device removal
                    val device = eventInfo.device
                    Log.i(LOG_TAG, "Device disconnected: ${device?.friendlyName}")

                    removeDevice(device)
                }
            }
        }
        devices.addAll(vizbeeX.members)
    }

    //val payload = JSONObject().apply {
    //    put("action", "productView")
    //    put("productId", "12345")
    //    put("timestamp", System.currentTimeMillis())
    //}
    fun sendMessage(payload: JSONObject) {
        val message = VizbeeXMessage.create(payload)
        vizbeeX.send(message, object : ICommandCallback<Unit> {
            override fun onSuccess(success: Unit?) {
                println("Message sent successfully")
            }

            override fun onFailure(error: VizbeeError?) {
                println("Failed to send message: ${error?.message}")
            }
        })
    }

    fun receiveMessage() {
        vizbeeX.receive { message ->
            val payload = message.payload
            val sender = message.sender

            // payload values
            val messageType = payload.optString(VizbeeXMessageParameter.MESSAGE_TYPE.value)

            // Process the received message
            when (messageType) {
                VizbeeXMessageType.CREATE_GAME.value, VizbeeXMessageType.JOIN_GAME.value -> {
                    // 1. Join the broadcast channel
                    joinChannel(payload.optString(VizbeeXMessageParameter.CHANNEL_ID.value))

                    // 2. Start the waiting screen
                    triggerStartActivity(messageType, payload)
                }

                VizbeeXMessageType.GAME_STATUS.value -> {
                    // Received Game Status update
                    // Do Nothing as the status is expected to send from the receiver to connected devices
                }
            }
        }
    }

    private fun joinChannel(channelId: String) {
        Log.i(LOG_TAG, "Joining channel with channelId = $channelId")
        // Join the broadcast channel
        channelVizbeeX.connect(VizbeeXConnectionType.BROADCAST, channelId) { event, eventInfo ->
            when (event) {
                VizbeeXConnectionEvent.READY -> {
                    Log.i(LOG_TAG, "channelVizbeeX is ready")
                    setupChannelMessageHandling()
//                    startGameplay()
                }

                VizbeeXConnectionEvent.NOT_READY -> {
                    Log.i(LOG_TAG, "channelVizbeeX is not ready")
                }

                VizbeeXConnectionEvent.ERROR -> {
                    val error = eventInfo.error
                    Log.i(LOG_TAG, "channelVizbeeX: Error occurred = ${error?.message}")
                }

                VizbeeXConnectionEvent.DEVICE_CONNECTED -> {
                    // Handle device connected
                    // Save the device and update the UI with device joining
                    val device = eventInfo.device
                    Log.i(LOG_TAG, "Device connected: ${device?.friendlyName}")

                    saveDevice(device)
                }

                VizbeeXConnectionEvent.DEVICE_DISCONNECTED -> {
                    // Handle device disconnected
                    // Remove the device and update the UI with device removal
                    val device = eventInfo.device
                    Log.i(LOG_TAG, "Device disconnected: ${device?.friendlyName}")

                    removeDevice(device)
                }

                else -> { /* Handle other events */
                }
            }
        }
        channelVizbeeX.members.forEach {
            if (!devices.contains(it)) {
                saveDevice(it)
            }
        }
    }

    private fun setupChannelMessageHandling() {
        channelVizbeeX.receive { message ->
            val payload = message.payload
            val sender = message.sender

            // payload values
            val messageType = payload.optString(VizbeeXMessageParameter.MESSAGE_TYPE.value)
            when (messageType) {
                VizbeeXMessageType.START_GAME.value -> {
                    // 1. Save the Movies Data
                    payload.optJSONArray(VizbeeXMessageParameter.MOVIES.value)?.let {
                        movies.addAll(jsonArrayToArrayList(it))
                    }

                    // 2. Start the game
                    triggerStartActivity(messageType, payload)
                }

                VizbeeXMessageType.SCORE_UPDATE.value -> {
                    // Received the score update
                    // Save the score and update the UI
                    triggerStartActivity(messageType, payload)
                }
            }
        }
    }

    private fun saveDevice(device: VizbeeDevice?) {
        // Save the connected device
        device?.let { actualDevice ->
            devices.find { it.deviceId == actualDevice.deviceId } ?: {
                devices.add(actualDevice)
                triggerDeviceChange(actualDevice)
            }
        }
    }

    private fun removeDevice(device: VizbeeDevice?) {
        // Remove the disconnected device
        device?.let {
            devices.remove(device)
            triggerDeviceChange(device)
        }
    }

    fun jsonArrayToArrayList(jsonArray: JSONArray): ArrayList<MovieItem> {
        val gson = Gson()
        val listType = object : TypeToken<List<MovieItem>>() {}.type
        return gson.fromJson(jsonArray.toString(), listType)
    }
    // endregion

    // region LiveData
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

    fun disconnect() {
        vizbeeX.disconnect()
        channelVizbeeX.disconnect()
    }
}