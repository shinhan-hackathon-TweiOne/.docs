package com.example.shinhantime.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.app.LoginFragment
import com.example.shinhantime.R
import com.example.shinhantime.fragment.FingerprintFragment
import com.example.shinhantime.fragment.PasswordFragment
import com.example.shinhantime.fragment.SignUpInputInformationFragment

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignUpInputInformationFragment())
            .commit()
    }

}