package com.example.itquiz.ui

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
        val textureView = remember { TextureView(context) }
        DisposableEffect(Unit) {
            val mediaPlayer = MediaPlayer()
            textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {
                    val mediaSurface = Surface(surface)
                    mediaPlayer.setSurface(mediaSurface)

                    try {
                        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd("background.mp4")
                        mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                        mediaPlayer.isLooping = true
                        mediaPlayer.setOnPreparedListener {
                            it.start()
                            Log.d("VideoBackground", "Video playing.")
                        }
                        mediaPlayer.prepareAsync()
                    } catch (e: Exception) {
                        Log.e("VideoBackground", "Error loading video: ${e.message}")
                    }
                }

                override fun onSurfaceTextureSizeChanged(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {}

                override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean {
                    mediaPlayer.release()
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
            }
            onDispose {
                mediaPlayer.release()
                Log.d("VideoBackground", "MediaPlayer released.")
            }
        }
        AndroidView(factory = { textureView })
    }
}