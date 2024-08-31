package com.example.shinhantime.activity

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shinhantime.R
import com.example.shinhantime.fragment.MainAccountExistFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 지금은 fragment로 mainaccountexistfragment를 추가합니다.
        // 실제로는 앱 데이터에 계정 정보 확인 후 없으면 nonefragment
        // 있으면 existfragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainAccountExistFragment())
            .commit()

        // 하단바 버튼 클릭 리스너 설정
        findViewById<ImageButton>(R.id.button_home).setOnClickListener {
            // 홈 버튼 클릭 시의 동작
            println("IS HOME !!")
        }

        findViewById<ImageButton>(R.id.button_menu).setOnClickListener {
            // 메뉴 버튼 클릭 시의 동작
            println("IS MENU !!")
        }

        findViewById<ImageButton>(R.id.button_mypage).setOnClickListener {
            // 마이페이지 버튼 클릭 시의 동작
            println("IS MYPAGE !!")
        }

        findViewById<ImageButton>(R.id.button_fleamarket).setOnClickListener{
            finish()
            val intent = Intent(this, FleaMarketActivity::class.java)
            startActivity(intent)
        }

        // WindowInsets 처리
        val rootView = findViewById<View>(R.id.fragment_container)
        rootView.setOnApplyWindowInsetsListener { view, insets ->
            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            view.updatePadding(bottom = navigationBarHeight)
            insets
        }
    }
}
