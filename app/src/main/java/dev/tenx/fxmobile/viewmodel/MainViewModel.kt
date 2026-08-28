package dev.tenx.fxmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tenx.fxmobile.data.repository.SessionRepository
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.AgentSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val currentSessionId: String? = null,
    val messages: List<AgentMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val error: String? = null,
    val inputDraft: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val sessions: StateFlow<List<AgentSession>> = sessionRepository
        .observeSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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

    init {
        viewModelScope.launch {
            sessions.collect { list ->
                if (list.isNotEmpty() && _uiState.value.currentSessionId == null) {
                    openSession(list.first().id)
                }
            }
        }
        viewModelScope.launch {
            messages.collect { list ->
                _uiState.update { it.copy(messages = list) }
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputDraft = text) }
    }

    fun send() {
        val text = _uiState.value.inputDraft.trim()
        if (text.isEmpty() || _uiState.value.isGenerating) return

        viewModelScope.launch {
            var sessionId = _uiState.value.currentSessionId
            if (sessionId == null) {
                sessionId = sessionRepository.createSession(text.take(30))
                _uiState.update { it.copy(currentSessionId = sessionId) }
            }

            _uiState.update { it.copy(isGenerating = true, inputDraft = "", error = null) }

            val result = sessionRepository.sendMessage(sessionId, text)
            result.fold(
                onSuccess = { _uiState.update { it.copy(isGenerating = false) } },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
            )
        }
    }

    fun openSession(id: String) {
        _uiState.update { it.copy(currentSessionId = id) }
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
                _uiState.update { it.copy(currentSessionId = null, messages = emptyList()) }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
