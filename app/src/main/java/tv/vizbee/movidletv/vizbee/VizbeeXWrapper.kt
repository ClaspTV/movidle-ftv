package tv.vizbee.movidletv.vizbee

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import tv.vizbee.movidletv.model.movie.MovieItem
import tv.vizbee.screen.api.session.model.device.VizbeeDevice
import tv.vizbee.screen.x.api.VizbeeX
import tv.vizbee.screen.x.api.model.event.VizbeeXConnectionEvent
import tv.vizbee.screen.x.api.model.message.VizbeeXMessage
import tv.vizbee.screen.x.api.model.options.VizbeeXConnectionType
import tv.vizbee.utils.ICommandCallback
import tv.vizbee.utils.VizbeeError

object VizbeeXWrapper {
    private const val LOG_TAG = "VizbeeXWrapper"

    val vizbeeX by lazy { VizbeeX() }
    val channelVizbeeX by lazy { VizbeeX() }
    val movies by lazy { arrayListOf<MovieItem>() }

    // region Vizbee X - BiCast
    fun connectVizbeeXBiCast() {
        Log.i(LOG_TAG, "Connecting VizbeeX with namespace = tv.vizbee.movidletv")

        // Initialise X SDK
        vizbeeX.connect(VizbeeXConnectionType.BICAST, "tv.vizbee.movidletv") { event, eventInfo ->
            when (event) {
                VizbeeXConnectionEvent.READY -> {
                    Log.i(LOG_TAG, "VizbeeX is ready")
                    receiveMessageWithBiCast()
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
        PlayerManager.addAll(vizbeeX.members)
    }

    fun sendMessageWithBiCast(payload: JSONObject) {
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

    private fun receiveMessageWithBiCast() {
        vizbeeX.receive { message ->
            val payload = message.payload
            val sender = message.sender

            // payload values
            val messageType = payload.optString(VizbeeXMessageParameter.MESSAGE_TYPE.value)

            // Process the received message
            when (messageType) {
                VizbeeXMessageType.CREATE_GAME.value, VizbeeXMessageType.JOIN_GAME.value -> {
                    // 1. Join the broadcast channel
                    connectVizbeeXBroadcast(payload.optString(VizbeeXMessageParameter.CHANNEL_ID.value))

                    // 2. Start the waiting screen
                    VizbeeXMessageListeners.triggerStartActivity(messageType, payload)
                }

                VizbeeXMessageType.GAME_STATUS.value -> {
                    // Received Game Status update
                    // Do Nothing as the status is expected to send from the receiver to connected devices
                }
            }
        }
    }
    // endregion

    // region Vizbee X - Broadcast
    private fun connectVizbeeXBroadcast(channelId: String) {
        Log.i(LOG_TAG, "Joining channel with channelId = $channelId")

        // Join the broadcast channel
        channelVizbeeX.connect(VizbeeXConnectionType.BROADCAST, channelId) { event, eventInfo ->
            when (event) {
                VizbeeXConnectionEvent.READY -> {
                    Log.i(LOG_TAG, "channelVizbeeX is ready")
                    receiveMessagesWithBroadcast()
                    // startGameplay()
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
        PlayerManager.addAll(channelVizbeeX.members)
    }

    private fun receiveMessagesWithBroadcast() {
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
                    VizbeeXMessageListeners.triggerStartActivity(messageType, payload)
                }

                VizbeeXMessageType.SCORE_UPDATE.value -> {
                    // Received the score update
                    // Save the score and update the UI
                    VizbeeXMessageListeners.triggerStartActivity(messageType, payload)
                }
            }
        }
    }
    // endregion

    // region Helper methods
    private fun saveDevice(device: VizbeeDevice?) {
        // Save the connected device and update the UI with device joining
        PlayerManager.addPlayer(device)
        device?.let { VizbeeXMessageListeners.triggerDeviceChange(it) }
    }

    private fun removeDevice(device: VizbeeDevice?) {
        // Remove the disconnected device and update the UI with device removal
        PlayerManager.removePlayer(device)
        device?.let { VizbeeXMessageListeners.triggerDeviceChange(device) }
    }

    private fun jsonArrayToArrayList(jsonArray: JSONArray): ArrayList<MovieItem> {
        val gson = Gson()
        val listType = object : TypeToken<List<MovieItem>>() {}.type
        return gson.fromJson(jsonArray.toString(), listType)
    }
    // endregion

    fun disconnect() {
        vizbeeX.disconnect()
        channelVizbeeX.disconnect()
    }
}