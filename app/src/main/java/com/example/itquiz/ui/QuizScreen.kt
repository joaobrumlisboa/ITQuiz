package com.example.itquiz.ui


import android.content.Context
import android.os.Build
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Log
import android.media.AudioManager
import android.media.AudioAttributes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.itquiz.data.AppDatabase
import com.example.itquiz.data.QuestionRepository
import com.example.itquiz.data.Score
import com.example.itquiz.ui.theme.buttonColors
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
    var totalTimeTaken by remember { mutableStateOf(0L) }
    var correctAnswersCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val scoreDao = db.scoreDao()
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var timeTaken = 0L

    if (currentQuestionIndex >= questions.size) {
        return
    }

    val currentQuestion = questions[currentQuestionIndex]
    val imageBitmap = loadImage(context, currentQuestion.image)?.asImageBitmap()




    fun playAudio(context: Context) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()

        try {
            val assetFileDescriptor = context.assets.openFd("beep.mp3")
            mediaPlayer?.apply {
                setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

                setAudioAttributes(audioAttributes)

                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val result = audioManager.requestAudioFocus(
                    AudioManager.OnAudioFocusChangeListener { },
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)

                    setOnPreparedListener {
                        start()
                        Log.d("QuizScreen", "Audio started playing")
                    }

                    setOnCompletionListener {
                        release()
                        mediaPlayer = null
                        Log.d("QuizScreen", "Audio playback completed and MediaPlayer released")
                    }
                    prepareAsync()
                } else {
                    Log.e("QuizScreen", "Failed to get audio focus")
                }
            }
        } catch (e: IOException) {
            Log.e("QuizScreen", "Error playing audio: ${e.message}")
        }
    }





    val randomizedIndex by remember(currentQuestionIndex) {
        mutableStateOf((0 until currentQuestion.options.size).shuffled())
    }

    val correctAnswerIndex = 1

    Column(
        modifier = Modifier.fillMaxSize().padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val correctAnswersImg = "${correctAnswersCount}acerto.png"
        loadImage(context, correctAnswersImg)?.asImageBitmap()?.let {
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

        randomizedIndex.forEachIndexed { index, randomIndex ->
            val option = currentQuestion.options[randomIndex]
            Button(
                onClick = {
                    selectedAnswer = randomIndex
                    showFeedback = true
                    isCorrect = randomIndex == correctAnswerIndex
                    totalTimeTaken += (System.currentTimeMillis() - timeTaken)
                    timeTaken = System.currentTimeMillis()
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

                if (isCorrect) {
                    currentQuestionIndex++
                }

                if (currentQuestionIndex >= questions.size) {
                    if (isCorrect) {
                        score -= (totalTimeTaken / 750).toInt()
                        if (score > 999){
                            score = 999
                        }
                        else if (score < 1){
                            score = 1
                        }
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                scoreDao.insertScore(Score(username = username, points = score))
                            }
                            onFinish(score)
                        }
                    } else {
                        onFinish(-1)
                    }
                } else if (!isCorrect) {
                    onFinish(-1)
                }
            }
        }
        LaunchedEffect(currentQuestionIndex) {
            timeTaken = System.currentTimeMillis()
        }
    }
}

private fun loadImage(context: Context, fileName: String): Bitmap? {
    val assetManager = context.assets
    val inputStream = assetManager.open(fileName)
    return BitmapFactory.decodeStream(inputStream)
}