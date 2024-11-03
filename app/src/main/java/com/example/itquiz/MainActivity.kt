package com.example.itquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
    private var username by mutableStateOf("") // Variável para armazenar o nome do usuário

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    VideoBackground()

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)) {
                        when (currentScreen) {
                            "main" -> MainScreen(
                                onStartClick = { name ->
                                    username = name // Armazena o nome do usuário
                                    currentScreen = "quiz"
                                },
                                onLeaderboardClick = { currentScreen = "leaderboard" }
                            )
                            "leaderboard" -> LeaderboardScreen(onBackClick = { currentScreen = "main" })
                            "quiz" -> QuizScreen(onFinish = { finalScore ->
                                score = finalScore
                                currentScreen = if (finalScore >= 0) {
                                    "leaderboard" // Mostra o leaderboard se o usuário ganhar
                                } else {
                                    "main" // Volta para o menu principal se perder
                                }
                            }, username = username) // Passa o nome do usuário para a QuizScreen
                        }
                    }
                }
            }
        }
    }
}
