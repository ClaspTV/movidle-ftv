package tv.vizbee.movidletv.vizbee

import android.util.Log
import org.json.JSONObject
import tv.vizbee.screen.api.session.model.device.VizbeeDevice
import tv.vizbee.screen.api.session.model.device.VizbeeDeviceType

object PlayerManager {
    val devices by lazy { arrayListOf<VizbeeDevice>() }
    val players = HashMap<String, Player>()

    fun addAll(members: List<VizbeeDevice>) {
        members.forEach {
            if (!devices.contains(it)) {
                addDevice(it)
            }
        }
    }

    fun addDevice(device: VizbeeDevice?) {
        Log.i("PlayerManager", "Add Device invoked. device = ${device}")
        device?.let { actualDevice ->
            if (actualDevice.deviceType == VizbeeDeviceType.ANDROID_MOBILE || actualDevice.deviceType == VizbeeDeviceType.IOS) {
                devices.find { it.deviceId == actualDevice.deviceId } ?: kotlin.run {
                    devices.add(actualDevice)
                    players.put(actualDevice.deviceId, Player(actualDevice.friendlyName, actualDevice.deviceId))
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
                it.username = name
            }
            players[userId] = it
        }
    }

    data class Player(var username: String, val userId: String, var score: String = "0")
}