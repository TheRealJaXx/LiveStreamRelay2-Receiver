package com.example.livestreamrelayreceiver.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.livestreamrelayreceiver.network.MjpegStreamClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReceiverUiState(
    val isConnected: Boolean = false,
    val showUi: Boolean = true,
    val serverIp: String = "192.168.1.10",
    val fps: Float = 0f,
    val connectionStatus: String = "Idle",
    val currentFrame: Bitmap? = null
)

class ReceiverViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ReceiverUiState())
    val uiState: StateFlow<ReceiverUiState> = _uiState.asStateFlow()

    private var client: MjpegStreamClient? = null

    init {
        connectToRelay()
    }

    fun connectToRelay(serverIp: String = "192.168.1.10") {
        val host = serverIp.trim()
        if (host.isEmpty()) return

        stopClient()

        _uiState.value = _uiState.value.copy(
            serverIp = host,
            connectionStatus = "Connecting",
            isConnected = false
        )

        client = MjpegStreamClient(
            host = host,
            port = 8888,
            onFrame = { bitmap ->
                _uiState.value = _uiState.value.copy(
                    isConnected = true,
                    currentFrame = bitmap,
                    connectionStatus = "Streaming"
                )
            },
            onStatus = { message ->
                _uiState.value = _uiState.value.copy(connectionStatus = message)
            },
            onError = {
                _uiState.value = _uiState.value.copy(
                    isConnected = false,
                    connectionStatus = "Connection failed"
                )
            }
        )

        client?.start()
    }

    fun toggleUi() {
        _uiState.value = _uiState.value.copy(showUi = !_uiState.value.showUi)
    }

    private fun stopClient() {
        client?.stop()
        client = null
    }

    override fun onCleared() {
        super.onCleared()
        stopClient()
    }
}
