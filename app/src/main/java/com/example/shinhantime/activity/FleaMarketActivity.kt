package com.example.shinhantime.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shinhantime.R
import com.example.shinhantime.fragment.FindingWithAroundFragment

class FleaMarketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finding)

        findViewById<Button>(R.id.button_home).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Fragment 생성 및 데이터 전달
        val fragment = FindingWithAroundFragment().apply {
            arguments = Bundle().apply {
                putString("pageName", "FleaMarketActivity") // 원하는 데이터를 추가
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        // WindowInsets 처리 ( 필요 없는 경우 삭제 가능 )
        val rootView = findViewById<View>(R.id.fragment_container)
        rootView.setOnApplyWindowInsetsListener { view, insets ->
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = navigationBarHeight)
            insets
        }
    }
}