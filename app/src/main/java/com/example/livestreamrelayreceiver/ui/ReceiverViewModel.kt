package com.example.livestreamrelayreceiver.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReceiverUiState(
    val isConnected: Boolean = false,
    val showUi: Boolean = true,
    val serverIp: String = "192.168.1.10",
    val fps: Float = 0f,
    val connectionStatus: String = "Idle"
)

class ReceiverViewModel(
    private val app: android.app.Application
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiverUiState())
    val uiState: StateFlow<ReceiverUiState> = _uiState.asStateFlow()

    fun toggleUi() {
        _uiState.value = _uiState.value.copy(showUi = !_uiState.value.showUi)
    }
}
