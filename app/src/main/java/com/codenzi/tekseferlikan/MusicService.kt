package com.codenzi.tekseferlikan

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        // Servis oluşturulduğunda MediaPlayer'ı hazırla
        mediaPlayer = MediaPlayer.create(this, R.raw.muzik)
        mediaPlayer?.isLooping = true // Müziğin sürekli çalmasını sağla
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Servis başlatıldığında müziği çal
        mediaPlayer?.start()
        return START_STICKY // Servis sistem tarafından kapatılırsa yeniden başlatmayı dene
    }

    override fun onDestroy() {
        super.onDestroy()
        // Servis durdurulduğunda müziği durdur ve kaynakları serbest bırak
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Bu uygulamada servise bağlanmayacağımız için null dönebiliriz.
        return null
    }
}