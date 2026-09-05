package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.BrowserDatabase
import com.example.data.repository.BrowserRepository
import com.example.engine.ManifestV3ExtensionManager
import com.example.ui.screens.BrowserScreen
import com.example.ui.theme.LiquidBrowserTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.BrowserViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = BrowserDatabase.getDatabase(applicationContext)
        val repository = BrowserRepository(database.browserDao())
        val extensionManager = ManifestV3ExtensionManager()

        setContent {
            LiquidBrowserTheme {
                val viewModel: BrowserViewModel = viewModel(
                    factory = BrowserViewModelFactory(repository, extensionManager)
                )

                Surface(modifier = Modifier.fillMaxSize()) {
                    BrowserScreen(viewModel = viewModel)
                }
            }
        }
    }
}

// Retained for test suite compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
