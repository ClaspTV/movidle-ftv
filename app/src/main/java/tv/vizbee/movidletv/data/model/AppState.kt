package tv.vizbee.movidletv.data.model

sealed class AppState {
    object NotConnected : AppState()
    object Connected : AppState()
    object WaitingForPlayers : AppState()
    object GameStarted : AppState()
    object GameInProgress : AppState()
    object GameCompleted : AppState()
}