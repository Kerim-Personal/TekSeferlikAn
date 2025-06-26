package com.codenzi.tekseferlikan

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {


    private val prefsName = "TekSeferlikAnPrefs"
    private val prefKeyRunBefore = "runBefore"

    private var scrollAnimator: ObjectAnimator? = null

    private val musicServiceIntent by lazy {
        Intent(this, MusicService::class.java)
    }


    private var isNavigatingInternally = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executeAppLogic()
    }

    override fun onResume() {
        super.onResume()

        isNavigatingInternally = false
    }

    override fun onStop() {
        super.onStop()

        if (!isNavigatingInternally) {
            setRunFlag()
            stopService(musicServiceIntent)
            finish()
        }
    }

    private fun startScrollAnimation(startScrollY: Int) {
        scrollAnimator?.cancel()

        val scrollView: ScrollView = findViewById(R.id.textScrollView)
        val textView = scrollView.getChildAt(0) as? TextView ?: return
        val maxScroll = textView.height - scrollView.height

        if (startScrollY >= maxScroll) return

        val totalDuration = 90000L
        val remainingDistance = maxScroll - startScrollY


        if (maxScroll <= 0) return

        val newDuration = (remainingDistance.toDouble() / maxScroll.toDouble() * totalDuration).toLong()

        scrollAnimator = ObjectAnimator.ofInt(scrollView, "scrollY", startScrollY, maxScroll).apply {
            duration = newDuration
            interpolator = LinearInterpolator()
        }
        scrollAnimator?.start()
    }

    private fun isRunBefore(): Boolean {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return prefs.getBoolean(prefKeyRunBefore, false)
    }

    private fun setRunFlag() {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit {
            putBoolean(prefKeyRunBefore, true)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun showFirstTimeUI() {
        setContentView(R.layout.activity_main)

        startService(musicServiceIntent)

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
                isNavigatingInternally = true
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }
        aboutButton.setOnClickListener {
            isNavigatingInternally = true
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        val scrollView: ScrollView = findViewById(R.id.textScrollView)


        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                scrollView.performClick()
                return super.onSingleTapUp(e)
            }
        })

        scrollView.setOnTouchListener { view, event ->
            gestureDetector.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    scrollAnimator?.cancel()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    startScrollAnimation(scrollView.scrollY)
                }
            }
            view.onTouchEvent(event)
            true
        }

        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                startScrollAnimation(0)
            }
        })

        farewellButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.farewell_dialog_title))
                .setMessage(getString(R.string.farewell_dialog_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.farewell_dialog_positive_button)) { dialog, _ ->
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
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.farewell_dialog_negative_button)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
