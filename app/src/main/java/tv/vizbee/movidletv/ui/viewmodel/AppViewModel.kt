package tv.vizbee.movidletv.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tv.vizbee.movidletv.data.model.AppState
import tv.vizbee.movidletv.vizbee.AppStateManager

class AppViewModel : ViewModel() {
    private val _appState = MutableLiveData<AppState>()
    val appState: LiveData<AppState> get() = _appState

    init {
        AppStateManager.getAppState(this)
    }

    fun updateAppState(state: AppState) {
        Log.i(LOG_TAG, "Received an updated state = $state")
        _appState.value = state
    }

    fun getCurrentState(): AppState? = _appState.value

    companion object {
        private const val LOG_TAG = "AppViewModel"
    }
}