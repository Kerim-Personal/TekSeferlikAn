package com.example.tekseferlikan // KENDİ PAKET İSMİNİ KONTROL ET!

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val flagFilename = "tek_seferlik_flag.txt"
    private val permissionRequestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handlePermissionsAndProceed()
    }

    private fun handlePermissionsAndProceed() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionRequestCode)
        } else {
            executeAppLogic()
        }
    }

    private fun executeAppLogic() {
        lifecycleScope.launch(Dispatchers.IO) {
            val flagExists = isFlagFileExists()
            withContext(Dispatchers.Main) {
                if (flagExists) {
                    Toast.makeText(this@MainActivity, getString(R.string.toast_already_lived), Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    showFirstTimeUI()
                }
            }
        }
    }

    private fun showFirstTimeUI() {
        setContentView(R.layout.activity_main)
        val farewellButton: Button = findViewById(R.id.farewellButton)

        farewellButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val (success, errorMessage) = createFlagFile()
                withContext(Dispatchers.Main) {
                    if (success) {
                        Toast.makeText(this@MainActivity, getString(R.string.toast_mark_left), Toast.LENGTH_SHORT).show()
                    } else {
                        val errorText = getString(R.string.toast_error_prefix) + " " + errorMessage
                        Toast.makeText(this@MainActivity, errorText, Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
            }
        }
    }

    private fun isFlagFileExists(): Boolean {
        // Bu fonksiyon doğru ve stabil, dokunmuyoruz.
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(flagFilename)

        contentResolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
            return cursor.count > 0
        }
        return false
    }

    // EN DOĞRU YÖNTEME GERİ DÖNÜYORUZ
    private fun createFlagFile(): Pair<Boolean, String?> {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // build.gradle düzeldiği için burası artık çalışmalı!
            MediaStore.Documents.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val values = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, flagFilename)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Files.FileColumns.IS_PENDING, 1)
            }
        }

        val uri: Uri?
        try {
            uri = contentResolver.insert(collection, values)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, e.localizedMessage ?: getString(R.string.toast_error_generic))
        }

        if (uri == null) {
            return Pair(false, getString(R.string.toast_error_uri_null))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Files.FileColumns.IS_PENDING, 0)
            try {
                contentResolver.update(uri, values, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
                return Pair(false, e.localizedMessage ?: getString(R.string.toast_error_generic))
            }
        }
        return Pair(true, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                executeAppLogic()
            } else {
                Toast.makeText(this, getString(R.string.toast_permission_needed), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}