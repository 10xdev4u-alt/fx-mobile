package dev.tenx.fxmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tenx.fxmobile.data.remote.KiloRepository
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
    val selectedModel: String = "anthropic/claude-sonnet-4.5",
    val availableModels: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val isLoadingModels: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val kiloRepository: KiloRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val existingKey = tokenProvider.getToken() ?: ""
            val savedModel = preferencesManager.getModel()
            _uiState.update { it.copy(apiKey = existingKey, selectedModel = savedModel) }
        }
    }

    fun onApiKeyChanged(key: String) {
        _uiState.update { it.copy(apiKey = key, error = null) }
    }

    fun onModelChanged(model: String) {
        _uiState.update { it.copy(selectedModel = model) }
        viewModelScope.launch {
            preferencesManager.setModel(model)
        }
    }

    suspend fun saveApiKey() {
        _uiState.update { it.copy(isSaving = true, error = null) }
        try {
            tokenProvider.setToken(_uiState.value.apiKey)
            loadAvailableModels()
            _uiState.update { it.copy(isSaving = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isSaving = false, error = e.message) }
        }
    }

    fun loadAvailableModels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingModels = true) }
            val result = kiloRepository.getAvailableModels()
            result.fold(
                onSuccess = { models ->
                    _uiState.update { it.copy(availableModels = models, isLoadingModels = false) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoadingModels = false, error = e.message) }
                }
            )
        }
    }

    fun clearApiKey() {
        viewModelScope.launch {
            tokenProvider.clearToken()
            _uiState.update { it.copy(apiKey = "", availableModels = emptyList()) }
        }
    }
}
