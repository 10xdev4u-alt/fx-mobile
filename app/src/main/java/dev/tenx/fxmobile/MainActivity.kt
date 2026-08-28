package dev.tenx.fxmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.tenx.fxmobile.ui.navigation.FxApp
import dev.tenx.fxmobile.ui.theme.FxTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FxTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    FxApp()
                }
            }
        }
    }
}
