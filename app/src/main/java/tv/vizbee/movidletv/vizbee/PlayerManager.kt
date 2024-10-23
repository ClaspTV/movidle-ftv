package tv.vizbee.movidletv.vizbee

import android.util.Log
import org.json.JSONObject
import tv.vizbee.screen.api.session.model.device.VizbeeDevice
import tv.vizbee.screen.api.session.model.device.VizbeeDeviceType

object PlayerManager {
    private val devices by lazy { arrayListOf<VizbeeDevice>() }
    val players by lazy { HashMap<String, Player>() }

    fun addDevice(device: VizbeeDevice?) {
        Log.i("PlayerManager", "Add Device invoked. device = ${device}")
        device?.let { actualDevice ->
            if (actualDevice.deviceType == VizbeeDeviceType.ANDROID_MOBILE || actualDevice.deviceType == VizbeeDeviceType.IOS) {
                devices.find { it.deviceId == actualDevice.deviceId } ?: kotlin.run {
                    devices.add(actualDevice)
//                    players.put(actualDevice.deviceId, Player(actualDevice.friendlyName, actualDevice.deviceId))
                }
            }
        }
    }

    fun removeDevice(device: VizbeeDevice?) {
        Log.i("PlayerManager", "Remove Device invoked. device = ${device}")
        device?.let { actualDevice ->
            devices.find { it.deviceId == actualDevice.deviceId }?.let {
                devices.remove(it)
                players.remove(actualDevice.deviceId)
            }
        }
    }

    fun updateScore(payload: JSONObject) {
        val userId = payload.optString(VizbeeXMessageParameter.USER_ID.value)
        players[userId]?.let {
            it.score = payload.optString(VizbeeXMessageParameter.SCORE.value)
            payload.optString(VizbeeXMessageParameter.USER_NAME.value)?.let { name ->
                it.userName = name
            }
            players[userId] = it
        }
    }

    fun addPlayer(device: VizbeeDevice?, userId: String, userName: String) {
        Log.i("PlayerManager", "addPlayer invoked. username = $userName, userId = $userId, device = $device")

        device?.let { actualDevice ->
//            if (actualDevice.deviceType == VizbeeDeviceType.ANDROID_MOBILE || actualDevice.deviceType == VizbeeDeviceType.IOS) {
            players[userId] = Player(userName, userId)
            Log.i("PlayerManager", "players = ${players.values}")
//            }
        }
    }

    fun clear() {
        devices.clear()
        players.clear()
    }

    data class Player(var userName: String, val userId: String, var score: String = "0") {
        fun getJson(): JSONObject {
            return JSONObject().apply {
                put(VizbeeXMessageParameter.USER_ID.value, userId)
                put(VizbeeXMessageParameter.USER_NAME.value, userName)
                put(VizbeeXMessageParameter.SCORE.value, score)
            }
        }

        fun getJsonWithoutScore(): JSONObject {
            return JSONObject().apply {
                put(VizbeeXMessageParameter.USER_ID.value, userId)
                put(VizbeeXMessageParameter.USER_NAME.value, userName)
            }
        }
    }
}