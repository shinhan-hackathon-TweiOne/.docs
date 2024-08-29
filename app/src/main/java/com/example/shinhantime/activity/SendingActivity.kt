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
        setContentView(R.layout.activity_sending)

        // intent로 전달받은 유저 이름, 이미지 등 정보
        val userName = intent.getStringExtra("userName")
        val userImageRes = intent.getIntExtra("userImage", -1)

        // 전달받은 정보를 fragment로 다시 전달
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
