package com.codenzi.tekseferlikan

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.muzik)
        mediaPlayer?.isLooping = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        mediaPlayer?.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }
}