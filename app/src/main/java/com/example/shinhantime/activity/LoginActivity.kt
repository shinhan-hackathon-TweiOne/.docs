package com.example.shinhantime.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.shinhantime.R
import com.example.shinhantime.fragment.FingerprintFragment
import com.example.shinhantime.fragment.PasswordFragment

class LoginActivity : AppCompatActivity() {

    private val handler = Handler()
    private val runnable = Runnable {
        showFingerprintFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 초기 Fragment로 PasswordFragment를 추가합니다.
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PasswordFragment())
            .commit()

        // 1초 후에 FingerprintFragment로 전환
        // handler.postDelayed(runnable, 1000)
    }

    private fun showFingerprintFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FingerprintFragment())
            .commit()
    }

    fun onFingerprintAuthenticated() {
        // 인증 성공 시, 다음 페이지로 이동
        // 예를 들어, 다음 Activity로 이동할 수 있습니다.
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun onFingerprintCancelled() {
        // 취소 시, PasswordFragment로 돌아가고 FingerprintFragment는 다시 실행되지 않음
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is FingerprintFragment) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PasswordFragment())
                .commit()
        }
    }
}