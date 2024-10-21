package tv.vizbee.movidletv.vizbee

import org.json.JSONObject
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

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

    fun addDevice(player: VizbeeDevice?) {
        player?.let { actualPlayer ->
            devices.find { it.deviceId == actualPlayer.deviceId } ?: {
                devices.add(actualPlayer)
                players.put(actualPlayer.deviceId, Player(actualPlayer.friendlyName, actualPlayer.deviceId))
            }
        }
    }

    fun removeDevice(player: VizbeeDevice?) {
        player?.let { actualPlayer ->
            devices.find { it.deviceId == actualPlayer.deviceId }?.let {
                devices.remove(it)
                players.remove(actualPlayer.deviceId)
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