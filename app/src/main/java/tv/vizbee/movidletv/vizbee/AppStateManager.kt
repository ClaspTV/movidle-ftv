package tv.vizbee.movidletv.vizbee

import tv.vizbee.movidletv.data.model.AppState
import tv.vizbee.movidletv.ui.viewmodel.AppViewModel
import tv.vizbee.movidletv.ui.viewmodel.PlayerViewModel
import tv.vizbee.movidletv.ui.viewmodel.ScoreViewModel

object AppStateManager {
    private var _appState: AppState = AppState.NotConnected

    private var appViewModel: AppViewModel? = null
    private var playerViewModel: PlayerViewModel? = null
    private var scoreViewModel: ScoreViewModel? = null

    fun updateState(appState: AppState) {
        this._appState = appState
        appViewModel?.updateAppState(state = this._appState)
    }

    fun getAppState(appViewModel: AppViewModel){
        this.appViewModel = appViewModel
        this.appViewModel?.updateAppState(this._appState)
    }
}