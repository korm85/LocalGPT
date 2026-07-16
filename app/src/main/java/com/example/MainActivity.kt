package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.ArtifactScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ModelScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

/**
 * Main entrance of the Bonsai AI Sandbox application.
 * Utilizes a single activity layout with lightweight tabbed states.
 */
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Supports full edge-to-edge immersive views
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var activeTab by remember { mutableStateOf(AppTab.CHAT) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("bottom_nav_bar")
                        ) {
                            AppTab.values().forEach { tab ->
                                NavigationBarItem(
                                    selected = activeTab == tab,
                                    onClick = { activeTab = tab },
                                    label = { Text(tab.title) },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title
                                        )
                                    },
                                    modifier = Modifier.testTag(tab.tag)
                                )
                            }
                        }
                    },
                    contentWindowInsets = WindowInsets.safeDrawing // Prevents camera notches clipping content
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (activeTab) {
                            AppTab.CHAT -> ChatScreen(
                                viewModel = viewModel,
                                onNavigateToArtifact = {
                                    activeTab = AppTab.ARTIFACTS
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                            AppTab.ARTIFACTS -> ArtifactScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                            AppTab.MODEL -> ModelScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                            AppTab.SETTINGS -> SettingsScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Defines the main application tabs and metadata.
 */
enum class AppTab(val title: String, val icon: ImageVector, val tag: String) {
    CHAT("Chat", Icons.Filled.Chat, "tab_chat"),
    ARTIFACTS("Artifacts", Icons.Filled.Extension, "tab_artifacts"),
    MODEL("GGUF Model", Icons.Filled.Memory, "tab_model"),
    SETTINGS("Settings", Icons.Filled.Settings, "tab_settings")
}
