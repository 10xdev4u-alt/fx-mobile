package dev.tenx.fxmobile.ui.screen.terminal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class TerminalLine(
    val id: String,
    val content: String,
    val isCommand: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(navController: NavHostController) {
    var command by remember { mutableStateOf("") }
    val lines = remember {
        mutableStateOf(
            listOf(
                TerminalLine("1", "Welcome to fx terminal", false),
                TerminalLine("2", "Type commands to interact with your workspace.", false),
                TerminalLine("3", "$ ", true)
            )
        )
    }
    val listState = rememberLazyListState()

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
                    .padding(8.dp)
            ) {
                items(lines.value, key = { it.id }) { line ->
                    Text(
                        text = line.content,
                        color = when {
                            line.isCommand -> Color(0xFF4EC9B0)
                            line.content.startsWith("Error") -> Color(0xFFF48771)
                            else -> Color(0xFFD4D4D4)
                        },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D2D))
                    .padding(8.dp),
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
                        fontFamily = FontFamily.Monospace
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (command.isEmpty()) {
                                Text(
                                    text = "Type a command...",
                                    color = Color(0xFF6A6A6A),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}
