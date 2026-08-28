package dev.tenx.fxmobile.ui.util

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

fun Modifier.semantics(
    contentDescription: String? = null,
    role: androidx.compose.ui.semantics.Role? = null
): Modifier = this.then(
    androidx.compose.ui.semantics.semantics {
        if (contentDescription != null) {
            this.contentDescription = contentDescription
        }
        if (role != null) {
            this.role = role
        }
    }
)

@Composable
fun Modifier.standardIconSize(): Modifier = this.then(
    Modifier.size(24.dp)
)
