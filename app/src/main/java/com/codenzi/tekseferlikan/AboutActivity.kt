package com.codenzi.tekseferlikan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val privacypolicyButton: Button = findViewById(R.id.privacypolicyButton)
        val backButton: Button = findViewById(R.id.backButton)

        privacypolicyButton.setOnClickListener {

            val browserIntent = Intent(Intent.ACTION_VIEW, "https://www.codenzi.com/privacy-just-a-moment.html".toUri())
            startActivity(browserIntent)
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}