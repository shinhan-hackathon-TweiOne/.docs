package com.example.shinhantime.activity

import LoadingSendWitchFragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.shinhantime.R
import com.example.shinhantime.fragment.LoadingBootFragment
import com.example.shinhantime.fragment.PasswordFragment
import com.example.shinhantime.fragment.SendingInputInformationFragment


class BootActivity : AppCompatActivity() {

    private val DELAY_LOADING = 1000 // milliseconds
    private val DELAY_SIGNIN = 3000 // milliseconds

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)

        handler.postDelayed({
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)
        }, 2000)//DELAY_LOADING)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

class LoadingActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val fragmentName = intent.getStringExtra("pageName")

        if (fragmentName == "SendWitch")
        {
            val type = intent.getStringExtra("loadType")

            val fragment = LoadingSendWitchFragment().apply {
                arguments = Bundle().apply {
                    putString("loadType", type)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
        else
        {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoadingBootFragment())
                .commit()

            handler.postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // 현재 Activity 종료
            }, 1000)//DELAY_SIGNIN)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}