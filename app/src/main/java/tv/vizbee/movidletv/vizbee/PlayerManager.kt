package tv.vizbee.movidletv.vizbee

import android.util.Log
import org.json.JSONObject
import tv.vizbee.movidletv.data.model.Player
import tv.vizbee.movidletv.ui.viewmodel.PlayerViewModel
import tv.vizbee.movidletv.ui.viewmodel.ScoreViewModel
import tv.vizbee.screen.api.session.model.device.VizbeeDevice
import tv.vizbee.screen.api.session.model.device.VizbeeDeviceType

object PlayerManager {
    const val LOG_TAG = "PlayerManager"

    private var playerViewModel: PlayerViewModel? = null
    private var scoreViewModel: ScoreViewModel? = null

    private val devices by lazy { arrayListOf<VizbeeDevice>() }
    val _players by lazy { HashMap<String, Player>() }

    fun getPlayers(playerViewModel: PlayerViewModel) {
        this.playerViewModel = playerViewModel
        this.playerViewModel?.updatePlayers(ArrayList(this._players.values))
    }

    fun getScores(scoreViewModel: ScoreViewModel) {
        this.scoreViewModel = scoreViewModel
        this.scoreViewModel?.updateScores(this._players)
    }

    fun addDevice(device: VizbeeDevice?) {
        Log.i(LOG_TAG, "Add Device invoked. device = ${device}")
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
                _players.remove(actualDevice.deviceId)
                this.playerViewModel?.removePlayer(actualDevice.deviceId)
            }
        }
    }

    fun updateScore(payload: JSONObject) {
        val userId = payload.optString(VizbeeXMessageParameter.USER_ID.value)
        _players[userId]?.let {
            it.score = payload.optString(VizbeeXMessageParameter.SCORE.value)
            payload.optString(VizbeeXMessageParameter.USER_NAME.value)?.let { name ->
                it.userName = name
            }
            _players[userId] = it
            this.scoreViewModel?.updateScore(userId, it.toString())
        }
    }

    fun addPlayer(device: VizbeeDevice?, userId: String, userName: String, userAvatar: String) {
        Log.i("PlayerManager", "addPlayer invoked. username = $userName, userId = $userId, device = $device")

        device?.let { actualDevice ->
//            if (actualDevice.deviceType == VizbeeDeviceType.ANDROID_MOBILE || actualDevice.deviceType == VizbeeDeviceType.IOS) {
            val player = Player(userName, userId, userAvatar = userAvatar)
            _players[userId] = player
            this.playerViewModel?.addPlayer(player)
            Log.i("PlayerManager", "players = ${_players.values}")
//            }
        }
    }

    fun clear() {
        devices.clear()
        _players.clear()
        this.playerViewModel?.updatePlayers(ArrayList(this._players.values))
    }
}