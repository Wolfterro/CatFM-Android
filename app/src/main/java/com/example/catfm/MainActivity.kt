package com.example.catfm

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.catfm.ui.theme.CatFMTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Music(
    val identifier: String,
    val name: String,
    val album: String,
    val artist: String,
    val year: Int,
    val genres: List<String>,
    val coverUrl: String,
    val fileUrl: String,
    val durationInSeconds: Int
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CatFMTheme {
                var musicList by remember { mutableStateOf<List<Music>>(emptyList()) }
                val scope = rememberCoroutineScope()

                // Faz a requisição para o endpoint
                LaunchedEffect(Unit) {
                    scope.launch {
                        val result = fetchMusicList()
                        if (result != null) {
                            musicList = result
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        if (musicList.isEmpty()) {
                            Text(
                                text = "Carregando músicas...",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            MusicList(musicList = musicList)
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchMusicList(): List<Music>? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://5093-2804-14d-5c95-567f-e252-cd07-55d-d900.ngrok-free.app/streaming/?page_size=1000") // Substitua pela URL correta
                val connection = url.openConnection() as HttpURLConnection

                // Adiciona o cabeçalho de autorização
                connection.setRequestProperty("Authorization", "Token b6ac3b0c526cdb7a29ea5f57043f3f78435ec999")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonResponse = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(jsonResponse)
                    val results = jsonObject.getJSONArray("results")

                    List(results.length()) { index ->
                        val item = results.getJSONObject(index)
                        Music(
                            identifier = item.getString("identifier"),
                            name = item.getString("name"),
                            album = item.getString("album"),
                            artist = item.getString("artist"),
                            year = item.getInt("year"),
                            genres = item.getJSONArray("genres").let { genresArray ->
                                List(genresArray.length()) { i -> genresArray.getString(i) }
                            },
                            coverUrl = item.getString("cover_url"),
                            fileUrl = "https://5093-2804-14d-5c95-567f-e252-cd07-55d-d900.ngrok-free.app${item.getString("file_url")}",
                            durationInSeconds = item.getInt("duration_in_seconds")
                        )
                    }
                } else {
                    println("Erro na requisição: ${connection.responseCode}")
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
