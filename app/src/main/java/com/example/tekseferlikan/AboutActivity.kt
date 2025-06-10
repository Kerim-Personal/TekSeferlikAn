package com.example.tekseferlikan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val donateButton: Button = findViewById(R.id.donateButton)
        val backButton: Button = findViewById(R.id.backButton)

        donateButton.setOnClickListener {
            // Buraya kendi bağış linkinizi ekleyebilirsiniz.
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sponsors/Codenzi"))
            startActivity(browserIntent)
        }

        backButton.setOnClickListener {
            finish() // Bu aktiviteyi kapatıp bir öncekine döner.
        }
    }
}