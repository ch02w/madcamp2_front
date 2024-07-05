package com.example.fridge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // LoginActivity 호출
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
