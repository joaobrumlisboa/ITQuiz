package com.example.itquiz.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itquiz.data.AppDatabase
import com.example.itquiz.data.Score
import com.example.itquiz.ui.theme.buttonColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreen(onBackClick: () -> Unit) {
    var scores by remember { mutableStateOf(listOf<Score>()) }
    val coroutineScope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(LocalContext.current)
    val scoreDao = db.scoreDao()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            scores = scoreDao.getAllScores().take(10)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 32.dp, end = 32.dp, top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Leaderboard", fontSize = 46.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            scores.forEach { score ->
                Text(
                    text = "${score.username} - ${score.points}",
                    color = Color.White,
                    fontSize = 22.sp
                )
            }
        }

        Button(
            onClick = onBackClick,
            border = BorderStroke(1.dp, Color.White),
            colors = buttonColors,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 32.dp, end = 32.dp, bottom = 16.dp)
        ) {
            Text(text = "Menu Principal", color = Color.Black)
        }
    }
}

