package com.example.shinhantime.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shinhantime.R
import com.example.shinhantime.fragment.FindingWithAroundFragment

class FindingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finding)

        // 초기 Fragment로 MainAccountNoneFragment를 추가합니다.
        // 계좌 정보가 이미 등록되어있다면 바로 MainAccountExistFragment로 할당
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, MainAccountNoneFragment())
//            .commit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FindingWithAroundFragment())
            .commit()

        // 1초 후에 FingerprintFragment로 전환
        // handler.postDelayed(runnable, 1000)

        // 하단바 버튼 클릭 리스너 설정

        // WindowInsets 처리
        val rootView = findViewById<View>(R.id.fragment_container)
        rootView.setOnApplyWindowInsetsListener { view, insets ->
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = navigationBarHeight)
            insets
        }
    }

}