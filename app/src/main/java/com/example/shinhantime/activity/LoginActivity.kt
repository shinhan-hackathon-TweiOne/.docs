package com.example.shinhantime.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.app.LoginFragment
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

        // 내부에 회원 정보가 있다면 password fragment
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, PasswordFragment())
//            .commit()

        // 없다면 회원 로그인 필요, log in fragment 이 후 password fragment로
        // 만약 회원이 아니라면 sign in fragment

        // 테스트
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()

        // 1초 후에 FingerprintFragment로 전환
        // handler.postDelayed(runnable, 1000)
    }

    private fun showFingerprintFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FingerprintFragment())
            .commit()
    }

    // 인증 성공
    fun onAuthenticated() {
        // 인증 성공 시, 다음 mainactivity 페이지로 이동
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // 지문 인식을 취소하면 비밀번호로만 인증되게 변환
    fun onFingerprintCancelled() {
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment is FingerprintFragment) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PasswordFragment())
                .commit()
        }
    }
}