package com.example.itquiz.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.itquiz.data.AppDatabase
import com.example.itquiz.data.QuestionRepository
import com.example.itquiz.data.Score
import com.example.itquiz.ui.theme.buttonColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun QuizScreen(onFinish: (Int) -> Unit, username: String) {
    val context = LocalContext.current
    val questionRepository = QuestionRepository(context)
    val questions = remember { questionRepository.randomizeQuestions(5) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf(-1) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(999) }
    var timeTaken by remember { mutableStateOf(0L) }
    var correctAnswersCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val scoreDao = db.scoreDao()
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // Proteção contra acesso a perguntas quando todas já foram respondidas
    if (currentQuestionIndex >= questions.size) {
        return // Retorna imediatamente se não houver mais perguntas
    }

    val currentQuestion = questions[currentQuestionIndex]
    val imageBitmap = loadImageFromAssets(context, currentQuestion.image)?.asImageBitmap()

    fun playAudio(context: Context) {
        // Libera o MediaPlayer anterior se estiver em uso
        mediaPlayer?.apply {
            reset()  // Redefine o MediaPlayer
            release() // Libera recursos
        }

        mediaPlayer = MediaPlayer()
        try {
            // Configura o arquivo de áudio a partir dos assets
            val assetFileDescriptor = context.assets.openFd("beep.mp3")
            mediaPlayer?.apply {
                setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)

                // Usa prepareAsync para preparar o áudio de forma assíncrona
                setOnPreparedListener {
                    start() // Reproduz o áudio quando estiver preparado
                    Log.d("QuizScreen", "Audio started playing successfully")
                }
                prepareAsync() // Prepara o áudio de forma assíncrona

                // Libera o MediaPlayer após a reprodução
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    Log.d("QuizScreen", "Audio playback completed and MediaPlayer released")
                }
            }
        } catch (e: IOException) {
            Log.e("QuizScreen", "Error playing audio: ${e.message}")
        }
    }












    val randomizedIndices by remember(currentQuestionIndex) {
        mutableStateOf((0 until currentQuestion.options.size).shuffled())
    }

    val correctAnswerIndex = 1
    val correctAnswer = currentQuestion.options[correctAnswerIndex]

    Column(
        modifier = Modifier.fillMaxSize().padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val correctAnswersImage = "${correctAnswersCount}acerto.png"
        loadImageFromAssets(context, correctAnswersImage)?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.size(133.dp, 75.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageBitmap?.let {
            Image(bitmap = it, contentDescription = null, modifier = Modifier.size(175.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = currentQuestion.text, fontSize = 20.sp, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        if (currentQuestion.id == 21) {
            Button(onClick = { playAudio(context) }, colors = buttonColors, modifier = Modifier.size(width = 150.dp, height = 50.dp)) {
                Text(text = "Tocar áudio")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        randomizedIndices.forEachIndexed { index, randomIndex ->
            val option = currentQuestion.options[randomIndex]
            Button(
                onClick = {
                    selectedAnswer = randomIndex
                    showFeedback = true
                    isCorrect = randomIndex == correctAnswerIndex
                    timeTaken = (System.currentTimeMillis() - timeTaken)
                    score -= (timeTaken / 100).toInt()
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        selectedAnswer == randomIndex && showFeedback && isCorrect -> Color.Green
                        selectedAnswer == randomIndex && showFeedback && !isCorrect -> Color.Red
                        else -> Color.White
                    }
                )
            ) {
                Text(text = option, color = Color.Black)
            }
        }

        LaunchedEffect(showFeedback) {
            if (showFeedback) {
                delay(500)
                showFeedback = false

                if (isCorrect) {
                    correctAnswersCount++
                }

                currentQuestionIndex++

                // Verifique se o usuário acertou e se é a última pergunta
                if (currentQuestionIndex >= questions.size) {
                    // O quiz terminou
                    if (isCorrect) {
                        // Salva o score ao final do quiz somente se o usuário acertou
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                scoreDao.insertScore(Score(username = username, points = score))
                            }
                            onFinish(score) // Chama onFinish após a inserção
                        }
                    } else {
                        // Se o usuário errou, não deve ir para a leaderboard
                        onFinish(-1) // Passar -1 para indicar que o quiz foi perdido
                    }
                } else if (!isCorrect) {
                    onFinish(-1) // Passar -1 se errou, mesmo que não seja a última pergunta
                } else {
                    timeTaken = System.currentTimeMillis()
                }
            }
        }


        LaunchedEffect(currentQuestionIndex) {
            timeTaken = System.currentTimeMillis()
        }
    }
}

private fun loadImageFromAssets(context: Context, fileName: String): Bitmap? {
    val assetManager = context.assets
    val inputStream = assetManager.open(fileName)
    return BitmapFactory.decodeStream(inputStream)
}