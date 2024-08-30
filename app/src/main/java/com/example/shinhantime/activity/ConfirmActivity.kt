package com.example.shinhantime.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.shinhantime.R
import com.example.shinhantime.fragment.PasswordFragment
import com.example.shinhantime.fragment.SendingInputInformationFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConfirmActivity : AppCompatActivity() {

    // 페이지가 이미 전환되었는지 확인하는 플래그
    private var isNavigated = false

    // 코루틴의 작업을 관리하는 Job 객체
    private var navigationJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        // intent로 전달받은 돌아갈 페이지 이름, 프래그먼트 이름, 타입
        val pageName = intent.getStringExtra("pageName")
        val fragmentName = intent.getStringExtra("fragmentName")
        val loadType = intent.getStringExtra("loadType")

        val textMessage = findViewById<TextView>(R.id.text_message)

        if (pageName != null) {
            setNextPage(pageName, fragmentName ?: "", loadType ?: "")
        }
        else if (pageName == "MainActivity") {
            textMessage.text = "입금을 완료했어요."
        }
        else if (pageName == "FleaMarketActivity") {
            textMessage.text = "입금을 확인했어요."
        }
    }

    private fun setNextPage(name: String, fragment: String, type: String) {
        // 클래스명을 문자열로 받아 Class로 변환
        val className = Class.forName("com.example.shinhantime.activity.$name")

        // 3초 뒤 페이지로 돌아가기
        navigationJob = CoroutineScope(Dispatchers.Main).launch {
            delay(3000L)
            if (!isNavigated) { // 페이지가 아직 전환되지 않은 경우만 실행
                navigateToPage(className, fragment, type)
            }
        }

        val confirmButton = findViewById<Button>(R.id.button_confirm)
        // 확인 버튼 클릭시 페이지로 돌아가기
        confirmButton.setOnClickListener {
            if (!isNavigated) { // 중복 실행 방지
                navigateToPage(className, fragment, type)
                navigationJob?.cancel() // 코루틴 취소
            }
        }
    }

    private fun navigateToPage(className: Class<*>, fragment: String, type: String) {
        if (isNavigated) return // 이미 전환된 경우 실행하지 않음
        isNavigated = true // 페이지가 전환되었음을 기록
        finish()
        val intent = Intent(this@ConfirmActivity, className).apply {
            putExtra("fragmentName", fragment)
            putExtra("loadType", type)
        }
        startActivity(intent)
    }
}
