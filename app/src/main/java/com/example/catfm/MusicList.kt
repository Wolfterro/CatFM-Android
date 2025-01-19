package com.example.catfm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun MusicList(musicList: List<Music>) {
    var currentPlaying by remember { mutableStateOf<String?>(null) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(musicList) { music ->
            MusicItem(
                music = music,
                isPlaying = currentPlaying == music.fileUrl,
                onPlay = { currentPlaying = music.fileUrl },
                onStop = { currentPlaying = null }
            )
        }
    }
}

