package tv.vizbee.movidletv.vizbee

import tv.vizbee.screen.api.session.model.device.VizbeeDevice

object PlayerManager {
    val players by lazy { arrayListOf<VizbeeDevice>() }

    fun addAll(members: List<VizbeeDevice>) {
        members.forEach {
            if (!players.contains(it)) {
                addPlayer(it)
            }
        }
    }

    fun addPlayer(player: VizbeeDevice?) {
        player?.let { actualPlayer ->
            players.find { it.deviceId == actualPlayer.deviceId } ?: {
                players.add(actualPlayer)
            }
        }
    }

    fun removePlayer(player: VizbeeDevice?) {
        player?.let { actualPlayer ->
            players.find { it.deviceId == actualPlayer.deviceId }?.let { players.remove(it) }
        }
    }
}