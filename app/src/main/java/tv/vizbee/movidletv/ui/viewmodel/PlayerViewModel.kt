package tv.vizbee.movidletv.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tv.vizbee.movidletv.data.model.Player
import tv.vizbee.movidletv.vizbee.PlayerManager

class PlayerViewModel : ViewModel() {
    private val _playerList = MutableLiveData<List<Player>>()
    val playerList: LiveData<List<Player>> get() = _playerList

    init {
        PlayerManager.getPlayers(this)
    }

    fun addPlayer(player: Player) {
        val updatedList = _playerList.value.orEmpty() + player
        _playerList.value = updatedList
    }

    fun removePlayer(playerId: String) {
        val updatedList = _playerList.value.orEmpty().filter { it.userId != playerId }
        _playerList.value = updatedList
    }

    fun updatePlayers(players: List<Player>) {
        _playerList.value = players
    }
}