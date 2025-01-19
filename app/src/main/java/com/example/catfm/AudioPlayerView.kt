package com.example.catfm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AudioPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = AudioServiceBinder()

    companion object {
        const val CHANNEL_ID = "AudioPlayerChannel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("AUDIO_URL")
        if (url != null) {
            initializeMediaPlayer(url)
        }
        return START_NOT_STICKY
    }

    private fun initializeMediaPlayer(url: String) {
        mediaPlayer?.release() // Libera qualquer recurso anterior
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                it.start()
                showNotification()
            }
            setOnCompletionListener {
                stopSelf() // Encerra o serviço quando o áudio termina
            }
        }
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reproduzindo áudio")
            .setContentText("Seu áudio está sendo reproduzido em segundo plano.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Audio Player",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder = binder

    inner class AudioServiceBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }
}
