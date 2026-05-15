package com.kreedaankana.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirect to Login screen immediately
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}