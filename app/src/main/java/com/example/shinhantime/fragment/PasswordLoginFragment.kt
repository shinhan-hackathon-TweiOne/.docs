package com.example.shinhantime.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import com.example.shinhantime.activity.LoginActivity

class PasswordFragment : Fragment() {

    private lateinit var charViews: List<TextView>
    private val password = StringBuilder()
    private val maxPasswordLength = 6

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 각 자리수 text 초기화
        charViews = listOf(
            view.findViewById(R.id.char_1),
            view.findViewById(R.id.char_2),
            view.findViewById(R.id.char_3),
            view.findViewById(R.id.char_4),
            view.findViewById(R.id.char_5),
            view.findViewById(R.id.char_6)
        )

        // 숫자 버튼 클릭 리스너 설정
        view.findViewById<Button>(R.id.button_1).setOnClickListener { appendDigit("1") }
        view.findViewById<Button>(R.id.button_2).setOnClickListener { appendDigit("2") }
        view.findViewById<Button>(R.id.button_3).setOnClickListener { appendDigit("3") }
        view.findViewById<Button>(R.id.button_4).setOnClickListener { appendDigit("4") }
        view.findViewById<Button>(R.id.button_5).setOnClickListener { appendDigit("5") }
        view.findViewById<Button>(R.id.button_6).setOnClickListener { appendDigit("6") }
        view.findViewById<Button>(R.id.button_7).setOnClickListener { appendDigit("7") }
        view.findViewById<Button>(R.id.button_8).setOnClickListener { appendDigit("8") }
        view.findViewById<Button>(R.id.button_9).setOnClickListener { appendDigit("9") }
        view.findViewById<Button>(R.id.button_0).setOnClickListener { appendDigit("0") }
        view.findViewById<Button>(R.id.button_back).setOnClickListener { clearLastPassword() }
    }

    // 뒤로 숫자 추가
    private fun appendDigit(digit: String) {
        if (password.length < maxPasswordLength) {
            password.append(digit)
            updatePasswordDisplay()
        }
        if (password.length == maxPasswordLength) {
            validatePassword()
        }
    }

    // 맨 뒤 숫자 하나 제거
    private fun clearLastPassword() {
        if (password.isNotEmpty()) {
            password.deleteCharAt(password.length - 1)
            updatePasswordDisplay()
        }
    }

    // 화면에 text 업데이트 * 또는 _
    private fun updatePasswordDisplay() {
        for (i in 0 until maxPasswordLength) {
            charViews[i].text = if (i < password.length) "*" else "_"
        }
    }

    // 비밀번호 검증
    private fun validatePassword() {
        // 비밀번호 검증 로직으로 내부 or DB에서 값 받아와서 비교필요
        if (password.toString() == "123456") {
            (activity as? LoginActivity)?.onAuthenticated()
        } else {
            (activity as? LoginActivity)?.onFingerprintCancelled()
        }
    }
}








