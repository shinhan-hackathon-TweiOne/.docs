package com.example.shinhantime.activity

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.shinhantime.Account
import com.example.shinhantime.R
import com.example.shinhantime.RetrofitInstances
import com.example.shinhantime.fragment.MainAccountExistFragment
import androidx.fragment.app.Fragment
import com.example.shinhantime.UserDto
import com.example.shinhantime.fragment.FingerprintFragment
import com.example.shinhantime.fragment.MainAccountNoneFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAccountInfo()

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

    private fun checkAccountInfo() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)

        if (userId == -1) {
            Toast.makeText(this, "유저 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            // 지워라
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainAccountExistFragment())
                .commit()
            return
        }

        RetrofitInstances.userApiService.getUserInfo(userId)
            .enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        val userInfo = response.body()

                        if (userInfo?.mainAccount == null) {
                            // Main Account 정보가 없는 경우
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, MainAccountNoneFragment())
                                .commit()
                        } else {
                            // Main Account 정보가 있는 경우
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, MainAccountExistFragment())
                                .commit()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "유저 정보 조회 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "서버와의 통신 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
