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
        
        // 메인 기본 => 내 주변 찾기로 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FindingWithAroundFragment())
            .commit()

        // WindowInsets 처리 ( 필요한지 잘 모르겟음, 해골물인가 )
        val rootView = findViewById<View>(R.id.fragment_container)
        rootView.setOnApplyWindowInsetsListener { view, insets ->
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = navigationBarHeight)
            insets
        }
    }

}