package com.example.tekseferlikan // KENDİ PAKET İSMİNİ KONTROL ET!

import android.content.Context
import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executeAppLogic()
    }

    override fun onStop() { // Yeni eklenen metod
        super.onStop()
        // Uygulama kapatıldığında veya arka plana alındığında işareti bırak
        setRunFlag()
    }

    /**
     * Uygulamanın daha önce çalışıp çalışmadığını SharedPreferences'tan kontrol eder.
     */
    private fun isRunBefore(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PREF_KEY_RUN_BEFORE, false)
    }

    /**
     * Uygulamanın çalıştığına dair işareti SharedPreferences'e kaydeder.
     */
    private fun setRunFlag() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(PREF_KEY_RUN_BEFORE, true)
            apply() // Kaydet
        }
    }

    /**
     * Ana uygulama mantığı.
     */
    private fun executeAppLogic() {
        // Uygulama daha önce çalıştıysa...
        if (isRunBefore()) {
            Toast.makeText(this, getString(R.string.toast_already_lived), Toast.LENGTH_LONG).show()
            finish() // Aktiviteyi hemen kapat.
        } else {
            // İlk defa çalışıyorsa arayüzü göster.
            showFirstTimeUI()
        }
    }

    /**
     * İlk açılış arayüzünü ve gizli "Hakkında" sayfası tetikleyicisini ayarlar.
     */
    private fun showFirstTimeUI() {
        setContentView(R.layout.activity_main)
        val farewellButton: Button = findViewById(R.id.farewellButton)
        val mainLayout: View = findViewById(R.id.main)
        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val aboutButton: Button = findViewById(R.id.aboutButton) // Hakkında butonu referansı

        // Gizli aktivite için tıklama sayacı
        var titleClickCount = 0
        var lastClickTime: Long = 0

        titleTextView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            // Tıklamalar arası 2 saniyeden fazla ise sayacı sıfırla
            if (currentTime - lastClickTime > 2000) {
                titleClickCount = 0
            }

            titleClickCount++
            lastClickTime = currentTime

            // 7 kez tıklandıysa Hakkında sayfasını aç
            if (titleClickCount == 3) {
                titleClickCount = 0 // Sayacı tekrar sıfırla
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }

        // Hakkında butonu için tıklama dinleyicisi
        aboutButton.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        farewellButton.setOnClickListener {
            // 1. Çalıştığına dair işareti bırak.
            // Bu çağrı artık onStop() içinde de yapılacağı için buradaki çağrı teknik olarak zorunlu değil ama bırakılabilir.
            // setRunFlag()

            // 2. Animasyonu başlat.
            val fadeOutAnimation =
                AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_out)
            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    // 3. Animasyon bitince aktiviteyi kapat.
                    finish()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            mainLayout.startAnimation(fadeOutAnimation)
        }
    }
}