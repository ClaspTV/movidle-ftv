package tv.vizbee.movidletv.data.model

sealed class AppState {
    object NotConnected : AppState()
    object Connected : AppState()
    data class WaitingForPlayers(val channelId: String) : AppState()
    object GameStarted : AppState()
    data class GameInProgress(val currentClip: Int) : AppState()
    data class GameCompleted(val finalScores: List<String>) : AppState()
}