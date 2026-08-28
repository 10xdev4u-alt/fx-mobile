package dev.tenx.fxmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tenx.fxmobile.data.remote.PreferencesManager
import dev.tenx.fxmobile.data.remote.TokenProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val apiKey: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val existingKey = tokenProvider.getToken() ?: ""
            _uiState.update { it.copy(apiKey = existingKey) }
        }
    }

    fun onApiKeyChanged(key: String) {
        _uiState.update { it.copy(apiKey = key, error = null) }
    }

    suspend fun saveApiKey() {
        _uiState.update { it.copy(isSaving = true, error = null) }
        try {
            tokenProvider.setToken(_uiState.value.apiKey)
            _uiState.update { it.copy(isSaving = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isSaving = false, error = e.message) }
        }
    }

    fun clearApiKey() {
        viewModelScope.launch {
            tokenProvider.clearToken()
            _uiState.update { it.copy(apiKey = "") }
        }
    }
}
