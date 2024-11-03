package com.example.itquiz.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itquiz.ui.theme.ITQuizTheme
import com.example.itquiz.ui.theme.buttonColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onStartClick: (String) -> Unit, // Agora recebe o nome do usuário
    onLeaderboardClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "ITQuiz",
                fontSize = 64.sp,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))

            TextField(
                value = username,
                onValueChange = { newName -> username = newName },
                label = { Text("Insira seu nome para começar", color = Color.White) },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent, // Define a cor do container como transparente
                    focusedIndicatorColor = Color.White, // Esconde o indicador de foco
                    unfocusedIndicatorColor = Color.White, // Esconde o indicador de foco
                    // Remova os parâmetros que estavam causando erros
                ),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White) // Define a cor do texto digitado
            )




            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onStartClick(username) },
                shape = RoundedCornerShape(10.dp),
                colors = buttonColors.copy(
                    disabledContainerColor = Color.LightGray, // Cor para o estado desabilitado
                    disabledContentColor = Color.Black // Cor do texto para o estado desabilitado
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .alpha(if (username.isNotEmpty()) 1f else 0.5f),
                enabled = username.isNotEmpty(),
            ) {
                Text(text = "Começar")
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLeaderboardClick,
                shape = RoundedCornerShape(10.dp),
                colors = buttonColors,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Leaderboard", color = Color.Black)
            }
        }
    }
}