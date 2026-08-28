package dev.tenx.fxmobile.ui.util

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

fun Modifier.accessibilitySemantics(
    contentDescription: String? = null
): Modifier = composed {
    if (contentDescription != null) {
        this.then(semantics {
            this.contentDescription = contentDescription
        })
    } else {
        this
    }
}

@Composable
fun Modifier.standardIconSize(): Modifier = this.then(
    Modifier.size(24.dp)
)
