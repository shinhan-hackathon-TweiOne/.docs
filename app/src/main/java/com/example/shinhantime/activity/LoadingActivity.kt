package com.example.shinhantime.activity

import LoadingSendWitchFragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.shinhantime.R
import com.example.shinhantime.fragment.LoadingBootFragment

class LoadingActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // intent pageName으로 받아온게 sendwitch인기 구분
        val fragmentName = intent.getStringExtra("fragmentName")

        println(fragmentName)
        println(intent.getStringExtra("loadType"))

        // sendwitch 로딩 페이지 요청이라면
        if (fragmentName == "SendWitch")
        {
            // 한가지 인자를 더 받아왔을 것
            val type = intent.getStringExtra("loadType")

            // loadingtype이 send인지 receive인지 fragement로 넘겨줌
            val fragment = LoadingSendWitchFragment().apply {
                arguments = Bundle().apply {
                    putString("loadType", type)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
        // 일단은 sendwitch 기능이 아닌 다른 로딩은 전부 하나로 처리
        else
        {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoadingBootFragment())
                .commit()

            handler.postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // 현재 Activity 종료
            }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}