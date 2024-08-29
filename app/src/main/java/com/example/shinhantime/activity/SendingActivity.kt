package com.example.shinhantime.activity

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

class SendingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sending)  // XML 레이아웃 파일 이름을 실제 이름으로 변경하세요

        val userName = intent.getStringExtra("userName")
        val userImageRes = intent.getIntExtra("userImage", -1)

        val fragment = SendingInputInformationFragment().apply {
            arguments = Bundle().apply {
                putString("userName", userName)
                putInt("userImage", userImageRes)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
