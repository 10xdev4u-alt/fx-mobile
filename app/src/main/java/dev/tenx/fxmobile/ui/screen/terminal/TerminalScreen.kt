package dev.tenx.fxmobile.ui.screen.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.tenx.fxmobile.terminal.CommandResult
import dev.tenx.fxmobile.terminal.ShellExecutor
import kotlinx.coroutines.launch

data class TerminalLine(
    val id: String,
    val content: String,
    val isCommand: Boolean = false,
    val isError: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(navController: NavHostController) {
    val executor = remember { ShellExecutor() }
    val lines = remember { mutableStateListOf<TerminalLine>() }
    var command by remember { mutableStateOf("") }
    var isExecuting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        lines.add(TerminalLine("welcome-1", "fx terminal", isCommand = false))
        lines.add(TerminalLine("welcome-2", "Working directory: ${executor.getWorkingDirectory()}", isCommand = false))
        lines.add(TerminalLine("welcome-3", "Type commands to interact with your workspace.", isCommand = false))
        lines.add(TerminalLine("welcome-4", "", isCommand = false))
    }

    fun executeCommand() {
        val cmd = command.trim()
        if (cmd.isEmpty() || isExecuting) return

        lines.add(TerminalLine("cmd-${System.currentTimeMillis()}", "$ $cmd", isCommand = true))
        command = ""
        isExecuting = true

        scope.launch {
            val result = executor.execute(cmd)
            lines.add(
                TerminalLine(
                    id = "out-${System.currentTimeMillis()}",
                    content = result.output.ifEmpty { "(no output)" },
                    isError = result.isError
                )
            )
            lines.add(TerminalLine("prompt-${System.currentTimeMillis()}", "", isCommand = false))
            isExecuting = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terminal") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(lines, key = { it.id }) { line ->
                    if (line.isCommand) {
                        Text(
                            text = line.content,
                            color = Color(0xFF4EC9B0),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (line.content.isNotEmpty()) {
                        Text(
                            text = line.content,
                            color = if (line.isError) Color(0xFFF48771) else Color(0xFFD4D4D4),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D2D))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$",
                    color = Color(0xFF4EC9B0),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                BasicTextField(
                    value = command,
                    onValueChange = { command = it },
                    modifier = Modifier.weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    ),
                    singleLine = true,
                    enabled = !isExecuting,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { executeCommand() }),
                    decorationBox = { innerTextField ->
                        Box {
                            if (command.isEmpty() && !isExecuting) {
                                Text(
                                    text = "Type a command...",
                                    color = Color(0xFF6A6A6A),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                if (isExecuting) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF4EC9B0)
                    )
                } else {
                    IconButton(onClick = { executeCommand() }) {
                        Icon(
                            Icons.Default.KeyboardReturn,
                            contentDescription = "Execute",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
