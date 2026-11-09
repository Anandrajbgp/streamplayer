package com.streamplayer.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.streamplayer.app.data.StreamSource
import com.streamplayer.app.ui.dashboard.DashboardScreen
import com.streamplayer.app.ui.player.CustomVideoPlayerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
                    var playerTitle by remember { mutableStateOf("") }
                    var playerStreams by remember { mutableStateOf<List<StreamSource>>(emptyList()) }

                    when (val screen = currentScreen) {
                        is Screen.Dashboard -> {
                            DashboardScreen(
                                onPlayVideo = { title, streams ->
                                    playerTitle = title
                                    playerStreams = streams
                                    currentScreen = Screen.Player
                                }
                            )
                        }
                        is Screen.Player -> {
                            CustomVideoPlayerScreen(
                                title = playerTitle,
                                streams = playerStreams,
                                onBack = {
                                    currentScreen = Screen.Dashboard
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object Dashboard : Screen()
    object Player : Screen()
}
