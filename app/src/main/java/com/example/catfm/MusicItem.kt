package com.example.catfm

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.material3.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun MusicItem(
    music: Music,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit
) {
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Aumente o tamanho da thumbnail aqui
        AsyncImage(
            model = music.coverUrl,
            contentDescription = null,
            modifier = Modifier
                .size(84.dp), // Tamanho ajustado para 128x128 dp
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = music.name, style = MaterialTheme.typography.titleMedium)
            Text(text = music.artist, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.width(16.dp))

        if (isPlaying) {
            Button(onClick = {
                mediaPlayer?.pause()
                onStop()
            }) {
                Text("Pause")
            }
        } else {
            Button(onClick = {
                scope.launch {
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(music.fileUrl)
                        prepareAsync()
                        setOnPreparedListener { start() }
                    }
                    onPlay()
                }
            }) {
                Text("Play")
            }
        }
    }
}
