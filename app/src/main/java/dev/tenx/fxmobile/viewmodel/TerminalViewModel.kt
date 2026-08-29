package dev.tenx.fxmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tenx.fxmobile.terminal.ShellExecutor
import dev.tenx.fxmobile.ui.screen.terminal.TerminalLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TerminalUiState(
    val lines: List<TerminalLine> = emptyList(),
    val currentInput: String = "",
    val isExecuting: Boolean = false,
    val workingDirectory: String = ""
)

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val shellExecutor: ShellExecutor
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    fun initialize() {
        if (_uiState.value.lines.isEmpty()) {
            _uiState.update {
                it.copy(
                    lines = listOf(
                        TerminalLine("welcome-1", "Welcome to fx terminal", false),
                        TerminalLine("welcome-2", "Working directory: ${shellExecutor.getWorkingDirectoryPath()}", false),
                        TerminalLine("welcome-3", "Type commands to interact with your workspace.", false),
                        TerminalLine("welcome-4", "", false)
                    ),
                    workingDirectory = shellExecutor.getWorkingDirectoryPath()
                )
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(currentInput = text) }
    }

    fun executeCommand() {
        val command = _uiState.value.currentInput.trim()
        if (command.isEmpty() || _uiState.value.isExecuting) return

        val commandLine = TerminalLine(
            id = "cmd-${UUID.randomUUID()}",
            content = "$ $command",
            isCommand = true
        )

        _uiState.update {
            it.copy(
                isExecuting = true,
                currentInput = "",
                lines = it.lines + commandLine
            )
        }

        viewModelScope.launch {
            val result = shellExecutor.execute(command)

            val resultLine = if (result.output.isNotEmpty()) {
                TerminalLine(
                    id = "out-${UUID.randomUUID()}",
                    content = result.output,
                    isError = result.isError
                )
            } else if (result.isError) {
                TerminalLine(
                    id = "err-${UUID.randomUUID()}",
                    content = "[Exit code: ${result.exitCode}]",
                    isError = true
                )
            } else {
                TerminalLine(
                    id = "ok-${UUID.randomUUID()}",
                    content = "[Command completed successfully]",
                    isError = false
                )
            }

            _uiState.update {
                it.copy(
                    isExecuting = false,
                    lines = it.lines + resultLine
                )
            }
        }
    }
}
