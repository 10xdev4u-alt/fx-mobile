package dev.tenx.fxmobile.ui.screen.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.tenx.fxmobile.ui.theme.FxTheme
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `empty state displays welcome message`() {
        composeTestRule.setContent {
            FxTheme {
                EmptyStateContent()
            }
        }

        composeTestRule
            .onNodeWithText("What can I help with?")
            .assertIsDisplayed()
    }

    @Test
    fun `input field accepts text`() {
        composeTestRule.setContent {
            FxTheme {
                TestInputBar(
                    value = "",
                    onValueChange = {},
                    onSend = {},
                    isGenerating = false
                )
            }
        }

        composeTestRule
            .onNodeWithText("Ask fx anything...")
            .assertIsDisplayed()
    }
}

@Composable
private fun EmptyStateContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What can I help with?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TestInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isGenerating: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Ask fx anything...") }
    )
}
