package com.example.tekseferlikan

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // SharedPreferences için sabitler.
    private val PREFS_NAME = "TekSeferlikAnPrefs"
    private val PREF_KEY_RUN_BEFORE = "runBefore"

    // DEĞİŞİKLİK 1: Müzik çaları buradan kaldırıp aşağıda oluşturacağımız
    // "ortak alana" (companion object) taşıyoruz.

    companion object {
        // YENİ: Müzik çaları tüm uygulama tarafından erişilebilen "ortak" bir alana taşıdık.
        // Bu sayede bir ekrandan diğerine geçince kaybolmayacak.
        private var mediaPlayer: MediaPlayer? = null

        // YENİ: Müziği başlatan ortak bir fonksiyon yazdık.
        // Bu fonksiyon, müziğin zaten çalıp çalmadığını kontrol eder.
        // Çalmıyorsa, yeni bir tane oluşturur ve başlatır.
        fun startPlayer(context: Context) {
            if (mediaPlayer == null) {
                // R.raw.muzik, "res/raw klasöründeki muzik isimli dosyayı bul" demektir.
                mediaPlayer = MediaPlayer.create(context, R.raw.muzik)
                mediaPlayer?.isLooping = true // İsterseniz müziğin tekrar etmesini sağlayabilirsiniz.
                mediaPlayer?.start()
            }
        }

        // YENİ: Müziği tamamen durduran ve kaynakları temizleyen ortak fonksiyon.
        fun stopPlayer() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executeAppLogic()
    }

    // DEĞİŞİKLİK 2: onStop() metodundan müzik durdurma kodunu tamamen kaldırıyoruz.
    // Çünkü artık "Hakkında" sayfasına gidince müziğin durmasını İSTEMİYORUZ.
    override fun onStop() {
        super.onStop()
        // Uygulama kapatıldığında veya arka plana alındığında işareti bırak
        setRunFlag()
        // Buradaki müzik durdurma kodları kaldırıldı.
    }

    // YENİ: onDestroy metodu ekliyoruz.
    // Bu, aktivite tamamen yok edildiğinde (örneğin "Hoşça Kal" butonuna basılınca) çağrılır.
    // Emniyet subabı olarak, eğer müzik hala çalıyorsa burada durdururuz.
    override fun onDestroy() {
        super.onDestroy()
        // "Hoşça Kal" butonuna basıldığında zaten stopPlayer() çağrılıyor,
        // ama kullanıcı uygulamayı başka bir yolla kapatırsa diye burada da kontrol ediyoruz.
        // stopPlayer() // Genellikle "Hoşça kal" butonu yeterli olacaktır.
    }

    private fun isRunBefore(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_KEY_RUN_BEFORE, false)
    }

    private fun setRunFlag() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(PREF_KEY_RUN_BEFORE, true)
            apply()
        }
    }

    private fun executeAppLogic() {
        if (isRunBefore()) {
            Toast.makeText(this, getString(R.string.toast_already_lived), Toast.LENGTH_LONG).show()
            finish()
        } else {
            showFirstTimeUI()
        }
    }

    private fun showFirstTimeUI() {
        setContentView(R.layout.activity_main)

        // DEĞİŞİKLİK 3: Müziği başlatmak için artık ortak fonksiyonumuzu çağırıyoruz.
        startPlayer(this)

        val farewellButton: Button = findViewById(R.id.farewellButton)
        val mainLayout: View = findViewById(R.id.main)
        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val aboutButton: Button = findViewById(R.id.aboutButton)

        var titleClickCount = 0
        var lastClickTime: Long = 0

        titleTextView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > 2000) {
                titleClickCount = 0
            }
            titleClickCount++
            lastClickTime = currentTime
            if (titleClickCount == 3) {
                titleClickCount = 0
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }

        aboutButton.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        // DEĞİŞİKLİK 4: "Hoşça Kal" butonuna basıldığında müziği durdurma komutunu ekliyoruz.
        farewellButton.setOnClickListener {
            // Animasyon başlamadan hemen önce müziği tamamen durdur.
            stopPlayer()

            val fadeOutAnimation =
                AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_out)
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    finish()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            mainLayout.startAnimation(fadeOutAnimation)
        }
    }
}