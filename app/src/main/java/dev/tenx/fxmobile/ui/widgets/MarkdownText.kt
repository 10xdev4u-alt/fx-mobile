package dev.tenx.fxmobile.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        val lines = markdown.lines()
        var inCodeBlock = false
        val codeBuffer = StringBuilder()

        lines.forEachIndexed { index, line ->
            when {
                line.startsWith("```") -> {
                    if (inCodeBlock) {
                        // End code block
                        SelectionContainer {
                            Text(
                                text = codeBuffer.toString().trimEnd(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        codeBuffer.clear()
                        inCodeBlock = false
                    } else {
                        inCodeBlock = true
                    }
                }
                inCodeBlock -> {
                    codeBuffer.append(line).append("\n")
                }
                line.startsWith("# ") -> {
                    Text(
                        text = line.removePrefix("# "),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = line.removePrefix("## "),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                line.startsWith("### ") -> {
                    Text(
                        text = line.removePrefix("### "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    Text(
                        text = "• ${line.removePrefix("- ").removePrefix("* ")}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                line.startsWith("1. ") || line.startsWith("2. ") || line.startsWith("3. ") -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                line.startsWith(">") -> {
                    Text(
                        text = line.removePrefix("> "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                line.contains("**") -> {
                    val parts = line.split("**")
                    Text(
                        text = parts.joinToString("") { it },
                        fontWeight = if (line.indexOf("**") < line.lastIndexOf("**")) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {
                    if (line.isNotBlank()) {
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
