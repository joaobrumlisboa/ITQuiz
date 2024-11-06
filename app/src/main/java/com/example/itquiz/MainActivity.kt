package com.example.itquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.itquiz.ui.LeaderboardScreen
import com.example.itquiz.ui.MainScreen
import com.example.itquiz.ui.QuizScreen
import com.example.itquiz.ui.VideoBackground

class MainActivity : ComponentActivity() {
    private var currentScreen by mutableStateOf("main")
    private var score by mutableStateOf(0)
    private var username by mutableStateOf("")

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    VideoBackground()

                    Box(modifier = Modifier.fillMaxSize().padding(32.dp)) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                slideInHorizontally(initialOffsetX = { width -> width }) with
                                        slideOutHorizontally(targetOffsetX = { width -> -width }) using
                                        SizeTransform(clip = false)
                            }
                        ) { screen ->
                            when (screen) {
                                "main" -> MainScreen(
                                    onStartClick = { name ->
                                        username = name
                                        currentScreen = "quiz"
                                    },
                                    onLeaderboardClick = {
                                        currentScreen = "leaderboard"
                                    }
                                )
                                "quiz" -> QuizScreen(
                                    onFinish = { finalScore ->
                                        score = finalScore
                                        currentScreen = if (finalScore >= 0) "leaderboard" else "main"
                                    },
                                    username = username
                                )
                                "leaderboard" -> LeaderboardScreen(
                                    onBackClick = { currentScreen = "main" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
