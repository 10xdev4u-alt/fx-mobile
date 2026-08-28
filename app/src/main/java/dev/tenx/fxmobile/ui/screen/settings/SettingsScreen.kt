package dev.tenx.fxmobile.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SettingsSection(title = "Appearance") {
                SettingsClickableRow(
                    icon = Icons.Default.Palette,
                    title = "Dark mode",
                    subtitle = "Use dark theme",
                    onClick = { /* TODO: toggle dark mode */ }
                )
            }

            SettingsSection(title = "General") {
                SettingsClickableRow(
                    icon = Icons.Default.Info,
                    title = "Notifications",
                    subtitle = "Show agent progress",
                    onClick = { /* TODO: toggle notifications */ }
                )
                HorizontalDivider()
                SettingsClickableRow(
                    icon = Icons.Default.Code,
                    title = "Auto-save sessions",
                    subtitle = "Save conversations automatically",
                    onClick = { /* TODO: toggle auto-save */ }
                )
            }

            SettingsSection(title = "Account") {
                SettingsClickableRow(
                    icon = Icons.Default.Person,
                    title = "Profile",
                    subtitle = "Manage your account",
                    onClick = { /* TODO: navigate to profile */ }
                )
                HorizontalDivider()
                SettingsClickableRow(
                    icon = Icons.Default.Security,
                    title = "Privacy",
                    subtitle = "Data and permissions",
                    onClick = { /* TODO: navigate to privacy */ }
                )
            }

            SettingsSection(title = "Data") {
                SettingsClickableRow(
                    icon = Icons.Default.Storage,
                    title = "Storage",
                    subtitle = "Manage local data",
                    onClick = { /* TODO: navigate to storage */ }
                )
                HorizontalDivider()
                SettingsClickableRow(
                    icon = Icons.Default.Terminal,
                    title = "Model settings",
                    subtitle = "Configure inference",
                    onClick = { /* TODO: navigate to model settings */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "fx-mobile v0.1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
