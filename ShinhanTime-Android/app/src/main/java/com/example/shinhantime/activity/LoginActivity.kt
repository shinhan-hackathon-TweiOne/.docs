package com.example.shinhantime.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.app.LoginFragment
import com.example.shinhantime.R
import com.example.shinhantime.fragment.FingerprintFragment
import com.example.shinhantime.fragment.PasswordFragment

class LoginActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val userData = loadUserData(this)
        val userId = userData["userId"] as Int

        if (userId != -1) {
            // 내부에 회원 정보가 있는 경우
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PasswordFragment())
                .commit()
        } else {
            // 내부에 회원 정보가 없는 경우 회원가입 화면으로 전환
            finish()
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserData(context: Context): Map<String, Any?> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getInt("userId", -1) // 기본값으로 -1을 설정
        val accessToken = sharedPreferences.getString("accessToken", null)
        val refreshToken = sharedPreferences.getString("refreshToken", null)

        return mapOf("userId" to userId, "accessToken" to accessToken, "refreshToken" to refreshToken)
    }

    // 인증 성공
    fun onAuthenticated() {
        // 인증 성공 시, 다음 mainactivity 페이지로 이동
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

}
