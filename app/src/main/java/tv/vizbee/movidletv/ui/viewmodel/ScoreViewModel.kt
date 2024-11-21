package tv.vizbee.movidletv.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tv.vizbee.movidletv.data.model.Player
import tv.vizbee.movidletv.vizbee.PlayerManager

class ScoreViewModel : ViewModel() {
    private val _scores = MutableLiveData<Map<String, String>>()
    val scores: LiveData<Map<String, String>> get() = _scores

    init {
        PlayerManager.getScores(this)
    }

    fun updateScore(playerId: String, score: String) {
        val updatedScores = _scores.value.orEmpty().toMutableMap()
        updatedScores[playerId] = score
        _scores.value = updatedScores
    }

    fun getPlayerScore(playerId: String): String = _scores.value?.get(playerId) ?: "0"

    fun updateScores(players: HashMap<String, Player>) {
        val map = HashMap<String, String>()
        for ((key, value) in players) {
            map[key] = value.score
        }
        _scores.value = map
    }
}