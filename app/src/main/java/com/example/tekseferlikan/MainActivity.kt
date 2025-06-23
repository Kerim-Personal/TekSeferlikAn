package com.example.tekseferlikan

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val PREFS_NAME = "TekSeferlikAnPrefs"
    private val PREF_KEY_RUN_BEFORE = "runBefore"

    companion object {
        private var mediaPlayer: MediaPlayer? = null
        fun startPlayer(context: Context) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.muzik)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
        }
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

    override fun onStop() {
        super.onStop()
        setRunFlag()
    }

    override fun onDestroy() {
        super.onDestroy()
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
        startPlayer(this)

        val farewellButton: Button = findViewById(R.id.farewellButton)
        val mainLayout: View = findViewById(R.id.main)
        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val aboutButton: Button = findViewById(R.id.aboutButton)

        // Title ve About butonlarının tıklama olayları
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

        // --- EN SAĞLAM ANİMASYON YÖNTEMİ ---
        val scrollView: ScrollView = findViewById(R.id.textScrollView)

        // ScrollView'un layout'u tamamlandığında animasyonu başlat
        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Bu listener'ı tekrar çalışmaması için hemen kaldırıyoruz.
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val textView = scrollView.getChildAt(0) as TextView
                val maxScroll = textView.height - scrollView.height

                // ScrollView'un "scrollY" özelliğini 0'dan maksimum değere kadar anime et
                val animator = ObjectAnimator.ofInt(scrollView, "scrollY", 0, maxScroll).apply {
                    duration = 90000
                    interpolator = LinearInterpolator()
                }
                animator.start()
            }
        })
        // --- ANİMASYON KODU BİTİŞİ ---

        farewellButton.setOnClickListener {
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