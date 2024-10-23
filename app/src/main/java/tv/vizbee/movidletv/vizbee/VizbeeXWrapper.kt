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

    private var connectedBroadcastChannel: String = ""

    // region Vizbee X - BiCast
    fun connectVizbeeXBiCast() {
        Log.i(LOG_TAG, "Connecting VizbeeX with namespace = tv.vizbee.movidle")

        // Initialise X SDK
        vizbeeX.connect(VizbeeXConnectionType.BICAST, "tv.vizbee.movidle") { event, eventInfo ->
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

                    device?.let { VizbeeXMessageListeners.triggerDeviceChange(it) }
                    if (connectedBroadcastChannel.isNotEmpty()) {
                        sendMessageWithBiCast(JSONObject().apply {
                            put(VizbeeXMessageParameter.MESSAGE_TYPE.value, VizbeeXMessageType.JOIN_GAME.value)
                            put(VizbeeXMessageParameter.CHANNEL_ID.value, connectedBroadcastChannel)
                        })
                    }
                }

                VizbeeXConnectionEvent.DEVICE_DISCONNECTED -> {
                    // Handle device disconnected
                    // Remove the device and update the UI with device removal
                    val device = eventInfo.device
                    Log.i(LOG_TAG, "Device disconnected: ${device?.friendlyName}")

                    device?.let { VizbeeXMessageListeners.triggerDeviceChange(it) }
                }
            }
        }
    }

    fun sendMessageWithBiCast(payload: JSONObject) {
        Log.i(LOG_TAG, "send message with bicast is invoked. payload = $payload")

        sendMessage(vizbeeX, payload)
    }

    private fun receiveMessageWithBiCast() {
        vizbeeX.receive { message ->
            val payload = message.payload
            val sender = message.sender

            // payload values
            val messageType = payload.optString(VizbeeXMessageParameter.MESSAGE_TYPE.value)
            Log.i(LOG_TAG, "BiCast: Received message with payload = $payload")

            // Process the received message
            when (messageType) {
                VizbeeXMessageType.JOIN_GAME.value -> {
                    val channelId = payload.optString(VizbeeXMessageParameter.CHANNEL_ID.value)
                    // @ToDo: Review the logic
                    if (connectedBroadcastChannel != channelId) {
                        // 1. Join the broadcast channel
                        connectVizbeeXBroadcast(channelId) {
                            // When a user or player joins the game after connecting to TV, send a broadcast that the user
                            // has joined
                            sendUserJoined(payload)
                        }

                        // 2. Start the waiting screen
                        VizbeeXMessageListeners.triggerStartActivity(messageType, payload)
                    } else {
                        sendUserJoined(payload)
                    }

                    PlayerManager.addPlayer(
                        sender,
                        payload.optString(VizbeeXMessageParameter.USER_ID.value),
                        payload.optString(VizbeeXMessageParameter.USER_NAME.value)
                    )
                }
            }
        }
    }

    private fun sendUserJoined(payload: JSONObject) {
        sendMessageWithBroadcast(JSONObject().apply {
            put(VizbeeXMessageParameter.MESSAGE_TYPE.value, VizbeeXMessageType.USER_JOINED.value)
            put(
                VizbeeXMessageParameter.USER_NAME.value,
                payload.optString(VizbeeXMessageParameter.USER_NAME.value)
            )
            put(
                VizbeeXMessageParameter.USER_ID.value,
                payload.optString(VizbeeXMessageParameter.USER_ID.value)
            )
        })
    }
    // endregion

    // region Vizbee X - Broadcast
    private fun connectVizbeeXBroadcast(channelId: String, callback: () -> Unit) {
        Log.i(LOG_TAG, "Joining channel with channelId = $channelId")

        PlayerManager.clear()
        connectedBroadcastChannel = channelId
        // Join the broadcast channel
        channelVizbeeX.connect(VizbeeXConnectionType.BROADCAST, channelId) { event, eventInfo ->
            when (event) {
                VizbeeXConnectionEvent.READY -> {
                    Log.i(LOG_TAG, "channelVizbeeX is ready")
                    receiveMessagesWithBroadcast()
                    callback.invoke()
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
                    Log.i(LOG_TAG, "channelVizbeeX: Device connected: ${device?.friendlyName}")

                    saveDevice(device)
                }

                VizbeeXConnectionEvent.DEVICE_DISCONNECTED -> {
                    // Handle device disconnected
                    // Remove the device and update the UI with device removal
                    val device = eventInfo.device
                    Log.i(LOG_TAG, "channelVizbeeX: Device disconnected: ${device?.friendlyName}")

                    removeDevice(device)
                }

                else -> { /* Handle other events */
                }
            }
        }

        Log.i(LOG_TAG, "Current members = ${channelVizbeeX.members}")
    }

    private fun receiveMessagesWithBroadcast() {
        channelVizbeeX.receive { message ->
            val payload = message.payload
            val sender = message.sender

            // payload values
            val messageType = payload.optString(VizbeeXMessageParameter.MESSAGE_TYPE.value)
            Log.i(LOG_TAG, "Broadcast: Received message with payload = $payload")

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
                    PlayerManager.updateScore(payload)
//                    VizbeeXMessageListeners.triggerStartActivity(messageType, payload)
                }

                VizbeeXMessageType.USER_JOINED.value -> {
                    // Share the current TV players
                    sendMessageWithBroadcast(JSONObject().apply {
                        put(VizbeeXMessageParameter.MESSAGE_TYPE.value, VizbeeXMessageType.CURRENT_USERS.value)
                        put(VizbeeXMessageParameter.USERS.value, JSONArray().apply {
                            for (player in PlayerManager.players.values) {
                                put(player.getJsonWithoutScore())
                            }
                        })
                    })
                }

                VizbeeXMessageType.CURRENT_USERS.value -> {
                    // Received the current users
                    Gson().fromJson(
                        payload.optString(VizbeeXMessageParameter.USERS.value),
                        Array<PlayerManager.Player>::class.java
                    ).forEach {
                        PlayerManager.players[it.userId] = it
                    }
                }
            }
        }
    }

    private fun sendMessageWithBroadcast(payload: JSONObject) {
        Log.i(LOG_TAG, "send message with broadcast is invoked. payload = $payload")

        sendMessage(channelVizbeeX, payload)
    }

    private fun sendMessage(vizbeeX: VizbeeX, payload: JSONObject) {
        val message = VizbeeXMessage.create(payload)

        vizbeeX.send(message, object : ICommandCallback<Unit> {
            override fun onSuccess(success: Unit?) {
                Log.i(LOG_TAG, "Message sent successfully")
            }

            override fun onFailure(error: VizbeeError?) {
                Log.i(LOG_TAG, "Failed to send message: ${error?.message}")
            }
        })
    }
    // endregion

    // region Helper methods
    private fun saveDevice(device: VizbeeDevice?) {
        // Save the connected device and update the UI with device joining
        Log.i(LOG_TAG, "Save device is invoked")
        PlayerManager.addDevice(device)
        device?.let { VizbeeXMessageListeners.triggerDeviceChange(it) }
    }

    private fun removeDevice(device: VizbeeDevice?) {
        // Remove the disconnected device and update the UI with device removal
        Log.i(LOG_TAG, "Remove device is invoked")
        PlayerManager.removeDevice(device)
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