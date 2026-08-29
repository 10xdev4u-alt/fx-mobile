package dev.tenx.fxmobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tenx.fxmobile.terminal.FileInfo
import dev.tenx.fxmobile.terminal.ShellExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class FileExplorerUiState(
    val currentPath: String = "/data/data/dev.tenx.fxmobile/files/workspace",
    val files: List<FileInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FileExplorerViewModel @Inject constructor(
    private val shellExecutor: ShellExecutor
) : ViewModel() {

    private val _uiState = MutableStateFlow(FileExplorerUiState())
    val uiState: StateFlow<FileExplorerUiState> = _uiState.asStateFlow()

    fun loadDirectory(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentPath = path) }
            try {
                val files = shellExecutor.listFiles(path)
                _uiState.update { it.copy(files = files.sortedWith(compareBy({ !it.isDirectory }, { it.name })), isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun navigateUp() {
        val parent = File(_uiState.value.currentPath).parent
        if (parent != null) {
            loadDirectory(parent)
        }
    }

    fun createNewFolder() {
        viewModelScope.launch {
            val result = shellExecutor.execute("mkdir -p ${_uiState.value.currentPath}/new-folder")
            if (!result.isError) {
                loadDirectory(_uiState.value.currentPath)
            }
        }
    }

    fun openFile(path: String) {
        viewModelScope.launch {
            val result = shellExecutor.readFile(path)
            // TODO: Open file viewer/editor
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
