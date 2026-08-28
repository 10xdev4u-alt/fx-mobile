package dev.tenx.fxmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tenx.fxmobile.data.repository.SessionRepository
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.AgentSession
import dev.tenx.fxmobile.domain.model.MessageRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class MainUiState(
    val currentSessionId: String? = null,
    val messages: List<AgentMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val error: String? = null,
    val inputDraft: String = ""
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val sessions: StateFlow<List<AgentSession>> = sessionRepository
        .observeSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            sessions.collect { list ->
                if (list.isNotEmpty() && _uiState.value.currentSessionId == null) {
                    openSession(list.first().id)
                }
            }
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
                _uiState.value = _uiState.value.copy(currentSessionId = sessionId)
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

    fun openSession(id: String) {
        _uiState.value = _uiState.value.copy(currentSessionId = id)
        viewModelScope.launch {
            sessionRepository.observeMessages(id).collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    fun createSession() {
        viewModelScope.launch {
            val id = sessionRepository.createSession()
            openSession(id)
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            sessionRepository.deleteSession(id)
            if (_uiState.value.currentSessionId == id) {
                _uiState.value = _uiState.value.copy(currentSessionId = null, messages = emptyList())
            }
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
