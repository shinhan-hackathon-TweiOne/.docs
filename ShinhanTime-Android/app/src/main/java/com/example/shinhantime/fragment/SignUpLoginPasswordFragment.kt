package com.example.shinhantime.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shinhantime.R
import com.example.shinhantime.activity.MainActivity

class SignUpLoginPasswordFragment : Fragment() {

    private lateinit var char1: TextView
    private lateinit var char2: TextView
    private lateinit var char3: TextView
    private lateinit var char4: TextView
    private lateinit var char5: TextView
    private lateinit var char6: TextView
    private lateinit var errorText: TextView

    private var firstPassword: StringBuilder = StringBuilder()
    private var secondPassword: StringBuilder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        char1 = view.findViewById(R.id.char_1)
        char2 = view.findViewById(R.id.char_2)
        char3 = view.findViewById(R.id.char_3)
        char4 = view.findViewById(R.id.char_4)
        char5 = view.findViewById(R.id.char_5)
        char6 = view.findViewById(R.id.char_6)
        errorText = view.findViewById(R.id.text_error)

        val keypad = view.findViewById<View>(R.id.blue_keypad)

        keypad.findViewById<View>(R.id.button_1).setOnClickListener { onKeyPressed('1') }
        keypad.findViewById<View>(R.id.button_2).setOnClickListener { onKeyPressed('2') }
        keypad.findViewById<View>(R.id.button_3).setOnClickListener { onKeyPressed('3') }
        keypad.findViewById<View>(R.id.button_4).setOnClickListener { onKeyPressed('4') }
        keypad.findViewById<View>(R.id.button_5).setOnClickListener { onKeyPressed('5') }
        keypad.findViewById<View>(R.id.button_6).setOnClickListener { onKeyPressed('6') }
        keypad.findViewById<View>(R.id.button_7).setOnClickListener { onKeyPressed('7') }
        keypad.findViewById<View>(R.id.button_8).setOnClickListener { onKeyPressed('8') }
        keypad.findViewById<View>(R.id.button_9).setOnClickListener { onKeyPressed('9') }
        keypad.findViewById<View>(R.id.button_0).setOnClickListener { onKeyPressed('0') }
        keypad.findViewById<View>(R.id.button_back).setOnClickListener { onBackspacePressed() }
    }

    private fun onKeyPressed(char: Char) {
        val currentPassword = if (secondPassword == null) firstPassword else secondPassword!!

        if (currentPassword.length < 6) {
            currentPassword.append(char)
            updatePasswordUI(currentPassword)
        }

        if (currentPassword.length == 6) {
            if (secondPassword == null) {
                // 첫 번째 비밀번호 입력이 완료되면 두 번째 입력을 위해 초기화
                secondPassword = StringBuilder()
                resetPasswordUI()
            } else {
                // 두 번째 비밀번호 입력이 완료되면 두 비밀번호를 비교
                if (firstPassword.toString() == secondPassword.toString()) {
                    // 비밀번호가 일치하면 성공 처리 (예: 비밀번호 등록 로직 호출)
                    savePasswordToSharedPreferences(firstPassword.toString())
                    Toast.makeText(requireContext(), "비밀번호가 등록되었습니다.", Toast.LENGTH_SHORT).show()

                    // 메인 액티비티로 이동
                    activity?.finish()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // 비밀번호가 일치하지 않으면 에러 메시지를 표시하고 재입력 요구
                    errorText.visibility = View.VISIBLE
                    firstPassword.clear()
                    secondPassword = null
                    resetPasswordUI()
                }
            }
        }
    }

    private fun onBackspacePressed() {
        val currentPassword = if (secondPassword == null) firstPassword else secondPassword!!

        if (currentPassword.isNotEmpty()) {
            currentPassword.deleteCharAt(currentPassword.length - 1)
            updatePasswordUI(currentPassword)
        }
    }

    private fun updatePasswordUI(password: StringBuilder) {
        val chars = listOf(char1, char2, char3, char4, char5, char6)
        for (i in chars.indices) {
            chars[i].text = if (i < password.length) "*" else "_"
        }
    }

    private fun resetPasswordUI() {
        updatePasswordUI(StringBuilder())
    }

    private fun savePasswordToSharedPreferences(password: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userPassword", password)
        editor.apply()
    }
}
