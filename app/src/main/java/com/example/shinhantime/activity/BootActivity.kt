package com.example.shinhantime.activity

import LoadingSendWitchFragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.shinhantime.R
import com.example.shinhantime.fragment.LoadingBootFragment
import com.example.shinhantime.fragment.PasswordFragment
import com.example.shinhantime.fragment.SendingInputInformationFragment


class BootActivity : AppCompatActivity() {
    
    private val DELAY_LOADING = 1000 // milliseconds
    private val DELAY_SIGNIN = 3000 // milliseconds

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)

        // 현재는 2초 뒤에 바로 로딩화면이 나오도록 되어있는데 바로 시작해도 됨
        // 로고, 앱 이름 등을 보여주기 위한 딜레이임
        handler.postDelayed({
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}