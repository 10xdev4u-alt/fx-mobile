package dev.tenx.fxmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tenx.fxmobile.data.remote.KiloRepository
import dev.tenx.fxmobile.data.remote.InferenceConfig
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.MessageRole
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ConversationUiState(
    val currentSessionId: String? = null,
    val title: String = "",
    val isGenerating: Boolean = false,
    val error: String? = null,
    val inputDraft: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val kiloRepository: KiloRepository,
    private val sessionRepository: dev.tenx.fxmobile.data.repository.SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    val messages: StateFlow<List<AgentMessage>> = _uiState
        .flatMapLatest { state ->
            val sessionId = state.currentSessionId
            if (sessionId != null) {
                sessionRepository.observeMessages(sessionId)
            } else {
                kotlinx.coroutines.flow.flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun loadSession(sessionId: String) {
        _uiState.value = _uiState.value.copy(currentSessionId = sessionId)
    }

    fun createNewSession() {
        viewModelScope.launch {
            val id = sessionRepository.createSession()
            _uiState.value = _uiState.value.copy(currentSessionId = id, title = "New session")
        }
    }

    fun onInputChanged(text: String) {
        _uiState.value = _uiState.value.copy(inputDraft = text)
    }

    fun send() {
        val text = _uiState.value.inputDraft.trim()
        if (text.isEmpty() || _uiState.value.isGenerating) return

        viewModelScope.launch {
            var sessionId = _uiState.value.currentSessionId
            if (sessionId == null) {
                sessionId = sessionRepository.createSession(text.take(30))
                _uiState.value = _uiState.value.copy(currentSessionId = sessionId, title = text.take(30))
            }

            _uiState.value = _uiState.value.copy(isGenerating = true, inputDraft = "", error = null)

            val result = sessionRepository.sendMessage(sessionId, text)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isGenerating = false)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            sessionRepository.deleteSession(id)
            if (_uiState.value.currentSessionId == id) {
                _uiState.value = _uiState.value.copy(currentSessionId = null)
            }
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
