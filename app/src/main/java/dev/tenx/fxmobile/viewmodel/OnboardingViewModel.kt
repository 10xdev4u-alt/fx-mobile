package dev.tenx.fxmobile.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tenx.fxmobile.data.remote.TokenProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class OnboardingUiState(
    val apiKey: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onApiKeyChanged(key: String) {
        _uiState.update { it.copy(apiKey = key, error = null) }
    }

    suspend fun saveApiKey() {
        _uiState.update { it.copy(isSaving = true) }
        try {
            tokenProvider.setToken(_uiState.value.apiKey)
            _uiState.update { it.copy(isSaving = false) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isSaving = false, error = e.message) }
        }
    }
}
