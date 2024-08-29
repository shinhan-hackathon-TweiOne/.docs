package com.example.shinhantime.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.shinhantime.R

class BootActivity : AppCompatActivity() {

    private val DELAY_LOADING = 1000 // milliseconds
    private val DELAY_SIGNIN = 3000 // milliseconds

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)

        handler.postDelayed({
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)
        }, 2000)//DELAY_LOADING)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

class LoadingActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 Activity 종료
        }, 1000)//DELAY_SIGNIN)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}