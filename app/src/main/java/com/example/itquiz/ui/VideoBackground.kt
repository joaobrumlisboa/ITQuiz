package com.example.itquiz.ui

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoBackground() {
    val context = LocalContext.current

    Box {
        // Use um TextureView para reproduzir o vídeo
        val textureView = remember { TextureView(context) }

        // Configurar o MediaPlayer dentro do DisposableEffect
        DisposableEffect(Unit) {
            val mediaPlayer = MediaPlayer()

            // Definir o listener para o TextureView
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {
                    // Supera a textura quando estiver disponível
                    val mediaSurface = Surface(surface)
                    mediaPlayer.setSurface(mediaSurface)

                    try {
                        // Carregar o vídeo do arquivo
                        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd("background.mp4")
                        mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                        mediaPlayer.isLooping = true

                        // Preparar o MediaPlayer e iniciar a reprodução
                        mediaPlayer.setOnPreparedListener {
                            it.start()
                            Log.d("VideoBackground", "Video started playing.")
                        }
                        mediaPlayer.prepareAsync() // Prepare assíncrono
                    } catch (e: Exception) {
                        Log.e("VideoBackground", "Error loading video: ${e.message}")
                    }
                }

                override fun onSurfaceTextureSizeChanged(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {}

                override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean {
                    // Liberar recursos do MediaPlayer
                    mediaPlayer.release()
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
            }

            // Liberar recursos quando o Composable é removido
            onDispose {
                mediaPlayer.release()
                Log.d("VideoBackground", "MediaPlayer released.")
            }
        }

        // Adicione o TextureView ao layout
        AndroidView(factory = { textureView })
    }
}